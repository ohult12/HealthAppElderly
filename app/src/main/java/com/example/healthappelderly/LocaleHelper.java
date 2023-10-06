package com.example.healthappelderly;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class LocaleHelper extends AppCompatActivity {
    public static void setLocale(Context context, String lang) {
        changeLocale(context, lang);
    }
    private static void changeLocale(Context context, String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        DisplayMetrics metrics = resources.getDisplayMetrics();
        resources.updateConfiguration(config, metrics);
    }
}
