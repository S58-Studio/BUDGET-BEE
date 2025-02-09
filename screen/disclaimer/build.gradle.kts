plugins {
    id("financeAndMoney.feature")
}

android {
    namespace = "com.financeAndMoney.disclaimer"
}

dependencies {
    implementation(projects.common.data.core)
    implementation(projects.common.userInterface.core)
    implementation(projects.common.userInterface.navigation)

    testImplementation(projects.common.userInterface.testing)
}
