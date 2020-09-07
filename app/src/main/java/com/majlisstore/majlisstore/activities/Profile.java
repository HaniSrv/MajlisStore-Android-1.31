package com.majlisstore.majlisstore.activities;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
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

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Profile extends AppCompatActivity {

    SessionManager session;

    Dialog dialog_pb;

    LinearLayout layProfile;
    ImageView imgBack;

    TextView txt1,txt3,txt5,txt6;
    EditText txt2,txt4;
    TextView updatetv;

    String customer_id,customer_name,customer_mob,customer_email,countarray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        session=new SessionManager(this);

        dialog_pb = new Dialog(this);
        dialog_pb.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_pb.setContentView(R.layout.progressbar);
        if(dialog_pb.getWindow()!=null)
        {
            dialog_pb.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        dialog_pb.setCancelable(false);
        dialog_pb.setCanceledOnTouchOutside(false);

        layProfile= findViewById(R.id.layProfile);
        imgBack= findViewById(R.id.imgBack);
        txt1= findViewById(R.id.txt1);
        txt2= findViewById(R.id.txt2);
        txt3= findViewById(R.id.txt3);
        txt4= findViewById(R.id.txt4);
        txt5= findViewById(R.id.txt5);
        txt6= findViewById(R.id.txt6);
        updatetv=findViewById(R.id.update);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    onBackPressed();
                }
                catch (Exception ignored)
                {

                }
            }
        });

        if(session.isLoggedIn())
        {
            getuserdetails();
        }

        if(CheckNetwork.isInternetAvailable(this))
        {
            profileview();
        }
        else
        {
            Snackbar snackbar = Snackbar.make(layProfile, "No Internet Connection", Snackbar.LENGTH_LONG);
            View sbView = snackbar.getView();
            sbView.setBackgroundResource(R.color.colorPrimaryDark);
            snackbar.show();
        }



        updatetv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    String customer_name1 = txt2.getText().toString();
                    String customer_email1 = txt4.getText().toString();

                    String emailPattern = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

                    if (customer_name1.isEmpty()){

                        Snackbar snackbar = Snackbar
                                .make(layProfile, "Please Enter Your Name!", Snackbar.LENGTH_LONG);
                        View sbView = snackbar.getView();
                        sbView.setBackgroundResource(R.color.colorPrimaryDark);
                        snackbar.show();

                    }else if (customer_email1.isEmpty()){

                        Snackbar snackbar = Snackbar
                                .make(layProfile, "Please Enter Your E-mail id!", Snackbar.LENGTH_LONG);
                        View sbView = snackbar.getView();
                        sbView.setBackgroundResource(R.color.colorPrimaryDark);
                        snackbar.show();

                    }else if(!customer_email1.matches(emailPattern)) {

                        Snackbar snackbar=Snackbar.make(layProfile,"Invalid E-mail",Snackbar.LENGTH_LONG);
                        View sbView= snackbar.getView();
                        sbView.setBackgroundResource(R.color.colorPrimaryDark);
                        snackbar.show();

                    }else
                    {
                        if(CheckNetwork.isInternetAvailable(Profile.this))
                        {
                            updateprofile(customer_name1,customer_email1);
                        }
                        else
                        {
                            Snackbar snackbar = Snackbar.make(layProfile, "No Internet Connection", Snackbar.LENGTH_LONG);
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
        });
    }


    private void getuserdetails()
    {
        Gson gson = new Gson();
        HashMap<String, String> user = session.getLoginSession();
        String json = user.get(SessionManager.KEY_LOGIN);
        ArrayList alist = gson.fromJson(json, ArrayList.class);

        JSONArray jsonArrA = new JSONArray(alist);
        try
        {
            JSONObject userdata = jsonArrA.getJSONObject(0);
            customer_id = userdata.getString("customer_id");
            customer_name = userdata.getString("customer_name");
            customer_mob = userdata.getString("customer_mob");
            customer_email = userdata.getString("customer_email");
            countarray = userdata.getString("countarray");

            txt2.setText(customer_name);
            txt4.setText(customer_email);
            txt6.setText(customer_mob);

        } catch (Exception ignored) {}
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void updateprofile(String customerName, String customerEmail) {

        if (!dialog_pb.isShowing() && dialog_pb != null) {
            dialog_pb.show();
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, getResources().getString(R.string.url_updateprofile), new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {

                if (dialog_pb.isShowing()) {
                    dialog_pb.dismiss();
                }

                if(s!=null && s.length()>0)
                {
                    try
                    {
                        JSONObject jsonObj=new JSONObject(s);
                        String response = jsonObj.getString("Response");
                        if (response.contains("Success"))
                        {
                            String cart_count=session.getCartQuantity();
                            ArrayList<HashMap<String, String>> list = new ArrayList<>();
                            HashMap<String, String> news = new HashMap<>();
                            news.put("customer_id", customer_id);
                            news.put("customer_name", customerName);
                            news.put("customer_mob", customer_mob);
                            news.put("customer_email", customerEmail);
                            news.put("countarray", cart_count);
                            list.add(news);

                            Gson gson = new Gson();
                            List<HashMap<String, String>> textList = new ArrayList<>(list);
                            String jsonText = gson.toJson(textList);

                            session.createLoginSession(jsonText);
                            session.setCartCount(cart_count);

                            getuserdetails();
                        }
                    }
                    catch (Exception ignored)
                    {
                    }

                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (dialog_pb.isShowing()) {
                    dialog_pb.dismiss();
                }

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(getResources().getString(R.string.param_customerid), customer_id);
                params.put(getResources().getString(R.string.param_fullname), customerName);
                params.put(getResources().getString(R.string.param_email), customerEmail);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public  void  profileview() {

        if (!dialog_pb.isShowing() && dialog_pb != null) {
            dialog_pb.show();
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, getResources().getString(R.string.url_viewprofile), new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {

                if (dialog_pb.isShowing()) {
                    dialog_pb.dismiss();
                }

                if(s!=null && s.length()>0)
                {
                    try
                    {
                        JSONObject jsonObj=new JSONObject(s);
                        String response = jsonObj.getString("Response");
                        if (response.contains("Success"))
                        {
                            JSONArray jsonArr = jsonObj.getJSONArray("Result");
                            JSONObject userdata = jsonArr.getJSONObject(0);
                            String customer_id = URLDecoder.decode(userdata.getString("customer_id"), "utf-8");
                            String customer_name = URLDecoder.decode(userdata.getString("customer_name"), "utf-8");
                            String customer_mob = URLDecoder.decode(userdata.getString("customer_mob"), "utf-8");
                            String customer_email = URLDecoder.decode(userdata.getString("customer_email"), "utf-8");

                            String cart_count=session.getCartQuantity();
                            ArrayList<HashMap<String, String>> list = new ArrayList<>();
                            HashMap<String, String> news = new HashMap<>();
                            news.put("customer_id", customer_id);
                            news.put("customer_name", customer_name);
                            news.put("customer_mob", customer_mob);
                            news.put("customer_email", customer_email);
                            news.put("countarray", cart_count);
                            list.add(news);
                            Gson gson = new Gson();
                            List<HashMap<String, String>> textList = new ArrayList<>(list);
                            String jsonText = gson.toJson(textList);

                            session.createLoginSession(jsonText);
                            session.setCartCount(cart_count);

                            getuserdetails();
                        }
                    }
                    catch (Exception ignored)
                    {
                    }

                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (dialog_pb.isShowing()) {
                    dialog_pb.dismiss();
                }

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(getResources().getString(R.string.param_customerid), customer_id);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

}

