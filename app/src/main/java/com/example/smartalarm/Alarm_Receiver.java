package com.example.smartalarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class Alarm_Receiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("the receiver","Ok");
        Intent myintent = new Intent(context,RingtoneService.class);
        String get_your_string = intent.getExtras().getString("extra");
        Log.e("What is the key? ",get_your_string);
        myintent.putExtra("extra",get_your_string);
        context.startService(myintent);
    }
}