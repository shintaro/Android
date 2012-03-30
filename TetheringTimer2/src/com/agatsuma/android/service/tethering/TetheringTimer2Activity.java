package com.agatsuma.android.service.tethering;

import java.lang.reflect.Method;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TimePicker;
import android.widget.Toast;

public class TetheringTimer2Activity extends Activity {

	private TimePicker timePickerOn, timePickerOff;
	private Button button;
	private CheckBox ch;
	private static long onCycle, offCycle;
	private static boolean isTimerOn;
	private static boolean isTetheringOn;
	private AlarmManager am1, am2;
	private Intent intent;
	private PendingIntent sender;
	
	public static class TetheringTimerReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i("*****************", "Received", null);
			WifiManager mWM = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
			WifiConfiguration config = new WifiConfiguration();
			
			if (isTetheringOn) {
				Toast.makeText(context, "Tethering turns OFF", Toast.LENGTH_SHORT).show();
				try {
					mWM.setWifiEnabled(false);
					Method method = mWM.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
					method.invoke(mWM, config, false);
				} catch (Exception e) {
					Log.e("*********************", "Failed", e);
				}
			}
			else {
				Toast.makeText(context, "Tethering turns ON", Toast.LENGTH_SHORT).show();
				try {
					mWM.setWifiEnabled(false);
					Method method = mWM.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
					method.invoke(mWM, config, true);
				} catch (Exception e) {
					Log.e("*************************", "Failed", e);
				}
			}
			isTetheringOn = !isTetheringOn;
		}
	}
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

		am1 = (AlarmManager)getSystemService(ALARM_SERVICE);
		am2 = (AlarmManager)getSystemService(ALARM_SERVICE);
        intent = new Intent(TetheringTimer2Activity.this, TetheringTimerReceiver.class);
		sender = PendingIntent.getBroadcast(TetheringTimer2Activity.this, 0, intent, 0);
		isTimerOn = false;
		
        timePickerOn = (TimePicker)findViewById(R.id.timePicker1);
        timePickerOn.setIs24HourView(true);
        timePickerOn.setCurrentHour(0);
        timePickerOn.setCurrentMinute(30);
        
        timePickerOff = (TimePicker)findViewById(R.id.timePicker2);
        timePickerOff.setIs24HourView(true);
        timePickerOff.setCurrentHour(0);
        timePickerOff.setCurrentMinute(30);
        
        ch = (CheckBox)findViewById(R.id.checkBox);
        
        button = (Button)findViewById(R.id.button1);
        button.setOnClickListener(new View.OnClickListener() {        	
            @Override
			public void onClick(View view) {
            	if (isTimerOn) {
            		//Cancel Timer
            		am1.cancel(sender);
            		am2.cancel(sender);
            		button.setText("Start");
            		isTimerOn = false;
    				Toast.makeText(getBaseContext(), "Timer Cancelled", Toast.LENGTH_SHORT).show();

        			Log.i("*****************", "Canceled", null);

            	} else {
                    onCycle = timePickerOn.getCurrentHour() * 60 + timePickerOn.getCurrentMinute() * 1000;
                    offCycle = timePickerOff.getCurrentHour() * 60 + timePickerOff.getCurrentMinute() * 1000;
                    isTimerOn = true;
                    isTetheringOn = ch.isChecked();
                    
        			WifiManager mWM = (WifiManager) getBaseContext().getSystemService(Context.WIFI_SERVICE);
        			WifiConfiguration config = new WifiConfiguration();
        			
        			if (isTetheringOn) {
        				Toast.makeText(getBaseContext(), "Tethering turns ON", Toast.LENGTH_SHORT).show();
        				try {
        					mWM.setWifiEnabled(false);
        					Method method = mWM.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
        					method.invoke(mWM, config, true);
        				} catch (Exception e) {
        					Log.e("*********************", "Failed", e);
        				}
        			}
        			else {
        				Toast.makeText(getBaseContext(), "Tethering turns OFF", Toast.LENGTH_SHORT).show();
        				try {
        					mWM.setWifiEnabled(false);
        					Method method = mWM.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
        					method.invoke(mWM, config, false);
        				} catch (Exception e) {
        					Log.e("*************************", "Failed", e);
        				}
        			}
                    
            		am1.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + onCycle , onCycle + offCycle, sender);
            		am2.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + onCycle + offCycle , onCycle + offCycle, sender);

            		button.setText("Stop");
            	}
            }
        });
    }
    
    @Override
    public void onResume() {
    	super.onResume();

    	if (isTimerOn) {
    		button.setText("Stop");
        }
        else {
        	button.setText("Start");
        }
    }
    
    @Override 
    public void onDestroy() {
    	super.onDestroy();
		am1.cancel(sender);
		am2.cancel(sender);
    }
}

/*
//make the object
Object wimaxManager = (Object)getBaseContext().getSystemService("wimax");

//declare the method
Method setWimaxEnabled = wimaxManager.getClass().getMethod("setWimaxEnabled", new Class[] { Boolean.TYPE });

//turn it on
setWimaxEnabled.invoke(wimaxManager, new Object[] { Boolean.TRUE });

//turn it off
setWimaxEnabled.invoke(wimaxManager, new Object[] { Boolean.FALSE });
*/