package com.zlove.gradle.plugins.tasks.transform

import com.zlove.gradle.plugins.utils.SystemPrint
import org.objectweb.asm.*
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
        SystemPrint.outPrintln(TAG, "--- onMethodEnter --- $name, nextLocal --- $nextLocal")
        SystemPrint.outPrintln(TAG, "argumentTypes.size = ${argumentTypes.size}")
        methodVisitor.visitMethodInsn(
            INVOKESTATIC,
            "java/lang/System",
            "currentTimeMillis",
            "()J",
            false
        )
        startVar = newLocal(Type.LONG_TYPE)
        SystemPrint.outPrintln(TAG, "new local is $startVar")
        methodVisitor.visitVarInsn(LSTORE, startVar)
        super.onMethodEnter()
    }

    override fun onMethodExit(opcode: Int) {
        if (isInitMethod() || methodVisitor == null || startVar == 0) {
            return
        }
        SystemPrint.outPrintln(TAG, " -- onMethodExit -- startVar: $startVar, nextLocal: $nextLocal.")
        methodVisitor.visitMethodInsn(
            INVOKESTATIC,
            "java/lang/System",
            "currentTimeMillis",
            "()J",
            false
        )

        methodVisitor.visitVarInsn(LLOAD, startVar)
        methodVisitor.visitInsn(LSUB)
        //存储sub
        val subVar = newLocal(Type.LONG_TYPE)
        SystemPrint.outPrintln(TAG, "subVar = $subVar,  nextLocal: $nextLocal")
        methodVisitor.visitVarInsn(LSTORE, subVar)

        //关键点4的实现
        //输出日志
        methodVisitor.visitLdcInsn("ExecutionTime")
        val log = "\' $name \' execution is %d ms"
        methodVisitor.visitLdcInsn(log)
        methodVisitor.visitInsn(Opcodes.ICONST_1)
        methodVisitor.visitTypeInsn(Opcodes.ANEWARRAY, "java/lang/Object")
        methodVisitor.visitInsn(Opcodes.DUP)
        methodVisitor.visitInsn(Opcodes.ICONST_0)
        methodVisitor.visitVarInsn(Opcodes.LLOAD, subVar)
        methodVisitor.visitMethodInsn(
            Opcodes.INVOKESTATIC,
            "java/lang/Long",
            "valueOf",
            "(J)Ljava/lang/Long;",
            false
        )
        methodVisitor.visitInsn(AASTORE)
        methodVisitor.visitMethodInsn(
            INVOKESTATIC,
            "java/lang/String",
            "format",
            "(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;",
            false
        )
        methodVisitor.visitMethodInsn(
            INVOKESTATIC,
            "android/util/Log",
            "v",
            "(Ljava/lang/String;Ljava/lang/String;)I",
            false
        )
        //关键点4的实现, 一定要有，否则该地方就会作为返回值返回
        methodVisitor.visitInsn(POP)
        //关键点5的实现
        returnValue()
        super.onMethodExit(opcode)
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