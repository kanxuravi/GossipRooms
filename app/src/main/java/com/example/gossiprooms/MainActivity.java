package com.example.gossiprooms;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    boolean lightModeON = false;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        SharedPreferences settings=getSharedPreferences("prefs",0);
        boolean firstRun = settings.getBoolean("firstRun",false);
        username = settings.getString("username","");
        if(!firstRun) //if running for first time
            //Splash will load for first time
        {
            Intent i=new Intent(this,firstTime.class);
            startActivity(i);
            finish();
        }

        Switch bgMode = findViewById(R.id.backgroundMode);
        bgMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                {
                    View mainView = findViewById( R.id.mainView);
                    TextView name = findViewById(R.id.name);
                    mainView.setBackgroundColor(getResources().getColor(R.color.white));
                    name.setTextColor(getResources().getColor(R.color.black));
                    lightModeON = true;
                }

                else
                    {
                        View mainView = findViewById( R.id.mainView);
                        TextView name = findViewById(R.id.name);
                        mainView.setBackgroundColor(getResources().getColor(R.color.black));
                        name.setTextColor(getResources().getColor(R.color.white));
                        lightModeON = false;
                    }
            }
        });

    }

    public void create_room(View view) {
        Intent create_intent = new Intent(MainActivity.this, createRoom1.class);
        create_intent.putExtra("lightmode",lightModeON);
        create_intent.putExtra("username",username);
        startActivity(create_intent);
    }

    public void join_room(View view) {
        Intent join_intent = new Intent(MainActivity.this, joinRoom1.class);
        join_intent.putExtra("lightmode",lightModeON);
        join_intent.putExtra("username",username);
        startActivity(join_intent);
    }

}
