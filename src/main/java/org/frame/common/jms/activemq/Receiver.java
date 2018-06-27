package org.frame.common.jms.activemq;

import java.util.ArrayList;
import java.util.List;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.Topic;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

public class Receiver {
	
	public static final int NOTICE_AUTO_ACKNOWLEDGE = Session.AUTO_ACKNOWLEDGE;
	
	public static final int NOTICE_CLIENT_ACKNOWLEDGE = Session.CLIENT_ACKNOWLEDGE;
	
	public static final int NOTICE_DUPS_OK_ACKNOWLEDGE = Session.DUPS_OK_ACKNOWLEDGE;
	
	public static final int NOTICE_SESSION_TRANSACTED = Session.SESSION_TRANSACTED;
	
	public List<Message> queue(String url, String name, int notice) {
		List<Message> result = new ArrayList<Message>();
		
		Connection connection = null;
		Session session = null;
		try {
			ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_USER, ActiveMQConnection.DEFAULT_PASSWORD, url);

			connection = connectionFactory.createConnection();
			connection.start();

			session = connection.createSession(Boolean.TRUE, notice);

			Destination destination = session.createQueue(name);
			MessageConsumer consumer = session.createConsumer(destination);

			while(true){  
				Message message = consumer.receive(1000);
				if (null != message) {
					result.add(message);
				} else break;
			}
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
	
	public List<Message> topic(String url, String topicName, String subscriber, int notice) {
		List<Message> result = new ArrayList<Message>();
		
		Connection connection = null;
		Session session = null;
		try {
			ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_USER, ActiveMQConnection.DEFAULT_PASSWORD, url);

			connection = connectionFactory.createConnection();
			connection.setClientID(subscriber);
			connection.start();

			session = connection.createSession(Boolean.TRUE, notice);

			Topic topic = session.createTopic(topicName);
			MessageConsumer consumer = session.createDurableSubscriber(topic, subscriber);

			while(true){  
				Message message = consumer.receive(1000);
				if (null != message) {
					result.add(message);
				} else break;
			}
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
	
	/**
	 * topic时的测试方法:
	 * 
	 * 测试一:
	 * A、先启动Publisher类
	 * B、再启动Receiver类
	 * C、结果无任何记录被订阅
	 * 
	 * 测试二:
	 * A、先启动Receiver类，让Receiver在相关主题上进行订阅
	 * B、停止Receiver类，再启动Publisher类
	 * C、待Publisher类运行完成后，再启动Receiver类
	 * D、结果发现相应主题的信息被订阅
	 */
	/*public static void main(String[] args) {
		String url = "failover:(tcp://192.168.15.170:61616)";//failover:失效转移
		String name = "jms";
		String subscriber = "Subscriber";
		int notice = Receiver.NOTICE_AUTO_ACKNOWLEDGE;
		
		List<Message> list = new Receiver().queue(url, "oggjmsqueue", notice);
		//List<Message> list = new Receiver().topic(url, name, subscriber);
		for (Message message : list) {
			try {
				System.out.println(((ActiveMQTextMessage) message).getText());
				//System.out.println(((MapMessage) message).getString("content"));
			} catch (JMSException e) {
				e.printStackTrace();
			}
		}
	}*/
	
}
