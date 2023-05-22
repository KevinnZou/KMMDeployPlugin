package com.kevinnzou.kmmdeploy.tasks

import com.kevinnzou.kmmdeploy.GROUP
import com.kevinnzou.kmmdeploy.kmmDeployExt
import org.gradle.api.Project
import org.gradle.api.tasks.Delete
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.register
import java.io.File

/**
 * Created By Kevin Zou On 2023/5/16
 */
internal fun Project.cleanKMMOutputs(): TaskProvider<Delete> {
    val task = tasks.register<Delete>("cleanKMMOutputs") {
        group = GROUP
        description = "clean the output files produced by kotlin multiplatform module"
        delete(File("${rootProject.rootDir}/${kmmDeployExt.outputDirectory.get()}"))
    }
    tasks.named("clean").configure {
        dependsOn(task)
    }
    return task
}