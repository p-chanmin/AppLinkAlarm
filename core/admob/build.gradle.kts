import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    id("oldogz.android.library")
    id("oldogz.android.compose")
}

android {
    namespace = "com.oldogz.core.admob"

    buildTypes {
        getByName("debug") {
            buildConfigField("String", "ADMOB_APP_ID", "\"${getPropertyKey("TEST_ADMOB_APP_ID")}\"")
            manifestPlaceholders["ADMOB_APP_ID"] = getPropertyKey("TEST_ADMOB_APP_ID")

            buildConfigField("String", "ADMOB_NATIVE_ADS_ID", "\"${getPropertyKey("TEST_ADMOB_NATIVE_ADS_ID")}\"")
            buildConfigField("String", "ADMOB_INTERSTITIAL_ADS_ID", "\"${getPropertyKey("TEST_ADMOB_INTERSTITIAL_ADS_ID")}\"")
        }

        getByName("release") {
            buildConfigField("String", "ADMOB_APP_ID", "\"${getPropertyKey("RELEASE_ADMOB_APP_ID")}\"")
            manifestPlaceholders["ADMOB_APP_ID"] = getPropertyKey("RELEASE_ADMOB_APP_ID")

            buildConfigField("String", "ADMOB_NATIVE_ADS_ID", "\"${getPropertyKey("RELEASE_ADMOB_NATIVE_ADS_ID")}\"")
            buildConfigField("String", "ADMOB_INTERSTITIAL_ADS_ID", "\"${getPropertyKey("RELEASE_ADMOB_INTERSTITIAL_ADS_ID")}\"")
        }
    }

    buildFeatures {
        viewBinding = true
    }
}

fun getPropertyKey(propertyKey: String): String {
    return gradleLocalProperties(rootDir, providers).getProperty(propertyKey)
}

dependencies {
    api(libs.play.services.ads)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.ui.viewbinding)
    implementation(libs.androidx.appcompat)
}