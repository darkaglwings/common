package org.frame.common.webservice.server.demo.impl;

import org.frame.common.webservice.server.demo.IHello;


//@WebService(endpointInterface="com.frame.webservice.server.demo.IHello")
public class Hello implements IHello{

	public String sayHello() {
		return "ok";
	}
	
}
