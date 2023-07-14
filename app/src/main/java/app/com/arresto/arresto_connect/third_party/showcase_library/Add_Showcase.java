package app.com.arresto.arresto_connect.third_party.showcase_library;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.view.View;

import java.util.ArrayList;

import static app.com.arresto.arresto_connect.constants.Static_values.isProfile;

public class Add_Showcase {
    Context context;
    ArrayList<String> messages;
    ArrayList<View> views;
    private SharedPreferences sharedPreferences;
    private static final String SHOWCASE_KEY = "showcase_key";

    public Add_Showcase(Context context) {
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences(SHOWCASE_KEY, 0);
    }

    public void setData(ArrayList<String> messages, ArrayList<View> views) {
        if (this.messages == null) {
            this.messages = messages;
            this.views = views;
        } else {
            this.messages.addAll(messages);
            this.views.addAll(views);
        }
        showStart();
    }

    public void showStart() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (guideView == null || !guideView.isShowing())
                    check_showcase();
            }
        }, 1000);
    }

    public GuideView guideView;

    public void check_showcase() {
        if (views.size() < 1 || isProfile) {
            this.messages.clear();
            this.views.clear();
            isProfile = false;
            return;
        }
        View targetView = views.get(0);

        if (targetView != null && targetView.getVisibility() == View.VISIBLE && !this.sharedPreferences.contains("" + targetView.getId())) {
            this.sharedPreferences.edit().putString("" + targetView.getId(), "" + targetView.getId()).apply();
            guideView = new GuideView.Builder(context)
//                .setTitle("Login")
                    .setContentText(messages.get(0))
                    .setTargetView(targetView)
                    .setContentTextSize(12) //optional
                    .setTitleTextSize(15)   //optional
                    .setDismissType(DismissType.outside) //Optional - default dismissible by TargetView
                    .setGuideListener(new GuideListener() {
                        @Override
                        public void onDismiss(View view) {
                            if (views.size() > 0) {
                                messages.remove(0);
                                views.remove(0);
                            }
                            if (views.size() > 0)
                                check_showcase();
                        }
                    })
                    .build();
            guideView.show();
        } else {
            messages.remove(0);
            views.remove(0);
            if (views.size() > 0)
                check_showcase();
        }
    }

    // object instance
    private static Add_Showcase showcase;

    public static Add_Showcase newInstance(Activity activity) {
        showcase = new Add_Showcase(activity);
        return showcase;
    }

    public static Add_Showcase getInstance(Activity activity) {
        if (showcase == null)
            showcase = new Add_Showcase(activity);
        return showcase;
    }


    public void clear_helpKey() {
        sharedPreferences.edit().clear().commit();
    }

}
