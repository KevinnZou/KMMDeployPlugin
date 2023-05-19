package com.kevinnzou.kmmdeploy

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.TaskProvider

/**
 * Created By Kevin Zou On 2023/5/18
 */
internal fun Project.configAndroidKMMPublish(dependTask: TaskProvider<Task>) {
    val androidArtifactId = kmmDeployExt.androidArtifactId.get()
    val version = kmmDeployExt.version.getOrElse(project.version as String)
    val publicationName = "KMMAndroid"
    publishExt.publications.create("${publicationName}Debug", MavenPublication::class.java) {
        this.version = version
        this.groupId = project.group as String
        artifactId = androidArtifactId
        artifact("build/outputs/aar/shared-debug.aar")
    }
    publishExt.publications.create("${publicationName}Release", MavenPublication::class.java) {
        this.version = version
        this.groupId = project.group as String
        artifactId = androidArtifactId
        artifact("build/outputs/aar/shared-release.aar")
    }

    val publishTaskNames = mutableListOf(
        "publish${publicationName}DebugPublicationToMavenLocal",
        "publish${publicationName}ReleasePublicationToMavenLocal",
    )
    if (publishExt.repositories.size > 0) {
        publishTaskNames.add(
            "publish${publicationName}DebugPublicationToMavenRepository",
        )
        publishTaskNames.add(
            "publish${publicationName}ReleasePublicationToMavenRepository"
        )
    }
    publishTaskNames.forEach {
        tasks.named(it).configure {
            dependsOn(dependTask)
        }
    }

}