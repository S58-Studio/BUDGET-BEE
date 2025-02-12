plugins {
    id("oneSaver.widget")
}

android {
    namespace = "com.oneSaver.widget"
}

dependencies {
    implementation(projects.common.base)
    implementation(projects.common.domain)
}
