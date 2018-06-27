package org.frame.common.jms.tibco;

import javax.annotation.Resource;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.StreamMessage;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

@Component
public class Publisher {

	private JmsTemplate jmsTemplate;   

	public void sendTextMessage(Destination destination, final String message) {   
		System.out.println("The producer send a message：" + message);   
		jmsTemplate.send(destination, new MessageCreator() {   
			public Message createMessage(Session session) throws JMSException {   
				return session.createTextMessage(message);   
			}   
		});   
	}    

	public void sendStreamMessage(Destination destination, final byte[] content){
		System.out.println("The producer send a message：" + new String(content));   
		jmsTemplate.send(destination, new MessageCreator() {   
			public Message createMessage(Session session) throws JMSException {   
				StreamMessage message = session.createStreamMessage();
				message.setIntProperty("length", content.length);
				message.writeBytes(content);
				return message;
			}   
		});  
	}

	public JmsTemplate getJmsTemplate() {   
		return jmsTemplate;   
	}    

	@Resource  
	public void setJmsTemplate(JmsTemplate jmsTemplate) {   
		this.jmsTemplate = jmsTemplate;   
	}   

	/*public static void main(String[] args) {
		ApplicationContext ac = new ClassPathXmlApplicationContext("org/frame/common/jms/tibco/applicationContext-jms-tibco.xml");
		Publisher publisher = (Publisher) ac.getBean("publisher");
		Destination destination = (Destination) ac.getBean("queueDestination");

		String str = "<?xmlversion=\"1.0\"encoding=\"UTF-8>";
		publisher.sendTextMessage(destination,"Text message");
		//sendEmsQueueMessage.sendStreamMessage(destination, str.getBytes());
	}*/


}
