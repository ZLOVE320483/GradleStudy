package com.zlove.gradle.plugins

import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.gradle.AppExtension
import com.zlove.gradle.plugins.utils.SystemPrint
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Author by zlove, Email zlove.zhang@bytedance.com, Date on 2022/6/8.
 */
class ManifestProcessPlugin: Plugin<Project> {

    override fun apply(project: Project) {
        SystemPrint.outPrintln("--- ManifestProcessPlugin ---")
        getValidVariantNameInBuild(project)
    }

    private fun getValidVariantNameInBuild(project: Project): Boolean {
        project.extensions.getByType(AndroidComponentsExtension::class.java).onVariants {
            SystemPrint.outPrintln(it.name)
        }

        project.afterEvaluate {
            SystemPrint.outPrintln("size --- ${project.extensions.getByType(AppExtension::class.java).applicationVariants.size}")
            project.extensions.getByType(AppExtension::class.java).applicationVariants.forEach {
                SystemPrint.outPrintln(it.name)
            }
        }
        return true
    }
}