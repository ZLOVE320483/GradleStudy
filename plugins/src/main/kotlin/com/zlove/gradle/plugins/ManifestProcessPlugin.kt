package com.zlove.gradle.plugins

import com.android.build.gradle.tasks.ProcessApplicationManifest
import com.zlove.gradle.plugins.extensions.BuildType
import com.zlove.gradle.plugins.extensions.ManifestExtension
import com.zlove.gradle.plugins.tasks.AddExportForPackageManifestTask
import com.zlove.gradle.plugins.utils.SystemPrint
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.util.regex.Pattern

/**
 * Author by zlove, Email zlove.zhang@bytedance.com, Date on 2022/6/8.
 */
class ManifestProcessPlugin: Plugin<Project> {

    private var variantName: String = ""

    override fun apply(project: Project) {
        createExtension(project)
        if (!getValidVariantNameInBuild(project)) {
            return
        }
        SystemPrint.outPrintln("variantName --- $variantName")
        addTasksForVariantAfterEvaluate(project)
    }

    private fun createExtension(project: Project) {
        project.extensions.create(ManifestExtension.TAG,
            ManifestExtension::class.javaObjectType,
            project.container(BuildType::class.javaObjectType))
    }

    private fun addTasksForVariantAfterEvaluate(project: Project) {
        project.afterEvaluate {
            try {
                addExportForPackageManifestAfterEvaluate(project)
            } catch (e: Exception) {

            }
        }
    }

    private fun addExportForPackageManifestAfterEvaluate(project: Project) {
        val processManifestTask =
            project.tasks.getByName("process${variantName}MainManifest")
        if (processManifestTask !is ProcessApplicationManifest) {
            return
        }
        //创建自定义Task
        val exportTask = project.tasks.register(
            AddExportForPackageManifestTask.TAG,
            AddExportForPackageManifestTask::class.javaObjectType,
        ).get()
        exportTask.setInputMainManifest(processManifestTask.mainManifest.get())
        exportTask.setInputManifests(processManifestTask.getManifests())
        processManifestTask.dependsOn(exportTask)
    }

    private fun getValidVariantNameInBuild(project: Project): Boolean {
        val parameter = project.gradle.startParameter.taskRequests.toString()
        val regex = if (parameter.contains("assemble")) {
            ":app:assemble(\\w+)"
        } else {
            ":app:generate(\\w+)"
        }
        val pattern = Pattern.compile(regex)
        val matcher = pattern.matcher(parameter)
        if (matcher.find()) {
            //group(0)整个字符串;group(1)第一个括号内的内容;group(2)第二个括号内的内容
            variantName = matcher.group(1)
        }
        //SystemPrint.outPrintln(variantName)
        if (variantName.isBlank()) {
            return false
        }
        return true
    }
}