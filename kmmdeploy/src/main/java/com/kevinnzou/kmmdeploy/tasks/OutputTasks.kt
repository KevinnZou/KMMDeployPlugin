package com.kevinnzou.kmmdeploy.tasks

import com.kevinnzou.kmmdeploy.GROUP
import com.kevinnzou.kmmdeploy.isCocoaPodsApplied
import com.kevinnzou.kmmdeploy.kmmDeployExt
import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import org.gradle.kotlin.dsl.register

/**
 * Created By Kevin Zou On 2023/5/18
 */
internal fun Project.copyKMMOutput() {
    copyAndroidAAR()
    copyXCFramework()
    tasks.register("copyKMMOutput") {
        group = GROUP
        description =
            "Copy the Output of the Kotlin Multiplatform(Android AAR & iOS XCFramework ) to Root Directory"
        dependsOn("buildKMM","copyAndroidAAR", "copyXCFramework")
    }
}

internal fun Project.copyAndroidAAR() = tasks.register<Copy>("copyAndroidAAR") {
    group = GROUP
    description = "Copy the Android AAR Output of the Kotlin Multiplatform to Root Directory"

    val dest = "../${kmmDeployExt.outputDirectory.get()}/aar"
    from(layout.buildDirectory.dir("outputs/aar"))
    into(layout.projectDirectory.dir(dest))

    doLast {
        logger.quiet("Android AAR successfully copied to ${rootProject.rootDir}/${kmmDeployExt.outputDirectory.get()}/aar")
    }

    dependsOn("buildKMMAARs")
}

internal fun Project.copyXCFrameworkToProject() = tasks.register<Copy>("copyXCFrameworkToProject") {
    group = GROUP
    description = "Copy the iOS XCFramework Output of the Kotlin Multiplatform to Root Directory"

    val outputDir =
        "${kmmDeployExt.outputDirectory.get()}/${kmmDeployExt.podspecRepoName.orNull ?: "kmm-xcframework"}"
    if (isCocoaPodsApplied) {
        from(layout.buildDirectory.dir("cocoapods/publish"))
    } else {
        from(layout.buildDirectory.dir("XCFrameworks"))
    }

    into(layout.projectDirectory.dir("../$outputDir"))

    doLast {
        logger.quiet("iOS XCFramework successfully copied to ${rootProject.rootDir}/${outputDir}")
    }

    dependsOn("buildKMMXCFrameworks")
}

internal fun Project.copyXCFrameworkToRepo() = tasks.register<Copy>("copyXCFrameworkToRepo") {
    group = GROUP
    description =
        "Copy the iOS XCFramework Output of the Kotlin Multiplatform to PodSpec git submodule"

    val repoName = kmmDeployExt.podspecRepoName.orNull ?: return@register

    if (isCocoaPodsApplied) {
        from(layout.buildDirectory.dir("cocoapods/publish/release"))
    } else {
        from(layout.buildDirectory.dir("XCFrameworks"))
    }
    into(layout.projectDirectory.dir("../$repoName"))

    doLast {
        logger.quiet("iOS XCFramework successfully copied to ${rootProject.rootDir}/${repoName}")
    }

    dependsOn("buildKMMXCFrameworks")
}

internal fun Project.copyXCFramework() {
    copyXCFrameworkToProject()
    copyXCFrameworkToRepo()
    tasks.register<Copy>("copyXCFramework") {
        group = GROUP
        description =
            "Copy the iOS XCFramework Output of the Kotlin Multiplatform to Root Directory and PodSpec git submodule"
        dependsOn("copyXCFrameworkToProject", "copyXCFrameworkToRepo")
    }
}