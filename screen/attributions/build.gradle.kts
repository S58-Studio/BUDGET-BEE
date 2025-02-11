plugins {
    id("financeAndMoney.feature")
}

android {
    namespace = "com.financeAndMoney.attributions"
}

dependencies {
    implementation(projects.common.base)
    implementation(projects.common.domain)
    implementation(projects.common.userInterface.core)
    implementation(projects.common.userInterface.navigation)

    testImplementation(projects.common.userInterface.testing)
}
