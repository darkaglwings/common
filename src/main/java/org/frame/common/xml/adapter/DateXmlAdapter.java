package org.frame.common.xml.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.frame.common.util.Date;

public class DateXmlAdapter extends XmlAdapter<String, Date> {
	
    @Override  
    public Date unmarshal(String date) throws Exception {
    	Date result = null;
    	
    	if (date != null && !"".equals(date) && !"null".equals(date)) {
    		result = new Date().string2Date(date);
    	}
        
    	return result;
    }  
  
    @Override  
    public String marshal(Date date) throws Exception {
    	String result = "";
    	
    	if (date != null) {
    		result = date.date2String();
    	}
        
    	return result;
    }

}
