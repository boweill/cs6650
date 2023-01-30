package BuildClient2;

import Common.MultiThreadedClientAbstract;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;

public class MultiThreadedClient2 extends MultiThreadedClientAbstract {

  private Thread[] threads;
  private MonitorThread monitorThread;
  private Queue<Record> recordQueue;

  public MultiThreadedClient2(int numOfThreads, int numOfRequests) {
    super(numOfThreads, numOfRequests);
    threads = new RecordSwipeThread[numOfThreads];
    this.recordQueue = new LinkedList<>();
    monitorThread = new MonitorThread(this, numOfRequests);

    for (int i = 0; i < numOfThreads; i++) {
      threads[i] = new RecordSwipeThread(this, recordQueue, i);
    }
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

  public void start() throws InterruptedException {

    monitorThread.start();
    int numOfThreads = getNumOfThreads();

    long start = System.currentTimeMillis();
    monitorThread.setStart(start);
    for (int i = 0; i < numOfThreads; i++) {
      threads[i].start();
    }

    for (int i = 0; i < numOfThreads; i++) {
      threads[i].join();
    }

    setTimeElapsed(System.currentTimeMillis() - start);

    monitorThread.join();
  }

  public MonitorThread getMonitorThread() {
    return monitorThread;
  }
}