package Servlet;

import Bean.ResponseMsg;
import Bean.SwipeDetails;
import QueueUtils.RMQChannelFactory;
import QueueUtils.RMQChannelPool;
import com.google.gson.Gson;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.util.concurrent.TimeoutException;

@WebServlet(name = "Servlet.SwipeServlet", value = "/Servlet.SwipeServlet")
public class SwipeServlet extends HttpServlet {
  // public final static String EXCHANGE_NAME = "6650Exchange";
  public final static String QUEUE_NAME = "tempStore";
  // public final static String QUEUE2_NAME = "threadExQ4";

  private final static int CHANNEL_POOL_SIZE = 100;
  private Connection conn;
  private RMQChannelPool channelPool;

  @Override
  public void init() throws ServletException {
    super.init();
    ConnectionFactory factory = new ConnectionFactory();
    // Establish RabbitMQ connection with following info.
    // Modify here if needed.
    factory.setHost("52.26.252.125");
    factory.setVirtualHost("/");
    factory.setUsername("admin");
    factory.setPassword("password");
    ExecutorService es = Executors.newFixedThreadPool(50);
    try {
      conn = factory.newConnection(es);
    } catch (IOException | TimeoutException ex) {
      System.err.println("Unable to connect to Queue : " + ex.getMessage());
    }
     RMQChannelFactory channelFactory = new RMQChannelFactory(conn);
     channelPool = new RMQChannelPool(CHANNEL_POOL_SIZE, channelFactory);
  }

  // To be implemented in future assignments
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
  }

  // Implementation for Assignment1
  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
    res.setContentType("application/json");
    String urlPath = req.getPathInfo();
    Gson gson = new Gson();

    // Check if there is a path at all
    if (urlPath == null || urlPath.isEmpty()) {
      res.setStatus(HttpServletResponse.SC_NOT_FOUND);
      ResponseMsg responseMsg = new ResponseMsg("missing parameters");
      res.getWriter().write(gson.toJson(responseMsg));
      return;
    }

    String[] urlParts = urlPath.split("/");

    // URL validation
    if (!isUrlValid(urlParts)) {
      res.setStatus(HttpServletResponse.SC_NOT_FOUND);
      ResponseMsg responseMsg = new ResponseMsg("User not found");
      res.getWriter().write(gson.toJson(responseMsg));
      return;
    }

    SwipeDetails swipeDetails;
    String swipeDetailsJson;
    String swiper;
    String swipee;
    String message;
    try {
      StringBuilder sb = new StringBuilder();
      String s;

      // Reading request data
      while ((s = req.getReader().readLine()) != null) {
        sb.append(s);
      }

      // Binding request data to a SwipeDetails object
      swipeDetailsJson = sb.toString();
      swipeDetails = (SwipeDetails) gson.fromJson(swipeDetailsJson, SwipeDetails.class);
      swiper = swipeDetails.getSwiper();
      swipee = swipeDetails.getSwipee();
      message = swipeDetails.getMessage();
      Boolean like = urlParts[1].equals("right") ? true : false;
      swipeDetails.setLike(like);
      swipeDetailsJson = gson.toJson(swipeDetails);

      // Must have swiper and swipee
      if (swiper == null || swipee == null) {
        throw new Exception("Missing parameters!");
      }

      // Message can be null but in case of not being null should be less than 256 chars long
      if (message != null && message.length() > 256) {
        throw new Exception("Message too long!");
      }
    } catch (Exception ex) {
      res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      // Any request that violates the restrictions for one of swiper/swipee/message is considered
      // to have invalid input; specific error message included in server response.
      ResponseMsg responseMsg = new ResponseMsg(String.format("Invalid inputs : %1$s", ex.getMessage()));
      res.getWriter().write(gson.toJson(responseMsg));
      return;
    }

    try {
      // borrow a channel from our channel pool.
      Channel channel = channelPool.borrowObject();

      // declare a fanout exchange to send messages to queues for both consumers.
      channel.queueDeclare(SwipeServlet.QUEUE_NAME, true, false, false, null);

      // set the content bytes and content type for the message to be pushed to the queue.
      byte[] payloadBytes = swipeDetailsJson.getBytes(StandardCharsets.UTF_8);
      AMQP.BasicProperties props = new AMQP.BasicProperties.Builder().contentType("application/json").build();

      // publishing the message to the queue as our tempStore.
      channel.basicPublish("", SwipeServlet.QUEUE_NAME, props, payloadBytes);

      // returning channel and print out a confirmation message.
      channelPool.returnObject(channel);
      System.out.println(" [x] Sent '" + swipeDetailsJson + "'");
    } catch (Exception e) {
      e.printStackTrace();
      return;
    }

    // We get response like "User 123 liked user 456" upon successful request
    String action = swipeDetails.getLike() ? "liked" : "disliked";
    res.setStatus(HttpServletResponse.SC_CREATED);
    ResponseMsg responseMsg = new ResponseMsg(String.format("User %1$s %2$s user %3$s. -v19", swiper, action, swipee));

    res.getWriter().write(gson.toJson(responseMsg));
  }

  private boolean isUrlValid(String[] urlParts) {
    if (urlParts.length != 2) {
      return false;
    }

    if (!urlParts[1].equals("left") && !urlParts[1].equals("right")) {
      return false;
    }

    return true;
  }
}
