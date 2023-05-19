package com.kevinnzou.kmmdeploy

import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.kotlin.dsl.getByType

/**
 * Created By Kevin Zou On 2023/5/16
 */

internal const val GROUP = "kmmdeploy"

internal val Project.publishExt
    get() = extensions.getByType<PublishingExtension>()

internal val Project.kmmDeployExt
    get() = extensions.getByType<KmmDeployExtension>()

internal fun String.splitBySpace() = this.split(" ")