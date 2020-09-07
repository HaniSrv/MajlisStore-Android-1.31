package com.majlisstore.majlisstore.activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.majlisstore.majlisstore.R;
import com.majlisstore.majlisstore.adapters.MyNotificationsAdapter;
import com.majlisstore.majlisstore.helpers.CheckNetwork;
import com.majlisstore.majlisstore.helpers.SessionManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Notifications extends AppCompatActivity {

    SessionManager session;

    Dialog dialog_pb;

    LinearLayout lay11,layMain;
    ImageView imgBack,imgSearch,cartid;
    TextView tvCartcount;
    LinearLayout lay2;
    ImageView noimg;
    TextView tv1,tv2;

    RecyclerView rv_id;

    String customer_id="0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        session=new SessionManager(Notifications.this);

        dialog_pb = new Dialog(this);
        dialog_pb.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_pb.setContentView(R.layout.progressbar);
        if(dialog_pb.getWindow()!=null)
        {
            dialog_pb.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        dialog_pb.setCancelable(false);
        dialog_pb.setCanceledOnTouchOutside(false);

        lay11=findViewById(R.id.lay11);
        layMain = findViewById(R.id.layMain);
        imgBack=findViewById(R.id.imgBack);
        imgSearch=findViewById(R.id.searchid);
        cartid=findViewById(R.id.cartid);
        tvCartcount=findViewById(R.id.tvCartcount);


        lay2=findViewById(R.id.lay2);
        noimg=findViewById(R.id.noimg);
        tv1=findViewById(R.id.tv1);
        tv2=findViewById(R.id.tv2);

        rv_id = findViewById(R.id.rv_id);
        rv_id.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutRv =  new LinearLayoutManager(Notifications.this, RecyclerView.VERTICAL,false);
        rv_id.setLayoutManager(layoutRv);

        if (session.isLoggedIn())
        {
            getuserdetails();
            getCartCount();

        }

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

                    if(session.isLoggedIn())
                    {
                        Intent i = new Intent(Notifications.this, Cart.class);
                        startActivity(i);
                    }
                    else
                    {
                        Snackbar snackbar = Snackbar
                                .make(layMain, "You are not logged in", Snackbar.LENGTH_LONG);
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
                    Intent i = new Intent(Notifications.this, Search.class);
                    startActivity(i);
                }
                catch (Exception ignored)
                {
                }
            }
        });


        if(CheckNetwork.isInternetAvailable(this))
        {
            layMain.setVisibility(View.GONE);
            lay2.setVisibility(View.GONE);
            loadNotifications();
        }
        else
        {
            Snackbar snackbar = Snackbar.make(lay11, "No Internet Connection", Snackbar.LENGTH_LONG);
            View sbView = snackbar.getView();
            sbView.setBackgroundResource(R.color.colorPrimaryDark);
            snackbar.show();

            layMain.setVisibility(View.GONE);
            lay2.setVisibility(View.VISIBLE);
            noimg.setImageResource(R.drawable.no_internet);
            tv1.setText(getResources().getString(R.string.nonet));
            tv2.setVisibility(View.VISIBLE);
        }

        tv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try
                {
                    if(CheckNetwork.isInternetAvailable(Notifications.this))
                    {
                        layMain.setVisibility(View.GONE);
                        lay2.setVisibility(View.GONE);
                        loadNotifications();
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
        } catch (Exception ignored) {}
    }

    private void getCartCount()
    {
        if (!session.getCartQuantity().equals("") && !session.getCartQuantity().equals("0"))
        {
            if (Integer.parseInt(session.getCartQuantity()) < 10)
            {
                tvCartcount.setVisibility(View.VISIBLE);
                tvCartcount.setText(session.getCartQuantity());
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

    private void loadNotifications()
    {
        if (!dialog_pb.isShowing() && dialog_pb!=null) {
            dialog_pb.show();
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, getResources().getString(R.string.notification_url), new Response.Listener<String>()
        {
            @Override
            public void onResponse(String s)
            {
                if (dialog_pb.isShowing())
                {
                    dialog_pb.dismiss();
                }

                if (s != null && s.length()>0)
                {
                    try
                    {
                        JSONObject jsonObj = new JSONObject(s);
                        String response = jsonObj.getString("Response");
                        if (response.contains("Success"))
                        {
                            ArrayList<HashMap<String,String>> listNotifications = new ArrayList<>();

                            JSONArray jarray = jsonObj.getJSONArray("Result");

                            for (int i = 0; i < jarray.length(); i++) {

                                JSONObject jsonObject = jarray.getJSONObject(i);
                                String notification_content = jsonObject.getString("notification_content");
                                String notification_datetime = jsonObject.getString("notification_datetime");
                                String notification_head = jsonObject.getString("notification_head");
                                String notification_id = jsonObject.getString("notification_id");

                                notification_content= URLDecoder.decode(notification_content, "utf-8");
                                notification_datetime= URLDecoder.decode(notification_datetime, "utf-8");
                                notification_head= URLDecoder.decode(notification_head, "utf-8");
                                notification_id= URLDecoder.decode(notification_id, "utf-8");

                                HashMap<String, String> hm1 = new HashMap<>();
                                hm1.put("notification_content", notification_content);
                                hm1.put("notification_datetime", notification_datetime);
                                hm1.put("notification_head",notification_head);
                                hm1.put("notification_id",notification_id);
                                listNotifications.add(hm1);
                            }

                            if (!listNotifications.isEmpty())
                            {
                                layMain.setVisibility(View.VISIBLE);
                                lay2.setVisibility(View.GONE);

                                MyNotificationsAdapter myNotificationsAdapter = new MyNotificationsAdapter(listNotifications,Notifications.this);
                                rv_id.setAdapter(myNotificationsAdapter);
                                myNotificationsAdapter.notifyDataSetChanged();
                            }
                            else
                            {
                                layMain.setVisibility(View.GONE);
                                lay2.setVisibility(View.VISIBLE);
                                noimg.setImageResource(R.drawable.product_not_found);
                                tv1.setText(getResources().getString(R.string.nonotifications));
                                tv2.setVisibility(View.GONE);
                            }
                        }
                        else
                        {
                            layMain.setVisibility(View.GONE);
                            lay2.setVisibility(View.VISIBLE);
                            noimg.setImageResource(R.drawable.product_not_found);
                            tv1.setText(getResources().getString(R.string.nonotifications));
                            tv2.setVisibility(View.GONE);
                        }
                    }
                    catch (Exception ignored)
                    {

                    }
                }
                else
                {
                    layMain.setVisibility(View.GONE);
                    lay2.setVisibility(View.VISIBLE);
                    noimg.setImageResource(R.drawable.product_not_found);
                    tv1.setText(getResources().getString(R.string.nonotifications));
                    tv2.setVisibility(View.GONE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (dialog_pb.isShowing())
                {
                    dialog_pb.dismiss();
                }
                layMain.setVisibility(View.GONE);
                lay2.setVisibility(View.VISIBLE);
                noimg.setImageResource(R.drawable.product_not_found);
                tv1.setText(getString(R.string.error));
                tv2.setVisibility(View.GONE);

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(getResources().getString(R.string.param_customerid),customer_id);
                return params;
            }
        };
        RequestQueue requestQueue   = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
