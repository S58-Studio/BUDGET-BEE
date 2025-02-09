plugins {
    id("financeAndMoney.feature")
}

android {
    namespace = "com.financeAndMoney.accounts"
}

dependencies {
    implementation(projects.common.base)
    implementation(projects.common.data.core)
    implementation(projects.common.domain)
    implementation(projects.common.userInterface.core)
    implementation(projects.common.userInterface.navigation)
    implementation(projects.temp.legacyCode)
    implementation(projects.temp.oldDesign)
    implementation(project(":screen:home"))

    testImplementation(projects.common.userInterface.testing)
}
