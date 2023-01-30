package BuildClient2;

import Common.SwipeThreadAbstract;
import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import java.util.Queue;

public class RecordSwipeThread extends SwipeThreadAbstract {
  private Queue<Record> recordQueue;

  public RecordSwipeThread(MultiThreadedClient2 multiThreadedClient2, Queue<Record> recordQueue, int num) {
    super(multiThreadedClient2, num);
    this.recordQueue = recordQueue;
  }

  @Override
  public void run() {

    MultiThreadedClient2 multiThreadedClient2 = (MultiThreadedClient2) getMultiThreadedClientAbstract();

    while (true) {
      // Attempt to increase the number of processed request first.
      // if successful boolean increase will return true.
      // Only then we proceed with sending the request,
      // Otherwise we break out of the loop and return.
      boolean increase = multiThreadedClient2.IncreaseProcessedRequest();
      if (!increase) {
        break;
      }
      // Generate random values used to send this request.
      generateRandomValues();
      boolean success = false;
      int retries = 5;

      while (!success && retries > 0) {
        try {
          long start = System.currentTimeMillis();
          ApiResponse<Void> response = sendRequest();
          // Uncomment line below to print out thread number to check we have a proper multithreaded behavior.
          // System.out.println(num + " " + response.getStatusCode());
          long end = System.currentTimeMillis();
          int latency = (int) (end - start);

          // Creating new metric record for current request and push it onto the queue
          // which is watched by the MonitorThread.
          Record record = new Record(start, end, latency, RequestType.POST, response.getStatusCode());
          multiThreadedClient2.addToRecord(record);
          success = true;
        } catch (ApiException e) {
          int code = e.getCode();
          // Retry the request if we get a 5xx or 4xx response.
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
        multiThreadedClient2.increaseSuccessRequests();
      }
      else {
        multiThreadedClient2.increaseFailedRequests();
      }
    }
  }
}
