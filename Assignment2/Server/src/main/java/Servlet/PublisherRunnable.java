package Servlet;

import QueueUtils.RMQChannelPool;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BasicProperties;
import com.rabbitmq.client.Channel;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class PublisherRunnable implements Runnable {
  private RMQChannelPool channelPool;
  private String payload;

  public PublisherRunnable(RMQChannelPool channelPool, String payload) {
    this.channelPool = channelPool;
    this.payload = payload;
  }
  @Override
  public void run() {
    try {
      Channel channel = channelPool.borrowObject();

      channel.queueDeclare(SwipeServlet.QUEUE_NAME, false, false, false, null);
      byte[] payloadBytes = payload.getBytes(StandardCharsets.UTF_8);
      AMQP.BasicProperties props = new AMQP.BasicProperties.Builder().contentType("application/json").build();
      channel.basicPublish("", SwipeServlet.QUEUE_NAME, props, payloadBytes);
      channelPool.returnObject(channel);
      System.out.println(" [x] Sent '" + payload + "'");
    } catch (Exception e) {
      e.printStackTrace();
    }


  }
}
