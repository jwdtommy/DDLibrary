/**
 * Copyright (C) 2014 The BaiduMusicFramework Project
 */
package com.hyena.framework.animation;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * @author yangzc
 * @version 1.0
 * @createTime 2014年9月26日 下午4:35:02
 * 
 */
public class CSurfaceView extends SurfaceView implements SurfaceHolder.Callback, RenderView {

	private static final int MSG_REFRESH_VIEW = 0x00000001;
	private Handler mHandler;
	private SurfaceHolder mSurfaceHolder;
	
	public CSurfaceView(Context context) {
		super(context);
		init();
	}

	public CSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public CSurfaceView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	private void init(){
		mSurfaceHolder = getHolder();
		mSurfaceHolder.addCallback(this);
//		mSurfaceHolder.setFormat(PixelFormat.TRANSPARENT);
		mSetfil = new PaintFlagsDrawFilter(0, Paint.FILTER_BITMAP_FLAG);
	}
	
	private PaintFlagsDrawFilter mSetfil;
	protected void doDraw(Canvas canvas) {
		try {
			canvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
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
			scene.render(canvas);
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
	public void surfaceCreated(SurfaceHolder holder) {
		HandlerThread thread = new HandlerThread("cSurfaceView");
		thread.start();
		mHandler = new Handler(thread.getLooper()){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				int what = msg.what;
				switch (what) {
				case MSG_REFRESH_VIEW:
					if(mDirector != null && mDirector.isViewVisible()){
						try {
							Canvas canvas = mSurfaceHolder.lockCanvas(null);
							doDraw(canvas);
							mSurfaceHolder.unlockCanvasAndPost(canvas);
						} catch (Exception e) {
						}
					}
					int delay = EngineConfig.MIN_REFRESH_DELAY;
					if (mDirector != null) {
						delay = mDirector.getRefreshDelay();
					}
					sendEmptyMessageDelayed(MSG_REFRESH_VIEW, delay);
					break;
				default:
					break;
				}
			}
		};
		startRefresh();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		Rect rect = new Rect(0, 0, getWidth(), getHeight());
		if(mSizeChangeListener != null){
			mSizeChangeListener.onSizeChange(rect);
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		stopRefresh();
	}

	private Director mDirector;
	@Override
	public void setDirector(Director director) {
		this.mDirector = director;
	}

	@Override
	public void forceRefresh() {

	}
}
