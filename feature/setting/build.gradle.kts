plugins {
    id("oldogz.android.feature")
}

android {
    namespace = "com.oldogz.applinkalarm.feature.setting"
}

dependencies {
    implementation(project(":core:alarm"))
}