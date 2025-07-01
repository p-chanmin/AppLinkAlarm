plugins {
    id("oldogz.android.feature")
}

android {
    namespace = "com.oldogz.applinkalarm.feature.main"
}
dependencies {
    implementation(libs.play.app.update.ktx)
    implementation(project(":feature:alarm"))
    implementation(project(":feature:setting"))
}