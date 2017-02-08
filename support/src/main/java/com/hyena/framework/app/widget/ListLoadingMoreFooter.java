package com.hyena.framework.app.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hyena.framework.utils.UIUtils;

/**
 * Created by yangzc on 16/2/15.
 */
public class ListLoadingMoreFooter extends RelativeLayout {

    private ImageView mIvLoading;
    private TextView mTvLoading;

    public ListLoadingMoreFooter(Context context) {
        super(context);
        initView();
    }

    public ListLoadingMoreFooter(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public ListLoadingMoreFooter(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
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
