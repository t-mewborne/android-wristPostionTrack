package com.example.bluetoothwristpostiontracker;

import android.bluetooth.BluetoothDevice;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;

//This class is in charge of updating the file and managing the list of bluetooth devices
public class BTDeviceDataManager {
    private ArrayList<BTDeviceData> deviceList;
    private String debugTag = "BTDeviceDataManager";
    private MainActivity main;

    public BTDeviceDataManager(MainActivity main) {
        Log.d(debugTag,"Data manager created");
        this.main = main;
        deviceList = new ArrayList<BTDeviceData>();
    }

    public void addOrUpdate(BluetoothDevice device, int rssi) {
        BTDeviceData data = find(device);
        String deviceName = device.getName();
        if (deviceName==null) return;
        if (data==null && deviceName != null){
            Log.d(debugTag,"addIfDoesNotExist -- new device found: \"" + deviceName + "\"\tAddress ["+device.getAddress()+"]\tRSSI: " + rssi);
            data=add(device, rssi);
        } else {//if (data!=null) {
            data.updateDevice(device, rssi,Calendar.getInstance().getTime());
        }
        main.updateTable();
        updateFile(data);
    }

    public BTDeviceData add(BluetoothDevice device, int rssi) {
        BTDeviceData newDevice = new BTDeviceData(device, rssi, Calendar.getInstance().getTime());
        deviceList.add(newDevice);
        return newDevice;
    }

    public BTDeviceData find(BluetoothDevice device) {
        if (device==null) return null;
        String macAddress = device.getAddress();
        for (BTDeviceData data:deviceList) {
            if (data.equals(macAddress)) return data;
        }
        return null;
    }

    //TODO
    public void updateFile(BTDeviceData data) {

    }

    public ArrayList<BTDeviceData> getDeviceList() {
        return deviceList;
    }
}
