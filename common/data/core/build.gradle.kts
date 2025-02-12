plugins {
    id("oneSaver.feature")
    id("oneSaver.room")
    id("oneSaver.integration.testing")
}

android {
    namespace = "com.oneSaver.data"
}

dependencies {
    implementation(projects.common.base)
    api(projects.common.data.model)

    implementation(libs.datastore)
    implementation(libs.bundles.ktor)

    testImplementation(projects.common.data.modelTesting)
    androidTestImplementation(libs.bundles.integration.testing)
}
