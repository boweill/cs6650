package Servlet;

import Bean.DynamoDBHandler;
import Bean.Matches;
import Bean.ResponseMsg;
import Bean.SwipeDetails;
import Bean.User;
import QueueUtils.RMQChannelFactory;
import QueueUtils.RMQChannelPool;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.fasterxml.jackson.databind.deser.DataFormatReaders.Match;
import com.google.gson.Gson;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeoutException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "Servlet.MatchesServlet", value = "/Servlet.MatchesServlet")
public class MatchesServlet extends HttpServlet {

  private AmazonDynamoDB ddb;
  private DynamoDBMapper mapper;
  @Override
  public void init() throws ServletException {
    this.ddb = DynamoDBHandler.getDDBClient();
    // DynamoDBHandler.createTable(ddb, "Users", "userId");
    this.mapper = new DynamoDBMapper(ddb);
  }

  // To be implemented in future assignments
  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse res)
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

    String userId = urlParts[1];

    User user = mapper.load(User.class, userId);
    if (user == null) {
      res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      ResponseMsg responseMsg = new ResponseMsg("Invalid User ID");
      res.getWriter().write(gson.toJson(responseMsg));
      return;
    }
    Set<String> matchesSet = user.getMatches();
    Set<String> limitedMatches = new HashSet<>();
    int i = 0;
    if (matchesSet != null) {
      for (String match : matchesSet) {
        limitedMatches.add(match);
        i++;
        if (i == 100) {
          break;
        }
      }
    }

    Matches matches = new Matches(limitedMatches.toArray(new String[0]));

    System.out.println(String.format("Retrieving matches for user %1$s", userId));
    res.setStatus(HttpServletResponse.SC_OK);
    res.getWriter().write(gson.toJson(matches));
  }

  // Implementation for Assignment1
  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
  }

  private boolean isUrlValid(String[] urlParts) {
    if (urlParts.length != 2) {
      return false;
    }

    try {
      Integer userId = Integer.parseInt(urlParts[1]);
    } catch (NumberFormatException nfe) {
      return false;
    }

    return true;
  }
}
