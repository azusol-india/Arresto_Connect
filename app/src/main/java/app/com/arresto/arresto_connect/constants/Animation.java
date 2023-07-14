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


import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.view.View;

public class Animation implements Animator.AnimatorListener {
   //SettingOpen
    private ObjectAnimator animator;
    private boolean flag;
    public static boolean open;
    private View view;
    private AnimationStatus listener;

    public Animation(View view, AnimationStatus listener) {
        this.view = view;
        this.listener = listener;
    }
    public Animation(AnimationStatus listener) {
        this.listener = listener;
    }

    public boolean isOpen() {
        return open;
    }

    public boolean isRunning() {
        if(animator != null && animator.isRunning())
            return true;
            return false;
    }

    public void openDrawer(Object slideView, String propertyName, float value,Object slideView2,String propertyName2, float value2) {
        if (isRunning())
            return;
        open = true;
        this.flag = false;
        animator = ObjectAnimator.ofFloat(slideView, propertyName, value);
        start();
        animator = ObjectAnimator.ofFloat(slideView2, propertyName2, value2);
        start();
    }
    public void openDrawer(Object slideView, String propertyName, float value) {
        if (isRunning())
            return;
        open = true;
        this.flag = false;
        animator = ObjectAnimator.ofFloat(slideView, propertyName, value);
        start();
    }

    public void closeDrawer(Object slideView, String propertyName, float value,Object slideView2, String propertyName2, float value2) {
        closeDrawer(slideView,propertyName,value,slideView2,propertyName2,value2,false);

    }
    public void closeDrawer(Object slideView, String propertyName, float value,Object slideView2, String propertyName2, float value2,boolean flag) {
        if (isRunning())
            return;
        open = false;
        this.flag = flag;
        animator = ObjectAnimator.ofFloat(slideView, propertyName, value);
        start();
        animator = ObjectAnimator.ofFloat(slideView2, propertyName2, value2);
        start();
    }
    public void closeDrawer(Object slideView, String propertyName, float value) {
        closeDrawer(slideView,propertyName,value,false);

    }
    public void move(Object slideView, String propertyName, float value,long duration) {
//        if (isRunning())
//            return;
        this.flag = true;
        animator = ObjectAnimator.ofFloat(slideView, propertyName, value);
        animator.setDuration(duration);
        animator.addListener(this);
        animator.start();
    }
    public void closeDrawer(Object slideView, String propertyName, float value,boolean flag) {
        if (isRunning())
            return;
        open = false;
        this.flag = flag;
        animator = ObjectAnimator.ofFloat(slideView, propertyName, value);
        start();

    }

    public void start() {
        animator.setDuration(400);
        animator.addListener(this);
        animator.start();
    }

    @Override
    public void onAnimationStart(Animator animation) {
        if(open&&view!=null)
            view.setVisibility(View.VISIBLE);
    }

    @Override
    public void onAnimationEnd(Animator animation) {
        if(!open&&view!=null)
            view.setVisibility(View.VISIBLE);
        if(flag && listener!=null)
            listener.animationEnd();
    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }

    public interface AnimationStatus{
        void animationEnd();
    }
}
