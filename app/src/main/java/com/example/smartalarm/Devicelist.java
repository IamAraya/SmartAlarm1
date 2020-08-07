package com.example.smartalarm;

import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;

public class Devicelist extends ListActivity {

    private BluetoothAdapter mBluetoothAdap = null;

    static String AddrMAC = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ArrayAdapter<String> ArrayBluetooth = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);

        mBluetoothAdap = BluetoothAdapter.getDefaultAdapter();

        Set<BluetoothDevice> deviceMatch = mBluetoothAdap.getBondedDevices();

        if(deviceMatch.size() > 0){
            for(BluetoothDevice device : deviceMatch){
                String nameBT = device.getName();
                String nameMAC = device.getAddress();
                ArrayBluetooth.add(nameBT + "\n" + nameMAC);
            }
        }

        setListAdapter(ArrayBluetooth);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        String  infoname = ((TextView)v).getText().toString();
        //Toast.makeText(getApplicationContext(), "Info : "+ infoname, Toast.LENGTH_SHORT).show();

        String addrMac = infoname.substring(infoname.length()-17);
        //Toast.makeText(getApplicationContext(), "MAC : "+ addrMac, Toast.LENGTH_SHORT).show();

        Intent ReturnMac = new Intent();
        ReturnMac.putExtra(AddrMAC,addrMac);

        setResult(RESULT_OK,ReturnMac);
        finish();
    }
}
