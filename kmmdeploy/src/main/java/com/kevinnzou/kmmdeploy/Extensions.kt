package com.kevinnzou.kmmdeploy

import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
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
    get() = kotlinExt.iOSTargets.getOrNull(0)?.binaries?.getFramework(
        NativeBuildType.DEBUG
    )?.baseName ?: project.name

internal fun String.splitBySpace() = this.split(" ")

internal fun String.capitalizeFirstLetter() = this.replaceFirstChar{ it.uppercaseChar()}