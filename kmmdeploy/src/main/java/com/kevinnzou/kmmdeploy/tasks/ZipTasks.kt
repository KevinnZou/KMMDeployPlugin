package com.kevinnzou.kmmdeploy.tasks

import com.kevinnzou.kmmdeploy.GROUP
import com.kevinnzou.kmmdeploy.xcFrameworkDebugFilePath
import com.kevinnzou.kmmdeploy.xcFrameworkDebugZipFile
import com.kevinnzou.kmmdeploy.xcFrameworkReleaseFilePath
import com.kevinnzou.kmmdeploy.xcFrameworkReleaseZipFile
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.bundling.Zip
import org.gradle.kotlin.dsl.register

/**
 * Created By Kevin Zou On 2023/5/23
 */

internal fun Project.zipXCFrameworkDebug() = tasks.register<Zip>("zipXCFrameworkDebug") {
    group = GROUP
    description =
        "Zip the Debug version of iOS XCFramework Output of the Kotlin Multiplatform to Root Directory"

    val zipFile = xcFrameworkDebugZipFile

    from(xcFrameworkDebugFilePath)
    destinationDirectory.set(zipFile.parentFile)
    archiveFileName.set(zipFile.name)

    doLast {
        logger.quiet("Debug Version of XCFrameworks successfully zipped to ${zipFile.path}")
    }

    dependsOn("buildKMMDebugXCFramework")
}

internal fun Project.zipXCFrameworkRelease() = tasks.register<Zip>("zipXCFrameworkRelease") {
    group = GROUP
    description =
        "Zip the Release version of iOS XCFramework Output of the Kotlin Multiplatform to Root Directory"

    val zipFile = xcFrameworkReleaseZipFile

    from(xcFrameworkReleaseFilePath)
    destinationDirectory.set(zipFile.parentFile)
    archiveFileName.set(zipFile.name)

    doLast {
        logger.quiet("Release Version of XCFrameworks successfully zipped to ${zipFile.path}")
    }

    dependsOn("buildKMMReleaseXCFramework")
}

internal fun Project.zipXCFrameworks(): TaskProvider<Task> {
    zipXCFrameworkDebug()
    zipXCFrameworkRelease()

    return tasks.register("zipXCFrameworks") {
        group = GROUP
        description = "Zip the iOS XCFramework Output of the Kotlin Multiplatform to Root Directory"

        dependsOn("zipXCFrameworkDebug", "zipXCFrameworkRelease")
    }
}