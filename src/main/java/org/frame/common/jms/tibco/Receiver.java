package org.frame.common.jms.tibco;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.StreamMessage;
import javax.jms.TextMessage;
    
public class Receiver implements MessageListener {
    
    public void onMessage(Message message) {
    	if (message instanceof javax.jms.TextMessage) {
    		TextMessage textMsg = (TextMessage) message;
    		try {
				System.out.println("The receiver received a message:" + textMsg.getText());
			} catch (JMSException e) {
				e.printStackTrace();
			}
    	} else if(message instanceof javax.jms.StreamMessage) {
    		StreamMessage streamMsg = (StreamMessage)message;
    		try {
    			int length = message.getIntProperty("length");
        		byte[] content = new byte[length];
				if(streamMsg.readBytes(content)>0)
				{
					String str = new String(content);
					System.out.println("The message length is:" + length);
					System.out.println("The receiver received a message:" + str);
				}
			} catch (JMSException e) {
				e.printStackTrace();
			}
    	} 
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
