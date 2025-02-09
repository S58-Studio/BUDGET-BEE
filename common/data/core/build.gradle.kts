plugins {
    id("financeAndMoney.feature")
    id("financeAndMoney.room")
    id("financeAndMoney.integration.testing")
}

android {
    namespace = "com.financeAndMoney.data"
}

dependencies {
    implementation(projects.common.base)
    api(projects.common.data.model)

    implementation(libs.datastore)
    implementation(libs.bundles.ktor)

    testImplementation(projects.common.data.modelTesting)
    androidTestImplementation(libs.bundles.integration.testing)
}
