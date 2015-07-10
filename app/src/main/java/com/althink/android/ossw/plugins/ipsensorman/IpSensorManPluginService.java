package com.althink.android.ossw.plugins.ipsensorman;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.preference.PreferenceManager;
import android.util.Log;

import com.iforpowell.android.ipantmanapi.IpAntManApi;

import java.math.BigDecimal;

/**
 * Created by krzysiek on 10/06/15.
 */
public class IpSensorManPluginService extends Service {

    private final static String TAG = IpSensorManPluginService.class.getSimpleName();

    private final Messenger mMessenger = new Messenger(new OperationHandler());

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            //Log.i(TAG, "Action: " + intent.getAction());

            if (IpAntManApi.HR_EVENT.equals(intent.getAction())) {
                int value = intent.getIntExtra(IpAntManApi.AMOUNT, 0);
                //Log.i(TAG, "HR: " + value);

                ContentValues values = new ContentValues();
                values.put(IpSensorManPluginProperty.HEART_RATE.getName(), value);
                getContentResolver().update(IpSensorManPluginContentProvider.PROPERTY_VALUES_URI, values, null, null);
            } else if (IpAntManApi.BIKE_SPEED_EVENT.equals(intent.getAction())) {
                int count = intent.getIntExtra(IpAntManApi.COUNT, 0);
                int time = intent.getIntExtra(IpAntManApi.TIME, 0);
                //Log.i(TAG, "Speed event, count: " + count + ", time: " + time);


                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                String speedUnit = sharedPref.getString(IpSensorManPluginSettingsActivity.CYCLING_SPEED_UNIT_PARAM_VALUE, getString(R.string.defaultCyclingSpeedUnit));
                float circumference = Integer.parseInt(sharedPref.getString(IpSensorManPluginSettingsActivity.CYCLING_WHEEL_CIRCUMFERENCE_PARAM_VALUE, getString(R.string.defaultCyclingCircumference))) / 1000f;

                float multiplier = 1;
                if ("km/h".equals(speedUnit)) {
                    multiplier = 3.6f;
                } else if ("mph".equals(speedUnit)) {
                    multiplier = 2.23694f;
                }

                float speed = (count * circumference / (time / 1024.f) * multiplier);
                //Log.i(TAG, "Speed: " + speed);

                ContentValues values = new ContentValues();
                values.put(IpSensorManPluginProperty.CYCLING_SPEED.getName(), speed);
                getContentResolver().update(IpSensorManPluginContentProvider.PROPERTY_VALUES_URI, values, null, null);
            } else if (IpAntManApi.BIKE_CADENCE_EVENT.equals(intent.getAction())) {
                int count = intent.getIntExtra(IpAntManApi.COUNT, 0);
                int time = intent.getIntExtra(IpAntManApi.TIME, 0);
                //Log.i(TAG, "Cadence event, count: " + count + ", time: " + time);

                float cadence = ((count) / (time / 1024.f) * 60);
                //Log.i(TAG, "Cadence: " + cadence);

                ContentValues values = new ContentValues();
                values.put(IpSensorManPluginProperty.CYCLING_CADENCE.getName(), cadence);
                getContentResolver().update(IpSensorManPluginContentProvider.PROPERTY_VALUES_URI, values, null, null);
            }
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        //Log.d(TAG, "onBind");
        registerWithIpSensorMan();
        return mMessenger.getBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind");
        unregisterReceiver(mReceiver);
        sendIpSensorManAction(IpAntManApi.UNREGISTER_ANT_ACTION);
        return super.onUnbind(intent);
    }

    private void registerWithIpSensorMan() {
        sendIpSensorManAction(IpAntManApi.REGISTER_ANT_ACTION);

        Intent hr_intent = new Intent(IpAntManApi.START_SENSOR_TYPE_ACTION);
        hr_intent.setClassName("com.iforpowell.android.ipantman", "com.iforpowell.android.ipantman.MainService");
        hr_intent.putExtra(IpAntManApi.DEVICE_TYPE, IpAntManApi.DEVICE_TYPE_HR);
        hr_intent.putExtra(IpAntManApi.DEVICE_ID, IpAntManApi.KNOWN_SENSORS);
        startService(hr_intent);

        Intent csc_intent = new Intent(IpAntManApi.START_SENSOR_TYPE_ACTION);
        csc_intent.setClassName("com.iforpowell.android.ipantman", "com.iforpowell.android.ipantman.MainService");
        csc_intent.putExtra(IpAntManApi.DEVICE_TYPE, IpAntManApi.DEVICE_TYPE_BIKE_SPDCAD);
        csc_intent.putExtra(IpAntManApi.DEVICE_ID, IpAntManApi.KNOWN_SENSORS);
        startService(csc_intent);

        IntentFilter filter = new IntentFilter();
        filter.addAction(IpAntManApi.NEW_SENSOR_EVENT);
        filter.addAction(IpAntManApi.HR_EVENT);
        filter.addAction(IpAntManApi.BIKE_SPEED_EVENT);
        filter.addAction(IpAntManApi.BIKE_CADENCE_EVENT);

        registerReceiver(mReceiver, filter);
    }

    private void sendIpSensorManAction(String action) {
        Intent intent;
        intent = new Intent(action);
        intent.setClassName("com.iforpowell.android.ipantman", "com.iforpowell.android.ipantman.MainService");
        intent.putExtra(IpAntManApi.NAME, getString(R.string.app_name));
        ComponentName comp = startService(intent);
    }

    private class OperationHandler extends Handler {

        public OperationHandler() {
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    }
}