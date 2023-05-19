package com.kevinnzou.kmmdeploy

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider

/**
 * Created By Kevin Zou On 2023/5/18
 */
internal fun Project.buildKMMDebug() = tasks.register("buildKMMDebug") {
    group = GROUP
    description = "Build the debug version of Android AAR and iOS XCFramework for Multiplatform Module"
    dependsOn("bundleDebugAar", "podPublishDebugXCFramework")
}

internal fun Project.buildKMMRelease() = tasks.register("buildKMMRelease") {
    group = GROUP
    description = "Build the release version of Android AAR and iOS XCFramework for Multiplatform Module"
    dependsOn("bundleReleaseAar", "podPublishReleaseXCFramework")
}

internal fun Project.buildKMM(): TaskProvider<Task> {
    buildKMMDebug()
    buildKMMRelease()
    return tasks.register("buildKMM") {
        group = GROUP
        description = "Build both debug and release version of Android AAR and iOS XCFramework for Multiplatform Module"
        dependsOn("buildKMMDebug", "buildKMMRelease")
    }
}