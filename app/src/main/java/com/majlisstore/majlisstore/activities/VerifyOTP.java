package com.majlisstore.majlisstore.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;
import com.majlisstore.majlisstore.R;
import com.majlisstore.majlisstore.helpers.CheckNetwork;
import com.majlisstore.majlisstore.helpers.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class VerifyOTP extends AppCompatActivity {

    SessionManager session;

    LinearLayout layVerify;
    ImageView imgBack;
    TextView tvVerify1,tvVerify2,tvResend;
    EditText etOTP1,etOTP2,etOTP3,etOTP4;
    Button btnVerify;

    String type="",mobile="",name="",email="";

    Dialog dialog_pb;

    String token = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLocale();
        setContentView(R.layout.activity_verify_otp);

        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (!task.isSuccessful()) {
                    return;
                }

                if (task.getResult() != null) {
                    token = task.getResult().getToken();
                }
            }
        });

        session=new SessionManager(this);

        layVerify=findViewById(R.id.layVerify);

        imgBack=findViewById(R.id.imgBack);

        tvVerify1=findViewById(R.id.tvVerify1);
        tvVerify2=findViewById(R.id.tvVerify2);
        tvResend=findViewById(R.id.tvResend);

        etOTP1=findViewById(R.id.etOTP1);
        etOTP2=findViewById(R.id.etOTP2);
        etOTP3=findViewById(R.id.etOTP3);
        etOTP4=findViewById(R.id.etOTP4);

        btnVerify=findViewById(R.id.btnVerify);

        dialog_pb = new Dialog(this);
        dialog_pb.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_pb.setContentView(R.layout.progressbar);
        dialog_pb.setCancelable(false);
        dialog_pb.setCanceledOnTouchOutside(false);
        if(dialog_pb.getWindow()!=null)
        {
            dialog_pb.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        type=getIntent().getStringExtra("type");
        if(type!=null && type.equals("Signup"))
        {
            mobile=getIntent().getStringExtra("mobile");
            name=getIntent().getStringExtra("name");
            email=getIntent().getStringExtra("email");
        }
        else
        {
            mobile=getIntent().getStringExtra("mobile");

        }
        String txt_tvVerify2=getResources().getString(R.string.verify2);
        if(mobile!=null && mobile.length()>7)
        {
            String mobile1=mobile.substring(6,8);
            txt_tvVerify2=txt_tvVerify2+ mobile1;
        }
        tvVerify2.setText(txt_tvVerify2);

        etOTP1.requestFocus();

        final StringBuilder sb=new StringBuilder();
        etOTP1.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub


                if(sb.length()==0&etOTP1.length()==1)
                {
                    sb.append(s);
                    etOTP1.clearFocus();
                    etOTP2.requestFocus();
                    etOTP2.setCursorVisible(true);

                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

                if(sb.length()==1)
                {
                    sb.deleteCharAt(0);

                }

            }

            public void afterTextChanged(Editable s) {

            }
        });


        etOTP2.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if(sb.length()==0&etOTP2.length()==1)
                {
                    sb.append(s);
                    etOTP2.clearFocus();
                    etOTP3.requestFocus();
                    etOTP3.setCursorVisible(true);

                }
                else
                {
                    if(etOTP2.length()>0)
                    {
                        etOTP2.setText("");
                    }
                    else if(etOTP2.length()==0)
                    {
                        etOTP2.clearFocus();
                        etOTP1.requestFocus();
                    }
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                if(sb.length()==1)
                {

                    sb.deleteCharAt(0);

                }

            }

            public void afterTextChanged(Editable s) {

            }
        });


        etOTP3.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if(sb.length()==0&etOTP3.length()==1)
                {
                    sb.append(s);
                    etOTP3.clearFocus();
                    etOTP4.requestFocus();
                    etOTP4.setCursorVisible(true);

                }
                else
                {
                    if(etOTP3.length()>0)
                    {
                        etOTP3.setText("");
                    }
                    else if(etOTP3.length()==0)
                    {
                        etOTP3.clearFocus();
                        etOTP2.requestFocus();
                    }
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

                if(sb.length()==1)
                {

                    sb.deleteCharAt(0);
                }

            }

            public void afterTextChanged(Editable s) {

            }
        });

        etOTP4.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if(sb.length()==0&etOTP4.length()==1)
                {
                    sb.append(s);

                }
                else
                {
                    if(etOTP4.length()>0)
                    {
                        etOTP4.setText("");
                    }
                    else if(etOTP4.length()==0)
                    {
                        etOTP4.clearFocus();
                        etOTP3.requestFocus();
                    }
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

                if(sb.length()==1)
                {

                    sb.deleteCharAt(0);
                }

            }

            public void afterTextChanged(Editable s) {

            }
        });


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

        tvResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try
                {
                    if(CheckNetwork.isInternetAvailable(VerifyOTP.this))
                    {
                        resendOTP(mobile);
                    }
                    else
                    {
                        Snackbar snackbar=Snackbar.make(layVerify,"No Internet Connection",Snackbar.LENGTH_LONG);
                        View sbView= snackbar.getView();
                        sbView.setBackgroundResource(R.color.colorPrimaryDark);
                        snackbar.show();
                    }
                }
                catch (Exception ignored)
                {

                }
            }
        });

        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try
                {
                    String otp=etOTP1.getText().toString().trim()+
                            etOTP2.getText().toString().trim()+
                            etOTP3.getText().toString().trim()+
                            etOTP4.getText().toString().trim();
                    validate(otp);

                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(etOTP4.getWindowToken(),
                            InputMethodManager.RESULT_UNCHANGED_SHOWN);
                }
                catch (Exception ignored)
                {

                }
            }
        });

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

    private void setLocale() {
        Locale myLocale = new Locale("en");
        Locale.setDefault(myLocale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.locale = myLocale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
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
            imm.hideSoftInputFromWindow(etOTP4.getWindowToken(),
                    InputMethodManager.RESULT_UNCHANGED_SHOWN);
        }
        catch (Exception ignored)
        {

        }
    }

    private void validate(String otp)
    {
        if(otp.length()==0)
        {
            Snackbar snackbar=Snackbar.make(layVerify,"Enter OTP",Snackbar.LENGTH_LONG);
            View sbView= snackbar.getView();
            sbView.setBackgroundResource(R.color.colorPrimaryDark);
            snackbar.show();
        }
        else if(otp.length()<4)
        {
            Snackbar snackbar=Snackbar.make(layVerify,"Invalid OTP",Snackbar.LENGTH_LONG);
            View sbView= snackbar.getView();
            sbView.setBackgroundResource(R.color.colorPrimaryDark);
            snackbar.show();
        }
        else
        {
            if(CheckNetwork.isInternetAvailable(VerifyOTP.this))
            {
                if(type.equals("Signup"))
                {
                    verifysignup(otp);
                }
                else
                {
                    verifysignin(otp);
                }
            }
            else
            {
                Snackbar snackbar=Snackbar.make(layVerify,"No Internet Connection",Snackbar.LENGTH_LONG);
                View sbView= snackbar.getView();
                sbView.setBackgroundResource(R.color.colorPrimaryDark);
                snackbar.show();
            }

        }
    }

    private void resendOTP(final String mobile)
    {
        if(dialog_pb!=null && !dialog_pb.isShowing())
        {
            dialog_pb.show();
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, getResources().getString(R.string.url_verifyresend), new Response.Listener<String>()
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
                        if (response.contains(getResources().getString(R.string.response_verify1)))
                        {
                            Snackbar snackbar=Snackbar.make(layVerify,"OTP Sent for Verification",Snackbar.LENGTH_LONG);
                            View sbView= snackbar.getView();
                            sbView.setBackgroundResource(R.color.colorPrimary);
                            snackbar.show();
                        }
                        else
                        {
                            Snackbar snackbar=Snackbar.make(layVerify,"Something went wrong. Please try again.",Snackbar.LENGTH_LONG);
                            View sbView= snackbar.getView();
                            sbView.setBackgroundResource(R.color.colorPrimaryDark);
                            snackbar.show();
                        }
                    }
                    catch (JSONException ignored)
                    {

                    }

                }
                else
                {
                    Snackbar snackbar=Snackbar.make(layVerify,"Something went wrong. Please try again.",Snackbar.LENGTH_LONG);
                    View sbView= snackbar.getView();
                    sbView.setBackgroundResource(R.color.colorPrimaryDark);
                    snackbar.show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (dialog_pb.isShowing())
                {
                    dialog_pb.dismiss();
                }

                Snackbar snackbar=Snackbar.make(layVerify,"Something went wrong. Please try again.",Snackbar.LENGTH_LONG);
                View sbView= snackbar.getView();
                sbView.setBackgroundResource(R.color.colorPrimaryDark);
                snackbar.show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(getResources().getString(R.string.param_verifyresend), mobile);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void verifysignin(final String otp)
    {
        if(dialog_pb!=null && !dialog_pb.isShowing())
        {
            dialog_pb.show();
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, getResources().getString(R.string.url_verifysignin), new Response.Listener<String>()
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
                        if (response.contains(getResources().getString(R.string.response_verify1)))
                        {
                            ArrayList<HashMap<String, String>> list = new ArrayList<>();

                            JSONArray jsonArr = jsonObj.getJSONArray("Result");

                            JSONObject userdata = jsonArr.getJSONObject(0);
                            String customer_id = URLDecoder.decode(userdata.getString("customer_id"), "utf-8");
                            String customer_name = URLDecoder.decode(userdata.getString("customer_name"), "utf-8");
                            String customer_mob = URLDecoder.decode(userdata.getString("customer_mob"), "utf-8");
                            String customer_email = URLDecoder.decode(userdata.getString("customer_email"), "utf-8");
                            String countarray = URLDecoder.decode(userdata.getString("countarray"), "utf-8");

                            HashMap<String, String> news = new HashMap<>();
                            news.put("customer_id", customer_id);
                            news.put("customer_name", customer_name);
                            news.put("customer_mob", customer_mob);
                            news.put("customer_email", customer_email);
                            news.put("countarray", countarray);
                            list.add(news);

                            Gson gson = new Gson();
                            List<HashMap<String, String>> textList = new ArrayList<>(list);
                            String jsonText = gson.toJson(textList);

                            session.createLoginSession(jsonText);
                            session.setCartCount(countarray);

                            Intent i =new Intent(VerifyOTP.this,Dashboard.class);
                            startActivity(i);
                            finish();

                        }
                        else if(response.contains(getResources().getString(R.string.response_verify2)))
                        {
                            showAlert(getResources().getString(R.string.response_verify2));
                        }
                        else
                        {
                            showAlert(getResources().getString(R.string.response_verify3));
                        }
                    }
                    catch (Exception ignored)
                    {

                    }
                }
                else
                {
                    showAlert(getResources().getString(R.string.response_verify3));
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (dialog_pb.isShowing())
                {
                    dialog_pb.dismiss();
                }

                showAlert(getResources().getString(R.string.response_verify3));
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(getResources().getString(R.string.param_verifysignin1), mobile);
                params.put(getResources().getString(R.string.param_verifysignin2), otp);
                params.put(getResources().getString(R.string.param_verifysignin3), token);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    private void verifysignup(final String otp) {
        if (dialog_pb != null && !dialog_pb.isShowing()) {
            dialog_pb.show();
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, getResources().getString(R.string.url_verifysignup), new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (dialog_pb.isShowing()) {
                    dialog_pb.dismiss();
                }
                if (s != null && s.length() > 0)
                {
                    try {
                        JSONObject jsonObj=new JSONObject(s);
                        String response = jsonObj.getString(getResources().getString(R.string.response));
                        if (response.contains(getResources().getString(R.string.response_verify1)))
                        {
                            ArrayList<HashMap<String, String>> list = new ArrayList<>();

                            JSONArray jsonArr = jsonObj.getJSONArray("Result");

                            JSONObject userdata = jsonArr.getJSONObject(0);
                            String customer_id = URLDecoder.decode(userdata.getString("customer_id"), "utf-8");
                            String customer_name = URLDecoder.decode(userdata.getString("customer_name"), "utf-8");
                            String customer_mob = URLDecoder.decode(userdata.getString("customer_mob"), "utf-8");
                            String customer_email = URLDecoder.decode(userdata.getString("customer_email"), "utf-8");
                            String countarray = URLDecoder.decode(userdata.getString("countarray"), "utf-8");

                            HashMap<String, String> news = new HashMap<>();
                            news.put("customer_id", customer_id);
                            news.put("customer_name", customer_name);
                            news.put("customer_mob", customer_mob);
                            news.put("customer_email", customer_email);
                            news.put("countarray", countarray);
                            list.add(news);

                            Gson gson = new Gson();
                            List<HashMap<String, String>> textList = new ArrayList<>(list);
                            String jsonText = gson.toJson(textList);

                            session.createLoginSession(jsonText);
                            session.setCartCount(countarray);

                            Intent i =new Intent(VerifyOTP.this,Dashboard.class);
                            startActivity(i);
                            finish();
                        }
                        else if(response.contains(getResources().getString(R.string.response_verify2)))
                        {
                            showAlert(getResources().getString(R.string.response_verify2));
                        }
                        else
                        {
                            showAlert(getResources().getString(R.string.response_verify3));
                        }
                    }
                    catch (Exception ignored)
                    {

                    }

                } else {
                    showAlert(getResources().getString(R.string.response_verify3));
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (dialog_pb.isShowing()) {
                    dialog_pb.dismiss();
                }

                showAlert(getResources().getString(R.string.response_verify3));
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(getResources().getString(R.string.param_verifysignup1), mobile);
                params.put(getResources().getString(R.string.param_verifysignup2), otp);
                params.put(getResources().getString(R.string.param_verifysignup3), token);
                params.put(getResources().getString(R.string.param_verifysignup4), name);
                params.put(getResources().getString(R.string.param_verifysignup5), email);
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
            String msg1=getResources().getString(R.string.msg_verify1);
            String msg2=getResources().getString(R.string.msg_verify2);
            if(s.contains(getResources().getString(R.string.response_verify2)))
            {
                msg2=getResources().getString(R.string.msg_verify3);
            }


            final Dialog dialog_msg = new Dialog(VerifyOTP.this);
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
                        if(s.contains(getResources().getString(R.string.response_verify2)))
                        {
                            etOTP1.setText("");
                            etOTP2.setText("");
                            etOTP3.setText("");
                            etOTP4.setText("");
                            etOTP1.requestFocus();
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

}
