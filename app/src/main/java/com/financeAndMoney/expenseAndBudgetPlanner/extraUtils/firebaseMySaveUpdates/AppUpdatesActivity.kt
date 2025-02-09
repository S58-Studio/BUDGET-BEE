package com.financeAndMoney.expenseAndBudgetPlanner.extraUtils.firebaseMySaveUpdates

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.financeAndMoney.expenseAndBudgetPlanner.R
import com.financeAndMoney.expenseAndBudgetPlanner.MySaveRootActivty
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@AndroidEntryPoint
class AppUpdatesActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_updates)

        val dialog = PopupWindowUpdates()
        dialog.isCancelable = false
        dialog.show(supportFragmentManager, "APP_UPDATE_DIALOG")
        checkNewAppUpdates()
    }

    private fun checkNewAppUpdates() {
        val dataSource = AppUpdatesDataSource(this)
        val apiInterface = FirebaseApiClient().getAppUpdates().create(ApiInterface::class.java)
        apiInterface.getAppUpdates().enqueue(object : Callback<AppUpdates> {
            override fun onResponse(call: Call<AppUpdates>, response: Response<AppUpdates>) {
                if(response.isSuccessful) {
                    val appUpdates = response.body()
                    try {
                        val pkgInfo = packageManager.getPackageInfo(packageName, 0)
                        if(appUpdates != null && appUpdates.version != pkgInfo.versionName) {
                            // Checking and making sure we don't overwrite the same data again and again
                            val previousUpdate = dataSource.getAppUpdates()
                            if(previousUpdate == null) {
                                dataSource.addAppUpdates(appUpdates)
                            }
                            if(previousUpdate != null && appUpdates.version != previousUpdate.version) {
                                dataSource.clearAppUpdates()
                                dataSource.addAppUpdates(appUpdates)
                            }

                            val dialog = PopupWindowUpdates()
                            dialog.isCancelable = false
                            dialog.show(supportFragmentManager, "APP_UPDATE_DIALOG")
                        }
                    }
                    catch (e: PackageManager.NameNotFoundException) {
                        intent = Intent(this@AppUpdatesActivity, MySaveRootActivty::class.java)
                        startActivity(intent)
                    }
                }
            }
            override fun onFailure(call: Call<AppUpdates>, t: Throwable) { }
        })
    }
}
