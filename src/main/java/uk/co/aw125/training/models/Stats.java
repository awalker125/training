package uk.co.aw125.training.models;

public class Stats {

  int total;
  int success;
  int failed;
  long maxElapsed;
  long minElapsed;
  double meanElapsed;
  double elapsedVariance;
  double elapsedStandardDeviation;

  public int getTotal() {
    return total;
  }

  public void setTotal(int total) {
    this.total = total;
  }

  public int getSuccess() {
    return success;
  }

  public void setSuccess(int success) {
    this.success = success;
  }

  public int getFailed() {
    return failed;
  }

  public void setFailed(int failed) {
    this.failed = failed;
  }

  public long getMaxElapsed() {
    return maxElapsed;
  }

  public void setMaxElapsed(long maxElapsed) {
    this.maxElapsed = maxElapsed;
  }

  public long getMinElapsed() {
    return minElapsed;
  }

  public void setMinElapsed(long minElapsed) {
    this.minElapsed = minElapsed;
  }

  public double getMeanElapsed() {
    return meanElapsed;
  }

  public void setMeanElapsed(double meanElapsed) {
    this.meanElapsed = meanElapsed;
  }

  public double getElapsedVariance() {
    return elapsedVariance;
  }

  public void setElapsedVariance(double elapsedVariance) {
    this.elapsedVariance = elapsedVariance;
  }

  public double getElapsedStandardDeviation() {
    return elapsedStandardDeviation;
  }

  public void setElapsedStandardDeviation(double elapsedStandardDeviation) {
    this.elapsedStandardDeviation = elapsedStandardDeviation;
  }



}
