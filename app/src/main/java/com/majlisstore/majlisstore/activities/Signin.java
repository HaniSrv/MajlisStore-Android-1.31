package com.majlisstore.majlisstore.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.majlisstore.majlisstore.R;
import com.majlisstore.majlisstore.helpers.CheckNetwork;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Signin extends AppCompatActivity {

    LinearLayout laySignin;
    ImageView imgBack;
    TextView tvSignin,tvCreate;
    EditText etPhone;
    Button btnSend;
    Dialog dialog_pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLocale();
        setContentView(R.layout.activity_signin);

        laySignin=findViewById(R.id.laySignin);

        imgBack=findViewById(R.id.imgBack);

        tvSignin=findViewById(R.id.tvSignin);
        tvCreate=findViewById(R.id.tvCreate);

        etPhone=findViewById(R.id.etPhone);

        btnSend=findViewById(R.id.btnSend);

        dialog_pb = new Dialog(this);
        dialog_pb.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_pb.setContentView(R.layout.progressbar);
        dialog_pb.setCancelable(false);
        dialog_pb.setCanceledOnTouchOutside(false);
        if(dialog_pb.getWindow()!=null)
        {
            dialog_pb.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
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

        tvCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try
                {
                    etPhone.setText("");
                    Intent i=new Intent(Signin.this,Signup.class);
                    startActivity(i);
                }
                catch (Exception ignored)
                {

                }
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try
                {
                    String mobile=etPhone.getText().toString().trim();
                    validate(mobile);

                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(etPhone.getWindowToken(),
                            InputMethodManager.RESULT_UNCHANGED_SHOWN);
                }
                catch (Exception ignored)
                {

                }
            }
        });

    }

    private void validate(String mobile)
    {
        if(mobile.length()==0)
        {
            Snackbar snackbar=Snackbar.make(laySignin,"Enter Mobile Number",Snackbar.LENGTH_LONG);
            View sbView= snackbar.getView();
            sbView.setBackgroundResource(R.color.colorPrimaryDark);
            snackbar.show();
        }
        else if(mobile.length()<8)
        {
            Snackbar snackbar=Snackbar.make(laySignin,"Invalid Mobile Number",Snackbar.LENGTH_LONG);
            View sbView= snackbar.getView();
            sbView.setBackgroundResource(R.color.colorPrimaryDark);
            snackbar.show();
        } else if(mobile.length()>8)
        {
            Snackbar snackbar=Snackbar.make(laySignin,"Invalid Mobile Number",Snackbar.LENGTH_LONG);
            View sbView= snackbar.getView();
            sbView.setBackgroundResource(R.color.colorPrimaryDark);
            snackbar.show();
        }
        else
        {
            if(CheckNetwork.isInternetAvailable(Signin.this))
            {
                signin(mobile);
            }
            else
            {
                Snackbar snackbar=Snackbar.make(laySignin,"No Internet Connection",Snackbar.LENGTH_LONG);
                View sbView= snackbar.getView();
                sbView.setBackgroundResource(R.color.colorPrimaryDark);
                snackbar.show();
            }

        }
    }

    private void signin(final String mobile)
    {
        if(dialog_pb!=null && !dialog_pb.isShowing())
        {
            dialog_pb.show();
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, getResources().getString(R.string.url_signin), new Response.Listener<String>()
        {
            @Override
            public void onResponse(String s)
            {
                if(dialog_pb.isShowing())
                {
                    dialog_pb.dismiss();
                }

                if(s!=null && s.length()>0)
                {
                    try
                    {
                        JSONObject jsonObj=new JSONObject(s);
                        String response = jsonObj.getString(getResources().getString(R.string.response));
                        if (response.contains(getResources().getString(R.string.response_signin1)))
                        {
                            Intent i=new Intent(Signin.this,VerifyOTP.class);
                            i.putExtra("type","Signin");
                            i.putExtra("mobile",mobile);
                            startActivity(i);
                        }
                        else if(response.contains(getResources().getString(R.string.response_signin2)))
                        {
                            showAlert(getResources().getString(R.string.response_signin2));
                        }
                        else if(response.contains(getResources().getString(R.string.response_signin3)))
                        {
                            showAlert(getResources().getString(R.string.response_signin3));
                        }
                        else
                        {
                            showAlert(getResources().getString(R.string.response_signin4));
                        }
                    }
                    catch (JSONException ignored)
                    {

                    }
                }
                else
                {
                    showAlert(getResources().getString(R.string.response_signin4));
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (dialog_pb.isShowing())
                {
                    dialog_pb.dismiss();
                }
                showAlert(getResources().getString(R.string.response_signin4));
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(getResources().getString(R.string.param_signin),mobile);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    private void showAlert(final String s)
    {
        try
        {
            String msg1=getResources().getString(R.string.msg_signin1);
            String msg2=getResources().getString(R.string.msg_signin2);
            if(s.contains(getResources().getString(R.string.response_signin2)))
            {
                msg2=getResources().getString(R.string.msg_signin3);
            }
            else if(s.contains(getResources().getString(R.string.response_signin3)))
            {
                msg2=getResources().getString(R.string.msg_signin4);
            }

            final Dialog dialog_msg = new Dialog(Signin.this);
            dialog_msg.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog_msg.setContentView(R.layout.message);
            dialog_msg.setCancelable(false);
            dialog_msg.setCanceledOnTouchOutside(false);

            TextView tv1 = (TextView) dialog_msg.findViewById(R.id.tv1);
            tv1.setText(msg1);

            TextView tv2 = (TextView) dialog_msg.findViewById(R.id.tv2);
            tv2.setText(msg2);

            TextView tv3 = (TextView) dialog_msg.findViewById(R.id.tv3);
            tv3.setText(R.string.ok);

            tv3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try
                    {
                        dialog_msg.dismiss();
                        if(s.contains(getResources().getString(R.string.response_signin3)))
                        {
                            etPhone.setText("");
                            Intent i=new Intent(Signin.this,Signup.class);
                            startActivity(i);
                        }
                    }
                    catch (Exception ignored)
                    {

                    }
                }
            });

            dialog_msg.show();
        }
        catch (Exception ignored)
        {

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setLocale();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if(dialog_pb.isShowing())
            {
                dialog_pb.dismiss();
            }
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(etPhone.getWindowToken(),
                    InputMethodManager.RESULT_UNCHANGED_SHOWN);
        }
        catch (Exception ignored)
        {

        }
    }

    private void setLocale() {
        Locale myLocale = new Locale("en");
        Locale.setDefault(myLocale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.locale = myLocale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
    }
}
