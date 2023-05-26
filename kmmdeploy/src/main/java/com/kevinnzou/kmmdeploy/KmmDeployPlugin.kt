package com.kevinnzou.kmmdeploy


import com.kevinnzou.kmmdeploy.configs.configAndroidKMMPublish
import com.kevinnzou.kmmdeploy.configs.configSpmKMMPublish
import com.kevinnzou.kmmdeploy.dependencymanager.createPackageSwiftFile
import com.kevinnzou.kmmdeploy.tasks.buildKMM
import com.kevinnzou.kmmdeploy.tasks.cleanKMMOutputs
import com.kevinnzou.kmmdeploy.tasks.copyKMMOutput
import com.kevinnzou.kmmdeploy.tasks.zipXCFrameworks
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.create

class KmmDeployPlugin : Plugin<Project> {
    override fun apply(target: Project): Unit = with(target) {
        extensions.create<KmmDeployExtension>("kmmDeploy").apply {
            androidArtifactId.convention("kmm-android")
            spmArtifactId.convention("kmm-spm")
            outputDirectory.convention("kmm-outputs")
            useSpm.convention(false)
        }
        afterEvaluate {
            applyBuildTasks()
            applyOutputTasks()
            applyDependencyManagerAndDeployTasks()
        }
    }

    private fun Project.applyBuildTasks() {
        buildKMM()
    }

    private fun Project.applyOutputTasks() {
        copyKMMOutput()
        cleanKMMOutputs()
    }

    private fun Project.applyDependencyManagerAndDeployTasks() {
        val zipTask = zipXCFrameworks()

        // apply the iOS deploy tasks
        val task = if (spm) {
            val spmPublishTask = tasks.register("publishXCFrameworks") {
                group = GROUP
                description =
                    "Publish the iOS XCFramework Output of the Kotlin Multiplatform to Maven Repository"

                dependsOn(zipTask)
            }
            val deployTask = deployKMMiOSSpm()
            configSpmKMMPublish(spmPublishTask)
            createPackageSwiftFile(spmPublishTask, deployTask)
            deployTask
        } else {
            deployKMMiOSCocoapods()
        }
        deployKMMiOS(task)

        // apply the Android deploy tasks
        val aarPublishTask = tasks.register("publishAAR") {
            group = GROUP
            description =
                "Publish the Android AAR Output of the Kotlin Multiplatform To Maven Repository"
        }
        configAndroidKMMPublish(aarPublishTask)
        deployKMMAndroid(aarPublishTask)

        // Deploy All Artifacts
        deployKMM()
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

    /**
     * The url of the repository that store the zip file of XCFrameworks
     * which will be used to fill the url filed of Package.swift files
     * Use the url of the first repository in maven-publish extension by default
     */
    val spmRepoUrl: Property<String>

    /**
     * ArtifactId for artifacts published to Maven
     * Use "kmm-spm" by default
     */
    val spmArtifactId: Property<String>

    /**
     * The Name of the Zip file of XCFrameworks
     * Use "$name-xcframework-$version" by default
     */
    val xcFrameworkZipName: Property<String>

    /**
     * Whether need SPM Support
     * Use false by default
     */
    val useSpm: Property<Boolean>

    /**
     * Need SPM Support
     */
    fun Project.spm() {
        useSpm.set(true)
    }
}