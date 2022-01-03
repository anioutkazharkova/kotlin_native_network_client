pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

rootProject.name = "KMM_Network_Client"
include(":kn_network_client")
includeBuild("convention-plugins")