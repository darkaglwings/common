/**
 * Reflect contains common reflect methods
 */
package org.frame.common.lang.reflect;

import java.lang.reflect.Method;

public class Reflect {

	/**
	 * invoke getXXX method of an object
	 * 
	 * @param object object to be operated
	 * @param info method name(except get-.)
	 * 
	 * @return return value of getXXX method
	 */
	public Object get(Object object, String info) {
		Object result = null;
		try {
			for (Method method : object.getClass().getDeclaredMethods()) {
				if (method.getName().toLowerCase().equals("get" + info.toLowerCase()) && method.getParameterTypes().length == 0) {
					result = method.invoke(object, new Object[]{});
					break;
				}
			}
		} catch (Exception e) {
			System.err.println("get" + info + " method invoke error.");
			e.printStackTrace();
		}
		
		return result;
	}
	
	/**
	 * invoke specific method of an object
	 * 
	 * @param object object to be operated
	 * @param name method name to be invoked
	 * @param args parameters of method
	 * 
	 * @return return value of method to be invoked
	 */
	public Object invoke(Object object, String name, Object... args) {
		Object result = null;
		try {
			for (Method method : object.getClass().getDeclaredMethods()) {
				if (method.getName().toLowerCase().equals(name.toLowerCase())) {
					result = method.invoke(object, args);
					break;
				}
			}
		} catch (Exception e) {
			System.err.println("method invoke error: " + name);
			e.printStackTrace();
		}
		
		return result;
	}
	
	/**
	 * invoke specific method of an object
	 * 
	 * @param object object to be operated
	 * @param name method name to be invoked
	 * @param types parameter classes of method
	 * @param args parameters of method
	 * 
	 * @return return value of method to be invoked
	 */
	public Object invoke(Object object, String name, Class<?>[] types, Object... args) {
		Object result;
		try {
			Method method = object.getClass().getMethod(name, types);
			result = method.invoke(object, args);
		} catch (Exception e) {
			result = null;
			System.err.println("method invoke error: " + name);
			e.printStackTrace();
		}
		
		return result;
	}
	
	/**
	 * invoke setXXX method of an object
	 * 
	 * @param object object to be invoked
	 * @param info method name(except set-.)
	 * @param parameter parameter of setXXX method
	 */
	public void set(Object object, String info, Object parameter) {
		for (Method method : object.getClass().getDeclaredMethods()) {
			if (method.getName().toLowerCase().equals("set" + info.toLowerCase()) && method.getParameterTypes().length == 1) {
				try {
					method.invoke(object, method.getParameterTypes()[0].cast(parameter));
				} catch (Exception e) {
					try {
						method.invoke(object, method.getParameterTypes()[0].cast(null));
					} catch (Exception exception) {
						System.err.println("set" + info + " method invoke error.");
						exception.printStackTrace();
					}

					System.err.println("set" + info + " method invoke error.");
					e.printStackTrace();
				}
				
				break;
			}
		}
	}
}
