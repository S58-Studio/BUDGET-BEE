plugins {
    id("oneSaver.widget")
}

android {
    namespace = "com.oneSaver.widget.transaction"
}

dependencies {
    implementation(projects.common.base)
    implementation(projects.common.base)
    implementation(projects.common.domain)
    implementation(projects.common.userInterface.core)
    implementation(projects.common.userInterface.navigation)

    implementation(projects.widget.commonBase)
}
