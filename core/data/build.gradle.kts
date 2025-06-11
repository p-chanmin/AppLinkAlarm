plugins {
    id("oldogz.android.library")
    id("oldogz.android.kotlin.serialization")
}

android {
    namespace = "com.oldogz.core.data"
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:database"))
}