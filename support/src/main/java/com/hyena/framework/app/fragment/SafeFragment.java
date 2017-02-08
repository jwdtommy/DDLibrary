package com.hyena.framework.app.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hyena.framework.clientlog.LogUtil;
import com.hyena.framework.config.FrameworkConfig;

/**
 * 安全Fragment
 * @author yangzc
 * <li>注意：不要随意修改</li>
 * <li>目的: 统一崩溃逻辑，在场景发生崩溃时能做到优雅退出</li>
 */
public class SafeFragment extends Fragment {

	private boolean DEBUG = false;
	
	public void onAttachImpl(Activity activity) {
		super.onAttach(activity);
		debug("onAttachImpl");
	}
	
	public void onCreateImpl(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		debug("onCreateImpl");
	}
	
	public View onCreateViewImpl(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		debug("onCreateViewImpl");
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		debug("onViewCreatedImpl");
	}
	
	public void onActivityCreatedImpl(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		debug("onActivityCreatedImpl");
	}
	
	public void onStartImpl() {
		super.onStart();
		debug("onStartImpl");
	}
	
	public void onResumeImpl() {
		super.onResume();
		debug("onResumeImpl");
	}
	
	public void onPauseImpl() {
		super.onPause();
		debug("onPauseImpl");
	}
	
	public void onStopImpl() {
		super.onStop();
		debug("onStopImpl");
	}
	
	public void onDestroyViewImpl() {
		super.onDestroyView();
		debug("onDestroyViewImpl");
	}
	
	public void onDestroyImpl() {
		super.onDestroy();
		debug("onDestroyImpl");
	}
	
	public void onDetachImpl() {
		super.onDetach();
		debug("onDetachImpl");
    }
	
	public void debug(String msg){
		if(DEBUG || FrameworkConfig.getConfig().isDebug()) {
            LogUtil.d("SafeFragment", ((Object)this).getClass().getSimpleName() + "|msg:" + msg);
        }
	}

	/**
	 * 发生异常
	 * @param e
	 */
	public void onError(Throwable e) {
		e.printStackTrace();
		throw new RuntimeException(e);
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		//TODO 不可见变为可见过程中，fragment manager有可能为空
		super.setUserVisibleHint(isVisibleToUser);
        debug("setUserVisibleHint, isVisibleToUser: " + isVisibleToUser);
	}

	//===========================隐藏以下生命周期==================================
	@Override
	final public void onAttach(Activity activity) {
		try {
			onAttachImpl(activity);
		} catch (Throwable e) {
			onError(e);
		}
	}
	
	@Override
	final public void onCreate(Bundle savedInstanceState) {
		try {
			onCreateImpl(savedInstanceState);
		} catch (Throwable e) {
			onError(e);
		}
	}
	
	@Override
	final public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		try {
			return onCreateViewImpl(inflater, container, savedInstanceState);
		} catch (Throwable e) {
			onError(e);
		}
		return new View(getActivity());
	}
	
	@Override
	final public void onViewCreated(View view, Bundle savedInstanceState) {
		try {
			onViewCreatedImpl(view, savedInstanceState);
		} catch (Throwable e) {
			onError(e);
		}
	}
	
	@Override
	final public void onActivityCreated(Bundle savedInstanceState) {
		try {
			onActivityCreatedImpl(savedInstanceState);
		} catch (Throwable e) {
			onError(e);
		}
	}
	
	@Override
	final public void onStart() {
		try {
			onStartImpl();
		} catch (Throwable e) {
			onError(e);
		}
	}
	
	@Override
	final public void onResume() {
		try {
			onResumeImpl();
		} catch (Throwable e) {
			onError(e);
		}
	}
	
	@Override
	final public void onPause() {
		try {
			onPauseImpl();
		} catch (Throwable e) {
			onError(e);
		}
	}
	
	@Override
	final public void onStop() {
		try {
			onStopImpl();
		} catch (Throwable e) {
			onError(e);
		}
	}
	
	@Override
	final public void onDestroyView() {
		try {
			onDestroyViewImpl();
		} catch (Throwable e) {
			onError(e);
		}
	}
	
	@Override
	final public void onDestroy() {
		try {
			onDestroyImpl();
		} catch (Throwable e) {
			onError(e);
		}
	}
	
	@Override
	final public void onDetach() {
		try {
			onDetachImpl();
		} catch (Throwable e) {
			onError(e);
		}
	}

}
