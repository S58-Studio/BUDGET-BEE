plugins {
    id("oneSaver.feature")
}

android {
    namespace = "com.oneSaver.core.userInterface"

}

dependencies {
    implementation(projects.common.domain)
    implementation(libs.appcompat.activity)
    implementation(project(":common:base"))
}