package com.example.gossiprooms;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class createRoom1 extends AppCompatActivity {

    boolean lightModeON, validation;
    String grpName, grpPass, user;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("rooms");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_room1);

        lightModeON = Objects.requireNonNull(getIntent().getExtras()).getBoolean("lightmode");
        if (lightModeON)
        {
            View mainView = findViewById( R.id.create_view);
            mainView.setBackgroundColor(getResources().getColor(R.color.white));
        }
        else {
            View mainView = findViewById( R.id.create_view);
            mainView.setBackgroundColor(getResources().getColor(R.color.black));
        }
    }

    public void create(View view) {
        final ConnectivityManager connMgr = (ConnectivityManager)
                this.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connMgr != null;
        final android.net.NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        final android.net.NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            EditText group_name = findViewById(R.id.group_name);
            EditText group_pass = findViewById(R.id.grp_pass);
            EditText username = findViewById(R.id.user_name);

            grpName = group_name.getText().toString();
            grpPass = group_pass.getText().toString();
            user = username.getText().toString();
            validate();



            if (validation) {
                final ProgressDialog progress = new ProgressDialog(this);
                progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progress.setCancelable(false);
                if (Objects.requireNonNull(wifi).isConnectedOrConnecting() || Objects.requireNonNull(mobile).isConnectedOrConnecting()) {
                progress.show();
                myRef.orderByChild("Group_Name").equalTo(grpName)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    progress.dismiss();
                                    Toast exists = Toast.makeText(getApplicationContext(), "Group Name already exists", Toast.LENGTH_SHORT);
                                    exists.show();
                                } else {
                                    Map<String, String> groupDetails = new HashMap<>();
                                    groupDetails.put("Group_Name", grpName);
                                    groupDetails.put("Group_PassKey", grpPass);
                                    myRef.push().setValue(groupDetails);
                                    progress.dismiss();
                                    Toast created = Toast.makeText(getApplicationContext(), "Group Created", Toast.LENGTH_SHORT);
                                    created.show();
                                    Intent intent = new Intent(createRoom1.this, room.class);
                                    intent.putExtra("lightmode", lightModeON);
                                    intent.putExtra("username", user);
                                    intent.putExtra("groupname", grpName);
                                    startActivity(intent);
                                    finish();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


            }
                else {
                    Toast network_error = Toast.makeText(getApplicationContext(), "NO INTERNET CONNECTION DETECTED", Toast.LENGTH_SHORT);
                    network_error.show();
                }
        }

    }

    private void validate() {
        if (grpName == null || grpName.length() <= 3){
            Toast groupNameError = Toast.makeText(getApplicationContext(),"Group Name must be of 4 characters",Toast.LENGTH_SHORT);
            groupNameError.show();
            validation = false;
        }

        else if (grpPass == null || grpPass.length() <= 7)
        {
            Toast groupNameError = Toast.makeText(getApplicationContext(),"Group Pass Key must be of 8 characters",Toast.LENGTH_SHORT);
            groupNameError.show();
            validation = false;
        }

        else if (user == null || user.length() <= 0)
        {
            Toast userNameError = Toast.makeText(getApplicationContext(),"Must Enter a Username to Enter a Group",Toast.LENGTH_SHORT);
            userNameError.show();
            validation = false;
        }

        else {
            validation =true;
        }
    }


}
