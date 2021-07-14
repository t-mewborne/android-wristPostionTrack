package com.example.bluetoothwristpostiontracker;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
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
    private boolean writingFile;
    private File file;

    public MyBluetoothDeviceManager(MainActivity main, Context context) {
        this.main = main;
        this.context=context;
        deviceList = new ArrayList<MyBluetoothDevice>();
        creationTime = Calendar.getInstance().getTime();
        String dateToName = creationTime.toString().replaceAll(" ","_").replaceAll(":",".");
        dateToName = dateToName.substring(4,11) + "At_" + dateToName.substring(11,23);
        filename = "TrackingData_" + dateToName + ".csv";
        Log.d(debugTag,"MyBluetoothDeviceManager -- new file will be named \"" + filename+"\"");
        File path = context.getFilesDir();
        file = new File(path,filename); //context.getDir(filename,main.MODE_APPEND);
        writingFile = false;
        table = new ArrayList<DataRow>();
        table.add(new DataRow("time","device_name","signal_strength"));
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

    public void forgetAll() {
        table.clear();
    }

    public void updateData(MyBluetoothDevice device) {
        table.add(new DataRow(device.getLastUpdateTime(),device.getName(),device.getRSSI()));
    }

    /*To access the created file:
     * Android Studio > View > Tool Windows > Device File Explorer
     * Files are saved at: data/data/com.example.bluetoothwristpositiontracker/files
     * Right click on the app folder and click "synchronize" to update
     * Right click and select "save as" to download to computer
     */
    public void updateFile() {
        if (writingFile || table.isEmpty()){
            Log.i(debugTag, "updateFile -- cannot write file for the following reasons: " +
                    (writingFile ? "\nalready writing to a file" : "") +
                    (table.isEmpty() ? "\nthere is no data available to write to the file" : ""));
            return;
        }

        Log.d(debugTag,"updateFile -- attempting to write data to file \""+filename+"\"...");
        writingFile=true;
        try {
            int dataCount = table.size();

            /*
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(filename, context.MODE_PRIVATE));
            for (DataRow data:table) outputStreamWriter.append(data.getRow()+'\n');
            outputStreamWriter.close();
            */

            FileWriter writer = new FileWriter(file,true);
            for (DataRow data:table) writer.append(data.getRow() + '\n');
            writer.close();


            table.clear();

            Log.d(debugTag,"updateFile -- file updated with " + dataCount + " data point(s).");
        } catch (IOException e) {
            Log.e(debugTag,"updateFile -- An IO exception occurred:\n"+e);
        } catch (Exception e) {
            Log.e(debugTag,"updateFile -- An exception occurred:\n"+e);
        } finally {
            writingFile=false;
        }
    }

    public ArrayList<MyBluetoothDevice> getDeviceList() {
        return deviceList;
    }
}
