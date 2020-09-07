package com.majlisstore.majlisstore.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.majlisstore.majlisstore.R;

import java.util.Locale;

public class Login extends AppCompatActivity {

    TextView tv1,tv2;
    Button btnSign,btnSkip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLocale();
        setContentView(R.layout.activity_login);

        tv1=findViewById(R.id.tv1);
        tv2=findViewById(R.id.tv2);

        btnSign=findViewById(R.id.btnSign);
        btnSkip=findViewById(R.id.btnSkip);

        btnSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try
                {
                    Intent i=new Intent(Login.this, Signin.class);
                    startActivity(i);
                }
                catch (Exception ignored)
                {

                }

            }
        });

        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try
                {
                    Intent i=new Intent(Login.this,Dashboard.class);
                    startActivity(i);
                    finish();
                }
                catch (Exception ignored)
                {

                }

            }
        });
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        finish();
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
