/**
 * Copyright (C) 2015 The KnowBoxTeacher2.0 Project
 */
package com.hyena.framework.servcie.debug;

public interface DebugService {

	public static final String SERVICE_NAME = "debug_service";
	
	public void enableDebug(boolean debugMode);
	
	public void showDebugMsg(String msg);
	
	public void clearMsg();
	
	public DebugServerObserver getObserver();
	
	public boolean isDebug();
	
}
