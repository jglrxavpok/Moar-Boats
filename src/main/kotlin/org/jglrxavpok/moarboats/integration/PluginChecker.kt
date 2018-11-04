package org.jglrxavpok.moarboats.integration

import jdk.internal.org.objectweb.asm.Type
import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Opcodes

class PluginChecker(): ClassVisitor(Opcodes.ASM5) {
    var isPlugin: Boolean = false
        private set
    var dependency: String? = null

    override fun visit(version: Int, access: Int, name: String?, signature: String?, superName: String?, interfaces: Array<out String>?) {
        super.visit(version, access, name, signature, superName, interfaces)
        if(interfaces != null) {
            isPlugin = interfaces.any { it == Type.getInternalName(MoarBoatsIntegration::class.java) }
        }
    }

    override fun visitAnnotation(desc: String, visible: Boolean): AnnotationVisitor? {
        if(desc == Type.getDescriptor(MoarBoatsIntegration::class.java)) {
            return MBIntegrationAnnotationVisitor()
        }
        return super.visitAnnotation(desc, visible)
    }

    private inner class MBIntegrationAnnotationVisitor: AnnotationVisitor(Opcodes.ASM5) {
        override fun visit(name: String, value: Any) {
            super.visit(name, value)
            if(name == "dependency") {
                dependency = value as String
            }
        }
    }
}
