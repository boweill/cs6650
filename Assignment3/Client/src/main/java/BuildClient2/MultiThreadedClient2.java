package BuildClient2;

import Common.MultiThreadedClientAbstract;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.concurrent.EventCountCircuitBreaker;

public class MultiThreadedClient2 extends MultiThreadedClientAbstract {

  private MonitorThread monitorThread;
  private Queue<Record> recordQueue;
  private EventCountCircuitBreaker breaker;
  private GetThread getThread;

  public MultiThreadedClient2(int numOfThreads, int numOfRequests) {
    super(numOfThreads + 1, numOfRequests);
    threads = new Thread[numOfThreads + 1];
    this.recordQueue = new LinkedList<>();
    monitorThread = new MonitorThread(this, numOfRequests);
    getThread = new GetThread(this, 1);
    breaker = new EventCountCircuitBreaker(5000, 1, TimeUnit.SECONDS, 3000);

    for (int i = 0; i < numOfThreads; i++) {
      threads[i] = new RecordSwipeThread(this, recordQueue, i);
    }
    threads[numOfThreads] = getThread;
  }

  public synchronized boolean incrementAndCheckState() {
    return breaker.incrementAndCheckState();
  }

  public synchronized boolean checkState() {
    return breaker.checkState();
  }

  public synchronized void addToRecord(Record record) {
    recordQueue.add(record);
  }

  public synchronized Record pullFromRecord() {
    if (recordQueue.isEmpty()) {
      return null;
    }
    return recordQueue.poll();
  }

  @Override
  public void start() throws InterruptedException {

    monitorThread.start();

    long start = System.currentTimeMillis();
    monitorThread.setStart(start);
    setStart(start);
    super.start();

    monitorThread.join();
  }

  public MonitorThread getMonitorThread() {
    return monitorThread;
  }

  public GetThread getGetThread() {return getThread;}
}