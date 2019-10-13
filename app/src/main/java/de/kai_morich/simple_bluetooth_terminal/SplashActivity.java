package de.kai_morich.simple_bluetooth_terminal;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            Thread.sleep(1500);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        startActivity(new Intent(this,ShowActivity.class));
        finish();
    }
}
