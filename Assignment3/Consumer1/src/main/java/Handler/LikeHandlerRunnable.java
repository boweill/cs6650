package Handler;

import Bean.DataWriter;
import Bean.SwipeDetails;
import Bean.User;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.google.gson.Gson;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LikeHandlerRunnable implements Runnable {

  private String exchangeName;
  private String queueName;
  private Connection conn;
  private DataWriter writer;
  private Gson gson = new Gson();
  Map<String, User> userMap = new HashMap<>();
  int messages = 0;
  long start = System.currentTimeMillis();

  public LikeHandlerRunnable(String exchangeName, String queueName, Connection conn, DataWriter writer) {
    this.exchangeName = exchangeName;
    this.queueName = queueName;
    this.conn = conn;
    this.writer = writer;
  }

  @Override
  public void run() {
    try {
      final Channel channel = conn.createChannel();

      channel.queueDeclare(queueName, true, false, false, null);

      channel.basicQos(1);
      // Print out confirmation message indicating threads are ready
      System.out.println(" [*] Thread waiting for messages. To exit press CTRL+C - v32");

      // Callback function used to handle the queue messages.
      DeliverCallback deliverCallback = (consumerTag, delivery) -> {
        // Get message and acknowledge message delivery.
        String message = new String(delivery.getBody(), "UTF-8");
        channel.basicAck(delivery.getEnvelope().getDeliveryTag(), true);

        messages++;

        // Parse the swipeDetails in json format into a SwipeDetails object
        SwipeDetails swipeDetails = gson.fromJson(message, SwipeDetails.class);

        String swiper = swipeDetails.getSwiper();
        String swipee = swipeDetails.getSwipee();
        Boolean like = swipeDetails.getLike();

        userMap.putIfAbsent(swiper, new User(swiper, null, 0, 0, null));
        userMap.putIfAbsent(swipee, new User(swipee, null, 0, 0, null));

        User swiperUser = userMap.get(swiper);
        User swipeeUser = userMap.get(swiper);

        // If it's a like we update likedUsers of the swiper
        // if the swipee liked the swiper back we update matches for both of them
        if (like) {
          if (swipeeUser.getLikedUsers() == null) {
            swiperUser.setLikedUsers(new HashSet<>());
          }
          swiperUser.getLikedUsers().add(swipee);
          swiperUser.setLikes(swiperUser.getLikes() + 1);

          Set<String> swipeeLikes = swipeeUser.getLikedUsers();
          if (swipeeLikes != null && swipeeUser.getLikedUsers().contains(swiper)) {
            if (swiperUser.getMatches() == null) {
              swiperUser.setMatches(new HashSet<>());
            }
            if (swipeeUser.getMatches() == null) {
              swipeeUser.setMatches(new HashSet<>());
            }
            swiperUser.getMatches().add(swipee);
            swipeeUser.getMatches().add(swiper);
          }
        } else {
          swiperUser.setDislikes(swiperUser.getDislikes() + 1);
        }

        // Do a batch write each time we've collected 500 messages
        if (messages == 500 || System.currentTimeMillis() - start > 10000) {
          writer.batchProcess(new HashMap<String, User>(userMap));
          messages = 0;
          start = System.currentTimeMillis();
          userMap = new HashMap<String, User>();
        }
      };

      // Specify that this consumer subscribes to the queue with the callback above.
      channel.basicConsume(queueName, false, deliverCallback, consumerTag -> { });

    } catch (IOException ex) {
      Logger.getLogger(LikeHandlerRunnable.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
}
