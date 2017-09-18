package com.salam.elearning;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;

import com.facebook.stetho.Stetho;
import com.salam.elearning.Models.User;

import java.util.List;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Stetho.initializeWithDefaults(this);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startApp();
            }
        }, 5000);
    }

    private void startApp(){
        Intent intent;
        List<User> user = User.getLoggedInUser();
        if(user.isEmpty())
            intent = new Intent(SplashActivity.this, LoginActivity.class);
        else {
            intent = new Intent(SplashActivity.this, MainActivity.class);
            intent.putExtra("userID", user.get(0).getServerId());
        }

        startActivity(intent);
        finish();

    }
}
