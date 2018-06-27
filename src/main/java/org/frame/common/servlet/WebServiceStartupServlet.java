package org.frame.common.servlet;

import java.util.Map;

import javax.servlet.http.HttpServlet;

import org.frame.common.webservice.server.Publisher;


public class WebServiceStartupServlet extends HttpServlet{
	
	private static final long serialVersionUID = 2294757338971642296L;

	@SuppressWarnings("unchecked")
	public void init() {
		try{
			String webServiceInfo = this.getInitParameter("WebServiceInfo");
			String webService = this.getInitParameter("WebService");
			if (webServiceInfo != null && !"".equals(webServiceInfo) && webService != null && !"".equals(webService)) {
				Class<?> clazz = Class.forName(webServiceInfo);
				Map<String, Object> map = (Map<String, Object>) clazz.getMethod(webService).invoke(clazz.newInstance());
				if (map != null) {
					Publisher publisher = new Publisher();
					for (String url : map.keySet()) {
						publisher.publish(url, map.get(url));
					}
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
}
