package com.example.bluetoothwristpostiontracker;

import android.util.Log;

import java.util.Date;

public class DataRow {
    private String timeStamp, name, signalStrength;

    public DataRow(Date timestamp, String name, int signalStrength) {
        this.timeStamp=timestamp.toString().substring(4,19).replaceAll(" ","_");
        this.name = name.replaceAll(" ","_");
        this.signalStrength=""+signalStrength;
    }

    public DataRow(String timestamp, String name, String signalStrength) {
        this.timeStamp=timestamp.toString().substring(4,19).replaceAll(" ","_");
        this.name = name.replaceAll(" ","_");
        this.signalStrength=signalStrength;
    }

    public String getName() {return name;}
    public String getTimeStamp() {return timeStamp;}
    public String getSignalStrength() {return signalStrength;}
    public String getRow() {return (timeStamp+"\t"+name+"\t"+signalStrength);}
}
