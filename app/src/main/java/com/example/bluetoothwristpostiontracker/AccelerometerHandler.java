package com.example.bluetoothwristpostiontracker;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Handler;

public class AccelerometerHandler extends Activity implements SensorEventListener {
    private Context context;
    private MainActivity main;
    private Handler handler; //Used to call methods repeatedly
    private int samplingPeriod;  //The interval (in microseconds) in which to collect accelerometer data.
    private SensorManager sensorManager;
    private String debugTag = "AccelerometerHandler";
    private Sensor accelerometer, gyroscope;
    private MyQueue<DataPointAccelerometer> accelerometerData;
    private String filename;
    private File file;
    private boolean writingFile;


    public AccelerometerHandler(Context context, MainActivity main) {
        this.context = context;
        this.main = main;
        sensorManager = (SensorManager) main.getSystemService(context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);//TODO make sure this is actually accelerometer data
        filename = main.getDateForFilename() + "_accelerometer.csv";
        Log.d(debugTag,"AccelerometerHandler -- new file will be named \"" + filename+"\"");
        File path = context.getFilesDir();
        file = new File(path,filename); //context.getDir(filename,main.MODE_APPEND);
        writingFile = false;
        //gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        int samplingPeriod = 5000000; //In microseconds, 1,000,000us = 1 second
        accelerometerData = new MyQueue<DataPointAccelerometer>();
        accelerometerData.enqueue(new DataPointAccelerometer("time(ms)","x","y","z"));
    }

    //Begin Collecting Accelerometer Data
    public void userStartedSearch(){
        sensorManager.registerListener(AccelerometerHandler.this,accelerometer,samplingPeriod);
    }

    //Pause collecting Accelerometer data
    public void userStoppedSearch() {
        sensorManager.unregisterListener(AccelerometerHandler.this);
        updateFile();
    }

    private void updateFile() {
        if (writingFile || accelerometerData.isEmpty()){
            Log.i(debugTag, "updateFile -- cannot write file for the following reasons: " +
                    (writingFile ? "\nalready writing to a file" : "") +
                    (accelerometerData.isEmpty() ? "\nthere is no data available to write to the file.": ""));
            return;
        }

        Log.d(debugTag,"updateFile -- attempting to write data to file \""+filename+"\"...");
        writingFile=true;
        try {
            int dataCount = accelerometerData.getSize();


            FileWriter writer = new FileWriter(file,true);
            while(!accelerometerData.isEmpty()) {
                writer.append(accelerometerData.dequeue().getRow()+'\n');
            }
            writer.close();

            Log.d(debugTag,"updateFile -- file updated with " + dataCount + " data point(s).");
        } catch (IOException e) {
            Log.e(debugTag,"updateFile -- An IO exception occurred:\n"+e);
        } catch (Exception e) {
            Log.e(debugTag,"updateFile -- An exception occurred:\n"+e);
        } finally {
            writingFile=false;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            accelerometerData.enqueue(new DataPointAccelerometer(""+main.millisecondsSinceStart(),event.values[0],event.values[1],event.values[2]));
            if (accelerometerData.getSize()>5000) {
                Log.d(debugTag,"onSensorChanged -- Exceeded max accelerometer queue size. Updating file...");
                updateFile();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
