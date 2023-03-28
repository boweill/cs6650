package BuildClient2;

import com.opencsv.CSVWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

public class MonitorThread extends Thread{
  private MultiThreadedClient2 multiThreadedClient2;
  private int[] latencies;
  private int numOfRequests;
  private int counter;
  private int lastProcessedRequests;
  private long start;
  private int secondsElapsed;
  private long totalResponseTime;
  private int minResponseTime;
  private int maxResponseTime;

  public MonitorThread(MultiThreadedClient2 multiThreadedClient2, int numOfRequests) {
    this.multiThreadedClient2 = multiThreadedClient2;
    this.numOfRequests = numOfRequests;
    this.minResponseTime = Integer.MAX_VALUE;
    latencies = new int[numOfRequests];
  }

  public void setStart(long start) {
    this.start = start;
  }

  public double getMeanResponseTime() {
    return totalResponseTime * 1.0 / numOfRequests;
  }

  public int getMinResponseTime() {
    return minResponseTime;
  }

  public int getMaxResponseTime() {
    return maxResponseTime;
  }

  public double getMedianResponseTime() {
    if (numOfRequests % 2 == 0) {
      return (latencies[numOfRequests / 2] + latencies[numOfRequests / 2 - 1]) / 2.0;
    }
    return latencies[numOfRequests / 2];
  }

  public int getPercentileResponseTime(int percentile) {
    int idx = (int) Math.ceil(percentile / 100.0 * (numOfRequests));
    return latencies[idx - 1];
  }

  @Override
  public void run() {
    // File and Writer for writing records for individual requests.
    File individualRecordFile = new File("./src/main/java/BuildClient2/individual_request_record.csv");
    FileWriter individualOutputFile;
    CSVWriter individualOutputWriter;
    String[] headerIndividual = new String[] {"Start Time", "Request Type", "Latency", "Response Code"};

    // File and Writer for writing throughput every second.
    File throughputRecordFile = new File("./src/main/java/BuildClient2/throughput_record.csv");
    FileWriter throughputOutputFile;
    CSVWriter throughputOutputWriter;
    String[] headerThroughput = new String[] {"Second", "Throughput"};
    try {
      individualOutputFile = new FileWriter(individualRecordFile);
      individualOutputWriter = new CSVWriter(individualOutputFile);
      individualOutputWriter.writeNext(headerIndividual);

      throughputOutputFile = new FileWriter(throughputRecordFile);
      throughputOutputWriter = new CSVWriter(throughputOutputFile);
      throughputOutputWriter.writeNext(headerThroughput);
    } catch (IOException e) {
      e.printStackTrace();
      return;
    }
    while (counter < numOfRequests) {
      // If we just entered a new second, calculate how many more requests were
      // processed in the past second and write data to the throughput per second file.
      if ((System.currentTimeMillis() - start) / 1000 > secondsElapsed) {
        secondsElapsed++;
        int temp = multiThreadedClient2.getProcessedRequests();
        int currProcessedRequests = temp - lastProcessedRequests;
        lastProcessedRequests = temp;
        String[] throughputData = new String[] {String.valueOf(secondsElapsed), String.valueOf(currProcessedRequests)};
        throughputOutputWriter.writeNext(throughputData);
      }

      // Pull a record from recordQueue, use the record to update
      // statistics we are interested in and write data to file.
      Record curr = multiThreadedClient2.pullFromRecord();
      if (curr == null) {
        continue;
      }
      int responseTime = curr.getLatency();
      totalResponseTime += responseTime;
      minResponseTime = Math.min(minResponseTime, responseTime);
      maxResponseTime = Math.max(maxResponseTime, responseTime);
      latencies[counter] = responseTime;
      String[] data = new String[] {
          String.valueOf(curr.getStart()),
          curr.getRequestType().label,
          String.valueOf(responseTime),
          String.valueOf(curr.getResponseCode())};
      individualOutputWriter.writeNext(data);
      counter++;
    }

    try{
      individualOutputWriter.close();
      throughputOutputWriter.close();
    } catch (IOException e) {
      e.printStackTrace();
    }

    // Sort the latencies array for percentile calculation.
    Arrays.sort(latencies);
  }
}
