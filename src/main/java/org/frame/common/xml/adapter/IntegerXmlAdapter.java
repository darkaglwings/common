package org.frame.common.xml.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class IntegerXmlAdapter extends XmlAdapter<String, Integer> {
	
    @Override  
    public Integer unmarshal(String data) throws Exception {
    	Integer result = null;
    	
    	try {
    		if (data != null && !"".equals(data) && !"null".equals(data)) {
        		result = Integer.valueOf(data);
        	}
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    	return result;
    }  
  
    @Override  
    public String marshal(Integer data) throws Exception {
    	String result = "";
    	
    	if (data != null) {
    		result = String.valueOf(data);
    	}
        
    	return result;
    }

}
