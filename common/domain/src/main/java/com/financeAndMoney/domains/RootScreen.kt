package com.financeAndMoney.domains

import android.net.Uri

interface RootScreen {
    /**
     * BuildConfig.DEBUG
     */
    val isDebug: Boolean

    /**
     * BuildConfig.VERSION_NAME
     */
    val buildVersionName: String

    /**
     * BuildConfig.VERSION_CODE
     */
    val buildVersionCode: Int

    fun reviewMySave(dismissReviewCard: Boolean)

    fun shareMySave()

    fun openUrlInBrowser(url: String)

    fun shareCSVFile(fileUri: Uri)

    fun shareZipFile(fileUri: Uri)

    fun openGooglePlayAppPage(appId: String)

    fun <T> pinWidget(widget: Class<T>)
}
