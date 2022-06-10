package com.zlove.gradle.plugins.tasks

import com.android.utils.FileUtils
import com.zlove.gradle.plugins.utils.SystemPrint
import groovy.util.Node
import groovy.xml.XmlParser
import groovy.xml.XmlUtil
import org.gradle.api.DefaultTask
import org.gradle.api.Task
import org.gradle.api.tasks.TaskAction
import org.gradle.work.InputChanges
import java.io.File

/**
 * Author by zlove, Email zlove.zhang@bytedance.com, Date on 2022/6/9.
 */
abstract class SetLatestVersionForMergedManifestTask: DefaultTask() {

    companion object {
        const val TAG = "SetLatestVersionForMergedManifestTask"
    }

    private var versionName: String = ""
    private var versionCode: String = ""

    @TaskAction
    fun runFullTaskAction(inputChanges: InputChanges) {
        SystemPrint.outPrintln(TAG, "input  isEmpty = " + inputs.files.isEmpty)
        SystemPrint.outPrintln(TAG,  "hasInputs = " + inputs.hasInputs)
        SystemPrint.errorPrintln(TAG, "isIncremental  = " + inputChanges.isIncremental)
        runFullTaskAction()
    }

    private fun runFullTaskAction() {
        if (!inputs.hasInputs) {
            SystemPrint.outPrintln(TAG, "NO-SOURCE interrupt the task , because input.hasInputs is false")
            return
        }
        handlerVersionForMainManifest()
    }

    private fun handlerVersionForMainManifest() {
        readVersionFromVersionManager()
        writeVersionToMainManifest()
    }

    private fun readVersionFromVersionManager() {
        val inputVersionFile = getVersionFileFromInputs()
        if (inputVersionFile == null) {
            SystemPrint.outPrintln(TAG, "NO-SOURCE interrupt the task , because input is null when read xml")
            return
        }
        val xmlParser = XmlParser()
        val node = xmlParser.parse(inputVersionFile)
        for (version in node.children()) {
            if (hasLatestVersionTag(version as Node)) {
                readVersionFromLatestNode(version)
                break
            }
        }
    }

    private fun hasLatestVersionTag(version: Node): Boolean {
        val attrs = version.attributes()
        for (key in attrs.keys) {
            if ("latest" == key.toString()) {
                return true
            }
        }
        return false
    }

    private fun readVersionFromLatestNode(latest: Node) {
        for (child in latest.children()) {
            if (child !is Node) {
                continue
            }
            val name = child.name().toString()
            if (name == "versionCode") {
                versionCode = child.text()
            }
            if (name == "versionName") {
                versionName = child.text()
            }
        }
    }

    private fun writeVersionToMainManifest() {
        val mainManifestFile = getMainManifestFileFromOutputs()
        if (mainManifestFile == null) {
            SystemPrint.outPrintln(TAG, "No Main Manifest xml, update the version FAILED, rebuild...")
            return
        }
        SystemPrint.outPrintln(TAG, "正在将 versionCode: $versionCode , versionName: $versionName" +
                " 写入到 ${mainManifestFile.absolutePath}")
        val xmlParser = XmlParser()
        val node = xmlParser.parse(mainManifestFile)
        var count = 0
        val attrs = node.attributes()
        for (key in attrs.keys) {
            if (key.toString().endsWith("versionCode")) {
                node.attributes().replace(key, versionCode)
                SystemPrint.outPrintln(TAG, node.attribute(key).toString())
                count++
                continue
            }
            if (key.toString().endsWith("versionName")) {
                node.attributes().replace(key, versionName)
                SystemPrint.outPrintln(TAG, node.attribute(key).toString())
                count++
                continue
            }
        }
        if (count == 2) {
            //写入到原manifest文件中
            val result = XmlUtil.serialize(node)
            mainManifestFile.writeText(result, Charsets.UTF_8)
            SystemPrint.outPrintln(TAG, "写入成功...")
        }
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
        SystemPrint.outPrintln("path --- ${outputs.files.asPath}" )
        for (file in outputs.files) {
            SystemPrint.outPrintln("file --- $file" )
            SystemPrint.outPrintln("exists --- ${file.exists()}" )
            if (!file.exists()) {
                continue
            }
            return file
        }
        return null
    }
}