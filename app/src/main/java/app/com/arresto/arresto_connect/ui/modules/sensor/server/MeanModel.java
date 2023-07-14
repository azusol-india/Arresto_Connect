package app.com.arresto.arresto_connect.ui.modules.sensor.server;

public class MeanModel {
    public String getTime_stamp() {
        return time_stamp;
    }

    public void setTime_stamp(String time_stamp) {
        this.time_stamp = time_stamp;
    }

    public double getMean_value() {
        return mean_value;
    }

    public void setMean_value(double mean_value) {
        this.mean_value = mean_value;
    }

    String time_stamp;
    double mean_value;

}
