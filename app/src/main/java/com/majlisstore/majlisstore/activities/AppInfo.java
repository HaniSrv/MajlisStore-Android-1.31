package com.majlisstore.majlisstore.activities;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.majlisstore.majlisstore.R;

import java.util.Locale;

public class AppInfo extends AppCompatActivity {

    LinearLayout layAppInfo;
    ImageView imgBack;
    TextView tvVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLocale();
        setContentView(R.layout.activity_app_info);

        layAppInfo=findViewById(R.id.layAppInfo);
        imgBack=findViewById(R.id.imgBack);
        tvVersion=findViewById(R.id.tvVersion);

        try
        {
            PackageManager manager = getPackageManager();
            PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
            String versionno = "Version "+info.versionName;
            tvVersion.setText(versionno);
        }
        catch (PackageManager.NameNotFoundException ignored)
        {
        }


        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try
                {
                    onBackPressed();
                }
                catch (Exception ignored)
                {

                }
            }
        });
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
