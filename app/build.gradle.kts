plugins {
    id("com.android.application")
    id("kotlin-android")
    id("org.jetbrains.kotlin.android")
    org.jetbrains.kotlin.plugin.compose
    id("dagger.hilt.android.plugin")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("com.google.devtools.ksp")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("io.gitlab.arturbosch.detekt")
}

android {
    namespace = "com.financeAndMoney.expenseAndBudgetPlanner"
    compileSdk = libs.versions.compile.sdk.get().toInt()

    defaultConfig {
        applicationId = "com.financeAndMoney.expenseAndBudgetPlanner"
        minSdk = libs.versions.min.sdk.get().toInt()
        targetSdk = libs.versions.compile.sdk.get().toInt()
        versionName = libs.versions.version.name.get()
        versionCode = libs.versions.version.code.get().toInt()
    }

    androidResources {
        generateLocaleConfig = true
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            isDebuggable = false
            isDefault = false

//            signingConfig = signingConfigs.getByName("release")

            resValue("string", "app_name", "MySave")
        }

        debug {
            isMinifyEnabled = false
            isShrinkResources = false

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            isDebuggable = true
            isDefault = true

            resValue("string", "app_name", "MySave")
        }
    }

    val javaVersion = libs.versions.jvm.target.get()
    kotlinOptions {
        jvmTarget = javaVersion
    }

    compileOptions {
        sourceCompatibility = JavaVersion.valueOf("VERSION_$javaVersion")
        targetCompatibility = JavaVersion.valueOf("VERSION_$javaVersion")
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    lint {
        disable += "ComposeViewModelInjection"
        checkDependencies = true
        abortOnError = false
        checkReleaseBuilds = false
        htmlReport = true
        htmlOutput = file("${project.rootDir}/build/reportStatements/lint/lint.html")
        xmlReport = true
        xmlOutput = file("${project.rootDir}/build/reportStatements/lint/lint.xml")
        baseline = file("lint-baseline.xml")
    }
}

dependencies {

    implementation(projects.screen.mulaBalanc)
    implementation(projects.screen.accounts)
    implementation(projects.screen.budgets)
    implementation(projects.screen.categories)
    implementation(projects.screen.disclaimer)
    implementation(projects.screen.editTransaction)
    implementation(projects.screen.exchangeRates)
    implementation(projects.screen.features)
    implementation(projects.screen.home)
    implementation(projects.screen.importData)
    implementation(projects.screen.loans)
    implementation(projects.screen.main)
    implementation(projects.screen.onboarding)
    implementation(projects.screen.piechart)
    implementation(projects.screen.iliyopangwaPayments)
    implementation(projects.screen.reportStatements)
    implementation(projects.screen.seek)
    implementation(projects.screen.controls)
    implementation(projects.screen.transfers)
    implementation(projects.common.base)
    implementation(projects.common.data.core)
    implementation(projects.common.domain)
    implementation(projects.common.userInterface.navigation)
    implementation(projects.temp.legacyCode)
    implementation(projects.temp.oldDesign)
    implementation(projects.widget.addTransaction)
    implementation(projects.widget.mulaBalanc)

    implementation(libs.bundles.kotlin)
    implementation(libs.bundles.kotlin.android)
    implementation(libs.bundles.ktor)
    implementation(libs.bundles.arrow)
    implementation(libs.bundles.compose)
    implementation(libs.bundles.activity)
    implementation(libs.datastore)
    implementation(libs.androidx.security)
    implementation(libs.androidx.biometrics)

    implementation(libs.bundles.hilt)
    implementation(libs.material)
    implementation(project(":common:userInterface:core"))
    ksp(libs.hilt.compiler)

    implementation(libs.bundles.room)
    ksp(libs.room.compiler)

    implementation(libs.timber)
    implementation(libs.keval)
    implementation(libs.bundles.opencsv)
    implementation(libs.androidx.work)
    implementation(libs.androidx.recyclerview)

    testImplementation(libs.bundles.testing)
    testImplementation(libs.androidx.work.testing)

    lintChecks(libs.slack.lint.compose)

    //firebase
    implementation(libs.bundles.google)
    implementation(libs.bundles.firebase)
    implementation(platform(libs.androidx.firebase.bom))
    implementation(libs.firebase.google.analytics)
    implementation(libs.firebase.google.database)
    implementation(libs.firebase.google.messaging)
    implementation(libs.firebase.google.display.messaging)

    //Admob and IAP
    implementation(libs.google.admob)
    implementation(libs.google.admob.consent)

    //Retrofit
    implementation(libs.retrofix.gson)
    implementation(libs.network.retrofix)

    //No internet dialog
    implementation (libs.oopsnointernet.v200)

    //Meta
    implementation (libs.facebook.android.sdk)
}
