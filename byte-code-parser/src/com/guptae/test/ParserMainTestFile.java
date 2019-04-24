package com.guptae.test;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ParserMainTestFile {

	private int intNum = 234563;
	private float floatNum = 23.49475f;
	private long longNum = 98733452L;
	private double doubleNum = 39493.928772;
	private byte byteNum = 32;
	private char c = 'a';
	private short shortNum = 324;
	private boolean isEnabled = false;
	private static ParserMainTestFile test = new ParserMainTestFile();
	public static final String str = "Checking public static";
	
	public static void main(String[] args) {
		ParserMainTestFile tp = new ParserMainTestFile();
		System.out.println("Hello, World !");
		System.out.println("Integer value: " + tp.intNum);
		System.out.println("Float value: " + tp.floatNum);
		System.out.println("Long value: " + tp.longNum);
		System.out.println("Double value: " + tp.doubleNum);
		System.out.println("Boolean value: " + tp.isEnabled);
		System.out.println("Printing character - " + tp.c);
		int sum = tp.testSum(tp.byteNum, tp.shortNum);
		System.out.println("The sum of 2 numbers is: " + sum);
		tp.testReflections();
		tp.testMethodHandle();
		tp.run();
		System.out.println("Testing private and static access flags" + test.getClass());
	}

	private int testSum(Byte byteNum, Short shortNum) {
		return (byteNum + shortNum);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void testReflections() {
		Class loadedList = null;
		List<String> list = null;

		try {
			loadedList = Class.forName("java.util.ArrayList");
			list = (List<String>) loadedList.newInstance();

			list.add("abc");
			list.add("def");

			Method m = loadedList.getMethod("size", (Class[]) null);
			Object size = m.invoke(list, (Object[]) null);
			System.out.println("Size of list using reflections: " + size);

		} catch (ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
		} catch (InstantiationException ie) {
			ie.printStackTrace();
		} catch (IllegalAccessException iae) {
			iae.printStackTrace();
		} catch (NoSuchMethodException nsme) {
			nsme.printStackTrace();
		} catch (SecurityException se) {
			se.printStackTrace();
		} catch (IllegalArgumentException iarge) {
			iarge.printStackTrace();
		} catch (InvocationTargetException ite) {
			ite.printStackTrace();
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void testMethodHandle() {
		Class loadedList = null;
		List<String> list = null;
		try {
			loadedList = Class.forName("java.util.ArrayList");
			list = (List<String>) loadedList.newInstance();

			list.add("abc");
			list.add("def");
			MethodHandle mh;

			MethodType desc = MethodType.methodType(int.class);
			mh = MethodHandles.lookup().findVirtual(loadedList, "size", desc);
			System.out.println("MethodHandle = " + mh);
			int size = (int) mh.invoke((ArrayList)list);
			System.out.println("Size of list using methodHandle: " + size);
		} catch (ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
		} catch (IllegalAccessException iae) {
			iae.printStackTrace();
		} catch (InstantiationException ie) {
			ie.printStackTrace();
		} catch (NoSuchMethodException nsme) {
			nsme.printStackTrace();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	private void run() {
        List<String> ls = new ArrayList<>();
        ls.add("Good Day");

        ArrayList<String> als = new ArrayList<>();
        als.add("Dydh Da");
    }
}
