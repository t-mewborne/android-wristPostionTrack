package com.example.bluetoothwristpostiontracker;

import android.app.Activity;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.bluetoothwristpostiontracker.databinding.ActivityMainBinding;

import java.util.ArrayList;

public class MainActivity extends Activity {

    private TextView txtBluetoothInfo;
    private TableLayout tblBluetoothData;
    private ActivityMainBinding binding;
    private MyBluetoothManager btMan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        txtBluetoothInfo = binding.txtBluetoothInfo;
        tblBluetoothData = binding.tblBluetoothData;

        btMan = new MyBluetoothManager(this,this);
        updateTable();
    }


    public void updateTable(){
        ArrayList<BluetoothDevice> devices = btMan.getConnectedDevices();
        for (BluetoothDevice device:devices){
            TableRow row = new TableRow(this);
            TextView txtName = new TextView(this);
            TextView textRSSI = new TextView(this);
            txtName.setText(device.getName());
            //textRSSI.setText(intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,Short.MIN_VALUE));
            row.addView(txtName);
            row.addView(textRSSI);
            tblBluetoothData.addView(row);
        }
        if (devices.size()!=0) txtBluetoothInfo.setText("Connected Devices");
        else txtBluetoothInfo.setText("No Connected Devices\nPlease connect 2+ devices");
    }
}