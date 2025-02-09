plugins {
    id("financeAndMoney.feature")
    id("financeAndMoney.room")
}

android {
    namespace = "com.financeAndMoney.data.testing"
}

dependencies {
    implementation(projects.common.data.core)
}
