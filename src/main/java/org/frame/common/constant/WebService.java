package org.frame.common.constant;

import java.util.HashMap;
import java.util.Map;

import org.frame.common.webservice.server.demo.impl.Hello;


public class WebService {
	
	private Map<String, Object> map = new HashMap<String, Object>();
	
	public Map<String, Object> getMap() {
		return map;
	}

	public void setMap(Map<String, Object> map) {
		this.map = map;
	}

	public WebService() {
		map.put("http://localhost:8081/HelloService", new Hello());
	}
	
	/*public static void main(String[] args) {
		System.out.println((Object[]) null);
		System.out.println(null instanceof Map<?, ?>);
	}*/
	
}
