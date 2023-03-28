package Servlet;

import Bean.DynamoDBHandler;
import Bean.MatchStats;
import Bean.ResponseMsg;
import Bean.User;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.google.gson.Gson;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "Servlet.StatsServlet", value = "/Servlet.StatsServlet")
public class StatsServlet extends HttpServlet {

  private AmazonDynamoDB ddb;
  private DynamoDBMapper mapper;
  @Override
  public void init() throws ServletException {
    this.ddb = DynamoDBHandler.getDDBClient();
    this.mapper = new DynamoDBMapper(ddb);
  }

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

    int likes = user.getLikes();
    int dislikes = user.getDislikes();

    MatchStats matchStats = new MatchStats(likes, dislikes);
    res.setStatus(HttpServletResponse.SC_OK);
    res.getWriter().write(gson.toJson(matchStats));
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
