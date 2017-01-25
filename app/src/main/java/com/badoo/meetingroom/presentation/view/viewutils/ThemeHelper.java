package com.badoo.meetingroom.presentation.view.viewutils;

import android.app.Activity;
import android.content.Intent;

import com.badoo.meetingroom.R;

/**
 * Created by zhangyaozhong on 24/01/2017.
 */

public class ThemeHelper {

    private static int mTheme;

    public final static int THEME_DEFAULT = 0;
    public final static int THEME_DARK = 1;

    /**
     * Set the theme of the Activity, and restart it by creating a new Activity of the same type.
     */
    public static void changeToTheme(Activity activity, int theme)
    {
        mTheme = theme;
        activity.finish();
        activity.overridePendingTransition(0, 0);
        activity.startActivity(new Intent(activity, activity.getClass()).setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
    }

    /** Set the theme of the activity, according to the configuration. */
    public static void onActivityCreateSetTheme(Activity activity)
    {
        switch (mTheme)
        {
            default:
            case THEME_DEFAULT:
                activity.setTheme(R.style.AppTheme_Light);
                break;
            case THEME_DARK:
                activity.setTheme(R.style.AppTheme_Dark);
                break;
        }
    }

    public static int getCurrentTheme() {
        return mTheme;
    }
}
