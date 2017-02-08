package com.hyena.framework.app.widget;

import android.content.Context;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hyena.framework.R;
import com.hyena.framework.clientlog.LogUtil;
import com.hyena.framework.utils.UIUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by yangzc on 16/10/28.
 */
public class CommonRefreshableHeader extends AbsRefreshablePanel {

    private static final String TAG = "CommonRefreshableHeader";

    private TextView mTvTitle;
    private TextView mTvDateTime;
    private ProgressBar mProgressBar;

    public CommonRefreshableHeader(Context context) {
        super(context);
        init();
    }

    public CommonRefreshableHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CommonRefreshableHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        View.inflate(getContext(), R.layout.pull_to_refresh_header, this);

        mTvTitle = (TextView) findViewById(R.id.pull_to_refresh_text);
        mTvDateTime = (TextView) findViewById(R.id.pull_to_refresh_sub_text);
        mProgressBar = (ProgressBar) findViewById(R.id.pull_to_refresh_progress);
    }

    private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

    private int mLastStatus = -1;
    @Override
    public void setStatus(int status) {
        LogUtil.v(TAG, "status: " + status);
        if (mLastStatus == status) {
            return;
        }
        mLastStatus = status;
        switch (status) {
            case STATUS_RESET: {
                mTvTitle.setText("下拉刷新");
                mTvDateTime.setText("上次刷新时间 : " + sdf.format(new Date()));
                break;
            }
            case STATUS_START_PULL: {
                mTvTitle.setText("下拉刷新");
                break;
            }
            case STATUS_READY_REFRESH: {
                mTvTitle.setText("松开刷新");
                break;
            }
            case STATUS_REFRESH: {
                mTvTitle.setText("数据加载中...");
                break;
            }
        }
    }

    @Override
    public void setScrolling(float scrollY, float maxScrollY) {
        super.setScrolling(scrollY, maxScrollY);
        mProgressBar.setMax((int) maxScrollY);
        mProgressBar.setProgress((int) scrollY);
    }

    @Override
    public int getContentHeight() {
        return UIUtils.dip2px(50);
    }
}
