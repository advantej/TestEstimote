package com.advantej.glass.testestimote;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;

import java.util.List;

public class GlasstimoteService extends Service {

//    private static final String LIVE_CARD_TAG = "BEACONS_CARD";
    public static final String ACTION_FOUND_BEACONS = "com.advantej.glass.testestimote.ACTION_FOUND_BEACONS";
    private static final String TAG = "GlasstimoteService";

    private BeaconManager mBeaconManager;
    private static final Region ALL_ESTIMOTE_BEACONS_REGION = new Region("rid", null, null, null);
    private List<Beacon> mBeacons = null;

    private final IBinder mBinder = new MyBinder();

    public class MyBinder extends Binder {
        public GlasstimoteService getService() {
            return GlasstimoteService.this;
        }
    }

    public GlasstimoteService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mBeaconManager = new BeaconManager(this);
        mBeaconManager.setRangingListener(mRangingListener);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        publishOrUpdateLiveCard(0);
        checkBTStatusAndStartRanging();
        return START_STICKY;
    }

    private void checkBTStatusAndStartRanging() {

        // Check if device supports Bluetooth Low Energy.
        if (!mBeaconManager.hasBluetooth()) {
            Toast.makeText(this, getString(R.string.error_bluetooth_le_unsupported), Toast.LENGTH_LONG).show();
            return;
        }

        // If Bluetooth is not enabled, let user enable it.
        if (!mBeaconManager.isBluetoothEnabled()) {
            Toast.makeText(this, getString(R.string.error_bluetooth_not_enabled), Toast.LENGTH_LONG).show();
            return;
        } else {
            connectToService();
        }

    }

    private void connectToService() {
        mBeaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                try {
                    mBeaconManager.startRanging(ALL_ESTIMOTE_BEACONS_REGION);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        mBeaconManager.disconnect();
        unpublishLiveCard();
        super.onDestroy();
    }

    private BeaconManager.RangingListener mRangingListener = new BeaconManager.RangingListener() {
        @Override
        public void onBeaconsDiscovered(Region region, List<Beacon> beacons) {

            mBeacons = beacons;

            int count = beacons.size();
            if (count > 0) {
                publishOrUpdateLiveCard(beacons.size());
            } else if (count == 0) {
                unpublishLiveCard();
            }
        }
    };

    private void publishOrUpdateLiveCard(int beaconCount){
        //removed glass related code

        Log.d(TAG, beaconCount + " beacons found");
        //send an event to notify activity
        Intent intent = new Intent();
        intent.setAction(ACTION_FOUND_BEACONS);
        sendBroadcast(intent);
    }

    private void unpublishLiveCard() {
        //removed glass related code
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public List<Beacon> getBeacons() {
        return mBeacons;
    }
}
