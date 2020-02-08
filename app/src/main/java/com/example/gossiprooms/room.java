package com.example.gossiprooms;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class room extends AppCompatActivity {

    boolean lightModeON;
    String username, msg;
    String groupname;
    LinearLayout msgList;
    FirebaseDatabase database = FirebaseDatabase.getInstance();


    @SuppressLint("SetTextI18n")


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.room);

        lightModeON = Objects.requireNonNull(getIntent().getExtras()).getBoolean("lightmode");
        username = getIntent().getExtras().getString("username");
        groupname = getIntent().getExtras().getString("groupname");
        final ScrollView scrollLayout = findViewById(R.id.scrollView2);

        if (lightModeON) {
            View mainView = findViewById(R.id.mainRoom);
            TextView welcome = findViewById(R.id.group_name);

            mainView.setBackgroundColor(getResources().getColor(R.color.white));
            welcome.setTextColor(getResources().getColor(R.color.black));



        } else {
            View mainView = findViewById(R.id.mainRoom);
            TextView welcome = findViewById(R.id.group_name);
            mainView.setBackgroundColor(getResources().getColor(R.color.black));
            welcome.setTextColor(getResources().getColor(R.color.white));
        }

        @SuppressLint("CutPasteId") TextView welcomeName = findViewById(R.id.group_name);
        welcomeName.setText("Welcome to " + groupname);

      updateView();

        scrollLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                scrollLayout.fullScroll(ScrollView.FOCUS_DOWN);

            }
        },1000);


    }

    private void updateView() {
        DatabaseReference myRef = database.getReference("messages");
        DatabaseReference viewRef = myRef.child(groupname);
        msgList = findViewById(R.id.msgContainer);
        final ScrollView scrollLayout = findViewById(R.id.scrollView2);


        ChildEventListener eventListener = new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    String Message = ds.getValue(String.class);
                    final TextView tv = new TextView(room.this);
                    tv.setTextSize(20);
                    tv.setPadding(20,20,20,20);
                    tv.setText(Message);
                    if (lightModeON)
                    {
                        tv.setTextColor(getResources().getColor(R.color.black));
                    }
                    else
                    {
                        tv.setTextColor(getResources().getColor(R.color.white));
                    }
                    msgList.addView(tv);
                    scrollLayout.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            scrollLayout.fullScroll(ScrollView.FOCUS_DOWN);

                        }
                    },1000);

                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {



            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        };
        viewRef.addChildEventListener(eventListener);
    }



    public void send_msg(final View view)
        {
            final ConnectivityManager connMgr = (ConnectivityManager)
                    this.getSystemService(Context.CONNECTIVITY_SERVICE);
            assert connMgr != null;
            final android.net.NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            final android.net.NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (Objects.requireNonNull(wifi).isConnectedOrConnecting() || Objects.requireNonNull(mobile).isConnectedOrConnecting())
            {

                final DatabaseReference myRef = database.getReference("messages");
            myRef.orderByChild("Group_Name").equalTo(groupname)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            DatabaseReference msgRef = myRef.child(groupname);
                            EditText msgProvider = findViewById(R.id.inputMessage);
                            msg = msgProvider.getText().toString();
                            if(!msg.equals(""))
                            {
                                Map<String, String> messages = new HashMap<>();
                                messages.put("message", username +" : "+msg);
                                msgRef.push().setValue(messages);
                                msgProvider.setText("");
                                InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
                                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                            }
                            else
                            {
                                Toast nomsg = Toast.makeText(getApplicationContext(),"No Message to send",Toast.LENGTH_SHORT);
                                nomsg.show();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast dberr = Toast.makeText(getApplicationContext(),"Database Error",Toast.LENGTH_SHORT);
                            dberr.show();
                        }

                    });
        }
        else {
                Toast network_error = Toast.makeText(getApplicationContext(), "NO INTERNET CONNECTION DETECTED", Toast.LENGTH_SHORT);
                network_error.show();
            }
        }


    public void exit_chat(View view) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        Intent intent = new Intent(room.this,MainActivity.class);
                        startActivity(intent);
                        finish();

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setMessage("Are you sure to Exit Group").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();


    }

    @Override
    public void onBackPressed() {
    }

    public void delete_chat(View view) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        final DatabaseReference myRef = database.getReference("messages");
                        myRef.child(groupname).removeValue();
                        Toast nomsg = Toast.makeText(getApplicationContext(),"Messages deleted successfully.",Toast.LENGTH_SHORT);
                        nomsg.show();
                        recreate();


                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setMessage("Are you sure to delete all messages?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();

    }
}