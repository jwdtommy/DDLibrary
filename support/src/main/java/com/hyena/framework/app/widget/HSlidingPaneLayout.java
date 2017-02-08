/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hyena.framework.app.widget;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.AccessibilityDelegateCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityEvent;

/**
 * SlidingPaneLayout provides a horizontal, multi-pane layout for use at the top
 * level of a UI. A left (or first) pane is treated as a content list or
 * browser, subordinate to a primary detail view for displaying content.
 *
 * <p>
 * Child views may overlap if their combined width exceeds the available width
 * in the SlidingPaneLayout. When this occurs the user may slide the topmost
 * view out of the way by dragging it, or by navigating in the direction of the
 * overlapped view using a keyboard. If the content of the dragged child view is
 * itself horizontally scrollable, the user may grab it by the very edge.
 * </p>
 *
 * <p>
 * Thanks to this sliding behavior, SlidingPaneLayout may be suitable for
 * creating layouts that can smoothly adapt across many different screen sizes,
 * expanding out fully on larger screens and collapsing on smaller screens.
 * </p>
 *
 * <p>
 * SlidingPaneLayout is distinct from a navigation drawer as described in the
 * design guide and should not be used in the same scenarios. SlidingPaneLayout
 * should be thought of only as a way to allow a two-pane layout normally used
 * on larger screens to adapt to smaller screens in a natural way. The
 * interaction patterns expressed by SlidingPaneLayout imply a physicality and
 * direct information hierarchy between panes that does not necessarily exist in
 * a scenario where a navigation drawer should be used instead.
 * </p>
 *
 * <p>
 * Appropriate uses of SlidingPaneLayout include pairings of panes such as a
 * contact list and subordinate interactions with those contacts, or an email
 * thread list with the content pane displaying the contents of the selected
 * thread. Inappropriate uses of SlidingPaneLayout include switching between
 * disparate functions of your app, such as jumping from a social stream view to
 * a view of your personal profile - cases such as this should use the
 * navigation drawer pattern instead. ({@link DrawerLayout DrawerLayout}
 * implements this pattern.)
 * </p>
 *
 * <p>
 * Like {@link android.widget.LinearLayout LinearLayout}, SlidingPaneLayout
 * supports the use of the layout parameter <code>layout_weight</code> on child
 * views to determine how to divide leftover space after measurement is
 * complete. It is only relevant for width. When views do not overlap weight
 * behaves as it does in a LinearLayout.
 * </p>
 *
 * <p>
 * When views do overlap, weight on a slideable pane indicates that the pane
 * should be sized to fill all available space in the closed state. Weight on a
 * pane that becomes covered indicates that the pane should be sized to fill all
 * available space except a small minimum strip that the user may use to grab
 * the slideable view and pull it back over into a closed state.
 * </p>
 */
@SuppressLint("ClickableViewAccessibility")
public class HSlidingPaneLayout extends ViewGroup
{
	private static final String TAG = "SlidingPaneLayout";

	/**
	 * If no fade color is given by default it will fade to 80% gray.
	 */
	private static final int DEFAULT_FADE_COLOR = 0xcccccccc;

	/**
	 * The fade color used for the sliding panel. 0 = no fading.
	 */
	private int mSliderFadeColor = HSlidingPaneLayout.DEFAULT_FADE_COLOR;

	/**
	 * Minimum velocity that will be detected as a fling
	 */
	private static final int MIN_FLING_VELOCITY = 680; // dips per second

	/**
	 * The fade color used for the panel covered by the slider. 0 = no fading.
	 */
	private int mCoveredFadeColor;

	/**
	 * Drawable used to draw the shadow between panes.
	 */
	private Drawable mShadowDrawable;

	/**
	 * The size of the overhang in pixels. This is the minimum section of the
	 * sliding panel that will be visible in the open state to allow for a
	 * closing drag.
	 */
	private final int mOverhangSize;

	/**
	 * True if a panel can slide with the current measurements
	 */
	private boolean mCanSlide;

	/**
	 * The child view that can slide, if any.
	 */
	private View mSlideableView;

	/**
	 * How far the panel is offset from its closed position. range [0, 1] where
	 * 0 = closed, 1 = open.
	 */
	private float mSlideOffset;

	/**
	 * How far the non-sliding panel is parallaxed from its usual position when
	 * open. range [0, 1]
	 */
	private float mParallaxOffset;

	/**
	 * How far in pixels the slideable panel may move.
	 */
	private int mSlideRange;

	/**
	 * A panel view is locked into internal scrolling or another condition that
	 * is preventing a drag.
	 */
	private boolean mIsUnableToDrag;

	/**
	 * Distance in pixels to parallax the fixed pane by when fully closed
	 */
	private int mParallaxBy;

	private float mInitialMotionX;
	private float mInitialMotionY;

	private PanelSlideListener mPanelSlideListener;

	private final ViewDragHelper mDragHelper;

	/**
	 * Stores whether or not the pane was open the last time it was slideable.
	 * If open/close operations are invoked this state is modified. Used by
	 * instance state save/restore.
	 */
	private boolean mPreservedOpenState;
	private boolean mFirstLayout = true;

	private final Rect mTmpRect = new Rect();

	private final ArrayList<DisableLayerRunnable> mPostedRunnables =
			new ArrayList<DisableLayerRunnable>();

	static final SlidingPanelLayoutImpl IMPL;

	static
	{
		final int deviceVersion = Build.VERSION.SDK_INT;
		if (deviceVersion >= 17)
		{
			IMPL = new SlidingPanelLayoutImplJBMR1();
		}
		else if (deviceVersion >= 16)
		{
			IMPL = new SlidingPanelLayoutImplJB();
		}
		else
		{
			IMPL = new SlidingPanelLayoutImplBase();
		}
	}

	/**
	 * Listener for monitoring events about sliding panes.
	 */
	public interface PanelSlideListener
	{
		/**
		 * Called when a sliding pane's position changes.
		 *
		 * @param panel
		 *            The child view that was moved
		 * @param slideOffset
		 *            The new offset of this sliding pane within its range, from
		 *            0-1
		 */
		public void onPanelSlide(View panel, float slideOffset);

		/**
		 * Called when a sliding pane becomes slid completely open. The pane may
		 * or may not be interactive at this point depending on how much of the
		 * pane is visible.
		 *
		 * @param panel
		 *            The child view that was slid to an open position,
		 *            revealing other panes
		 */
		public void onPanelOpened(View panel);

		/**
		 * Called when a sliding pane becomes slid completely closed. The pane
		 * is now guaranteed to be interactive. It may now obscure other views
		 * in the layout.
		 *
		 * @param panel
		 *            The child view that was slid to a closed position
		 */
		public void onPanelClosed(View panel);
	}

	/**
	 * No-op stubs for {@link PanelSlideListener}. If you only want to implement
	 * a subset of the listener methods you can extend this instead of implement
	 * the full interface.
	 */
	public static class SimplePanelSlideListener implements PanelSlideListener
	{
		@Override
		public void onPanelSlide(final View panel, final float slideOffset)
		{
		}

		@Override
		public void onPanelOpened(final View panel)
		{
		}

		@Override
		public void onPanelClosed(final View panel)
		{
		}
	}

	public HSlidingPaneLayout(final Context context)
	{
		this(context, null);
	}

	public HSlidingPaneLayout(final Context context, final AttributeSet attrs)
	{
		this(context, attrs, 0);
	}

	public HSlidingPaneLayout(final Context context, final AttributeSet attrs, final int defStyle)
	{
		super(context, attrs, defStyle);

		final float density = context.getResources().getDisplayMetrics().density;
		this.mOverhangSize = -32;

		this.setWillNotDraw(false);

		ViewCompat.setAccessibilityDelegate(this, new AccessibilityDelegate());
		ViewCompat.setImportantForAccessibility(this, ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_YES);

		this.mDragHelper = ViewDragHelper.create(this, 0.5f, new DragHelperCallback());
		this.mDragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_LEFT);
		this.mDragHelper.setMinVelocity(HSlidingPaneLayout.MIN_FLING_VELOCITY * density);
	}

	/**
	 * Set a distance to parallax the lower pane by when the upper pane is in
	 * its fully closed state. The lower pane will scroll between this position
	 * and its fully open state.
	 *
	 * @param parallaxBy
	 *            Distance to parallax by in pixels
	 */
	public void setParallaxDistance(final int parallaxBy)
	{
		this.mParallaxBy = parallaxBy;
		this.requestLayout();
	}

	/**
	 * @return The distance the lower pane will parallax by when the upper pane
	 *         is fully closed.
	 *
	 * @see #setParallaxDistance(int)
	 */
	public int getParallaxDistance()
	{
		return this.mParallaxBy;
	}

	/**
	 * Set the color used to fade the sliding pane out when it is slid most of
	 * the way offscreen.
	 *
	 * @param color
	 *            An ARGB-packed color value
	 */
	public void setSliderFadeColor(final int color)
	{
		this.mSliderFadeColor = color;
	}

	/**
	 * @return The ARGB-packed color value used to fade the sliding pane
	 */
	public int getSliderFadeColor()
	{
		return this.mSliderFadeColor;
	}

	/**
	 * Set the color used to fade the pane covered by the sliding pane out when
	 * the pane will become fully covered in the closed state.
	 *
	 * @param color
	 *            An ARGB-packed color value
	 */
	public void setCoveredFadeColor(final int color)
	{
		this.mCoveredFadeColor = color;
	}

	/**
	 * @return The ARGB-packed color value used to fade the fixed pane
	 */
	public int getCoveredFadeColor()
	{
		return this.mCoveredFadeColor;
	}

	public void setPanelSlideListener(final PanelSlideListener listener)
	{
		this.mPanelSlideListener = listener;
	}

	void dispatchOnPanelSlide(final View panel)
	{
		if (this.mPanelSlideListener != null)
		{
			this.mPanelSlideListener.onPanelSlide(panel, this.mSlideOffset);
		}
	}

	void dispatchOnPanelOpened(final View panel)
	{
		if (this.mPanelSlideListener != null)
		{
			this.mPanelSlideListener.onPanelOpened(panel);
		}
		this.sendAccessibilityEvent(AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED);
	}

	void dispatchOnPanelClosed(final View panel)
	{
		if (this.mPanelSlideListener != null)
		{
			this.mPanelSlideListener.onPanelClosed(panel);
		}
		this.sendAccessibilityEvent(AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED);
	}

	void updateObscuredViewsVisibility(final View panel)
	{
		final int leftBound = this.getPaddingLeft();
		final int rightBound = this.getWidth() - this.getPaddingRight();
		final int topBound = this.getPaddingTop();
		final int bottomBound = this.getHeight() - this.getPaddingBottom();
		final int left;
		final int right;
		final int top;
		final int bottom;
		if ((panel != null) && HSlidingPaneLayout.viewIsOpaque(panel))
		{
			left = panel.getLeft();
			right = panel.getRight();
			top = panel.getTop();
			bottom = panel.getBottom();
		}
		else
		{
			left = right = top = bottom = 0;
		}

		for (int i = 0, childCount = this.getChildCount(); i < childCount; i++)
		{
			final View child = this.getChildAt(i);

			if (child == panel)
			{
				// There are still more children above the panel but they won't
				// be affected.
				break;
			}

			final int clampedChildLeft = Math.max(leftBound, child.getLeft());
			final int clampedChildTop = Math.max(topBound, child.getTop());
			final int clampedChildRight = Math.min(rightBound, child.getRight());
			final int clampedChildBottom = Math.min(bottomBound, child.getBottom());
			final int vis;
			if ((clampedChildLeft >= left) && (clampedChildTop >= top) &&
					(clampedChildRight <= right) && (clampedChildBottom <= bottom))
			{
				vis = View.INVISIBLE;
			}
			else
			{
				vis = View.VISIBLE;
			}
			child.setVisibility(vis);
		}
	}

	void setAllChildrenVisible()
	{
		for (int i = 0, childCount = this.getChildCount(); i < childCount; i++)
		{
			final View child = this.getChildAt(i);
			if (child.getVisibility() == View.INVISIBLE)
			{
				child.setVisibility(View.VISIBLE);
			}
		}
	}

	private static boolean viewIsOpaque(final View v)
	{
		//if (ViewCompat.isOpaque(v))
		//{
		//	return true;
		//}

		// View#isOpaque didn't take all valid opaque scrollbar modes into
		// account
		// before API 18 (JB-MR2). On newer devices rely solely on isOpaque
		// above and return false
		// here. On older devices, check the view's background drawable directly
		// as a fallback.
		if (Build.VERSION.SDK_INT >= 18)
		{
			return false;
		}

		final Drawable bg = v.getBackground();
		if (bg != null)
		{
			return bg.getOpacity() == PixelFormat.OPAQUE;
		}
		return false;
	}

	@Override
	protected void onAttachedToWindow()
	{
		super.onAttachedToWindow();
		this.mFirstLayout = true;
	}

	@Override
	protected void onDetachedFromWindow()
	{
		super.onDetachedFromWindow();
		this.mFirstLayout = true;

		for (int i = 0, count = this.mPostedRunnables.size(); i < count; i++)
		{
			final DisableLayerRunnable dlr = this.mPostedRunnables.get(i);
			dlr.run();
		}
		this.mPostedRunnables.clear();
	}

	@Override
	protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec)
	{
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);

		if (widthMode != MeasureSpec.EXACTLY)
		{
			if (this.isInEditMode())
			{
				// Don't crash the layout editor. Consume all of the space if
				// specified
				// or pick a magic number from thin air otherwise.
				// TODO Better communication with tools of this bogus state.
				// It will crash on a real device.
				if (widthMode == MeasureSpec.AT_MOST)
				{
					widthMode = MeasureSpec.EXACTLY;
				}
				else if (widthMode == MeasureSpec.UNSPECIFIED)
				{
					widthMode = MeasureSpec.EXACTLY;
					widthSize = 300;
				}
			}
			else
			{
				throw new IllegalStateException("Width must have an exact value or MATCH_PARENT");
			}
		}
		else if (heightMode == MeasureSpec.UNSPECIFIED)
		{
			if (this.isInEditMode())
			{
				// Don't crash the layout editor. Pick a magic number from thin
				// air instead.
				// TODO Better communication with tools of this bogus state.
				// It will crash on a real device.
				if (heightMode == MeasureSpec.UNSPECIFIED)
				{
					heightMode = MeasureSpec.AT_MOST;
					heightSize = 300;
				}
			}
			else
			{
				throw new IllegalStateException("Height must not be UNSPECIFIED");
			}
		}

		int layoutHeight = 0;
		int maxLayoutHeight = -1;
		switch (heightMode)
		{
			case MeasureSpec.EXACTLY:
				layoutHeight = maxLayoutHeight = heightSize - this.getPaddingTop() - this.getPaddingBottom();
				break;
			case MeasureSpec.AT_MOST:
				maxLayoutHeight = heightSize - this.getPaddingTop() - this.getPaddingBottom();
				break;
		}

		float weightSum = 0;
		boolean canSlide = false;
		int widthRemaining = widthSize - this.getPaddingLeft() - this.getPaddingRight();
		final int childCount = this.getChildCount();

		if (childCount > 2)
		{
			Log.e(HSlidingPaneLayout.TAG, "onMeasure: More than two child views are not supported.");
		}

		// We'll find the current one below.
		this.mSlideableView = null;

		// First pass. Measure based on child LayoutParams width/height.
		// Weight will incur a second pass.
		for (int i = 0; i < childCount; i++)
		{
			final View child = this.getChildAt(i);
			final LayoutParams lp = (LayoutParams) child.getLayoutParams();

			if (child.getVisibility() == View.GONE)
			{
				lp.dimWhenOffset = false;
				continue;
			}

			if (lp.weight > 0)
			{
				weightSum += lp.weight;

				// If we have no width, weight is the only contributor to the
				// final size.
				// Measure this view on the weight pass only.
				if (lp.width == 0)
				{
					continue;
				}
			}

			int childWidthSpec;
			final int horizontalMargin = lp.leftMargin + lp.rightMargin;
			if (lp.width == android.view.ViewGroup.LayoutParams.WRAP_CONTENT)
			{
				childWidthSpec = MeasureSpec.makeMeasureSpec(widthSize - horizontalMargin,
						MeasureSpec.AT_MOST);
			}
			else if (lp.width == android.view.ViewGroup.LayoutParams.MATCH_PARENT)
			{
				childWidthSpec = MeasureSpec.makeMeasureSpec(widthSize - horizontalMargin,
						MeasureSpec.EXACTLY);
			}
			else
			{
				childWidthSpec = MeasureSpec.makeMeasureSpec(lp.width, MeasureSpec.EXACTLY);
			}

			int childHeightSpec;
			if (lp.height == android.view.ViewGroup.LayoutParams.WRAP_CONTENT)
			{
				childHeightSpec = MeasureSpec.makeMeasureSpec(maxLayoutHeight, MeasureSpec.AT_MOST);
			}
			else if (lp.height == android.view.ViewGroup.LayoutParams.MATCH_PARENT)
			{
				childHeightSpec = MeasureSpec.makeMeasureSpec(maxLayoutHeight, MeasureSpec.EXACTLY);
			}
			else
			{
				childHeightSpec = MeasureSpec.makeMeasureSpec(lp.height, MeasureSpec.EXACTLY);
			}

			child.measure(childWidthSpec, childHeightSpec);
			final int childWidth = child.getMeasuredWidth();
			final int childHeight = child.getMeasuredHeight();

			if ((heightMode == MeasureSpec.AT_MOST) && (childHeight > layoutHeight))
			{
				layoutHeight = Math.min(childHeight, maxLayoutHeight);
			}

			widthRemaining -= childWidth;
			canSlide |= lp.slideable = widthRemaining < 0;
			if (lp.slideable)
			{
				this.mSlideableView = child;
			}
		}
		if(!isCanSlider)
			canSlide = false;

		// Resolve weight and make sure non-sliding panels are smaller than the
		// full screen.
		if (canSlide || (weightSum > 0))
		{
			final int fixedPanelWidthLimit = widthSize - this.mOverhangSize;

			for (int i = 0; i < childCount; i++)
			{
				final View child = this.getChildAt(i);

				if (child.getVisibility() == View.GONE)
				{
					continue;
				}

				final LayoutParams lp = (LayoutParams) child.getLayoutParams();

				if (child.getVisibility() == View.GONE)
				{
					continue;
				}

				final boolean skippedFirstPass = (lp.width == 0) && (lp.weight > 0);
				final int measuredWidth = skippedFirstPass ? 0 : child.getMeasuredWidth();
				if (canSlide && (child != this.mSlideableView))
				{
					if ((lp.width < 0) && ((measuredWidth > fixedPanelWidthLimit) || (lp.weight > 0)))
					{
						// Fixed panels in a sliding configuration should
						// be clamped to the fixed panel limit.
						final int childHeightSpec;
						if (skippedFirstPass)
						{
							// Do initial height measurement if we skipped
							// measuring this view
							// the first time around.
							if (lp.height == android.view.ViewGroup.LayoutParams.WRAP_CONTENT)
							{
								childHeightSpec = MeasureSpec.makeMeasureSpec(maxLayoutHeight,
										MeasureSpec.AT_MOST);
							}
							else if (lp.height == android.view.ViewGroup.LayoutParams.MATCH_PARENT)
							{
								childHeightSpec = MeasureSpec.makeMeasureSpec(maxLayoutHeight,
										MeasureSpec.EXACTLY);
							}
							else
							{
								childHeightSpec = MeasureSpec.makeMeasureSpec(lp.height,
										MeasureSpec.EXACTLY);
							}
						}
						else
						{
							childHeightSpec = MeasureSpec.makeMeasureSpec(
									child.getMeasuredHeight(), MeasureSpec.EXACTLY);
						}
						final int childWidthSpec = MeasureSpec.makeMeasureSpec(
								fixedPanelWidthLimit, MeasureSpec.EXACTLY);
						child.measure(childWidthSpec, childHeightSpec);
					}
				}
				else if (lp.weight > 0)
				{
					int childHeightSpec;
					if (lp.width == 0)
					{
						// This was skipped the first time; figure out a real
						// height spec.
						if (lp.height == android.view.ViewGroup.LayoutParams.WRAP_CONTENT)
						{
							childHeightSpec = MeasureSpec.makeMeasureSpec(maxLayoutHeight,
									MeasureSpec.AT_MOST);
						}
						else if (lp.height == android.view.ViewGroup.LayoutParams.MATCH_PARENT)
						{
							childHeightSpec = MeasureSpec.makeMeasureSpec(maxLayoutHeight,
									MeasureSpec.EXACTLY);
						}
						else
						{
							childHeightSpec = MeasureSpec.makeMeasureSpec(lp.height,
									MeasureSpec.EXACTLY);
						}
					}
					else
					{
						childHeightSpec = MeasureSpec.makeMeasureSpec(
								child.getMeasuredHeight(), MeasureSpec.EXACTLY);
					}

					if (canSlide)
					{
						// Consume available space
						final int horizontalMargin = lp.leftMargin + lp.rightMargin;
						final int newWidth = widthSize - horizontalMargin;
						final int childWidthSpec = MeasureSpec.makeMeasureSpec(
								newWidth, MeasureSpec.EXACTLY);
						if (measuredWidth != newWidth)
						{
							child.measure(childWidthSpec, childHeightSpec);
						}
					}
					else
					{
						// Distribute the extra width proportionally similar to
						// LinearLayout
						final int widthToDistribute = Math.max(0, widthRemaining);
						final int addedWidth = (int) ((lp.weight * widthToDistribute) / weightSum);
						final int childWidthSpec = MeasureSpec.makeMeasureSpec(
								measuredWidth + addedWidth, MeasureSpec.EXACTLY);
						child.measure(childWidthSpec, childHeightSpec);
					}
				}
			}
		}

		this.setMeasuredDimension(widthSize, layoutHeight);
		this.mCanSlide = canSlide;
		if ((this.mDragHelper.getViewDragState() != ViewDragHelper.STATE_IDLE) && !canSlide)
		{
			// Cancel scrolling in progress, it's no longer relevant.
			this.mDragHelper.abort();
		}
	}
	
	private boolean isCanSlider = true;
	public void setDragable(boolean canslider){
		this.isCanSlider = canslider;
	}
	public boolean isDragable(){
		return isCanSlider;
	}

	@Override
	protected void onLayout(final boolean changed, final int l, final int t, final int r, final int b)
	{

		final int width = r - l;
		final int paddingLeft = this.getPaddingLeft();
		final int paddingRight = this.getPaddingRight();
		final int paddingTop = this.getPaddingTop();

		final int childCount = this.getChildCount();
		int xStart = paddingLeft;
		int nextXStart = xStart;

		if (this.mFirstLayout)
		{
			this.mSlideOffset = this.mCanSlide && this.mPreservedOpenState ? 1.f : 0.f;
		}

		for (int i = 0; i < childCount; i++)
		{
			final View child = this.getChildAt(i);

			if (child.getVisibility() == View.GONE)
			{
				continue;
			}

			final LayoutParams lp = (LayoutParams) child.getLayoutParams();

			final int childWidth = child.getMeasuredWidth();
			int offset = 0;

			if (lp.slideable)
			{
				final int margin = lp.leftMargin + lp.rightMargin;
				final int range = Math.min(nextXStart,
						width - paddingRight - this.mOverhangSize) - xStart - margin;
				this.mSlideRange = range;
				lp.dimWhenOffset = (xStart + lp.leftMargin + range + (childWidth / 2)) >
						(width - paddingRight);
				xStart += (int) (range * this.mSlideOffset) + lp.leftMargin;
			}
			else if (this.mCanSlide && (this.mParallaxBy != 0))
			{
				offset = (int) ((1 - this.mSlideOffset) * this.mParallaxBy);
				xStart = nextXStart;
			}
			else
			{
				xStart = nextXStart;
			}

			final int childLeft = xStart - offset;
			final int childRight = childLeft + childWidth;
			final int childTop = paddingTop;
			final int childBottom = childTop + child.getMeasuredHeight();
			child.layout(childLeft, paddingTop, childRight, childBottom);

			nextXStart += child.getWidth();
		}

		if (this.mFirstLayout)
		{
			if (this.mCanSlide)
			{
				if (this.mParallaxBy != 0)
				{
					this.parallaxOtherViews(this.mSlideOffset);
				}
				if (((LayoutParams) this.mSlideableView.getLayoutParams()).dimWhenOffset)
				{
					this.dimChildView(this.mSlideableView, this.mSlideOffset, this.mSliderFadeColor);
				}
			}
			else
			{
				// Reset the dim level of all children; it's irrelevant when
				// nothing moves.
				for (int i = 0; i < childCount; i++)
				{
					this.dimChildView(this.getChildAt(i), 0, this.mSliderFadeColor);
				}
			}
			this.updateObscuredViewsVisibility(this.mSlideableView);
		}

		this.mFirstLayout = false;
	}

	@Override
	protected void onSizeChanged(final int w, final int h, final int oldw, final int oldh)
	{
		super.onSizeChanged(w, h, oldw, oldh);
		// Recalculate sliding panes and their details
		if (w != oldw)
		{
			this.mFirstLayout = true;
		}
	}

	@Override
	public void requestChildFocus(final View child, final View focused)
	{
		super.requestChildFocus(child, focused);
		if (!this.isInTouchMode() && !this.mCanSlide)
		{
			this.mPreservedOpenState = child == this.mSlideableView;
		}
	}
	
	private boolean mIsClosing = false;
	
	public void markClose(){
		mIsClosing = true;
	}
	
	public void markOpen(){
		mIsClosing = false;
	}
	
	public boolean isClosing(){
		return mIsClosing;
	}

	@Override
	public boolean onInterceptTouchEvent(final MotionEvent ev)
	{
		if(isClosing()){
			return false;
		}
		
		final int action = MotionEventCompat.getActionMasked(ev);

		// Preserve the open state based on the last view that was touched.
		if (!this.mCanSlide && (action == MotionEvent.ACTION_DOWN) && (this.getChildCount() > 1))
		{
			// After the first things will be slideable.
			final View secondChild = this.getChildAt(1);
			if (secondChild != null)
			{
				this.mPreservedOpenState = !this.mDragHelper.isViewUnder(secondChild,
						(int) ev.getX(), (int) ev.getY());
			}
		}

		if (!this.mCanSlide || (this.mIsUnableToDrag && (action != MotionEvent.ACTION_DOWN)))
		{
			this.mDragHelper.cancel();
			return super.onInterceptTouchEvent(ev);
		}

		if ((action == MotionEvent.ACTION_CANCEL) || (action == MotionEvent.ACTION_UP))
		{
			this.mDragHelper.cancel();
			return false;
		}

		boolean interceptTap = false;

		switch (action)
		{
			case MotionEvent.ACTION_DOWN:
			{
				this.mIsUnableToDrag = false;
				final float x = ev.getX();
				final float y = ev.getY();
				this.mInitialMotionX = x;
				this.mInitialMotionY = y;

				if (this.mDragHelper.isViewUnder(this.mSlideableView, (int) x, (int) y) &&
						this.isDimmed(this.mSlideableView))
				{
					interceptTap = true;
				}
				break;
			}

			case MotionEvent.ACTION_MOVE:
			{
				final float x = ev.getX();
				final float y = ev.getY();
				final float adx = Math.abs(x - this.mInitialMotionX);
				final float ady = Math.abs(y - this.mInitialMotionY);
				final int slop = this.mDragHelper.getTouchSlop();
				if (((adx > slop) && (ady > adx)) || this.canScroll(this, false, Math.round(x - this.mInitialMotionX), Math.round(x), Math.round(y)))
				{
					this.mDragHelper.cancel();
					this.mIsUnableToDrag = true;
					return false;
				}
			}
		}

		final boolean interceptForDrag = this.mDragHelper.shouldInterceptTouchEvent(ev);

		return interceptForDrag || interceptTap;
	}

	@Override
	public boolean onTouchEvent(final MotionEvent ev)
	{
		if(isClosing()){
			return false;
		}
		
		if (!this.mCanSlide)
		{
			return super.onTouchEvent(ev);
		}

		this.mDragHelper.processTouchEvent(ev);

		final int action = ev.getAction();
		final boolean wantTouchEvents = true;

		switch (action & MotionEventCompat.ACTION_MASK)
		{
			case MotionEvent.ACTION_DOWN:
			{
				final float x = ev.getX();
				final float y = ev.getY();
				this.mInitialMotionX = x;
				this.mInitialMotionY = y;
				break;
			}

			case MotionEvent.ACTION_UP:
			{
				if (this.isDimmed(this.mSlideableView))
				{
					final float x = ev.getX();
					final float y = ev.getY();
					final float dx = x - this.mInitialMotionX;
					final float dy = y - this.mInitialMotionY;
					final int slop = this.mDragHelper.getTouchSlop();
					if ((((dx * dx) + (dy * dy)) < (slop * slop)) &&
							this.mDragHelper.isViewUnder(this.mSlideableView, (int) x, (int) y))
					{
						// Taps close a dimmed open pane.
						this.closePane(this.mSlideableView, 0);
						break;
					}
				}
				break;
			}
		}

		return wantTouchEvents;
	}

	private boolean closePane(final View pane, final int initialVelocity)
	{
		if (this.mFirstLayout || this.smoothSlideTo(0.f, initialVelocity))
		{
			this.mPreservedOpenState = false;
			return true;
		}
		return false;
	}

	private boolean openPane(final View pane, final int initialVelocity)
	{
		if (this.mFirstLayout || this.smoothSlideTo(1.f, initialVelocity))
		{
			this.mPreservedOpenState = true;
			return true;
		}
		return false;
	}

	/**
	 * @deprecated Renamed to {@link #openPane()} - this method is going away
	 *             soon!
	 */
	@Deprecated
	public void smoothSlideOpen()
	{
		this.openPane();
	}

	/**
	 * Open the sliding pane if it is currently slideable. If first layout has
	 * already completed this will animate.
	 *
	 * @return true if the pane was slideable and is now open/in the process of
	 *         opening
	 */
	public boolean openPane()
	{
		return this.openPane(this.mSlideableView, 0);
	}

	/**
	 * @deprecated Renamed to {@link #closePane()} - this method is going away
	 *             soon!
	 */
	@Deprecated
	public void smoothSlideClosed()
	{
		this.closePane();
	}

	/**
	 * Close the sliding pane if it is currently slideable. If first layout has
	 * already completed this will animate.
	 *
	 * @return true if the pane was slideable and is now closed/in the process
	 *         of closing
	 */
	public boolean closePane()
	{
		return this.closePane(this.mSlideableView, 0);
	}

	/**
	 * Check if the layout is completely open. It can be open either because the
	 * slider itself is open revealing the left pane, or if all content fits
	 * without sliding.
	 *
	 * @return true if sliding panels are completely open
	 */
	public boolean isOpen()
	{
		return !this.mCanSlide || (this.mSlideOffset == 1);
	}

	/**
	 * @return true if content in this layout can be slid open and closed
	 * @deprecated Renamed to {@link #isSlideable()} - this method is going away
	 *             soon!
	 */
	@Deprecated
	public boolean canSlide()
	{
		return this.mCanSlide;
	}

	/**
	 * Check if the content in this layout cannot fully fit side by side and
	 * therefore the content pane can be slid back and forth.
	 *
	 * @return true if content in this layout can be slid open and closed
	 */
	public boolean isSlideable()
	{
		return this.mCanSlide;
	}

	private void onPanelDragged(final int newLeft)
	{
		final LayoutParams lp = (LayoutParams) this.mSlideableView.getLayoutParams();
		final int leftBound = this.getPaddingLeft() + lp.leftMargin;

		this.mSlideOffset = (float) (newLeft - leftBound) / this.mSlideRange;

		if (this.mParallaxBy != 0)
		{
			this.parallaxOtherViews(this.mSlideOffset);
		}

		if (lp.dimWhenOffset)
		{
			this.dimChildView(this.mSlideableView, this.mSlideOffset, this.mSliderFadeColor);
		}
		this.dispatchOnPanelSlide(this.mSlideableView);
	}

	private void dimChildView(final View v, final float mag, final int fadeColor)
	{
		final LayoutParams lp = (LayoutParams) v.getLayoutParams();

		if ((mag > 0) && (fadeColor != 0))
		{
			final int baseAlpha = (fadeColor & 0xff000000) >>> 24;
			final int imag = (int) (baseAlpha * mag);
			final int color = (imag << 24) | (fadeColor & 0xffffff);
			if (lp.dimPaint == null)
			{
				lp.dimPaint = new Paint();
			}
			lp.dimPaint.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_OVER));
			if (ViewCompat.getLayerType(v) != ViewCompat.LAYER_TYPE_HARDWARE)
			{
				ViewCompat.setLayerType(v, ViewCompat.LAYER_TYPE_HARDWARE, lp.dimPaint);
			}
			this.invalidateChildRegion(v);
		}
		else if (ViewCompat.getLayerType(v) != ViewCompat.LAYER_TYPE_NONE)
		{
			if (lp.dimPaint != null)
			{
				lp.dimPaint.setColorFilter(null);
			}
			final DisableLayerRunnable dlr = new DisableLayerRunnable(v);
			this.mPostedRunnables.add(dlr);
			ViewCompat.postOnAnimation(this, dlr);
		}
	}

	@Override
	protected boolean drawChild(final Canvas canvas, final View child, final long drawingTime)
	{
		final LayoutParams lp = (LayoutParams) child.getLayoutParams();
		boolean result;
		final int save = canvas.save(Canvas.CLIP_SAVE_FLAG);

		if (this.mCanSlide && !lp.slideable && (this.mSlideableView != null))
		{
			// Clip against the slider; no sense drawing what will immediately
			// be covered.
			canvas.getClipBounds(this.mTmpRect);
			this.mTmpRect.right = Math.min(this.mTmpRect.right, this.mSlideableView.getLeft());
			canvas.clipRect(this.mTmpRect);
		}

		if (Build.VERSION.SDK_INT >= 11)
		{ // HC
			result = super.drawChild(canvas, child, drawingTime);
		}
		else
		{
			if (lp.dimWhenOffset && (this.mSlideOffset > 0))
			{
				if (!child.isDrawingCacheEnabled())
				{
					child.setDrawingCacheEnabled(true);
				}
				Bitmap cache = child.getDrawingCache();
				if (cache != null && !cache.isRecycled())
				{
					canvas.drawBitmap(cache, child.getLeft(), child.getTop(), lp.dimPaint);
					result = false;
				}
				else
				{
					Log.e(HSlidingPaneLayout.TAG, "drawChild: child view " + child + " returned null drawing cache");
					result = super.drawChild(canvas, child, drawingTime);
				}
			}
			else
			{
				if (child.isDrawingCacheEnabled())
				{
					child.setDrawingCacheEnabled(false);
				}
				result = super.drawChild(canvas, child, drawingTime);
			}
		}

		canvas.restoreToCount(save);

		return result;
	}

	private void invalidateChildRegion(final View v)
	{
		HSlidingPaneLayout.IMPL.invalidateChildRegion(this, v);
	}

	/**
	 * Smoothly animate mDraggingPane to the target X position within its range.
	 *
	 * @param slideOffset
	 *            position to animate to
	 * @param velocity
	 *            initial velocity in case of fling, or 0.
	 */
	boolean smoothSlideTo(final float slideOffset, final int velocity)
	{
		if (!this.mCanSlide)
		{
			// Nothing to do.
			return false;
		}

		final LayoutParams lp = (LayoutParams) this.mSlideableView.getLayoutParams();

		final int leftBound = this.getPaddingLeft() + lp.leftMargin;
		final int x = (int) (leftBound + (slideOffset * this.mSlideRange));

		if (this.mDragHelper.smoothSlideViewTo(this.mSlideableView, x, this.mSlideableView.getTop()))
		{
			this.setAllChildrenVisible();
			ViewCompat.postInvalidateOnAnimation(this);
			return true;
		}
		return false;
	}

	@Override
	public void computeScroll()
	{
		if (this.mDragHelper.continueSettling(true))
		{
			if (!this.mCanSlide)
			{
				this.mDragHelper.abort();
				return;
			}

			ViewCompat.postInvalidateOnAnimation(this);
		}
	}

	/**
	 * Set a drawable to use as a shadow cast by the right pane onto the left
	 * pane during opening/closing.
	 *
	 * @param d
	 *            drawable to use as a shadow
	 */
	public void setShadowDrawable(final Drawable d)
	{
		this.mShadowDrawable = d;
	}

	/**
	 * Set a drawable to use as a shadow cast by the right pane onto the left
	 * pane during opening/closing.
	 *
	 * @param resId
	 *            Resource ID of a drawable to use
	 */
	public void setShadowResource(final int resId)
	{
		this.setShadowDrawable(this.getResources().getDrawable(resId));
	}

	@Override
	public void draw(final Canvas c)
	{
		super.draw(c);

		final View shadowView = this.getChildCount() > 1 ? this.getChildAt(1) : null;
		if ((shadowView == null) || (this.mShadowDrawable == null))
		{
			// No need to draw a shadow if we don't have one.
			return;
		}

		final int shadowWidth = this.mShadowDrawable.getIntrinsicWidth();
		final int right = shadowView.getLeft();
		final int top = shadowView.getTop();
		final int bottom = shadowView.getBottom();
		final int left = right - shadowWidth;
		this.mShadowDrawable.setBounds(left, top, right, bottom);
		this.mShadowDrawable.draw(c);
	}

	private void parallaxOtherViews(final float slideOffset)
	{
		final LayoutParams slideLp = (LayoutParams) this.mSlideableView.getLayoutParams();
		final boolean dimViews = slideLp.dimWhenOffset && (slideLp.leftMargin <= 0);
		final int childCount = this.getChildCount();
		for (int i = 0; i < childCount; i++)
		{
			final View v = this.getChildAt(i);
			if (v == this.mSlideableView)
			{
				continue;
			}

			final int oldOffset = (int) ((1 - this.mParallaxOffset) * this.mParallaxBy);
			this.mParallaxOffset = slideOffset;
			final int newOffset = (int) ((1 - slideOffset) * this.mParallaxBy);
			final int dx = oldOffset - newOffset;

			v.offsetLeftAndRight(dx);

			if (dimViews)
			{
				this.dimChildView(v, 1 - this.mParallaxOffset, this.mCoveredFadeColor);
			}
		}
	}

	/**
	 * Tests scrollability within child views of v given a delta of dx.
	 *
	 * @param v
	 *            View to test for horizontal scrollability
	 * @param checkV
	 *            Whether the view v passed should itself be checked for
	 *            scrollability (true), or just its children (false).
	 * @param dx
	 *            Delta scrolled in pixels
	 * @param x
	 *            X coordinate of the active touch point
	 * @param y
	 *            Y coordinate of the active touch point
	 * @return true if child views of v can be scrolled by delta of dx.
	 */
	protected boolean canScroll(final View v, final boolean checkV, final int dx, final int x, final int y)
	{
		if (v instanceof ViewGroup)
		{
			final ViewGroup group = (ViewGroup) v;
			final int scrollX = v.getScrollX();
			final int scrollY = v.getScrollY();
			final int count = group.getChildCount();
			// Count backwards - let topmost views consume scroll distance
			// first.
			for (int i = count - 1; i >= 0; i--)
			{
				// TODO: Add versioned support here for transformed views.
				// This will not work for transformed views in Honeycomb+
				final View child = group.getChildAt(i);
				if (((x + scrollX) >= child.getLeft()) && ((x + scrollX) < child.getRight()) &&
						((y + scrollY) >= child.getTop()) && ((y + scrollY) < child.getBottom()) &&
						this.canScroll(child, true, dx, (x + scrollX) - child.getLeft(),
								(y + scrollY) - child.getTop()))
				{
					return true;
				}
			}
		}

		return checkV && ViewCompat.canScrollHorizontally(v, -dx);
	}

	boolean isDimmed(final View child)
	{
		if (child == null)
		{
			return false;
		}
		final LayoutParams lp = (LayoutParams) child.getLayoutParams();
		return this.mCanSlide && lp.dimWhenOffset && (this.mSlideOffset > 0);
	}

	@Override
	protected ViewGroup.LayoutParams generateDefaultLayoutParams()
	{
		return new LayoutParams();
	}

	@Override
	protected ViewGroup.LayoutParams generateLayoutParams(final ViewGroup.LayoutParams p)
	{
		return p instanceof MarginLayoutParams
				? new LayoutParams((MarginLayoutParams) p)
				: new LayoutParams(p);
	}

	@Override
	protected boolean checkLayoutParams(final ViewGroup.LayoutParams p)
	{
		return (p instanceof LayoutParams) && super.checkLayoutParams(p);
	}

	@Override
	public ViewGroup.LayoutParams generateLayoutParams(final AttributeSet attrs)
	{
		return new LayoutParams(this.getContext(), attrs);
	}

	@Override
	protected Parcelable onSaveInstanceState()
	{
		final Parcelable superState = super.onSaveInstanceState();

		final SavedState ss = new SavedState(superState);
		ss.isOpen = this.isSlideable() ? this.isOpen() : this.mPreservedOpenState;

		return ss;
	}

	@Override
	protected void onRestoreInstanceState(final Parcelable state)
	{
		final SavedState ss = (SavedState) state;
		super.onRestoreInstanceState(ss.getSuperState());

		if (ss.isOpen)
		{
			this.openPane();
		}
		else
		{
			this.closePane();
		}
		this.mPreservedOpenState = ss.isOpen;
	}
	
	private View mDragView;
	public void setDragView(View content){
		this.mDragView = content;
	}

	private class DragHelperCallback extends ViewDragHelper.Callback
	{

		@Override
		public boolean tryCaptureView(final View child, final int pointerId)
		{
			if (HSlidingPaneLayout.this.mIsUnableToDrag)
			{
				return false;
			}

//			return ((LayoutParams) child.getLayoutParams()).slideable;
			return child == mDragView;
		}

		@Override
		public void onViewDragStateChanged(final int state)
		{
			if (HSlidingPaneLayout.this.mDragHelper.getViewDragState() == ViewDragHelper.STATE_IDLE)
			{
				if (HSlidingPaneLayout.this.mSlideOffset == 0)
				{
					HSlidingPaneLayout.this.updateObscuredViewsVisibility(HSlidingPaneLayout.this.mSlideableView);
					HSlidingPaneLayout.this.dispatchOnPanelClosed(HSlidingPaneLayout.this.mSlideableView);
					HSlidingPaneLayout.this.mPreservedOpenState = false;
				}
				else
				{
					HSlidingPaneLayout.this.dispatchOnPanelOpened(HSlidingPaneLayout.this.mSlideableView);
					HSlidingPaneLayout.this.mPreservedOpenState = true;
				}
			}
		}

		@Override
		public void onViewCaptured(final View capturedChild, final int activePointerId)
		{
			// Make all child views visible in preparation for sliding things
			// around
			HSlidingPaneLayout.this.setAllChildrenVisible();
		}

		@Override
		public void onViewPositionChanged(final View changedView, final int left, final int top, final int dx, final int dy)
		{
			HSlidingPaneLayout.this.onPanelDragged(left);
			HSlidingPaneLayout.this.invalidate();
		}

		@Override
		public void onViewReleased(final View releasedChild, final float xvel, final float yvel)
		{
			final LayoutParams lp = (LayoutParams) releasedChild.getLayoutParams();
			int left = HSlidingPaneLayout.this.getPaddingLeft() + lp.leftMargin;
			if ((xvel > 0) || ((xvel == 0) && (HSlidingPaneLayout.this.mSlideOffset > 0.5f)))
			{
				left += HSlidingPaneLayout.this.mSlideRange;
			}
			HSlidingPaneLayout.this.mDragHelper.settleCapturedViewAt(left, releasedChild.getTop());
			HSlidingPaneLayout.this.invalidate();
		}

		@Override
		public int getViewHorizontalDragRange(final View child)
		{
			return HSlidingPaneLayout.this.mSlideRange;
		}

		@Override
		public int clampViewPositionHorizontal(final View child, final int left, final int dx)
		{
			final LayoutParams lp = (LayoutParams) HSlidingPaneLayout.this.mSlideableView.getLayoutParams();
			final int leftBound = HSlidingPaneLayout.this.getPaddingLeft() + lp.leftMargin;
			final int rightBound = leftBound + HSlidingPaneLayout.this.mSlideRange;

			final int newLeft = Math.min(Math.max(left, leftBound), rightBound);

			return newLeft;
		}

		@Override
		public void onEdgeDragStarted(final int edgeFlags, final int pointerId)
		{
			HSlidingPaneLayout.this.mDragHelper.captureChildView(HSlidingPaneLayout.this.mSlideableView, pointerId);
		}
	}

	public static class LayoutParams extends ViewGroup.MarginLayoutParams
	{
		private static final int[] ATTRS = new int[] {
				android.R.attr.layout_weight
		};

		/**
		 * The weighted proportion of how much of the leftover space this child
		 * should consume after measurement.
		 */
		public float weight = 0;

		/**
		 * True if this pane is the slideable pane in the layout.
		 */
		boolean slideable;

		/**
		 * True if this view should be drawn dimmed when it's been offset from
		 * its default position.
		 */
		boolean dimWhenOffset;

		Paint dimPaint;

		public LayoutParams()
		{
			super(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		}

		public LayoutParams(final int width, final int height)
		{
			super(width, height);
		}

		public LayoutParams(final android.view.ViewGroup.LayoutParams source)
		{
			super(source);
		}

		public LayoutParams(final MarginLayoutParams source)
		{
			super(source);
		}

		public LayoutParams(final LayoutParams source)
		{
			super(source);
			this.weight = source.weight;
		}

		public LayoutParams(final Context c, final AttributeSet attrs)
		{
			super(c, attrs);

			final TypedArray a = c.obtainStyledAttributes(attrs, LayoutParams.ATTRS);
			this.weight = a.getFloat(0, 0);
			a.recycle();
		}

	}

	static class SavedState extends BaseSavedState
	{
		boolean isOpen;

		SavedState(final Parcelable superState)
		{
			super(superState);
		}

		private SavedState(final Parcel in)
		{
			super(in);
			this.isOpen = in.readInt() != 0;
		}

		@Override
		public void writeToParcel(final Parcel out, final int flags)
		{
			super.writeToParcel(out, flags);
			out.writeInt(this.isOpen ? 1 : 0);
		}

		public static final Parcelable.Creator<SavedState> CREATOR =
				new Parcelable.Creator<SavedState>()
				{
					@Override
					public SavedState createFromParcel(final Parcel in)
					{
						return new SavedState(in);
					}

					@Override
					public SavedState[] newArray(final int size)
					{
						return new SavedState[size];
					}
				};
	}

	interface SlidingPanelLayoutImpl
	{
		void invalidateChildRegion(HSlidingPaneLayout parent, View child);
	}

	static class SlidingPanelLayoutImplBase implements SlidingPanelLayoutImpl
	{
		@Override
		public void invalidateChildRegion(final HSlidingPaneLayout parent, final View child)
		{
			ViewCompat.postInvalidateOnAnimation(parent, child.getLeft(), child.getTop(),
					child.getRight(), child.getBottom());
		}
	}

	static class SlidingPanelLayoutImplJB extends SlidingPanelLayoutImplBase
	{
		/*
		 * Private API hacks! Nasty! Bad!
		 *
		 * In Jellybean, some optimizations in the hardware UI renderer prevent
		 * a changed Paint on a View using a hardware layer from having the
		 * intended effect. This twiddles some internal bits on the view to
		 * force it to recreate the display list.
		 */
		private Method mGetDisplayList;
		private Field mRecreateDisplayList;

		SlidingPanelLayoutImplJB()
		{
			try
			{
				this.mGetDisplayList = View.class.getDeclaredMethod("getDisplayList", (Class[]) null);
			}
			catch (final NoSuchMethodException e)
			{
				Log.e(HSlidingPaneLayout.TAG, "Couldn't fetch getDisplayList method; dimming won't work right.", e);
			}
			try
			{
				this.mRecreateDisplayList = View.class.getDeclaredField("mRecreateDisplayList");
				this.mRecreateDisplayList.setAccessible(true);
			}
			catch (final NoSuchFieldException e)
			{
				Log.e(HSlidingPaneLayout.TAG, "Couldn't fetch mRecreateDisplayList field; dimming will be slow.", e);
			}
		}

		@Override
		public void invalidateChildRegion(final HSlidingPaneLayout parent, final View child)
		{
			if ((this.mGetDisplayList != null) && (this.mRecreateDisplayList != null))
			{
				try
				{
					this.mRecreateDisplayList.setBoolean(child, true);
					this.mGetDisplayList.invoke(child, (Object[]) null);
				}
				catch (final Exception e)
				{
					Log.e(HSlidingPaneLayout.TAG, "Error refreshing display list state", e);
				}
			}
			else
			{
				// Slow path. REALLY slow path. Let's hope we don't get here.
				child.invalidate();
				return;
			}
			super.invalidateChildRegion(parent, child);
		}
	}

	static class SlidingPanelLayoutImplJBMR1 extends SlidingPanelLayoutImplBase
	{
		@Override
		public void invalidateChildRegion(final HSlidingPaneLayout parent, final View child)
		{
			ViewCompat.setLayerPaint(child, ((LayoutParams) child.getLayoutParams()).dimPaint);
		}
	}

	class AccessibilityDelegate extends AccessibilityDelegateCompat
	{
		private final Rect mTmpRect = new Rect();

		@Override
		public void onInitializeAccessibilityNodeInfo(final View host, final AccessibilityNodeInfoCompat info)
		{
			final AccessibilityNodeInfoCompat superNode = AccessibilityNodeInfoCompat.obtain(info);
			super.onInitializeAccessibilityNodeInfo(host, superNode);
			this.copyNodeInfoNoChildren(info, superNode);
			superNode.recycle();

			info.setClassName(HSlidingPaneLayout.class.getName());
			info.setSource(host);

			final ViewParent parent = ViewCompat.getParentForAccessibility(host);
			if (parent instanceof View)
			{
				info.setParent((View) parent);
			}

			// This is a best-approximation of addChildrenForAccessibility()
			// that accounts for filtering.
			final int childCount = HSlidingPaneLayout.this.getChildCount();
			for (int i = 0; i < childCount; i++)
			{
				final View child = HSlidingPaneLayout.this.getChildAt(i);
				if (!this.filter(child) && (child.getVisibility() == View.VISIBLE))
				{
					// Force importance to "yes" since we can't read the value.
					ViewCompat.setImportantForAccessibility(
							child, ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_YES);
					info.addChild(child);
				}
			}
		}

		@Override
		public void onInitializeAccessibilityEvent(final View host, final AccessibilityEvent event)
		{
			super.onInitializeAccessibilityEvent(host, event);

			event.setClassName(HSlidingPaneLayout.class.getName());
		}

		@Override
		public boolean onRequestSendAccessibilityEvent(final ViewGroup host, final View child,
				final AccessibilityEvent event)
		{
			if (!this.filter(child))
			{
				return super.onRequestSendAccessibilityEvent(host, child, event);
			}
			return false;
		}

		public boolean filter(final View child)
		{
			return HSlidingPaneLayout.this.isDimmed(child);
		}

		/**
		 * This should really be in AccessibilityNodeInfoCompat, but there
		 * unfortunately seem to be a few elements that are not easily cloneable
		 * using the underlying API. Leave it private here as it's not
		 * general-purpose useful.
		 */
		private void copyNodeInfoNoChildren(final AccessibilityNodeInfoCompat dest,
				final AccessibilityNodeInfoCompat src)
		{
			final Rect rect = this.mTmpRect;

			src.getBoundsInParent(rect);
			dest.setBoundsInParent(rect);

			src.getBoundsInScreen(rect);
			dest.setBoundsInScreen(rect);

			dest.setVisibleToUser(src.isVisibleToUser());
			dest.setPackageName(src.getPackageName());
			dest.setClassName(src.getClassName());
			dest.setContentDescription(src.getContentDescription());

			dest.setEnabled(src.isEnabled());
			dest.setClickable(src.isClickable());
			dest.setFocusable(src.isFocusable());
			dest.setFocused(src.isFocused());
			dest.setAccessibilityFocused(src.isAccessibilityFocused());
			dest.setSelected(src.isSelected());
			dest.setLongClickable(src.isLongClickable());

			dest.addAction(src.getActions());

			dest.setMovementGranularities(src.getMovementGranularities());
		}
	}

	private class DisableLayerRunnable implements Runnable
	{
		final View mChildView;

		DisableLayerRunnable(final View childView)
		{
			this.mChildView = childView;
		}

		@Override
		public void run()
		{
			if (this.mChildView.getParent() == HSlidingPaneLayout.this)
			{
				ViewCompat.setLayerType(this.mChildView, ViewCompat.LAYER_TYPE_NONE, null);
				HSlidingPaneLayout.this.invalidateChildRegion(this.mChildView);
			}
			HSlidingPaneLayout.this.mPostedRunnables.remove(this);
		}
	}
}