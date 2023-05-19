pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
    includeBuild("kmmdeploy")
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "KMMDeployPlugin"
include(":androidApp")
include(":shared")
//include(":kmmdeploy")
