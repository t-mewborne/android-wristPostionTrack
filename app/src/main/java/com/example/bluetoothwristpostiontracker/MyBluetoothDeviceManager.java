package com.example.bluetoothwristpostiontracker;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

//This class is in charge of updating the file and managing the list of bluetooth devices
public class MyBluetoothDeviceManager {
    private ArrayList<MyBluetoothDevice> deviceList;
    ArrayList<DataRow> table;
    private String debugTag = "BTDeviceDataManager";
    private MainActivity main;
    private Context context;
    private Date creationTime;
    private String filename;

    public MyBluetoothDeviceManager(MainActivity main, Context context) {
        this.main = main;
        this.context=context;
        deviceList = new ArrayList<MyBluetoothDevice>();
        creationTime = Calendar.getInstance().getTime();
        filename = "WT_" + creationTime.toString().replaceAll(" ","_") + ".csv";
        Log.d(debugTag,"BTDeviceDataManager -- file will be named \"" + filename+"\"");

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



    //TODO
    public void updateData(MyBluetoothDevice device) {
        table.add(new DataRow(device.getLastUpdateTime(),device.getName(),device.getRSSI()));
    }

    //TODO, wrtie all the data to a file
    //private static final int CREATE_FILE = 1;
    private void createFile(/*Uri pickerInitialUri*/) {

        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/csv");
        intent.putExtra(Intent.EXTRA_TITLE, filename);

        // Optionally, specify a URI for the directory that should be opened in
        // the system file picker when your app creates the document.
        //intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri);

        context.startActivity(intent);
    }

    public ArrayList<MyBluetoothDevice> getDeviceList() {
        return deviceList;
    }
}
