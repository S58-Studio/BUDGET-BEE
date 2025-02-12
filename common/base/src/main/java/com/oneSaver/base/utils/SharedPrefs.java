package com.oneSaver.base.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefs {
    public static final String IS_PRO = "isPro";
    public static final String ALL_IVY_DATA = "all_ivy_data";

    public static boolean getIsPro(Context context) {
        SharedPreferences pref = context.getSharedPreferences(ALL_IVY_DATA, Context.MODE_PRIVATE);
        return pref.getBoolean(IS_PRO, false);
    }

    public static void setIsPro(Context context, boolean isPro) {
        SharedPreferences pref = context.getSharedPreferences(ALL_IVY_DATA, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(IS_PRO, isPro);
        editor.apply();
    }
}
