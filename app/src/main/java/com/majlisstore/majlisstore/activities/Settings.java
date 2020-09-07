package com.majlisstore.majlisstore.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.majlisstore.majlisstore.R;
import com.majlisstore.majlisstore.helpers.CheckNetwork;

import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.Locale;

public class Settings extends AppCompatActivity {

    LinearLayout laySettings,layAbout,layApp,layCancel,layFaq,layPrivacy,layReach,layReview,layUpdate,layTerms;
    ImageView imgBack;
    TextView tvSettings,tvAbout,tvApp,tvCancel,tvFaq,tvPrivacy,tvReach,tvReview,tvUpdate,tvTerms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLocale();
        setContentView(R.layout.activity_settings);

        laySettings=findViewById(R.id.laySettings);
        layAbout=findViewById(R.id.layAbout);
        layApp=findViewById(R.id.layApp);
        layCancel=findViewById(R.id.layCancel);
        layFaq=findViewById(R.id.layFaq);
        layPrivacy=findViewById(R.id.layPrivacy);
        layReach=findViewById(R.id.layReach);
        layReview=findViewById(R.id.layReview);
        layUpdate=findViewById(R.id.layUpdate);
        layTerms=findViewById(R.id.layTerms);

        tvSettings=findViewById(R.id.tvSettings);
        tvAbout=findViewById(R.id.tvAbout);
        tvApp=findViewById(R.id.tvApp);
        tvCancel=findViewById(R.id.tvCancel);
        tvFaq=findViewById(R.id.tvFaq);
        tvPrivacy=findViewById(R.id.tvPrivacy);
        tvReach=findViewById(R.id.tvReach);
        tvReview=findViewById(R.id.tvReview);
        tvUpdate=findViewById(R.id.tvUpdate);
        tvTerms=findViewById(R.id.tvTerms);

        imgBack=findViewById(R.id.imgBack);


        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    onBackPressed();
                }
                catch (Exception ignored)
                {

                }
            }
        });

        layAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try
                {
                    callUrl("https://majlisstore.com/about");
                }
                catch (Exception ignored)
                {

                }

            }
        });

        layApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try
                {
                    Intent i = new Intent(Settings.this, AppInfo.class);
                    startActivity(i);
                }
                catch (Exception ignored)
                {

                }
            }
        });

        layCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try
                {
                    callUrl("https://majlisstore.com/returnpolicy");
                }
                catch (Exception ignored)
                {

                }

            }
        });

        layFaq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try
                {
                    callUrl("https://majlisstore.com/faq");
                }
                catch (Exception ignored)
                {

                }
            }
        });

        layPrivacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try
                {
                    callUrl("https://majlisstore.com/privacypolicy");
                }
                catch (Exception ignored)
                {

                }
            }
        });

        layReach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try
                {
                    callUrl("https://majlisstore.com/contact");
                }
                catch (Exception ignored)
                {

                }
            }
        });

        layReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try
                {
                    reviewApp();
                }
                catch (Exception ignored)
                {

                }
            }
        });

        layUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try
                {
                    updateApp();
                }
                catch (Exception ignored)
                {

                }
            }
        });

        layTerms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try
                {
                    callUrl("https://majlisstore.com/terms");
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

    private void callUrl(String url)
    {
        try
        {
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
        catch (Exception ignored)
        {

        }

    }

    private void reviewApp()
    {
        if(CheckNetwork.isInternetAvailable(this))
        {
            Context context = getApplicationContext();
            Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);

            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            try {
                startActivity(goToMarket);
            } catch (ActivityNotFoundException ignored) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=" + context.getPackageName())));
            }
        }
        else
        {
            Snackbar snackbar = Snackbar
                    .make(laySettings, "No Internet Connection", Snackbar.LENGTH_LONG);
            View sbView = snackbar.getView();
            sbView.setBackgroundResource(R.color.colorPrimaryDark);
            snackbar.show();}
    }

    private void updateApp()
    {
        if(CheckNetwork.isInternetAvailable(this))
        {
            try {
                PackageManager packageManager = this.getPackageManager();
                PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(),0);
                String currentVersion = packageInfo.versionName;
                new ForceUpdateAsync(currentVersion,Settings.this).execute();
            } catch (PackageManager.NameNotFoundException ignored) {
            }

        }
        else
        {
            Snackbar snackbar = Snackbar
                    .make(laySettings, "No Internet Connection", Snackbar.LENGTH_LONG);
            View sbView = snackbar.getView();
            sbView.setBackgroundResource(R.color.colorPrimaryDark);
            snackbar.show();
        }
    }

    public class ForceUpdateAsync extends AsyncTask<String, String, JSONObject> {
        private String latestVersion=null;
        private String currentVersion;
        private Context context;

        public ForceUpdateAsync(String currentVersion, Context context){
            this.currentVersion = currentVersion;
            this.context = context;
        }

        @Override
        protected JSONObject doInBackground(String... params) {

            try
            {
                latestVersion = Jsoup.connect("https://play.google.com/store/apps/details?id="+context.getPackageName()+"&hl=en")
                        .timeout(30000)
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("http://www.google.com")
                        .get()
                        .select(".hAyfc .htlgb")
                        .get(7)
                        .ownText();

            }
            catch (IOException ignored)
            {
            }
            return new JSONObject();
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            try
            {
                if(latestVersion!=null)
                {
                    if (!currentVersion.equalsIgnoreCase(latestVersion)) {
                        if (!(context instanceof SplashScreen)) {
                            if (!((Activity) context).isFinishing()) {
                                showForceUpdateDialog();
                            }
                        }
                    } else {
                        Snackbar snackbar = Snackbar
                                .make(laySettings, "No New Version Available", Snackbar.LENGTH_LONG);
                        View sbView = snackbar.getView();
                        sbView.setBackgroundResource(R.color.colorPrimaryDark);
                        snackbar.show();

                    }

                }

            }
            catch (Exception ignored)
            {

            }

        }

        public void showForceUpdateDialog() {

            final Dialog dialog_msg = new Dialog(Settings.this);
            dialog_msg.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog_msg.setContentView(R.layout.message);
            dialog_msg.setCancelable(false);
            dialog_msg.setCanceledOnTouchOutside(false);

            TextView tv1 = dialog_msg.findViewById(R.id.tv1);
            tv1.setText(context.getString(R.string.youAreNotUpdatedTitle));

            TextView tv2 = dialog_msg.findViewById(R.id.tv2);
            String textUpdate=context.getString(R.string.youAreNotUpdatedMessage) + " " + latestVersion + context.getString(R.string.youAreNotUpdatedMessage1);
            tv2.setText(textUpdate);

            TextView tv3 = dialog_msg.findViewById(R.id.tv3);
            tv3.setText(R.string.updateapp);

            tv3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try
                    {
                        dialog_msg.dismiss();
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + context.getPackageName())));
                    }
                    catch (Exception ignored)
                    {

                    }
                }
            });

            dialog_msg.show();

        }
    }


}
