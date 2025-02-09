plugins {
    id("financeAndMoney.widget")
}

android {
    namespace = "com.financeAndMoney.widget.mulaBalanc"
}

dependencies {
    implementation(projects.common.base)
    implementation(projects.common.domain)
    implementation(projects.common.userInterface.core)
    implementation(projects.common.userInterface.navigation)

    implementation(projects.temp.oldDesign)
    implementation(projects.widget.commonBase)
    implementation(projects.temp.legacyCode)
}
