package BuildClient1;

import Common.MultiThreadedClientAbstract;

public class MultiThreadedClient extends MultiThreadedClientAbstract {
  public static double THROUGHPUT_ESTIMATE = 60.0;
  private Thread[] threads;

  public MultiThreadedClient(int numOfThreads, int numOfRequests) {
    super(numOfThreads, numOfRequests);
    THROUGHPUT_ESTIMATE *= numOfThreads;
    threads = new SwipeThread[numOfThreads];

    for (int i = 0; i < numOfThreads; i++) {
      threads[i] = new SwipeThread(this, i);
    }
  }

  public void start() throws InterruptedException {

    long start = System.currentTimeMillis();
    int numOfThreads = getNumOfThreads();
    for (int i = 0; i < numOfThreads; i++) {
      threads[i].start();
    }

    for (int i = 0; i < numOfThreads; i++) {
      threads[i].join();
    }

    setTimeElapsed(System.currentTimeMillis() - start);
  }
}