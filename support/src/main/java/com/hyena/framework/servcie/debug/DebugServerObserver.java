/**
 * Copyright (C) 2015 The KnowBoxTeacher2.0 Project
 */
package com.hyena.framework.servcie.debug;

import java.util.ArrayList;
import java.util.List;

public class DebugServerObserver {

	private List<DebugModeListener> mDebugModeListeners = new ArrayList<DebugModeListener>();

	public void addDebugModeListener(DebugModeListener listener){
		if(mDebugModeListeners.contains(listener))
			return;
		mDebugModeListeners.add(listener);
	}
	
	public void removeDebugModeListener(DebugModeListener listener){
		mDebugModeListeners.remove(listener);
	}
	
	public void notifyDebugChange(boolean debugMode){
		for(DebugModeListener listener : mDebugModeListeners){
			listener.onDebugModeChange(debugMode);
		}
	}
	
	public void notifyShowDebugMsg(String msg){
		for(DebugModeListener listener : mDebugModeListeners){
			listener.onShowDebugMsg(msg);
		}
	}
}
