package com.aminiladri.ibm.tania;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.Arrays;

public class StartActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{

    // Instantiating the Interface
    private Button Button1;


    //Account Variables
    public static String Email, FullName, mEmail, mDomain, ApprovdUser;
    private static final int RC_SIGN_IN = 1;
    private GoogleApiClient mGoogleApiClient;

    //Following Thread will show the Toast while quitting the app if the User is not an authorized one
    Thread thread = new Thread(){
        @Override
        public void run() {
            try {
                Thread.sleep(Toast.LENGTH_LONG); // As I am using LENGTH_LONG in Toast
                StartActivity.this.finish();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        Button1 = (Button) findViewById(R.id.getGoogleID);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        Button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

    }
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from
        //   GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount acct = result.getSignInAccount();
                // Get account information
                FullName = acct.getDisplayName(); //Niladri Saha Roy
                Email = acct.getEmail(); //ceniladri@gmail.com

                String[] parts = Email.split("@");
                mEmail = parts[0]; //ceniladri
                mDomain = parts[1]; // gmail.com

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef_tracker = database.getReference("Users");
                Query Tracker_Query = myRef_tracker.orderByKey().limitToLast(1);
                Tracker_Query.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
                    @Override
                    public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
                        ApprovdUser = dataSnapshot.child("Val1").getValue(String.class);
                        String[] array = ApprovdUser.split(";");
                        if (Arrays.asList(array).contains(mEmail)) {
                            //Approved Users
                            Intent intent = new Intent(StartActivity.this, MainActivity.class);
                            StartActivity.this.startActivity(intent);
                        }else {
                            Toast.makeText(getApplicationContext(),"Sorry, You are not authorized to use this App yet",Toast.LENGTH_SHORT).show();
                            thread.start();
                            //SystemClock.sleep(7000);
                            //finish();
                            //System.exit(0);
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
            else {//Toast.makeText(getApplicationContext(), result.getStatus().toString(), Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(), "Error getting the credentials. Please Try once again.", Toast.LENGTH_SHORT).show();}
        }
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

}
