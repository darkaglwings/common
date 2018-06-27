package org.frame.common.xml.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class DoubleXmlAdapter extends XmlAdapter<String, Double> {
	
    @Override  
    public Double unmarshal(String data) throws Exception {
    	Double result = null;
    	
    	try {
    		if (data != null && !"".equals(data) && !"null".equals(data)) {
        		result = Double.valueOf(data);
        	}
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    	return result;
    }  
  
    @Override  
    public String marshal(Double data) throws Exception {
    	String result = "";
    	
    	if (data != null) {
    		result = String.valueOf(data);
    	}
        
    	return result;
    }

}
