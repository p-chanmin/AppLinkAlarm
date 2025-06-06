plugins {
    id("oldogz.android.library")
}

android {
    namespace = "com.oldogz.core.data"
}

dependencies {
    implementation(project(":core:model"))
}