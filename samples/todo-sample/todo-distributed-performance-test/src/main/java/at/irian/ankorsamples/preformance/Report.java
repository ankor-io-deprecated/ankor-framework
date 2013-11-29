package at.irian.ankorsamples.preformance;

public class Report {
    private String type = Report.class.getSimpleName();
    private double avg;
    private double std;
    private int failures;
    private int[] responseTimes;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getAvg() {
        return avg;
    }

    public void setAvg(double avg) {
        this.avg = avg;
    }

    public double getStd() {
        return std;
    }

    public void setStd(double std) {
        this.std = std;
    }

    public int getFailures() {
        return failures;
    }

    public void setFailures(int failures) {
        this.failures = failures;
    }

    public int[] getResponseTimes() {
        return responseTimes;
    }

    public void setResponseTimes(int[] responseTimes) {
        this.responseTimes = responseTimes;
    }
}

