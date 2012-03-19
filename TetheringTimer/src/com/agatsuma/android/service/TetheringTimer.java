package com.agatsuma.android.service;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

public class TetheringTimer extends Activity {

	private class TetheringTimerReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String str;
			if (tetheringTimerService.isTetheringOn())
				str = "Tethring On";
			else
				str = "Tethering Off";
			Toast toast = Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT);
			toast.show();
			
		}
	}
	
	private TetheringTimerService tetheringTimerService;
	private final TetheringTimerReceiver receiver = new TetheringTimerReceiver();

	private ServiceConnection serviceConnection =  new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			tetheringTimerService = ((TetheringTimerService.TetheringTimerBinder)service).getService();
		}
		
		@Override
		public void onServiceDisconnected(ComponentName className) {
			tetheringTimerService = null;
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
        final TimePicker timePickerOn = (TimePicker)findViewById(R.id.timePickerOn);
        timePickerOn.setIs24HourView(true);
        timePickerOn.setCurrentHour(0);
        timePickerOn.setCurrentMinute(10);
        
        final TimePicker timePickerOff = (TimePicker)findViewById(R.id.timePickerOff);
        timePickerOff.setIs24HourView(true);
        timePickerOff.setCurrentHour(0);
        timePickerOff.setCurrentMinute(10);

        
        
        final Button button = (Button)findViewById(R.id.button1);
        if (tetheringTimerService == null || tetheringTimerService.isTimerRunning())
        	button.setText("Stop");
        button.setOnClickListener(new View.OnClickListener() {        	
            public void onClick(View view) {
            	if (tetheringTimerService.isTimerRunning()) {
            		tetheringTimerService.stopTimer();
            		button.setText("Start");
            	} else {
                	long hourOn = timePickerOn.getCurrentHour();
                    long minOn = timePickerOn.getCurrentMinute();
                    long hourOff = timePickerOff.getCurrentHour();
                    long minOff = timePickerOff.getCurrentMinute();

                    tetheringTimerService.schedule((hourOn * 60 + minOn) * 1000, (hourOff * 60 + minOff) * 1000);
                    moveTaskToBack(true);
            	}
            }
        });

        Intent intent = new Intent(this, TetheringTimerService.class);
        startService(intent);
        IntentFilter filter1 = new IntentFilter(TetheringTimerService.ACTION_ON);
        IntentFilter filter2 = new IntentFilter(TetheringTimerService.ACTION_OFF);
        registerReceiver(receiver, filter1);
        registerReceiver(receiver, filter2);

        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

        unbindService(serviceConnection);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		unbindService(serviceConnection);
		unregisterReceiver(receiver);
		tetheringTimerService.stopSelf();
	}
}
