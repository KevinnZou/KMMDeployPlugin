pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
    includeBuild("kmmdeploy")
}

fun getProperty(propertyName: String, defaultValue: String?): String? {
    val properties = java.util.Properties()
    val inputStream = File(rootDir, "local.properties").inputStream()
    properties.load(inputStream)
    return properties.getProperty(propertyName, defaultValue)
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
//        maven("repos/releases")
//        maven {
//            url = uri("https://maven.pkg.github.com/KevinnZou/KMMDeployPlugin")
//            credentials {
//                username = getProperty("gpr.user","")
//                password = getProperty("gpr.key","")
//            }
//        }
    }
}

rootProject.name = "KMMDeployPlugin"
include(":androidApp")
include(":shared")
//include(":kmmdeploy")
