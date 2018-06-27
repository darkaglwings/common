/**
 * NetState contains data for net
 */
package org.frame.common.network;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.frame.common.context.OperateSystem;
import org.frame.common.lang.StringHelper;

public class NetState {
	
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
	
	public String getURLContent(String url, String encoding) {
		StringBuffer content = new StringBuffer();
		try {
			URL u = new URL(url);
			InputStream in = new BufferedInputStream(u.openStream());
			InputStreamReader theHTML = new InputStreamReader(in, encoding);
			int c;
			while ((c = theHTML.read()) != -1) {
				content.append((char) c);
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return content.toString();
	}
	
	/**
	 * to get all ip of server
	 * 
	 * @return string[] of ip
	 */
	public String[] ip() {
		List<String> ip = new ArrayList<String>();
        try {
            Enumeration <?> enumeration = (Enumeration <?>) NetworkInterface.getNetworkInterfaces();
            while (enumeration.hasMoreElements()) {
                NetworkInterface networkInterface = (NetworkInterface) enumeration.nextElement();
                Enumeration <?> netAddresses = networkInterface.getInetAddresses();
                while (netAddresses.hasMoreElements()) {
                    InetAddress address = (InetAddress) netAddresses.nextElement();
                    ip.add(address.getHostAddress());
                }
            }
        } catch (Exception e) {  
            e.printStackTrace();
        }
        
        return ip.toArray(new String[ip.size()]);
	}
	
	/**
	 * to get ip of sepcific network adapter
	 * 
	 * @param adapter name of network adpater
	 * 
	 * @return string[] of ip
	 */
	public String[] ip(String adapter) {
		List<String> ip = new ArrayList<String>();
        try {
            Enumeration <?> enumeration = (Enumeration <?>) NetworkInterface.getNetworkInterfaces();
            while (enumeration.hasMoreElements()) {
                NetworkInterface networkInterface = (NetworkInterface) enumeration.nextElement();
                if (adapter != null && adapter.equals(networkInterface.getName())) {
                	Enumeration <?> netAddresses = networkInterface.getInetAddresses();
                    while (netAddresses.hasMoreElements()) {
                        InetAddress address = (InetAddress) netAddresses.nextElement();
                        ip.add(address.getHostAddress());
                    }
                }
                
            }
        } catch (Exception e) {  
            e.printStackTrace();
        }
        
        return ip.toArray(new String[ip.size()]);
	}
	
	public String ipFromRequest(HttpServletRequest request) {
		String ip = request.getHeader("X-Forwarded-For");
		
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		} else if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		} else if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_CLIENT_IP");
		} else if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		} else if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		
		return ip;
	}
	
	/**
	 * to get localhost ip
	 * 
	 * @return localhost ip
	 */
	public String localhostIP() {
		String result = null;
		try {
			result = InetAddress.getLocalHost().getHostAddress();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	/**
	 * to get localhost mac(only for windows)
	 * 
	 * @return localhost mac
	 */
	@Deprecated
	public String localhostMac() {
		if (OperateSystem.isWindows())
			return this.localhostMac("-");
		else if(OperateSystem.isLinux())
			return this.localhostMac(":");
		else
			return this.localhostMac("");
	}
	
	/**
	 * to get localhost mac with specific separate symbol(only for windows)
	 * 
	 * @param separate separate symbol in mac address
	 * 
	 * @return localhost mac with specific separate symbol
	 */
	@Deprecated
	public String localhostMac(String separate) {
		String result = null;
    	try {
    		String ip = InetAddress.getLocalHost().getHostAddress();
    		
    		byte[] mac = null;
    		Enumeration <?> enumeration = (Enumeration <?>) NetworkInterface.getNetworkInterfaces();
    		while (enumeration.hasMoreElements()) {
    			NetworkInterface networkInterface = (NetworkInterface) enumeration.nextElement();
    			Enumeration <?> netAddresses = networkInterface.getInetAddresses();
    			while (netAddresses.hasMoreElements()) {
    				InetAddress address = (InetAddress) netAddresses.nextElement();
    				if (ip.equals(address.getHostAddress())) mac = networkInterface.getHardwareAddress();
    			}

    		}
    		
    		if (mac != null) {
    			StringBuffer sbuf = new StringBuffer();
        		if (mac != null && mac.length > 1) {
        			for (int i = 0; i < mac.length; i++) {
        				if (i == 0)
        					sbuf.append(parseByte(mac[i]));
        				else
        					sbuf.append(separate).append(parseByte(mac[i]));
        			}
        			result = sbuf.toString();
        		}
    		}
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
        return result;
	}
	
	/**
	 * to get localhost name
	 * 
	 * @return localhost name
	 */
	public String localhostName() {
		String result = null;
		try {
			result = InetAddress.getLocalHost().getHostName();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	/**
	 * to get all mac of server
	 * 
	 * @return string[] of mac address
	 */
	public String[] mac() {
		if (System.getProperty("os.name").startsWith("Windows"))
			return this.mac("-");
		else if(System.getProperty("os.name").startsWith("Linux"))
			return this.mac(":");
		else
			return this.mac("");
	}
	
	/**
	 * to get all mac of server with specific separate symbol
	 * 
	 * @param separate separate symbol in mac address
	 * 
	 * @return string[] of mac address
	 */
    public String[] mac(String separate) {
    	String[] result = {};
    	try {
    		List<String> list = new ArrayList<String>();
    		Enumeration<NetworkInterface> enumeration = NetworkInterface.getNetworkInterfaces();
    		while (enumeration.hasMoreElements()) {
    			NetworkInterface networkInterface = enumeration.nextElement();
    			if (networkInterface != null) {
    				if (networkInterface.getHardwareAddress() != null) {
    					byte[] address = networkInterface.getHardwareAddress();
    					StringBuffer sbuf = new StringBuffer();
    					if (address != null && address.length > 1) {
    						for (int i = 0; i < address.length; i++) {
    							if (i == 0)
    								sbuf.append(parseByte(address[i]));
    							else
    								sbuf.append(separate).append(parseByte(address[i]));
    						}
    						list.add(sbuf.toString());
    					}
    				}
    			} else {
    				System.out.println("get mac address error.");
    			}
    		}

    		result = list.toArray(new String[list.size()]);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
        return result;
    }
    
    /**
	 * to get mac of specific network adapter with specific separate symbol
	 * 
	 * @param separate separate symbol in mac address
	 * @param adapter name of network adpater
	 * 
	 * @return string[] of mac address
	 */
    public String mac(String separate, String adapter) {
    	String result = null;
    	try {
    		Enumeration<NetworkInterface> enumeration = NetworkInterface.getNetworkInterfaces();
    		while (enumeration.hasMoreElements()) {
    			NetworkInterface networkInterface = enumeration.nextElement();
    			if (networkInterface != null) {
    				if (adapter != null && adapter.equals(networkInterface.getName())) {
    					if (networkInterface.getHardwareAddress() != null) {
    						byte[] address = networkInterface.getHardwareAddress();
    						StringBuffer sbuf = new StringBuffer();
    						if (address != null && address.length > 1) {
    							for (int i = 0; i < address.length; i++) {
    								if (i == 0)
    									sbuf.append(parseByte(address[i]));
    								else
    									sbuf.append(separate).append(parseByte(address[i]));
    							}
    							result = sbuf.toString();
    						}
    					}
    				}
    			} else {
    				System.out.println("get mac address error.");
    			}
    		}
    	} catch (Exception e) {
    		e.printStackTrace();
    	}

    	return result;
    }
	
    /**
	 * to get all names of server
	 * 
	 * @return string[] of name
	 */
	public String[] name() {
		List<String> name = new ArrayList<String>();
        try {
            Enumeration <?> enumeration = (Enumeration <?>) NetworkInterface.getNetworkInterfaces();
            while (enumeration.hasMoreElements()) {
                NetworkInterface networkInterface = (NetworkInterface) enumeration.nextElement();
                Enumeration <?> netAddresses = networkInterface.getInetAddresses();
                while (netAddresses.hasMoreElements()) {
                    InetAddress address = (InetAddress) netAddresses.nextElement();
                    name.add(address.getHostName());
                }
            }
        } catch (Exception e) {  
            e.printStackTrace ();
        }
        
        return name.toArray(new String[name.size()]);
	}
	
	/**
	 * to get all names of specific network adapter
	 * 
	 * @param adapter name of network adapter
	 * 
	 * @return string[] of name
	 */
	public String[] name(String adapter) {
		List<String> name = new ArrayList<String>();
        try {
            Enumeration <?> enumeration = (Enumeration <?>) NetworkInterface.getNetworkInterfaces();
            while (enumeration.hasMoreElements()) {
                NetworkInterface networkInterface = (NetworkInterface) enumeration.nextElement();
                if (adapter != null && adapter.equals(networkInterface.getName())) {
                	Enumeration <?> netAddresses = networkInterface.getInetAddresses();
                    while (netAddresses.hasMoreElements()) {
                        InetAddress address = (InetAddress) netAddresses.nextElement();
                        name.add(address.getHostName());
                    }
                }
            }
        } catch (Exception e) {  
            e.printStackTrace ();
        }
        
        return name.toArray(new String[name.size()]);
	}
	
	/**
	 * formate byte in mac address to hex
	 * 
	 * @param byte in mac address
	 * 
	 * @return string of hex byte
	 */
	private String parseByte(byte b) {
		String string = "00" + Integer.toHexString(b);
		return string.substring(string.length() - 2);
	}
	
	/*public static void main(String[] args) {
		System.out.println(System.getProperty("os.name"));
		System.out.println(new NetState().localhostIP());
		System.out.println(new NetState().localhostMac());
	}*/
	
}