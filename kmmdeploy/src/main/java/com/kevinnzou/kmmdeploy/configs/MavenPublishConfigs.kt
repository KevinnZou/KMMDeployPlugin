package com.kevinnzou.kmmdeploy.configs

import com.kevinnzou.kmmdeploy.androidPublishName
import com.kevinnzou.kmmdeploy.kmmDeployExt
import com.kevinnzou.kmmdeploy.publishExt
import com.kevinnzou.kmmdeploy.publishingRepos
import com.kevinnzou.kmmdeploy.spmPublishName
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.bundling.Zip
import org.gradle.configurationcache.extensions.capitalized

/**
 * Created By Kevin Zou On 2023/5/18
 */
internal fun Project.configAndroidKMMPublish(publishTask: TaskProvider<Task>? = null) {
    val androidArtifactId = kmmDeployExt.androidArtifactId.get()
    val version = kmmDeployExt.version.getOrElse(project.version as String)
    val publicationName = androidPublishName
    publishExt.publications.create(publicationName, MavenPublication::class.java) {
        this.version = version
        this.groupId = project.group as String
        artifactId = androidArtifactId
        val archiveProvider = project.tasks.named("bundleReleaseAar", Zip::class.java).flatMap {
            it.archiveFile
        }
        artifact(archiveProvider) {
            extension = "aar"
        }
//        artifact("build/outputs/aar/shared-release.aar")
    }

    configPublishDependency(publicationName, publishTask)
}

internal fun Project.configSpmKMMPublish(publishTask: TaskProvider<Task>? = null) {
    val spmArtifactId = kmmDeployExt.spmArtifactId.get()
    val version = kmmDeployExt.version.getOrElse(project.version as String)
    val publicationName = spmPublishName
    publishExt.publications.create(publicationName, MavenPublication::class.java) {
        this.version = version
        this.groupId = project.group as String
        artifactId = spmArtifactId

        val archiveProvider =
            project.tasks.named("zipXCFrameworkRelease", Zip::class.java).flatMap {
                it.archiveFile
            }
//        artifact("build/kmmDeploy/${xcFrameworkReleaseZipFile.name}")
        artifact(archiveProvider) {
            extension = "zip"
        }
    }

    configPublishDependency(publicationName, publishTask)
}

internal fun Project.configPublishDependency(
    publicationName: String,
    publishTask: TaskProvider<Task>? = null
) {
    val publishRemoteTaskNames = publishingRepos.map { repo ->
        val repoName = repo.name.capitalized()
        "publish${publicationName}PublicationTo${repoName}Repository"
    }

    publishTask?.configure {
        dependsOn(publishRemoteTaskNames)
    }
}