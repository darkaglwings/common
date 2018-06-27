package org.frame.common.xml.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class StringXmlAdapter extends XmlAdapter<String, String> {
	
    @Override  
    public String unmarshal(String data) throws Exception {
    	String result = null;
    	
    	if (data.startsWith("<![CDATA[")) {
    		result = data.substring(9, data.length() - 3);
    	}
        
    	return result;
    }  
  
    @Override  
    public String marshal(String data) throws Exception {
    	String result = "";
    	
    	if (data != null) {
    		result = "<![CDATA[" + data + "]]>";
    	}
        
    	return result;
    }

}
