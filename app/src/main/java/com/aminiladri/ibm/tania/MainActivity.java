package com.aminiladri.ibm.tania;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.FirebaseDatabase;



public class MainActivity extends AppCompatActivity {

    private Button Button1, Button2, Button3, Button4;
    private TextView NetworkAddress, GPSAddress;
    private BroadcastReceiver broadcastReceiver; //To Get the Link from GPS Service as a Broadcast
    private FirebaseDatabase database = FirebaseDatabase.getInstance();

    public String tracerID_edittext;

    @Override
    protected void onResume() {
        super.onResume();
        if (broadcastReceiver == null) {
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String NetLoc = ""+intent.getExtras().get("Network Location");
                    String GPSLoc = ""+intent.getExtras().get("GPS Location");

                    if(NetLoc.startsWith("maps.google.com")){
                        NetworkAddress.setText("Network Location: " + NetLoc);//Got the location from GPS Service
                    }
                    if(GPSLoc.startsWith("maps.google.com")){
                        GPSAddress.setText("GPS Location: " + GPSLoc);//Got the location from GPS Service
                    }
                }
            };
        }
        registerReceiver(broadcastReceiver, new IntentFilter("location update"));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button1 = (Button) findViewById(R.id.button1);
        Button2 = (Button) findViewById(R.id.button2);
        Button3 = (Button) findViewById(R.id.button3);
        Button4 = (Button) findViewById(R.id.button4);
        NetworkAddress = (TextView) findViewById(R.id.NetworkAddress);
        GPSAddress = (TextView) findViewById(R.id.GPSAddress);


        if (!runtime_permissions())
            enable_buttons();
    }


    public void enable_buttons() {

        Button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), GPS_Service.class);
                startService(i); //Starting Location Tracking Service
                FuncActivity.DelElements(StartActivity.mEmail);
            }

        });

        Button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), GPS_Service.class);
                stopService(i); //Stopping the Location Tracking Service

            }

        });

        Button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Previous Code moved to MapsActivity.java. Now we shall be showing the precise location in Google Maps
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                MainActivity.this.startActivity(intent);

            }

        });

        Button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //we are adding the tracker id here. Only the id will be able to tracker this device's location
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                View view_tracer = LayoutInflater.from(MainActivity.this).inflate(R.layout.tracer, null);
                final EditText tracerID = (EditText) view_tracer.findViewById(R.id.tracerid);
                final Button setTracer = (Button) view_tracer.findViewById(R.id.settracer);
                builder.setView(view_tracer);
                tracerID.setText(tracerID_edittext);
                final AlertDialog show = builder.show();

                setTracer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String tracker_str = tracerID.getText().toString();
                        tracerID_edittext = tracker_str;
                        database.getReference(StartActivity.mEmail).child("Tracer").setValue(tracker_str);
                        FuncActivity.blink("Tracker Added",getApplicationContext());
                        show.dismiss();
                    }
                });
            }
        });
    }

    private boolean runtime_permissions() {

        if (Build.VERSION.SDK_INT >= 23 && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET}, 100); //Dialog Box Created to request permission
            return true; //Got the Permission
        }

        return false;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) { //Checking the permissions
                enable_buttons();
            } else {
                runtime_permissions(); //Start the dialog to request permission
            }
        }
    }

}
