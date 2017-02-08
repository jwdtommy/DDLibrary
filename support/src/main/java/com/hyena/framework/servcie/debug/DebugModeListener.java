/**
 * Copyright (C) 2015 The KnowBoxTeacher2.0 Project
 */
package com.hyena.framework.servcie.debug;

public interface DebugModeListener {

	public void onDebugModeChange(boolean debugMode);
	
	public void onShowDebugMsg(String msg);
	
}
