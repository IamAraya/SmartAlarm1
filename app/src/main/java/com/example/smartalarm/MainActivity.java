package com.example.smartalarm;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    TimePicker alarm_timepicker;
    AlarmManager alarm_manager;
    TextView update_text;
    Context context;
    PendingIntent pending_intent;
    Calendar calendar;

    Button btnCon,btnReady;

    private static final int REQ_ACT = 1;
    private static final int REQ_CONNECT = 2;
    private  static  String MAC = null;

    ConnectedThread connectedThread;

    BluetoothAdapter mBluetoothAdapter = null;
    BluetoothDevice mBluetoothDevice = null;
    BluetoothSocket mBluetoothSocket = null;

    boolean buttonCon = false;

    UUID my_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.context = this;

        // intialize our alarm manager
        alarm_manager = (AlarmManager)getSystemService(ALARM_SERVICE);

        /// intialize our timepicker
        alarm_timepicker = (TimePicker)findViewById(R.id.timepicker);

        //intialize our text updats Box
        update_text = (TextView)findViewById(R.id.update_text);

        //create an instance of a calendar
        final Calendar calendar = Calendar.getInstance();

        //create an intent to the alarm Receiver class
        final Intent my_intent = new Intent(this.context, Alarm_Receiver.class);


        Button alarm_on = (Button)findViewById(R.id.AlarmOn);
        alarm_on.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                calendar.set(Calendar.HOUR_OF_DAY,alarm_timepicker.getCurrentHour());
                calendar.set(Calendar.MINUTE,alarm_timepicker.getCurrentMinute());

                int hour = alarm_timepicker.getCurrentHour();
                int minute = alarm_timepicker.getCurrentMinute();

                String string_hour = String.valueOf(hour);
                String string_min = String.valueOf(minute);

                if(hour > 12)
                {
                    string_hour = String.valueOf(hour-12);
                }
                if(minute < 10)
                {
                    string_min = "0"+String.valueOf(minute);
                }
                my_intent.putExtra("extra","alarm on");
                pending_intent = PendingIntent.getBroadcast(
                        MainActivity.this,0,my_intent,PendingIntent.FLAG_UPDATE_CURRENT);
                alarm_manager.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pending_intent);
                set_alarm_text("Alarm "+ string_hour + " : "+string_min);
            }
        });

        Button alarm_off = (Button)findViewById(R.id.AlarmOff);
        alarm_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                set_alarm_text("Alarm Off!");
                alarm_manager.cancel(pending_intent);
                my_intent.putExtra("extra","off");
                sendBroadcast(my_intent);
            }
        });


        btnCon = (Button)findViewById(R.id.btnCon);
        btnReady = (Button)findViewById(R.id.btnReady);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter == null){
            Toast.makeText(getApplicationContext(), "Device doesn't support Bluetooth", Toast.LENGTH_SHORT).show();
        }else if(!mBluetoothAdapter.isEnabled()){
            Intent active_Blue = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(active_Blue , REQ_ACT);
        }

        btnCon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(buttonCon){
                    //disconnect
                    try {
                        mBluetoothSocket.close();
                        buttonCon = false;
                        btnCon.setText("Connect");
                        Toast.makeText(getApplicationContext(), "Bluetooth has been disconnected", Toast.LENGTH_SHORT).show();
                    }catch(IOException error){
                        Toast.makeText(getApplicationContext(), "error occurred: " + error, Toast.LENGTH_SHORT).show();
                    }
                }else{
                    //connect
                    Intent openList = new Intent(MainActivity.this,Devicelist.class);
                    startActivityForResult(openList,REQ_CONNECT);
                }
            }
        });

        btnReady.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(buttonCon){
                    connectedThread.setSendData("led1");

                }else{
                    Toast.makeText(MainActivity.this, "Bluetooth is disconnected", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void set_alarm_text(String output) {
        update_text.setText(output);
    }

    @Override  // check a BT is active
    protected void onActivityResult(int requestCode, int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){

            case  REQ_ACT :
                if(resultCode == Activity.RESULT_OK){
                    Toast.makeText(getApplicationContext(), "0 Bluetooth was Activate", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(), "0 Bluetooth wasn't Activate, 0 app will be closed", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;

            case REQ_CONNECT :
                if (resultCode == Activity.RESULT_OK){
                    MAC = data.getExtras().getString(Devicelist.AddrMAC);
                    //Toast.makeText(getApplicationContext(), "MAC Final : " + MAC, Toast.LENGTH_SHORT).show();

                    mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(MAC);

                    try {
                        mBluetoothSocket = mBluetoothDevice.createInsecureRfcommSocketToServiceRecord(my_UUID);

                        mBluetoothSocket.connect();

                        buttonCon = true;

                        connectedThread = new ConnectedThread(mBluetoothSocket);
                        connectedThread.start();

                        btnCon.setText("Disconnect");

                        Toast.makeText(getApplicationContext(), "connected with : " + MAC, Toast.LENGTH_SHORT).show();
                    }catch (IOException error){
                        buttonCon = false;
                        Toast.makeText(getApplicationContext(), "error occurred: " + error, Toast.LENGTH_SHORT).show();
                    }

                }else{
                    Toast.makeText(getApplicationContext(), "fail get MAC", Toast.LENGTH_SHORT).show();
                }
        }

    }

    private class ConnectedThread extends Thread {

        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private byte[] mmBuffer; // mmBuffer store for the stream

        public ConnectedThread(BluetoothSocket socket) {
            mBluetoothSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams; using temp objects because
            // member streams are final.
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {

            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            mmBuffer = new byte[1024];
            int numBytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs.
         /*   while (true) {
                try {
                    // Read from the InputStream.
                    numBytes = mmInStream.read(mmBuffer);
                    // Send the obtained bytes to the UI activity.
                    //Message readMsg = handler.obtainMessage(
                    //        MessageConstants.MESSAGE_READ, numBytes, -1,
                    //        mmBuffer);
                    //readMsg.sendToTarget();

                }catch (IOException e) {
                    break;
                }
            }*/

        }

        // Call this from the main activity to send data to the remote device.
        public void setSendData(String sendData) {
            byte[] magBuffer = sendData.getBytes();
            try {
                mmOutStream.write(magBuffer);

            } catch (IOException e) {

            }
        }

        // Call this method from the main activity to shut down the connection.

    }
}