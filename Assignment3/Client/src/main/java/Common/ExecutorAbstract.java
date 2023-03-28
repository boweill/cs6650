package Common;

public abstract class ExecutorAbstract {

  private MultiThreadedClientAbstract multiThreadedClientAbstract;

  public ExecutorAbstract(MultiThreadedClientAbstract multiThreadedClientAbstract) {
    this.multiThreadedClientAbstract = multiThreadedClientAbstract;
  }

  public MultiThreadedClientAbstract getMultiThreadedClientAbstract() {
    return multiThreadedClientAbstract;
  }

  public void run() {
    try {
      multiThreadedClientAbstract.start();
    } catch (InterruptedException e) {
      System.out.println("Execution interrupted");
      e.printStackTrace();
    }
  }

  public abstract void printResults();
}
