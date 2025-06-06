package com.dev

import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

internal fun Project.configureCoroutineAndroid() {
    dependencies {
        "implementation"(libs.findLibrary("coroutines.core").get())
        "implementation"(libs.findLibrary("coroutines.android").get())
        "testImplementation"(libs.findLibrary("coroutines.test").get())
    }
}