package BuildClient2;

import BuildClient1.Executor;
import BuildClient1.MultiThreadedClient;

public class MainProgram2 {
  public static void main(String[] args) {
    int numOfThreads = 50;
    int numOfRequests = 500000;
    double throughputEstimateSingle = getThroughputEstimateSingle(1000);
    MultiThreadedClient2 multiThreadedClient2 = new MultiThreadedClient2(numOfThreads, numOfRequests);
    Executor2 executor2 = new Executor2(multiThreadedClient2);
    executor2.run();
    executor2.printResults();

    System.out.println(String.format("Number of Threads: %1$s", multiThreadedClient2.getNumOfThreads()));
    System.out.println(String.format("Estimated throughput: %1$.2f", throughputEstimateSingle * numOfThreads));
  }

  private static double getThroughputEstimateSingle(int numOfRequests) {
    MultiThreadedClient testClient = new MultiThreadedClient(1, numOfRequests);
    Executor testExecutor = new Executor(testClient);
    testExecutor.run();
    return testClient.getThroughput();
  }

}
