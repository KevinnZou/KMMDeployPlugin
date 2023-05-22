# KMMDeployPlugin
A plugin that helps you build the Kotlin Multiplatform Project and Deploy the output.
It currently supports the Android AAR and iOS XCFramework which can be used for Cocoapods.

# Features
This plugin mainly provides three features for Kotlin Multiplatform project:
1. Build the KMM project for both Android and iOS artifacts
2. Config the Maven Publish tasks for Android artifacts which is AAR
3. Deploy the iOS artifacts which is XCFramework to the remote Podspec module

## Build the Project
For developers who are trying the KMM first time, it may be confused for them on 
how to build the Android and iOS artifacts. Kotlin Multiplatform plugins produced
so many tasks and does not provide a guide on how to build the project. 
Thus, this plugin provides several tasks that clearly show you how to produce the 
artifacts for Android and iOS.

1. Build the debug version of Android AAR and iOS XCFramework for Multiplatform Module
```shell
./gradlew buildKMMDebug
```
2. Build the release version of Android AAR and iOS XCFramework for Multiplatform Module
```shell
./gradlew buildKMMRelease
```
3. Build both debug and release version of Android AAR and iOS XCFramework for Multiplatform Module
```shell
./gradlew buildKMM
```
4. Build just Android AAR or iOS XCFramework for Multiplatform Module
```shell
./gradlew buildKMMAARs
./gradlew buildKMMXCFrameworks
```

## Config the Maven Publish for Android AAR
After we get the AAR output of the KMM, we need to deploy it to a remote repository so that 
our main project can import and apply it. The normal way it to deploy it to a remote Maven repository.
Thus, this plugin will automatically create some useful maven publish tasks for output aars.
1. Publish the debug version of the Android AAR which is at /build/outputs/aar/shared-debug.aar
```shell
./gradlew publishKMMAndroidDebugPublicationToMavenLocal
./gradlew publishKMMAndroidDebugPublicationToMavenRepository
```
2. Publish the release version of the Android AAR which is at /build/outputs/aar/shared-release.aar
```shell
./gradlew publishKMMAndroidReleasePublicationToMavenLocal
./gradlew publishKMMAndroidReleasePublicationToMavenRepository
```

## Deploy the iOS Artifact XCFramework
For iOS artifact, this plugin supports two ways to deploy:

### Local Directory
It just copies the output which is XCFramework to an output directory at the root directory. You will have
the freedom on how to use that artifact
```shell
./gradlew copyXCFrameworkToProject
```

### Cocoapods
It also supports the cocoapods way to deploy artifacts:

1. Create an remote git repo to hold the Podspec file and XCFramework
2. Add this repo as a submodule as the KMM project
```shell
git git submodule add https://github.com/url
```
3. Run the tasks produced by this plugin to copy the latest output to that submodule
```shell
./gradlew copyXCFrameworkToRepo
```

## Deploy All Artifacts
Lastly, this plugin creates a task that combine all tasks discussed above and does all things at once.
```shell
./gradlew deployKMM
```
This task will build the project for both iOS and Android, config the maven publish task, 
and copy the artifact to correct directory.

However, for safety concerns, it will not publish the artifacts for you automatically. If you want to do
that, simply call the maven publish tasks produced by this plugin and your artifacts will be officially deployed.

# Getting Started

## Prerequisites
Since this project use CocoaPods and Maven for Artifacts deployment of Kotlin Multiplatform project, the project that want to use this plugin
must apply kotlin("native.cocoapods"), `maven-publish`, and kotlin("multiplatform") plugins.
```kotlin
plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    `maven-publish`
}
```

## Apply Plugin
Since this plugin has be uploaded to the Center Gradle Portal, you can just apply it without adding depending repository.

Current version is **_1.3.0_**
```kotlin
plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    `maven-publish`
    id("io.github.kevinnzou.kmmdeploy")  version "1.3.0"
}
```

## Setup Maven Publish Repo
If you want to publish the AAR output to remote Maven repository, then you need to setup the Maven Repository.
```kotlin
publishing {
    repositories {
        maven {
            url = uri("[input your repository url]")
            credentials {  }
        }
    }
}
```

## Setup Cocoapods
The build task of this plugin relies on cocoapods configuration. Thus, you need to configure the 
necessary fields to make it deployable with Cocoapods.
```kotlin
kotlin {
    // ...
    
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
}
```

Since Version 1.3.0, this plugin supports the build of iOS XCFrameworks without applying CocoaPods Plugin.
```kotlin
kotlin {
    // ...
    
    val iosTargets = listOf(iosX64(), iosArm64(), iosSimulatorArm64())
    val xcf = XCFramework()
    iosTargets.forEach {
        it.binaries.framework {
            baseName = "shared"
            xcf.add(this)
        }
    }
}
```
Note that, you must specify the baseName for frameworks like above. Otherwise, you will get an error.

## Config Plugin Properties
This plugin provides an extension for config the properties:
```kotlin
interface KmmDeployExtension {
    /**
     * Version for artifacts published to Maven
     * Use gradle version by default
     */
    val version: Property<String>
    /**
     * ArtifactId for artifacts published to Maven
     * Use "kmm-android" by default
     */
    val androidArtifactId: Property<String>
    /**
     * The name of the submodule that manages the Podspec file and XCFramework
     * If you do not want to use submodule, please don't specify this property
     */
    val podspecRepoName: Property<String>
    /**
     * The name of the output directory for artifacts of Android and iOS
     * Use "KMMOutputs" by default
     */
    val outputDirectory: Property<String>
}
```
Example configuration will look like:
```kotlin
kmmDeploy {
    androidArtifactId.set("kmm-android")
    podspecRepoName.set("kmmdeploy-podspec")
}
```

# License
Compose PagingList is distributed under the terms of the Apache License (Version 2.0). See the [license](https://github.com/KevinnZou/KMMDeployPlugin/blob/main/LICENSE) for more information.
