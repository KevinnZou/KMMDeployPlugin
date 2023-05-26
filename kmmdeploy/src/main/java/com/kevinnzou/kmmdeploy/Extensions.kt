package com.kevinnzou.kmmdeploy

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType

/**
 * Created By Kevin Zou On 2023/5/16
 */

internal const val GROUP = "kmmdeploy"

internal val Project.publishExt
    get() = extensions.getByType<PublishingExtension>()

internal val Project.kmmDeployExt
    get() = extensions.getByType<KmmDeployExtension>()

internal val Project.kotlinExt
    get() = extensions.getByType<KotlinMultiplatformExtension>()

internal val KotlinMultiplatformExtension.iOSTargets
    get() = targets.withType(KotlinNativeTarget::class.java)
        .filter { it.konanTarget.family.isAppleFamily }

internal val Project.isCocoaPodsApplied
    get() = plugins.hasPlugin("org.jetbrains.kotlin.native.cocoapods")

internal val Project.baseName
    get() = run {
        try {
            kotlinExt.iOSTargets.getOrNull(0)?.binaries?.getFramework(
                NativeBuildType.DEBUG
            )?.baseName ?: project.name
        } catch (exception: Exception) {
            project.name
        }
    }

internal val Project.androidPublishName
    get() = "KMMAndroid"

internal val Project.spmPublishName
    get() = "KMMSpm"

internal val Project.xcFrameworkPath
    get() = run {
        if (isCocoaPodsApplied) {
            "$buildDir/cocoapods/publish"
        } else {
            "$buildDir/XCFrameworks"
        }
    }

internal val Project.xcFrameworkDebugPath
    get() = "$xcFrameworkPath/debug"

internal val Project.xcFrameworkReleasePath
    get() = "$xcFrameworkPath/release"

internal val Project.xcFrameworkDebugFilePath
    get() = "$xcFrameworkPath/debug/$baseName.xcframework"

internal val Project.xcFrameworkReleaseFilePath
    get() = "$xcFrameworkPath/release/$baseName.xcframework"

internal val Project.baseVersion
    get() = kmmDeployExt.version.getOrElse(version as String)

internal val Project.xcFrameworkDebugZipFile
    get() = run {
        val prefix = kmmDeployExt.xcFrameworkZipName.orNull ?: "$baseName-xcframework"
        val fileName = "$prefix-debug-$baseVersion.zip"
        val dest = "$buildDir/kmmDeploy/$fileName"
        file(dest)
    }

internal val Project.xcFrameworkReleaseZipFile
    get() = run {
        val prefix = kmmDeployExt.xcFrameworkZipName.orNull ?: "$baseName-xcframework"
        val fileName = "$prefix-$baseVersion.zip"
        val dest = "$buildDir/kmmDeploy/$fileName"
        file(dest)
    }

internal val Project.packageSwiftFilePath
    get() = "$rootDir/Package.swift"

internal val Project.publishingRepos
    get() = publishExt.repositories.filterIsInstance<MavenArtifactRepository>()

internal val Project.spmRepoUrl
    get() = run {
        kmmDeployExt.spmUrl.orNull ?: publishingRepos.firstOrNull()?.url?.toString() ?: ""
    }

internal val Project.spm
    get() = kmmDeployExt.useSpm.get()

internal fun TaskProvider<Task>.beDependedBy(task: TaskProvider<Task>): TaskProvider<Task> {
    task.configure {
        this.dependsOn(this@beDependedBy)
    }
    return this
}

internal fun TaskProvider<Task>.beDependedByIf(
    rule: Boolean,
    task: TaskProvider<Task>
): TaskProvider<Task> {
    if (!rule) return this
    task.configure {
        this.dependsOn(this@beDependedByIf)
    }
    return this
}

internal fun String.splitBySpace() = this.split(" ")

internal fun String.capitalizeFirstLetter() = this.replaceFirstChar { it.uppercaseChar() }