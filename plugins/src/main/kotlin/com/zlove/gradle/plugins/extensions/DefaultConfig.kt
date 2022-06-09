package com.zlove.gradle.plugins.extensions

/**
 * Author by zlove, Email zlove.zhang@bytedance.com, Date on 2022/6/9.
 */
class DefaultConfig {

    private var applicationId: String = ""
    private var minSdkVersion: Int = 0

    fun applicationId(id: String) {
        this.applicationId = id
    }

    fun applicationId(): String {
        return this.applicationId
    }

    fun minSdkVersion(sdk: Int) {
        this.minSdkVersion = sdk
    }

    fun minSdkVersion(): Int {
        return this.minSdkVersion
    }

}