package Common;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class MultiThreadedClientAbstract {

  protected Thread[] threads;
  private long start;
  private long timeElapsed;
  private int numOfThreads;
  private int numOfRequests;
  private int processedRequests;
  private AtomicInteger successfulRequests;
  private AtomicInteger failedRequests;
  private double throughput;

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

  public double getThroughput() {
    return throughput;
  }

  public void setThroughput(double throughput) {
    this.throughput = throughput;
  }

  public long getStart() {
    return start;
  }

  public void setStart(long start) {
    this.start = start;
  }

  public void start() throws InterruptedException {
    for (int i = 0; i < numOfThreads; i++) {
      threads[i].start();
    }

    for (int i = 0; i < numOfThreads; i++) {
      threads[i].join();
    }

    setTimeElapsed(System.currentTimeMillis() - start);
    setThroughput(getNumOfRequests() * 1000.0 / getTimeElapsed());
  }
}