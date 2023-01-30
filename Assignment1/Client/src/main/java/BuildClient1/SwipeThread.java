package BuildClient1;

import Common.MultiThreadedClientAbstract;
import Common.SwipeThreadAbstract;
import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;

public class SwipeThread extends SwipeThreadAbstract {

  public SwipeThread(MultiThreadedClient multiThreadedClient, int num) {
    super(multiThreadedClient, num);
  }

  @Override
  public void run() {
    MultiThreadedClientAbstract multiThreadedClientAbstract = getMultiThreadedClientAbstract();
    while (true) {
      // Attempt to increase the number of processed request first.
      // if successful boolean increase will return true.
      // Only then we proceed with sending the request,
      // Otherwise we break out of the loop and return.
      boolean increase = multiThreadedClientAbstract.IncreaseProcessedRequest();
      if (!increase) {
        break;
      }
      generateRandomValues();
      boolean success = false;
      int retries = 5;

      while (!success && retries > 0) {
        try {
          ApiResponse<Void> response = sendRequest();
          // Uncomment line below to print out thread number to check we have a proper multithreaded behavior.
          // System.out.println(num + " " + response.getStatusCode());
          success = true;
        } catch (ApiException e) {
          int code = e.getCode();
          // Retry if we get a 5xx or 4xx status code.
          if (code / 100 == 5 || code / 100 == 4) {
            retries--;
          }
          else {
            System.out.println(e.getCode());
            e.printStackTrace();
            break;
          }
        }
      }
      if (success) {
        multiThreadedClientAbstract.increaseSuccessRequests();
      }
      else {
        multiThreadedClientAbstract.increaseFailedRequests();
      }
    }
  }
}
