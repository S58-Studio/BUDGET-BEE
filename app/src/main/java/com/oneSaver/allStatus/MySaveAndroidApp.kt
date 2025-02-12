package com.oneSaver.allStatus

import android.app.Activity
import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.anythink.core.api.ATSDK
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.oneSaver.base.legacy.appContext
import com.oneSaver.base.utils.MySaveConstants.Companion.FIREBASE_RTDB_ADSPROVIDER_URL
import com.oneSaver.base.utils.MySaveConstants.Companion.TOPON_APP_ID
import com.oneSaver.base.utils.MySaveConstants.Companion.TOPON_APP_KEY
import com.oneSaver.base.utils.SharedPreferencesHelper
import com.google.android.gms.ads.MobileAds
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import timber.log.Timber.DebugTree
import javax.inject.Inject


@HiltAndroidApp
class MySaveAndroidApp : Application(), Configuration.Provider {
    @Inject
    lateinit var workerFactory: HiltWorkerFactory
    private val backgroundScope = CoroutineScope(Dispatchers.IO)
    private val backgroundScopee = CoroutineScope(Dispatchers.Default)
    private var currentActivity: Activity? = null
    private lateinit var firebaseAnalytics: FirebaseAnalytics


    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        appContext = this

        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }

        AppEventsLogger.activateApp(this)
        //Ad initialization
        backgroundScope.launch {
            // Network-bound work
            fetchAdNetworkProviderFromRTDB()
        }
        backgroundScopee.launch {
            // CPU-bound work
            firebaseAnalytics = Firebase.analytics
            MobileAds.initialize(appContext) { }
            ATSDK.init(appContext, TOPON_APP_ID, TOPON_APP_KEY)
            FacebookSdk.sdkInitialize(appContext)
        }

    }


    private fun fetchAdNetworkProviderFromRTDB() {

        val database = FirebaseDatabase.getInstance().getReference(FIREBASE_RTDB_ADSPROVIDER_URL)
        Timber.tag("ADNETWORKs").d("FETCH AD NETWORKS CALLED")

        database.get().addOnSuccessListener { snapshot ->
            Timber.tag("ADNETWORKs").d("WAS A SUCCESS: Snapshot exists")
            if (snapshot.exists()) {
                val adNetworkProvider = snapshot.value?.toString() ?: "MetaAds"
                SharedPreferencesHelper.saveAdNetworkProvider(appContext, adNetworkProvider)
                Timber.tag("ADNETWORKs").d("AD provider %s", adNetworkProvider)
            } else {
                Timber.tag("ADNETWORKs").d("Snapshot does not exist.")
                SharedPreferencesHelper.saveAdNetworkProvider(appContext, "MetaAds")
            }
        }.addOnFailureListener { exception ->
            Timber.tag("ADNETWORKs").e(exception, "AD NETWORK fetching failed")
            SharedPreferencesHelper.saveAdNetworkProvider(appContext, "MetaAds")
        }
    }

}
