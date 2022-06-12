package com.zlove.gradle.plugins.tasks

import com.zlove.gradle.plugins.utils.SystemPrint
import groovy.util.Node
import groovy.util.XmlParser
import groovy.xml.XmlUtil
import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.TaskAction
import java.io.File

/**
 * Author by zlove, Email zlove.zhang@bytedance.com, Date on 2022/6/11.
 */
open class AddExportForPackageManifestTask: DefaultTask() {

    companion object {
        const val TAG = "AddExportForPackageManifest"
        const val ATTRIBUTE_EXPORTED = "{http://schemas.android.com/apk/res/android}exported"
        const val ATTRIBUTE_NAME = "{http://schemas.android.com/apk/res/android}name"
    }

    /**
     * 所有的被打包到APP的manifest文件,但不包括app下的manifest文件
     */
    private lateinit var inputManifests: FileCollection

    /**
     * app下对应的manifest文件
     */
    private lateinit var inputMainManifest: File

    /**
     * 是否在处理app下的manifest文件,如果是app下的manifest文件只报错提示,不处理
     */
    private var isHandlerMainManifest: Boolean = false

    fun setInputManifests(input: FileCollection) {
        this.inputManifests = input
    }

    fun setInputMainManifest(input: File) {
        this.inputMainManifest = input
    }

    @TaskAction
    fun doTaskAction() {
        handlerNonMainManifest()
        println()
        handlerMainManifest()
    }

    /**
     * 非app下的manifest文件
     */
    private fun handlerNonMainManifest() {
        isHandlerMainManifest = false
        SystemPrint.errorPrintln(
            TAG,
            "<<!!!  警告信息非error \n" +
                    "开始为 \"所有被打包到APP的manifest文件\" 检查和增加 \"android:exported\"\n" +
                    "因为操作的第三方的manifest,所以该属性为true   >>"
        )
        for (input in inputManifests) {
            readAndWriteManifestForExported(input)
        }
    }

    /**
     * 处理主app下的manifest文件
     */
    private fun handlerMainManifest() {
        isHandlerMainManifest = true
        SystemPrint.errorPrintln(
            TAG,
            "<<!!!  警告信息非error \n" +
                    "开始为 \"app的manifest文件\" 检查和报错提示 \"android:exported\"\n" +
                    "开发者需要根据报错的组件,按照实际开发需要设置属性值   >>"
        )
        readAndWriteManifestForExported(inputMainManifest)
    }

    /**
     * 处理非app下的manifest文件
     */
    private fun readAndWriteManifestForExported(manifest: File) {
        if (!manifest.exists()) {
            return
        }
        val node = readAndResetComponentFromManifest(manifest)
        writeComponentToManifest(manifest, node)
    }

    /**
     * 读取manifest文件下的所有内容,存放到node中
     */
    private fun readAndResetComponentFromManifest(manifest: File): Node {
        val xmlParser = XmlParser()
        //得到所有的结点树
        val node = xmlParser.parse(manifest)
        //node.attributes();获取的一级内容<?xml> <manifest>里设置的内容如:key为package、encoding,value为对应的值
        //node.children();获取的二级内容 <application> <uses-sdk>
        val firstChildren = node.children()
        //从集合中找到application的结点
        for (child in firstChildren) {
            if (notFindRightNode(child, "application")) {
                continue
            }
            //选择"application"这个结点
            val application = (child as Node).children()
            //从集合中找到里面的activity、service、receiver结点
            for (component in application) {
                if (notFindRightNode(component, "activity") &&
                    notFindRightNode(component, "service") &&
                    notFindRightNode(component, "receiver")
                ) {
                    continue
                }
                //处理activity、service、receiver结点的属性
                handlerNodeWithoutExported(component as Node)
            }
        }

        return node
    }

    /**
     * 处理没有android:exported的component
     */
    private fun handlerNodeWithoutExported(node: Node) {
        //已经含有android:exported
        if (hasAttributeExportedInNode(node)) {
            SystemPrint.outPrintln(TAG, "已经含有\"android:exported\"")
            return
        }
        for (intentFilter in node.children()) {
            //没有intent-filter
            if (notFindRightNode(intentFilter, "intent-filter")) {
                continue
            }
            //处理含有intent-filter所在的父结点上添加android:exported属性
            val name = attributeWithoutExportedName(node)
            handlerNodeAddExported(node, name)
        }

    }

    /**
     * 为符合条件的node添加android:exported
     */
    private fun handlerNodeAddExported(node: Node, name: String) {
        if (isHandlerMainManifest) {
            handlerNodeAddExportedForMainManifest(name)
            return
        }
        handlerNodeAddExportedForPackagedManifest(node, name)
    }

    /**
     * 处理app的manifest文件,仅做报错信息提示
     */
    private fun handlerNodeAddExportedForMainManifest(name: String) {
        SystemPrint.errorPrintln(
            TAG, "<<!!! error \n " +
                    "必须为 < $name > 添加android:exported属性,错误原因见Build Output的编译错误或https://developer.android.com/guide/topics/manifest/activity-element#exported  >>"
        )
    }

    /**
     * 处理被打包到APP的其他manifest文件中添加android:exported
     */
    private fun handlerNodeAddExportedForPackagedManifest(node: Node, name: String) {
        SystemPrint.outPrintln(TAG, "为 < $name > 添加android:exported=true")
        node.attributes()["android:exported"] = true
    }

    /**
     * 将更新之后的node重新写入原文件
     */
    private fun writeComponentToManifest(manifest: File, node: Node) {
        if (isHandlerMainManifest) {
            //如果是主app的manifest文件,只报错,不改写,需要开发者自行配置
            return
        }
        val result = XmlUtil.serialize(node)
        //重新写入原文件
        manifest.writeText(result, Charsets.UTF_8)
    }

    private fun notFindRightNode(node: Any?, name: String): Boolean {
        return node !is Node ||
                ((name) != node.name().toString())
    }

    private fun hasAttributeExportedInNode(node: Node): Boolean {
        val attributes = node.attributes()
        for (key in attributes.keys) {
            if (ATTRIBUTE_EXPORTED == key.toString()) {
                return true
            }
        }
        return false
    }

    private fun attributeWithoutExportedName(node: Node): String {
        val attributes = node.attributes()
        for (key in attributes.keys) {
            if (ATTRIBUTE_NAME == key.toString()) {
                return attributes[key].toString()
            }
        }
        return ""
    }
}