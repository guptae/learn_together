package com.guptae.profiler.agent;

import java.lang.instrument.Instrumentation;

public class MyInstrumentationAgentV2 {

	public static void premain(String agentArgs, Instrumentation inst)
	{
		System.out.println("[Agent] in premain method");
		System.out.println("Is RedefineClasses Supported: " + inst.isRedefineClassesSupported());
		System.out.println("Is RetransformClasses Supported: " + inst.isRetransformClassesSupported());
//		String className = "com.guptae.profiler.application.MyATM";
		inst.addTransformer(new ATMTransformerV2(), true);
	}
	
	public static void agentmain(String agentArgs, Instrumentation inst)
	{
		System.out.println("[Agent] in agentmain method");
		System.out.println("Is RedefineClasses Supported: " + inst.isRedefineClassesSupported());
		System.out.println("Is RetransformClasses Supported: " + inst.isRetransformClassesSupported());
		inst.addTransformer(new ATMTransformerV2(), true);
	}
}