package com.majlisstore.majlisstore.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import com.google.gson.Gson;
import com.majlisstore.majlisstore.R;
import com.majlisstore.majlisstore.helpers.CheckNetwork;
import com.majlisstore.majlisstore.helpers.SessionManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ContactUs extends AppCompatActivity {

    SessionManager sessionManager;
    String customer_id = "0";

    Dialog dialog_pb;

    ImageView imgBack,imgSearch,cartid;
    TextView tvCartcount;

    LinearLayout layContact;
    EditText etName,etEmail,etComment;
    Button btnSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLocale();
        setContentView(R.layout.activity_contact_us);

        sessionManager=new SessionManager(this);

        dialog_pb = new Dialog(this);
        dialog_pb.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_pb.setContentView(R.layout.progressbar);
        if(dialog_pb.getWindow()!=null)
        {
            dialog_pb.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        dialog_pb.setCancelable(false);
        dialog_pb.setCanceledOnTouchOutside(false);

        imgBack=findViewById(R.id.imgBack);
        imgSearch=findViewById(R.id.searchid);
        cartid=findViewById(R.id.cartid);
        tvCartcount=findViewById(R.id.tvCartcount);

        layContact=findViewById(R.id.layContact);

        etName=findViewById(R.id.etName);
        etEmail=findViewById(R.id.etEmail);
        etComment=findViewById(R.id.etComment);

        btnSend=findViewById(R.id.btnSend);

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

        cartid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    if(sessionManager.isLoggedIn())
                    {

                        Intent i = new Intent(ContactUs.this, Cart.class);
                        startActivity(i);
                    }
                    else
                    {
                        Snackbar snackbar = Snackbar
                                .make(layContact, "You are not logged in", Snackbar.LENGTH_LONG);
                        View sbView = snackbar.getView();
                        sbView.setBackgroundResource(R.color.colorPrimaryDark);
                        snackbar.show();
                    }

                }
                catch (Exception ignored)
                {

                }

            }
        });

        imgSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try
                {
                    Intent i = new Intent(ContactUs.this, Search.class);
                    startActivity(i);
                }
                catch (Exception ignored)
                {
                }
            }
        });


        if(sessionManager.isLoggedIn())
        {
            getuserdetails();
            getCartCount();
        }

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    String name = etName.getText().toString();
                    String email = etEmail.getText().toString();
                    String comment = etComment.getText().toString();

                    String emailPattern = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

                    if (name.length()==0)
                    {
                        Snackbar snackbar = Snackbar.make(layContact, "Please Enter Name", Snackbar.LENGTH_LONG);
                        View sbView = snackbar.getView();
                        sbView.setBackgroundResource(R.color.colorPrimaryDark);
                        snackbar.show();
                    }
                    else if (email.length()==0){
                        Snackbar snackbar = Snackbar.make(layContact, "Please Enter E-mail", Snackbar.LENGTH_LONG);
                        View sbView = snackbar.getView();
                        sbView.setBackgroundResource(R.color.colorPrimaryDark);
                        snackbar.show();
                    } else if(!email.matches(emailPattern))
                    {
                        Snackbar snackbar=Snackbar.make(layContact,"Invalid E-mail",Snackbar.LENGTH_LONG);
                        View sbView= snackbar.getView();
                        sbView.setBackgroundResource(R.color.colorPrimaryDark);
                        snackbar.show();
                    }
                    else  if (comment.length()==0)
                    {
                        Snackbar snackbar = Snackbar.make(layContact, "Please Enter Comments", Snackbar.LENGTH_LONG);
                        View sbView = snackbar.getView();
                        sbView.setBackgroundResource(R.color.colorPrimaryDark);
                        snackbar.show();
                    }
                    else {

                        if (CheckNetwork.isInternetAvailable(ContactUs.this)) {
                            cotactUs(name, email, comment);
                        } else
                        {
                            Snackbar snackbar = Snackbar.make(layContact, "No Internet Connection", Snackbar.LENGTH_LONG);
                            View sbView = snackbar.getView();
                            sbView.setBackgroundResource(R.color.colorPrimaryDark);
                            snackbar.show();
                        }

                    }

                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(etName.getWindowToken(),
                            InputMethodManager.RESULT_UNCHANGED_SHOWN);

                }
                catch (Exception ignored)
                {

                }
            }
        });

    }

    private void getuserdetails()
    {
        Gson gson = new Gson();
        HashMap<String, String> user = sessionManager.getLoginSession();
        String json = user.get(SessionManager.KEY_LOGIN);
        ArrayList alist = gson.fromJson(json, ArrayList.class);

        JSONArray jsonArrA = new JSONArray(alist);
        try
        {
            JSONObject userdata = jsonArrA.getJSONObject(0);
            customer_id = userdata.getString("customer_id");
        }
        catch (Exception ignored)
        {
        }
    }

    private void getCartCount()
    {
        if (!sessionManager.getCartQuantity().equals("") && !sessionManager.getCartQuantity().equals("0"))
        {
            if (Integer.parseInt(sessionManager.getCartQuantity()) < 10)
            {
                tvCartcount.setVisibility(View.VISIBLE);
                tvCartcount.setText(sessionManager.getCartQuantity());
            }
            else
            {
                tvCartcount.setVisibility(View.VISIBLE);
                tvCartcount.setText("9+");
            }
        }
        else
        {
            tvCartcount.setVisibility(View.INVISIBLE);
        }

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

    private void cotactUs(String name, String email, String message)
    {
        if (!dialog_pb.isShowing() && dialog_pb!=null) {
            dialog_pb.show();
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, getResources().getString(R.string.contact_us_url), new Response.Listener<String>()
        {
            @Override
            public void onResponse(String s)
            {

                if(dialog_pb.isShowing())
                {
                    dialog_pb.dismiss();
                }
                if (s != null && s.length()>0)
                {
                    try
                    {
                        JSONObject jsonObj = new JSONObject(s);
                        String response = jsonObj.getString("Response");
                        if (response.contains("Inserted"))
                        {
                            etName.setText("");
                            etEmail.setText("");
                            etComment.setText("");

                            Snackbar snackbar = Snackbar
                                    .make(layContact, "Added Successfully", Snackbar.LENGTH_LONG);
                            View sbView = snackbar.getView();
                            sbView.setBackgroundResource(R.color.colorPrimaryDark);
                            snackbar.show();
                        }
                        else
                        {
                            Snackbar snackbar = Snackbar
                                    .make(layContact, "Please Try Again.", Snackbar.LENGTH_LONG);
                            View sbView = snackbar.getView();
                            sbView.setBackgroundResource(R.color.colorPrimaryDark);
                            snackbar.show();
                        }
                    }
                    catch (Exception ignored)
                    {

                    }
                }
                else
                {
                    Snackbar snackbar = Snackbar
                            .make(layContact, "Something went wrong!", Snackbar.LENGTH_LONG);
                    View sbView = snackbar.getView();
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
                Snackbar snackbar = Snackbar
                        .make(layContact, "Something went wrong!", Snackbar.LENGTH_LONG);
                View sbView = snackbar.getView();
                sbView.setBackgroundResource(R.color.colorPrimaryDark);
                snackbar.show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("name",name);
                params.put("email",email);
                params.put("message",message);
                params.put("type", "1");
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
