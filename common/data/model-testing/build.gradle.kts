plugins {
    id("financeAndMoney.feature")
}

android {
    namespace = "com.financeAndMoney.data.model.testing"
}

dependencies {
    implementation(projects.common.data.model)

    implementation(libs.bundles.testing)
}