# KMMDeployPlugin

A plugin that helps you build the Kotlin Multiplatform Project and Deploy the output.
It currently supports the Android AAR and iOS XCFramework which can be used for Cocoapods and Swift
Package Management.

# Features

This plugin mainly provides three features for Kotlin Multiplatform project:

1. Build the KMM project for both Android and iOS artifacts
2. Config the Maven Publish tasks for Android artifacts which is AAR and iOS artifacts which is
   XCFrameworks
3. Deploy the iOS artifacts which is XCFramework to the remote Podspec module or SPM module

## Build the Project

For developers who are trying the KMM first time, it may be confused for them on
how to build the Android and iOS artifacts. Kotlin Multiplatform plugins produced
so many tasks and does not provide a guide on how to build the project.
Thus, this plugin provides several tasks that clearly show you how to produce the
artifacts for Android and iOS.

1. Build the debug/release version of Android AAR and iOS XCFramework for Multiplatform Module

```shell
./gradlew buildKMMDebug
./gradlew buildKMMRelease
```

2. Build both debug and release version of Android AAR and iOS XCFramework for Multiplatform Module

```shell
./gradlew buildKMM
```

3. Build just Android AAR or iOS XCFramework or JVM Jar for Multiplatform Module

```shell
./gradlew buildKMMAARs
./gradlew buildKMMXCFrameworks
./gradlew buildKMMJvmJar
```

After the build tasks finished, you can find the corresponding artifacts at:

1. Android: /build/outputs/aar/***.aar
2. iOS CocoaPods: /build/cocoapods/publish/
3. iOS XCFrameworks: /build/XCFrameworks/
4. Jvm Jar: /build/lib/***.jar

## Config the Maven Publish for Artifacts

### Android

After we get the AAR output of the KMM, we need to deploy it to a remote repository so that
our main project can import and apply it. The normal way it to deploy it to a remote Maven
repository.
Thus, this plugin will automatically create some useful maven publish tasks for output aars.

```shell
./gradlew publishKMMAndroidPublicationToMavenLocal
./gradlew publishKMMAndroidPublicationToMavenRepository
```

Under the group kmmdeploy, there is also a task which will trigger
publishKMMAndroidPublicationToMavenRepository for convenient

```shell
./gradlew publishAAR
```

### iOS SPM

Start from version 2.0.0, this plugin add support for SPM. It will configure the maven publish tasks
for the zip file of XCFrameworks.

```shell
./gradlew publishKMMSpmPublicationToMavenLocal
./gradlew publishKMMSpmPublicationToMavenRepository
```

Under the group kmmdeploy, there is also a task which will trigger
publishKMMSpmPublicationToMavenRepository for convenient

```shell
./gradlew publishXCFrameworks
```

### Jvm

Start from version 2.2.0, this plugin add support for JVM. It will configure the maven publish tasks
for the output of Jvm Jar.

```shell
./gradlew publishKMMJvmPublicationToMavenLocal
./gradlew publishKMMJvmPublicationToMavenRepository
```

Under the group kmmdeploy, there is also a task which will trigger
publishKMMJvmPublicationToMavenRepository for convenient

```shell
./gradlew publishJvmJar
```

Note: the plugin will publish the source code for all targets by default, set 'publishSources' to false if you do not need that.

## Deploy the Artifacts

For iOS artifacts, this plugin supports three ways to deploy:

### Local Directory

If you are at the developing stage and want to debug the current code. Then you can deploy it
locally.
It just copies the output which is XCFramework to an output directory at the root directory.
You will have the freedom on how to use that artifact.

```shell
./gradlew copyXCFrameworkToProject
```

### Cocoapods

It also supports the cocoapods way to deploy artifacts:

1. Create an remote git repo to hold the Podspec file and XCFramework
2. Add this repo as a submodule as the KMM project

```shell
git submodule add https://github.com/url
```

3. Run the tasks produced by this plugin to deploy the latest output to that submodule

```shell
./gradlew deployKMMiOSCocoapods
```

### SPM

As mentioned before, since version 2.0.0, this plugin supports the Swift Package Management.
It will create the zip file of the XCFramework and Package.swift file which contains the package
information.
Then it will publish the zip file to maven when you run deploy task.

To use it, you just need to configure the remote repository url for zip file:

```kotlin
kmmDeploy {
    spm("https://maven.pkg.github.com/KevinnZou/KMMDeployPlugin")
}
```

The Url after spm is the remote repository that will keep the zip file. It will take the first
repository
in Maven Publish configuration by default.

Then run the deploy task to create the Package.swift file and publish the zip file to remote
repository

```shell
./gradlew deployKMMiOSSpm
```

### Deploy Android Artifacts

The deploy task will trigger the maven publish tasks for Android AAR configured at before.
Furthermore, it will copy the artifacts
to root folder of the project so that you can use it for local development.

```shell
./gradlew deployKMMAndroid
```

### Deploy Jvm Artifacts

The deploy task will trigger the maven publish tasks for Jvm Jar configured at before. Furthermore,
it will copy the artifacts
to root folder of the project so that you can use it for local development.

```shell
./gradlew deployKMMJvm
```

### Deploy All Artifacts

Lastly, this plugin creates a task that combine all tasks discussed above and does all things at
once.

```shell
./gradlew deployKMM
```

This task will build the project for both iOS and Android, config the maven publish task,
and copy the artifact to correct directory.

Then, it will create the Package.swift file if you apply SPM support and publish the artifacts
to Maven for both Android and iOS.

# Getting Started

## Prerequisites

Since this project use CocoaPods and Maven for Artifacts deployment of Kotlin Multiplatform project,
the project that want to use this plugin must apply kotlin("native.cocoapods"), `maven-publish`,
and kotlin("multiplatform") plugins.

```kotlin
plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    `maven-publish`
}
```

Notice that： Start from Version 1.3.0， Cocoapods plugin is not required to be applied. If you still
want to
build XCFramework, you can configure the project as follows:

```kotlin
kotlin {

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

Note that, you must specify the baseName for frameworks like above. Otherwise, you will get an
error.

## Apply Plugin

Since this plugin has be uploaded to the Center Gradle Portal, you can just apply it without adding
depending repository.

Current version is **_2.3.0_**

```kotlin
plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    `maven-publish`
    id("io.github.kevinnzou.kmmdeploy") version "2.3.0"
}
```

## Setup Maven Publish Repo

If you want to publish the AAR output to remote Maven repository, then you need to setup the Maven
Repository.

```kotlin
publishing {
    repositories {
        maven {
            url = uri("[input your repository url]")
            credentials { }
        }
    }
}
```

## Setup Cocoapods

As mentioned before, since version 1.3.0, it is not required to use cocoapods plugin.

If you want to use cocoapods as dependency for iOS deploy, then you need to configure the
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
    * ArtifactId for Android artifacts published to Maven
    * Use "kmm-android" by default
    */
   val androidArtifactId: Property<String>

   /**
    * whether to include the source code in published artifacts
    * Use true by default
    */
   val publishSources: Property<Boolean>

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

   /**
    * ArtifactId for Jvm artifacts published to Maven
    * Use "kmm-spm" by default
    */
   val jvmArtifactId: Property<String>

   /**
    * ArtifactId for iOS artifacts published to Maven
    * Use "kmm-spm" by default
    */
   val spmArtifactId: Property<String>

   /**
    * The Name of the Zip file of XCFrameworks
    * Use "$name-xcframework-$version" by default
    */
   val xcFrameworkZipName: Property<String>

   /**
    * Whether need SPM Support
    * Use false by default
    */
   val useSpm: Property<Boolean>

   /**
    * The url of the repository that store the zip file of XCFrameworks
    * which will be used to fill the url filed of Package.swift files
    * Use the url of the first repository in maven-publish extension by default
    */
   val spmUrl: Property<String>

   /**
    * Need SPM Support
    */
   fun Project.spm(url: String? = null) {
      useSpm.set(true)
      url?.apply { spmUrl.set(url) }
   }
}
```

Example configuration will look like:

```kotlin
kmmDeploy {
    androidArtifactId.set("kmm-android")
    podspecRepoName.set("kmmdeploy-podspec")
    spm("https://maven.pkg.github.com/KevinnZou/KMMDeployPlugin")
}
```

# Local Development

This plugin also supports local development

## Android

After you finished development, you can just call copyAndroidAAR task which will build the android
aar
and copy it to the root directory of the project.

```shell
./gradlew copyAndroidAAR
```

Assume your android project is at the same directory with KMM project, then you can depend on the
KMM Aar
produced at last step like that:

```kotlin
dependencies {
    implementation(files("../KMMProject/kmm-outputs/shared-debug.aar"))
}
```

## iOS

For iOS, we provide both cocoapods and SPM way. You can just call copyXCFramework task which will
build the
iOS artifact, zip it, and copy it to the root project. It will also produce the podfile if you apply
cocoapods
plugin. With these outputs, you can directly move the produced XCFramework to Xcode and use it
locally.

```shell
./gradlew copyXCFramework
```

# License

Compose PagingList is distributed under the terms of the Apache License (Version 2.0). See
the [license](https://github.com/KevinnZou/KMMDeployPlugin/blob/main/LICENSE) for more information.
