package com.zlove.gradle.plugins.tasks

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils
import com.zlove.gradle.plugins.utils.SystemPrint

/**
 * Author by zlove, Email zlove.zhang@bytedance.com, Date on 2022/7/4.
 */
open class AutoLogTransformTask: Transform() {

    companion object {
        const val TAG = "AutoLogTransformTask"
    }


    override fun getName(): String {
        return TAG
    }

    override fun getInputTypes(): MutableSet<QualifiedContent.ContentType> {
        return TransformManager.CONTENT_CLASS
    }

    override fun getScopes(): MutableSet<in QualifiedContent.Scope> {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    override fun isIncremental(): Boolean {
        return true
    }

    override fun transform(transformInvocation: TransformInvocation?) {
        super.transform(transformInvocation)
        transformInvocation?: return

        transformInvocation.referencedInputs
        val context = transformInvocation.context
        SystemPrint.outPrintln("project name is ${context.projectName}\n"
                + "variantName is ${context.variantName}\n"
                + "path is ${context.path}\n")

    }

    private fun writeOutputFile(input: QualifiedContent,
                                format: Format,
                                outputsProvider: TransformOutputProvider) {
        val outputFile = outputsProvider.getContentLocation(
            input.name,
            input.contentTypes,
            input.scopes,
            format
        )
        if (format == Format.JAR) {
            FileUtils.copyFile(input.file, outputFile)
        } else {
            FileUtils.copyDirectory(input.file, outputFile)
        }
    }
}