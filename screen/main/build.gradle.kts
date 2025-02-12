plugins {
    id("oneSaver.feature")
}

android {
    namespace = "com.oneSaver.main"
}

dependencies {
    implementation(projects.screen.accounts)
    implementation(projects.screen.home)
    implementation(projects.common.base)
    implementation(projects.common.data.core)
    implementation(projects.common.domain)
    implementation(projects.screen.reportStatements)
    implementation(projects.screen.accounts)
    implementation(projects.screen.controls)
    implementation(projects.common.userInterface.core)
    implementation(projects.common.userInterface.navigation)
    implementation(projects.temp.legacyCode)
    implementation(projects.temp.oldDesign)
}
