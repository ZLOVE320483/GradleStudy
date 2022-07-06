package com.zlove.gradle.plugins.tasks.transform

import com.zlove.gradle.plugins.utils.SystemPrint
import org.objectweb.asm.Attribute
import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.commons.AdviceAdapter

/**
 * Author by zlove, Email zlove.zhang@bytedance.com, Date on 2022/7/6.
 */
open class AutoLogAdviceAdapter(
    api: Int,
    private val methodVisitor: MethodVisitor?,
    access: Int,
    name: String?,
    descriptor: String?): AdviceAdapter(api, methodVisitor, access, name, descriptor) {

        companion object {
            private const val TAG = "AutoLogAdviceAdapter"
        }

    private var startVar = 0

    override fun onMethodEnter() {
        if (isInitMethod() || methodVisitor == null) {
            return
        }

    }

    override fun onMethodExit(opcode: Int) {

    }

    private fun isInitMethod(): Boolean {
        SystemPrint.outPrintln(TAG, "-- isInitMethod -- $name")
        return "<init>" == name || "<clinit>" == name
    }

    override fun visitIntInsn(opcode: Int, operand: Int) {
        super.visitIntInsn(opcode, operand)
        SystemPrint.outPrintln(TAG, " -- visitIntInsn opcode -- $opcode")
    }

    override fun visitInsn(opcode: Int) {
        super.visitInsn(opcode)
        SystemPrint.outPrintln(TAG, " -- visitInsn opcode -- $opcode")
    }

    override fun visitVarInsn(opcode: Int, `var`: Int) {
        super.visitVarInsn(opcode, `var`)
        SystemPrint.outPrintln(TAG, " -- visitVarInsn opcode -- $opcode")
    }

    override fun visitMethodInsn(
        opcode: Int,
        owner: String?,
        name: String?,
        descriptor: String?,
        isInterface: Boolean
    ) {
        super.visitMethodInsn(opcode, owner, name, descriptor, isInterface)
        SystemPrint.outPrintln(TAG, " -- visitMethodInsn opcode -- $opcode, name -- $name")

    }


    override fun visitFieldInsn(opcode: Int, owner: String?, name: String?, descriptor: String?) {
        super.visitFieldInsn(opcode, owner, name, descriptor)
        SystemPrint.outPrintln(TAG, " -- visitFieldInsn name -- $name")

    }

    override fun visitAttribute(attribute: Attribute?) {
        super.visitAttribute(attribute)
        SystemPrint.outPrintln(TAG, " -- visitAttribute name -- $attribute")

    }

    override fun visitLabel(label: Label?) {
        super.visitLabel(label)
        SystemPrint.outPrintln(TAG, " -- visitLabel label -- $label")

    }

    override fun visitLocalVariable(
        name: String?,
        descriptor: String?,
        signature: String?,
        start: Label?,
        end: Label?,
        index: Int
    ) {
        super.visitLocalVariable(name, descriptor, signature, start, end, index)
        SystemPrint.outPrintln(TAG, " -- visitLocalVariable name -- $name")

    }

    override fun visitFrame(
        type: Int,
        numLocal: Int,
        local: Array<out Any>?,
        numStack: Int,
        stack: Array<out Any>?
    ) {
        super.visitFrame(type, numLocal, local, numStack, stack)
        SystemPrint.outPrintln(TAG, " -- visitFrame type -- $type")

    }

    override fun visitMaxs(maxStack: Int, maxLocals: Int) {
        super.visitMaxs(maxStack + 4, maxLocals)
        SystemPrint.outPrintln(TAG, " -- visitMaxs maxStack -- $maxStack maxLocals $maxLocals")

    }
}