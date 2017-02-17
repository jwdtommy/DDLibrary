package com.dd.framework.widgets;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import com.dd.framework.base.CustomFragment;

/**
 * Created by J.Tommy on 17/2/14.
 */
public  class TopView extends RelativeLayout{
	private CustomFragment mCustomFragment;


	public TopView(Context context) {
		super(context);
	}

	public TopView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setCustomFragment(CustomFragment customFragment) {
		mCustomFragment = customFragment;
	}

}
