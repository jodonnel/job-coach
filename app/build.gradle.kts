plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.jodonnel.jobcoach"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.jodonnel.jobcoach"
        minSdk = 29
        targetSdk = 35
        versionCode = 1
        versionName = "0.1.0"

        val metaAppId = providers.gradleProperty("dat.meta.app.id")
            .orElse(providers.environmentVariable("META_APP_ID"))
            .getOrElse("")
        manifestPlaceholders["metaAppId"] = metaAppId

        val openShiftEndpoint = providers.gradleProperty("openshift.endpoint")
            .getOrElse("https://north-qr-demo-qa.apps.rosa.rosa-rvdtn.ypet.p3.openshiftapps.com")
        buildConfigField("String", "OPENSHIFT_ENDPOINT", "\"$openShiftEndpoint\"")
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            applicationIdSuffix = ".debug"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(project(":core"))
    implementation(project(":wearable"))
    implementation(project(":stt"))

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    implementation(platform(libs.compose.bom))
    implementation(libs.compose.material3)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.preview)
    debugImplementation(libs.compose.ui.tooling)

    implementation(libs.lifecycle.runtime)
    implementation(libs.lifecycle.viewmodel)
    implementation(libs.coroutines.android)

    testImplementation(libs.junit)
    testImplementation(libs.coroutines.test)

    // MockDeviceKit for debug builds (demo mode without glasses)
    debugImplementation(libs.mwdat.mockdevice)
}
