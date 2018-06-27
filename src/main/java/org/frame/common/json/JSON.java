/**
 * JSON contains tools for json
 * 
 * JSON NEEDS JARS:
 * commons-beanutils-core-1.8.3.jar
 * commons-collections-3.2.1.jar
 * commons-lang-exception-2.0.jar
 * commons-lang3-3.1.jar
 * commons-logging-1.1.1.jar
 * ezmorph-1.0.6.jar
 * groovy-2.0.5.jar
 * oro-2.0.8.jar
 * xom-1.2.8.jar
 */
package org.frame.common.json;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;

import org.frame.common.json.model.JsonDateValueProcessor;

public class JSON {
	
	/**
	 * convert json string to array
	 * 
	 * @param jsonString json string to be converted
	 * 
	 * @return object[] data
	 */
	public Object[] toArray(String jsonString) {
		return JSONArray.fromObject(jsonString).toArray();
	}
	
	/**
	 * convert json to java bean
	 * 
	 * @param jsonString json string to be converted
	 * @param clazz class of java bean to convert
	 * 
	 * @return instance of java bean
	 */
	public Object toBean(String jsonString, Class<?> clazz) {
		JSONObject jsonObject = JSONObject.fromObject(jsonString);
		return JSONObject.toBean(jsonObject, clazz);
	}
	
	/**
	 * convert json string to Integer array
	 * 
	 * @param jsonString json string to be converted
	 * 
	 * @return integer[] data
	 */
	public Integer[] toIntegerArray(String jsonString) {
		JSONArray jsonArray = JSONArray.fromObject(jsonString);
		Integer[] result = new Integer[jsonArray.size()];
		for (int i = 0; i < jsonArray.size(); i++) {
			result[i] = jsonArray.getInt(i);
		}
		
		return result;
	}
	
	/**
	 * convert json string to list
	 * 
	 * @param jsonString json string to be converted
	 * @param clazz class of element in list
	 * 
	 * @return list data
	 */
	public List<Object> toList(String jsonString, Class<?> clazz) {
		JSONArray jsonArray = JSONArray.fromObject(jsonString);
		List<Object> list = new ArrayList<Object>();
		for (int i = 0; i < jsonArray.size(); i++) {
			list.add(JSONObject.toBean(jsonArray.getJSONObject(i), clazz));
		}

		return list;
	}
	
	/**
	 * convert json string to Long array
	 * 
	 * @param jsonString json string to be converted
	 * 
	 * @return long[] data
	 */
	public Long[] toLongArray(String jsonString) {
		JSONArray jsonArray = JSONArray.fromObject(jsonString);
		Long[] result = new Long[jsonArray.size()];
		for (int i = 0; i < jsonArray.size(); i++) {
			result[i] = jsonArray.getLong(i);
		}
		return result;
	}
	
	/**
	 * convert json string to map
	 * 
	 * @param jsonString json string to be converted
	 * 
	 * @return map data
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> toMap(String jsonString) {
		JSONObject jsonObject = JSONObject.fromObject(jsonString);
		Iterator<String> iter = (Iterator<String>) jsonObject.keys();
		Map<String, Object> map = new HashMap<String, Object>();

		String key;
		while (iter.hasNext()) {
			key = iter.next();
			map.put(key, jsonObject.get(key));
		}

		return map;
	}
	
	/**
	 * convert json string to string array
	 * 
	 * @param jsonString json string to be converted
	 * 
	 * @return string[] data
	 */
	public String[] toStringArray(String jsonString) {
		JSONArray jsonArray = JSONArray.fromObject(jsonString);
		String[] result = new String[jsonArray.size()];
		for (int i = 0; i < jsonArray.size(); i++) {
			result[i] = jsonArray.getString(i);
		}

		return result;
	}      

	/**
	 * convert json string to date array with default pattern(yyyy-MM-dd HH:mm:ss)
	 * 
	 * @param jsonString json string to be converted
	 * 
	 * @return date[] data
	 */
	public Date[] toDateArray(String jsonString) {
		return this.toDateArray(jsonString, "yyyy-MM-dd HH:mm:ss");
	}

	/**
	 * convert json string to date array with specific pattern
	 * 
	 * @param jsonString json string to be converted
	 * @param pattern date format pattern
	 * 
	 * @return date[] data
	 */
	public Date[] toDateArray(String jsonString, String pattern) {
		Date[] result = null;
		try {
			JSONArray jsonArray = JSONArray.fromObject(jsonString);
			result = new Date[jsonArray.size()];

			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
			for (int i = 0; i < jsonArray.size(); i++) {
				result[i] = simpleDateFormat.parse(jsonArray.getString(i));
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return result;
	}
	
	/**
	 * convert json string to local date array
	 * 
	 * @param jsonString json string to be converted
	 * 
	 * @return local date[] data
	 */
	public Date[] toLocalDateArray(String jsonString) {
		return this.toDateArray(jsonString, "yyyy年MM月dd日 HH时mm分ss秒");
	}
	
	/**
	 * convert json string to double array
	 * 
	 * @param jsonString json string to be converted
	 * 
	 * @return double[] data
	 */
	public Double[] toDoubleArray(String jsonString) {
		JSONArray jsonArray = JSONArray.fromObject(jsonString);
		Double[] result = new Double[jsonArray.size()];
		for (int i = 0; i < jsonArray.size(); i++) {
			result[i] = jsonArray.getDouble(i);
		}
		
		return result;
	}

	/**
	 * convert list of data to json string
	 * 
	 * @param data list of data to be converted
	 * 
	 * @return json string
	 */
	public String toJsonString(List<?> data) {
		return JSONArray.fromObject(data).toString();
	}
	
	/**
	 * convert java bean to json string
	 * 
	 * @param object java bean to be converted
	 * 
	 * @return json string
	 */
	public String toJsonString(Object object) {
		JSONObject json = JSONObject.fromObject(object);
		return json.toString();
	}

	/**
	 * convert date to json string with default pattern(yyyy-MM-dd HH:mm:ss)
	 * 
	 * @param date date to be converted
	 * 
	 * @return json string
	 */
	public String toJsonString(Date date) {
		JSONObject json = JSONObject.fromObject((Object) date, configJson("yyyy-MM-dd HH:mm:ss"));
		return json.toString();
	}
	
	/**
	 * convert date to json string
	 * 
	 * @param date date to be converted
	 * @param pattern date format pattern
	 * 
	 * @return json string
	 */
	public String toJsonString(Date date, String pattern) {
		JSONObject json = JSONObject.fromObject((Object) date, configJson(pattern));
		return json.toString();
	}
	
	/**
	 * convert local date to json string
	 * 
	 * @param date date to be converted
	 * 
	 * @return json string
	 */
	public String toLocalJsonString(Date date) {
		return this.toJsonString(date, "yyyy年MM月dd日 HH时mm分ss秒");
	}
	
	/**
	 * make date suit for specific pattern
	 * 
	 * @param pattern date format pattern
	 * 
	 * @return json string
	 */
	protected JsonConfig configJson(String pattern) {
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setExcludes(new String[] { "" });
		jsonConfig.setIgnoreDefaultExcludes(false);
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		jsonConfig.registerJsonValueProcessor(Date.class, new JsonDateValueProcessor(pattern));

		return jsonConfig;
	}

	/**
	 * make specific date suit for specific pattern
	 * 
	 * @param excludes data not to be formatted
	 * @param pattern date format pattern
	 * 
	 * @return json string
	 */
	protected JsonConfig configJson(String[] excludes, String pattern) {
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setExcludes(excludes);
		jsonConfig.setIgnoreDefaultExcludes(true);
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		jsonConfig.registerJsonValueProcessor(Date.class, new JsonDateValueProcessor(pattern));

		return jsonConfig;
	}
	
	/*public static void main(String[] args) {
		Account account = new Account();
		account.setUserName("aaa");
		account.setPassword("bbb");
		
		String jsonString;
		Object[] object;
		Map<String, Object> map;
		JSON json = new JSON();
		jsonString = json.toJsonString(account);
		
		System.out.println(jsonString);
		
		map = json.toMap(jsonString);
		System.out.println(map.get("password"));
		System.out.println(map.get("userName"));
	}*/

}
