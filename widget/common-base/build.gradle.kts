plugins {
    id("financeAndMoney.widget")
}

android {
    namespace = "com.financeAndMoney.widget"
}

dependencies {
    implementation(projects.common.base)
    implementation(projects.common.domain)
}
