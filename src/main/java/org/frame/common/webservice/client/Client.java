/**
 * web service client based on axis2
 * 
 * AXIS2 NEEDS JARS:
 *   activation-1.1.jar
 *   axiom-api-1.2.13.jar
 *   axiom-impl-1.2.13.jar
 *   axis2-adb-1.6.2.jar
 *   axis2-adb-codegen-1.6.2.jar
 *   axis2-codegen-1.6.2.jar
 *   axis2-java2wsdl-1.6.2.jar
 *   axis2-kernel-1.6.2.jar
 *   axis2-transport-http-1.6.2.jar
 *   axis2-transport-local-1.6.2.jar
 *   commons-codec-1.3.jar
 *   commons-fileupload-1.2.jar
 *   commons-httpclient-3.1.jar
 *   commons-logging-1.1.1.jar
 *   httpcore-4.0.jar
 *   mail-1.4.jar
 *   neethi-3.0.2.jar
 *   woden-api-1.0M9.jar
 *   woden-impl-commons-1.0M9.jar
 *   woden-impl-dom-1.0M9.jar
 *   wsdl4j-1.6.2.jar
 *   wstx-asl-3.2.9.jar
 *   XmlSchema-1.4.7.jar
 */
package org.frame.common.webservice.client;

import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.rpc.client.RPCServiceClient;

public class Client {
	
	/**
	 * web service common invoker with no return values
	 * 
	 * @param url web service url
	 * @param namespace web service namespace
	 * @param method web service method
	 * @param args web service method's parameters
	 * 
	 */
	public void invoke(String url, String namespace, String method, Object... args) {
		try {
			EndpointReference endpointReference = new EndpointReference(url);
			RPCServiceClient serviceClient = new RPCServiceClient();
			Options options = serviceClient.getOptions();
			options.setTimeOutInMilliSeconds(60 * 60 * 1000);
			options.setTo(endpointReference);
			QName qname = new QName(namespace, method);
			if (args == null) args = new Object[] {};
			serviceClient.invokeRobust(qname, args);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * web service common invoker
	 * 
	 * @param url web service url
	 * @param namespace web service namespace
	 * @param method web service method
	 * @param type classes of web service method's parameters
	 * @param args web service method's parameters
	 * 
	 * @return object[] in common object[0] is the return value of web service method
	 */
	public Object[] invoke(String url, String namespace, String method, Class<?>[] type, Object... args) {
		Object[] result = null;
		try {
			EndpointReference endpointReference = new EndpointReference(url);
			RPCServiceClient serviceClient = new RPCServiceClient();
			Options options = serviceClient.getOptions();
			options.setTimeOutInMilliSeconds(60 * 60 * 1000);
			options.setTo(endpointReference);
			QName qname = new QName(namespace, method);
			if (type == null) type = new Class<?>[] {};
			if (args == null) args = new Object[] {};
			result = serviceClient.invokeBlocking(qname, args, type);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * web service common invoker(needs web service interface)
	 * 
	 * @param url web service url
	 * @param namespace web service namespace
	 * @param serviceName web service name
	 * @param clazz web service interface class
	 * @param method web service method
	 * @param type classes of web service method's parameters
	 * @param args web service method's parameters
	 * 
	 * @return return value of web service method
	 */
	@Deprecated
	public Object invoke(String url, String namespace, String serviceName, Class<?> clazz, String method, Class<?>[] type, Object... args) {
		try{
			QName qName = new QName(namespace, serviceName); 
			Service service = Service.create(new URL(url), qName);
			Object client = service.getPort(clazz);
			return client.getClass().getMethod(method, type).invoke(client, args);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

    /*public static void main(String args[]) throws Exception {
    	String url = "http://localhost:8083/proxyService";
		String namespace = "http://webservice.dmx.com/";
		Class<?>[] type = new Class<?>[] {String.class};
		Object[] result = new Client().invoke(url, namespace, "backupProxy", type, null);
		System.out.println(result[0]);
    	
    	String url = "http://192.168.1.2:8080/bcmp/services/receivBackDateService?wsdl";
    	String namespace = "http://impl.task.bcmp.dmx.com";
    	Class<?>[] type = new Class<?>[] {String.class};
    	Object[] result = new Client().invoke(url, namespace, "getBackXml", type, new Object[]{"aaa"});
    	System.out.println(result[0]);
    }*/
	
}
