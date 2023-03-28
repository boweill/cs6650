package BuildClient1;

public class MainProgram {
  public static void main(String[] args) {
    int numOfThreads = 50;
    int numOfRequests = 5000;
    double throughputEstimateSingle = getThroughputEstimateSingle(1000);
    MultiThreadedClient multiThreadedClient = new MultiThreadedClient(numOfThreads, numOfRequests);
    Executor executor = new Executor(multiThreadedClient);
    executor.run();
    executor.printResults();
    System.out.println(String.format("Estimated throughput: %1$.2f", throughputEstimateSingle * numOfThreads));
  }

  private static double getThroughputEstimateSingle(int numOfRequests) {
    MultiThreadedClient testClient = new MultiThreadedClient(1, numOfRequests);
    Executor testExecutor = new Executor(testClient);
    testExecutor.run();
    return testClient.getThroughput();
  }

}
