package com.hyena.framework.samples.animator.render.node;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.MotionEvent;

import com.hyena.framework.animation.Director;
import com.hyena.framework.animation.texture.CTexture;

/**
 * Created by yangzc on 16/4/24.
 */
public class StateTexture extends CTexture {

    private Bitmap mNormal;
    private Bitmap mPressed;
    public static StateTexture create(Director director, Bitmap normal, Bitmap pressed){
        return new StateTexture(director, normal, pressed);
    }

    protected StateTexture(Director director, Bitmap normal, Bitmap pressed) {
        super(director, normal);
        this.mNormal = normal;
        this.mPressed = pressed;
    }

    @Override
    public boolean onTouch(MotionEvent event) {
        return super.onTouch(event);
    }

    @Override
    public void render(Canvas canvas) {
        super.render(canvas);
    }

    @Override
    protected void onTouchDown() {
        super.onTouchDown();
        if (mPressed != null)
            setTexture(mPressed);
    }

    @Override
    protected void onTouchUp() {
        super.onTouchUp();
        setTexture(mNormal);
    }
}
