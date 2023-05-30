package com.kevinnzou.kmmdeploy.dependencymanager

import com.kevinnzou.kmmdeploy.GROUP
import com.kevinnzou.kmmdeploy.baseName
import com.kevinnzou.kmmdeploy.baseVersion
import com.kevinnzou.kmmdeploy.kmmDeployExt
import com.kevinnzou.kmmdeploy.packageSwiftFilePath
import com.kevinnzou.kmmdeploy.spmRepoUrl
import com.kevinnzou.kmmdeploy.xcFrameworkReleaseZipFile
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider
import java.io.ByteArrayOutputStream
import java.io.File
import java.nio.charset.Charset

/**
 * Created By Kevin Zou On 2023/5/23
 */
internal fun Project.createPackageSwiftFile(
    publishTask: TaskProvider<Task>? = null,
    deployTask: TaskProvider<Task>? = null
) {
    val createPackageSwiftTask = tasks.register("createPackageSwiftFile") {
        group = GROUP
        description = "Create the Package.swift file for SPM publications"

        doLast {
            val packageSwiftFile = file(packageSwiftFilePath)
            val checksum = getPackageSwiftChecksum(xcFrameworkReleaseZipFile)

            val groupUrl = project.group.toString().replace(".", "/")
            val spmArtifactId = kmmDeployExt.spmArtifactId.getOrElse("")
            val url =
                "$spmRepoUrl/$groupUrl/${spmArtifactId}/$baseVersion/${spmArtifactId}-$version.zip"

            packageSwiftFile.delete()
            packageSwiftFile.writeText(
                getPackageSwiftFileText(
                    baseName,
                    url,
                    checksum
                )
            )
        }
        dependsOn("zipXCFrameworks")
    }

    // Do not depend on publish tasks if it is called by createPackageSwiftFile.
    if (!gradle.startParameter.taskNames.contains("createPackageSwiftFile")) {
        publishTask?.let {
            createPackageSwiftTask.configure {
                dependsOn(it)
            }
        }
    }
    deployTask?.configure { dependsOn(createPackageSwiftTask) }
}


private fun Project.getPackageSwiftChecksum(filePath: File): String {
    val os = ByteArrayOutputStream()
    exec {
        commandLine(
            "swift",
            "package",
            "compute-checksum",
            filePath.path
        )
        standardOutput = os
    }

    return os.toByteArray().toString(Charset.defaultCharset()).trim()
}

private fun getPackageSwiftFileText(
    frameworkName: String,
    frameworkUri: String,
    checksum: String
) = """
        // swift-tools-version:5.3
        import PackageDescription

        let package = Package(
            name: "$frameworkName",
            platforms: [
                .iOS(.v13)
            ],
            products: [
                .library(
                    name: "$frameworkName",
                    targets: ["$frameworkName"])
            ],
            dependencies: [],
            targets: [
                .binaryTarget(
                    name: "$frameworkName",
                    url: "$frameworkUri",
                    checksum: "$checksum"
                )
            ]
        )
    """.trimIndent()