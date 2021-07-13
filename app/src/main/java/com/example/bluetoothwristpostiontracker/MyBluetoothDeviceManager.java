package com.example.bluetoothwristpostiontracker;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

//This class is in charge of updating the file and managing the list of bluetooth devices
public class MyBluetoothDeviceManager {
    private ArrayList<MyBluetoothDevice> deviceList;
    ArrayList<DataRow> table;
    private String debugTag = "MyBluetoothDeviceManager";
    private MainActivity main;
    private Context context;
    private Date creationTime;
    private String filename;
    private File file;

    public MyBluetoothDeviceManager(MainActivity main, Context context) {
        this.main = main;
        this.context=context;
        deviceList = new ArrayList<MyBluetoothDevice>();
        creationTime = Calendar.getInstance().getTime();
        String dateToName = creationTime.toString().replaceAll(" ","_").replaceAll(":",".");
        dateToName = dateToName.substring(0,11) + "At_" + dateToName.substring(11);
        filename = "TrackingData_" + dateToName + ".csv";
        Log.d(debugTag,"MyBluetoothDeviceManager -- new file will be named \"" + filename+"\"");
        file = main.getDir(filename,main.MODE_APPEND);
        table = new ArrayList<DataRow>();
    }

    public void addOrUpdate(BluetoothDevice device, int rssi) {
        MyBluetoothDevice data = find(device);
        String deviceName = device.getName();
        if (deviceName==null) return;
        //Log.d(debugTag,"device \"" + deviceName + "\" null? " + (deviceName==null));
        if (data==null && deviceName != null){ //If data is null, device does not exist yet
            Log.d(debugTag,"addIfDoesNotExist -- new device found: \"" + deviceName + "\"\tAddress ["+device.getAddress()+"]\tRSSI: " + rssi);
            data=add(device, rssi);
            updateData(data);
        } else if (data!=null) {
            //Log.d(debugTag,"addIfDoesNotExist -- device updated: \"" + deviceName + "\"\tAddress ["+device.getAddress()+"]\tRSSI: " + rssi);
            data.updateDevice(device, rssi,Calendar.getInstance().getTime());
            updateData(data);
        }
        main.updateTable();

    }

    public MyBluetoothDevice add(BluetoothDevice device, int rssi) {
        MyBluetoothDevice newDevice = new MyBluetoothDevice(device, rssi, Calendar.getInstance().getTime());
        deviceList.add(newDevice);
        return newDevice;
    }

    public MyBluetoothDevice find(BluetoothDevice device) {
        if (device==null) return null;
        String macAddress = device.getAddress();
        for (MyBluetoothDevice data:deviceList) {
            if (data.equals(macAddress)) return data;
        }
        return null;
    }

    public void updateData(MyBluetoothDevice device) {
        table.add(new DataRow(device.getLastUpdateTime(),device.getName(),device.getRSSI()));
    }

    //TODO, wrtie all the data to a file
    //private static final int CREATE_FILE = 1;
    public void updateFile() {
        Log.d(debugTag,"updateFile -- attempting to write data to file \""+filename+"\"...");
        try {
            FileOutputStream out = main.openFileOutput(filename,main.MODE_APPEND);
            out.close();
            Log.d(debugTag,"updateFile -- file updated");
            /*
            FileWriter writer = new FileWriter(file);
            for (DataRow data:table) {
                writer.append(data.getRow() + "\n");
            }
            table.clear();
            writer.close();
            Log.d(debugTag,"updateFile -- file updated");*/
        } catch (IOException e) {
            Log.e(debugTag,"updateFile -- An IO exception occurred:\n"+e);
            //e.printStackTrace();
        } catch (Exception e) {
            Log.e(debugTag,"updateFile -- An error occurred:\n"+e);
        }
    }

    public ArrayList<MyBluetoothDevice> getDeviceList() {
        return deviceList;
    }
}
