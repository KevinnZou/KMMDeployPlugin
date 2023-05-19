plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("com.android.library")
    id("io.github.kevinnzou.kmmdeploy")
    `maven-publish`
}

group = "com.netease.yanxuan"
version = "0.0.1-SNAPSHOT"

kmmDeploy {
    androidArtifactId.set("kmm-android")
    podspecRepoName.set("kmmdeploy-podspec")
}

kotlin {
    android {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    cocoapods {
        summary = "Some description for the Shared Module"
        homepage = "https://github.com/KevinnZou/kmmdeploy-podspec"
        source = "{:git=> 'https://github.com/KevinnZou/kmmdeploy-podspec.git' }"
        version = project.version.toString()
        ios.deploymentTarget = "14.1"
        podfile = project.file("../iosApp/Podfile")
        framework {
            baseName = "shared"
        }
    }
    
    sourceSets {
        val commonMain by getting
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting
        val androidUnitTest by getting
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
        }
        val iosX64Test by getting
        val iosArm64Test by getting
        val iosSimulatorArm64Test by getting
        val iosTest by creating {
            dependsOn(commonTest)
            iosX64Test.dependsOn(this)
            iosArm64Test.dependsOn(this)
            iosSimulatorArm64Test.dependsOn(this)
        }
    }
}

android {
    namespace = "com.kevinnzou.kmmdeployplugin"
    compileSdk = 33
    defaultConfig {
        minSdk = 24
    }
}

publishing {
    repositories {
        maven {
            // change URLs to point to your repos, e.g. http://my.org/repo
            val releasesRepoUrl = uri(layout.projectDirectory.dir("repos/releases"))
            val snapshotsRepoUrl = uri(layout.projectDirectory.dir("repos/snapshots"))
            url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl
//            credentials {
//                val properties = gradleLocalProperties(rootDir)
//                username = properties["mavenCentralUsername"] as String?
//                password = properties["mavenCentralPassword"] as String?
//            }
        }
    }
}