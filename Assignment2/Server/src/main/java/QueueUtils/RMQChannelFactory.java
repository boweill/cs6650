package QueueUtils;

/**
 * (Reference Code from text book Ch 7)
 * @author Ian Gorton, Northeastern University
 * The examples supplement Chapter 7 of the Foundations of Scalable Systems, O'Reilly Media 2022
 */


import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

import java.io.IOException;

/**
 * A simple RabbitMQ channel factory based on the Apache pooling libraries
 */
public class RMQChannelFactory extends BasePooledObjectFactory<Channel> {

  // Valid RMQ connection  
  private final Connection connection;
  // used to count created channels for debugging
  private int count;

  public RMQChannelFactory(Connection connection) {
    this.connection = connection;
    count = 0;
  }

  @Override
  synchronized public Channel create() throws IOException {
    count ++;
    Channel chan = connection.createChannel();
    return chan;

  }

  @Override
  public PooledObject<Channel> wrap(Channel channel) {
    return new DefaultPooledObject<>(channel);
  }

  public int getChannelCount() {
    return count;
  }

  // for all other methods, the no-op implementation
  // in BasePooledObjectFactory will suffice
}