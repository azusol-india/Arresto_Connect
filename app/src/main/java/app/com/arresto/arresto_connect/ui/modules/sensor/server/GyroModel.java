package app.com.arresto.arresto_connect.ui.modules.sensor.server;

public class GyroModel {
    String TimeStamp, Acc_X, Acc_Y, Acc_Z, Gyro_X, Gyro_Y, Gyro_Z, clockWiseCount, antiClockWiseCount;

    public String getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        TimeStamp = timeStamp;
    }

    public String getAcc_X() {
        return Acc_X;
    }

    public void setAcc_X(String acc_X) {
        Acc_X = acc_X;
    }

    public String getAcc_Y() {
        return Acc_Y;
    }

    public void setAcc_Y(String acc_Y) {
        Acc_Y = acc_Y;
    }

    public String getAcc_Z() {
        return Acc_Z;
    }

    public void setAcc_Z(String acc_Z) {
        Acc_Z = acc_Z;
    }

    public String getGyro_X() {
        return Gyro_X;
    }

    public void setGyro_X(String gyro_X) {
        Gyro_X = gyro_X;
    }

    public String getGyro_Y() {
        return Gyro_Y;
    }

    public void setGyro_Y(String gyro_Y) {
        Gyro_Y = gyro_Y;
    }

    public String getGyro_Z() {
        return Gyro_Z;
    }

    public void setGyro_Z(String gyro_Z) {
        Gyro_Z = gyro_Z;
    }

    public String getClockWiseCount() {
        return clockWiseCount;
    }

    public void setClockWiseCount(String clockWiseCount) {
        this.clockWiseCount = clockWiseCount;
    }

    public String getAntiClockWiseCount() {
        return antiClockWiseCount;
    }

    public void setAntiClockWiseCount(String antiClockWiseCount) {
        this.antiClockWiseCount = antiClockWiseCount;
    }
}
