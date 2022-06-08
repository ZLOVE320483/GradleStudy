package com.zlove.gradle.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Author by zlove, Email zlove.zhang@bytedance.com, Date on 2022/6/8.
 */
class ManifestProcessPlugin: Plugin<Project> {

    override fun apply(project: Project) {
        println("manifest processing...")
    }
}