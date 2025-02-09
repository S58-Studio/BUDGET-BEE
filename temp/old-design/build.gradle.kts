plugins {
    id("financeAndMoney.feature")
}

android {
    namespace = "com.financeAndMoney.design"
}

dependencies {
    implementation(projects.common.base)
    implementation(projects.common.userInterface.core)

    implementation(projects.common.domain)
}