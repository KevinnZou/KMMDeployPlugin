plugins {
    `kotlin-dsl`
    kotlin("jvm") version "1.8.0"
    `maven-publish`
    id("com.gradle.plugin-publish") version "1.1.0"
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8

    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}

group = "io.github.kevinnzou"
version = "1.0.0"

gradlePlugin {
    website.set("https://github.com/KevinnZou/KMMDeployPlugin")
    vcsUrl.set("https://github.com/KevinnZou/KMMDeployPlugin.git")
    plugins {
        register("kmmdeploy-plugin"){
            id = "io.github.kevinnzou.kmmdeploy"
            implementationClass = "com.kevinnzou.kmmdeploy.KmmDeployPlugin"
            displayName = "KmmDeployPlugin"
            description = "A plugin that helps you build the Kotlin Multiplatform Project and Deploy the output efficiently"
            tags.set(listOf("kmm", "kotlin", "multiplatform", "mobile", "android", "ios", "cocoapods", "xcode", "xcframework", "deploy", "publish"))
        }
    }
}
