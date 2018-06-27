package org.frame.common.management.server;

import java.lang.management.ManagementFactory;
import java.util.Iterator;
import java.util.Map;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

public class Register {
	
	public Register(Map<String, Object> map) {
		this.publish(map);
	}
	
	public void publish(Map<String, Object> map) {
		MBeanServer server = ManagementFactory.getPlatformMBeanServer();
		try {
			if (map != null) {
				String key;
				for (Iterator<String> iterator = map.keySet().iterator(); iterator.hasNext();) {
					key = (String) iterator.next();
					server.registerMBean(map.get(key), new ObjectName(key));
				}

				do {
					Thread.sleep(3000);
				} while (true);
			}
		} catch (MalformedObjectNameException e) {
			e.printStackTrace();
		} catch (InstanceAlreadyExistsException e) {
			e.printStackTrace();
		} catch (MBeanRegistrationException e) {
			e.printStackTrace();
		} catch (NotCompliantMBeanException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		/*MBeanServer server = MBeanServerFactory.createMBeanServer();

        ObjectName name = new ObjectName("jmx:bean=common");
        server.registerMBean(new Common(), name);

        ObjectName adapterName = new ObjectName("jmx:server=htmladapter,port=8082");
        HtmlAdaptorServer adapter = new HtmlAdaptorServer();
        server.registerMBean(adapter, adapterName);

        adapter.start();
        System.out.println("start.....");*/
	}

}
