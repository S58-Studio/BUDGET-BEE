plugins {
    id("oneSaver.feature")
}

android {
    namespace = "com.oneSaver.navigation"
}

dependencies {
    implementation(projects.common.base)
    implementation(projects.common.domain)
    implementation(projects.common.userInterface.core)
}
