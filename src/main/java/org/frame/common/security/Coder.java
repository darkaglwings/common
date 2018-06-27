/**
 * Security contains encode and decode methods
 */
package org.frame.common.security;

import java.io.IOException;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class Coder {
	
	/**
	 * encode message with BASE64
	 * 
	 * @param message string to be encoded
	 * 
	 * @return encrypted string  
	 */
	public String base64Encoder(String message) {
		String result = null;
		if (message != null) {
			result = new BASE64Encoder().encodeBuffer(message.getBytes());
		}
		
		return result;
	}
	

	/**
	 * decode message with BASE64
	 * 
	 * @param message string to be decoded
	 * 
	 * @return decrypted string  
	 */
	public String base64Decoder(String message) {
		String result = null;
		if (message != null) {
			try {
				byte[] bytes = new BASE64Decoder().decodeBuffer(message);
				result = new String(bytes);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return result;
	}
	
	/**
	 * encode message
	 * 
	 * @param message string to be encoded
	 * 
	 * @return encrypted string  
	 */
	public String encode(String message) {
		String result = null;
		
		if (message != null && !"".equals(message) && !"null".equals(message)) {
			char[] character = message.toCharArray();
			
			for (int i = 0; i < character.length; i++) {
				character[i] = (char) (character[i] ^ 't');
			}
			
			result = new String(character);
		}
		
		return result;
	}
	
	/**
	 * decode message
	 * 
	 * @param message string to be decoded
	 * 
	 * @return decrypted string  
	 */
	public String decode(String message) {
		String result = null;
		
		if (message != null && !"".equals(message) && !"null".equals(message)) {
			char[] character = message.toCharArray();
			for (int i = 0; i < character.length; i++) {
				character[i] = (char) (character[i] ^ 't');
			}
			result = new String(character);
		}
		
		return result;
	}
	
	/*public static void main(String[] args) {
		Coder coder = new Coder(); 
		System.out.println(coder.base64Encoder(coder.encode("JKS")));
		System.out.println(coder.base64Encoder(coder.encode("changeit")));
		System.out.println(coder.base64Encoder(coder.encode("C:/Program Files/Java/jdk1.7.0_03/jre/lib/security/cacerts")));
		System.out.println(coder.base64Encoder(coder.encode("chinajavaworld")));
	}*/
	
}
