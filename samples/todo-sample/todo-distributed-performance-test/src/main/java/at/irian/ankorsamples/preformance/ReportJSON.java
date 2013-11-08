package at.irian.ankorsamples.preformance;

class ReportJSON {
    private String type;
    private double avg;
    private double std;
    private int failures;

    String getType() {
        return type;
    }

    void setType(String type) {
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
}

