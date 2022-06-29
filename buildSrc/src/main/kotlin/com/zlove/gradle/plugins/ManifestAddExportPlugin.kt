package com.zlove.gradle.plugins

import com.android.build.gradle.AppExtension
import com.android.build.gradle.tasks.ProcessApplicationManifest
import com.zlove.gradle.plugins.tasks.AddExportForAndroidManifestTask
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Author by zlove, Email zlove.zhang@bytedance.com, Date on 2022/6/28.
 */
class ManifestAddExportPlugin: Plugin<Project> {

    override fun apply(project: Project) {
        project.afterEvaluate {
            //创建自定义Task
            val exportTask = project.tasks.register(
                AddExportForAndroidManifestTask.TAG,
                AddExportForAndroidManifestTask::class.javaObjectType,
            ).get()
            val android = it.extensions.getByType(AppExtension::class.java)
            android.applicationVariants.forEach { variant ->
                addExportForPackageManifestAfterEvaluate(project, upperCaseFirst(variant.name), exportTask)
                println(variant.name)

            }
        }
    }

    private fun addExportForPackageManifestAfterEvaluate(project: Project,
                                                         variantName: String,
                                                         exportTask: AddExportForAndroidManifestTask) {
        val processManifestTask =
            project.tasks.getByName("process${variantName}MainManifest")
        if (processManifestTask !is ProcessApplicationManifest) {
            return
        }

        exportTask.setInputMainManifest(processManifestTask.mainManifest.get())
        exportTask.setInputManifests(processManifestTask.getManifests())
        processManifestTask.dependsOn(exportTask)
    }

    private fun upperCaseFirst(source: String): String {
        val arr = source.toCharArray();
        arr[0] = Character.toUpperCase(arr[0]);
        return String(arr);
    }

}