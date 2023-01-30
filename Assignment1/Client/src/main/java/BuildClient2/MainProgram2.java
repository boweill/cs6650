package BuildClient2;

public class MainProgram2 {
  public static void main(String[] args) {
    int numOfThreads = 90;
    int numOfRequests = 500000;
    MultiThreadedClient2 multiThreadedClient2 = new MultiThreadedClient2(numOfThreads, numOfRequests);
    Executor2 executor2 = new Executor2(multiThreadedClient2);
    executor2.run();
    executor2.printResults();
  }

}
