package Common;

import com.squareup.okhttp.ConnectionPool;
import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import io.swagger.client.api.SwipeApi;
import io.swagger.client.model.SwipeDetails;
import java.util.Random;

public abstract class SwipeThreadAbstract extends Thread {
  private static final String serverBaseUrl = "http://35.80.96.92:8080/Server_war/";
  private static final int MAX_IDLE_CONNECTION = 200;
  private static final long KEEP_ALIVE_DURATION_MS = 300000L;
  private static final int SWIPER_ID_UPPER_BOUND = 5000;
  private static final int SWIPEE_ID_UPPER_BOUND = 1000000;
  private MultiThreadedClientAbstract multiThreadedClientAbstract;
  private SwipeApi swipeApi;
  private Random random = new Random();
  private SwipeDetails swipeDetails;
  private String leftOrRight;
  private int num;

  public SwipeThreadAbstract(MultiThreadedClientAbstract multiThreadedClientAbstract, int num) {
    this.multiThreadedClientAbstract = multiThreadedClientAbstract;
    this.num = num;
    ApiClient apiClient = new ApiClient();
    apiClient.setBasePath(serverBaseUrl);
    apiClient.setConnectTimeout(0);
    apiClient.setReadTimeout(0);
    apiClient.getHttpClient().setConnectionPool(new ConnectionPool(MAX_IDLE_CONNECTION, KEEP_ALIVE_DURATION_MS));
    swipeApi = new SwipeApi(apiClient);
    swipeDetails = new SwipeDetails();
  }

  public void generateRandomValues() {
    String swiper = String.valueOf(random.nextInt(SWIPER_ID_UPPER_BOUND) + 1);
    String swipee = String.valueOf(random.nextInt(SWIPEE_ID_UPPER_BOUND) + 1);
    byte[] messageArray = new byte[256];

    random.nextBytes(messageArray);
    String message = new String(messageArray);
    swipeDetails.setSwiper(swiper);
    swipeDetails.setSwipee(swipee);
    swipeDetails.setComment(message); // SwipeDetails | response details
    leftOrRight = random.nextInt(2) == 0 ? "right" : "left"; // String | I like or dislike user
  }

  public MultiThreadedClientAbstract getMultiThreadedClientAbstract() {
    return this.multiThreadedClientAbstract;
  }

  public ApiResponse<Void> sendRequest() throws ApiException {
    return swipeApi.swipeWithHttpInfo(swipeDetails, leftOrRight);
  }

}
