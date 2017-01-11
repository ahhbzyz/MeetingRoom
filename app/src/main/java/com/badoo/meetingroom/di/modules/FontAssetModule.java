package com.badoo.meetingroom.di.modules;

import android.content.Context;
import android.graphics.Typeface;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by zhangyaozhong on 10/01/2017.
 */
@Module
public class FontAssetModule {

    public FontAssetModule() {}

    @Provides
    @Named("stolzl_regular")
    Typeface provideStolzlRegularFont(Context context) {
        return Typeface.createFromAsset(context.getAssets(),"fonts/stolzl_regular.otf");
    }

    @Provides
    @Named("stolzl_medium")
    Typeface provideStolzlMediumFont(Context context) {
        return Typeface.createFromAsset(context.getAssets(),"fonts/stolzl_medium.otf");
    }

}
