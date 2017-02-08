package com.hyena.framework.app.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hyena.framework.clientlog.LogUtil;
import com.hyena.framework.utils.UIUtils;

import java.util.Date;

/**
 * Created by yangzc on 16/10/28.
 */
public class CommonRefreshableFooter extends AbsRefreshablePanel {

    private static final String TAG = "CommonRefreshableHeader";

    private ImageView mIvLoading;
    private TextView mTvLoading;

    public CommonRefreshableFooter(Context context) {
        super(context);
        initView();
    }

    public CommonRefreshableFooter(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public CommonRefreshableFooter(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

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
                mTvLoading.setText("上拉获取更多");
                break;
            }
            case STATUS_START_PULL: {
                mTvLoading.setText("上拉获取更多");
                break;
            }
            case STATUS_READY_REFRESH: {
                mTvLoading.setText("松开获取更多");
                break;
            }
            case STATUS_REFRESH: {
                mTvLoading.setText("数据加载中...");
                break;
            }
        }
    }

    @Override
    public int getContentHeight() {
        return UIUtils.dip2px(50);
    }

    private void initView(){
        LinearLayout ll = new LinearLayout(getContext());
        ll.setOrientation(LinearLayout.HORIZONTAL);

        mIvLoading = new ImageView(getContext());
        int ivSize = UIUtils.dip2px(26);
        int ivPadding = UIUtils.dip2px(3);
        mIvLoading.setPadding(ivPadding, ivPadding, ivPadding, ivPadding);
        LinearLayout.LayoutParams ivParams = new LinearLayout.LayoutParams(ivSize, ivSize);
        ivParams.gravity = Gravity.CENTER_VERTICAL;
        ll.addView(mIvLoading, ivParams);

        mTvLoading = new TextView(getContext());
        mTvLoading.setTextColor(0xff4D4D4D);
        mTvLoading.setTextSize(14);
        LinearLayout.LayoutParams tvParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        tvParams.leftMargin = UIUtils.dip2px(10);
        ivParams.gravity = Gravity.CENTER_VERTICAL;
        ll.addView(mTvLoading, tvParams);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        int padding = UIUtils.dip2px(10);
        ll.setPadding(padding, padding, padding, padding);
        ll.setGravity(Gravity.CENTER);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        addView(ll, params);
    }

    public void setTextColor(int color) {
        mTvLoading.setTextColor(color);
    }

    public void setTextSize(int size) {
        mTvLoading.setTextSize(size);
    }

    public void setText(String text){
        mTvLoading.setText(text);
    }

    public TextView getTextView(){
        return mTvLoading;
    }

    public void setImageResourceId(int resId){
        mIvLoading.setImageResource(resId);
    }

    public ImageView getImageView() {
        return mIvLoading;
    }
}
