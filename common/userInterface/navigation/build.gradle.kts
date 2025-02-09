plugins {
    id("financeAndMoney.feature")
}

android {
    namespace = "com.financeAndMoney.navigation"
}

dependencies {
    implementation(projects.common.base)
    implementation(projects.common.domain)
    implementation(projects.common.userInterface.core)
}
