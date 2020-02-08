package com.example.gossiprooms;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class joinRoom1 extends AppCompatActivity {

    boolean lightModeON , validation;
    String grpName, grpPass, roomkey;
    String groupname, grouppass, user;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("rooms");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.join_room1);

        lightModeON = Objects.requireNonNull(getIntent().getExtras()).getBoolean("lightmode");
        if (lightModeON)
        {
            View mainView = findViewById( R.id.join_view);
            EditText checkGroupName = findViewById(R.id.enterGrpName);
            EditText checkGroupPass = findViewById(R.id.enterPassKey);
            Button join = findViewById(R.id.joinbtn);
            checkGroupName.setTextColor(getResources().getColor(R.color.white));
            checkGroupPass.setTextColor(getResources().getColor(R.color.white));
            join.setTextColor(getResources().getColor(R.color.white));
            mainView.setBackgroundColor(getResources().getColor(R.color.white));
        }
        else {
            View mainView = findViewById( R.id.join_view);
            EditText checkGroupName = findViewById(R.id.enterGrpName);
            EditText checkGroupPass = findViewById(R.id.enterPassKey);
            Button join = findViewById(R.id.joinbtn);
            checkGroupName.setTextColor(getResources().getColor(R.color.white));
            checkGroupPass.setTextColor(getResources().getColor(R.color.white));
            join.setTextColor(getResources().getColor(R.color.white));
            mainView.setBackgroundColor(getResources().getColor(R.color.black));
        }

    }

    public void join(View view) {
             validateIfExists();
        }





    private void validateIfExists() {
        EditText GroupName = findViewById(R.id.enterGrpName);
        EditText GroupPass = findViewById(R.id.enterPassKey);
        EditText username = findViewById(R.id.enterUserName);
        grpName = GroupName.getText().toString();
        grpPass = GroupPass.getText().toString();
        user = username.getText().toString();

        final ConnectivityManager connMgr = (ConnectivityManager)
                this.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connMgr != null;
        final android.net.NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        final android.net.NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);


        if (grpName.matches("")) {
            Toast.makeText(this, "You did not enter a Group Name", Toast.LENGTH_SHORT).show();
            return;
        }
        if (grpPass.matches("")) {
            Toast.makeText(this, "You did not enter a Group Pass Key", Toast.LENGTH_SHORT).show();
            return;
        }
        if (user.matches("")) {
            Toast.makeText(this, "You must enter a Username", Toast.LENGTH_SHORT).show();
            return;
        }
        final ProgressDialog progress = new ProgressDialog(this);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setCancelable(false);
        Objects.requireNonNull(progress.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        if (Objects.requireNonNull(wifi).isConnectedOrConnecting() || Objects.requireNonNull(mobile).isConnectedOrConnecting()) {

            progress.show();


            myRef.orderByChild("Group_Name").equalTo(grpName)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                                    grouppass = messageSnapshot.child("Group_PassKey").getValue().toString();
                                    groupname = messageSnapshot.child("Group_Name").getValue().toString();
                                }

                                if (grouppass.equals(grpPass)) {
                                    validation = true;
                                    progress.dismiss();
                                    Toast rightPass = Toast.makeText(getApplicationContext(), "Joined Successfully", Toast.LENGTH_SHORT);
                                    rightPass.show();
                                    Intent intent = new Intent(joinRoom1.this, room.class);
                                    intent.putExtra("lightmode", lightModeON);
                                    intent.putExtra("groupname", groupname);
                                    intent.putExtra("username", user);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    validation = false;
                                    progress.dismiss();
                                    Toast wrongPass = Toast.makeText(getApplicationContext(), "Incorrect Passkey", Toast.LENGTH_SHORT);
                                    wrongPass.show();
                                }

                            } else {
                                validation = false;
                                progress.dismiss();
                                Toast noexists = Toast.makeText(getApplicationContext(), "Group does not exist.", Toast.LENGTH_SHORT);
                                noexists.show();
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



