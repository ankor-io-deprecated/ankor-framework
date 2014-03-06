package at.irian.ankorsamples.performance;

public class WorkLoad {
    private String type = WorkLoad.class.getSimpleName();
    private int n;
    private int rampUpRate;

    public int getN() {
        return n;
    }

    public void setN(int n) {
        this.n = n;
    }

    public int getRampUpRate() {
        return rampUpRate;
    }

    public void setRampUpRate(int rampUpRate) {
        this.rampUpRate = rampUpRate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}