plugins {
    id("oneSaver.feature")
}

android {
    namespace = "com.oneSaver.data.model.testing"
}

dependencies {
    implementation(projects.common.data.model)

    implementation(libs.bundles.testing)
}