package com.dd.framework.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by J.Tommy on 16/12/12.
 */
public class SafeFragment extends Fragment {

    public void onAttachImpl(Context context) {
        super.onAttach(context);
    }

    public void onActivityCreatedImpl(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void onCreateImpl(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateViewImpl(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public void onViewCreatedImpl(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void onStartImpl() {
        super.onStart();
    }

    public void onResumeImpl() {
        super.onResume();
    }

    public void onPauseImpl() {
        super.onPause();
    }

    public void onStopImpl() {
        super.onStop();
    }

    public void onDetachImpl() {
        super.onDetach();
    }

    public void onDestroyViewImpl() {
        super.onDestroyView();
    }

    public void onDestroyImpl() {
        super.onDestroy();
    }

    @Override
    final public void onAttach(Context context) {
        super.onAttach(context);
        try {
            onAttachImpl(context);
        } catch (Exception e) {

        }
    }

    @Override
    final public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            onActivityCreatedImpl(savedInstanceState);
        } catch (Exception e) {
            onError(e);
        }
    }

    @Override
    final public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            onCreateImpl(savedInstanceState);
        } catch (Exception e) {
            onError(e);
        }
    }

    @Nullable
    @Override
    final public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        try {
            return onCreateViewImpl(inflater, container, savedInstanceState);
        } catch (Exception e) {
            onError(e);
        }
        return new View(getActivity());
    }

    @Override
    final public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        try {
            onViewCreatedImpl(view, savedInstanceState);
        } catch (Exception e) {
            onError(e);
        }
    }

    @Override
    final public void onStart() {
        super.onStart();
        try {
            onStartImpl();
        } catch (Exception e) {
            onError(e);
        }
    }

    @Override
    final public void onResume() {
        super.onResume();
        try {
            onResumeImpl();
        } catch (Exception e) {
            onError(e);
        }
    }

    @Override
    final public void onPause() {
        super.onPause();
        try {
            onPauseImpl();
        } catch (Exception e) {
            onError(e);
        }
    }

    @Override
    final public void onStop() {
        super.onStop();
        try {
            onStopImpl();
        } catch (Exception e) {
            onError(e);
        }
    }

    @Override
    final public void onDetach() {
        super.onDetach();
        try {
            onDetachImpl();
        } catch (Exception e) {
            onError(e);
        }
    }

    @Override
    final public void onDestroyView() {
        super.onDestroyView();
        try {
            onDestroyViewImpl();
        } catch (Exception e) {
            onError(e);
        }
    }

    @Override
    final public void onDestroy() {
        super.onDestroy();
        try {
            onDestroyImpl();
        } catch (Exception e) {
            onError(e);
        }
    }

    public void onError(Exception e) {
        e.printStackTrace();
        throw new RuntimeException(e);
    }
}
