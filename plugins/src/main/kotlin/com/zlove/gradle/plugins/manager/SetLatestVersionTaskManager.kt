package com.zlove.gradle.plugins.manager

import com.android.build.gradle.tasks.ProcessMultiApkApplicationManifest
import com.zlove.gradle.plugins.extensions.ManifestExtension
import com.zlove.gradle.plugins.tasks.SetLatestVersionForMergedManifestTask
import org.gradle.api.Project
import java.io.File

/**
 * Author by zlove, Email zlove.zhang@bytedance.com, Date on 2022/6/10.
 */
class SetLatestVersionTaskManager(private val project: Project, private val variantName: String) {

    fun addSetLatestVersionForMergedManifestAfterEvaluate() {
        val multiApkProvider = project.tasks.named("process${variantName}Manifest",
            ProcessMultiApkApplicationManifest::class.javaObjectType)
        val multiApkApplicationManifest = multiApkProvider.get()
        if (multiApkApplicationManifest !is ProcessMultiApkApplicationManifest) {
            return
        }

         val versionTask = project.tasks.create(
            SetLatestVersionForMergedManifestTask.TAG,
            SetLatestVersionForMergedManifestTask::class.javaObjectType
        )

        val inputFile = getVersionManagerFromExtension()
        if (inputFile.exists()) {
            versionTask.inputs.file(inputFile)
        }

        versionTask.outputs.file(multiApkApplicationManifest.mainMergedManifest.asFile.get())
        multiApkApplicationManifest.finalizedBy(versionTask)
    }

    private fun getVersionManagerFromExtension(): File {
        val extension = project.extensions.findByType(ManifestExtension::class.javaObjectType)
        extension?: return File("")
        return extension.versionManager()
    }
}