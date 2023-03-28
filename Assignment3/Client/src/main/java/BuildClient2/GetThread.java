package BuildClient2;

import Common.ClientThreadAbstract;
import Common.MultiThreadedClientAbstract;
import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;

public class GetThread extends ClientThreadAbstract {
  private Integer randomId;
  private Boolean callMatches = false;
  private static int REQUEST_PER_SECOND = 5;
  private int totalRequests = 0;
  private long totalLatency = 0;
  private long minLatency = Long.MAX_VALUE;
  private long maxLatency = 0;
  private Double meanLatency;

  public GetThread(MultiThreadedClientAbstract multiThreadedClientAbstract, int num) {
    super(multiThreadedClientAbstract, num);
  }

  @Override
  public void generateRandomValues() {
    randomId = random.nextInt(SWIPER_ID_UPPER_BOUND) + 1;
    callMatches = random.nextInt(2) == 1 ? true : false;
  }

  @Override
  public ApiResponse sendRequest() throws ApiException {
    String stringId = String.valueOf(randomId);
    if (callMatches) {
      return matchesApi.matchesWithHttpInfo(stringId);
    }
    return statsApi.matchStatsWithHttpInfo(stringId);
  }

  @Override
  public void run() {
    long start = System.currentTimeMillis();
    MultiThreadedClientAbstract client = getMultiThreadedClientAbstract();
    while (client.getProcessedRequests() < client.getNumOfRequests()) {
      if (System.currentTimeMillis() - start >= 1000) {
        start = System.currentTimeMillis();

        for (int i = 0; i < REQUEST_PER_SECOND; i++) {
          generateRandomValues();
          try {
            long reqStart = System.currentTimeMillis();
            sendRequest();
            long currLatency = System.currentTimeMillis() - reqStart;
            minLatency = Math.min(minLatency, currLatency);
            maxLatency = Math.max(maxLatency, currLatency);

            totalLatency += (currLatency);
            totalRequests++;
          } catch (ApiException e) {
            System.err.println(e.getMessage());
          }
        }
      }
    }
  }

  public void printLatencyStats() {
    meanLatency = totalLatency * 1.0 / totalRequests * 1.0;
    System.out.println("Get Request Stats");
    System.out.println("Min latency was: " + minLatency);
    System.out.println("Max latency was: " + maxLatency);
    System.out.println(String.format("Mean latency was: %1.2f", meanLatency));
  }
}
