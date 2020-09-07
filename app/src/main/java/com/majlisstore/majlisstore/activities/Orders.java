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
import com.majlisstore.majlisstore.adapters.MyOrdersAdapter;
import com.majlisstore.majlisstore.helpers.CheckNetwork;
import com.majlisstore.majlisstore.helpers.SessionManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Orders extends AppCompatActivity {

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
        setContentView(R.layout.activity_orders);

        session=new SessionManager(Orders.this);

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
        RecyclerView.LayoutManager layoutRv =  new LinearLayoutManager(Orders.this, RecyclerView.VERTICAL,false);
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
                        Intent i = new Intent(Orders.this, Cart.class);
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
                    Intent i = new Intent(Orders.this, Search.class);
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
            loadOrders();
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
                    if(CheckNetwork.isInternetAvailable(Orders.this))
                    {
                        layMain.setVisibility(View.GONE);
                        lay2.setVisibility(View.GONE);
                        loadOrders();
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

    private void loadOrders()
    {
        if (!dialog_pb.isShowing() && dialog_pb!=null) {
            dialog_pb.show();
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, getResources().getString(R.string.order_list_url), new Response.Listener<String>()
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
                        String response = jsonObj.getString("response");
                        if (response.contains("success"))
                        {
                            JSONArray jsonArr = jsonObj.getJSONArray("order");

                            ArrayList<HashMap<String,String>> listOrders = new ArrayList<>();

                            for (int g = 0; g < jsonArr.length(); g++)
                            {
                                JSONObject userdata1 = jsonArr.getJSONObject(g);
                                String o_id = URLDecoder.decode(userdata1.getString("o_id"),"utf-8");
                                String o_number = URLDecoder.decode(userdata1.getString("o_number"),"utf-8");
                                String o_date = URLDecoder.decode(userdata1.getString("o_date"),"utf-8");
                                String o_payment = URLDecoder.decode(userdata1.getString("o_payment"),"utf-8");
                                String o_total = URLDecoder.decode(userdata1.getString("o_total"),"utf-8");
                                String payment_mode = URLDecoder.decode(userdata1.getString("payment_mode"),"utf-8");
                                String order_cancel = URLDecoder.decode(userdata1.getString("order_cancel"),"utf-8");
                                String o_pay_paid = URLDecoder.decode(userdata1.getString("o_pay_paid"),"utf-8");
                                String tracking_id = URLDecoder.decode(userdata1.getString("tracking_id"),"utf-8");
                                String tracking_status = URLDecoder.decode(userdata1.getString("tracking_status"),"utf-8");
                                String delivery_date_time = URLDecoder.decode(userdata1.getString("delivery_date_time"),"utf-8");

                                HashMap<String,String> hm1 = new HashMap<>();
                                hm1.put("o_id", o_id);
                                hm1.put("o_number", o_number);
                                hm1.put("o_date",o_date);
                                hm1.put("o_payment",o_payment);
                                hm1.put("o_total",o_total);
                                hm1.put("payment_mode",payment_mode);
                                hm1.put("order_cancel",order_cancel);
                                hm1.put("o_pay_paid",o_pay_paid);
                                hm1.put("tracking_id",tracking_id);
                                hm1.put("tracking_status",tracking_status);
                                hm1.put("delivery_date_time",delivery_date_time);
                                listOrders.add(hm1);
                            }

                            if(!listOrders.isEmpty())
                            {
                                layMain.setVisibility(View.VISIBLE);
                                lay2.setVisibility(View.GONE);

                                MyOrdersAdapter myOrdersAdapter = new MyOrdersAdapter(listOrders,Orders.this);
                                rv_id.setAdapter(myOrdersAdapter);
                                myOrdersAdapter.notifyDataSetChanged();

                            }
                            else
                            {
                                layMain.setVisibility(View.GONE);
                                lay2.setVisibility(View.VISIBLE);
                                noimg.setImageResource(R.drawable.product_not_found);
                                tv1.setText(getResources().getString(R.string.noorders));
                                tv2.setVisibility(View.GONE);
                            }
                        }
                        else
                        {
                            layMain.setVisibility(View.GONE);
                            lay2.setVisibility(View.VISIBLE);
                            noimg.setImageResource(R.drawable.product_not_found);
                            tv1.setText(getResources().getString(R.string.noorders));
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
                    tv1.setText(getResources().getString(R.string.noorders));
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
        try
        {
            Intent in = new Intent(Orders.this,Dashboard.class);
            startActivity(in);
            finish();
        }
        catch (Exception ignored)
        {

        }
    }
}