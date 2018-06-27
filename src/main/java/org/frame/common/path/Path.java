/**
 * Path contains web project paths
 */
package org.frame.common.path;

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;

public class Path {
	
	private String encoding = "UTF-8";
	
	/**
	 * constructor with default encoding(UTF-8)
	 */
	public Path() {
		
	}
	
	/**
	 * constructor with specific encoding
	 * 
	 * @param encoding path encoding
	 */
	public Path(String encoding) {
		this.encoding = encoding;
	}
	
	/**
	 * to find bin
	 * 
	 * @return path of bin
	 */
	
	public String bin() {
		
		String bin = "";
		try {
			bin = new File(java.net.URLDecoder.decode(Thread.currentThread().getContextClassLoader().getResource(".").getPath(), encoding)).getAbsolutePath().replace("\\", "/");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return bin;
	}
	
	public String getPath(String path) {
		String result = "";
		try {
			result = java.net.URLDecoder.decode(Thread.currentThread().getContextClassLoader().getResource("").getPath(), encoding);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return result;
		
	}
	
	/**
	 * to find classes
	 * 
	 * @return path of classes
	 */
	public String classes() {
		String classes = "";
		try {
			classes = new File(java.net.URLDecoder.decode(Thread.currentThread().getContextClassLoader().getResource("/").getPath(), encoding)).getAbsolutePath().replace("\\", "/");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return classes;
	}
	
	/**
	 * to find lib
	 * 
	 * @return path of lib
	 */
	
	public String lib() {
		String lib = "";
		try {
			lib = new File(java.net.URLDecoder.decode(Thread.currentThread().getContextClassLoader().getResource("").getPath(), encoding)).getAbsolutePath().replace("\\", "/");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return lib;
	}
	
	public String parent(String path) {
		String parent = "";
		try {
			parent = new File(java.net.URLDecoder.decode(Thread.currentThread().getContextClassLoader().getResource("").getPath(), encoding)).getParent().replace("\\", "/");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return parent;
	}
	
	/**
	 * to find resource
	 * 
	 * @param path file path of folder relative to src
	 * 
	 * @return path of folder
	 */
	
	public String resource(String path) {
		String result = "";
		try {
			result = java.net.URLDecoder.decode(Thread.currentThread().getContextClassLoader().getResource(path).getPath(), this.encoding);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	public InputStream resourceAsStream(String path) {
		return Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
	}
	
	/**
	 * to find application
	 * 
	 * @return path of application
	 */
	
	public String server() {
		File file = new File(this.web_root());
		return new File(file.getParent()).getParent().replace("\\", "/");
	}
	
	/**
	 * to find src
	 * 
	 * @return path of src
	 */
	public String src() {
		return System.getProperty("user.dir").replace("\\", "/") + "/src";
	}
	
	/**
	 * to find WEB-INF
	 * 
	 * @return path of WEB-INF
	 */
	public String web_inf() {
		File file = new File(this.classes());
		return (file.getParent()).replace("\\", "/");
	}
	
	/**
	 * to find web application context path
	 * 
	 * @param request http servlet request
	 * 
	 * @return web application context path
	 */
	public String web_path(HttpServletRequest request) {
		return request.getContextPath();
	}
	
	/**
	 * to find webRoot
	 * 
	 * @return path of webRoot
	 */
	public String web_root() {
		File file = new File(this.classes());
		return new File(file.getParent()).getParent().replace("\\", "/");
	}
	
	/**
	 * to find web application url
	 * 
	 * @param request http servlet request
	 * 
	 * @return web application url
	 */
	public String web_url(HttpServletRequest request) {
		return request.getScheme() + "://" +request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
	}
	
	/*public static void main(String[] args) {
		System.out.println(System.getProperty("user.dir"));
		System.out.println(new Path().bin());
	}*/
	
}
