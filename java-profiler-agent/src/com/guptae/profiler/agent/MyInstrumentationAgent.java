package com.guptae.profiler.agent;

import java.lang.instrument.Instrumentation;

public class MyInstrumentationAgent {

	public static void premain(String agentArgs, Instrumentation inst)
	{
		System.out.println("[Agent] in premain method");
		System.out.println("Is RedefineClasses Supported: " + inst.isRedefineClassesSupported());
		System.out.println("Is RetransformClasses Supported: " + inst.isRetransformClassesSupported());
		String className = "com.guptae.profiler.application.MyATM";
		transformClass(className, inst);
	}
	
	public static void agentmain(String agentArgs, Instrumentation inst)
	{
		System.out.println("[Agent] in agentmain method");
		
		String className = "com.guptae.profiler.application.MyATM";
		transformClass(className, inst);
	}
	
	private static void transformClass(String className, Instrumentation instrumentation)
	{
		Class<?> targetCls = null;
		ClassLoader targetClassLoader = null;
		// Getting class using Class forName
		try{
			targetCls = Class.forName(className);
			targetClassLoader = targetCls.getClassLoader();
			transform(targetCls, targetClassLoader, instrumentation);
			return;
		} catch (Exception e)
		{
			System.out.println("Class " + targetCls + " not found using Class.forName");
		}
		// if not found using forName then iterate all loaded classes
		for(Class<?> clazz: instrumentation.getAllLoadedClasses()){
			if(clazz.getName().equals(className))
			{
				targetCls = clazz;
				targetClassLoader = targetCls.getClassLoader();
				transform(targetCls, targetClassLoader, instrumentation);
				return;
			}
		}
		throw new RuntimeException("Failed to find class [" + className + "]");
	}
	
	private static void transform(Class<?> clazz, ClassLoader classLoader, Instrumentation instrumentation)
	{
		ATMTransformer transformer = new ATMTransformer(clazz.getName(), classLoader);
		System.out.println("Transformer class: " + transformer.getClass().getName());
		instrumentation.addTransformer(transformer, true);
		try{
			instrumentation.retransformClasses(clazz);
		} catch(Exception e)
		{
			throw new RuntimeException("Transform failed for class [" +clazz.getName() + "]", e);
		}
	}
}