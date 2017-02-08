package com.hyena.framework.samples.widgets;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hyena.framework.clientlog.LogUtil;

/**
 * Created by yangzc on 16/6/2.
 */
public class ChartFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final CircleChart chart = new CircleChart(getActivity());
        chart.setItemSelectListener(new CircleChart.OnItemSelectListener() {

            @Override
            public void onPreItemSelected(String tag, boolean isReset) {
                LogUtil.v("yangzc", "onPreItemSelected : isReset: " + isReset);
                chart.syncData();
            }

            @Override
            public void onItemSelected(String tag, boolean isReset) {
                LogUtil.v("yangzc", "onItemSelected : isReset: " + isReset);

            }
        });
        return chart;
    }
}
