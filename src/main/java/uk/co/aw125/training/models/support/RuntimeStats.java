package uk.co.aw125.training.models.support;

import uk.co.aw125.training.helpers.ThreadUtilities;

public class RuntimeStats {

  long freeMemory;
  long totalMemory;
  long maxMemory;
  int availableProcessors;
  int threadCount;


  public RuntimeStats() {
    Runtime runtime = Runtime.getRuntime();
    freeMemory = runtime.freeMemory();
    totalMemory = runtime.totalMemory();
    maxMemory = runtime.maxMemory();
    availableProcessors = runtime.availableProcessors();
    threadCount = 0;
    Thread[] threads = ThreadUtilities.getAllThreads();

    if (threads != null) {
      threadCount = threads.length;
    }

  }


  public long getFreeMemory() {
    return freeMemory;
  }


  public void setFreeMemory(long freeMemory) {
    this.freeMemory = freeMemory;
  }


  public long getTotalMemory() {
    return totalMemory;
  }


  public void setTotalMemory(long totalMemory) {
    this.totalMemory = totalMemory;
  }


  public long getMaxMemory() {
    return maxMemory;
  }


  public void setMaxMemory(long maxMemory) {
    this.maxMemory = maxMemory;
  }


  public int getAvailableProcessors() {
    return availableProcessors;
  }


  public void setAvailableProcessors(int availableProcessors) {
    this.availableProcessors = availableProcessors;
  }


  public int getThreadCount() {
    return threadCount;
  }


  public void setThreadCount(int threadCount) {
    this.threadCount = threadCount;
  }



}
