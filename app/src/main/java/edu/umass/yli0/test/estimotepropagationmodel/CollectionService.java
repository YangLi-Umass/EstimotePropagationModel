package edu.umass.yli0.test.estimotepropagationmodel;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class CollectionService extends Service {
    public CollectionService() {
    }

    public static final String BROADCAST_FINISH_SIGNAL = "BROADCAST_FINISH_SIGNAL";


    Set <Integer> beaconMinorsSet;
    int collectionTimes;
    String distance;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    private static final Region ALL_ESTIMOTE_BEACONS = new Region("regionId", UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"), null, null);
    private static final String BeaconServiceLog = "BeaconServer";
    private BeaconManager beaconManager;

    StringBuilder text = new StringBuilder();
    WriteDataToExternal writeDataToExternal;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        beaconManager = new BeaconManager(this);

        Bundle bundle = intent.getExtras();
        distance = bundle.getString(MainActivity.INTENT_SERVICE_DISTANCE);
        final String beaconMinor = bundle.getString(MainActivity.INTENT_SERVICE_MINOR_GROUP);
        collectionTimes = Integer.parseInt(bundle.getString(MainActivity.INTENT_SERVICE_COLLECTION_TIMES));

        text.append(dateFormat.format(new Date()) + "\n" + "Minor ID, RSSI, Distance" + "\n");

        beaconMinorsSet = convertStringToSetForBeaconMinors(beaconMinor);
        File file = Environment.getExternalStorageDirectory();
        writeDataToExternal = new WriteDataToExternal("PropagationModel", distance + ".csv", file);
        writeDataToExternal.open();

        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> beacons) {

                Log.d(BeaconServiceLog, "Ranged beacons: " + beacons.size());

                if (beacons.size() > 0 && collectionTimes > 0) {
                    collectionTimes--;
                    for(Beacon beacon : beacons){
                        if(beaconMinorsSet.contains(new Integer(beacon.getMinor()))){
                            Log.d("Debug", beacon.getMinor() + ";" + beacon.getRssi());
                            text.append(beacon.getMinor() + "," + beacon.getRssi() + "," + distance + "\n");
                        }
                    }
                }else if(collectionTimes == 0){
                    writeDataToExternal.write(text.toString());
                    writeDataToExternal.close();
                    Intent intent = new Intent(BROADCAST_FINISH_SIGNAL);
                    sendBroadcast(intent);
                    stopSelf();
                }
            }
        });
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {

                beaconManager.startRanging(ALL_ESTIMOTE_BEACONS);

            }
        });

        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    Set<Integer> convertStringToSetForBeaconMinors(String string) {
        String[] minor = string.split(",");
        Set<Integer> minorSet = new HashSet<>();
        for (String str : minor) {
            minorSet.add(Integer.parseInt(str));
        }
        return minorSet;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        beaconManager.stopRanging(ALL_ESTIMOTE_BEACONS);
    }

}
