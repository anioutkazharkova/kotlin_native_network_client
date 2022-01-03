pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

rootProject.name = "KMM_Network_Client"
include(":shared")
includeBuild("convention-plugins")