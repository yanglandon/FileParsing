public class Speaker {
    private String name;
    private double timeSpoken = 0;
    private int timesSpoken = 0;
    private double avgSpokenTime = 0;
    public Speaker(String name, double timeSpoken, int timesSpoken, double avgSpokenTime){
        this.name = name;
        this.timeSpoken = timeSpoken;
        this.timesSpoken = timesSpoken;
        this.avgSpokenTime = avgSpokenTime;
    }
    public void setAvgSpokenTime() {
        avgSpokenTime = timeSpoken / timesSpoken;
//to minutes
        avgSpokenTime /= 60;
        timeSpoken /= 60;
    }
    public void incrementTimeSpoken(double increase) {
        timeSpoken += increase;
        timesSpoken++;
    }
    public String getName() {
        return name;
    }
    public double getTimeSpoken() {
        return timeSpoken;
    }
    public int getTimesSpoken() {
        return timesSpoken;
    }
    public double getAvgSpokenTime() {
        return avgSpokenTime;
    }
    @Override
    public String toString() {
        return "Speaker{" +
                "name='" + name + '\'' +
                ", timeSpoken=" + timeSpoken +
                ", timesSpoken=" + timesSpoken +
                ", avgSpokenTime=" + avgSpokenTime +
                '}';
    }
}