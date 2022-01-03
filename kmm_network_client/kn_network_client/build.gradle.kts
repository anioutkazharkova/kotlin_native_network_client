import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("com.android.library")
    id("convention.publication")
}

group = "io.github.anioutkazharkova"
version = "1.0.0"

repositories {
    mavenCentral()
}


kotlin {
    android {
        publishLibraryVariants("release", "debug")
    }

    val iosTarget: (String, KotlinNativeTarget.() -> Unit) -> KotlinNativeTarget = when {
        System.getenv("SDK_NAME")?.startsWith("iphoneos") == true -> ::iosArm64
        System.getenv("NATIVE_ARCH")?.startsWith("arm") == true -> ::iosSimulatorArm64
        else -> ::iosX64
    }

    iosTarget("ios") {
        binaries {
            framework {
                baseName = "kn_network_client"
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.0")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.1")
                //implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${findProperty("version.kotlinx.serialization")}")
            }
        }
        val commonTest by getting
        val androidMain by getting {
            dependencies {
                implementation("com.squareup.okhttp3:okhttp:4.9.3")
                implementation("com.squareup.okhttp3:okhttp-bom:4.9.3")

                // define any required OkHttp artifacts without version
                implementation("com.squareup.okhttp3:okhttp")
                implementation("com.squareup.okhttp3:logging-interceptor")
            }
        }
        val androidTest by getting {
            dependencies {
            }
        }
        val iosMain by getting
        val iosTest by getting
    }
}

android {
    compileSdk = 31
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 21
        targetSdk = 31
    }
}