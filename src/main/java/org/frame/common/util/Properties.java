/**
 * PropertiesUtil contains tools for java.util.Properties
 */
package org.frame.common.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class Properties extends java.util.Properties {
	
	private static final long serialVersionUID = 2071673630124620500L;
	
	private String name = "config.properties";
	
	/**
	 * constructor
	 */
	public Properties() {
		super();
	}
	
	public Properties(java.util.Properties defaults) {
		super(defaults);
	}
	
	/**
	 * constructor with specific properties in src
	 * 
	 * @param name name of properties
	 */
	public Properties(String name) {
		super();
		this.name = name;
		if (Thread.currentThread().getContextClassLoader().getResourceAsStream(name) == null) {
			throw new RuntimeException("properties not found: " + name);
		}
		
	}
	
	/**
	 * read content of default properties(src/config.properties) to map
	 * 
	 * @return map of content
	 */
	public Map<String, String> read() {
		Map<String, String> map = new HashMap<String, String>();
		InputStream inputStream = null;
		try {
			inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(this.name);
			this.load(inputStream);
			Enumeration<?> enumeration = this.propertyNames();
			String key = null;
			while (enumeration.hasMoreElements()) {
				key = (String) enumeration.nextElement();
				map.put(key, this.getProperty(key));
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				if (inputStream != null) inputStream.close();
			} catch (IOException e) {
				inputStream = null;
				e.printStackTrace();
			}
		}
		
		return map;
	}
	
	/**
	 * read content of properties to map
	 * 
	 * @param file properties file to be read
	 * 
	 * @return map of content
	 */
	public Map<String, String> read(File file) {
		Map<String, String> map = new HashMap<String, String>();
		InputStream inputStream = null;
		try {
			inputStream = new BufferedInputStream (new FileInputStream(file));
			this.load(inputStream);
			Enumeration<?> enumeration = this.propertyNames();
			String key = null;
			while (enumeration.hasMoreElements()) {
				key = (String) enumeration.nextElement();
				map.put(key, this.getProperty(key));
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				if (inputStream != null) inputStream.close();
			} catch (IOException e) {
				inputStream = null;
				e.printStackTrace();
			}
		}
		
		return map;
	}

	/**
	 * read value corresponds to key from properties
	 * 
	 * @param key key of properties keys
	 * 
	 * @return value corresponds to key
	 */
	public String read(String key) {
		InputStream inputStream = null;
		try {
			inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(this.name);
			this.load(inputStream);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				if (inputStream != null) inputStream.close();
			} catch (IOException e) {
				inputStream = null;
				e.printStackTrace();
			}
		}
		return this.getProperty(key);
	}
	
	/**
	 * read value corresponds to key from specified properties
	 * 
	 * @param filePath file path of specified properties
	 * @param key key of properties keys
	 * 
	 * @return value corresponds to key
	 */
	public String read(String filePath, String key) {
		return this.read(new File(filePath), key);
	}
	
	/**
	 * read value corresponds to key from specified properties
	 * 
	 * @param file specified properties file
	 * @param key key of properties keys
	 * 
	 * @return value corresponds to key
	 */
	public String read(File file, String key) {
		InputStream inputStream = null;
		try {
			if (file.exists()) {
				inputStream = new FileInputStream(file);
				this.load(inputStream);
			} else {
				System.err.println("file not found. file name: " + file.getName());
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				if (inputStream != null) inputStream.close();
			} catch (IOException e) {
				inputStream = null;
				e.printStackTrace();
			}
		}
		return this.getProperty(key);
	}
	
	/**
	 * remove a key-value record from properties
	 * 
	 * @param key key of record
	 * @param value value of record
	 */
	public void remove(String key) {
		this.remove(key, null);
	}
	
	/**
	 * remove a key-value record from properties
	 * 
	 * @param key key of record
	 * @param value value of record
	 * @param comment comment of record
	 */
	public void remove(String key, String comment) {
		InputStream inputStream = null;
		try {
			inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(this.name);
			this.load(inputStream);
			this.remove(key);
			
			OutputStream outputStream = new FileOutputStream(new File(URLDecoder.decode(Thread.currentThread().getContextClassLoader().getResource(this.name).getPath(), "UTF-8")));
			this.store(outputStream, comment);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (inputStream != null) inputStream.close();
			} catch (IOException e) {
				inputStream = null;
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * remove a key-value record from specified properties
	 * 
	 * @param file specified properties file
	 * @param key key of record
	 * @param value value of record
	 */
	public void remove(File file, String key) {
		if (file.exists())
			this.remove(file, key, null);
		else
			System.err.println("file not found. file name: " + file.getName());
	}
	
	/**
	 * remove a key-value record from specified properties
	 * 
	 * @param filePath file path of specified properties
	 * @param key key of record
	 * @param value value of record
	 * @param comment comment of record
	 */
	public void remove(String filePath, String key, String comment) {
		this.remove(new File(filePath), key, comment);
	}
	
	/**
	 * remove a key-value record from specified properties
	 * 
	 * @param file specified properties file
	 * @param key key of record
	 * @param value value of record
	 * @param comment comment of record
	 */
	public void remove(File file, String key, String comment) {
		InputStream inputStream = null;
		try {
			if (file.exists()) {
				inputStream = new FileInputStream(file);
				this.load(inputStream);
				this.remove(key);
				
				OutputStream outputStream = new FileOutputStream(file);
				this.store(outputStream, comment);
			} else
				System.err.println("file not found. file name: " + file.getName());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (inputStream != null) inputStream.close();
			} catch (IOException e) {
				inputStream = null;
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * write a key-value record to properties
	 * 
	 * @param key key of record
	 * @param value value of record
	 */
	public void write(String key, String value) {
		this.write(key, value, null);
	}
	
	/**
	 * write a key-value record to properties
	 * 
	 * @param key key of record
	 * @param value value of record
	 * @param comment comment of record
	 */
	public void write(String key, String value, String comment) {
		InputStream inputStream = null;
		OutputStream outputStream = null;
		try {
			inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(this.name);
			this.load(inputStream);
			outputStream = new FileOutputStream(new File(URLDecoder.decode(Thread.currentThread().getContextClassLoader().getResource(this.name).getPath(), "UTF-8")));
			this.setProperty(key, value);
			this.store(outputStream, comment);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (inputStream != null) inputStream.close();
			} catch (IOException e) {
				inputStream = null;
				e.printStackTrace();
			}
			
			try {
				if (outputStream != null) outputStream.close();
			} catch (IOException e) {
				outputStream = null;
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * write a key-value record to specified properties
	 * 
	 * @param file specified properties file
	 * @param key key of record
	 * @param value value of record
	 */
	public void write(File file, String key, String value) {
		if (file.exists())
			this.write(file, key, value, null);
		else
			System.err.println("file not found. file name: " + file.getName());
	}
	
	/**
	 * write a key-value record to specified properties
	 * 
	 * @param filePath file path of specified properties
	 * @param key key of record
	 * @param value value of record
	 * @param comment comment of record
	 */
	public void write(String filePath, String key, String value, String comment) {
		this.write(new File(filePath), key, value, comment);
	}
	
	/**
	 * write a key-value record to specified properties
	 * 
	 * @param file specified properties file
	 * @param key key of record
	 * @param value value of record
	 * @param comment comment of record
	 */
	public void write(File file, String key, String value, String comment) {
		InputStream inputStream = null;
		OutputStream outputStream = null;
		try {
			if (file.exists()) {
				inputStream = new FileInputStream(file);
				this.load(inputStream);
				outputStream = new FileOutputStream(file);
				this.setProperty(key, value);
				this.store(outputStream, comment);
			} else
				System.err.println("file not found. file name: " + file.getName());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (inputStream != null) inputStream.close();
			} catch (IOException e) {
				inputStream = null;
				e.printStackTrace();
			}
			
			try {
				if (outputStream != null) outputStream.close();
			} catch (IOException e) {
				outputStream = null;
				e.printStackTrace();
			}
		}
	}
}
