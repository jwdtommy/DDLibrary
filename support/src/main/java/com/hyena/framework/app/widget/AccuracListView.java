package com.hyena.framework.app.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class AccuracListView extends ListView {

	public AccuracListView(Context context) {
		super(context);
	}

	public AccuracListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public AccuracListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
				MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}

}
