package com.oneSaver.base.utils

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.android.billingclient.api.SkuDetailsParams
import com.oneSaver.base.R

class AccessAllFeaturesActivity : AppCompatActivity() {

    private var skuDetail: String = MySaveConstants.MONTHLY_SKU
    private var billingClient: BillingClient? = null
    private lateinit var llPerYear: LinearLayout
    private lateinit var tvYearlyTitle: TextView
    private lateinit var tvYearlyPrice: TextView
    private lateinit var imgCheckYearly: ImageView
    private lateinit var llPerMonth: LinearLayout
    private lateinit var tvMonthlyTitle: TextView
    private lateinit var tvMonthlyPrice: TextView
    private lateinit var imgCheckMonthly: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_access_all_feature)

        llPerYear = findViewById(R.id.llPerYear)
        tvYearlyTitle = findViewById(R.id.tvYearlyTitle)
        tvYearlyPrice = findViewById(R.id.tvYearlyPrice)
        imgCheckYearly = findViewById(R.id.imgCheckYearly)
        llPerMonth = findViewById(R.id.llPerMonth)
        tvMonthlyTitle = findViewById(R.id.tvMonthlyTitle)
        tvMonthlyPrice = findViewById(R.id.tvMonthlyPrice)
        imgCheckMonthly = findViewById(R.id.imgCheckMonthly)

        initIntentParam()
        init()
    }

    private fun initIntentParam() {
        try {
            // Handle intent parameters if needed
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun init() {
        findViewById<View>(R.id.imgBack).setOnClickListener {
            finish()
        }

        llPerYear.setOnClickListener {
            onYearlyClick()
        }

        llPerMonth.setOnClickListener {
            onPerMonthClick()
        }

        findViewById<View>(R.id.btnContinue).setOnClickListener {
            onContinueClick()
        }

        initInAppPurchase()
    }

    override fun onResume() {
        super.onResume()
    }

    private fun onYearlyClick() {
        llPerYear.backgroundTintList =
            ContextCompat.getColorStateList(this, R.color.md_theme_light_primary)
        tvYearlyTitle.setTextColor(
            ContextCompat.getColor(this, R.color.md_blue_400)
        )
        tvYearlyPrice.setTextColor(
            ContextCompat.getColor(this, R.color.primary)
        )
        imgCheckYearly.visibility = View.VISIBLE

        llPerMonth.backgroundTintList =
            ContextCompat.getColorStateList(this, R.color.col_999)
        tvMonthlyPrice.setTextColor(
            ContextCompat.getColor(this, R.color.grayish)
        )
        tvMonthlyTitle.setTextColor(
            ContextCompat.getColor(this, R.color.grayish)
        )
        imgCheckMonthly.visibility = View.GONE

        skuDetail = MySaveConstants.YEARLY_SKU
    }

    private fun onPerMonthClick() {
        llPerYear.backgroundTintList =
            ContextCompat.getColorStateList(this, R.color.col_999)
        tvYearlyTitle.setTextColor(
            ContextCompat.getColor(this, R.color.grayish)
        )
        tvYearlyPrice.setTextColor(
            ContextCompat.getColor(this, R.color.grayish)
        )
        imgCheckYearly.visibility = View.GONE

        llPerMonth.backgroundTintList =
            ContextCompat.getColorStateList(this, R.color.md_theme_light_primary)
        tvMonthlyPrice.setTextColor(
            ContextCompat.getColor(this, R.color.primary)
        )
        tvMonthlyTitle.setTextColor(
            ContextCompat.getColor(this, R.color.md_blue_400)
        )
        imgCheckMonthly.visibility = View.VISIBLE

        skuDetail = MySaveConstants.MONTHLY_SKU
    }

    private fun onContinueClick() {
        onPurchaseClick(skuDetail)
    }

    private fun onPurchaseClick(SKU: String) {
        val skuList = arrayListOf(SKU)
        val params = SkuDetailsParams.newBuilder()
        params.setSkusList(skuList).setType(BillingClient.SkuType.SUBS)
        billingClient!!.querySkuDetailsAsync(params.build()) { _, list ->
            if (list!!.isNotEmpty()) {
                runOnUiThread {
                    val billingFlowParams = BillingFlowParams.newBuilder()
                        .setSkuDetails(list[0])
                        .build()
                    val responseCode =
                        billingClient!!.launchBillingFlow(this, billingFlowParams).responseCode

                    Log.e("BILLING", responseCode.toString())

                    if (responseCode == BillingClient.BillingResponseCode.OK) {
                        logPurchaseEvent(SKU) // Log purchase event
                    }
                }
            } else {
                Log.d("BILLING", "BILLING LIST IS EMPTY")
            }
        }
    }

    private fun logPurchaseEvent(SKU: String) {
        //WILL IMPLEMENT LATER
    }

    private fun initInAppPurchase() {
        try {
            billingClient = BillingClient.newBuilder(this).setListener(purchaseUpdateListener)
                .enablePendingPurchases().build()
            billingClient!!.startConnection(object : BillingClientStateListener {
                override fun onBillingServiceDisconnected() {
                    Log.e("TAG", "onBillingServiceDisconnected::::: ")
                }

                override fun onBillingSetupFinished(p0: BillingResult) {
                    Log.e("TAG", "onBillingSetupFinished:::: " + p0.debugMessage)
                    checkSubscriptionList()
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private val purchaseUpdateListener: PurchasesUpdatedListener =
        PurchasesUpdatedListener { result, _ ->
            try {
                if (result.responseCode != BillingClient.BillingResponseCode.OK) {
                    if (result.responseCode == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
                        PremiumUserUtils.setPref(this, MySaveConstants.PREF_KEY_PURCHASE_STATUS, true)
                        restartApp()
                        finish()
                    }
                } else {
                    PremiumUserUtils.setPref(this, MySaveConstants.PREF_KEY_PURCHASE_STATUS, true)
                    restartApp()
                    finish()
                }
                checkSubscriptionList()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    private fun checkSubscriptionList() {
        if (billingClient != null) {
            var isPurchasedSku = false
            try {
                billingClient!!.queryPurchasesAsync(
                    QueryPurchasesParams.newBuilder()
                        .setProductType(BillingClient.ProductType.SUBS)
                        .build()
                ) { purchasesResult, purchaseList ->
                    if (purchasesResult.responseCode == 0) {
                        if (purchaseList.isNotEmpty()) {
                            for (purchaseData in purchaseList) {
                                if ((purchaseData.products.contains(MySaveConstants.MONTHLY_SKU)) || (purchaseData.products.contains(MySaveConstants.YEARLY_SKU))) {
                                    isPurchasedSku = true
                                }

                                if (purchaseData.purchaseState == Purchase.PurchaseState.PURCHASED) {
                                    if (!purchaseData.isAcknowledged) {
                                        val acknowledgePurchaseParams =
                                            AcknowledgePurchaseParams.newBuilder()
                                                .setPurchaseToken(purchaseData.purchaseToken)
                                        billingClient!!.acknowledgePurchase(
                                            acknowledgePurchaseParams.build()
                                        ) { p0 ->
                                            Log.e("BillingResult ======>", p0.debugMessage)
                                        }
                                    }
                                }
                            }
                        }
                        PremiumUserUtils.setPref(this, MySaveConstants.PREF_KEY_PURCHASE_STATUS, isPurchasedSku)
                        getSKUDetails()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun getSKUDetails() {
        val productListMonth = listOf(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(MySaveConstants.MONTHLY_SKU)
                .setProductType(BillingClient.ProductType.SUBS)
                .build(),
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(MySaveConstants.YEARLY_SKU)
                .setProductType(BillingClient.ProductType.SUBS)
                .build(),
        )

        val paramsNewMonth = QueryProductDetailsParams.newBuilder().setProductList(productListMonth)

        billingClient!!.queryProductDetailsAsync(paramsNewMonth.build()) { billingResult, skuDetailsList ->
            if (billingResult.responseCode == 0 && skuDetailsList.isNotEmpty()) {
                for (thisResponse in skuDetailsList) {
                    try {
                        runOnUiThread {
                            when (thisResponse.productId) {
                                MySaveConstants.MONTHLY_SKU -> {
                                    tvMonthlyPrice.text = "${thisResponse.subscriptionOfferDetails!![0].pricingPhases.pricingPhaseList[0].formattedPrice} / Month"
                                }
                                MySaveConstants.YEARLY_SKU -> {
                                    tvYearlyPrice.text = "${thisResponse.subscriptionOfferDetails!![0].pricingPhases.pricingPhaseList[0].formattedPrice} / Yearly"
                                }
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    private fun restartApp() {
        val packageManager = packageManager
        val intent = packageManager.getLaunchIntentForPackage(packageName)
        val componentName = intent?.component
        val mainIntent = Intent.makeRestartActivityTask(componentName)
        startActivity(mainIntent)
        Runtime.getRuntime().exit(0)
    }
}
