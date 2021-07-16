package com.example.bluetoothwristpostiontracker;

import android.util.Log;

import java.util.Date;

public class DataRowBluetooth {
    private String time, name, signalStrength;
    //private long time;
    //Time represented in seconds

    public DataRowBluetooth(long time, String name, int signalStrength) {
        //this.timeStamp=timestamp.toString().substring(4,19).replaceAll(" ","_");
        this.time = ""+time;
        this.name = name.replaceAll(" ","_");
        this.signalStrength=""+signalStrength;
    }

    public DataRowBluetooth(String time, String name, String signalStrength) {
        //this.timeStamp=timestamp.replaceAll(" ","_");
        this.time = time;
        this.name = name.replaceAll(" ","_");
        this.signalStrength=signalStrength;
    }

    public String getName() {return name;}
    public String getTimeStamp() {return ""+time;}
    public String getSignalStrength() {return signalStrength;}
    public String getRow() {return (time+"\t"+name+"\t"+signalStrength);}
}
