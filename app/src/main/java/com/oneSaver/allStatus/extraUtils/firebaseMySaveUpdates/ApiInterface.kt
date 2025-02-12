package com.oneSaver.allStatus.extraUtils.firebaseMySaveUpdates

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url


interface ApiInterface {

    @GET("NewAppUpdates.json")
    fun getAppUpdates(): Call<AppUpdates>

    @GET
    fun fetchFile(@Url fileUrl: String): Call<ResponseBody>
}