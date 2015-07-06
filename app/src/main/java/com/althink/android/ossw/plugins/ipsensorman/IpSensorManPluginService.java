package com.althink.android.ossw.plugins.ipsensorman;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
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

            Log.i(TAG, "Action: " + intent.getAction());

            if (IpAntManApi.HR_EVENT.equals(intent.getAction())) {
                int value = intent.getIntExtra(IpAntManApi.AMOUNT, 0);
                Log.i(TAG, "HR: " + value);

                ContentValues values = new ContentValues();
                values.put(IpSensorManPluginProperty.HEART_RATE.getName(), value);
                getContentResolver().update(IpSensorManPluginContentProvider.PROPERTY_VALUES_URI, values, null, null);
            } else if (IpAntManApi.BIKE_SPEED_EVENT.equals(intent.getAction())) {
                int count = intent.getIntExtra(IpAntManApi.COUNT, 0);
                int time = intent.getIntExtra(IpAntManApi.TIME, 0);
                Log.i(TAG, "Speed event, count: " + count + ", time: " + time);

                float speed = ((count) * 2.149f / (time / 1024.f) * 3.6f);
                Log.i(TAG, "Speed: " + speed);

                ContentValues values = new ContentValues();
                values.put(IpSensorManPluginProperty.CYCLING_SPEED.getName(), speed);
                getContentResolver().update(IpSensorManPluginContentProvider.PROPERTY_VALUES_URI, values, null, null);
            } else if (IpAntManApi.BIKE_CADENCE_EVENT.equals(intent.getAction())) {
                int count = intent.getIntExtra(IpAntManApi.COUNT, 0);
                int time = intent.getIntExtra(IpAntManApi.TIME, 0);
                Log.i(TAG, "Cadence event, count: " + count + ", time: " + time);

                float cadence = ((count) / (time / 1024.f) * 60);
                Log.i(TAG, "Cadence: " + cadence);

                ContentValues values = new ContentValues();
                values.put(IpSensorManPluginProperty.CYCLING_CADENCE.getName(), cadence);
                getContentResolver().update(IpSensorManPluginContentProvider.PROPERTY_VALUES_URI, values, null, null);
            }
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        registerWithIpSensorMan();
        return mMessenger.getBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind");
        unregisterReceiver(mReceiver);
        return super.onUnbind(intent);
    }

    private void registerWithIpSensorMan() {
        Intent intent;
        intent = new Intent(IpAntManApi.REGISTER_ANT_ACTION);
        intent.setClassName("com.iforpowell.android.ipantman", "com.iforpowell.android.ipantman.MainService");
        intent.putExtra(IpAntManApi.NAME, getString(R.string.app_name));
        ComponentName comp = startService(intent);

        Intent hr_intent = new Intent(IpAntManApi.START_SENSOR_TYPE_ACTION);
        hr_intent.setClassName("com.iforpowell.android.ipantman", "com.iforpowell.android.ipantman.MainService");
        hr_intent.putExtra(IpAntManApi.DEVICE_TYPE, IpAntManApi.DEVICE_TYPE_HR);
        hr_intent.putExtra(IpAntManApi.DEVICE_ID, IpAntManApi.KNOWN_SENSORS);
        startService(hr_intent);

        IntentFilter filter = new IntentFilter();
        filter.addAction(IpAntManApi.NEW_SENSOR_EVENT);
        filter.addAction(IpAntManApi.HR_EVENT);
        filter.addAction(IpAntManApi.BIKE_SPEED_EVENT);
        filter.addAction(IpAntManApi.BIKE_CADENCE_EVENT);

        registerReceiver(mReceiver, filter);
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