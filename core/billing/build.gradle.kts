import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    id("oldogz.android.library")
    id("oldogz.android.compose")
}

android {
    namespace = "com.oldogz.core.billing"

    defaultConfig {
        buildConfigField("String", "PREMIUM_MEMBERSHIP_PRODUCT_ID", "\"${getPropertyKey("PREMIUM_MEMBERSHIP_PRODUCT_ID")}\"")
        buildConfigField("String", "PREMIUM_MONTHLY_TRIAL_7D_OFFER_ID", "\"${getPropertyKey("PREMIUM_MONTHLY_TRIAL_7D_OFFER_ID")}\"")
    }
}

fun getPropertyKey(propertyKey: String): String {
    return gradleLocalProperties(rootDir, providers).getProperty(propertyKey)
}

dependencies {
    api(libs.billing.ktx)
}