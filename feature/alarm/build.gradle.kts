plugins {
    id("oldogz.android.feature")
}

android {
    namespace = "com.oldogz.applinkalarm.feature.alarm"
}

dependencies {
    implementation(project(":core:alarm"))
}