package com.advantej.glass.testestimote;

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
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.estimote.sdk.Beacon;

import java.util.List;

/**
 * Created by tejas on 5/6/14.
 */
public class BeaconListActivity extends Activity {

    private GlasstimoteService mService;

    private ListView mListViewBeacons;

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {

            mService = ((GlasstimoteService.MyBinder)binder).getService();

            if (mService != null) {
                setUpBeaconList(mService.getBeacons());
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_beacon_list);

        mListViewBeacons = (ListView) findViewById(R.id.lv_beacons);

        startService(new Intent(this, GlasstimoteService.class));

        registerReceiver(mBroadcastReceiver, new IntentFilter(GlasstimoteService.ACTION_FOUND_BEACONS));
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            bindService(new Intent(BeaconListActivity.this, GlasstimoteService.class), mServiceConnection, BIND_AUTO_CREATE);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
        stopService(new Intent(this, GlasstimoteService.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mService != null) {
            unbindService(mServiceConnection);
        }
    }

    private void setUpBeaconList(List<Beacon> beacons) {

        mListViewBeacons.setAdapter(new BeaconsAdapter(beacons));
    }

    private class BeaconsAdapter extends BaseAdapter {
        private List<Beacon> mBeaconList;

        private BeaconsAdapter(List<Beacon> beaconList) {
            mBeaconList = beaconList;
        }

        @Override
        public int getCount() {
            return mBeaconList == null ? 0 : mBeaconList.size();
        }

        @Override
        public Beacon getItem(int position) {
            return mBeaconList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(BeaconListActivity.this, R.layout.row_beacon, null);
            }

            Beacon beacon = getItem(position);
            ((TextView)convertView.findViewById(R.id.tv_beacon_name)).setText(beacon.getName());
            ((TextView)convertView.findViewById(R.id.tv_beacon_addr)).setText(beacon.getMacAddress());

            return convertView;
        }
    }
}
