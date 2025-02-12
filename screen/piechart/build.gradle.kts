plugins {
    id("oneSaver.feature")
}

android {
    namespace = "com.oneSaver.piechart"
}

dependencies {
    implementation(projects.common.base)
    implementation(projects.common.data.core)
    implementation(projects.common.domain)
    implementation(projects.common.userInterface.core)
    implementation(projects.common.userInterface.navigation)
    implementation(projects.temp.legacyCode)
    implementation(projects.temp.oldDesign)
}
