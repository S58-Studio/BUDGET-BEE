plugins {
    id("oneSaver.feature")
}

android {
    namespace = "com.oneSaver.userInterface.testing"
}

dependencies {
    implementation(projects.common.userInterface.core)

    // for this module we need test deps as "implementation" and not only "testImplementation"
    // because it'll be added as "testImplementation"
    implementation(libs.bundles.testing)
    implementation(libs.paparazzi)
}
