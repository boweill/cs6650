import BuildClient1.MultiThreadedClient;
import org.junit.jupiter.api.Test;

class MultiThreadedClientTest {

  @Test
  public void singleThreadThroughputTest() {
    int numOfRequests = 10000;
    MultiThreadedClient singleThreadClient = new MultiThreadedClient(1, numOfRequests);
    try {
      singleThreadClient.start();
    } catch (InterruptedException e) {
      System.out.println("Execution interrupted");
      e.printStackTrace();
    }

    long timeElapsed = singleThreadClient.getTimeElapsed();
    double throughput = numOfRequests * 1000.0 / timeElapsed;
    System.out.println(throughput);
  }

}