package com.aminiladri.ibm.tania;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    // Instantiating the Interface
    private Button FindNetLoc, FindGPSLoc;
    private EditText Edittext;
    private Switch Switch1;
    public boolean bool = false;
    private LatLng base;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Instantiating the Interface
        //FindNetLoc = (Button) findViewById(R.id.findNetLoc);
        FindGPSLoc = (Button) findViewById(R.id.findGPSLoc);
        Edittext = (EditText) findViewById(R.id.editText);
        Switch1 = (Switch) findViewById(R.id.switch1);
        base = new LatLng( 0, 0);

        MapFragment Mapfragment = (MapFragment) getFragmentManager().findFragmentById(R.id.fragment);
        Mapfragment.getMapAsync(this);

        Switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //Following switch will determine whether the Network Location will be tracker or not
                if (isChecked) {
                    Toast.makeText(getApplicationContext(), "Network Location Tracking Turned On", Toast.LENGTH_SHORT).show();
                    bool= true;
                } else {
                    Toast.makeText(getApplicationContext(), "Network Location Tracking Turned Off", Toast.LENGTH_SHORT).show();
                    bool= false;
                }
            }
        });

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        FindGPSLoc.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //Checking Whether id has the tracker access
                String tracker_str = Edittext.getText().toString();
                if (StartActivity.mEmail.equals(tracker_str)) {
                    //The tracked and tracking id is same therefore the user is tracking his own location
                    FuncActivity.blink("You will be tracking your own location", getApplicationContext());
                    getGPSLocation(tracker_str);
                }
                else {
                    //The User wants to track a different user, Checking the Permissions
                    if (StartActivity.mEmail.equals(FuncActivity.GetTracker(tracker_str))) {
                        //User's id is set as the Tracer id for the id to be traced, Show the locations
                        FuncActivity.blink("You have access. Showing the Locations", getApplicationContext());
                        getGPSLocation(tracker_str);
                    } else {
                        //User's id is different from the Tracer id for the id to be traced. Errorred!
                        FuncActivity.blink("You do not seem to have the necessary permissions", getApplicationContext());
                    }
                }
            }
        });

    }

    public void getGPSLocation( String id_str) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef_GPS = database.getReference(id_str).child("GPS");
        //Following command will help pull the last updated location
        Query GPSQuery = myRef_GPS.orderByKey().limitToLast(1);


        GPSQuery.addValueEventListener(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {

                for (com.google.firebase.database.DataSnapshot child: dataSnapshot.getChildren()) {
                    String gps_lat_str = child.child("latitude").getValue(String.class);
                    String gps_long_str = child.child("longitude").getValue(String.class);
                    String gps_time = child.child("time").getValue(String.class);
                    String gps_date = child.child("date").getValue(String.class);
                    String gps_provider = child.child("provider").getValue(String.class);
                    //TraceActivity.GPSLocation.setText("GPS Location updated on: " +gps_date + " " +gps_time +": maps.google.com/maps?q=loc:" + gps_lat_str + "," + gps_long_str);
                    //String GPSLocation = ("GPS Location updated on: " +gps_date + " " +gps_time +": maps.google.com/maps?q=loc:" + gps_lat_str + "," + gps_long_str);
                    //Toast.makeText(getApplicationContext(), GPSLocation, Toast.LENGTH_SHORT).show();
                    try {
                        LatLng sydney = new LatLng(Double.parseDouble(gps_lat_str), Double.parseDouble(gps_long_str));
                        //Toast.makeText(getApplicationContext(), gps_provider, Toast.LENGTH_SHORT).show();
                        if (gps_provider.equals("gps")) {
                            mMap.addMarker(new MarkerOptions().position(sydney).title("Updated On: " + gps_time + " " + gps_date).icon(BitmapDescriptorFactory.fromResource(R.mipmap.dot)));
                        } else if (gps_provider.equals("network") && bool == true) {
                            //If the switch is turned off, Ignore the Network Locations
                            //If the switch is turned on, show the Network Locations
                            mMap.addMarker(new MarkerOptions().position(sydney).title("Updated On: " + gps_time + " " + gps_date).icon(BitmapDescriptorFactory.fromResource(R.mipmap.black_dot)));
                        }
                        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, mMap.getMaxZoomLevel()));
                        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,  16.0f));
                        mMap.setMyLocationEnabled(true);

                        if (gps_provider.equals("network") && bool == false) {
                            //If the switch is turned off, Ignore the Network Locations
                        } else{
                            //If the switch is turned on, show the Network Locations
                            if (base.latitude == 0 && base.longitude == 0) {
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 16.0f));
                            } else {
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, mMap.getCameraPosition().zoom));
                                //Toast.makeText(getApplicationContext(), "Base: " + base, Toast.LENGTH_SHORT).show();
                                PolylineOptions poption = new PolylineOptions().add(sydney).add(base).width(5).color(Color.RED).geodesic(true);
                                mMap.addPolyline(poption);
                            }
                            base = sydney;

                        }
                    }catch (NullPointerException ex) {
                        //Toast.makeText(getApplicationContext(), "NullPointerException", Toast.LENGTH_SHORT).show();
                    }
                }
            }        @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}