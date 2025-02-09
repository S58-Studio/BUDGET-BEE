plugins {
    id("financeAndMoney.feature")
}

android {
    namespace = "com.financeAndMoney.base"

}
dependencies {
    //Admob and IAP
    implementation(libs.google.admob)
    implementation(libs.google.iab)
    //Android
    implementation(libs.androidx.preference)
    implementation(libs.material)
    implementation(libs.appcompat.activity)
    implementation (libs.androidx.constraintlayout) //2.1.4
    implementation (libs.sdp.android)
    implementation (libs.intuit.ssp.android)
    //Meta
    implementation(libs.adapter.facebook)
    implementation (libs.androidx.annotation)
    implementation (libs.facebook.android.sdk)
    implementation (libs.infer.annotation)
    //Lottie
    implementation (libs.lottie)

    //Topon
    api(libs.core)
    implementation(libs.interstitial)
    //Tramini
    implementation(libs.tramini.plugin)

}
