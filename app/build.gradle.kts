import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    id("oldogz.android.application")
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.aboutLibraries)
}

android {
    namespace = "com.oldogz.applinkalarm"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.oldogz.applinkalarm"
        minSdk = 26
        targetSdk = 35
        versionCode = 11
        versionName = "1.0.3"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    signingConfigs {
        getByName("debug") {
            keyAlias = getPropertyKey("SIGNED_KEY_ALIAS")
            keyPassword = getPropertyKey("SIGNED_KEY_PASSWORD")
            storePassword = getPropertyKey("SIGNED_STORE_PASSWORD")
            storeFile = file(getPropertyKey("SIGNED_STORE_FILE"))
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("debug")
            isDebuggable = false
        }
    }
}

fun getPropertyKey(propertyKey: String): String {
    return gradleLocalProperties(rootDir, providers).getProperty(propertyKey)
}

dependencies {
    implementation(project(":feature:main"))
}