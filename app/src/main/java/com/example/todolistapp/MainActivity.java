package com.example.todolistapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final int SPLASH = 3300;

    Animation iconAnim,toDoAppTxtAnim;
    ImageView mainIcon;
    TextView mainTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        iconAnim = AnimationUtils.loadAnimation(this, R.anim.icon_anim);
        toDoAppTxtAnim = AnimationUtils.loadAnimation(this, R.anim.todoapptextanim);
        mainIcon = findViewById(R.id.mainIcon);
        mainTxt = findViewById(R.id.mainTxt);

        mainIcon.setAnimation(iconAnim);
        mainTxt.setAnimation(toDoAppTxtAnim);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        },SPLASH);

    }
}