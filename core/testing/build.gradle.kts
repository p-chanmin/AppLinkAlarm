plugins {
    id("oldogz.android.library")
}

android {
    namespace = "com.oldogz.core.testing"

    packaging {
        resources {
            excludes += "/META-INF/**"
        }
    }
}

dependencies {
    api(libs.junit4)
    api(libs.junit.vintage.engine)
    api(libs.kotlin.test)
    api(libs.mockk)
    api(libs.turbine)
    api(libs.coroutines.test)
    api(libs.androidx.compose.ui.test)
    implementation(libs.hilt.android.testing)
    implementation(libs.androidx.compose.ui.test)
}