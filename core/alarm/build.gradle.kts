plugins {
    id("oldogz.android.library")
}

android {
    namespace = "com.oldogz.core.alarm"
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:data"))
    implementation(project(":core:navigation"))
    implementation(project(":core:firebase"))
    implementation(project(":core:billing"))
}