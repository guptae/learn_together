package com.guptae.profiler.agent;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.NotFoundException;

public class ATMTransformer implements ClassFileTransformer{

	private static final String WITHDRAW_MONEY_METHOD = "withdrawMoney";
	private String targetClassName;
	private ClassLoader targetClassLoader;
	private static boolean isFieldAdded;
	public ATMTransformer(String targetClassName, ClassLoader targetClassLoader) {
		this.targetClassName = targetClassName;
		this.targetClassLoader = targetClassLoader;
	}

	@Override
	public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
			ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {

		System.out.println("Inside - ATMTransformer - Transforming the class: " + className);
		byte[] byteCode = classfileBuffer;
		// replace . with /
		String finalTargetClassName = this.targetClassName.replaceAll("\\.", "/");
		if(!className.equals(finalTargetClassName))
		{
			return byteCode;
		}
		if(className.equals(finalTargetClassName) && loader.equals(targetClassLoader))
		{
			System.out.println("Agent transforming class MyATM");
			try{
				ClassPool cp = ClassPool.getDefault();
				
				CtClass cl = cp.makeClass(new java.io.ByteArrayInputStream(byteCode));
				CtClass cc = cp.get(targetClassName);
				System.out.println("Final target classname: " + finalTargetClassName);
				System.out.println("Target classloader: " + targetClassLoader);
				CtField[] fields = cl.getDeclaredFields();
				for(int i=0; i<fields.length; i++)
				{
					System.out.println("field: " + fields[i]);
				}
				
				//CtField f = new CtField(CtClass.intType, "_count", cc);
				if(!isFieldAdded)
				{
					CtField f = CtField.make("public static int _count = 0;", cl);
					System.out.println("New field: " + f.getName());
					System.out.println("New field info: " + f.getFieldInfo());
					cl.addField(f);
					isFieldAdded = true;
				}
				CtMethod m = cc.getDeclaredMethod(WITHDRAW_MONEY_METHOD);
				m.addLocalVariable("startTime", CtClass.longType);
				m.insertBefore("startTime = System.currentTimeMillis();");
				StringBuilder endBlock = new StringBuilder();
					
				m.addLocalVariable("endTime", CtClass.longType);
				m.addLocalVariable("opTime", CtClass.longType);
				endBlock.append("endTime = System.currentTimeMillis();");
				endBlock.append("opTime = (endTime - startTime)/1000;");
					
				endBlock.append("System.out.println(\"[Application] " + m.getName() +" completed in \" + opTime + \" seconds.\");");
					
				m.insertAfter(endBlock.toString());

				byteCode = cc.toBytecode();
				cc.detach();
			} catch(NotFoundException | CannotCompileException | IOException e) {
				e.printStackTrace();
			}
			
		}
		return byteCode;
	}

}
