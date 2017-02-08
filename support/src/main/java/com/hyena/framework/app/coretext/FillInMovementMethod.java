/**
 * Copyright (C) 2015 The AppFramework Project
 */
package com.hyena.framework.app.coretext;

import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.MotionEvent;
import android.widget.TextView;

import com.hyena.framework.app.coretext.span.FillInSpan;

public class FillInMovementMethod extends LinkMovementMethod {

	private FillInSpan mFillInSpan;
	
	@Override
	public boolean onTouchEvent(TextView widget, Spannable buffer,
			MotionEvent event) {
		int action = event.getAction();

        if (action == MotionEvent.ACTION_UP ||
            action == MotionEvent.ACTION_DOWN) {
            int x = (int) event.getX();
            int y = (int) event.getY();

            x -= widget.getTotalPaddingLeft();
            y -= widget.getTotalPaddingTop();

            x += widget.getScrollX();
            y += widget.getScrollY();

            if (buffer != null) {
            	FillInSpan[] link = buffer.getSpans(0, buffer.length(), FillInSpan.class);
				for (int i = 0; i < link.length; i++) {
					if (link[i].isPositionIn(x, y)) {
						if (action == MotionEvent.ACTION_UP) {
							link[i].onClick(widget);
							mFillInSpan = link[i];
							if (mFillInSelectedListener != null) {
								mFillInSelectedListener.onFillInSelected(mFillInSpan);
							}
						} else if (action == MotionEvent.ACTION_DOWN) {
							if (mFillInSpan != null) {
								mFillInSpan.setFocus(false);
							}
							link[i].setFocus(true);
						}
						return true;
					}
				}
			}
            
            Layout layout = widget.getLayout();
            int line = layout.getLineForVertical(y);
            int off = layout.getOffsetForHorizontal(line, x);
            
            ClickableSpan[] link = buffer.getSpans(off, off, ClickableSpan.class);
            if (link.length != 0) {
                if (action == MotionEvent.ACTION_UP) {
                    link[0].onClick(widget);
                } else if (action == MotionEvent.ACTION_DOWN) {
                    Selection.setSelection(buffer,
                                           buffer.getSpanStart(link[0]),
                                           buffer.getSpanEnd(link[0]));
                }

                return true;
            } else {
                Selection.removeSelection(buffer);
            }
        }

		return super.onTouchEvent(widget, buffer, event);
	}
	
	private OnFillInSelectedListener mFillInSelectedListener;
	
	public void setFillInSelectedListener(OnFillInSelectedListener listener){
		this.mFillInSelectedListener = listener;
	}
	public static interface OnFillInSelectedListener {
		
		public void onFillInSelected(FillInSpan fillInSpan);
	}
	
	/**
	 * 获得当前选中的输入空
	 * @return
	 */
	public FillInSpan getFillInSpan(){
		return mFillInSpan;
	}
	
	/**
	 * 设置当前输入框
	 * @param fillInSpan
	 */
	public void setFillInSpan(FillInSpan fillInSpan){
		if (fillInSpan == null) {
			return;
		}
		this.mFillInSpan = fillInSpan;
		mFillInSpan.setFocus(true);
	}
	
	public static FillInMovementMethod getInstance() {
        if (sInstance == null)
            sInstance = new FillInMovementMethod();

        return sInstance;
    }

    private static FillInMovementMethod sInstance;
}
