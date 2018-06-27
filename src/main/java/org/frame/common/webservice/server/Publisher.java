/**
 * web service publisher based on JDK6 or up
 */
package org.frame.common.webservice.server;

import javax.xml.ws.Endpoint;

public class Publisher {
	
	/**
	 * publish a class to a web service
	 * 
	 * @param url web service url
	 * @param implementor web service implementor
	 
	 * @return  true publish success<br>
	 *          false publish failure(could be errors)
	 */
	public boolean publish(String url, Object implementor) {
		boolean result = false;
		
		try{
			Endpoint.publish(url, implementor);
			result = true;
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	/*public static void main(String[] args) {
		String url="http://localhost:8081/HelloService";
		new Publisher().publish(url, new Hello());
	}*/
}
