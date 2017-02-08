package com.hyena.framework.samples.gridlayout;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.GridLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.utils.UIUtils;

/**
 * Created by yangzc on 16/5/3.
 */
public class GridLayoutSamples extends BaseUIFragment {

    @Override
    public View onCreateViewImpl(Bundle savedInstanceState) {
        return new CustomGridLayout(getActivity());
    }

    public class CustomGridLayout extends GridLayout {

        public CustomGridLayout(Context context) {
            super(context);
            setBackgroundColor(Color.RED);
            setRowCount(2);
            setColumnCount(2);

            Spec rowSpec = GridLayout.spec(0);
            Spec columnSpec = GridLayout.spec(0, 2, CENTER);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams(rowSpec, columnSpec);

            TextView textView = new TextView(getContext());
            textView.setText("Demo");
            textView.setBackgroundColor(Color.BLUE);
            params.height = 500- UIUtils.dip2px(1);
            params.width = UIUtils.getWindowWidth(getActivity());
            RelativeLayout rl = new RelativeLayout(getContext());
            rl.addView(textView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            rl.setPadding(0, 0, 0, UIUtils.dip2px(1));
            addView(rl, params);

            rowSpec = GridLayout.spec(1);
            columnSpec = GridLayout.spec(0);
            params = new GridLayout.LayoutParams(rowSpec, columnSpec);
            RelativeLayout layout = new RelativeLayout(getContext());
            textView = new TextView(getContext());
            textView.setText("Demo1");
            textView.setBackgroundColor(Color.BLUE);
            layout.addView(textView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            layout.setPadding(0, 0, UIUtils.dip2px(1), UIUtils.dip2px(1));
            params.height = 500;
            params.width = UIUtils.getWindowWidth(getActivity())/2;
            addView(layout, params);

            rowSpec = GridLayout.spec(1);
            columnSpec = GridLayout.spec(1);
            params = new GridLayout.LayoutParams(rowSpec, columnSpec);
            textView = new TextView(getContext());
            textView.setText("Demo2");
            textView.setBackgroundColor(Color.BLUE);
            params.height = 500;
            params.width = UIUtils.getWindowWidth(getActivity())/2;
            addView(textView, params);
        }
    }

    @Override
    public void onError(Throwable e) {
//        super.onError(e);
        e.printStackTrace();
    }
}
