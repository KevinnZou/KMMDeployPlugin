import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("com.android.library")
    id("io.github.kevinnzou.kmmdeploy")
    `maven-publish`
}

group = "io.github.kevinnzou"
version = "0.0.2"

kmmDeploy {
    androidArtifactId.set("kmm-android")
    podspecRepoName.set("kmmdeploy-podspec")
    spm("https://maven.pkg.github.com/KevinnZou/KMMDeployPlugin")
}

kotlin {
    android {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
//        publishLibraryVariants("release")
    }
    jvm()

    val iosTargets = listOf(iosX64(), iosArm64(), iosSimulatorArm64())

    // uncomment it if you don't want to use CococaPods plugin, then you can use this to produce XCFrameworks'
//    val xcf = XCFramework()
//    iosTargets.forEach {
//        it.binaries.framework {
//            baseName = "shared"
//            xcf.add(this)
//        }
//    }

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
        // Publish to Github Maven repository
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/KevinnZou/KMMDeployPlugin")
            credentials {
                username = gradleLocalProperties(rootDir).getProperty("gpr.user")
                password = gradleLocalProperties(rootDir).getProperty("gpr.key")
            }
        }
        // Publish to local
        maven {
            // change URLs to point to your repos, e.g. http://my.org/repo
            val releasesRepoUrl = uri(layout.projectDirectory.dir("../repos/releases"))
            val snapshotsRepoUrl = uri(layout.projectDirectory.dir("../repos/snapshots"))
            url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl
//            credentials {
//                val properties = gradleLocalProperties(rootDir)
//                username = properties["mavenCentralUsername"] as String?
//                password = properties["mavenCentralPassword"] as String?
//            }
        }
    }
}