package fakefans.dd.com.fakefans.fresco;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.GenericDraweeView;

/**
 * Created by adong.
 * 基于Fresco的图片加载View
 */
public class FrescoImageView extends GenericDraweeView {
    public FrescoImageView(Context context, GenericDraweeHierarchy hierarchy) {
        super(context, hierarchy);
    }

    public FrescoImageView(Context context) {
        super(context,new GenericDraweeHierarchyBuilder(context.getResources()).build());
    }

    public FrescoImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FrescoImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public FrescoImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
    
    public void setForeground(Drawable drawable){
    	if(drawable != null){
    		getHierarchy().setControllerOverlay(drawable);
    	}
    }
    
    public View getCurrentView()
    {
    	return this;
    }
}
