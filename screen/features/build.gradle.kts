plugins {
    id("oneSaver.feature")
}

android {
    namespace = "com.oneSaver.features"
}

dependencies {
    implementation(projects.common.base)
    implementation(projects.common.domain)
    implementation(projects.common.userInterface.core)
    implementation(projects.common.userInterface.core)
    implementation(projects.common.userInterface.navigation)
}