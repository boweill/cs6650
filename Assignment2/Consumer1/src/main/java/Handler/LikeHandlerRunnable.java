package Handler;

import Bean.DataAggregator;
import Bean.SwipeDetails;
import com.google.gson.Gson;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LikeHandlerRunnable implements Runnable {

  private String exchangeName;
  private String queueName;
  private Connection conn;
  private DataAggregator aggregator;
  private Gson gson = new Gson();

  public LikeHandlerRunnable(String exchangeName, String queueName, Connection conn, DataAggregator aggregator) {
    this.exchangeName = exchangeName;
    this.queueName = queueName;
    this.conn = conn;
    this.aggregator = aggregator;
  }

  @Override
  public void run() {
    try {
      final Channel channel = conn.createChannel();

      // Declare the fanout exchange and bind the queue this consumer is polling from to
      // the exchange
      channel.exchangeDeclare(exchangeName, BuiltinExchangeType.FANOUT);
      channel.queueBind(queueName, exchangeName, "");

      channel.basicQos(1);
      // Print out confirmation message indicating threads are ready
      System.out.println(" [*] Thread waiting for messages. To exit press CTRL+C - v6");

      // Callback function used to handle the queue messages.
      DeliverCallback deliverCallback = (consumerTag, delivery) -> {
        // Get message and acknowledge message delivery.
        String message = new String(delivery.getBody(), "UTF-8");
        channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);

        // Parse the swipeDetails in json format into a SwipeDetails object
        SwipeDetails swipeDetails = gson.fromJson(message, SwipeDetails.class);
        aggregator.handleSwipeDetails(swipeDetails);

        String swiper = swipeDetails.getSwiper();
        String swipee = swipeDetails.getSwipee();
        String action = swipeDetails.getLike() ? "liked" : "disliked";

        // Printing out likes, dislikes and matches of user who sent the request
        // to confirm the data aggregation class is working as expected
        System.out.println( "User " + swiper + " " + action + " user " + swipee + ";\n"
            + "User " + swiper + " liked " + aggregator.getLikesForSwiper(swiper) + " user(s);\n"
            + "Some of them are: " + aggregator.getPotentialMatchesForSwiper(swiper) + ";\n"
            + "Disliked " + aggregator.getDislikesForSwiper(swiper) + " user(s);\n"
            + "And has " + aggregator.getNumOfMatchesForSwiper(swiper) + " match(es).\n");
      };

      // Specify that this consumer subscribes to the queue with the callback above.
      channel.basicConsume(queueName, false, deliverCallback, consumerTag -> { });

    } catch (IOException ex) {
      Logger.getLogger(LikeHandlerRunnable.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
}
