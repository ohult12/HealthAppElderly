package com.example.healthappelderly;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class LocaleHelper extends AppCompatActivity {
    public static final String LANG_PREF = "Lang Prefs";
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
    public static String getLocale(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("Languages", Context.MODE_PRIVATE);
        return preferences.getString(LocaleHelper.LANG_PREF, "NA");
    }
    public static void saveLocale(Context context,String lang) {
        SharedPreferences preferences = context.getSharedPreferences("Languages", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(LocaleHelper.LANG_PREF, lang);
        editor.apply();
    }
}
