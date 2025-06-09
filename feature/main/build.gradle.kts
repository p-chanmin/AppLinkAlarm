plugins {
    id("oldogz.android.feature")
}

android {
    namespace = "com.oldogz.applinkalarm.feature.main"
}
dependencies {
    implementation(project(":feature:alarm"))
}

