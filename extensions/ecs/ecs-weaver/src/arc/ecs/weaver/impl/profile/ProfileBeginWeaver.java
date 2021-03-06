package arc.ecs.weaver.impl.profile;

import arc.ecs.weaver.meta.*;
import org.objectweb.asm.*;
import org.objectweb.asm.commons.*;

class ProfileBeginWeaver extends AdviceAdapter implements Opcodes{
    private ClassMetadata info;

    ProfileBeginWeaver(MethodVisitor methodVisitor, ClassMetadata info, int access, String name, String desc){
        super(ASM4, methodVisitor, access, name, desc);
        this.info = info;
    }

    @Override
    protected void onMethodEnter(){
        String systemName = info.type.getInternalName();
        String profiler = info.profilerClass.getInternalName();
        String profileDescriptor = info.profilerClass.getDescriptor();

        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, systemName, "$profiler", profileDescriptor);
        mv.visitMethodInsn(INVOKEVIRTUAL, profiler, "start", "()V", false);
    }
}
