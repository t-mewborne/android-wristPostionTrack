package com.example.bluetoothwristpostiontracker;

import android.app.Activity;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.bluetoothwristpostiontracker.databinding.ActivityMainBinding;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends Activity {

    private TextView txtBluetoothInfo;
    private TableLayout tblBluetoothData;
    private Button btnStartStop;
    private ActivityMainBinding binding;
    private MyBluetoothManager btMan;
    private String debugTag = "MainActivity";
    private int permissionRequestConstant = 43;
    private boolean  running = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        txtBluetoothInfo = binding.txtBluetoothInfo;
        tblBluetoothData = binding.tblBluetoothData;
        btnStartStop = binding.btnStartStop;

        btnStartStop.setText("Start");


        btnStartStop.setOnClickListener(v->btnStartStopClick());

        btMan = new MyBluetoothManager(this,this,permissionRequestConstant);
        updateTable();
        txtBluetoothInfo.setText("Press Start to Begin");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); //TODO Remove later, keeps screen on for testing

        String existingfiles = "";
        for(String file:fileList()) existingfiles+=("\n"+file);
        Log.i(debugTag," Existing files:"+ existingfiles);
    }

    private void btnStartStopClick() {
        Log.d(debugTag,"btnStartStopClick -- button clicked");
        if (running) {
            btnStartStop.setText("start");
            btMan.userStoppedSearch();
            running=!running;
            updateTable();

        } else if (btMan.isReady()){
            btnStartStop.setText("stop");
            btMan.userStartedSearch();
            running=!running;
            updateTable();
        } else if (!btMan.isReady()) {
            txtBluetoothInfo.setText("Wait...");
        }

    }

    public void searchStopped() {
        txtBluetoothInfo.setText("Search Stopped.");
        tblBluetoothData.removeAllViews();
        btMan.forgetDevices();
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
        } else if (running) {
            txtBluetoothInfo.setText("Searching...");
        } else {
            txtBluetoothInfo.setText("");
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