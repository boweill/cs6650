package Servlet;

import Bean.ResponseMsg;
import Bean.SwipeDetails;
import com.google.gson.Gson;
import java.io.IOException;
import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;

@WebServlet(name = "Servlet.SwipeServlet", value = "/Servlet.SwipeServlet")
public class SwipeServlet extends HttpServlet {

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
      swipeDetails = (SwipeDetails) gson.fromJson(sb.toString(), SwipeDetails.class);
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
