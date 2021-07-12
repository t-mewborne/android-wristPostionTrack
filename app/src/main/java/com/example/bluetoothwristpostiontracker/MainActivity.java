package com.example.bluetoothwristpostiontracker;

import android.app.Activity;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.bluetoothwristpostiontracker.databinding.ActivityMainBinding;

import java.util.ArrayList;

public class MainActivity extends Activity {

    private TextView txtBluetoothInfo;
    private TableLayout tblBluetoothData;
    private ActivityMainBinding binding;
    private MyBluetoothManager btMan;
    private String debugTag = "MainActivity";
    private int permissionRequestConstant = 43;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        txtBluetoothInfo = binding.txtBluetoothInfo;
        tblBluetoothData = binding.tblBluetoothData;

        btMan = new MyBluetoothManager(this,this,permissionRequestConstant);
        updateTable();
    }


    public void updateTable(){
        //Log.d(debugTag,"updateTable -- function called");
        ArrayList<MyBluetoothDevice> devices = btMan.getNearbyDevices();
        tblBluetoothData.removeAllViews();
        if (devices!=null && devices.size()>0) {
            for (MyBluetoothDevice data : devices) {
                TableRow row = new TableRow(this);
                TextView txtName = new TextView(this);
                TextView textRSSI = new TextView(this);
                txtName.setText(data.getDisplayName());
                textRSSI.setText(""+data.getRSSI());
                //Log.d(debugTag,"updateTable -- rssi " + data.getRSSI());
                row.addView(txtName);
                row.addView(textRSSI);
                tblBluetoothData.addView(row);
            }
            txtBluetoothInfo.setText("Nearby Devices:");
        } else if (!btMan.isSearching()) {
            txtBluetoothInfo.setText("Something went wrong");
        } else {
            txtBluetoothInfo.setText("Searching...");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == permissionRequestConstant) {
            boolean result0 = grantResults[0] == PackageManager.PERMISSION_GRANTED; //ACCESS_FINE_LOCATION
            boolean result1 = grantResults[1] == PackageManager.PERMISSION_GRANTED; //BLUETOOTH

            Log.d(debugTag,"onRequestPermissionsResult -- Fine location permission " + (result0 ? "granted!" : "denied."));
            Log.d(debugTag,"onRequestPermissionsResult -- Bluetooth  permission " + (result1 ? "granted!" : "denied."));

            boolean permissionResult = result0 && result1;
            Log.d(debugTag,"onRequestPermissionsResult -- Permission " + (permissionResult ? "granted!" : "denied."));
            if (btMan != null) btMan.permissionsReady(permissionResult);
            else Log.e(debugTag,"Bluetooth Manager does not exist");
        }
    }
}