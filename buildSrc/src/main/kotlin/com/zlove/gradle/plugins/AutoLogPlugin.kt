package com.zlove.gradle.plugins

import com.android.build.gradle.AppExtension
import com.zlove.gradle.plugins.tasks.transform.AutoLogTransformTask
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Author by zlove, Email zlove.zhang@bytedance.com, Date on 2022/7/7.
 */
class AutoLogPlugin: Plugin<Project> {

    override fun apply(project: Project) {
        val extension = project.extensions.findByType(AppExtension::class.javaObjectType)
        extension?.registerTransform(AutoLogTransformTask())
    }
}