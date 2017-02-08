/**
 * Copyright (C) 2015 The KnowBoxTeacher2.0 Project
 */
package com.hyena.framework.servcie.debug;

import java.util.ArrayList;
import java.util.List;

public class DebugServiceImpl implements DebugService {

	private List<String> mMsgList = new ArrayList<String>();
//	private WindowManager mWindowManager;
//	private WindowManager.LayoutParams mLayoutParams;
//	private View mWindowView;
	
	private boolean mDebugMode = false;
	private DebugServerObserver mObserver = new DebugServerObserver();

	@Override
	public void enableDebug(boolean debugMode) {
		this.mDebugMode = debugMode;
		getObserver().notifyDebugChange(debugMode);
	}
	
	@Override
	public DebugServerObserver getObserver() {
		return mObserver;
	}
	
	@Override
	public void clearMsg() {
		mMsgList.clear();
		getObserver().notifyShowDebugMsg("");
	}
	
	@Override
	public void showDebugMsg(String msg) {
		if(!mDebugMode)
			return;
		
		if(mMsgList.size() > 10){
			mMsgList.remove(mMsgList.size() - 1);
		}
		mMsgList.add(0, msg);
		StringBuffer buffer = new StringBuffer();
		for(int i =0; i< mMsgList.size(); i++){
			buffer.append( mMsgList.get(i) + "\n");
		}
		getObserver().notifyShowDebugMsg(buffer.toString());
	}
	
	@Override
	public boolean isDebug() {
		return mDebugMode;
	}
	
//	@Override
//	public void showWindows() {
//		if(mWindowView != null)
//			return;
//		
//		mWindowManager = (WindowManager) BaseApp.getAppContext()
//				.getSystemService(Context.WINDOW_SERVICE);
//		mWindowView = View.inflate(BaseApp.getAppContext(), R.layout.windows_debug, null);
//		mWindowView.setOnTouchListener(new View.OnTouchListener() {
//			float lastX, lastY;
//
//			@SuppressLint("ClickableViewAccessibility")
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//				final int action = event.getAction();
//
//				float x = event.getX();
//				float y = event.getY();
//				if (action == MotionEvent.ACTION_DOWN) {
//					lastX = x;
//					lastY = y;
//				} else if (action == MotionEvent.ACTION_MOVE) {
//					mLayoutParams.x += (int) (x - lastX);
//					mLayoutParams.y += (int) (y - lastY);
//					mWindowManager.updateViewLayout(mWindowView, mLayoutParams);
//				}
//				return true;
//			}
//		});
//
//		mLayoutParams = new WindowManager.LayoutParams();
//		mLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
//		mLayoutParams.format = 1;
//		mLayoutParams.flags = 40;
//		mLayoutParams.x = 0;
//		mLayoutParams.y = 0;
//		mLayoutParams.gravity = Gravity.LEFT | Gravity.TOP;
//		mLayoutParams.format = PixelFormat.RGBA_8888;
//		mLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
//		mLayoutParams.height = 100;
//		mWindowManager.addView(mWindowView, mLayoutParams);
//	}

//	@Override
//	public void hiddenWindows(){
//		if(mWindowManager != null && mWindowView != null){
//			mWindowManager.removeViewImmediate(mWindowView);
//		}
//		mWindowView = null;
//	}
	
//	@Override
//	public void appendDebugMsg(final String txt){
//		UiThreadHandler.post(new Runnable(){
//			@Override
//			public void run() {
//				if(mWindowView != null){
//					if(mMsgList.size()> 10){
//						List<String> list = mMsgList.subList(0, 10);
//						mMsgList.clear();
//						mMsgList.addAll(list);
//					}
//					mMsgList.add(0, txt);
//					TextView msgTxt = (TextView) mWindowView.findViewById(R.id.windows_debug_txt);
//					msgTxt.setText("");
//					for (int i = 0; i < mMsgList.size(); i++) {
//						String msg = mMsgList.get(i);
//						if(msg != null){
//							msgTxt.append(msg);
//						}
//					}
//				}
//			}
//		});
//	}
}
