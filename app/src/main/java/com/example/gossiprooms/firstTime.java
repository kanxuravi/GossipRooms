package com.example.gossiprooms;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.system.Os;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class firstTime extends AppCompatActivity {

    String username;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.firsttime);
    }


    public void startUp(View view) {
        EditText getUserName = findViewById(R.id.usernamesetup);
        username = getUserName.getText().toString().trim();
        Date currentTime = Calendar.getInstance().getTime();
        final String time = currentTime.toString();
        if (username.isEmpty())
        {
            getUserName.requestFocus();
            InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,InputMethodManager.HIDE_IMPLICIT_ONLY);
            Toast userNameError = Toast.makeText(getApplicationContext(),"You must enter a username",Toast.LENGTH_SHORT);
            userNameError.show();
        }
        else
        {
            final ConnectivityManager connMgr = (ConnectivityManager)
                    this.getSystemService(Context.CONNECTIVITY_SERVICE);
            assert connMgr != null;
            final android.net.NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            final android.net.NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (Objects.requireNonNull(wifi).isConnectedOrConnecting() || Objects.requireNonNull(mobile).isConnectedOrConnecting())
            {
                myRef.orderByKey().equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists())
                        {
                            Toast userNameError = Toast.makeText(getApplicationContext(),"Username has been taken. Please choose another",Toast.LENGTH_SHORT);
                            userNameError.show();
                        }
                        else
                        {
                            myRef.child(username).setValue(time);
                            SharedPreferences settings=getSharedPreferences("prefs",0);
                            SharedPreferences.Editor editor=settings.edit();
                            editor.putString("username",username);
                            editor.putBoolean("firstRun",true);
                            editor.apply();
                            Toast userNamesuccess = Toast.makeText(getApplicationContext(),"Username Set Successfully",Toast.LENGTH_SHORT);
                            userNamesuccess.show();
                            Intent i = new Intent(firstTime.this, MainActivity.class);
                            startActivity(i);
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



            }
            else
            {
                Toast network_error = Toast.makeText(getApplicationContext(), "NO INTERNET CONNECTION DETECTED", Toast.LENGTH_SHORT);
                network_error.show();
            }
        }
    }
}
