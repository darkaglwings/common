/**
 * REST Client contains tools for REST service
 */
package org.frame.common.rest.client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Client {
	
	/**
	 * invoke REST service
	 * 
	 * @param url REST service url
	 * @param type REST service type(GET, POST or PUT)
	 * @param username access REST service username
	 * @param password access REST service password
	 * @param json REST service parameter
	 * 
	 * @return instance of java.net.HttpURLConnection
	 */
	public HttpURLConnection invoke(String url, String type, String username, String password, String json) {
		HttpURLConnection connection = null;

		try {
			connection = (HttpURLConnection) new URL(url).openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setRequestMethod(type);
			connection.setUseCaches(false);
			connection.setInstanceFollowRedirects(true);
			connection.setRequestProperty("Content-Type", "application/json");
			String authentication = (new sun.misc.BASE64Encoder()).encode((username + ":" + password).getBytes());
			connection.setRequestProperty("Authorization", "Basic " + authentication);
			connection.connect();

			if (json != null) {
				DataOutputStream out = new DataOutputStream(connection.getOutputStream());
				out.write(json.getBytes("utf-8"));
				out.flush();
				out.close();
			}

			return connection;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * release resource when invoke REST service done
	 * 
	 * @param connection REST service connection
	 */
	public void finish(HttpURLConnection connection) {
		try {
			connection.disconnect();
			connection = null;
		} catch (Exception e) {
			connection = null;
			e.printStackTrace();
		}
	}
	
	/**
	 * access REST service with GET
	 * 
	 * @param url REST service url
	 * @param username access REST service username
	 * @param password access REST service password
	 * 
	 * @return return value of REST service
	 */
	public String get(String url, String username, String password) {
		return this.read(url, "GET", username, password, null);
	}
	
	/**
	 * access REST service with POST
	 * 
	 * @param url REST service url
	 * @param username access REST service username
	 * @param password access REST service password
	 * @param json REST service parameter
	 * 
	 * @return return value of REST service
	 */
	public String post(String url, String username, String password, String json) {
		return this.read(url, "POST", username, password, json);
	}

	/**
	 * access REST service with PUT
	 * 
	 * @param url REST service url
	 * @param username access REST service username
	 * @param password access REST service password
	 * @param json REST service parameter
	 * 
	 * @return return value of REST service
	 */
	public String put(String url, String username, String password, String json) {
		return this.read(url, "PUT", username, password, json);
	}
	
	/**
	 * access REST service
	 * 
	 * @param url REST service url
	 * @param type REST service type(GET, POST or PUT)
	 * @param username access REST service username
	 * @param password access REST service password
	 * @param json REST service parameter
	 * 
	 * @return return value of REST service
	 */
	private String read(String url, String type, String username, String password, String json) {
		StringBuffer result = new StringBuffer("");
		HttpURLConnection connection = null;
		BufferedReader reader = null;

		try {
			connection = (HttpURLConnection) new URL(url).openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setRequestMethod(type);
			connection.setUseCaches(false);
			connection.setInstanceFollowRedirects(true);
			connection.setRequestProperty("Content-Type", "application/json");
			String authentication = (new sun.misc.BASE64Encoder()).encode((username + ":" + password).getBytes());
			connection.setRequestProperty("Authorization", "Basic " + authentication);
			connection.connect();
			

			if (json != null) {
				DataOutputStream out = new DataOutputStream(connection.getOutputStream());
				out.write(json.getBytes("utf-8"));
				out.flush();
				out.close();
			}

			reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

			String lines;
			while ((lines = reader.readLine()) != null) {
				lines = new String(lines.getBytes(), "utf-8");
				result.append(lines);
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
			String lines;
			try {
				while ((lines = reader.readLine()) != null) {
					lines = new String(lines.getBytes(), "utf-8");
					result.append(lines);
				}
			} catch (UnsupportedEncodingException exception) {
				exception.printStackTrace();
			} catch (IOException exception) {
				exception.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (reader != null) reader.close();
				if (connection != null) connection.disconnect();
			} catch (Exception exception) {
				reader = null;
				connection = null;
				exception.printStackTrace();
			}
		}
		return result.toString();
	}
	
}
