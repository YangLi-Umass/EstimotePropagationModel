package edu.umass.yli0.test.estimotepropagationmodel;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    public static final String INTENT_SERVICE_DISTANCE = "IntentServiceDistance";
    public static final String INTENT_SERVICE_MINOR_GROUP = "IntentServiceMinorGroup";
    public static final String INTENT_SERVICE_COLLECTION_TIMES = "IntentServiceCollectionTimes";
    Intent intentService;

    EditText editTextDistance;
    EditText editTextMinorGroup;
    EditText editTextCollectionTimes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        editTextDistance = (EditText)findViewById(R.id.distance);
        editTextMinorGroup = (EditText)findViewById(R.id.beacons_minor);
        editTextCollectionTimes = (EditText)findViewById(R.id.collection_times);

        IntentFilter positionReceiverFilter;
        positionReceiverFilter = new IntentFilter(CollectionService.BROADCAST_FINISH_SIGNAL);
        PositionReceiver positionReceiver = new PositionReceiver();
        registerReceiver(positionReceiver, positionReceiverFilter);

    }

    public void onClickStartButton(View v) {
        // Perform action on click
        if(!editTextDistance.getText().toString().equals("")
                && !editTextMinorGroup.getText().toString().equals("")
                && !editTextCollectionTimes.getText().toString().equals("")){

            intentService = new Intent(getApplicationContext(), CollectionService.class);
            Bundle bundle = new Bundle();
            bundle.putString(INTENT_SERVICE_DISTANCE, editTextDistance.getText().toString());
            bundle.putString(INTENT_SERVICE_MINOR_GROUP, editTextMinorGroup.getText().toString());
            bundle.putString(INTENT_SERVICE_COLLECTION_TIMES, editTextCollectionTimes.getText().toString());

            intentService.putExtras(bundle);
            startService(intentService);

        }else {
            Toast.makeText(MainActivity.this, "Please Check All Fields.", Toast.LENGTH_SHORT).show();
        }

    }

    public void onClickStopButton(View v) {
        // Perform action on click
        stopService(intentService);
    }

    public class PositionReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            editTextDistance.setText("Finish Collection.");
        }
    }

}
