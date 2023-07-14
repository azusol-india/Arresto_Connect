package app.com.arresto.arresto_connect.third_party.showcase_library;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Build.VERSION;
import android.util.AttributeSet;
import android.view.View;
import java.util.ArrayList;
import java.util.List;

public class ShowcaseView extends View {
    static final int DEFAULT_ALPHA_COLOR = 200;
    int backgroundOverlayColor = Color.argb(200, 0, 0, 0);
    List<Shape> shapes;

    public ShowcaseView(Context context) {
        super(context);
        initialize();
    }

    public ShowcaseView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public ShowcaseView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    public void addCircle(Circle circle) {
        this.shapes.add(circle);
    }

    public void addRoundRect(RoundRect roundRect) {
        this.shapes.add(roundRect);
    }

    public int getBackgroundOverlayColor() {
        return this.backgroundOverlayColor;
    }

    public void setBackgroundOverlayColor(int backgroundOverlayColor2) {
        this.backgroundOverlayColor = backgroundOverlayColor2;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(this.backgroundOverlayColor);
        for (Shape shape : this.shapes) {
            shape.drawOn(canvas);
        }
    }

    private void initialize() {
        this.shapes = new ArrayList();
        setDrawingCacheEnabled(true);
        if (VERSION.SDK_INT >= 11) {
            setLayerType(1, null);
        }
    }
}
