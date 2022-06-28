package com.zlove.gradle.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Author by zlove, Email zlove.zhang@bytedance.com, Date on 2022/6/28.
 */
class ManifestAddExportPlugin: Plugin<Project> {

    override fun apply(p0: Project) {
        println("---- ManifestAddExportPlugin ---")
    }

}