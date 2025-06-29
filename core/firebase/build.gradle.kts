plugins {
    id("oldogz.android.library")
    id("oldogz.android.compose")
}

android {
    namespace = "com.oldogz.core.firebase"
}

dependencies {
    api(platform(libs.firebase.bom))
    api(libs.firebase.analytics.ktx)
    api(libs.firebase.crashlytics.ktx)
}