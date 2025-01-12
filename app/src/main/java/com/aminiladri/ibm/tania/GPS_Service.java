package com.aminiladri.ibm.tania;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.Date;



public class GPS_Service extends Service {

    private LocationListener listener; //location variables
    private LocationManager locationmanager;
    FirebaseDatabase database = FirebaseDatabase.getInstance();   // Initializing the Firebase records


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;

    }


    @Override
    public void onCreate(){


        listener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                Intent i = new Intent("location update");

                //getting the location values
                final String lat_str = ""+ location.getLatitude();
                final String long_str = ""+ location.getLongitude();
                final String provider_str = location.getProvider();
                final double accuracy = location.getAccuracy();
                //final String id_str = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID); //device id

                //StartActivity.mEmail from MainActivity is the key of getting the database loaded
                DatabaseReference myNetLoc = database.getReference(StartActivity.mEmail).child("Network").push(); //Network Location Database
                DatabaseReference myGPSLoc = database.getReference(StartActivity.mEmail).child("GPS").push(); //GPS Location Database



                //Getting the Last Key for getting the last value for web app
                String myGPSLoc_key =  myGPSLoc.getKey();
                database.getReference(StartActivity.mEmail).child("GPSlastkey").setValue(myGPSLoc_key);

                //FuncActivity.blink("Got GPS Location",getApplicationContext());
                myGPSLoc.child("latitude").setValue(lat_str);
                myGPSLoc.child("longitude").setValue(long_str);

                String currentTime = new java.text.SimpleDateFormat("HH:mm:ss").format(new Date());
                myGPSLoc.child("time").setValue(currentTime);
                String currentDate = new java.text.SimpleDateFormat("yyyy-MMM-dd").format(new Date());
                myGPSLoc.child("date").setValue(currentDate);
                myGPSLoc.child("provider").setValue(provider_str);
                final String link_str = "maps.google.com/maps?q=loc:" + lat_str + "," + long_str;
                i.putExtra("GPS Location", link_str);
                sendBroadcast(i); //sending the values to MainActivity

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {}
            @Override
            public void onProviderEnabled(String s) {}
            @Override
            public void onProviderDisabled(String s) {

                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);

            }

        };

        //Here we are invoking both GPS and Network Location. You may change the frequency if battery or data usage is an issue
        locationmanager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        locationmanager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, listener);
        locationmanager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 10, listener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        locationmanager.removeUpdates(listener); //stopping the location listener on exit
    }

}