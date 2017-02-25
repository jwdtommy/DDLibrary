package com.dd.news.widgets;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.dd.framework.widgets.EmptyView;
import com.dd.news.R;
/**
 * Created by adong on 17/2/19.
 */

public class TDEmptyView extends EmptyView {

    public TDEmptyView(Context context) {
        super(context);
        init();
    }

    public TDEmptyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        View view = View.inflate(getContext(), R.layout.layout_empty, null);
        addView(view,new RelativeLayout.LayoutParams(-1,-1));
    }
}
