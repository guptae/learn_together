package com.guptae.profiler.agent;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtBehavior;
import javassist.CtClass;

public class ATMTransformerV2 implements ClassFileTransformer{

/*	private static final String WITHDRAW_MONEY_METHOD = "withdrawMoney";
	private String targetClassName;
	private ClassLoader targetClassLoader;
	private static boolean isFieldAdded;*/

	/** classes to always not to instrument */
    static final String[] DEFAULT_EXCLUDES = new String[] {"com/sun/", "sun/", "java/", "javax/", "com/guptae/profiler/agent/" };

    /** only this classes should instrument or leave empty to instrument all classes that not excluded */
    static final String[] INCLUDES = new String[] {
    		"com/guptae/profiler/application/"
    };
    
//    static final Map<String, Integer> methodTimeMap = new HashMap<>();

	@Override
	public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
			ProtectionDomain protectionDomain, byte[] bytes) throws IllegalClassFormatException {

		byte[] byteArray = null;
		for( String include : INCLUDES ) {

            if( className.startsWith( include ) ) {
            	byteArray = doClass( className, classBeingRedefined, bytes );
                return byteArray;
            }
        }

        for( int i = 0; i < DEFAULT_EXCLUDES.length; i++ ) {

            if( className.startsWith( DEFAULT_EXCLUDES[i] ) ) {
                return bytes;
            }
        }
        byteArray = doClass( className, classBeingRedefined, bytes );
        
        return byteArray;
	}

	@SuppressWarnings("rawtypes")
	private byte[] doClass(String className, final Class classBeingRedefined, byte[] classfileBuffer) {
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
						methodArray[i].addLocalVariable("startTime", CtClass.longType);
						startBlock.append("com.guptae.profiler.agent.statistics.StatsHolder.incrementInvocationCount(\""+ key + "\");");
						startBlock.append("startTime = System.currentTimeMillis();");
						methodArray[i].insertBefore(startBlock.toString());
						
						StringBuilder endBlock = new StringBuilder();
						methodArray[i].addLocalVariable("endTime", CtClass.longType);
						methodArray[i].addLocalVariable("opTime", CtClass.longType);
						endBlock.append("endTime = System.currentTimeMillis();");
						endBlock.append("opTime = (endTime-startTime)/1000;");
						
						endBlock.append("System.out.println(\"[Application] " + methodArray[i].getName() +" completed in \" + opTime + \" seconds.\");");
						methodArray[i].insertAfter(endBlock.toString());

					}
					if("main".equals(methodArray[i].getName()))
					{
						methodArray[i].insertAfter("com.guptae.profiler.agent.statistics.StatsHolder.printCurrentStats();");
					}
				}
				byteCode = cl.toBytecode();
				cl.detach();
			} catch(CannotCompileException | IOException e) {
				e.printStackTrace();
			}
			
		
		return byteCode;
	}

}
