package com.guptae.structure;

public class ClassFileStructure {
	/**
	 * u4 - four byte item
	 */
	private byte[] magicNum = new byte[4];
	/**
	 * u2 - two byte item
	 */
	private byte[] minor_version = new byte[2];
	/**
	 * u2 - two byte item
	 */
	private byte[] major_version = new byte[2];
	/**
	 * u2 - two byte item
	 */
	private byte[] cp_count = new byte[2];
	/**
	 * variable bytes sized table
	 */
	private byte[] constant_pool_info;
	/**
	 * u2 - two byte item
	 */
	private byte[] access_flag = new byte[2];
	/**
	 * u2 - two byte item
	 */
	private byte[] this_class = new byte[2];
	/**
	 * u2 - two byte item
	 */
	private byte[] super_class = new byte[2];
	/**
	 * u2 - two byte item
	 */
	private byte[] interface_count = new byte[2];
	/**
	 * u2 - two byte item
	 */
	private byte[] interface_info = new byte[2];
	/**
	 * u2 - two byte item
	 */
	private byte[] fields_count = new byte[2];
	/**
	 * variable bytes sized table
	 */
	private byte[] fields_info;
	/**
	 * u2 - two byte item
	 */
	private byte[] method_count = new byte[2];
	/**
	 * variable bytes sized table
	 */
	private byte[] method_info;
	/**
	 * u2 - two byte item
	 */
	private byte[] attribute_count = new byte[2];
	/**
	 * variable bytes sized table
	 */
	private byte[] attribute_info;

	/**
	 * 
	 * @return
	 */
	public byte[] getMagicNum() {
		return magicNum;
	}
	
	public byte[] getMinor_version() {
		return minor_version;
	}
	
	public byte[] getMajor_version() {
		return major_version;
	}
	
	public byte[] getCp_count() {
		return cp_count;
	}
	
	public byte[] getConstant_pool_info() {
		return constant_pool_info;
	}
	
	public byte[] getAccess_flag() {
		return access_flag;
	}
	
	public byte[] getThis_class() {
		return this_class;
	}
	
	public byte[] getSuper_class() {
		return super_class;
	}
	
	public byte[] getInterface_count() {
		return interface_count;
	}
	
	public byte[] getInterface_info() {
		return interface_info;
	}
	
	public byte[] getFields_count() {
		return fields_count;
	}
	
	public byte[] getFields_info() {
		return fields_info;
	}
	
	public byte[] getMethod_count() {
		return method_count;
	}
	
	public byte[] getMethod_info() {
		return method_info;
	}
	
	public byte[] getAttribute_count() {
		return attribute_count;
	}
	
	public byte[] getAttribute_info() {
		return attribute_info;
	}
	
	
}
