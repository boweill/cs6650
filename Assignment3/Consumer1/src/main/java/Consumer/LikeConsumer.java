package Consumer;

import Bean.DataWriter;
import Bean.DynamoDBHandler;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import Handler.LikeHandlerRunnable;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

public class LikeConsumer {
  // the exchange and queue that this consumer is connected to
  private String exchangeName;
  private String queueName;

  // The number of consumer threads and the actual threads the consumer will start
  public int numOfThreads;
  private Thread[] threads;

  // The connection RabbitMQ
  private Connection conn;

  // The object that does the data storing and aggregating
  private DataWriter writer;

  public LikeConsumer(String exchangeName, String queueName, int numOfThreads) {
    this.exchangeName = exchangeName;
    this.queueName = queueName;
    ConnectionFactory factory = new ConnectionFactory();

    // Setting information needed to form a connection to the RabbitMQ server.
    // Please modify here if needed
    factory.setHost("52.26.252.125");
    factory.setVirtualHost("/");
    factory.setUsername("admin");
    factory.setPassword("password");
    ExecutorService es = Executors.newFixedThreadPool(100);
    try {
      conn = factory.newConnection(es);
    } catch (IOException | TimeoutException ex) {
      System.err.println("Unable to connect to Queue : " + ex.getMessage());
    }
    this.numOfThreads = numOfThreads;
    threads = new Thread[numOfThreads];

    writer = new DataWriter();

    for (int i = 0; i < numOfThreads; i++) {
      threads[i] = new Thread(new LikeHandlerRunnable(exchangeName, queueName, conn, writer));
    }
  }

  public void start() {

    for (int i = 0; i < numOfThreads; i++) {
      threads[i].start();
    }
  }

  public static void main(String[] argv) {
    // exchangeName, queueName and numOfThreads are specified via command line arguments
    String exchangeName = argv[0];
    String queueName = argv[1];
    int numOfThreads = Integer.valueOf(argv[2]);

    LikeConsumer likeConsumer = new LikeConsumer(exchangeName, queueName, numOfThreads);
    likeConsumer.start();
    System.out.println("Consumer.LikeConsumer running!");
  }
}
