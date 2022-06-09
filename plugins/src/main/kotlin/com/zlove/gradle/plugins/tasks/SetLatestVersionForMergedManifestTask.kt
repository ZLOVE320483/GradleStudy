package com.zlove.gradle.plugins.tasks

import com.android.utils.FileUtils
import groovy.util.Node
import org.gradle.api.DefaultTask
import org.gradle.api.Task
import java.io.File

/**
 * Author by zlove, Email zlove.zhang@bytedance.com, Date on 2022/6/9.
 */
abstract class SetLatestVersionForMergedManifestTask: DefaultTask() {

    companion object {
        const val TAG = "SetLatestVersionForMergedManifestTask"
    }

    private fun readVersionFromLatestNode(latest: Node) {
    }

    private fun Task.cleanUpTaskOutputs() {
        for (file in outputs.files) {
            if (file.isDirectory) {
                FileUtils.deleteDirectoryContents(file)
            } else {
                FileUtils.deletePath(file)
            }
        }
    }

    private fun getVersionFileFromInputs(): File? {
        for (file in inputs.files) {
            if (!file.exists()) {
                continue
            }
            return file
        }
        return null
    }

    private fun getMainManifestFileFromOutputs(): File? {
        for (file in outputs.files) {
            if (!file.exists()) {
                continue
            }
            return file
        }
        return null
    }
}