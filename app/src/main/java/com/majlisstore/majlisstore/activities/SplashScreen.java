package com.majlisstore.majlisstore.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.majlisstore.majlisstore.R;
import com.majlisstore.majlisstore.helpers.SessionManager;

import java.util.Locale;

public class SplashScreen extends AppCompatActivity {

    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLocale();
        setContentView(R.layout.activity_splash_screen);

        session=new SessionManager(this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                try
                {
                    if(session.isLoggedIn())
                    {
                        Intent i=new Intent(SplashScreen.this,Dashboard.class);
                        startActivity(i);
                        finish();
                    }
                    else
                    {
                        Intent i=new Intent(SplashScreen.this, Login.class);
                        startActivity(i);
                        finish();
                    }
                }
                catch (Exception ignored)
                {
                }
            }
        },2000);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ActivityCompat.finishAffinity(this);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        setLocale();
    }

    private void setLocale() {
        Locale myLocale = new Locale("en");
        Locale.setDefault(myLocale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.locale = myLocale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
    }
}
