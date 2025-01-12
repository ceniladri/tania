package com.aminiladri.ibm.tania;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by ibm on 11/10/2017.
 */


public class FuncActivity extends AppCompatActivity {
    public static String NetworkLocation, GPSLocation, Tracker, ApprovdUser;


    //This function will handle all the Toast messages
    public static void blink(String message, Context context){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    //This function gets the most recently updated Network Location
    public static String GetNetworkLocation(String id_str){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef_Net = database.getReference(id_str).child("Network");
        Query NetQuery = myRef_Net.orderByKey().limitToLast(1);


        //NetQuery.addValueEventListener(new com.google.firebase.database.ValueEventListener() {
        NetQuery.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {

                for (com.google.firebase.database.DataSnapshot child: dataSnapshot.getChildren()) {
                    String net_lat_str = child.child("latitude").getValue(String.class);
                    String net_long_str = child.child("longitude").getValue(String.class);
                    String net_time = child.child("time").getValue(String.class);
                    String net_date = child.child("date").getValue(String.class);
                    //TraceActivity.NetworkLocation.setText("Network Location updated on: " +net_date + " " +net_time +": maps.google.com/maps?q=loc:" + net_lat_str + "," + net_long_str);
                    NetworkLocation = ("Network Location updated on: " +net_date + " " +net_time +": maps.google.com/maps?q=loc:" + net_lat_str + "," + net_long_str);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }

        });
    return (NetworkLocation);
    }

    //This function gets the most recently updated GPS Location
    public static String GetGPSLocation(String id_str){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef_GPS = database.getReference(id_str).child("GPS");
        Query GPSQuery = myRef_GPS.orderByKey().limitToLast(1);


        GPSQuery.addValueEventListener(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {

                for (com.google.firebase.database.DataSnapshot child: dataSnapshot.getChildren()) {
                    String gps_lat_str = child.child("latitude").getValue(String.class);
                    String gps_long_str = child.child("longitude").getValue(String.class);
                    String gps_time = child.child("time").getValue(String.class);
                    String gps_date = child.child("date").getValue(String.class);
                    //TraceActivity.GPSLocation.setText("GPS Location updated on: " +gps_date + " " +gps_time +": maps.google.com/maps?q=loc:" + gps_lat_str + "," + gps_long_str);
                    GPSLocation = ("GPS Location updated on: " +gps_date + " " +gps_time +": maps.google.com/maps?q=loc:" + gps_lat_str + "," + gps_long_str);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    return(GPSLocation);
    }

    //Getting the one-time value of the tracer
    public static String GetTracker(String id_str) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef_tracker = database.getReference(id_str);
        Query Tracker_Query = myRef_tracker.orderByKey().limitToLast(1);
        Tracker_Query.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
                Tracker = dataSnapshot.child("Tracer").getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    return (Tracker);
    }

    public static void DelElements(String mEmail){
        //The next code deletes the older info from the database (we cannot keep all the info forever)
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef_Net = database.getReference(mEmail).child("Network");
        DatabaseReference myRef_GPS = database.getReference(mEmail).child("GPS");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 0);
        Date todate1 = cal.getTime();
        final String fromdate = new java.text.SimpleDateFormat("yyyy-MMM-dd").format(todate1);

        Query Del_Net_Query = myRef_Net.orderByChild("date");

        // Read all the values from the database and delete only old values
        Del_Net_Query.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
                for (com.google.firebase.database.DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    if (fromdate.equals(snapshot.child("date").getValue(String.class))) {
                    } else{
                        //Below IF will skip the key field
                        if (snapshot.hasChild("date")) {
                            snapshot.getRef().removeValue();
                        }
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        Query Del_GPS_Query = myRef_GPS.orderByChild("date");

        // Read all the values from the database and delete only old values
        Del_GPS_Query.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
                for (com.google.firebase.database.DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (fromdate.equals(snapshot.child("date").getValue(String.class))) {
                    } else{
                        //Below IF will skip the key field
                        if (snapshot.hasChild("date")) {
                            snapshot.getRef().removeValue();
                        }
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }



    //Getting the one-time value of the tracer
    public static String GetApprovdUser() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef_tracker = database.getReference("Users");
        Query Tracker_Query = myRef_tracker.orderByKey().limitToLast(1);
        Tracker_Query.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
                ApprovdUser = dataSnapshot.child("Val1").getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
        return (ApprovdUser);
    }


}
