package com.zlove.gradle.plugins.extensions

import java.io.File

/**
 * Author by zlove, Email zlove.zhang@bytedance.com, Date on 2022/6/9.
 */
class BuildType constructor(var name: String) {

    private var minifyEnabled: Boolean = false
    private var proguardFiles: File = File("")

    fun minifyEnabled(enabled: Boolean) {
        this.minifyEnabled = enabled
    }

    fun proguardFiles(file: File) {
        this.proguardFiles = file
    }

    fun minifyEnabled(): Boolean {
        return this.minifyEnabled
    }

    fun proguardFiles(): File {
        return this.proguardFiles
    }

    override fun toString(): String {
        return " ${name}: < minifyEnabled: $minifyEnabled ; proguardFiles: ${proguardFiles.path} >"
    }
}