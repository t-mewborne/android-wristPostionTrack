package com.example.bluetoothwristpostiontracker;

import android.util.Log;

import java.util.Date;

public class DataRow {
    private String timeStamp, name;
    private int signalStrength;
    public DataRow(Date timestamp, String name, int signalStrength) {
        this.timeStamp=timestamp.toString().substring(4,19);
        this.name = name;
        this.signalStrength=signalStrength;
        //Log.d("DataRow","row added -- " + timeStamp + "   " + name + "   " + signalStrength);
    }
    public String getName() {return name;}
    public String getTimeStamp() {return timeStamp;}
    public int getSignalStrength() {return signalStrength;}
}
