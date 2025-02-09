plugins {
    id("financeAndMoney.feature")
    id("financeAndMoney.integration.testing")
    id("financeAndMoney.room")
}

android {
    namespace = "com.financeAndMoney.domain"
}

dependencies {
    implementation(projects.common.base)
    implementation(projects.common.data.core)

    implementation(libs.datastore)
    implementation(libs.bundles.ktor)
    implementation(libs.bundles.opencsv)

    testImplementation(projects.common.data.modelTesting)
    testImplementation(projects.common.data.coreTesting)

    androidTestImplementation(libs.bundles.integration.testing)
    androidTestImplementation(libs.mockk.android)
}