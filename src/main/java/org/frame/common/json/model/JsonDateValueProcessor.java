package org.frame.common.json.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import net.sf.json.JsonConfig;
import net.sf.json.processors.JsonValueProcessor;

public class JsonDateValueProcessor implements JsonValueProcessor{

    private String pattern = "yyyy-MM-dd HH:mm:ss";

    public JsonDateValueProcessor() {
        super();
    }

    public JsonDateValueProcessor(String pattern) {
        super();
        this.pattern = pattern;
    }

    public Object processArrayValue(Object value, JsonConfig jsonConfig) {
        return process(value);
    }

    public Object processObjectValue(String key, Object value,
            JsonConfig jsonConfig) {
        return process(value);
    }

    private Object process(Object value) {
        try {
            if (value instanceof Date) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, Locale.UK);
                return simpleDateFormat.format((Date) value);
            }
            return value == null ? "" : value.toString();
        } catch (Exception e) {
            return "";
        }

    }

    public String getPattern() {
        return pattern;
    }
    
    public void setPattern(String pattern) {
    	this.pattern = pattern;
    }

}
