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
 * @author D. Lagamtzis
 * @version 2.0
 */
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import edu.example.ssf.mma.R;
import edu.example.ssf.mma.charts.AccChart;
import edu.example.ssf.mma.config.ConfigApp;
import edu.example.ssf.mma.data.CsvFileWriter;
import edu.example.ssf.mma.data.CurrentTickData;
import edu.example.ssf.mma.google.GoogleAccessor;
import edu.example.ssf.mma.hardwareAdapter.HardwareFactory;
import edu.example.ssf.mma.timer.StateMachineHandler;

public class MainActivity extends AppCompatActivity implements  NavigationView.OnNavigationItemSelectedListener{

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    public static boolean mmaCallBackBool = false;
    private boolean navigationBool = false;
    private int idOfNavObj;

    // Init HW-Factory
    HardwareFactory hw;

    //Permissions Android
    public  static final int PERMISSIONS_MULTIPLE_REQUEST = 123;

    // UI
    private TextView headerTextView;
    private TextView textView1;
    private TextView textView2;
    private TextView textView3;
    private TextView textView4;
    private TextView textView5;
    private static TextView textViewActState;
    private ToggleButton recButton;
    private Button fileBrowserButton, showChartButton;

    //Text View Result
    private String defaultMessage = "Please Choose your Sensor to Display!";


    /** Declaration of the state machine. */

    private StateMachineHandler stateMachineHandler;
    static GoogleAccessor GoogleAccessor;




    @SuppressLint("RestrictedApi")
    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        GoogleAccessor.actvityResultHanlder(requestCode, resultCode, data);

    }


    @SuppressLint("RestrictedApi")
    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        GoogleAccessor = new GoogleAccessor(this);
        GoogleAccessor.signIn();


        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(MainActivity.this, new  String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.RECORD_AUDIO,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_MULTIPLE_REQUEST);

        //UI -- Textviews init
        headerTextView = findViewById(R.id.headerTextView);
        headerTextView.setText(defaultMessage);
        textView1 = findViewById(R.id.TextOne);
        textView2 = findViewById(R.id.TextTwo);
        textView3 = findViewById(R.id.TextThree);
        textView4 = findViewById(R.id.TextFour);
        textView5 =  findViewById(R.id.TextFive);
        textViewActState=findViewById(R.id.textViewActState);
        textViewActState.setText("");

        // NavigationView init
        mDrawerLayout = findViewById(R.id.drawerLayout);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout,R.string.open,R.string.close);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //Buttons & Toggle Buttons
        fileBrowserButton = findViewById(R.id.fileexplorer);
        fileBrowserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =  new Intent(MainActivity.this, ListFileActivity.class);
                startActivity(intent);
            }
        });

        recButton = findViewById(R.id.recMic);
        recButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ConfigApp.isSimulation){
                        mmaCallBackBool = true;
                    Log.d("CHECKED", "called");
                }
                else {
                    if (recButton.isChecked()) {
                        onClickMicREC();
                        CsvFileWriter.crtFile();
                        mmaCallBackBool = true;
                    } else {
                        onClickMicREC();
                        CurrentTickData.resetValues();
                        CsvFileWriter.closeFile();
                        GoogleAccessor.startSaveCsvActivity(CsvFileWriter.lastFilePath);
                        stateMachineHandler.stopStateMachine();
                    }
                }
            }
        });

        showChartButton = findViewById(R.id.showChart);
        showChartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (t.isAlive()){
                    // Do Nothing
                }
                else{
                    t.start();}
                if(showChartButton.isPressed()){
                    Intent intent = new Intent(MainActivity.this, AccChart.class);
                    startActivity(intent);
                }
            }
        });
        //Buttons & Toggle Buttons

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[]grantResults) {
        switch (requestCode) {
            case PERMISSIONS_MULTIPLE_REQUEST:
                //
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Hardware
                    hw = new HardwareFactory(this);
                    stateMachineHandler=new StateMachineHandler(this);
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
            onClickUI();
        }
            if (idOfNavObj == R.id.nav_mic) {
                headerTextView.setText(R.string.mic);
                navigationBool = true;
                onClickUI();
            }
            mDrawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return mToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    public static void actState(String state){
        textViewActState.setText(state);

    }

    Thread t = new Thread(new Runnable() {
        @Override
        public void run() {
            while (true) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (idOfNavObj == R.id.nav_mic) {
                            textView2.setVisibility(View.INVISIBLE);
                            textView3.setVisibility(View.INVISIBLE);
                            textView4.setVisibility(View.INVISIBLE);
                            textView5.setVisibility(View.INVISIBLE);
                            textView1.setText("Max Aplitude : " + CurrentTickData.micMaxAmpl);
                        }
                    }
                });
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    // Do Nothing
                }
            }
        }
    });

    /** TODO SEPARATE INTO OWN INTERFACE SoC
     *  onClick Handlers for different Sensors + UI (later non textual but graphical )
     */
    public void onClickUI(){
        if(navigationBool) {
            //Thread
            if (t.isAlive()){
                //Do Nothing
            }
            else{
                t.start();}
            // UI
            show_hideUI();

        } else {
            //UI
            headerTextView.setText(defaultMessage);
            show_hideUI();

        }
    }
    public void onClickMicREC(){
        if(recButton.isChecked()) {
            //Thread
            if (t.isAlive()){}
            else{
                t.start();}
            //Sensor
            HardwareFactory.hwMic.start();
            Toast.makeText(this,"Aufnahme beginnt", Toast.LENGTH_SHORT).show();
            Toast.makeText(this,"Datei geöffnet", Toast.LENGTH_LONG).show();
        } else {
            //Sensor
            HardwareFactory.hwMic.stop();
            Toast.makeText(this,"Aufnahme beendet", Toast.LENGTH_SHORT).show();
            Toast.makeText(this,"Datei geschlossen", Toast.LENGTH_LONG).show();
        }
    }
    public void show_hideUI(){
        if(textView1.getVisibility() == View.VISIBLE){
            textView1.setVisibility(View.INVISIBLE);
            textView2.setVisibility(View.INVISIBLE);
            textView3.setVisibility(View.INVISIBLE);
            textView4.setVisibility(View.INVISIBLE);
            textView5.setVisibility(View.INVISIBLE);
        }else if(textView1.getVisibility() == View.INVISIBLE){
            textView1.setVisibility(View.VISIBLE);
            textView2.setVisibility(View.VISIBLE);
            textView3.setVisibility(View.VISIBLE);
            textView4.setVisibility(View.VISIBLE);
            textView5.setVisibility(View.VISIBLE);

        }
    }
}