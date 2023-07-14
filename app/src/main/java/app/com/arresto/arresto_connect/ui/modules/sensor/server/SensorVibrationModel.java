package app.com.arresto.arresto_connect.ui.modules.sensor.server;

import java.io.Serializable;

public class SensorVibrationModel implements Serializable {

//         {time:1650713385,x:-271.02,y:378.02,z:-36.97,r:276,p:8,w:306,cl:1,acl:0}
//                   {time:1650713386,uid:2D1C9F214376C11A,alert:A,batt:57}

    String date, vibrationType = "";
    String clockWiseCount, antiClockCount;
    int hook_mode=-1;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getVibrationType() {
        return vibrationType;
    }

    public void setVibrationType(String vibrationType) {
        this.vibrationType = vibrationType;
    }

    public String getClockWiseCount() {
        return clockWiseCount;
    }

    public void setClockWiseCount(String clockWiseCount) {
        this.clockWiseCount = clockWiseCount;
    }

    public String getAntiClockCount() {
        return antiClockCount;
    }

    public void setAntiClockCount(String antiClockCount) {
        this.antiClockCount = antiClockCount;
    }

    public int getHook_mode() {
        return hook_mode;
    }

    public void setHook_mode(int hook_mode) {
        this.hook_mode = hook_mode;
    }
}
