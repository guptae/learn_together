package com.guptae.profiler.agent;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtBehavior;
import javassist.CtClass;

public class ClassTransformer implements ClassFileTransformer{

	/** classes to always not to instrument */
    static final String[] DEFAULT_EXCLUDES = new String[] {"com/sun/", "sun/", "java/", "javax/", "com/guptae/profiler/agent/" };

    /** only this classes should instrument or leave empty to instrument all classes that not excluded */
    static final String[] INCLUDES = new String[] {

    };

	private String[] excludes;
	private boolean isInvocationCountEnabled;
	private boolean isTimeTakenEnabled;

	public ClassTransformer(String[] excludes, boolean isInvocationCountEnabled, boolean isTimeTakenEnabled) {
		this.excludes = excludes;
		this.isInvocationCountEnabled = isInvocationCountEnabled;
		this.isTimeTakenEnabled = isTimeTakenEnabled;
	}

	public ClassTransformer() {
		this(null, false, false);
	}	
	@Override
	public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
			ProtectionDomain protectionDomain, byte[] bytes) throws IllegalClassFormatException {

		for( String include : INCLUDES ) {

            if( className.startsWith( include ) ) {
                return doClass( className, bytes );
            }
        }
		String[] exclPkgList = (null == excludes)?DEFAULT_EXCLUDES:this.excludes;
        for( int i = 0; i < exclPkgList.length; i++ ) {
            if( className.startsWith( exclPkgList[i].trim() ) ) {
                return bytes;
            }
        }

        return doClass( className, bytes );
	}

	private byte[] doClass(String className, byte[] classfileBuffer) {
		byte[] byteCode = classfileBuffer;
			
			System.out.println("Agent transforming class: " + className);
			try{
				ClassPool cp = ClassPool.getDefault();
				CtClass cl = cp.makeClass(new java.io.ByteArrayInputStream(byteCode));

				CtBehavior[] methodArray = cl.getDeclaredBehaviors();
				for(int i=0; i<methodArray.length; i++)
				{
					if(!methodArray[i].isEmpty())
					{
						String key = className+"_"+methodArray[i].getName();
						StringBuilder startBlock = new StringBuilder();
						if(this.isInvocationCountEnabled) {
							startBlock.append("com.guptae.profiler.agent.statistics.StatsCollector.incrementInvocationCount(\""+ key + "\");");
						}
						if(this.isTimeTakenEnabled) {
							methodArray[i].addLocalVariable("startTime", CtClass.longType);
							startBlock.append("startTime = System.currentTimeMillis();");
							StringBuilder endBlock = new StringBuilder();
							methodArray[i].addLocalVariable("endTime", CtClass.longType);
							methodArray[i].addLocalVariable("opTime", CtClass.longType);
							endBlock.append("endTime = System.currentTimeMillis();");
							endBlock.append("opTime = (endTime-startTime);");
							endBlock.append("System.out.println(opTime);");
							endBlock.append("com.guptae.profiler.agent.statistics.StatsCollector.addExecutionTime(\""+ key + "\", opTime);");
							
							endBlock.append("System.out.println(\"[Application] " + methodArray[i].getName() +" completed in \" + opTime + \" milliseconds.\");");
							methodArray[i].insertAfter(endBlock.toString());
						}
						methodArray[i].insertBefore(startBlock.toString());
					}
/*					if("main".equals(methodArray[i].getName()))
					{
						methodArray[i].insertAfter("com.guptae.profiler.agent.statistics.StatsHolder.printCurrentStats();");
					}*/
				}
				byteCode = cl.toBytecode();
				cl.detach();
			} catch(CannotCompileException | IOException e) {
				e.printStackTrace();
			}

		return byteCode;
	}

}
