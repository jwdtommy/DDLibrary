package com.hyena.framework.samples.widgets.pull;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.hyena.framework.app.widget.AbsRefreshablePanel;
import com.hyena.framework.samples.R;
import com.hyena.framework.utils.UIUtils;

/**
 * Created by yangzc on 16/9/16.
 */

public class RefreshablePanelHeaderPanel extends AbsRefreshablePanel {

    private TextView mTvTitle;

    private int mCurrentStatus;

    public RefreshablePanelHeaderPanel(Context context) {
        super(context);
        init();
    }

    public RefreshablePanelHeaderPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RefreshablePanelHeaderPanel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        View view = View.inflate(getContext(), R.layout.layout_pull2refresh_header, this);
        this.mTvTitle = (TextView) view.findViewById(R.id.tv_pull2refresh_title);
        setStatus(STATUS_START_PULL);
    }

    @Override
    public void setStatus(int status) {
        if (status == mCurrentStatus)
            return;

        this.mCurrentStatus = status;
        switch (status) {
            case STATUS_START_PULL: {
                mTvTitle.setText("下拉获取更多");
                break;
            }
            case STATUS_READY_REFRESH: {
                mTvTitle.setText("松开获取更多");
                break;
            }
            case STATUS_REFRESH: {
                mTvTitle.setText("正在加载中...");
                break;
            }
            case STATUS_RESET: {
                mTvTitle.setText("下拉获取更多");
                break;
            }
        }
    }

    @Override
    public int getContentHeight() {
        return UIUtils.dip2px(50);
    }

}
