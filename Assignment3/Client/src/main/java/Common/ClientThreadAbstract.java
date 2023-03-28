package Common;

import com.squareup.okhttp.ConnectionPool;
import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import io.swagger.client.api.MatchesApi;
import io.swagger.client.api.StatsApi;
import io.swagger.client.api.SwipeApi;
import io.swagger.client.model.SwipeDetails;
import java.util.Random;

public abstract class ClientThreadAbstract extends Thread {
  private static final String SERVER_BASE_URL = "http://35.80.96.92:8080/Server_war/";
  // private static final String SERVER_BASE_URL = "http://6650elb-708091575.us-west-2.elb.amazonaws.com/Server_war/";
  //private static final String SERVER_BASE_URL = "http://localhost:8080/Server_war_exploded/";
  private static final int MAX_IDLE_CONNECTION = 200;
  private static final long KEEP_ALIVE_DURATION_MS = 300000L;
  protected static final int SWIPER_ID_UPPER_BOUND = 5000;
  protected static final int SWIPEE_ID_UPPER_BOUND = 1000000;
//  private static final int SWIPER_ID_UPPER_BOUND = 10;
//  private static final int SWIPEE_ID_UPPER_BOUND = 10;
  private MultiThreadedClientAbstract multiThreadedClientAbstract;
  protected SwipeApi swipeApi;
  protected MatchesApi matchesApi;
  protected StatsApi statsApi;
  protected Random random = new Random();
  private SwipeDetails swipeDetails;
  private String leftOrRight;
  private int num;

  public ClientThreadAbstract(MultiThreadedClientAbstract multiThreadedClientAbstract, int num) {
    this.multiThreadedClientAbstract = multiThreadedClientAbstract;
    this.num = num;
    ApiClient apiClient = new ApiClient();
    apiClient.setBasePath(SERVER_BASE_URL);
    apiClient.setConnectTimeout(0);
    apiClient.setReadTimeout(0);
    apiClient.getHttpClient().setConnectionPool(new ConnectionPool(MAX_IDLE_CONNECTION, KEEP_ALIVE_DURATION_MS));
    swipeApi = new SwipeApi(apiClient);
    matchesApi = new MatchesApi(apiClient);
    statsApi = new StatsApi(apiClient);
    swipeDetails = new SwipeDetails();
  }

  public void generateRandomValues() {
    int swiperInt = random.nextInt(SWIPER_ID_UPPER_BOUND) + 1;
    int swipeeInt = random.nextInt(SWIPEE_ID_UPPER_BOUND) + 1;
    while (swiperInt == swipeeInt) {
      swipeeInt = random.nextInt(SWIPEE_ID_UPPER_BOUND) + 1;
    }
    String swiper = String.valueOf(swiperInt);
    String swipee = String.valueOf(swipeeInt);
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

  public ApiResponse sendRequest() throws ApiException {
    return swipeApi.swipeWithHttpInfo(swipeDetails, leftOrRight);
  }

}
