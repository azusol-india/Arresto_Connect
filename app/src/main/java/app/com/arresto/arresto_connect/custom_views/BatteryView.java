package app.com.arresto.arresto_connect.custom_views;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.CheckResult;
import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import org.jetbrains.annotations.NotNull;

import app.com.arresto.arresto_connect.R;
import kotlin.jvm.internal.Intrinsics;

import static android.graphics.Paint.ANTI_ALIAS_FLAG;

public class BatteryView extends View {

    public BatteryView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        backgroundRect = new Rect();
        batteryLevelRect = new Rect();
        batteryHeadRect = new Rect();
        backgroundPaint = new Paint(ANTI_ALIAS_FLAG);
        backgroundPaintStroke = new Paint(ANTI_ALIAS_FLAG);
        textValuePaint = new Paint(ANTI_ALIAS_FLAG);
        batteryHeadPaint = new Paint(ANTI_ALIAS_FLAG);
        batteryLevelPaint = new Paint(ANTI_ALIAS_FLAG);
        chargingLogoPaint = new Paint(ANTI_ALIAS_FLAG);
        batteryLevelColor = DEFAULT_BATTERY_LEVEL_COLOR;
        warningColor = DEFAULT_WARNING_COLOR;
        backgroundRectColor = DEFAULT_BACKGROUND_COLOR;
        batteryHeadColor = DEFAULT_BATTERY_HEAD_COLOR;
        chargingColor = DEFAULT_CHARGING_COLOR;
        textColor = DEFAULT_TEXT_COLOR;
        parseAttr(attrs);

        batteryLevelPaint.setStyle(Paint.Style.FILL);
        batteryLevelPaint.setColor(batteryLevelColor);

        backgroundPaint.setStyle(Paint.Style.FILL);
        backgroundPaint.setColor(backgroundRectColor);

        backgroundPaintStroke.setStyle(Paint.Style.STROKE);
        backgroundPaintStroke.setStrokeWidth(2.0F);
        backgroundPaintStroke.setColor(Color.BLACK);

        batteryHeadPaint.setStyle(Paint.Style.FILL);
        batteryHeadPaint.setColor(batteryHeadColor);

        chargingLogoPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        chargingLogoPaint.setColor(chargingColor);
        chargingLogoPaint.setStrokeWidth(8.0F);

        textValuePaint.setTextAlign(Paint.Align.CENTER);
        textValuePaint.setColor(textColor);
    }

    public BatteryView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        backgroundRect = new Rect();
        batteryLevelRect = new Rect();
        batteryHeadRect = new Rect();
        backgroundPaint = new Paint(ANTI_ALIAS_FLAG);
        backgroundPaintStroke = new Paint(ANTI_ALIAS_FLAG);
        textValuePaint = new Paint(ANTI_ALIAS_FLAG);
        batteryHeadPaint = new Paint(ANTI_ALIAS_FLAG);
        batteryLevelPaint = new Paint(ANTI_ALIAS_FLAG);
        chargingLogoPaint = new Paint(ANTI_ALIAS_FLAG);
        batteryLevelColor = DEFAULT_BATTERY_LEVEL_COLOR;
        warningColor = DEFAULT_WARNING_COLOR;
        backgroundRectColor = DEFAULT_BACKGROUND_COLOR;
        batteryHeadColor = DEFAULT_BATTERY_HEAD_COLOR;
        chargingColor = DEFAULT_CHARGING_COLOR;
        textColor = DEFAULT_TEXT_COLOR;
        parseAttr(attrs);

        batteryLevelPaint.setStyle(Paint.Style.FILL);
        batteryLevelPaint.setColor(batteryLevelColor);

        backgroundPaint.setStyle(Paint.Style.FILL);
        backgroundPaint.setColor(backgroundRectColor);

        backgroundPaintStroke.setStyle(Paint.Style.STROKE);
        backgroundPaintStroke.setStrokeWidth(2.0F);
        backgroundPaintStroke.setColor(Color.BLACK);

        batteryHeadPaint.setStyle(Paint.Style.FILL);
        batteryHeadPaint.setColor(batteryHeadColor);

        chargingLogoPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        chargingLogoPaint.setColor(chargingColor);
        chargingLogoPaint.setStrokeWidth(8.0F);

        textValuePaint.setTextAlign(Paint.Align.CENTER);
        textValuePaint.setColor(textColor);
    }

    private int batteryHeadWidth;
    private int contentHeight;
    private int contentWidth;
    private int mainContentOffset=3;
    private final Rect backgroundRect;
    private final Rect batteryLevelRect;
    private final Rect batteryHeadRect;
    private final Paint backgroundPaint;
    private final Paint backgroundPaintStroke;
    private final Paint textValuePaint;
    private final Paint batteryHeadPaint;
    private final Paint batteryLevelPaint;
    private final Paint chargingLogoPaint;
    private int batteryLevelColor;
    private int warningColor;
    private int backgroundRectColor;
    private int batteryHeadColor;
    private int chargingColor;
    private int textColor;
    private boolean isCharging;
    private int batteryLevel= 40;
    private int warningLevel = 20;
    private static final boolean DEFAULT_CHARGING_STATE = false;
    private static final int DEFAULT_BATTERY_LEVEL = 40;
    private static final int DEFAULT_WARNING_LEVEL = 20;
    private static final int DEFAULT_BATTERY_LEVEL_COLOR = Color.GREEN;
    private static final int DEFAULT_WARNING_COLOR = Color.RED;
    private static final int DEFAULT_BACKGROUND_COLOR = Color.TRANSPARENT;
    private static final int DEFAULT_BATTERY_HEAD_COLOR = Color.DKGRAY;
    private static final int DEFAULT_TEXT_COLOR = Color.DKGRAY;
    private static final int DEFAULT_CHARGING_COLOR = Color.DKGRAY;
    private static final float TEXT_SIZE_RATIO = 0.5F;

    public final int getBatteryLevelColor() {
        return batteryLevelColor;
    }

    public final void setBatteryLevelColor(@ColorInt int color) {
        batteryLevelColor = color;
        batteryLevelPaint.setColor(color);
        invalidate();
    }

    public final int getWarningColor() {
        return warningColor;
    }

    public final void setWarningColor(@ColorInt int color) {
        warningColor = color;
        batteryLevelPaint.setColor(color);
        invalidate();
    }

    public final int getBackgroundRectColor() {
        return backgroundRectColor;
    }

    public final void setBackgroundRectColor(@ColorInt int color) {
        backgroundRectColor = color;
        backgroundPaint.setColor(color);
        invalidate();
    }

    public final int getBatteryHeadColor() {
        return batteryHeadColor;
    }

    public final void setBatteryHeadColor(@ColorInt int color) {
        batteryHeadColor = color;
        batteryHeadPaint.setColor(color);
        invalidate();
    }

    public final int getChargingColor() {
        return chargingColor;
    }

    public final void setChargingColor(@ColorInt int color) {
        chargingColor = color;
        chargingLogoPaint.setColor(color);
        invalidate();
    }

    public final int getTextColor() {
        return textColor;
    }

    public final void setTextColor(@ColorInt int color) {
        textColor = color;
        textValuePaint.setColor(color);
        invalidate();
    }

    @CheckResult
    public final boolean isCharging() {
        return isCharging;
    }

    public final void setCharging(boolean value) {
        isCharging = value;
        invalidate();
    }

    @CheckResult
    public final int getBatteryLevel() {
        return batteryLevel;
    }

    public final void setBatteryLevel(int level) {
        batteryLevel = level > 100 ? 100 : (level < 0 ? 0 : level);
        if (batteryLevel <= warningLevel) {
            batteryLevelPaint.setColor(warningColor);
        } else {
            batteryLevelPaint.setColor(batteryLevelColor);
        }
        invalidate();
    }

    public final int getWarningLevel() {
        return warningLevel;
    }

    public final void setWarningLevel(int var1) {
        warningLevel = var1;
    }

    private final void parseAttr(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.BatteryView, 0, 0);
        setCharging(typedArray.getBoolean(R.styleable.BatteryView_charging, DEFAULT_CHARGING_STATE));
        setBatteryLevel(typedArray.getInteger(R.styleable.BatteryView_battery_level, DEFAULT_BATTERY_LEVEL));
        warningLevel = typedArray.getInteger(R.styleable.BatteryView_warning_level, DEFAULT_WARNING_LEVEL);
        setBatteryLevelColor(typedArray.getColor(R.styleable.BatteryView_normal_fill_color, DEFAULT_BATTERY_LEVEL_COLOR));
        setBackgroundRectColor(typedArray.getColor(R.styleable.BatteryView_background_fill_color, DEFAULT_BACKGROUND_COLOR));
        setWarningColor(typedArray.getColor(R.styleable.BatteryView_warning_fill_color, DEFAULT_WARNING_COLOR));
        setBatteryHeadColor(typedArray.getColor(R.styleable.BatteryView_battery_head_color, DEFAULT_BATTERY_HEAD_COLOR));
        setChargingColor(typedArray.getColor(R.styleable.BatteryView_charging_color, DEFAULT_CHARGING_COLOR));
        setTextColor(typedArray.getColor(R.styleable.BatteryView_textcolor, DEFAULT_TEXT_COLOR));
        typedArray.recycle();
    }

    protected void onSizeChanged(int width, int height, int oldw, int oldh) {
        contentWidth = width - getPaddingLeft() - getPaddingRight();
        contentHeight = height - getPaddingTop() - getPaddingBottom();
        textValuePaint.setTextSize((float) contentHeight * 0.5F);
        batteryHeadWidth = (int) ((0.083333336F * (float) contentWidth) + 1);
        backgroundRect.set(15, 15, contentWidth - batteryHeadWidth - 5, contentHeight - 15);
        batteryHeadRect.set(backgroundRect.right + 2,
                backgroundRect.top + contentHeight / 9,
                backgroundRect.left + contentWidth - 25,
                (int) (backgroundRect.top + contentHeight * 3 / 5.5));
    }

    protected void onDraw(@NotNull Canvas canvas) {
        Intrinsics.checkParameterIsNotNull(canvas, "canvas");
        drawBackground(canvas);
        drawBatteryHead(canvas);
        drawBatteryLevel(canvas);
        if (isCharging()) {
            drawChargingLogo(canvas);
        } else {
            drawCurrentBatteryValueText(canvas);
        }

    }

    private final void drawBackground(Canvas canvas) {
        canvas.drawRect(backgroundRect, backgroundPaint);
        canvas.drawRoundRect(new RectF(backgroundRect), 10.0F, 10.0F, backgroundPaintStroke);
    }

    private final void drawBatteryHead(Canvas canvas) {
        canvas.drawRoundRect(new RectF(batteryHeadRect), 5.0F, 5.0F, batteryHeadPaint);
    }

    private final void drawBatteryLevel(Canvas canvas) {
        if (getBatteryLevel() <= warningLevel) {
            batteryLevelPaint.setColor(warningColor);
        } else {
            batteryLevelPaint.setColor(batteryLevelColor);
        }

        if (getBatteryLevel() == 0) {
            drawBatteryEmptyStatus(canvas);
        } else {
            batteryLevelRect.set(backgroundRect.left + mainContentOffset,
                backgroundRect.top + mainContentOffset,
                (backgroundRect.right - mainContentOffset) * getBatteryLevel() / 100,
                backgroundRect.bottom - mainContentOffset);
            canvas.drawRoundRect(new RectF(batteryLevelRect), 10.0F, 10.0F, batteryLevelPaint);
        }

    }

    private final void drawChargingLogo(Canvas canvas) {
        VectorDrawableCompat var7 = VectorDrawableCompat.create(getContext().getResources(), R.drawable.ic_charging, (Resources.Theme) null);
        if (var7 != null) {
            var7.setBounds(backgroundRect.left + contentWidth / 4, backgroundRect.top + contentHeight / 4, backgroundRect.right - contentWidth / 4, backgroundRect.bottom - contentHeight / 4);
            var7.setColorFilter(chargingColor, PorterDuff.Mode.SRC_IN);
            var7.draw(canvas);
        }
    }

    private final void drawCurrentBatteryValueText(Canvas canvas) {
        String text = getBatteryLevel() == 0 ? "Empty" : String.valueOf(getBatteryLevel());
        canvas.drawText(text, (float) ((double) contentWidth * 0.45D), (float) ((double) contentHeight * 0.7D), textValuePaint);
    }

    private final void drawBatteryEmptyStatus(Canvas canvas) {
        canvas.drawText("Empty", (float) ((double) contentWidth * 0.45D), (float) ((double) contentHeight * 0.7D), textValuePaint);
        canvas.drawRoundRect(new RectF(backgroundRect), 10.0F, 10.0F, backgroundPaint);
        canvas.drawRoundRect(new RectF(backgroundRect), 10.0F, 10.0F, backgroundPaintStroke);
    }


}
