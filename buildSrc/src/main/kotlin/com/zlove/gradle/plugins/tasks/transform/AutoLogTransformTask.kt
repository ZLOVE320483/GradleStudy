package com.zlove.gradle.plugins.tasks.transform

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils
import com.zlove.gradle.plugins.utils.SystemPrint
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

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

        val inputs = transformInvocation.inputs
        val outputsProvider = transformInvocation.outputProvider
        transformInput(inputs, outputsProvider)

    }

    private fun transformInput(inputs: Collection<TransformInput>,
                               outputsProvider: TransformOutputProvider) {
        inputs.forEach {
            it.jarInputs.forEach { jarInput ->
                writeOutputFile(jarInput, Format.JAR, outputsProvider)
            }
            it.directoryInputs.forEach { directory ->
                handlerDirectoryInputFiles(directory.file)
                writeOutputFile(directory, Format.DIRECTORY, outputsProvider)
            }
        }
    }

    private fun handlerDirectoryInputFiles(inputFile: File) {
        if (inputFile.isDirectory) {
            val filesInDirectory = inputFile.listFiles()
            filesInDirectory?.forEach {
                handlerDirectoryInputFiles(it)
            }
        } else {
            handlerDirectoryInputClassFileByAsm(inputFile)
        }
    }

    private fun handlerDirectoryInputClassFileByAsm(inputClassFile: File) {
        SystemPrint.outPrintln(TAG, "Handler .class is \n${inputClassFile.absolutePath}\n")
        if (!inputClassFile.name.endsWith(".class")) {
            return
        }
        //1.创建ClassReader
        val fis = FileInputStream(inputClassFile)
        val classReader = ClassReader(fis)

        //2.创建ClassWriter
        val classWriter =
            ClassWriter(classReader, ClassWriter.COMPUTE_FRAMES)
        //3.实例化自定义的AutoLogClassVisitor
        val autoLogClassVisitor = AutoLogClassVisitor(classWriter)
        //4.注册AutoLogClassVisitor
        classReader.accept(autoLogClassVisitor, ClassReader.SKIP_FRAMES)

        //5.将修改之后的.class文件重新写入到该文件中
        val fos = FileOutputStream(inputClassFile)
        fos.write(classWriter.toByteArray())
        fos.close()
        fis.close()
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