plugins {
    id("financeAndMoney.feature")
}

android {
    namespace = "com.financeAndMoney.features"
}

dependencies {
    implementation(projects.common.base)
    implementation(projects.common.domain)
    implementation(projects.common.userInterface.core)
    implementation(projects.common.userInterface.core)
    implementation(projects.common.userInterface.navigation)
}