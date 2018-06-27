package org.frame.common.algorithm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.frame.common.lang.StringHelper;

public class Hash {
	
	public enum HASH_TYPE {
		MD5("md5");

		private HASH_TYPE(String type){

		}
	}
	
	public String hash(File file, HASH_TYPE type) {
		String result = null;
		
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(file);
			byte[] buffer = new byte[1024];
			MessageDigest md5 = MessageDigest.getInstance(type.toString());
			int numRead = 0;
			while ((numRead = inputStream.read(buffer)) > 0) {
				md5.update(buffer, 0, numRead);
			}
			
			result = new StringHelper().byte2Hex(md5.digest());
		} catch (FileNotFoundException e) {
			System.err.println("file not found: " + file.getAbsolutePath());
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (inputStream != null) inputStream.close();
			} catch (IOException e) {
				inputStream = null;
				e.printStackTrace();
			}
		}
		
		return result;
	}
	
	public String hash(String content, HASH_TYPE type) {
		String result = null;
		
		try {
			MessageDigest md5 = MessageDigest.getInstance(type.toString());
			md5.update(content.getBytes(), 0, content.getBytes().length);
			result = new StringHelper().byte2Hex(md5.digest());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	public Long hash(String content, HASH_TYPE type, int times) {
		Long result = null;
		
		try {
			MessageDigest md5 = MessageDigest.getInstance(type.toString());
			md5.update(content.getBytes(), 0, content.getBytes().length);
			byte[] digest = md5.digest();
			
			result = ((long)(digest[3 + times * 4] & 0xFF) << 24)
					| ((long)(digest[2 + times * 4] & 0xFF) << 16)
					| ((long)(digest[1 + times * 4] & 0xFF) << 8)
					| ((long)digest[0 + times * 4] & 0xFF);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	public static void main(String[] args) throws Exception {
		Hash hash = new Hash();
		
		/*String fileName = "d:/系统建设方案模板文件.docx";
		HASH_TYPE hashType = HASH_TYPE.MD5;
		System.out.println(hashType + " == " + hash.hash(new File(fileName), hashType));
		
		fileName = "d:/系统建设方案模板文件2.docx";
		System.out.println(hashType + " == " + hash.hash(new File(fileName), hashType));
		
		System.out.println("两次md5值相同,即使文件名变了,也表示两个文件内容一致,未被篡改");
		
		fileName = "d:/系统建设方案模板文件3.docx";
		System.out.println(hashType + " == " + hash.hash(new File(fileName), hashType));
		
		System.out.println("和之前的MD5值不一致,文件被改动过");*/
		
		System.out.println(hash.hash("aaa", HASH_TYPE.MD5, 0));
		System.out.println(hash.hash("aab", HASH_TYPE.MD5, 0));
		System.out.println(hash.hash("aac", HASH_TYPE.MD5, 0));
	}
}
