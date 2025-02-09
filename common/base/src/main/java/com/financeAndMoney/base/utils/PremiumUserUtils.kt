package com.financeAndMoney.base.utils

import android.content.Context
import androidx.preference.PreferenceManager

object PremiumUserUtils {

    fun setPref(c: Context, pref: String, `val`: Boolean) {
        val e = PreferenceManager.getDefaultSharedPreferences(c).edit()
        e.putBoolean(pref, `val`)
        e.apply()
    }

    fun getPref(c: Context, pref: String, `val`: Boolean): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(c).getBoolean(
            pref, `val`
        )
    }

    fun isPurchased(context: Context):Boolean{
        if(Debug.DEBUG_IS_PURCHASE)
            return true
        return getPref(context, MySaveConstants.PREF_KEY_PURCHASE_STATUS, false)
    }
}