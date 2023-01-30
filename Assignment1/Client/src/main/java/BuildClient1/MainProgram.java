package BuildClient1;

public class MainProgram {
  public static void main(String[] args) {
    int numOfThreads = 90;
    int numOfRequests = 500000;
    MultiThreadedClient multiThreadedClient = new MultiThreadedClient(numOfThreads, numOfRequests);
    Executor executor = new Executor(multiThreadedClient);
    executor.run();
    executor.printResults();
  }

}
