package com.hyena.framework.animation;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

import com.hyena.framework.clientlog.LogUtil;

/**
 * 视图基类
 * @author yangzc
 */
public class CGLView extends View implements RenderView {

	private static final int MSG_REFRESH_VIEW = 0x00000001;
	private Handler mHandler;
	
	public CGLView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	public CGLView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public CGLView(Context context) {
		super(context);
		init();
	}
	
	private void init(){
		mHandler = new Handler(Looper.getMainLooper()){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				int what = msg.what;
				switch (what) {
				case MSG_REFRESH_VIEW:
				{
//					LogUtil.v("CGLView", "refresh");
					if (mDirector != null && mDirector.isViewVisible()) {
						invalidate();
					}

					int delay = EngineConfig.MIN_REFRESH_DELAY;
					if (mDirector != null) {
						delay = mDirector.getRefreshDelay();
					}
					sendEmptyMessageDelayed(MSG_REFRESH_VIEW, delay);
					break;
				}
				default:
					break;
				}
			}
		};
		mSetfil = new PaintFlagsDrawFilter(0, Paint.FILTER_BITMAP_FLAG);
	}
	
	private PaintFlagsDrawFilter mSetfil;
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		try {
			if(mSetfil != null)
				canvas.setDrawFilter(mSetfil);
			
			drawEngine(canvas);
		} catch (Throwable e) {
		}
	}
	
	/**
	 * 绘画引擎
	 * @param canvas
	 */
	protected void drawEngine(Canvas canvas){
		if (mDirector == null)
			return;

		CScene scene = mDirector.getActiveScene();
		if(scene != null){
//			long start = System.currentTimeMillis();
			scene.render(canvas);
//			DebugUtils.debug("yangzc", "cost: " + (System.currentTimeMillis() - start));
		}
	}

	/**
	 * 开始刷新
	 */
	public void startRefresh(){
		if(mHandler != null){
			mHandler.sendEmptyMessage(MSG_REFRESH_VIEW);
		}
	}
	
	/**
	 * 停止刷新
	 */
	public void stopRefresh(){
		if(mHandler != null){
			mHandler.removeMessages(MSG_REFRESH_VIEW);
		}
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		Rect rect = new Rect(0, 0, getWidth(), getHeight());
		if(mSizeChangeListener != null){
			mSizeChangeListener.onSizeChange(rect);
		}
	}
	
	private SizeChangeListener mSizeChangeListener;
	public void setSizeChangeListener(SizeChangeListener sizeChangeListener){
		this.mSizeChangeListener = sizeChangeListener;
		
		if(getWidth() >0 && getHeight() > 0){
			Rect rect = new Rect(0, 0, getWidth(), getHeight());
			if(mSizeChangeListener != null){
				mSizeChangeListener.onSizeChange(rect);
			}
		}
	}

	@Override
	public void computeScroll() {
		super.computeScroll();
		if (mDirector != null) {

		}
	}

	@Override
	public void forceRefresh() {
		postInvalidate();
	}

	private Director mDirector;
	@Override
	public void setDirector(Director director) {
		this.mDirector = director;
	}
}
