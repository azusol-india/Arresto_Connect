package app.com.arresto.arresto_connect.third_party.showcase_library;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Build.VERSION;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewParent;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.Window;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.ColorInt;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.core.view.ViewPropertyAnimatorListenerAdapter;

import app.com.arresto.arresto_connect.R;

public class Showcase {
    public static final float DEFAULT_ADDITIONAL_RADIUS_RATIO = 1.5f;
    private static final String SHARED_TUTO = "showcase_key";
    /* access modifiers changed from: private */
    public FrameLayout container;
    private boolean fitsSystemWindows = false;
    /* access modifiers changed from: private */
    public Listener listener;
    private SharedPreferences sharedPreferences;
    /* access modifiers changed from: private */
    public ShowcaseView showView;

    public static class ActionViewActionsEditor extends ViewActionsEditor {
        public ActionViewActionsEditor(ViewActions viewActions) {
            super(viewActions);
        }

        public ActionViewActionsEditor delayed(int delay) {
            this.viewActions.settings.delay = Integer.valueOf(delay);
            return this;
        }

        public ActionViewActionsEditor duration(int duration) {
            this.viewActions.settings.duration = Integer.valueOf(duration);
            return this;
        }

        public ActionViewActionsEditor animated(boolean animated) {
            this.viewActions.settings.animated = animated;
            return this;
        }
    }

    public interface Listener {
        void onDismissed();
    }

    public static class ShapeViewActionsEditor extends ViewActionsEditor {
        public ShapeViewActionsEditor(ViewActions viewActions) {
            super(viewActions);
        }

        public ShapeViewActionsEditor withBorder() {
            this.viewActions.settings.withBorder = true;
            return this;
        }

        public ShapeViewActionsEditor onClick(OnClickListener onClickListener) {
            this.viewActions.settings.onClickListener = onClickListener;
            return this;
        }
    }

    public static class ViewActions {
        private final boolean fitsSystemWindow;
        /* access modifiers changed from: private */
        public final ViewActionsSettings settings = new ViewActionsSettings();
        private final Showcase showcase;
        /* access modifiers changed from: private */
        public final View view;

        public ViewActions(Showcase showcase2, View view2, boolean fitsSystemWindow2) {
            this.showcase = showcase2;
            this.view = view2;
            this.fitsSystemWindow = fitsSystemWindow2;
        }

        public ViewActions on(@IdRes int viewId) {
            return this.showcase.on(viewId);
        }

        public ViewActions on(View view2) {
            return this.showcase.on(view2);
        }

        public Showcase show() {
            return this.showcase.show();
        }

        /* access modifiers changed from: private */
        public void displaySwipable(final boolean left) {
            final Rect rect = new Rect();
            this.view.getGlobalVisibleRect(rect);
            final ImageView hand = new ImageView(this.view.getContext());
            if (left) {
                hand.setImageResource(R.drawable.finger_moving_left);
            } else {
                hand.setImageResource(R.drawable.finger_moving_right);
            }
            hand.setLayoutParams(new MarginLayoutParams(-2, -2));
            hand.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
                public boolean onPreDraw() {
                    float tX;
                    int x = (int) (((float) rect.centerX()) - (((float) hand.getWidth()) / 2.0f));
                    ViewCompat.setTranslationY(hand, (float) ((int) (((float) rect.centerY()) - (((float) hand.getHeight()) / 2.0f))));
                    ViewCompat.setTranslationX(hand, (float) x);
                    if (ViewActions.this.settings.animated) {
                        if (left) {
                            tX = (float) rect.left;
                        } else {
                            tX = ((float) rect.left) + (((float) rect.width()) * 0.7f);
                        }
                        ViewCompat.animate(hand).translationX(tX).setStartDelay(ViewActions.this.settings.delay != null ? (long) ViewActions.this.settings.delay.intValue() : 500).setDuration(ViewActions.this.settings.duration != null ? (long) ViewActions.this.settings.duration.intValue() : 600).setInterpolator(new DecelerateInterpolator());
                    }
                    hand.getViewTreeObserver().removeOnPreDrawListener(this);
                    return false;
                }
            });
            this.showcase.container.addView(hand);
            this.showcase.container.invalidate();
        }

        public ActionViewActionsEditor displaySwipableLeft() {
            this.view.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
                public boolean onPreDraw() {
                    ViewActions.this.displaySwipable(true);
                    ViewActions.this.view.getViewTreeObserver().removeOnPreDrawListener(this);
                    return false;
                }
            });
            return new ActionViewActionsEditor(this);
        }

        public ActionViewActionsEditor displaySwipableLeftRight() {
            this.view.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
                public boolean onPreDraw() {
                    ViewActions.this.displaySwipable(true);
                    ViewActions.this.displaySwipable(false);
                    ViewActions.this.view.getViewTreeObserver().removeOnPreDrawListener(this);
                    return false;
                }
            });
            return new ActionViewActionsEditor(this);
        }

        public ActionViewActionsEditor displaySwipableRight() {
            this.view.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
                public boolean onPreDraw() {
                    ViewActions.this.displaySwipable(false);
                    ViewActions.this.view.getViewTreeObserver().removeOnPreDrawListener(this);
                    return false;
                }
            });
            return new ActionViewActionsEditor(this);
        }

        public ActionViewActionsEditor displayScrollable() {
            this.view.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
                public boolean onPreDraw() {
                    ViewActions.this.displayScrollableOnView();
                    ViewActions.this.view.getViewTreeObserver().removeOnPreDrawListener(this);
                    return false;
                }
            });
            return new ActionViewActionsEditor(this);
        }

        /* access modifiers changed from: private */
        public void displayScrollableOnView() {
            final Rect rect = new Rect();
            this.view.getGlobalVisibleRect(rect);
            final int height = rect.height();
            final ImageView hand = new ImageView(this.view.getContext());
            hand.setImageResource(R.drawable.finger_moving_down);
            hand.setLayoutParams(new MarginLayoutParams(-2, -2));
            hand.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
                public boolean onPreDraw() {
                    int x = (int) (((float) rect.centerX()) - (((float) hand.getWidth()) / 2.0f));
                    int y = ((int) (((float) rect.centerY()) - (((float) hand.getHeight()) / 2.0f))) - ViewActions.this.getStatusBarOffset();
                    ViewCompat.setTranslationY(hand, (float) y);
                    ViewCompat.setTranslationX(hand, (float) x);
                    if (ViewActions.this.settings.animated) {
                        ViewCompat.animate(hand).translationY((((float) y) + (((float) height) * 0.8f)) - ((float) ViewActions.this.getStatusBarOffset())).setStartDelay(ViewActions.this.settings.delay != null ? (long) ViewActions.this.settings.delay.intValue() : 500).setDuration(ViewActions.this.settings.duration != null ? (long) ViewActions.this.settings.duration.intValue() : 600).setInterpolator(new DecelerateInterpolator());
                    }
                    hand.getViewTreeObserver().removeOnPreDrawListener(this);
                    return false;
                }
            });
            this.showcase.container.addView(hand);
            this.showcase.container.invalidate();
        }

        /* access modifiers changed from: private */
        public void addCircleOnView(float additionalRadiusRatio) {
            Rect rect = new Rect();
            this.view.getGlobalVisibleRect(rect);
            Circle circle = new Circle(rect.centerX(), rect.centerY(), (int) ((((float) Math.max(rect.width(), rect.height())) / 2.0f) * additionalRadiusRatio));
            circle.setDisplayBorder(this.settings.withBorder);
            this.showcase.showView.addCircle(circle);
            addClickableView(rect, this.settings.onClickListener, additionalRadiusRatio);
            this.showcase.showView.postInvalidate();
        }

        public ShapeViewActionsEditor addRoundRect() {
            return addRoundRect(1.5f);
        }

        public ShapeViewActionsEditor addRoundRect(final float additionalRadiusRatio) {
            this.view.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
                public boolean onPreDraw() {
                    ViewActions.this.addRoundRectOnView(additionalRadiusRatio);
                    ViewActions.this.view.getViewTreeObserver().removeOnPreDrawListener(this);
                    return false;
                }
            });
            return new ShapeViewActionsEditor(this);
        }

        public ShapeViewActionsEditor addCircle() {
            return addCircle(1.5f);
        }

        public ShapeViewActionsEditor addCircle(final float additionalRadiusRatio) {
            if(this.view!=null)
            this.view.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
                public boolean onPreDraw() {
                    ViewActions.this.addCircleOnView(additionalRadiusRatio);
                    ViewActions.this.view.getViewTreeObserver().removeOnPreDrawListener(this);
                    return false;
                }
            });
            return new ShapeViewActionsEditor(this);
        }

        /* access modifiers changed from: private */
        public void addRoundRectOnView(float additionalRadiusRatio) {
            Rect rect = new Rect();
            this.view.getGlobalVisibleRect(rect);
            RoundRect roundRect = new RoundRect(rect.left - 40, (rect.top - getStatusBarOffset()) - 40, rect.width() + (40 * 2), rect.height() + (40 * 2));
            roundRect.setDisplayBorder(this.settings.withBorder);
            this.showcase.showView.addRoundRect(roundRect);
            addClickableView(rect, this.settings.onClickListener, additionalRadiusRatio);
            this.showcase.showView.postInvalidate();
        }

        /* access modifiers changed from: private */
        public int getStatusBarOffset() {
            if (this.fitsSystemWindow) {
                return 0;
            }
            Resources resources = this.view.getContext().getResources();
            int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                return resources.getDimensionPixelSize(resourceId);
            }
            return 0;
        }

        private void addClickableView(Rect rect, OnClickListener onClickListener, float additionalRadiusRatio) {
            View cliclableView = new View(this.view.getContext());
            int width = (int) (((float) rect.width()) * additionalRadiusRatio);
            int height = (int) (((float) rect.height()) * additionalRadiusRatio);
            int x = rect.left - ((width - rect.width()) / 2);
            int y = (rect.top - ((height - rect.height()) / 2)) - getStatusBarOffset();
            cliclableView.setLayoutParams(new MarginLayoutParams(width, height));
            ViewCompat.setTranslationY(cliclableView, (float) y);
            ViewCompat.setTranslationX(cliclableView, (float) x);
            cliclableView.setOnClickListener(onClickListener);
            this.showcase.container.addView(cliclableView);
            this.showcase.container.invalidate();
        }

        public Showcase showOnce(String key) {
            return this.showcase.showOnce(key);
        }

        public Showcase onClickContentView(@IdRes int viewId, OnClickListener onClickListener) {
            return this.showcase.onClickContentView(viewId, onClickListener);
        }
    }

    public static class ViewActionsEditor {
        protected final ViewActions viewActions;

        public ViewActionsEditor(ViewActions viewActions2) {
            this.viewActions = viewActions2;
        }

        public ViewActions on(@IdRes int viewId) {
            return this.viewActions.on(viewId);
        }

        public ViewActions on(View view) {
            return this.viewActions.on(view);
        }

        public Showcase show() {
            return this.viewActions.show();
        }

        public Showcase showOnce(String key) {
            return this.viewActions.showOnce(key);
        }

        public Showcase onClickContentView(@IdRes int viewId, OnClickListener onClickListener) {
            return this.viewActions.onClickContentView(viewId, onClickListener);
        }
    }

    private static class ViewActionsSettings {
        /* access modifiers changed from: private */
        public boolean animated;
        /* access modifiers changed from: private */
        public Integer delay;
        /* access modifiers changed from: private */
        public Integer duration;
        /* access modifiers changed from: private */
        @Nullable
        public OnClickListener onClickListener;
        /* access modifiers changed from: private */
        public boolean withBorder;

        private ViewActionsSettings() {
            this.animated = true;
            this.withBorder = false;
            this.delay = Integer.valueOf(0);
            this.duration = Integer.valueOf(300);
        }
    }

    private Showcase(@NonNull Activity activity) {
        boolean z = false;
        this.sharedPreferences = activity.getSharedPreferences(SHARED_TUTO, 0);
        this.container = new FrameLayout(activity);
        this.showView = new ShowcaseView(activity);
        Window window = activity.getWindow();
        if (window != null) {
            ViewGroup decorView = (ViewGroup) window.getDecorView();
            if (decorView != null) {
                ViewGroup content =  decorView.findViewById(16908290);
                if (content != null) {
                    content.addView(this.container, -1, -1);
                    this.container.addView(this.showView, -1, -1);
                    if (VERSION.SDK_INT >= 16) {
                        View inflatedLayout = content.getChildAt(0);
                        if (inflatedLayout != null) {
                            z = inflatedLayout.getFitsSystemWindows();
                        }
                        this.fitsSystemWindows = z;
                    }
                }
            }
        }
        this.container.setVisibility(8);
        ViewCompat.setAlpha(this.container, 0.0f);
    }

    @NonNull
    public static Showcase from(@NonNull Activity activity) {
        return new Showcase(activity);
    }

    public Showcase setBackgroundColor(@ColorInt int color) {
        this.showView.setBackgroundOverlayColor(color);
        return this;
    }

    public Showcase setFitsSystemWindows(boolean fitsSystemWindows2) {
        this.fitsSystemWindows = fitsSystemWindows2;
        return this;
    }

    public Showcase setListener(Listener listener2) {
        this.listener = listener2;
        return this;
    }

    public Showcase setContentView(@LayoutRes int content) {
        this.container.addView(LayoutInflater.from(this.showView.getContext()).inflate(content, this.container, false), -1, -1);
        return this;
    }

    public void dismiss() {
        ViewCompat.animate(this.container).alpha(0.0f).setDuration(this.container.getResources().getInteger(17694721)).setListener(new ViewPropertyAnimatorListenerAdapter() {
            public void onAnimationEnd(View view) {
                super.onAnimationEnd(view);
                ViewParent parent = view.getParent();
                if (parent instanceof ViewGroup) {
                    ((ViewGroup) parent).removeView(view);
                }
                if (Showcase.this.listener != null) {
                    Showcase.this.listener.onDismissed();
                }
            }
        }).start();
    }

    public Showcase withDismissView(@IdRes int viewId) {
        View view = this.container.findViewById(viewId);
        if (view != null) {
            view.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    Showcase.this.dismiss();
                }
            });
        }
        return this;
    }

    public Showcase onClickContentView(int id, OnClickListener onClickListener) {
        View view = this.container.findViewById(id);
        if (view != null) {
            view.setOnClickListener(onClickListener);
        }
        return this;
    }

    public Showcase show() {
        this.container.setVisibility(0);
        ViewCompat.animate(this.container).alpha(1.0f).setDuration((long) this.container.getResources().getInteger(17694722)).start();
        this.container.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Showcase.this.dismiss();
            }
        });
        return this;
    }

    public Showcase showOnce(String key) {
        if (!this.sharedPreferences.contains(key)) {
            show();
            this.sharedPreferences.edit().putString(key, key).apply();
        }
        return this;
    }

    public Showcase resetTutorial(String key) {
        this.sharedPreferences.edit().remove(key).apply();
        return this;
    }

    @Nullable
    private View findViewById(@IdRes int viewId) {
        Context context = this.showView.getContext();
        if (context instanceof Activity) {
            return ((Activity) context).findViewById(viewId);
        }
        return null;
    }

    public ViewActions on(@IdRes int viewId) {
        return new ViewActions(this, findViewById(viewId), this.fitsSystemWindows);
    }

    public ViewActions on(View view) {
        return new ViewActions(this, view, this.fitsSystemWindows);
    }
}
