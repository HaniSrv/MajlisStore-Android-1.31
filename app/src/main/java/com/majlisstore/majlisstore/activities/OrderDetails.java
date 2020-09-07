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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.majlisstore.majlisstore.R;
import com.majlisstore.majlisstore.adapters.OrderedItemsListAdapter;
import com.majlisstore.majlisstore.helpers.CheckNetwork;
import com.majlisstore.majlisstore.helpers.SessionManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class OrderDetails extends AppCompatActivity {

    SessionManager sessionManager;
    String customer_id="0";

    Dialog dialog_pb;

    LinearLayout lay11,layMain;
    ImageView imgBack,imgSearch,cartid;
    TextView tvCartcount;
    LinearLayout lay2;
    ImageView noimg;
    TextView tv1,tv2;

    String o_id;

    RecyclerView recyclerView;
    LinearLayout lay_status,lay_canceled1;
    ImageView img;
    TextView order_unique_id,txt_total,order_mode,order_payment,order_status,order_date,txt_address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        sessionManager = new SessionManager(this);

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

        o_id = getIntent().getStringExtra("o_id");

        if (sessionManager.isLoggedIn())
        {
            getuserdetails();
            getCartCount();

        }

        recyclerView = findViewById(R.id.order_items_list);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutRv =  new LinearLayoutManager(OrderDetails.this, RecyclerView.VERTICAL,false);
        recyclerView.setLayoutManager(layoutRv);

        order_unique_id = findViewById(R.id.order_unique_id);
        order_date = findViewById(R.id.order_date);
        txt_total = findViewById(R.id.total);
        order_mode = findViewById(R.id.order_mode);
        order_payment = findViewById(R.id.order_payment);
        lay_status = findViewById(R.id.lay_status);
        img = findViewById(R.id.img);
        order_status = findViewById(R.id.order_status);
        txt_address = findViewById(R.id.txt_address);
        lay_canceled1 = findViewById(R.id.lay_canceled1);


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

                        Intent i = new Intent(OrderDetails.this, Cart.class);
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
                    Intent i = new Intent(OrderDetails.this, Search.class);
                    startActivity(i);
                }
                catch (Exception ignored)
                {
                }
            }
        });

        lay_canceled1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if(CheckNetwork.isInternetAvailable(OrderDetails.this))
                    {
                        cancel_order(o_id);
                    }
                    else
                    {
                        Snackbar snackbar = Snackbar.make(lay11, "No Internet Connection", Snackbar.LENGTH_LONG);
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


        if(CheckNetwork.isInternetAvailable(this))
        {
            layMain.setVisibility(View.GONE);
            lay2.setVisibility(View.GONE);
            loadOrderItems();
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
                    if(CheckNetwork.isInternetAvailable(OrderDetails.this))
                    {
                        layMain.setVisibility(View.GONE);
                        lay2.setVisibility(View.GONE);
                        loadOrderItems();
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

    private void loadOrderItems()
        {
            if (dialog_pb!=null && !dialog_pb.isShowing()) {
                dialog_pb.show();
            }

            StringRequest stringRequest = new StringRequest(Request.Method.POST, getResources().getString(R.string.order_pro_url), new Response.Listener<String>()
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
                                showOrderDetails(jsonObj);
                            }
                            else
                            {
                                layMain.setVisibility(View.GONE);
                                lay2.setVisibility(View.VISIBLE);
                                noimg.setImageResource(R.drawable.product_not_found);
                                tv1.setText(getResources().getString(R.string.nodetails));
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
                        tv1.setText(getResources().getString(R.string.nodetails));
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
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("order_id",o_id);
                    return params;
                }
            };
            RequestQueue requestQueue   = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);
        }

    private void showOrderDetails(JSONObject jsonObj) {
        try
        {
            JSONObject userdata1 = jsonObj.getJSONObject("order");
            String o_id = URLDecoder.decode(userdata1.getString("o_id"),"utf-8");
            String o_number = URLDecoder.decode(userdata1.getString("o_number"),"utf-8");
            String o_date = URLDecoder.decode(userdata1.getString("o_date"),"utf-8");
            String o_payment = URLDecoder.decode(userdata1.getString("o_payment"),"utf-8");
            String o_total = URLDecoder.decode(userdata1.getString("o_total"),"utf-8");
            String order_cancel = URLDecoder.decode(userdata1.getString("order_cancel"),"utf-8");
            String o_cust_ret_name = URLDecoder.decode(userdata1.getString("o_cust_ret_name"),"utf-8");
            String o_cust_ret_address = URLDecoder.decode(userdata1.getString("o_cust_ret_address"),"utf-8");
            String o_cust_ret_pincode = URLDecoder.decode(userdata1.getString("o_cust_ret_pincode"),"utf-8");
            String o_cust_ret_state = URLDecoder.decode(userdata1.getString("o_cust_ret_state"),"utf-8");
            String payment_mode = URLDecoder.decode(userdata1.getString("payment_mode"),"utf-8");
            String o_pay_paid = URLDecoder.decode(userdata1.getString("o_pay_paid"),"utf-8");
            String tracking_id = URLDecoder.decode(userdata1.getString("tracking_id"),"utf-8");
            String tracking_status = URLDecoder.decode(userdata1.getString("tracking_status"),"utf-8");
            String delivery_date_time = URLDecoder.decode(userdata1.getString("delivery_date_time"),"utf-8");

            JSONArray jsonArr = userdata1.getJSONArray("products");
            ArrayList<HashMap<String,String>> listOrderProducts = new ArrayList<>();

            for (int g = 0; g < jsonArr.length(); g++)
            {
                JSONObject userdata2 = jsonArr.getJSONObject(g);
                String product_image_main = URLDecoder.decode(userdata2.getString("product_image_main"),"utf-8");
                String product_id = URLDecoder.decode(userdata2.getString("product_id"),"utf-8");
                String o_item_qty = URLDecoder.decode(userdata2.getString("o_item_qty"),"utf-8");
                String o_item_amount = URLDecoder.decode(userdata2.getString("o_item_amount"),"utf-8");
                String product_name_en = userdata2.getString("product_name_en");
                try {
                    product_name_en = URLDecoder.decode(userdata2.getString("product_name_en"), "utf-8");
                }
                catch (Exception ignored)
                {

                }
                String category_name_english = URLDecoder.decode(userdata2.getString("category_name_english"),"utf-8");


                HashMap<String,String> hm1 = new HashMap<>();
                hm1.put("product_image_main", product_image_main);
                hm1.put("product_id", product_id);
                hm1.put("o_item_qty",o_item_qty);
                hm1.put("o_item_amount",o_item_amount);
                hm1.put("product_name_en",product_name_en);
                hm1.put("category_name_english",category_name_english);
                listOrderProducts.add(hm1);
            }

            layMain.setVisibility(View.VISIBLE);
            lay2.setVisibility(View.GONE);

            if(!listOrderProducts.isEmpty())
            {
                OrderedItemsListAdapter myOrdersAdapter = new OrderedItemsListAdapter(listOrderProducts,OrderDetails.this);
                recyclerView.setAdapter(myOrdersAdapter);
                myOrdersAdapter.notifyDataSetChanged();

            }

            String textOrderUniqueId="Ref: "+o_number;
            order_unique_id.setText(textOrderUniqueId);
            String textTotal="Grand Total: QAR "+ o_total;
            txt_total.setText(textTotal);

            if(o_payment!=null && o_payment.equals("cash"))
            {
                order_mode.setText(getResources().getString(R.string.paymode_cod));
            }
            else
            {
                order_mode.setText(getResources().getString(R.string.paymode_online));
            }

            if (o_pay_paid!=null && o_pay_paid.equals("0"))
            {
                order_payment.setText(getResources().getString(R.string.pay_unpaid));
            }
            else
            {
                order_payment.setText(getResources().getString(R.string.pay_paid));
            }

            String ordDate=o_date;
            try
            {
                SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("MMM dd,yyyy", Locale.ENGLISH);

                if(o_date!=null && o_date.length()>0)
                {
                    Date ordDate1=simpleDateFormat1.parse(o_date);
                    if(ordDate1!=null)
                    {
                        ordDate=simpleDateFormat2.format(ordDate1);
                    }
                }
            }
            catch (Exception ignored)
            {
            }

            order_date.setText(ordDate);

            String tracking_status_name= "";
            if(tracking_status!=null && tracking_status.length()>0)
            {
                switch (tracking_status) {
                    case "0":
                        img.setImageResource(R.drawable.status_box);
                        tracking_status_name = "Packed";
                        break;
                    case "1":
                        img.setImageResource(R.drawable.delivery);
                        tracking_status_name = "Shipped";
                        break;
                    case "2":
                        img.setImageResource(R.drawable.delivery_truck);
                        tracking_status_name = "Out for Delivery";
                        break;
                    case "3":
                        img.setImageResource(R.drawable.paid_box);
                        tracking_status_name = "Delivered";
                        break;
                    case "10":
                        img.setImageResource(R.drawable.canceled);
                        tracking_status_name = "Cancelled";

                        break;
                    default:
                        img.setImageResource(R.drawable.status_box);
                        tracking_status_name = "Order Placed";
                        break;
                }
            }
            else
            {
                img.setImageResource(R.drawable.status_box);
                tracking_status_name="Order Placed";
            }

            if (order_cancel!=null && order_cancel.equals("1"))
            {
                img.setImageResource(R.drawable.canceled);
                tracking_status_name="Cancelled";
            }

            order_status.setText(tracking_status_name);

            if (order_cancel!=null && order_cancel.equals("0") && tracking_status!=null)
            {
                if (tracking_status.equals("3") || tracking_status.equals("10") || tracking_status.equals("2") ){

                    lay_canceled1.setVisibility(View.GONE);
                }
                else
                {
                    lay_canceled1.setVisibility(View.VISIBLE);
                }
            }
            else
            {
                lay_canceled1.setVisibility(View.GONE);
            }

            String textAddress=o_cust_ret_name+"\n"+o_cust_ret_address+"\n"+o_cust_ret_state+", "+o_cust_ret_pincode;
            txt_address.setText(textAddress);
        }
        catch (Exception ignored)
        {

        }
    }

    public void cancel_order(final String order_id)
    {
        if (!dialog_pb.isShowing() && dialog_pb!=null) {
            dialog_pb.show();
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, getResources().getString(R.string.order_cancel_url), new Response.Listener<String>()
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
                        String response = jsonObj.getString("response");
                        if (response.contains("success"))
                        {
                            Snackbar snackbar = Snackbar
                                    .make(lay11, "Canceled Successfully.", Snackbar.LENGTH_LONG);
                            View sbView = snackbar.getView();
                            sbView.setBackgroundResource(R.color.colorPrimaryDark);
                            snackbar.show();

                            Intent in = new Intent(OrderDetails.this,Orders.class);
                            startActivity(in);
                            finish();
                        }
                        else
                        {
                            Snackbar snackbar = Snackbar
                                    .make(lay11, "Please try again!", Snackbar.LENGTH_LONG);
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
                            .make(lay11, "Please try again!", Snackbar.LENGTH_LONG);
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
                        .make(layMain, "Something went wrong!", Snackbar.LENGTH_LONG);
                View sbView = snackbar.getView();
                sbView.setBackgroundResource(R.color.colorPrimaryDark);
                snackbar.show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(getResources().getString(R.string.param_orderid),order_id);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try
        {
            finish();
        }
        catch (Exception ignored)
        {

        }
    }
}
