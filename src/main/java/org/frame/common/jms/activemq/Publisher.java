package org.frame.common.jms.activemq;

import java.util.Iterator;
import java.util.Map;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.Topic;

import org.apache.activemq.ActiveMQConnectionFactory;

public class Publisher {
	
	public static final int NOTICE_AUTO_ACKNOWLEDGE = Session.AUTO_ACKNOWLEDGE;
	
	public static final int NOTICE_CLIENT_ACKNOWLEDGE = Session.CLIENT_ACKNOWLEDGE;
	
	public static final int NOTICE_DUPS_OK_ACKNOWLEDGE = Session.DUPS_OK_ACKNOWLEDGE;
	
	public static final int NOTICE_SESSION_TRANSACTED = Session.SESSION_TRANSACTED;

	public boolean queue(String url, String name, Map<String, Object> parameter, int notice) {
		boolean result = false;
		
		Connection connection = null;
		Session session = null;
		try {
			ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);

	        connection = connectionFactory.createConnection();
	        //connection.setExceptionListener(new MyJmsException());
	        connection.start();

	        session = connection.createSession(Boolean.TRUE, notice);
	        
	        Queue queue = session.createQueue(name);
	        MessageProducer producer = session.createProducer(queue);
	        //producer.setDeliveryMode(DeliveryMode.PERSISTENT);
	        MapMessage message = session.createMapMessage();
	        String key;
	        for (Iterator<String> iterator = parameter.keySet().iterator(); iterator.hasNext();) {
	        	key = iterator.next();
	        	message.setObject(key, parameter.get(key));
	        }
	        producer.send(message);

	        session.commit();
		} catch (JMSException e) {
			e.printStackTrace();
		} finally {
			try {
				if (session != null) session.close();
			} catch (JMSException e) {
				session = null;
				e.printStackTrace();
			}
			
			try {
				if (connection != null) connection.close();
			} catch (JMSException e) {
				connection = null;
				e.printStackTrace();
			}
		}
		
		return result;
	}
	
	public boolean topic(String url, String name, Map<String, Object> parameter, int notice) {
		boolean result = false;
		
		Connection connection = null;
		Session session = null;
		try {
			ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);

	        connection = connectionFactory.createConnection();
	        //connection.setExceptionListener(new MyJmsException());
	        connection.start();

	        session = connection.createSession(Boolean.TRUE, notice);
	        
	        Topic topic = session.createTopic(name);
	        MessageProducer producer = session.createProducer(topic);
	        //producer.setDeliveryMode(DeliveryMode.PERSISTENT);
	        MapMessage message = session.createMapMessage();
	        String key;
	        for (Iterator<String> iterator = parameter.keySet().iterator(); iterator.hasNext();) {
	        	key = iterator.next();
	        	message.setObject(key, parameter.get(key));
	        }
	        producer.send(message);

	        session.commit();
		} catch (JMSException e) {
			e.printStackTrace();
		} finally {
			try {
				if (session != null) session.close();
			} catch (JMSException e) {
				session = null;
				e.printStackTrace();
			}
			
			try {
				if (connection != null) connection.close();
			} catch (JMSException e) {
				connection = null;
				e.printStackTrace();
			}
		}
		
		return result;
	}
	
	/*public static void main(String[] args) {
		String url = "failover:(tcp://localhost:61616)";//failover:失效转移
		String name = "jms";
		int notice = Publisher.NOTICE_AUTO_ACKNOWLEDGE;
		
		Map<String, Object> parameter = new HashMap<String, Object>();
		parameter.put("content", "aaa");
		
		new Publisher().queue(url, name, parameter, notice);
		//new Publisher().topic(url, name, parameter);
	}*/

}
