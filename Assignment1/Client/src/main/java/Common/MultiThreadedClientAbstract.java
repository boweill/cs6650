package Common;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class MultiThreadedClientAbstract {

  private long timeElapsed;
  private int numOfThreads;
  private int numOfRequests;
  private int processedRequests;
  private AtomicInteger successfulRequests;
  private AtomicInteger failedRequests;

  public MultiThreadedClientAbstract(int numOfThreads, int numOfRequests) {
    this.numOfThreads = numOfThreads;
    this.numOfRequests = numOfRequests;
    this.successfulRequests = new AtomicInteger(0);
    this.failedRequests = new AtomicInteger(0);
  }

  public synchronized boolean IncreaseProcessedRequest() {
    if (processedRequests < numOfRequests) {
      processedRequests++;
      return true;
    }
    return false;
  }

  public int getProcessedRequests() {
    return processedRequests;
  }

  public void increaseSuccessRequests() {
    successfulRequests.incrementAndGet();
  }

  public void increaseFailedRequests() {
    failedRequests.incrementAndGet();
  }

  public abstract void start() throws InterruptedException;

  public long getTimeElapsed() {
    return timeElapsed;
  }

  public void setTimeElapsed(long timeElapsed) {
    this.timeElapsed = timeElapsed;
  }

  public int getNumOfRequests() {
    return numOfRequests;
  }

  public int getSuccessfulRequests() {
    return successfulRequests.get();
  }

  public int getFailedRequests() {
    return failedRequests.get();
  }

  public int getNumOfThreads() {
    return numOfThreads;
  }
}