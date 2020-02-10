package com.example.gossiprooms;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class room extends AppCompatActivity {

    boolean lightModeON;
    String username, msg;
    String groupname;
    LinearLayout msgList;
    ArrayList<String> userlist = new ArrayList<>();
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
        welcomeName.setText("Gossip Room : " + groupname);

      updateView();
      updateUserList();

        scrollLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                scrollLayout.fullScroll(ScrollView.FOCUS_DOWN);

            }
        },1000);


    }

    private void updateUserList() {
        DatabaseReference myRef = database.getReference("currentusers");
        DatabaseReference viewRef = myRef.child(groupname);

        userlist.add("Active Users");

        ChildEventListener eventListener = new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String users = ds.getValue(String.class);
                    userlist.add(users);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String users = ds.getValue(String.class);
                    userlist.remove(users);
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        };
        viewRef.addChildEventListener(eventListener);
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
                    tv.setTypeface(ResourcesCompat.getFont(getApplicationContext(),R.font.anonymous_pro_bold));
                    tv.setPadding(20,20,20,20);
                    tv.setText(Message);
                    tv.setTextColor(getResources().getColor(R.color.colorLightGreen));

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
            final InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            assert imm != null;
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
                                msgProvider.setText("");
                                InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
                                assert inputMethodManager != null;
                                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                                Map<String, String> messages = new HashMap<>();
                                messages.put("message", username +" > "+msg);
                                msgRef.push().setValue(messages);
                            }
                            else
                            {
                                Toast nomsg = Toast.makeText(getApplicationContext(),"No Message to send, Please Write something",Toast.LENGTH_SHORT);
                                nomsg.show();
                                msgProvider.requestFocus();
                                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,InputMethodManager.HIDE_IMPLICIT_ONLY);

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
                        removeUserFromList();
                        startActivity(intent);
                        finish();

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setMessage("Are you sure to Exit Room?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();


    }

    public void removeUserFromList() {
        DatabaseReference myRef = database.getReference("currentusers").child(groupname);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                {
                    if(dataSnapshot1.child("user").getValue().toString().equals(username))
                    {
                        dataSnapshot1.getRef().setValue(null);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        }

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onStart() {
        super.onStart();
        addUserInList();
    }

    @Override
    protected void onStop() {
        super.onStop();
        removeUserFromList();
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

    public void showUserList(View view) {
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popuplist, null);
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        ListView popupText = popupView.findViewById(R.id.roomMemberList);
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this,
                        android.R.layout.simple_list_item_1, userlist);
        popupText.setAdapter(adapter);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        popupView.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });


    }

    private void addUserInList() {
        final DatabaseReference myRef = database.getReference("currentusers");
        myRef.orderByChild("Group_Name").equalTo(groupname)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.exists())
                        {
                            DatabaseReference userRef = myRef.child(groupname);
                            Map<String, String> unames = new HashMap<>();
                            unames.put("user", username);
                            userRef.push().setValue(unames);
                        }
                        else
                        {
                            Toast exists = Toast.makeText(getApplicationContext(), "Same username have already joined. Please choose another name.", Toast.LENGTH_SHORT);
                            exists.show();
                        }


                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}