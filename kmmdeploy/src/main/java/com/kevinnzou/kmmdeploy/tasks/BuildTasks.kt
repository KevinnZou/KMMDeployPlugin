package com.kevinnzou.kmmdeploy.tasks

import com.kevinnzou.kmmdeploy.GROUP
import com.kevinnzou.kmmdeploy.baseName
import com.kevinnzou.kmmdeploy.capitalizeFirstLetter
import com.kevinnzou.kmmdeploy.hasJvm
import com.kevinnzou.kmmdeploy.isCocoaPodsApplied
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider

/**
 * Created By Kevin Zou On 2023/5/18
 */
internal fun Project.buildKMMJvmJar() = tasks.register("buildKMMJvmJar") {
    group = GROUP
    description = "Build the Jvm Jar for Multiplatform Module"
    dependsOn("jvmJar")
}

internal fun Project.buildKMMXCFrameworks(): TaskProvider<Task> {
    buildKMMDebugXCFramework()
    buildKMMReleaseXCFramework()
    return tasks.register("buildKMMXCFrameworks") {
        group = GROUP
        description =
            "Build both debug and release version of iOS XCFrameworks for Multiplatform Module"
        dependsOn("buildKMMDebugXCFramework", "buildKMMReleaseXCFramework")
    }
}

internal fun Project.buildKMMDebugXCFramework() = tasks.register("buildKMMDebugXCFramework") {
    group = GROUP
    description = "Build the debug version of iOS XCFramework for Multiplatform Module"
    if (!isCocoaPodsApplied) {
        val baseName = baseName.capitalizeFirstLetter()
        dependsOn("assemble${baseName}DebugXCFramework")
    } else {
        dependsOn("podPublishDebugXCFramework")
    }
}

internal fun Project.buildKMMReleaseXCFramework() = tasks.register("buildKMMReleaseXCFramework") {
    group = GROUP
    description = "Build the release version of iOS XCFramework for Multiplatform Module"
    if (!isCocoaPodsApplied) {
        val baseName = baseName.capitalizeFirstLetter()
        dependsOn("assemble${baseName}ReleaseXCFramework")
    } else {
        dependsOn("podPublishReleaseXCFramework")
    }
}

internal fun Project.buildKMMDebugAAR() = tasks.register("buildKMMDebugAAR") {
    group = GROUP
    description = "Build the debug version of Android AAR for Multiplatform Module"
    dependsOn("bundleDebugAar")
}

internal fun Project.buildKMMReleaseAAR() = tasks.register("buildKMMReleaseAAR") {
    group = GROUP
    description = "Build the release version of Android AAR for Multiplatform Module"
    dependsOn("bundleReleaseAar")
}

internal fun Project.buildKMMAARs(): TaskProvider<Task> {
    buildKMMDebugAAR()
    buildKMMReleaseAAR()
    return tasks.register("buildKMMAARs") {
        group = GROUP
        description =
            "Build both the debug and release version of Android AAR for Multiplatform Module"
        dependsOn("buildKMMDebugAAR", "buildKMMReleaseAAR")
    }
}

internal fun Project.buildKMMDebug() = tasks.register("buildKMMDebug") {
    group = GROUP
    description =
        "Build the debug version of Android AAR and iOS XCFramework for Multiplatform Module"
    dependsOn("buildKMMDebugAAR", "buildKMMDebugXCFramework")
    if (hasJvm) dependsOn("buildKMMJvmJar")
}

internal fun Project.buildKMMRelease() = tasks.register("buildKMMRelease") {
    group = GROUP
    description =
        "Build the release version of Android AAR and iOS XCFramework for Multiplatform Module"
    dependsOn("buildKMMReleaseAAR", "buildKMMReleaseXCFramework")
    if (hasJvm) dependsOn("buildKMMJvmJar")
}

internal fun Project.buildKMM(): TaskProvider<Task> {
    buildKMMDebug()
    buildKMMRelease()
    buildKMMAARs()
    buildKMMXCFrameworks()
    if (hasJvm) buildKMMJvmJar()
    return tasks.register("buildKMM") {
        group = GROUP
        description =
            "Build both debug and release version of Android AAR and iOS XCFramework for Multiplatform Module"
        dependsOn("buildKMMDebug", "buildKMMRelease")
    }
}