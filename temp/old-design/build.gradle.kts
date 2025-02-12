plugins {
    id("oneSaver.feature")
}

android {
    namespace = "com.oneSaver.design"
}

dependencies {
    implementation(projects.common.base)
    implementation(projects.common.userInterface.core)

    implementation(projects.common.domain)
}