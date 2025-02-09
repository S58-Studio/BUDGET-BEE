package com.financeAndMoney.base.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesHelper {
    private static final String PREF_NAME = "MySavePreferences";
    private static final String KEY_AD_NETWORK_PROVIDER = "AdNetworkProvider";
    private static final String PREFERENCES_FILE = "my_prefs";
    private static final String LAST_AD_WAS_ADMOB_KEY = "last_ad_was_admob";

    public static void saveAdNetworkProvider(Context context, String adNetworkProvider) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_AD_NETWORK_PROVIDER, adNetworkProvider);
        editor.apply();
    }

    public static String getAdNetworkProvider(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_AD_NETWORK_PROVIDER, "MetaAds"); // Default to "MetaAds"
    }

    // Retrieve the last ad network shown (default is false, meaning the last ad was Meta)
    public static boolean getLastAdNetworkWasAdMob(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        return preferences.getBoolean(LAST_AD_WAS_ADMOB_KEY, false); // Default to false (Meta Ads)
    }

    // Save the last ad network shown
    public static void setLastAdNetworkWasAdMob(Context context, boolean wasAdMob) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(LAST_AD_WAS_ADMOB_KEY, wasAdMob);
        editor.apply();
    }
}

