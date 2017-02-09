package com.yulore.bigdata.data_processor.util;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

public class CypherUtil {
	
	public static byte[] encodeBase64(String data){
		
		return Base64.encodeBase64(data.getBytes());
	}
	
	public static String encodeBase64String(String data){
		
		return Base64.encodeBase64String(data.getBytes());
	}
	
	public static byte[] decodeBase64(String data){
		
		return Base64.decodeBase64(data);
	}

	public static String decodeBase64String(String data){
		
		return new String(Base64.decodeBase64(data));
	}
	
	public static String md5Hex(String data) {
		
		return DigestUtils.md5Hex(data);
	}
	
	public static byte[] md5(String data) {
		
		return DigestUtils.md5(data);
	}
	
	public static byte[] sha1(String data) {
		
		return DigestUtils.sha(data);
	}
	
	public static String sha1Hex(String data) {
		
		return DigestUtils.shaHex(data);
	}
	
	public static byte[] sha256(String data) {
		
		return DigestUtils.sha256(data);
	}
	
	public static String sha256Hex(String data) {
		
		return DigestUtils.sha256Hex(data);
	}
	
	public static byte[] sha512(String data) {
		
		return DigestUtils.sha512(data);
	}
	
	public static String sha512Hex(String data) {
		
		return DigestUtils.sha512Hex(data);
	}

}