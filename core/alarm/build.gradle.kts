plugins {
    id("oldogz.android.library")
    id("oldogz.android.kotlin.serialization")
}

android {
    namespace = "com.oldogz.core.alarm"
}

dependencies {
    api(libs.androidx.work.runtime.ktx)
    implementation(project(":core:model"))
    implementation(project(":core:data"))
    implementation(project(":core:navigation"))
    implementation(project(":core:firebase"))
    implementation(project(":core:billing"))
}