plugins {
    id("financeAndMoney.feature")
}

android {
    namespace = "com.financeAndMoney.home"
}

dependencies {
    implementation(projects.common.base)
    implementation(projects.common.data.core)
    implementation(projects.common.domain)
    implementation(projects.common.userInterface.core)
    implementation(projects.common.userInterface.navigation)
    implementation(projects.temp.legacyCode)
    implementation(projects.temp.oldDesign)
    implementation(projects.widget.addTransaction)

    testImplementation(projects.common.userInterface.testing)
}
