package com.oneSaver.allStatus.extraUtils.firebaseMySaveUpdates

import com.oneSaver.base.utils.MySaveConstants.Companion.FIREBASE_NEW_UPDATES_DB_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class FirebaseApiClient {


    fun getAppUpdates(): Retrofit{
        return Retrofit.Builder()
            .baseUrl(FIREBASE_NEW_UPDATES_DB_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    //fetching admob ads
    fun getAdMobIds(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(FIREBASE_NEW_UPDATES_DB_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

}