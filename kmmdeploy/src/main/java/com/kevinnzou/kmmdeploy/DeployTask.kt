package com.kevinnzou.kmmdeploy

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider

/**
 * Created By Kevin Zou On 2023/5/18
 */
internal fun Project.deployKMM(): TaskProvider<Task> {
    return tasks.register("deployKMM") {
        group = GROUP
        description = "Deploy the KMM Artifacts"
        dependsOn("deployKMMAndroid", "deployKMMiOS")
        if (hasJvm) dependsOn("deployKMMJvm")
    }
}

internal fun Project.deployKMMJvm(publishTask: TaskProvider<Task>) =
    tasks.register("deployKMMJvm") {
        group = GROUP
        description = "Deploy the KMM Jvm Artifact(JAR)"
        dependsOn("copyJvmJar")
        dependsOn(publishTask)
    }

internal fun Project.deployKMMAndroid(publishTask: TaskProvider<Task>) =
    tasks.register("deployKMMAndroid") {
        group = GROUP
        description = "Deploy the KMM Android Artifact(AAR)"
        dependsOn("copyAndroidAAR")
        dependsOn(publishTask)
    }

internal fun Project.deployKMMiOS(task: TaskProvider<Task>) {
    tasks.register("deployKMMiOS") {
        group = GROUP
        description = "Deploy the KMM iOS Artifact(XCFrameworks)"
        dependsOn(task)
    }
}

internal fun Project.deployKMMiOSCocoapods() = tasks.register("deployKMMiOSCocoapods") {
    group = GROUP
    description = "Deploy the KMM iOS Artifact(XCFrameworks) for Cocoapods"
    dependsOn("copyXCFramework")
}

internal fun Project.deployKMMiOSSpm() = tasks.register("deployKMMiOSSpm") {
    group = GROUP
    description =
        "Deploy the KMM iOS Artifact(XCFrameworks.zip & Package.swift) for Swift Package Management"
    dependsOn("copyXCFramework")
}