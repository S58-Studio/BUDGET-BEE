plugins {
    id("financeAndMoney.feature")
}

android {
    namespace = "com.financeAndMoney.core.userInterface"

}

dependencies {
    implementation(projects.common.domain)
    implementation(libs.appcompat.activity)
    implementation(project(":common:base"))
}