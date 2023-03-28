package BuildClient2;

import Common.ExecutorAbstract;
import Common.MultiThreadedClientAbstract;

public class Executor2 extends ExecutorAbstract {

  public Executor2(MultiThreadedClientAbstract multiThreadedClientAbstract) {
    super(multiThreadedClientAbstract);
  }

  public void printResults() {
    MultiThreadedClient2 multiThreadedClient2 = (MultiThreadedClient2) getMultiThreadedClientAbstract();
    long timeElapsed = multiThreadedClient2.getTimeElapsed();
    int successfulRequests = multiThreadedClient2.getSuccessfulRequests();
    int failedRequests = multiThreadedClient2.getFailedRequests();
    double throughput = multiThreadedClient2.getNumOfRequests() * 1000.0 / timeElapsed;
    MonitorThread monitorThread = multiThreadedClient2.getMonitorThread();
    System.out.println(String.format(
        "The requests took %1$s milliseconds to complete.\n"
            + "With %2$s successes and %3$s failures.\n"
            + "Throughput: %4$.2f\n"
            + "Mean response time: %5$.2f\n"
            + "Median response time: %6$.2f\n"
            + "99 percentile response time: %7$s\n"
            + "Minimum response time: %8$s\n"
            + "Maximum response time: %9$s\n",
        timeElapsed,
        successfulRequests,
        failedRequests,
        throughput,
        monitorThread.getMeanResponseTime(),
        monitorThread.getMedianResponseTime(),
        monitorThread.getPercentileResponseTime(99),
        monitorThread.getMinResponseTime(),
        monitorThread.getMaxResponseTime()));

    GetThread getThread = multiThreadedClient2.getGetThread();
    getThread.printLatencyStats();
  }
}
