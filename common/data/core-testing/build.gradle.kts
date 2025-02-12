plugins {
    id("oneSaver.feature")
    id("oneSaver.room")
}

android {
    namespace = "com.oneSaver.data.testing"
}

dependencies {
    implementation(projects.common.data.core)
}
