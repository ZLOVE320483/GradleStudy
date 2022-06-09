package com.zlove.gradle.plugins.extensions

import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectContainer
import java.io.File

/**
 * Author by zlove, Email zlove.zhang@bytedance.com, Date on 2022/6/9.
 */
class ManifestExtension(var buildTypes: NamedDomainObjectContainer<BuildType>) {

    companion object {
        const val TAG = "ManifestExtension"
    }

    private var compileSdkVersion: Int = 0
    private var buildToolVersion: String = ""
    private var defaultConfig: DefaultConfig = DefaultConfig()
    private var versionManager: File = File("")

    fun versionManager(file: File) {
        this.versionManager = file
    }

    fun versionManager(): File {
        return this.versionManager
    }

    fun compileSdkVersion(sdk: Int) {
        this.compileSdkVersion = sdk
    }

    fun buildToolVersion(version: String) {
        this.buildToolVersion = version
    }

    fun compileSdkVersion(): Int {
        return this.compileSdkVersion
    }

    fun buildToolVersion(): String {
        return this.buildToolVersion
    }

    fun defaultConfig(action: Action<DefaultConfig>) {
        action.execute(defaultConfig)
    }

    fun buildTypes(action: Action<NamedDomainObjectContainer<BuildType>>) {
        action.execute(buildTypes)
    }

    fun buildTypes(): NamedDomainObjectContainer<BuildType> {
        return this.buildTypes

    }

    override fun toString(): String {
        val buildBuffer = StringBuffer()
        for (build in buildTypes()) {
            buildBuffer.append("${build}\n")
        }

        return "\ncompileSdkVersion: ${compileSdkVersion()}\nbuildToolVersion: ${buildToolVersion()}\n" +
                "defaultConfig: {\n  applicationId: ${defaultConfig.applicationId()}\n  minSdkVersion: ${defaultConfig.minSdkVersion()}" +
                " }\nbuildTypes:{\n ${buildBuffer}}"
    }
}