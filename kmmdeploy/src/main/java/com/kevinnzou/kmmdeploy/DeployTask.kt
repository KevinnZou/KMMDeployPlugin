package com.kevinnzou.kmmdeploy

import org.gradle.api.Project

/**
 * Created By Kevin Zou On 2023/5/18
 */
internal fun Project.deployKMM() = tasks.register("deployKMM"){
    group = GROUP
    description = "Deploy the KMM Outputs"
    dependsOn("copyKMMOutput")
}