package com.agatsuma.android.service;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

public class TetheringTimerService extends Service {

	class TetheringTimerBinder extends Binder {
		TetheringTimerService getService() {
			return TetheringTimerService.this;
		}
	}

    public static final String ACTION_ON = "Tethering Timer Service";
    public static final String ACTION_OFF = "Tethering Timer Service";
	private boolean isTetheringOn = false;
	private long onCycle, offCycle;
	private Timer timer;
	
	@Override
	public void onCreate() {
		super.onCreate();
		Toast toast = Toast.makeText(getApplicationContext(), "onCreate()", Toast.LENGTH_SHORT);
		toast.show();
		
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		Toast toast = Toast.makeText(getApplicationContext(), "onStart()", Toast.LENGTH_SHORT);
		toast.show();
	}
	
	@Override
	public void onDestroy() {
		Toast toast = Toast.makeText(getApplicationContext(), "onDestory()", Toast.LENGTH_SHORT);
		toast.show();
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		Toast toast = Toast.makeText(getApplicationContext(), "onBind()", Toast.LENGTH_SHORT);
		toast.show();
		return new TetheringTimerBinder();
	}
	
	@Override
	public void onRebind(Intent intent) {
		Toast toast = Toast.makeText(getApplicationContext(), "onRebind()", Toast.LENGTH_SHORT);
		toast.show();
	}

	@Override
	public boolean onUnbind(Intent intent) {
		Toast toast = Toast.makeText(getApplicationContext(), "onUnbind()", Toast.LENGTH_SHORT);
		toast.show();
		return true;
	}

	public void schedule(long delay1, long delay2) {
		if (timer != null) {
			timer.cancel();
		}
		
		onCycle = delay1;
		offCycle = delay2;
		
		timer = new Timer();
		if (isTetheringOn) {
			TimerTask timerTask = new TimerTask() {
				@Override
				public void run() {
					sendBroadcast(new Intent(ACTION_OFF));
					isTetheringOn = !isTetheringOn;
					schedule(onCycle, offCycle);
				}
			};
			timer.schedule(timerTask, onCycle);		
		} else {
			TimerTask timerTask = new TimerTask() {
				@Override
				public void run() {
					sendBroadcast(new Intent(ACTION_ON));
					isTetheringOn = !isTetheringOn;
					schedule(onCycle, offCycle);
				}
			};
			timer.schedule(timerTask, offCycle);
		}			
	}
	
	public boolean isTetheringOn() {
		return isTetheringOn;
	}
	
	public boolean isTimerRunning() {
		if (timer != null)
			return true;
		else
			return false;
	}
	
	public void stopTimer() {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}
}
