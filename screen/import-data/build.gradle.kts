plugins {
    id("financeAndMoney.feature")
}

android {
    namespace = "com.financeAndMoney.importdata"
}

dependencies {
    implementation(projects.screen.onboarding)
    implementation(projects.common.base)
    implementation(projects.common.data.core)
    implementation(projects.common.domain)
    implementation(projects.common.userInterface.core)
    implementation(projects.common.userInterface.navigation)
    implementation(projects.temp.legacyCode)
    implementation(projects.temp.oldDesign)

    implementation(libs.bundles.opencsv)
}