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

import android.app.Activity;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.ArrayList;
import java.util.Arrays;

import app.com.arresto.arresto_connect.third_party.showcase_library.Add_Showcase;
import app.com.arresto.arresto_connect.ui.activity.BaseActivity;
import app.com.arresto.arresto_connect.ui.activity.HomeActivity;


public class LoadFragment {

    public static void add(Fragment fragment, BaseActivity activity, String tag) {
        activity.headerTv.setText(tag);
        activity.video_btn.setVisibility(View.GONE);
        FragmentManager fM = activity.getSupportFragmentManager();
//        Log.e("count ", " is " + fM.getBackStackEntryCount());
        fM.beginTransaction().add(activity.fragContainer, fragment, tag).addToBackStack(tag).commitAllowingStateLoss();
    }

    public static void replace(Fragment fragment, Activity activity1, String tag) {
        BaseActivity activity = (BaseActivity) activity1;
        FragmentManager fM = activity.getSupportFragmentManager();
        activity.headerTv.setText(tag);
        activity.video_btn.setVisibility(View.GONE);
        check_fragment_count(fM.getBackStackEntryCount(), activity);
        fM.beginTransaction().replace(activity.fragContainer, fragment, tag).addToBackStack(tag).commitAllowingStateLoss();
    }

    public static void backTo_fragment(BaseActivity activity,String tag) {
        activity.getSupportFragmentManager().popBackStack(tag, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    public static void Stack_replace(Fragment fragment, Activity activity1, String tag, String stack_name) {
        BaseActivity activity = (BaseActivity) activity1;
        FragmentManager fM = activity.getSupportFragmentManager();
        activity.headerTv.setText(tag);
        activity.video_btn.setVisibility(View.GONE);
        check_fragment_count(fM.getBackStackEntryCount(), activity);
        fM.beginTransaction().replace(activity.fragContainer, fragment, stack_name).addToBackStack(tag).commitAllowingStateLoss();
    }

    private static void show_showCase(BaseActivity activity) {
        ArrayList<String> mesages = new ArrayList<>(Arrays.asList(AppUtils.getResString("lbl_click_here_to_go_back")));
        ArrayList<View> views = new ArrayList<>(Arrays.asList(activity.back_button));
        Add_Showcase.getInstance(activity).setData(mesages, views);
    }

    private static void check_fragment_count(int count, BaseActivity activity) {
        if (count > 0) {
            activity.back_button.setVisibility(View.VISIBLE);
            activity.menuButton.setVisibility(View.GONE);
            show_showCase(activity);
        } else {
            activity.back_button.setVisibility(View.GONE);
            activity.menuButton.setVisibility(View.VISIBLE);
        }
    }
}
