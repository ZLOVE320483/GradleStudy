package com.zlove.gradle.plugins.tasks.transform

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

/**
 * Author by zlove, Email zlove.zhang@bytedance.com, Date on 2022/7/7.
 */
open class AutoLogClassVisitor(visitor: ClassVisitor): ClassVisitor(Opcodes.ASM9, visitor) {

    companion object {
        private const val TAG = "AutoLogClassVisitor"
    }

    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        return AutoLogAdviceAdapter(api,
            super.visitMethod(access, name, descriptor, signature, exceptions),
            access,
            name,
            descriptor)
    }
}