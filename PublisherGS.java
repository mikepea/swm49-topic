package jobs;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;

public class PublisherGS {

    private static String brokerURL = "tcp://localhost:61616";
    private static ConnectionFactory factory;
    private Connection connection;
    private Session session;
    private MessageProducer producer;
    
    private static int count = 10;
    private static int total;
    private static int id = 1000000;
    
    private String jobs[] = new String[]{"suspend", "delete"};
    
    public PublisherGS() throws JMSException {
    	factory = new ActiveMQConnectionFactory(brokerURL);
    	connection = factory.createConnection();
        connection.start();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        producer = session.createProducer(null);
    }    
    
    public void close() throws JMSException {
        if (connection != null) {
            connection.close();
        }
    }    
    
	public static void main(String[] args) throws JMSException {
    	PublisherGS publisher = new PublisherGS();
        while (total < 1000) {
            for (int i = 0; i < count; i++) {
                publisher.sendMessage();
            }
            total += count;
            System.out.println("Published '" + count + "' of '" + total + "' job messages");
            try {
              Thread.sleep(1000);
            } catch (InterruptedException x) {
            }
          }
        publisher.close();

	}
	
    public void sendMessage() throws JMSException {
        int idx = 0;
        while (true) {
            idx = (int)Math.round(jobs.length * Math.random());
            if (idx < jobs.length) {
                break;
            }
        }
        String job = jobs[idx];
        Destination destination = session.createQueue("JOBS." + job);
        Message message = session.createObjectMessage(id++);
        System.out.println("Sending: id: " + ((ObjectMessage)message).getObject() + " on queue: " + destination);
        producer.send(destination, message);
    }	

}
