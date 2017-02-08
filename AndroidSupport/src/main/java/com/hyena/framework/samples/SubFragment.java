package com.hyena.framework.samples;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

/**
 * Created by yangzc on 16/8/5.
 */
public class SubFragment extends UIFragment {

    @Override
    public void onCreateImpl(Bundle savedInstanceState) {
        super.onCreateImpl(savedInstanceState);
        setSlideable(true);
        setStatusTintBarEnable(false);
        setStatusTintBarColor(getResources().getColor(R.color.color2));
    }

    @Override
    public View onCreateViewImpl(Bundle savedInstanceState) {
        LinearLayout view = new LinearLayout(getActivity());
        view.setBackgroundColor(getResources().getColor(R.color.color2));
        Button btn = new Button(getActivity());
        btn.setText("jdsklfjlksjflksdjflkj");
        btn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                setStatusTintBarEnable(!isStatusBarTintEnabled());
            }
        });
        view.addView(btn);
        return view;
    }
}
