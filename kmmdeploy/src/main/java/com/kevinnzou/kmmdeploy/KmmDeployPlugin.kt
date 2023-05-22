package com.kevinnzou.kmmdeploy


import com.kevinnzou.kmmdeploy.configs.configAndroidKMMPublish
import com.kevinnzou.kmmdeploy.tasks.buildKMM
import com.kevinnzou.kmmdeploy.tasks.cleanKMMOutputs
import com.kevinnzou.kmmdeploy.tasks.copyKMMOutput
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.create

class KmmDeployPlugin : Plugin<Project> {
    override fun apply(target: Project): Unit = with(target) {
        val kmmDeployExt = extensions.create<KmmDeployExtension>("kmmDeploy").apply {
            androidArtifactId.convention("kmm-android")
            outputDirectory.convention("KMMOutputs")
            version.convention(project.version.toString())
        }
        afterEvaluate {
            val buildTask = buildKMM()
            copyKMMOutput()
            configAndroidKMMPublish(buildTask)
            cleanKMMOutputs()
            deployKMM()
        }
    }
}

interface KmmDeployExtension {
    /**
     * Version for artifacts published to Maven
     * Use gradle version by default
     */
    val version: Property<String>

    /**
     * ArtifactId for artifacts published to Maven
     * Use "kmm-android" by default
     */
    val androidArtifactId: Property<String>

    /**
     * The name of the submodule that manages the Podspec file and XCFramework
     * If you do not want to use submodule, please don't specify this property
     */
    val podspecRepoName: Property<String>

    /**
     * The name of the output directory for artifacts of Android and iOS
     * Use "KMMOutputs" by default
     */
    val outputDirectory: Property<String>
}