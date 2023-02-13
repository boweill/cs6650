package Servlet;

import Bean.ResponseMsg;
import Bean.SwipeDetails;
import QueueUtils.RMQChannelFactory;
import QueueUtils.RMQChannelPool;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "Servlet.SwipeServlet", value = "/Servlet.SwipeServlet")
public class SwipeServlet extends HttpServlet {
  public final static String QUEUE_NAME = "threadExQ";
  private Connection conn;
  private Executor threadPool;
  private RMQChannelPool channelPool;

  @Override
  public void init() throws ServletException {
    super.init();
    threadPool = Executors.newFixedThreadPool(5);
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost("52.26.252.125");
    factory.setVirtualHost("/");
    factory.setUsername("admin");
    factory.setPassword("password");
    try {
      conn = factory.newConnection();
    } catch (IOException | TimeoutException ex) {
      System.err.println("Unable to connect to Queue : " + ex.getMessage());
    }
    RMQChannelFactory channelFactory = new RMQChannelFactory(conn);
    channelPool = new RMQChannelPool(5, channelFactory);
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

    // Check if there is a path at all
    if (urlPath == null || urlPath.isEmpty()) {
      res.setStatus(HttpServletResponse.SC_NOT_FOUND);
      res.getWriter().write("missing parameters");
    }

    String[] urlParts = urlPath.split("/");
    Gson gson = new Gson();

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

    threadPool.execute(new PublisherRunnable(channelPool, swipeDetailsJson));
    // We get response like "User 123 liked user 456" upon successful request
    String action = urlParts[1].equals("right") ? "liked" : "disliked";
    res.setStatus(HttpServletResponse.SC_CREATED);

    res.getWriter().write(String.format("User %1$s %2$s user %3$s.", swiper, action, swipee));
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
