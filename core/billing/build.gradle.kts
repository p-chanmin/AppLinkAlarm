plugins {
    id("oldogz.android.library")
}

android {
    namespace = "com.oldogz.core.billing"
}

dependencies {
    implementation(libs.billing)
}