package at.irian.ankorsamples.performance;

public class OverallReport {
    private String type = OverallReport.class.getSimpleName();
    private int avg;
    private int mad;
    private int std;
    private int failures;
    private int numClients;
    private int numSimulatedClients;
    private int quartile90;
    private int max;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getFailures() {
        return failures;
    }

    public void setFailures(int failures) {
        this.failures = failures;
    }

    public int getNumClients() {
        return numClients;
    }

    public void setNumClients(int numClients) {
        this.numClients = numClients;
    }

    public int getNumSimulatedClients() {
        return numSimulatedClients;
    }

    public void setNumSimulatedClients(int numSimulatedClients) {
        this.numSimulatedClients = numSimulatedClients;
    }

    public int getQuartile90() {
        return quartile90;
    }

    public void setQuartile90(int quartile90) {
        this.quartile90 = quartile90;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getMad() {
        return mad;
    }

    public void setMad(int mad) {
        this.mad = mad;
    }

    public int getStd() {
        return std;
    }

    public void setStd(int std) {
        this.std = std;
    }

    public int getAvg() {
        return avg;
    }

    public void setAvg(int avg) {
        this.avg = avg;
    }
}

