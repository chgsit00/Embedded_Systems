/*
 *  This file is part of the Multimodal Mobility Analyser(MMA), based
 *  on the Smartphone Sensing Framework (SSF)

    MMA (also SSF) is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    MMA (also SSF) is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with MMA.  If not, see <http://www.gnu.org/licenses/>.
 */

package edu.example.ssf.mma.userInterface;


/**
 * This class provides the Main Activity of the application
 * Its only activ responsibility is the management of the UI.
 * Everything else is a passiv responsibility and is management by typical OOP or callbacks
 *
 * @author D. Lagamtzis
 * @version 2.0
 */

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.sql.Timestamp;
import java.util.Date;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.onsets.OnsetHandler;
import be.tarsos.dsp.onsets.PercussionOnsetDetector;
import edu.example.ssf.mma.R;
import edu.example.ssf.mma.hardwareAdapter.HardwareFactory;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private boolean navigationBool = false;
    private int idOfNavObj;

    // Init HW-Factory
    HardwareFactory hw;

    //Permissions Android
    public static final int PERMISSIONS_MULTIPLE_REQUEST = 123;

    // UI
    private TextView headerTextView;

    //Text View Result
    private String defaultMessage = "Please Choose your Sensor to Display!";


    /**
     * Declaration of the state machine.
     */

    private TextView textViewLogs;
    private Button startTimerButton;
    private Boolean isTrackingTime = false;
    private Date beginnOfTrackingTime = null;


    @SuppressLint("RestrictedApi")
    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //GoogleAccessor.actvityResultHanlder(requestCode, resultCode, data);

    }


    @SuppressLint("RestrictedApi")
    @Override
    protected void onStart() {
        super.onStart();
    }

    private void onTimeDelta(long delta) {
        int seconds = (int) delta / 1000;
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        seconds = (seconds % 3600) % 60;
        String info = "Time tracked - hrs: " + hours + " min: " + minutes + " sec: " + seconds;
        CharSequence text = textViewLogs.getText();
        textViewLogs.setText(text + "\n" + info);
    }

    private void toggleTracking() {
        Boolean iscurrentlyTracking = this.isTrackingTime;
        if (iscurrentlyTracking) {
            startTimerButton.setText("Start Tracking");
            startTimerButton.setBackgroundColor(Color.parseColor("#FF00CD90"));
            Date now = new Date();
            Timestamp timestampEnd = new Timestamp(now.getTime());
            long diff = timestampEnd.getTime() - this.beginnOfTrackingTime.getTime();
            onTimeDelta(diff);
        } else {
            startTimerButton.setText("End Tracking");
            startTimerButton.setBackgroundColor(Color.parseColor("#ff00ff"));

            this.beginnOfTrackingTime = new Date();
        }
        this.isTrackingTime = !iscurrentlyTracking;

    }

    private Date lastClapDetected = null;

    private Boolean setAndCheckForDoubleClap() {
        // init first clap
        if (lastClapDetected == null) {
            lastClapDetected = new Date();
            return false;
        }
        // last clap less then 500 ms in the past
        if (lastClapDetected.getTime() - (new Date()).getTime() < 500) {
            return true;
        }
        // new first clap
        lastClapDetected = new Date();
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

//        GoogleAccessor = new GoogleAccessor(this);
//        GoogleAccessor.signIn();


        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_MULTIPLE_REQUEST);


        textViewLogs = findViewById(R.id.textviewLogs);

        // NavigationView init
        mDrawerLayout = findViewById(R.id.drawerLayout);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        startTimerButton = findViewById(R.id.startButton);
        startTimerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                toggleTracking();
            }
        });

        AudioDispatcher mDispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050, 1024, 0);
        double threshold = 8;
        double sensitivity = 40;
        PercussionOnsetDetector mPercussionDetector = new PercussionOnsetDetector(22050, 1024,
                new OnsetHandler() {

                    @Override
                    public void handleOnset(double time, double salience) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(setAndCheckForDoubleClap()){
                                    toggleTracking();
                                }

                            }
                        });
                        Log.d("clap", "Clap detected!");
                    }
                }, sensitivity, threshold);
        mDispatcher.addAudioProcessor(mPercussionDetector);
        Thread t = new Thread(mDispatcher);
        t.start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_MULTIPLE_REQUEST:
                //
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Hardware
                    //stateMachineHandler = new StateMachineHandler(this);
                    hw = new HardwareFactory(this);
                } else {
                    // Do Nothing
                }
        }
    }

    @Override
    public void onBackPressed() {
        if (this.mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        idOfNavObj = item.getItemId();
        if (navigationBool) {
            navigationBool = false;
        }
        if (idOfNavObj == R.id.nav_mic) {
            headerTextView.setText(R.string.mic);
            navigationBool = true;
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return mToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    public static void actState(String state) {
        //  textViewActState.setText(state);

    }


}
