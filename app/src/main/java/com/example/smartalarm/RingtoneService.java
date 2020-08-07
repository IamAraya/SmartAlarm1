package com.example.smartalarm;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

import java.security.Provider;

public class RingtoneService extends Service {

    MediaPlayer mediaPlayer;
    int id;
    boolean isRunning;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public int onStartCommand(Intent intent,int flags,int startId)
    {
        Log.e("Music","ok");

        String state = intent.getExtras().getString("extra");
        Log.e("LocalService" ,state);

        assert  state != null;

        switch (state)
        {
            case "alarm on":
                id =1;
                break;
            case "alarm off":
                id =0;
                Log.e("Start ID is",state);
                break;
            default:
                id = 0;
                break;
        }

        if(!this.isRunning && id == 1)
        {
            mediaPlayer = MediaPlayer.create(this,R.raw.ending);
            mediaPlayer.start();
            this.isRunning = true;
            this.id = 0;
        }else if(this.isRunning && id == 0)
        {
            mediaPlayer.stop();
            mediaPlayer.reset();
            this.isRunning = false;
            this.id = 0;
        }else if(this.isRunning && id == 0)
        {
            this.isRunning = false;
            this.id =0;
        }else {
            mediaPlayer.stop();
            mediaPlayer.reset();

            this.isRunning = false;
            this.id = 0;
        }


        return START_NOT_STICKY;
    }

}
