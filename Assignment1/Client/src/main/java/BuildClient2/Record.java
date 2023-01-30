package BuildClient2;

public class Record {

  private long start;
  private long end;
  private int latency;
  private RequestType requestType;
  private int responseCode;

  public Record(long start, long end, int latency, RequestType requestType, int responseCode) {
    this.start = start;
    this.end = end;
    this.latency = latency;
    this.requestType = requestType;
    this.responseCode = responseCode;
  }

  public long getStart() {
    return start;
  }

  public long getEnd() {
    return end;
  }

  public int getLatency() {
    return latency;
  }

  public RequestType getRequestType() {
    return requestType;
  }

  public int getResponseCode() {
    return responseCode;
  }
}
