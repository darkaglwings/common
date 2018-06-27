package org.frame.common.health;

import java.util.HashMap;
import java.util.Map;

import org.frame.common.context.OperateSystem;
import org.frame.common.lang.StringHelper;


public class Supervisor {
	
	public Map<String, String> delay(String destination) {
		Map<String, String> result = null;
		
		if (OperateSystem.isWindows()) {
			result = new HashMap<String, String>();
			Process process = new OperateSystem().execute("ping " + destination);
			String recive = new StringHelper().inputStream2String(process.getInputStream(), "GBK");
			String[] info = recive.split("\n");
			recive = info[info.length - 1].trim();
			if (recive.indexOf("ms") == -1) {
				result.put("min", "-1");
				result.put("max", "-1");
				result.put("avg", "-1");
			} else {
				info = recive.split("，");
				for (int i = 0; i < info.length; i++) {
					result.put("min", info[0].split(" ")[2]);
					result.put("max", info[1].split(" ")[2]);
					result.put("avg", info[2].split(" ")[2]);
				}
			}
		} else if (OperateSystem.isLinux()) {
			result = new HashMap<String, String>();
			result = new HashMap<String, String>();
			Process process = new OperateSystem().execute("ping " + destination + " -c 4");
			String recive = new StringHelper().inputStream2String(process.getInputStream(), "GBK");
			String[] info = recive.split("\n");
			recive = info[info.length - 1].trim();
			if (recive.indexOf("=") == -1) {
				result.put("min", "-1");
				result.put("max", "-1");
				result.put("avg", "-1");
			} else {
				recive = recive.substring(recive.indexOf("=") + 1, recive.length()).replace("ms", "").trim();
				info = recive.split("/");
				for (int i = 0; i < info.length; i++) {
					result.put("min", info[0] + "ms");
					result.put("avg", info[1] + "ms");
					result.put("max", info[2] + "ms");
					result.put("mdev", info[3] + "ms");
				}
			}
		} else ;
		
		return result;
	}
	
	/*public static void main(String[] args) {
		new Supervisor().delay("10.10.10.10");
	}*/
	
}
