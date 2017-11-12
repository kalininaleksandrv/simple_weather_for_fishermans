package dev.eyesless.simple_weather_for_fishermans;

import android.app.Application;
import android.content.res.Configuration;

import java.util.Locale;

public class MyApplication extends Application {

    public static String defSystemLanguage;

    @Override
    public void onCreate() {
        super.onCreate();

        defSystemLanguage = Locale.getDefault().getLanguage();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        defSystemLanguage = newConfig.locale.getLanguage();
    }

}

