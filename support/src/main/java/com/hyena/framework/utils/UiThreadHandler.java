package com.hyena.framework.utils;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;

/**
 * 
 * @author yangzc
 * @version 1.0
 * @createTime 2014年11月29日 下午5:54:18
 *
 */
public class UiThreadHandler {
	private static final int LOOP = 0x001;
	private static final int LOOP_TIME = 0x002;

	private static Handler uiHandler = new Handler(Looper.getMainLooper()) {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
				case LOOP:
					Runnable runnable = (Runnable) msg.obj;
                    run(runnable);
					loop(runnable, msg.arg1);
					break;
				case LOOP_TIME:
					LoopHandler handler = (LoopHandler) msg.obj;
					try {
						handler.run();
					} catch (Exception e) {
					}
					loop(handler, msg.arg1, (--msg.arg2));
					break;
			}
		}

        public void run(Runnable runnable) {
            try {
                runnable.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

	private static Object token = new Object();

	public final static boolean post(Runnable r) {
		if (uiHandler == null) {
			return false;
		}
		return uiHandler.post(new ReleaseRunnable(r));
	}
	
	public final static boolean postOnce(Runnable r) {
		if (uiHandler == null) {
			return false;
		}
		uiHandler.removeCallbacks(r, token);
		return uiHandler.postAtTime(r, token, SystemClock.uptimeMillis());
	}

	public final static boolean postDelayed(Runnable r, long delayMillis) {
		if (uiHandler == null) {
			return false;
		}

		return uiHandler.postDelayed(new ReleaseRunnable(r), delayMillis);
	}

	public final static Handler getUiHandler() {
		return uiHandler;
	}

	public final static boolean postOnceDelayed(Runnable r, long delayMillis) {
		if (uiHandler == null) {
			return false;
		}
		uiHandler.removeCallbacks(r, token);
		return uiHandler.postAtTime(r, token, SystemClock.uptimeMillis() + delayMillis);
	}

	public static void loop(Runnable runnable, int delay) {
		if (uiHandler == null) {
			return;
		}
		uiHandler.removeMessages(LOOP);
		Message msg = Message.obtain();
		msg.what = LOOP;
		msg.obj = runnable;
		msg.arg1 = delay;
		uiHandler.sendMessageDelayed(msg, delay);
	}

	public static void stopLoop() {
		if (uiHandler == null) {
			return;
		}
		uiHandler.removeMessages(LOOP);
	}

	public static void loop(LoopHandler handler, int delay, int time) {
		if (uiHandler == null || handler == null) {
			return;
		}
		uiHandler.removeMessages(LOOP_TIME);
		if(time == 0) {
			handler.end();
			return;
		}
		Message msg = Message.obtain();
		msg.what = LOOP_TIME;
		msg.obj = handler;
		msg.arg1 = delay;
		msg.arg2 = time;
		uiHandler.sendMessageDelayed(msg, delay);
	}

	public static interface LoopHandler {
		void run();
		void end();
	}
	
	/**
	 * 可容错的Runnable
	 * @author yangzc
	 *
	 */
	public static class ReleaseRunnable implements Runnable {

		private Runnable mRunnable;
		public ReleaseRunnable(Runnable runnable){
			this.mRunnable = runnable;
		}
		
		@Override
		public void run() {
			if(mRunnable != null){
				try {
					mRunnable.run();
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		}
		
	}
}
