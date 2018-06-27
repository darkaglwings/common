package org.frame.common.xml.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class LongXmlAdapter extends XmlAdapter<String, Long> {
	
    @Override  
    public Long unmarshal(String data) throws Exception {
    	Long result = null;
    	
    	try {
    		if (data != null && !"".equals(data) && !"null".equals(data)) {
        		result = Long.valueOf(data);
        	}
		} catch (Exception e) {
			e.printStackTrace();
		}
        
    	return result;
    }  
  
    @Override  
    public String marshal(Long data) throws Exception {
    	String result = "";
    	
    	if (data != null) {
    		result = String.valueOf(data);
    	}
        
    	return result;
    }

}
