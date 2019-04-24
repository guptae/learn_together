package com.guptae.parser;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ClassFileParser {

//	private List<Map<Integer, List<String>>> constantPool = new ArrayList<Map<Integer, List<String>>>();
	private Object[] constantPoolArray;
	private DataInputStream dis;

	private static final Map<Integer, String> JAVA_VERSION_MAPPING = new HashMap<Integer, String>();
	static {
		JAVA_VERSION_MAPPING.put(45, "JDK 1.1");
		JAVA_VERSION_MAPPING.put(46, "JDK 1.2");
		JAVA_VERSION_MAPPING.put(47, "JDK 1.3");
		JAVA_VERSION_MAPPING.put(48, "JDK 1.4");
		JAVA_VERSION_MAPPING.put(49, "Java SE 5.0");
		JAVA_VERSION_MAPPING.put(50, "Java SE 6.0");
		JAVA_VERSION_MAPPING.put(51, "Java SE 7");
		JAVA_VERSION_MAPPING.put(52, "Java SE 8");
		JAVA_VERSION_MAPPING.put(53, "Java SE 9");
		JAVA_VERSION_MAPPING.put(54, "Java SE 10");
		JAVA_VERSION_MAPPING.put(55, "Java SE 11");
		JAVA_VERSION_MAPPING.put(56, "Java SE 12");
		JAVA_VERSION_MAPPING.put(57, "Java SE 13");
	}
	/**
	 * ACC_PUBLIC		0x0001 	Declared public; may be accessed from outside its package. 
	 * ACC_FINAL		0x0010 	Declared final; no subclasses allowed.
	 * ACC_SUPER 		0x0020 	Treat superclass methods specially when invoked by the invokespecial instruction. 
	 * ACC_INTERFACE 	0x0200 	Is an interface, not a class. 
	 * ACC_ABSTRACT 	0x0400 	Declared abstract; must not be instantiated.
	 * ACC_SYNTHETIC 	0x1000 	Declared synthetic; not present in the source code.
	 * ACC_ANNOTATION 	0x2000 	Declared as an annotation type. 
	 * ACC_ENUM 		0x4000 	Declared as an enum type.
	 * 
	 */
	private static final Map<String, String> CLASS_ACCESS_FLAG_MASK_MAP = new HashMap<String, String>();
	static {
		CLASS_ACCESS_FLAG_MASK_MAP.put("1", "ACC_PUBLIC");
		CLASS_ACCESS_FLAG_MASK_MAP.put("10", "ACC_FINAL");
		CLASS_ACCESS_FLAG_MASK_MAP.put("20", "ACC_SUPER");
		CLASS_ACCESS_FLAG_MASK_MAP.put("21", "ACC_SUPER ACC_PUBLIC");
		CLASS_ACCESS_FLAG_MASK_MAP.put("11", "ACC_FINAL ACC_PUBLIC");
		CLASS_ACCESS_FLAG_MASK_MAP.put("200", "ACC_INTERFACE");
		CLASS_ACCESS_FLAG_MASK_MAP.put("400", "ACC_ABSTRACT");
		CLASS_ACCESS_FLAG_MASK_MAP.put("421", "ACC_ABSTRACT ACC_SUPER ACC_PUBLIC");
		CLASS_ACCESS_FLAG_MASK_MAP.put("401", "ACC_ABSTRACT ACC_PUBLIC");
		CLASS_ACCESS_FLAG_MASK_MAP.put("1000", "ACC_SYNTHETIC");
		CLASS_ACCESS_FLAG_MASK_MAP.put("4000", "ACC_ENUM");
		CLASS_ACCESS_FLAG_MASK_MAP.put("4020", "ACC_ENUM ACC_SUPER");
		CLASS_ACCESS_FLAG_MASK_MAP.put("4001", "ACC_ENUM ACC_PUBLIC");
		CLASS_ACCESS_FLAG_MASK_MAP.put("4021", "ACC_ENUM ACC_SUPER ACC_PUBLIC");
		CLASS_ACCESS_FLAG_MASK_MAP.put("2200", "ACC_ANNOTATION ACC_INTERFACE");
		CLASS_ACCESS_FLAG_MASK_MAP.put("2201", "ACC_ANNOTATION ACC_INTERFACE ACC_PUBLIC");
		CLASS_ACCESS_FLAG_MASK_MAP.put("2221", "ACC_ANNOTATION ACC_INTERFACE ACC_SUPER ACC_PUBLIC");
		CLASS_ACCESS_FLAG_MASK_MAP.put("1221", "ACC_SYNTHETIC ACC_INTERFACE ACC_SUPER ACC_PUBLIC");
		CLASS_ACCESS_FLAG_MASK_MAP.put("1421", "ACC_SYNTHETIC ACC_ABSTRACT ACC_SUPER ACC_PUBLIC");
		CLASS_ACCESS_FLAG_MASK_MAP.put("1201", "ACC_SYNTHETIC ACC_INTERFACE ACC_PUBLIC");
		CLASS_ACCESS_FLAG_MASK_MAP.put("1401", "ACC_SYNTHETIC ACC_ABSTRACT ACC_PUBLIC");
	}

	/**
	 * ACC_PUBLIC		0x0001	Declared public; may be accessed from outside its package.
	 * ACC_PRIVATE		0x0002	Declared private; usable only within the defining class.
	 * ACC_PROTECTED	0x0004	Declared protected; may be accessed within subclasses.
	 * ACC_STATIC		0x0008	Declared static.
	 * ACC_FINAL		0x0010	Declared final; never directly assigned to after object construction (JLS §17.5).
	 * ACC_VOLATILE		0x0040	Declared volatile; cannot be cached.
	 * ACC_TRANSIENT	0x0080	Declared transient; not written or read by a persistent object manager.
	 * ACC_SYNTHETIC	0x1000	Declared synthetic; not present in the source code.
	 * ACC_ENUM			0x4000	Declared as an element of an enum.
	 */
	private static final Map<String, String> FIELD_ACCESS_FLAG_MASK_MAP = new HashMap<String, String>();
	static {
		FIELD_ACCESS_FLAG_MASK_MAP.put("1", "ACC_PUBLIC");
		FIELD_ACCESS_FLAG_MASK_MAP.put("2", "ACC_PRIVATE");
		FIELD_ACCESS_FLAG_MASK_MAP.put("4", "ACC_PROTECTED");
		FIELD_ACCESS_FLAG_MASK_MAP.put("8", "ACC_STATIC");
		FIELD_ACCESS_FLAG_MASK_MAP.put("10", "ACC_FINAL");
		FIELD_ACCESS_FLAG_MASK_MAP.put("40", "ACC_VOLATILE");
		FIELD_ACCESS_FLAG_MASK_MAP.put("80", "ACC_TRANSIENT");
		FIELD_ACCESS_FLAG_MASK_MAP.put("1000", "ACC_SYNTHETIC");
		FIELD_ACCESS_FLAG_MASK_MAP.put("4000", "ACC_ENUM");
		FIELD_ACCESS_FLAG_MASK_MAP.put("11", "ACC_FINAL ACC_PUBLIC");
		FIELD_ACCESS_FLAG_MASK_MAP.put("19", "ACC_FINAL ACC_PUBLIC ACC STATIC");
		FIELD_ACCESS_FLAG_MASK_MAP.put("1A", "ACC_FINAL ACC_PRIVATE ACC STATIC");
		FIELD_ACCESS_FLAG_MASK_MAP.put("12", "ACC_FINAL ACC_PRIVATE");
		FIELD_ACCESS_FLAG_MASK_MAP.put("14", "ACC_FINAL ACC_PROTECTED");
		FIELD_ACCESS_FLAG_MASK_MAP.put("18", "ACC_FINAL ACC_STATIC");
		FIELD_ACCESS_FLAG_MASK_MAP.put("41", "ACC_VOLATILE ACC_PUBLIC");
		FIELD_ACCESS_FLAG_MASK_MAP.put("81", "ACC_TRANSIENT ACC_PUBLIC");
		FIELD_ACCESS_FLAG_MASK_MAP.put("84", "ACC_TRANSIENT ACC_PROTECTED");
		FIELD_ACCESS_FLAG_MASK_MAP.put("88", "ACC_TRANSIENT ACC_STATIC");
		FIELD_ACCESS_FLAG_MASK_MAP.put("4001", "ACC_ENUM ACC_PUBLIC");
		FIELD_ACCESS_FLAG_MASK_MAP.put("4002", "ACC_ENUM ACC_PRIVATE");
		FIELD_ACCESS_FLAG_MASK_MAP.put("4004", "ACC_ENUM ACC_PROTECTED");
		FIELD_ACCESS_FLAG_MASK_MAP.put("1011", "ACC_SYNTHETIC ACC_FINAL ACC_PUBLIC");
		FIELD_ACCESS_FLAG_MASK_MAP.put("1018", "ACC_SYNTHETIC ACC_FINAL ACC_STATIC");
	}
	
	/**
	 * ACC_PUBLIC		0x0001	Declared public; may be accessed from outside its package.
	 * ACC_PRIVATE		0x0002	Declared private; accessible only within the defining class.
	 * ACC_PROTECTED	0x0004	Declared protected; may be accessed within subclasses.
	 * ACC_STATIC		0x0008	Declared static.
	 * ACC_FINAL		0x0010	Declared final; must not be overridden (§5.4.5).
	 * ACC_SYNCHRONIZED	0x0020	Declared synchronized; invocation is wrapped by a monitor use.
	 * ACC_BRIDGE		0x0040	A bridge method, generated by the compiler.
	 * ACC_VARARGS		0x0080	Declared with variable number of arguments.
	 * ACC_NATIVE		0x0100	Declared native; implemented in a language other than Java.
	 * ACC_ABSTRACT		0x0400	Declared abstract; no implementation is provided.
	 * ACC_STRICT		0x0800	Declared strictfp; floating-point mode is FP-strict.
	 * ACC_SYNTHETIC	0x1000	Declared synthetic; not present in the source code.
	 */
	private static final Map<String, String> METHOD_ACCESS_FLAG_MASK_MAP = new HashMap<String, String>();
	static {
		METHOD_ACCESS_FLAG_MASK_MAP.put("1", "ACC_PUBLIC");
		METHOD_ACCESS_FLAG_MASK_MAP.put("2", "ACC_PRIVATE");
		METHOD_ACCESS_FLAG_MASK_MAP.put("4", "ACC_PROTECTED");
		METHOD_ACCESS_FLAG_MASK_MAP.put("8", "ACC_STATIC");
		METHOD_ACCESS_FLAG_MASK_MAP.put("10", "ACC_FINAL");
		METHOD_ACCESS_FLAG_MASK_MAP.put("20", "ACC_SYNCHRONIZED");
		METHOD_ACCESS_FLAG_MASK_MAP.put("40", "ACC_BRIDGE");
		METHOD_ACCESS_FLAG_MASK_MAP.put("80", "ACC_VARARGS");
		METHOD_ACCESS_FLAG_MASK_MAP.put("100", "ACC_NATIVE");
		METHOD_ACCESS_FLAG_MASK_MAP.put("800", "ACC_ABSTRACT");
		METHOD_ACCESS_FLAG_MASK_MAP.put("1000", "ACC_SYNTHETIC");
		METHOD_ACCESS_FLAG_MASK_MAP.put("9", "ACC_STATIC ACC_PUBLIC");
		METHOD_ACCESS_FLAG_MASK_MAP.put("A", "ACC_STATIC ACC_PRIVATE");
		METHOD_ACCESS_FLAG_MASK_MAP.put("11", "ACC_FINAL ACC_PUBLIC");
		METHOD_ACCESS_FLAG_MASK_MAP.put("12", "ACC_FINAL ACC_PRIVATE");
		METHOD_ACCESS_FLAG_MASK_MAP.put("18", "ACC_FINAL ACC_STATIC");
		METHOD_ACCESS_FLAG_MASK_MAP.put("21", "ACC_SYNCHRONIZED ACC_PUBLIC");
		METHOD_ACCESS_FLAG_MASK_MAP.put("22", "ACC_SYNCHRONIZED ACC_PRIVATE");
		METHOD_ACCESS_FLAG_MASK_MAP.put("18", "ACC_FINAL ACC_STATIC");
		METHOD_ACCESS_FLAG_MASK_MAP.put("19", "ACC_FINAL ACC_PUBLIC ACC_STATIC");
		METHOD_ACCESS_FLAG_MASK_MAP.put("41", "ACC_BRIDGE ACC_PUBLIC");
		METHOD_ACCESS_FLAG_MASK_MAP.put("81", "ACC_VARARGS ACC_PUBLIC");
		METHOD_ACCESS_FLAG_MASK_MAP.put("82", "ACC_VARARGS ACC_PRIVATE");
		METHOD_ACCESS_FLAG_MASK_MAP.put("84", "ACC_VARARGS ACC_PROTECTED");
		METHOD_ACCESS_FLAG_MASK_MAP.put("89", "ACC_VARARGS ACC_PUBLIC ACC_STATIC");
		METHOD_ACCESS_FLAG_MASK_MAP.put("8A", "ACC_VARARGS ACC_PRIVATE ACC_STATIC");
		METHOD_ACCESS_FLAG_MASK_MAP.put("101", "ACC_NATIVE ACC_PUBLIC");
		METHOD_ACCESS_FLAG_MASK_MAP.put("104", "ACC_NATIVE ACC_PROTECTED");
		METHOD_ACCESS_FLAG_MASK_MAP.put("401", "ACC_ABSTRACT ACC_PUBLIC");
		METHOD_ACCESS_FLAG_MASK_MAP.put("404", "ACC_ABSTRACT ACC_PROTECTED");
		METHOD_ACCESS_FLAG_MASK_MAP.put("801", "ACC_STRICT ACC_PUBLIC");
		METHOD_ACCESS_FLAG_MASK_MAP.put("802", "ACC_STRICT ACC_PRIVATE");
		METHOD_ACCESS_FLAG_MASK_MAP.put("804", "ACC_STRICT ACC_PROTECTED");
		METHOD_ACCESS_FLAG_MASK_MAP.put("1011", "ACC_SYNTHETIC ACC_FINAL ACC_PUBLIC");
		METHOD_ACCESS_FLAG_MASK_MAP.put("1001", "ACC_SYNTHETIC ACC_PUBLIC");
		METHOD_ACCESS_FLAG_MASK_MAP.put("1018", "ACC_SYNTHETIC ACC_FINAL ACC_STATIC");
	}
	
	/**
	 * ACC_PUBLIC	0x0001	Marked or implicitly public in source.
	 * ACC_PRIVATE	0x0002	Marked private in source.
	 * ACC_PROTECTED	0x0004	Marked protected in source.
	 * ACC_STATIC	0x0008	Marked or implicitly static in source.
	 * ACC_FINAL	0x0010	Marked final in source.
	 * ACC_INTERFACE	0x0200	Was an interface in source.
	 * ACC_ABSTRACT	0x0400	Marked or implicitly abstract in source.
	 * ACC_SYNTHETIC	0x1000	Declared synthetic; not present in the source code.
	 * ACC_ANNOTATION	0x2000	Declared as an annotation type.
	 * ACC_ENUM	0x4000	Declared as an enum type.
	 */
	private static final Map<String, String> ATTRIBUTE_ACCESS_FLAG_MAP = new HashMap<String, String>();
	static{
		ATTRIBUTE_ACCESS_FLAG_MAP.put("1", "ACC_PUBLIC");
		ATTRIBUTE_ACCESS_FLAG_MAP.put("2", "ACC_PRIVATE");
		ATTRIBUTE_ACCESS_FLAG_MAP.put("4", "ACC_PROTECTED");
		ATTRIBUTE_ACCESS_FLAG_MAP.put("8", "ACC_STATIC");
		ATTRIBUTE_ACCESS_FLAG_MAP.put("10", "ACC_FINAL");
		ATTRIBUTE_ACCESS_FLAG_MAP.put("200", "ACC_INTERFACE");
		ATTRIBUTE_ACCESS_FLAG_MAP.put("400", "ACC_ABSTRACT");
		ATTRIBUTE_ACCESS_FLAG_MAP.put("1000", "ACC_SYNTHETIC");
		ATTRIBUTE_ACCESS_FLAG_MAP.put("2000", "ACC_ANNOTATION");
		ATTRIBUTE_ACCESS_FLAG_MAP.put("4000", "ACC_ENUM");
		ATTRIBUTE_ACCESS_FLAG_MAP.put("9", "ACC_STATIC ACC_PUBLIC");
		ATTRIBUTE_ACCESS_FLAG_MAP.put("A", "ACC_STATIC ACC_PRIVATE");
		ATTRIBUTE_ACCESS_FLAG_MAP.put("11", "ACC_FINAL ACC_PUBLIC");
		ATTRIBUTE_ACCESS_FLAG_MAP.put("12", "ACC_FINAL ACC_PRIVATE");
		ATTRIBUTE_ACCESS_FLAG_MAP.put("18", "ACC_FINAL ACC_STATIC");
		ATTRIBUTE_ACCESS_FLAG_MAP.put("19", "ACC_FINAL ACC_PUBLIC ACC_STATIC");
		ATTRIBUTE_ACCESS_FLAG_MAP.put("201", "ACC_INTERFACE ACC_PUBLIC");
		ATTRIBUTE_ACCESS_FLAG_MAP.put("204", "ACC_INTERFACE ACC_PROTECTED");
		ATTRIBUTE_ACCESS_FLAG_MAP.put("401", "ACC_ABSTRACT ACC_PUBLIC");
		ATTRIBUTE_ACCESS_FLAG_MAP.put("404", "ACC_ABSTRACT ACC_PROTECTED");
		ATTRIBUTE_ACCESS_FLAG_MAP.put("2200", "ACC_ANNOTATION ACC_INTERFACE");
		ATTRIBUTE_ACCESS_FLAG_MAP.put("2201", "ACC_ANNOTATION ACC_INTERFACE ACC_PUBLIC");
		ATTRIBUTE_ACCESS_FLAG_MAP.put("4001", "ACC_ENUM ACC_PUBLIC");
		ATTRIBUTE_ACCESS_FLAG_MAP.put("1011", "ACC_SYNTHETIC ACC_FINAL ACC_PUBLIC");
		ATTRIBUTE_ACCESS_FLAG_MAP.put("1001", "ACC_SYNTHETIC ACC_PUBLIC");
		ATTRIBUTE_ACCESS_FLAG_MAP.put("1018", "ACC_SYNTHETIC ACC_FINAL ACC_STATIC");
	}
	private static final int UTF8 = 1;
	private static final int INTEGER = 3;
	private static final int FLOAT = 4;
	private static final int LONG = 5;
	private static final int DOUBLE = 6;
	private static final int CLASS = 7;
	private static final int STRING = 8;
	private static final int FIELD_REF = 9;
	private static final int METHOD_REF = 10;
	private static final int INTERFACE_METHOD_REF = 11;
	private static final int NAME_AND_TYPE = 12;
	private static final int METHOD_HANDLE_REF = 15;
	private static final int METHOD_TYPE_REF = 16;
	private static final int INVOKE_DYNAMIC_REF = 18;

	public static void main(String[] args) {
		if (args.length == 0 || !(args[0].endsWith(".class"))) {
			System.out.println("Please provide a proper .class file to parse.");
			System.exit(-1);
		}

		String filename = args[0];
		ClassFileParser cfp = new ClassFileParser();
		try {

			cfp.dis = new DataInputStream(new FileInputStream(filename));

			cfp.parseMagicNumber();
			cfp.parseVersionNumber();
			cfp.parseConstantPool();
			cfp.parseAccessFlag();
			cfp.parseClassNameIndex();
			cfp.parseSuperClassNameIndex();
			cfp.parseInterfaces();
			cfp.parseFieldInfo();
			cfp.parseMethodInfo();
			cfp.parseAttributeInfo();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void parseAttributeInfo() throws IOException {
		int attributeCount = dis.readUnsignedShort();
		System.out.println("No. of Attributes: " + attributeCount + "\n");
		attributeLookup(attributeCount);
	}

	private void parseMethodInfo() throws IOException {
		int methodCount = dis.readUnsignedShort();
		System.out.println("No. of Methods: " + methodCount + "\n");
		for (int k = 0; k < methodCount; k++) {
			String hexBitMask = Integer.toHexString(dis.readUnsignedShort());
			String accessFlagValue = FIELD_ACCESS_FLAG_MASK_MAP.get(hexBitMask);
			if (null != accessFlagValue) {
				System.out.println("Field Access Flag set is: " + accessFlagValue);
			} else {
				System.out.println("Hex representation of Access Flag is: " + hexBitMask);
			}
			int methodNameIndex = dis.readUnsignedShort();
			System.out.println("Method name index in the constant pool array is: " + methodNameIndex);
			
			int methodDescriptorIndex = dis.readUnsignedShort();
			System.out.println("Method descriptor index in the constant pool array is: " + methodDescriptorIndex);
			
			int attributeCount = dis.readUnsignedShort();
			System.out.println("No. of Attributes for the field: " + attributeCount);
			
			attributeLookup(attributeCount);
		}
	}

	private void attributeLookup(int attributeCount) throws IOException {
		for (int i = 0; i < attributeCount; i++) {
			int attributeNameIndex = dis.readUnsignedShort();
			System.out.println("Attribute name index in the constant pool array is: " + attributeNameIndex);
			
			int attributeLength = dis.readInt();
			System.out.println("Attribute Length is: " + attributeLength);
			if (attributeNameIndex == 25) {
				System.out.println("Attribute name: ConstantValue");
				for (int j = 0; j < attributeLength; j++) {
					System.out.println("Attribute info[" + i
							+ "] - index of attribute type in the constant pool array is: " + dis.readByte());
				}
			} 
			else if (attributeNameIndex == 30 || attributeNameIndex == 7) {
				System.out.println("Attribute name: Code");
				parseCodeAttribute();			
			}
			else if (attributeNameIndex == 36 || attributeNameIndex == 10) {
				System.out.println("Attribute name: LineNumberTable");
				parseLineNumberTableAttribute();
			}
			else if (attributeNameIndex == 37 || attributeNameIndex == 11) {
				System.out.println("Attribute name: LocalVariableTable");
				parseLocalVariableTable();
			}
			else if (attributeNameIndex == 246) {
				System.out.println("Attribute name: LocalVariableTypeTable");
				parseLocalVariableTypeTable();
			}
			else if (attributeNameIndex == 248) {
				System.out.println("Attribute name: StackMapTable");
				parseStackMapTable();
			}
			else if (attributeNameIndex == 302 || attributeNameIndex == 20) {
				System.out.println("Attribute name: SourceFile");
				parseSourceFile();
			}
			else if (attributeNameIndex == 304) {
				System.out.println("Attribute name: InnerClasses");
				parseInnerClasses();
			}
		}
	}

	/**
	 * InnerClasses_attribute {
     * 		u2 attribute_name_index;
     * 		u4 attribute_length;
     * 		u2 number_of_classes;
     *		{   u2 inner_class_info_index;
     * 			u2 outer_class_info_index;
     *			u2 inner_name_index;
     *			u2 inner_class_access_flags;
     *		} classes[number_of_classes];
	 *	}
	 * @throws IOException 
	 */
	private void parseInnerClasses() throws IOException {
		int numOfClasses = dis.readUnsignedShort();
		System.out.println("Number of classes: " + numOfClasses);
		
		for(int i=0; i<numOfClasses; i++)
		{
			System.out.println("Inner class info index: " + dis.readUnsignedShort());
			System.out.println("Outer class info index: " + dis.readUnsignedShort());
			System.out.println("Inner name index: " + dis.readUnsignedShort());
			String hexBitMask = Integer.toHexString(dis.readUnsignedShort());
			String accessFlagValue = ATTRIBUTE_ACCESS_FLAG_MAP.get(hexBitMask);
			if (null != accessFlagValue) {
				System.out.println("Attribute Access Flag set is: " + accessFlagValue);
			} else {
				System.out.println("Hex representation of Access Flag is: " + hexBitMask);
			}
		}
	}

	/**
	 * SourceFile_attribute {
     * 	u2 attribute_name_index;
     * 	u4 attribute_length;
     * 	u2 sourcefile_index;
		}
	 * @throws IOException 
	 */
	private void parseSourceFile() throws IOException {
		System.out.println("The sourcefile_index in the constant pool array is: " + dis.readUnsignedShort());
	}

	/**
	 * StackMapTable_attribute {
     *	u2              attribute_name_index;
     *	u4              attribute_length;
     *	u2              number_of_entries;
     *		stack_map_frame entries[number_of_entries];
	 * }
	 * 
	 * full_frame {
     * 	u1 	frame_type = FULL_FRAME; // 255 
     * 	u2 	offset_delta;
     * 	u2 	number_of_locals;
     * 		verification_type_info locals[number_of_locals];
     * 	u2 	number_of_stack_items;
     * 		verification_type_info stack[number_of_stack_items];
	 * }
	 * 
	 * same_locals_1_stack_item_frame {
     *	u1 frame_type = SAME_LOCALS_1_STACK_ITEM; // 64-127
     *	verification_type_info stack[1];
	 * }
	 * 
	 * same_frame {
     *	u1 frame_type = SAME; // 0-63
	 * }
	 * @throws IOException 
	 */
	private void parseStackMapTable() throws IOException {
		int numOfEntries = dis.readUnsignedShort();
		System.out.println(" The number of stack_map_frame entries in the entries table: " + numOfEntries);
		int frameType, offsetDelta, numOfLocals, numOfStackItems = 0;
		Object[] arrayOfLocals = new Object[numOfEntries];
		Object[] arrayOfStackItems = new Object[numOfEntries];
		for(int i=0; i<numOfEntries; i++)
		{
			frameType = dis.readUnsignedByte();
 			if(frameType == 255)
			{
				System.out.println("Frame tpe is: FULL_FRAME");
				offsetDelta = dis.readUnsignedShort();
				System.out.println("Offset delta: " + offsetDelta);
				numOfLocals = dis.readUnsignedShort();
				System.out.println("Number of local variables: " + numOfLocals);
				arrayOfLocals[i] = parseLocalsArray(numOfLocals);
				numOfStackItems = dis.readUnsignedShort();
				System.out.println("Number of stack items: " + numOfStackItems);
				arrayOfStackItems[i] = parseLocalsArray(numOfStackItems);
			} else if(frameType >= 64 && frameType <= 127)
 			{
 				System.out.println("Frame type is: Same Locals 1 Stack Item Frame");
 				offsetDelta = frameType - 64;
				System.out.println("Offset delta: " + offsetDelta);
				arrayOfLocals[i] = arrayOfLocals[i-1];
 				arrayOfStackItems[i] = parseLocalsArray(1);
 			} else if(frameType >= 0 && frameType <= 63)
 			{
 				System.out.println("Frame type is: Same Frame");
				System.out.println("Offset delta: " + frameType);
				arrayOfLocals[i] = arrayOfLocals[i-1];
 				arrayOfStackItems[i] = null;
 			}
 			
		}
	}

	/**
	 * union verification_type_info {
     * 		Top_variable_info;
     * 		Integer_variable_info;
     * 		Float_variable_info;
     * 		Long_variable_info;
     * 		Double_variable_info;
     * 		Null_variable_info;
     * 		UninitializedThis_variable_info;
     * 		Object_variable_info;
     * 		Uninitialized_variable_info;
	 * }
	 * 
	 * @param numOfLocals
	 * @throws IOException
	 */
	private Object[][] parseLocalsArray(int numOfLocals) throws IOException {

		Object[][] localsArray = new Object[numOfLocals][2];
		for(int i=0; i<numOfLocals; i++)
		{
			int typeInfo = dis.readUnsignedByte();
			int classIndex, offset;
			System.out.println("verification type: " + typeInfo);
			switch(typeInfo) {
			case 0:	System.out.println("Verification type top: ITEM_TOP");
					localsArray[i][0] = typeInfo;
					break; 
			case 1:	System.out.println("Verification type int: ITEM_Integer");
					localsArray[i][0] = typeInfo;
					break;
			case 2:	System.out.println("Verification type float: ITEM_Float");
					localsArray[i][0] = typeInfo;
					break;
			case 3:	System.out.println("Verification type Long: ITEM_Long");
					localsArray[i][0] = typeInfo;
					break;
			case 4:	System.out.println("Verification type Double: ITEM_Double");
					localsArray[i][0] = typeInfo;
					break;
			case 5:	System.out.println("Verification type null: ITEM_Null");
					localsArray[i][0] = typeInfo;
					break;
			case 6:	System.out.println("Verification type uninitializedThis: ITEM_UninitializedThis");
					localsArray[i][0] = typeInfo;
					break;
			case 7:	System.out.println("Verification type Object: ITEM_Object");
					localsArray[i][0] = typeInfo;
					classIndex = dis.readUnsignedShort();
					localsArray[i][1] = classIndex;
					System.out.println("Class index in the constant pool array is: " + classIndex);
					break;
			case 8:	System.out.println("Verification type uninitialized: ITEM_Uninitialized");
					localsArray[i][0] = typeInfo;
					offset = dis.readUnsignedShort();
					localsArray[i][1] = offset;
					System.out.println("Offset value in the code array of the Code attribute: " + offset);
					break;
			}
		}
		return localsArray;
	}

	/**
	 * 	LocalVariableTypeTable_attribute {
     *	u2 attribute_name_index;
     *  u4 attribute_length;
     *	u2 local_variable_type_table_length;
     *  {   u2 start_pc;
     *   	u2 length;
     *   	u2 name_index;
     *   	u2 signature_index;
     *   	u2 index;
     *	} local_variable_type_table[local_variable_type_table_length];
	 *	}
	 * @throws IOException
	 */
	private void parseLocalVariableTypeTable() throws IOException {
		int localVarTypeTableLen = dis.readUnsignedShort();
		System.out.println(" The number of entries in the local_variable_type_table array: " + localVarTypeTableLen);
		
		int start_pc;
		int length;
		for(int i=0; i<localVarTypeTableLen; i++)
		{
			start_pc = dis.readUnsignedShort();
			length = dis.readUnsignedShort();
			System.out.println("start_pc index is: " + start_pc + " and length is: " + length + "\nHence, the local variable must have a value at indices into the code array in the interval: " + start_pc + "and " + (start_pc+length-1));
			
			System.out.println("The variable name index in the constant pool array is: " + dis.readUnsignedShort());
			System.out.println("The variable type signature index in the constant pool array is: " + dis.readUnsignedShort());
			System.out.println("The index of local variable in the local variable array of the current frame: " + dis.readUnsignedShort());
		}
	}

	/**
	 * LocalVariableTable_attribute {
    	u2 attribute_name_index;
    	u4 attribute_length;
    	u2 local_variable_table_length;
    	{   u2 start_pc;
        	u2 length;
        	u2 name_index;
        	u2 descriptor_index;
        	u2 index;
    	} local_variable_table[local_variable_table_length];
		}
	 * @throws IOException 
	 */
	private void parseLocalVariableTable() throws IOException {
		int localVarTableLen = dis.readUnsignedShort();
		System.out.println(" The number of entries in the local_variable_table array: " + localVarTableLen);
		
		int start_pc;
		int length;
		for(int i=0; i<localVarTableLen; i++)
		{
			start_pc = dis.readUnsignedShort();
			length = dis.readUnsignedShort();
			System.out.println("start_pc index is: " + start_pc + " and length is: " + length + "\nHence, the local variable must have a value at indices into the code array in the interval: " + start_pc + "and " + (start_pc+length-1));
			
			System.out.println("The variable name index in the constant pool array is: " + dis.readUnsignedShort());
			System.out.println("The variable type descriptor index in the constant pool array is: " + dis.readUnsignedShort());
			System.out.println("The index of local variable in the local variable array of the current frame: " + dis.readUnsignedShort());
		}
	}

	/**
	 * 	LineNumberTable_attribute {
    		u2 attribute_name_index;
    		u4 attribute_length;
    		u2 line_number_table_length;
    		{   u2 start_pc;
        		u2 line_number;	
    		} line_number_table[line_number_table_length];
		}
	 */
	private void parseLineNumberTableAttribute() throws IOException {
		int lineNumTableLen = dis.readUnsignedShort();
		System.out.println(" The number of entries in the line_number_table array: " + lineNumTableLen);
		
		for(int i=0; i<lineNumTableLen; i++)
		{
			System.out.println("start_pc: The code for a new line in the original source file begins at code[" + dis.readUnsignedShort() + "]");
			System.out.println("line_number: The corresponding line number in the original source file - " + dis.readUnsignedShort());
		}
	}

	/**
	 * 	Code_attribute {
			u2 attribute_name_index;
			u4 attribute_length;
			u2 max_stack;
			u2 max_locals;
			u4 code_length;
			u1 code[code_length];
			u2 exception_table_length;
			{   u2 start_pc;
				u2 end_pc;
				u2 handler_pc;
				u2 catch_type;
			} exception_table[exception_table_length];
			u2 attributes_count;
			attribute_info attributes[attributes_count];
		}	
	*/
	private void parseCodeAttribute() throws IOException {
		
		int maxStack = dis.readUnsignedShort();
		int maxLocals = dis.readUnsignedShort();
		int codeLength = dis.readInt();
		String hexCodeByte = null;
		System.out.println(" The maximum depth of the operand stack of this method: " + maxStack);
		System.out.println(" The number of local variables in the local variable array allocated upon invocation of this method: " + maxLocals);
		System.out.println(" The number of bytes in the code array for this method: " + codeLength);
		int indexByte1 = 0;
		int indexByte2 = 0;
		int varIndex = 0;
		for(int j=0; j<codeLength;)
		{
			hexCodeByte = Integer.toHexString(dis.readUnsignedByte());
			j++;
			switch(hexCodeByte)
			{
				case "0": 	System.out.println("Operator:\tnop (0x" + hexCodeByte + ")");
							System.out.println("Operation:\tDo nothing" + "\n");
							break;
				case "1": 	System.out.println("Operator:\taconst_null (0x" + hexCodeByte + ")");
							System.out.println("Operation:\tPush null" + "\n");
							break;
				case "3": 	System.out.println("Operator:\ticonst_0 (0x" + hexCodeByte + ")");
							System.out.println("Operation:\tPush int constant 0 onto operand stack" + "\n");
							break;
				case "7": 	System.out.println("Operator:\ticonst_4 (0x" + hexCodeByte + ")");
							System.out.println("Operation:\tPush int constant 4 onto operand stack" + "\n");
							break;
				case "10": 	System.out.println("Operator:\tbipush (0x" + hexCodeByte + ")");
							System.out.println("Operation:\tPush byte" + "\n");
							break;
				case "11": 	System.out.println("Operator:\tsipush (0x" + hexCodeByte + ")");
							System.out.println("Operation:\tPush short");
							indexByte1 = dis.readUnsignedByte() << 8;
							j++;
							indexByte2 = dis.readUnsignedByte();
							j++;
							System.out.println("The index in the constant pool array for class reference is:" + (indexByte1 | indexByte2) + "\n");
							break;
				case "12": 	System.out.println("Operator:\tldc (0x" + hexCodeByte + ")");
							System.out.println("Operation:\tPush item from run-time constant pool index: " + dis.readUnsignedByte() +"\n");
							j++;
							break;
				case "13": 	System.out.println("Operator:\tldc_w (0x" + hexCodeByte + ")");
							System.out.println("Operation:\tPush item from run-time constant pool (wide index)" +"\n");
							indexByte1 = dis.readUnsignedByte() << 8;
							j++;
							indexByte2 = dis.readUnsignedByte();
							j++;
							System.out.println("The index in the constant pool array for class reference is:" + (indexByte1 | indexByte2) + "\n");
							break;
				case "14": 	System.out.println("Operator:\tldc2_w (0x" + hexCodeByte + ")");
							System.out.println("Operation:\tPush long or double from run-time constant pool (wide index)");
							indexByte1 = dis.readUnsignedByte() << 8;
							j++;
							indexByte2 = dis.readUnsignedByte();
							j++;
							System.out.println("The index in the constant pool array for class reference is:" + (indexByte1 | indexByte2) + "\n");
							break;
				case "15": 	System.out.println("Operator:\tiload (0x" + hexCodeByte + ")");
							System.out.println("Operation:\tLoad int from local variable" +"\n");
							System.out.println("The index into the local variable array of the current frame: " + dis.readUnsignedByte());
							j++;
							break;				
				case "19": 	System.out.println("Operator:\taload (0x" + hexCodeByte + ")");
							System.out.println("Operation:\tLoad reference from local variable" + "\n");
							System.out.println("The index into the local variable array of the current frame: "+ dis.readUnsignedByte());
							j++;
							break;
				case "1c": 	System.out.println("Operator:\tiload_<n> (0x" + hexCodeByte + ")");
							System.out.println("Operation:\tLoad int from local variable" + "\n");
							break;
				case "20": 	System.out.println("Operator:\tlload_n (0x" + hexCodeByte + ")");
							System.out.println("Operation:\tLoad long from local variable" + "\n");
							break;
				case "2a":
				case "2b":
				case "2c":
				case "2d":	System.out.println("Operator:\taload_<n> (0x" + hexCodeByte + ")");
							System.out.println("Operation:\tLoad reference from local variable at index: " + varIndex +" (aload_" + varIndex +")\n");
							varIndex++;
							break;
				case "36": 	System.out.println("Operator:\tistore (0x" + hexCodeByte + ")");
							System.out.println("Operation:\tStore int into local variable" +"\n");
							System.out.println("The index into the local variable array of the current frame: " + dis.readUnsignedByte());
							j++;
							break;
				case "3a": 	System.out.println("Operator:\tastore (0x" + hexCodeByte + ")");
							System.out.println("Operation:\tStore reference into local variable" + "\n");
							System.out.println("The index into the local variable array of the current frame: " + dis.readUnsignedByte());
							j++;
							break;
				case "3c": 	System.out.println("Operator:\tistore_1 (0x" + hexCodeByte + ")");
							System.out.println("Operation:\tStore int into local variable. \'1\' is an index into the local variable array of the current frame" + "\n");
							break;
				case "3d": 	System.out.println("Operator:\tistore_2 (0x" + hexCodeByte + ")");
							System.out.println("Operation:\tStore int into local variable. \'2\' is an index into the local variable array of the current frame" + "\n");
							break;
				case "4c": 	System.out.println("Operator:\tastore_1 (0x" + hexCodeByte + ")");
							System.out.println("Operation:\tStore reference into local variable. \'1\' is an index into the local variable array of the current frame" + "\n");
							break;
				case "4d": 	System.out.println("Operator:\tastore_2 (0x" + hexCodeByte + ")");
							System.out.println("Operation:\tStore reference into local variable. \'2\' is an index into the local variable array of the current frame" + "\n");
							break;
				case "4e": 	System.out.println("Operator:\tastore_3 (0x" + hexCodeByte + ")");
							System.out.println("Operation:\tStore reference into local variable. \'3\' is an index into the local variable array of the current frame" + "\n");
							break;
				case "57": 	System.out.println("Operator:\tpop (0x" + hexCodeByte + ")");
							System.out.println("Operation:\tPop the top operand stack value" + "\n");
							break;
				case "59": 	System.out.println("Operator:\tdup (0x" + hexCodeByte + ")");
							System.out.println("Operation:\tDuplicate the top operand stack value" + "\n");
							break;
				case "60": 	System.out.println("Operator:\tiadd (0x" + hexCodeByte + ")");
							System.out.println("Operation:\tAdd int" + "\n");
							break;
				case "61": 	System.out.println("Operator:\tladd (0x" + hexCodeByte + ")");
							System.out.println("Operation:\tAdd long" + "\n");
							break;
				case "84": 	System.out.println("Operator:\tiinc (0x" + hexCodeByte + ")");
							System.out.println("Operation:\tIncrement local variable by constant");
							System.out.println("The index into the local variable array of the current frame: " + dis.readUnsignedByte());
							j++;
							System.out.println("The incremented value is:" + dis.readUnsignedByte() + "\n");
							j++;
							break;
				case "a7": 	System.out.println("Operator:\tgoto (0x" + hexCodeByte + ")");
							System.out.println("Operation:\tBranch always");
							indexByte1 = dis.readUnsignedByte() << 8;
							j++;
							indexByte2 = dis.readUnsignedByte();
							j++;
							System.out.println("The opcode of an instruction within the method that contains this goto instruction: " + (indexByte1 | indexByte2) + "\n");
							break;
				case "ac": 	System.out.println("Operator:\tireturn (0x" + hexCodeByte + ")");
							System.out.println("Operation:\tReturn int from method" + "\n");
							break;
				case "b1": 	System.out.println("Operator:\treturn (0x" + hexCodeByte + ")");
							System.out.println("Operation:\tReturn void from method" + "\n");
							break;
				case "b2": 	System.out.println("Operator:\tgetstatic (0x" + hexCodeByte + ")");
							System.out.println("Operation:\tGet static field in class");
							indexByte1 = dis.readUnsignedByte() << 8;
							j++;
							indexByte2 = dis.readUnsignedByte();
							j++;
							System.out.println("The index in the constant pool array for field reference is:" + (indexByte1 | indexByte2) + "\n");
							break;
				case "b3": 	System.out.println("Operator:\tputstatic (0x" + hexCodeByte + ")");
							System.out.println("Operation:\tSet static field in class");
							indexByte1 = dis.readUnsignedByte() << 8;
							j++;
							indexByte2 = dis.readUnsignedByte();
							j++;
							System.out.println("The index in the constant pool array for field reference is:" + (indexByte1 | indexByte2) + "\n");
							break;
				case "b4": 	System.out.println("Operator:\tgetfield (0x" + hexCodeByte + ")");
							System.out.println("Operation:\tFetch field from object");
							indexByte1 = dis.readUnsignedByte() << 8;
							j++;
							indexByte2 = dis.readUnsignedByte();
							j++;
							System.out.println("The index in the constant pool array for field reference is:" + (indexByte1 | indexByte2) + "\n");
							break;
				case "b5": 	System.out.println("Operator:\tputfield (0x" + hexCodeByte + ")");
							System.out.println("Operation:\tSet field in object");
							indexByte1 = dis.readUnsignedByte() << 8;
							j++;
							indexByte2 = dis.readUnsignedByte();
							j++;
							System.out.println("The index in the constant pool array for class reference is:" + (indexByte1 | indexByte2) + "\n");
							break;
				case "b6": 	System.out.println("Operator:\tinvokevirtual (0x" + hexCodeByte + ")");
							System.out.println("Operation:\tInvoke instance method; dispatch based on class");
							indexByte1 = dis.readUnsignedByte() << 8;
							j++;
							indexByte2 = dis.readUnsignedByte();
							j++;
							System.out.println("The index in the constant pool array for class reference is:" + (indexByte1 | indexByte2) + "\n");
							break;
				case "b7": 	System.out.println("Operator:\tinvokeSpecial (0x" + hexCodeByte + ")");
							System.out.println("Operation:\tInvoke instance method");
							indexByte1 = dis.readUnsignedByte() << 8;
							j++;
							indexByte2 = dis.readUnsignedByte();
							j++;
							System.out.println("The index in the constant pool array for method reference is:" + (indexByte1 | indexByte2) + "\n");
							break;
				case "b8": 	System.out.println("Operator:\tinvokestatic (0x" + hexCodeByte + ")");
							System.out.println("Operation:\tInvoke a class (static) method");
							indexByte1 = dis.readUnsignedByte() << 8;
							j++;
							indexByte2 = dis.readUnsignedByte();
							j++;
							System.out.println("The index in the constant pool array for method reference is:" + (indexByte1 | indexByte2) + "\n");
							break;
				case "b9": 	System.out.println("Operator:\tinvokeinterface (0x" + hexCodeByte + ")");
							System.out.println("Operation:\tInvoke interface method");
							indexByte1 = dis.readUnsignedByte() << 8;
							j++;
							indexByte2 = dis.readUnsignedByte();
							j++;
							System.out.println("The index in the constant pool array for method reference is:" + (indexByte1 | indexByte2) + "\n");
							System.out.println("Count operand: " + dis.readUnsignedByte() +"\n");
							j++;
							System.out.println("0: " + dis.readUnsignedByte() +"\n");
							j++;
							break;
				case "bb": 	System.out.println("Operator:\tnew (0x" + hexCodeByte + ")");
							System.out.println("Operation:\tCreate new object");
							indexByte1 = dis.readUnsignedByte() << 8;
							j++;
							indexByte2 = dis.readUnsignedByte();
							j++;
							System.out.println("The index in the constant pool array for class reference is:" + (indexByte1 | indexByte2) + "\n");
							break;
				case "bc": 	System.out.println("Operator:\tnewarray (0x" + hexCodeByte + ")");
							System.out.println("Operation:\tCreate new array" + "\n");
							System.out.println("Code that indicates the type of array to create: "+ dis.readUnsignedByte());
							j++;
							break;
				case "c0": 	System.out.println("Operator:\tcheckcast (0x" + hexCodeByte + ")");
							System.out.println("Operation:\tCheck whether object is of given type");
							indexByte1 = dis.readUnsignedByte() << 8;
							j++;
							indexByte2 = dis.readUnsignedByte();
							j++;
							System.out.println("The index in the constant pool array for class reference is:" + (indexByte1 | indexByte2) + "\n");
							break;
			}
		}
		int exceptionTableLength = dis.readUnsignedShort();
		System.out.println(" The number of entries in the exception_table table: " + exceptionTableLength);
		
		for(int j=0; j<exceptionTableLength;j++)
		{
			System.out.println("start_pc: " + dis.readUnsignedShort());
			System.out.println("end_pc: " + dis.readUnsignedShort());
			System.out.println("handler_pc: " + dis.readUnsignedShort());
			System.out.println("catch_type: " + dis.readUnsignedShort());
		}
		int attributeCount = dis.readUnsignedShort();
		System.out.println(" The number of attributes of the Code attribute: " + attributeCount);
		
		attributeLookup(attributeCount);
		System.out.println("\n");
	}

	private void parseFieldInfo() throws IOException {
		int fieldCount = dis.readUnsignedShort();
		System.out.println("No. of Fields: " + fieldCount + "\n");

		for (int k = 0; k < fieldCount; k++) {
			String hexBitMask = Integer.toHexString(dis.readUnsignedShort());
			String accessFlagValue = FIELD_ACCESS_FLAG_MASK_MAP.get(hexBitMask);
			if (null != accessFlagValue) {
				System.out.println("Field Access Flag set is: " + accessFlagValue);
			} else {
				System.out.println("Hex representation of Access Flag is: " + hexBitMask);
			}
			int fieldNameIndex = dis.readUnsignedShort();
			System.out.println("Field name index in the constant pool array is: " + fieldNameIndex);

			int fieldDescriptorIndex = dis.readUnsignedShort();
			System.out.println("Field descriptor index in the constant pool array is: " + fieldDescriptorIndex);

			int attributeCount = dis.readUnsignedShort();
			System.out.println("No. of Attributes for the field: " + attributeCount);

			/*for (int i = 0; i < attributeCount; i++) {
				int attributeNameIndex = dis.readUnsignedShort();
				System.out.println("Attribute name index in the constant pool array is: " + attributeNameIndex);

				int attributeLength = dis.readInt();
				System.out.println("Attribute Length is: " + attributeLength);
				for (int j = 0; j < attributeLength; j++) {
					System.out.println("Attribute info[" + i + "] - index of attribute type in the constant pool array is: " + dis.readByte());
				}
			}*/
			attributeLookup(attributeCount);
			System.out.println("\n");
		}
	}

	private void parseInterfaces() throws IOException {
		int interface_count = dis.readUnsignedShort();
		System.out.println("No. of Interfaces: " + interface_count + "\n");
		
		for (int i = 0; i < interface_count; i++) {
			System.out.println("Inteface["+i+"] : " + dis.readUnsignedShort());
		}
	}

	private void parseSuperClassNameIndex() throws IOException {
		int superClassIndex = dis.readUnsignedShort();
		System.out.println("Super Class name index in the constant pool array is: " + superClassIndex);
	}

	private void parseClassNameIndex() throws IOException {
		int classIndex = dis.readUnsignedShort();
		System.out.println("Class name index in the constant pool array is: " + classIndex);
	}

	private void parseAccessFlag() throws IOException {

		String hexBitMask = Integer.toHexString(dis.readUnsignedShort());
		String accessFlagValue = CLASS_ACCESS_FLAG_MASK_MAP.get(hexBitMask);
		if (null != accessFlagValue) {
			System.out.println("Class Access Flag set is: " + accessFlagValue);
		} else {
			System.out.println("Hex representation of Access Flag is: " + hexBitMask);
		}
	}

	private void parseConstantPool() throws IOException {
		int cp_size = dis.readUnsignedShort();
		System.out.println("Constant pool size: " + cp_size + "\n");
		constantPoolArray = new Object[cp_size];
		Map<Integer, Object> contentMap;
		for (int i = 1; i < cp_size; i++) {
			int tag_byte = dis.readUnsignedByte();
			contentMap = new HashMap<Integer, Object>();
			switch (tag_byte) {
			case UTF8:
				int numChar = dis.readUnsignedShort();
				byte[] byteArray = new byte[numChar];
				System.out.println("Length of the utf8 array is: " + numChar);
				dis.read(byteArray, 0, numChar);
				System.out.println("Constant_pool[" + i + "] tag byte:\tUTF-8 - " + new String(byteArray, "UTF-8"));
				contentMap.put(UTF8, new String(byteArray, "UTF-8"));
				constantPoolArray[i] = contentMap;
				break;
			case INTEGER:
				int intValue = dis.readInt();
				System.out.println("Constant_pool[" + i + "] tag byte:\tINTEGER - " + intValue);
				contentMap.put(INTEGER, intValue);
				constantPoolArray[i] = contentMap;
				break;
			case FLOAT:
				float floatValue = dis.readFloat();
				System.out.println("Constant_pool[" + i + "] tag byte:\tFLOAT - " + floatValue);
				contentMap.put(FLOAT, floatValue);
				constantPoolArray[i] = contentMap;
				break;
			case LONG:
				int highByte = dis.readInt();
				int lowByte = dis.readInt();
				int[] longArray ={highByte, lowByte};
				System.out.println("Constant_pool[" + i + "] tag byte:\tHigh byte - " + highByte);
				System.out.println("Constant_pool[" + (i + 1) + "] tag byte:\tLow byte - " + lowByte);
				contentMap.put(LONG, longArray);
				constantPoolArray[i] = contentMap;
				i++;
				break;
			case DOUBLE:
				double doubleValue = dis.readDouble();
				System.out.println("Constant_pool[" + i + "] tag byte:\tDOUBLE - " + doubleValue);
				contentMap.put(DOUBLE, doubleValue);
				constantPoolArray[i] = contentMap;
				i++;
				break;
			case CLASS:
				int classNameIndex = dis.readUnsignedShort();
				System.out.println("Constant_pool[" + i + "] tag byte:\tCLASS_REF - Constant_pool["+ classNameIndex + "]");
				contentMap.put(CLASS, "CP_IDX_" + classNameIndex);
				constantPoolArray[i] = contentMap;
				break;
			case STRING:
				int stringIndex = dis.readUnsignedShort(); 
				System.out.println("Constant_pool[" + i + "] tag byte:\tSTRING_REF - Constant_pool["
						+ stringIndex + "]");
				contentMap.put(STRING, "CP_IDX_" + stringIndex);
				constantPoolArray[i] = contentMap;
				break;
			case FIELD_REF:
				classNameIndex = dis.readUnsignedShort();
				int nameTypeIndex = dis.readUnsignedShort();
				int[] fieldRefArray = {classNameIndex, nameTypeIndex};
				System.out.println("Constant_pool[" + i + "] tag byte:\tFIELD_REF - Class index: Constant_pool["
						+ classNameIndex + "] and name and type index: Constant_pool["
						+ nameTypeIndex + "]");
				contentMap.put(FIELD_REF, fieldRefArray);
				constantPoolArray[i] = contentMap;
				break;
			case METHOD_REF:
				classNameIndex = dis.readUnsignedShort();
				nameTypeIndex = dis.readUnsignedShort();
				int[] methodRefArray = {classNameIndex, nameTypeIndex};
				System.out.println("Constant_pool[" + i + "] tag byte:\tMETHOD_REF - Class index: Constant_pool["
						+ classNameIndex + "] and name and type index: Constant_pool["
						+ nameTypeIndex + "]");
				contentMap.put(METHOD_REF, methodRefArray);
				constantPoolArray[i] = contentMap;
				break;
			case INTERFACE_METHOD_REF:
				classNameIndex = dis.readUnsignedShort();
				nameTypeIndex = dis.readUnsignedShort();
				int[] interfaceMethodRefArray = {classNameIndex, nameTypeIndex};
				System.out.println("Constant_pool[" + i
						+ "] tag byte:\tINTERFACE_METHOD_REF - Class index: Constant_pool[" + classNameIndex
						+ "] and name and type index: Constant_pool[" + nameTypeIndex + "]");
				contentMap.put(INTERFACE_METHOD_REF, interfaceMethodRefArray);
				constantPoolArray[i] = contentMap;
				break;
			case NAME_AND_TYPE:
				int nameIndex = dis.readUnsignedShort();
				int descriptorIndex = dis.readUnsignedShort();
				int[] nameTypeArray = {nameIndex, descriptorIndex};
				System.out.println("Constant_pool[" + i + "] tag byte:\tNAME_AND_TYPE_REF - Name index: Constant_pool["
						+ nameIndex + "] and descriptor index: Constant_pool[" + descriptorIndex + "]");
				contentMap.put(NAME_AND_TYPE, nameTypeArray);
				constantPoolArray[i] = contentMap;
				break;
			case METHOD_HANDLE_REF:
				int refKind = dis.readUnsignedByte();
				int refIndex = dis.readUnsignedShort();
				int[] methHandleRefArray = {refKind, refIndex};
				System.out.println("Constant_pool[" + i + "] tag byte:\tMETHOD_HANDLE_REF - Reference kind: "
						+ refKind + " and Reference index: Constant_pool[" + refIndex + "]");
				contentMap.put(METHOD_HANDLE_REF, methHandleRefArray);
				constantPoolArray[i] = contentMap;
				break;
			case METHOD_TYPE_REF:
				int methTypeRefIndex = dis.readUnsignedShort();
				System.out.println("Constant_pool[" + i + "] tag byte:\tMETHOD_TYPE_REF - Constant_pool["
						+ methTypeRefIndex + "]");
				contentMap.put(METHOD_TYPE_REF, "CP_IDX_" + methTypeRefIndex);
				constantPoolArray[i] = contentMap;
				break;
			case INVOKE_DYNAMIC_REF:
				int bootstrapMethIndex = dis.readUnsignedShort();
				nameTypeIndex = dis.readUnsignedShort();
				int[] invokeDynRefArray = {bootstrapMethIndex, nameTypeIndex};
				System.out.println("Constant_pool[" + i
						+ "] tag byte:\tINVOKE_DYNAMIC_REF - Bootstrap method attribute index: bootstraps_method["
						+ bootstrapMethIndex + "] and name and type index: Constant_pool["
						+ nameTypeIndex + "]");
				contentMap.put(INVOKE_DYNAMIC_REF, invokeDynRefArray);
				constantPoolArray[i] = contentMap;
				break;
			}

		}
		System.out.println("\n");

	}

	private void parseVersionNumber() throws IOException {
		int minorVersion = dis.readUnsignedShort();
		int majorVersion = dis.readUnsignedShort();
		System.out.println("Java Version used for compiling: " + JAVA_VERSION_MAPPING.get(majorVersion) + "."
				+ minorVersion + "\n");
	}

	/**
	 * Prints the magic number
	 * 
	 * @param dis
	 * @throws IOException
	 */
	private void parseMagicNumber() throws IOException {
		System.out.println("Magic number: " + Integer.toHexString(dis.readInt()) + "\n");
	}

}
