package com.kevinnzou.kmmdeploy.tasks

import com.kevinnzou.kmmdeploy.GROUP
import com.kevinnzou.kmmdeploy.hasJvm
import com.kevinnzou.kmmdeploy.kmmDeployExt
import com.kevinnzou.kmmdeploy.xcFrameworkPath
import com.kevinnzou.kmmdeploy.xcFrameworkReleasePath
import com.kevinnzou.kmmdeploy.xcFrameworkReleaseZipFile
import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import org.gradle.kotlin.dsl.register

/**
 * Created By Kevin Zou On 2023/5/18
 */
internal fun Project.copyKMMOutput() {
    copyAndroidAAR()
    copyXCFramework()
    if (hasJvm) copyJvmJar()
    tasks.register("copyKMMOutput") {
        group = GROUP
        description =
            "Copy the Output of the Kotlin Multiplatform(Android AAR & iOS XCFramework ) to Root Directory"
        dependsOn("copyAndroidAAR", "copyXCFramework")
        if (hasJvm) dependsOn("copyJvmJar")
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

    from(xcFrameworkPath)
    into(layout.projectDirectory.dir("../$outputDir"))

    doLast {
        logger.quiet("iOS XCFramework successfully copied to ${rootProject.rootDir}/${outputDir}")
    }

    dependsOn("buildKMMXCFrameworks")
}

internal fun Project.copyReleaseXCFrameworkToRepo() =
    tasks.register<Copy>("copyReleaseXCFrameworkToRepo") {
        group = GROUP
        description =
            "Copy the release version of iOS XCFramework Output of the Kotlin Multiplatform to PodSpec git submodule"

        val repoName = kmmDeployExt.podspecRepoName.orNull ?: return@register
        from(xcFrameworkReleasePath)
        into(layout.projectDirectory.dir("../$repoName"))

        doLast {
            logger.quiet("iOS XCFramework successfully copied to ${rootProject.rootDir}/${repoName}")
        }

        dependsOn("buildKMMXCFrameworks")
    }

internal fun Project.copyXCFrameworkZipToProject() =
    tasks.register<Copy>("copyXCFrameworkZipToProject") {
        group = GROUP
        description =
            "Copy the iOS XCFramework Zip Output of the Kotlin Multiplatform to Root Directory"

        val outputDir =
            "${kmmDeployExt.outputDirectory.get()}/${kmmDeployExt.podspecRepoName.orNull ?: "kmm-xcframework"}"

        from(xcFrameworkReleaseZipFile.parentFile.path)
        into(layout.projectDirectory.dir("../$outputDir"))

        doLast {
            logger.quiet("iOS XCFramework Zip successfully copied to ${rootProject.rootDir}/${outputDir}")
        }

        dependsOn("zipXCFrameworks")
    }

internal fun Project.copyXCFramework() {
    copyXCFrameworkToProject()
    copyReleaseXCFrameworkToRepo()
    copyXCFrameworkZipToProject()
    tasks.register<Copy>("copyXCFramework") {
        group = GROUP
        description =
            "Copy the iOS XCFramework Output of the Kotlin Multiplatform to Root Directory and PodSpec git submodule"
        dependsOn(
            "copyXCFrameworkToProject",
            "copyReleaseXCFrameworkToRepo",
            "copyXCFrameworkZipToProject"
        )
    }
}

internal fun Project.copyJvmJar() = tasks.register<Copy>("copyJvmJar") {
    group = GROUP
    description = "Copy the Jvm Jar Output of the Kotlin Multiplatform to Root Directory"

    val dest = "../${kmmDeployExt.outputDirectory.get()}/jar"
    from(layout.buildDirectory.dir("libs")) {
        include("*.jar")
    }
    into(layout.projectDirectory.dir(dest))

    doLast {
        logger.quiet("Jvm Jar successfully copied to ${rootProject.rootDir}/${kmmDeployExt.outputDirectory.get()}/aar")
    }

    dependsOn("buildKMMJvmJar")
}