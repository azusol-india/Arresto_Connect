/*
 *  Copyright (c)
 *  @website: http://arresto.in/
 *  @author: Arresto Solutions Pvt. Ltd.
 *  @license: http://arresto.in/
 *
 *  The below module/code/specifications belong to Arresto Solutions Pvt. Ltd. solely.
 */

/*
 *  *The below module/code/specifications belong to Arresto Solutions Pvt. Ltd. solely.
 */

package app.com.arresto.arresto_connect.constants;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class OverlayImageView extends ImageView {
    Overlay overlay = new Overlay(null);

    public OverlayImageView(Context context) {
        super(context);
    }

    public OverlayImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        overlay.draw(canvas);
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        overlay.setDrawableState(getDrawableState());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        overlay.setDrawableBounds(getMeasuredWidth(), getMeasuredHeight());
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);
        overlay.setDrawableBounds(width, height);
    }

    @Override
    public void invalidateDrawable(Drawable drawable) {
        if (drawable == overlay.drawable) {
            invalidate();
        } else {
            super.invalidateDrawable(drawable);
        }
    }

    /*
     * Sets the drawable to be drawn on top the ImageView content.
     *
     * @param drawable The drawable
     */
    public void setOverlayDrawable(Drawable drawable) {
        if (drawable != overlay.drawable) {
            overlay.cleanupDrawable(this);
            if (drawable != null) {
                drawable.setCallback(this);
            }

            overlay = new Overlay(drawable);
            overlay.setDrawableState(getDrawableState());
            requestLayout();
        }
    }

    /**
     * Takes a {@link Drawable} and draws it on top the ImageView content.
     * The overlay drawable will respect the view's current state so a selector can be passed in.
     */
    static protected class Overlay {
        final Drawable drawable;

        Overlay(Drawable drawable) {
            this.drawable = drawable;
        }

        protected void cleanupDrawable(ImageView imageView) {
            if (drawable != null) {
                drawable.setCallback(null);
                imageView.unscheduleDrawable(drawable);
            }
        }

        protected void setDrawableBounds(int width, int height) {
            if (drawable != null) {
                drawable.setBounds(0, 0, width, height);
            }
        }

        protected void setDrawableState(int[] state) {
            if (drawable != null && drawable.isStateful()) {
                drawable.setState(state);
            }
        }

        protected void draw(Canvas canvas) {
            if (drawable != null) {
                drawable.draw(canvas);
            }
        }
    }
}
