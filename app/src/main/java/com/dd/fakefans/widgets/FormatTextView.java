package com.dd.fakefans.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by J.Tommy on 16/9/14.
 */
public class FormatTextView extends TextView {
    public FormatTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(formatString((String)text), type);
    }


    private String formatString(String str)
    {
        if(str==null)
        {
            return "";
        }
        return str.replaceAll("\n","");
    }
}
