package org.frame.common.management;

import java.util.HashMap;
import java.util.Map;

import org.frame.common.constant.ICommonConstant;
import org.frame.common.management.server.Register;
import org.frame.common.util.Properties;

public class Common implements CommonMBean {

	private String password;
	
	private String keystore;
	
	private String alias;
	
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		Properties properties = new Properties(ICommonConstant.DEFAULT_CONFIG_PROPERTIES);
		properties.write("p", password);
		this.password = password;
	}

	public String getKeystore() {
		return keystore;
	}

	public void setKeystore(String keystore) {
		Properties properties = new Properties(ICommonConstant.DEFAULT_CONFIG_PROPERTIES);
		properties.write("c", keystore);
		this.keystore = keystore;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		Properties properties = new Properties(ICommonConstant.DEFAULT_CONFIG_PROPERTIES);
		properties.write("a", alias);
		this.alias = alias;
	}

	public Common() {
		Properties properties = new Properties(ICommonConstant.DEFAULT_CONFIG_PROPERTIES);
		this.alias = properties.getProperty("a");
		this.keystore = properties.getProperty("c");
		this.password = properties.getProperty("p");
	}
	
	public static void main(String[] args) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("org.frame.management.common:name=common", new Common());
		
		new Register(map);
	}
	
}
