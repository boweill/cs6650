package BuildClient1;

import Common.MultiThreadedClientAbstract;

public class MultiThreadedClient extends MultiThreadedClientAbstract {

  public MultiThreadedClient(int numOfThreads, int numOfRequests) {
    super(numOfThreads, numOfRequests);
    threads = new SwipeThread[numOfThreads];

    for (int i = 0; i < numOfThreads; i++) {
      threads[i] = new SwipeThread(this, i);
    }
  }

  public void start() throws InterruptedException {

    long start = System.currentTimeMillis();
    setStart(start);
    super.start();
  }
}