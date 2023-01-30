package BuildClient1;

import Common.ExecutorAbstract;
import Common.MultiThreadedClientAbstract;

public class Executor extends ExecutorAbstract {

  public Executor(MultiThreadedClientAbstract multiThreadedClientAbstract) {
    super(multiThreadedClientAbstract);
  }

  public void printResults() {
    MultiThreadedClient multiThreadedClient = (MultiThreadedClient) getMultiThreadedClientAbstract();
    long timeElapsed = multiThreadedClient.getTimeElapsed();
    int successfulRequests = multiThreadedClient.getSuccessfulRequests();
    int failedRequests = multiThreadedClient.getFailedRequests();
    double throughput = multiThreadedClient.getNumOfRequests() * 1000.0 / timeElapsed;
    System.out.println(String.format(
        "The requests took %1$s milliseconds to complete.\n"
            + "With %2$s successes and %3$s failures.\n"
            + "Throughput is %4$.2f\n"
            + "Number of threads: %5$s\n"
            + "Throughput estimate: %6$.2f",
        timeElapsed,
        successfulRequests,
        failedRequests,
        throughput,
        multiThreadedClient.getNumOfRequests(),
        MultiThreadedClient.THROUGHPUT_ESTIMATE));
  }
}
