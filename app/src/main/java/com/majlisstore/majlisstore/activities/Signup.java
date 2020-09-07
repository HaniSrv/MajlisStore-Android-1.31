package com.majlisstore.majlisstore.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
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

public class Signup extends AppCompatActivity {


    LinearLayout laySignup;
    ImageView imgBack;
    TextView tvSignup,tvTerms;
    EditText etName,etPhone,etEmail;
    Button btnSignup;

    Dialog dialog_pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLocale();
        setContentView(R.layout.activity_signup);


        laySignup=findViewById(R.id.laySignup);

        imgBack=findViewById(R.id.imgBack);

        tvSignup=findViewById(R.id.tvSignup);
        tvTerms=findViewById(R.id.tvTerms);

        etName=findViewById(R.id.etName);
        etPhone=findViewById(R.id.etPhone);
        etEmail=findViewById(R.id.etEmail);

        btnSignup=findViewById(R.id.btnSignup);

        dialog_pb = new Dialog(this);
        dialog_pb.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_pb.setContentView(R.layout.progressbar);
        dialog_pb.setCancelable(false);
        dialog_pb.setCanceledOnTouchOutside(false);
        if(dialog_pb.getWindow()!=null)
        {
            dialog_pb.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        try
        {
            String first = "By creating an account, you agree with our ";
            String second = "<font color='#007AFF'>Terms of Use</font>";
            tvTerms.setText(Html.fromHtml(first + second));
        }
        catch (Exception ignored)
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

        tvTerms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try
                {
                    callUrl(getResources().getString(R.string.termsconditionurl));
                }
                catch (Exception ignored)
                {
                }
            }
        });

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try
                {
                    String name=etName.getText().toString().trim();
                    String mobile=etPhone.getText().toString().trim();
                    String email=etEmail.getText().toString().trim();
                    validate(name,mobile,email);

                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(etEmail.getWindowToken(),
                            InputMethodManager.RESULT_UNCHANGED_SHOWN);
                }
                catch (Exception ignored)
                {

                }
            }
        });

    }

    private void validate(String name, String mobile, String email)
    {
        String emailPattern = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        if(name.length()==0)
        {
            Snackbar snackbar=Snackbar.make(laySignup,"Enter Name",Snackbar.LENGTH_LONG);
            View sbView= snackbar.getView();
            sbView.setBackgroundResource(R.color.colorPrimaryDark);
            snackbar.show();
        }
        else if(mobile.length()==0)
        {
            Snackbar snackbar=Snackbar.make(laySignup,"Enter Mobile Number",Snackbar.LENGTH_LONG);
            View sbView= snackbar.getView();
            sbView.setBackgroundResource(R.color.colorPrimaryDark);
            snackbar.show();
        }
        else if(mobile.length()<8)
        {
            Snackbar snackbar=Snackbar.make(laySignup,"Invalid Mobile Number",Snackbar.LENGTH_LONG);
            View sbView= snackbar.getView();
            sbView.setBackgroundResource(R.color.colorPrimaryDark);
            snackbar.show();
        }
        else if(email.length()==0)
        {
            Snackbar snackbar=Snackbar.make(laySignup,"Enter E-mail",Snackbar.LENGTH_LONG);
            View sbView= snackbar.getView();
            sbView.setBackgroundResource(R.color.colorPrimaryDark);
            snackbar.show();
        }
        else if(!email.matches(emailPattern))
        {
            Snackbar snackbar=Snackbar.make(laySignup,"Invalid E-mail",Snackbar.LENGTH_LONG);
            View sbView= snackbar.getView();
            sbView.setBackgroundResource(R.color.colorPrimaryDark);
            snackbar.show();
        }
        else
        {
            if(CheckNetwork.isInternetAvailable(Signup.this))
            {
                signup(name,mobile,email);
            }
            else
            {
                Snackbar snackbar=Snackbar.make(laySignup,"No Internet Connection",Snackbar.LENGTH_LONG);
                View sbView= snackbar.getView();
                sbView.setBackgroundResource(R.color.colorPrimaryDark);
                snackbar.show();
            }

        }
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
            imm.hideSoftInputFromWindow(etEmail.getWindowToken(),
                    InputMethodManager.RESULT_UNCHANGED_SHOWN);
        }
        catch (Exception ignored)
        {

        }
    }

    private void signup(final String name, final String mobile, final String email)
    {
        if(dialog_pb!=null && !dialog_pb.isShowing())
        {
            dialog_pb.show();
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, getResources().getString(R.string.url_signup), new Response.Listener<String>()
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
                        if (response.contains(getResources().getString(R.string.response_signup1)))
                        {
                            Intent i = new Intent(Signup.this, VerifyOTP.class);
                            i.putExtra("type", "Signup");
                            i.putExtra("mobile", mobile);
                            i.putExtra("name", name);
                            i.putExtra("email", email);
                            startActivity(i);
                        }
                        else if (response.contains(getResources().getString(R.string.response_signup2)))
                        {
                            showAlert(getResources().getString(R.string.response_signup2));
                        }
                        else
                        {
                            showAlert(getResources().getString(R.string.response_signup3));
                        }
                    }
                    catch (JSONException ignored)
                    {

                    }
                }
                else
                {
                    showAlert(getResources().getString(R.string.response_signup3));
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (dialog_pb.isShowing())
                {
                    dialog_pb.dismiss();
                }
                showAlert(getResources().getString(R.string.response_signup3));
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(getResources().getString(R.string.param_signup1), name);
                params.put(getResources().getString(R.string.param_signup2), mobile);
                params.put(getResources().getString(R.string.param_signup3), email);
                params.put(getResources().getString(R.string.param_signup4), "");
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
            String msg1=getResources().getString(R.string.msg_signup1);
            String msg2=getResources().getString(R.string.msg_signup2);
            if(s.contains(getResources().getString(R.string.response_signup2)))
            {
                msg2=getResources().getString(R.string.msg_signup3);
            }

            final Dialog dialog_msg = new Dialog(Signup.this);
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
