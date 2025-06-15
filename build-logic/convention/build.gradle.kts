import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
}

group = "com.oldogz.applinkalarm.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
}

dependencies {
    implementation(libs.android.gradlePlugin)
    implementation(libs.kotlin.gradlePlugin)
    compileOnly(libs.compose.compiler.gradle.plugin)
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "oldogz.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }

        register("androidHilt") {
            id = "oldogz.android.hilt"
            implementationClass = "AndroidHiltConventionPlugin"
        }

        register("androidLibrary") {
            id = "oldogz.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }

        register("androidFeature") {
            id = "oldogz.android.feature"
            implementationClass = "AndroidFeatureConventionPlugin"
        }

        register("androidCompose") {
            id = "oldogz.android.compose"
            implementationClass = "AndroidComposeConventionPlugin"
        }

        register("androidKotlinSerialization") {
            id = "oldogz.android.kotlin.serialization"
            implementationClass = "AndroidKotlinSerializationConventionPlugin"
        }

        register("androidRoom") {
            id = "oldogz.android.room"
            implementationClass = "AndroidRoomConventionPlugin"
        }
    }
}