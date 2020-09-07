package com.majlisstore.majlisstore.activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.Button;
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
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Buynow extends AppCompatActivity {

    SessionManager session;

    Dialog dialog_pb, dialog_insufficient_quantity;
    TextView tv_message,tv_ok;

    LinearLayout lay_cart_main;
    ImageView imgBack;

    LinearLayout lay1;
    ImageView iv_cart;
    TextView tv_product_name,tv_cart_category,tv_price,et_cart_quanity;

    LinearLayout lay_add;
    TextView stv1;

    TextView total_amount_value,taxable_amount_value,payable_amount_value;

    Button ContinueToCheckOutButton;

    String address_id,address_name,address_address,address_pin;
    boolean nostock=false;
    String rupee_symbol = "QAR ";
    String customer_id = "0";
    String product_id,product_name_en,category_name,cost,stock_qty,product_image_main,cart_item_quantity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_now_page);

        product_id = getIntent().getStringExtra("product_id");
        product_name_en = getIntent().getStringExtra("product_name_en");
        category_name = getIntent().getStringExtra("category_name_english");
        cost = getIntent().getStringExtra("cost");
        stock_qty = getIntent().getStringExtra("stock_qty");
        product_image_main = getIntent().getStringExtra("product_image_main");
        cart_item_quantity = getIntent().getStringExtra("cart_item_quantity");

        init();

        if (session.isLoggedIn()) {
            getuserdetails();

        }

        tv_product_name.setText(product_name_en);
        tv_cart_category.setText(category_name);
        String textQty = "Qty " + cart_item_quantity;
        et_cart_quanity.setText(textQty);

        if(cost!=null && cost.length()>0 && cart_item_quantity!=null && cart_item_quantity.length()>0)
        {
            try
            {
                double total_single=Double.parseDouble(cost)*Double.parseDouble(cart_item_quantity);
                String textTotal=rupee_symbol+total_single;
                tv_price.setText(textTotal);
                taxable_amount_value.setText("1");
                total_amount_value.setText(textTotal);
                payable_amount_value.setText(textTotal);

            }
            catch(Exception ignored)
            {

            }
        }

        try {
            if (product_image_main != null && product_image_main.length() > 1) {
                Picasso.get().load(product_image_main).error(R.drawable.logo_icon).into(iv_cart);
            } else {
                Picasso.get().load(R.drawable.logo_icon).error(R.drawable.logo_icon).into(iv_cart);
            }
        } catch (Exception ignored) {

        }

        if (cart_item_quantity != null && cart_item_quantity.length() > 0 && stock_qty != null && stock_qty.length() > 0) {
            int cart_item_quantity1 = Integer.parseInt(cart_item_quantity);
            int stock_qty1 = Integer.parseInt(stock_qty);
            if (cart_item_quantity1 > stock_qty1) {
                nostock = true;
                et_cart_quanity.setTextColor(Color.rgb(197, 38, 0));

            } else {
                et_cart_quanity.setTextColor(Color.rgb(118, 118, 118));
            }
        }
    }

    public  void init()
    {
        session = new SessionManager(this);

        dialog_pb = new Dialog(Buynow.this);
        dialog_pb.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_pb.setContentView(R.layout.progressbar);
        dialog_pb.setCancelable(false);
        dialog_pb.setCanceledOnTouchOutside(false);
        if (dialog_pb.getWindow() != null) {
            dialog_pb.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        dialog_insufficient_quantity = new Dialog(Buynow.this);
        dialog_insufficient_quantity.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_insufficient_quantity.setContentView(R.layout.dialog_insufficient_quantity);
        dialog_insufficient_quantity.setCancelable(false);
        dialog_insufficient_quantity.setCanceledOnTouchOutside(false);
        tv_message = (TextView) dialog_insufficient_quantity.findViewById(R.id.tv_message);
        tv_ok = (TextView) dialog_insufficient_quantity.findViewById(R.id.tv_ok);
        tv_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    dialog_insufficient_quantity.dismiss();
                } catch (Exception ignored) {

                }
            }
        });


        lay_cart_main = findViewById(R.id.lay_cart_main);
        imgBack = findViewById(R.id.imgBack);

        lay1 = findViewById(R.id.lay1);
        iv_cart = findViewById(R.id.iv_cart);
        tv_product_name = findViewById(R.id.tv_product_name);
        tv_cart_category = findViewById(R.id.tv_cart_category);
        tv_price = findViewById(R.id.tv_price);
        et_cart_quanity = findViewById(R.id.et_cart_quanity);

        lay_add = findViewById(R.id.lay_add);
        stv1 = findViewById(R.id.stv1);

        total_amount_value = findViewById(R.id.total_amount_value);
        taxable_amount_value = findViewById(R.id.taxable_amount_value);
        payable_amount_value = findViewById(R.id.payable_amount_value);

        ContinueToCheckOutButton = findViewById(R.id.check_out_btn);

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

        ContinueToCheckOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                try {
                    if (!nostock)
                    {
                        if (address_id.equals(""))
                        {
                            Snackbar snackbar = Snackbar.make(lay_cart_main, "Please Select Address", Snackbar.LENGTH_LONG);
                            View sbView = snackbar.getView();
                            sbView.setBackgroundResource(R.color.colorPrimaryDark);
                            snackbar.show();
                        }
                        else
                        {

                            checkOrder();

                        }
                    } else {
                        Snackbar snackbar = Snackbar.make(lay_cart_main, "No Stock Available", Snackbar.LENGTH_LONG);
                        View sbView = snackbar.getView();
                        sbView.setBackgroundResource(R.color.colorPrimaryDark);
                        snackbar.show();
                    }

                } catch (Exception ignored) {

                }
            }
        });

        lay_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (session.isLoggedIn()) {
                        Intent in = new Intent(Buynow.this, Address.class);
                        in.putExtra("value","Buynow");
                        in.putExtra("product_id",product_id);
                        in.putExtra("product_name_en",product_name_en);
                        in.putExtra("category_name_english",category_name);
                        in.putExtra("cost",cost);
                        in.putExtra("stock_qty",stock_qty);
                        in.putExtra("product_image_main",product_image_main);
                        in.putExtra("cart_item_quantity",cart_item_quantity);
                        startActivity(in);
                        finish();
                    } else {
                        customer_id = "0";
                    }

                } catch (Exception ignored) {
                }
            }
        });

        lay1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try
                {
                    Intent in = new Intent(Buynow.this, ProductDetails.class);
                    in.putExtra("product_id", product_id);
                    startActivity(in);
                    finish();
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
        try {
            JSONObject userdata = jsonArrA.getJSONObject(0);
            customer_id = userdata.getString("customer_id");
        } catch (Exception ignored) {

        }

        HashMap<String, String> user1 = session.getDefaultAddress();
        address_id = user1.get(SessionManager.KEY_ADDR_ID);
        address_name = user1.get(SessionManager.KEY_ADDR_NAME);
        address_address = user1.get(SessionManager.KEY_ADDR_SHIPPING_ADDR);
        address_pin = user1.get(SessionManager.KEY_ADDR_PIN);
        if (address_id.equals(""))
        {
            stv1.setText("");
        }
        else
        {
            String textAddress=address_name + "\n" + address_address + ", " + address_pin + ".";
            stv1.setText(textAddress);
        }
    }

    private void checkOrder() {

        if (!dialog_pb.isShowing() && dialog_pb!=null) {
            dialog_pb.show();
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, getResources().getString(R.string.test_order), new Response.Listener<String>()
        {
            @Override
            public void onResponse(String s)
            {
                String order_inactive_status="1";
                if (s != null && s.length()>0)
                {
                    try
                    {
                        JSONObject jsonObj = new JSONObject(s);
                        String response = jsonObj.getString("Response");
                        if (response.equalsIgnoreCase("Success"))
                        {
                            JSONArray jsonArr = jsonObj.getJSONArray("Result");
                            JSONObject userdata1 = jsonArr.getJSONObject(0);
                            order_inactive_status = userdata1.getString("order_inactive_status");
                        }
                    }
                    catch (Exception ignored)
                    {

                    }
                }

                if (order_inactive_status.equalsIgnoreCase("0"))
                {
                    if (CheckNetwork.isInternetAvailable(Buynow.this))
                    {
                        placeorder(customer_id, product_id,"1","cash", "1", address_id, "");
                    }
                    else
                    {
                        if (dialog_pb.isShowing())
                        {
                            dialog_pb.dismiss();
                        }

                        Snackbar snackbar = Snackbar.make(lay_cart_main, "No Internet Connection", Snackbar.LENGTH_LONG);
                        View sbView = snackbar.getView();
                        sbView.setBackgroundResource(R.color.colorPrimaryDark);
                        snackbar.show();
                    }
                }
                else
                {
                    if (dialog_pb.isShowing())
                    {
                        dialog_pb.dismiss();
                    }

                    final Dialog dialog_msg = new Dialog(Buynow.this);
                    dialog_msg.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog_msg.setContentView(R.layout.message);
                    dialog_msg.setCancelable(false);
                    dialog_msg.setCanceledOnTouchOutside(false);

                    TextView tv1 = (TextView) dialog_msg.findViewById(R.id.tv1);
                    tv1.setText(getResources().getString(R.string.cantorder1));

                    TextView tv2 = (TextView) dialog_msg.findViewById(R.id.tv2);
                    tv2.setText(getResources().getString(R.string.cantorder2));

                    TextView tv3 = (TextView) dialog_msg.findViewById(R.id.tv3);
                    tv3.setText(getResources().getString(R.string.ok));

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
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (dialog_pb.isShowing())
                {
                    dialog_pb.dismiss();
                }

                Snackbar snackbar = Snackbar.make(lay_cart_main, "Order Failed, Please try again!", Snackbar.LENGTH_LONG);
                View sbView = snackbar.getView();
                sbView.setBackgroundResource(R.color.colorPrimaryDark);
                snackbar.show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() {
                return new HashMap<>();
            }
        };

        RequestQueue requestQueue   = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void placeorder(final String customer_id,final String product_id,final String quantity, final  String pay_type, final  String o_frm, final  String addressid, final  String payment_id)
    {
        if (dialog_pb!=null && !dialog_pb.isShowing()) {
            dialog_pb.show();
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, getResources().getString(R.string.cart_buy_now_url), new Response.Listener<String>()
        {
            @Override
            public void onResponse(String s)
            {
                if (dialog_pb.isShowing())
                {
                    dialog_pb.dismiss();
                }

                if (s != null && s.length() > 0) {
                    try {
                        JSONObject jsonObj = new JSONObject(s);
                        String response = jsonObj.getString("response");
                        if (response.equalsIgnoreCase("success"))
                        {
                            final Dialog dialog_msg = new Dialog(Buynow.this);
                            dialog_msg.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialog_msg.setContentView(R.layout.checkout);
                            dialog_msg.setCancelable(false);
                            dialog_msg.setCanceledOnTouchOutside(false);
                            dialog_msg.show();

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    startActivity(new Intent(Buynow.this, Dashboard.class));
                                }
                            }, 2000);

                            Snackbar snackbar = Snackbar.make(lay_cart_main, "Order Placed Successfully.", Snackbar.LENGTH_LONG);
                            View sbView = snackbar.getView();
                            sbView.setBackgroundResource(R.color.green);
                            snackbar.show();

                        }
                        else
                        {
                            Snackbar snackbar = Snackbar.make(lay_cart_main, "Order Failed, Please try again!", Snackbar.LENGTH_LONG);
                            View sbView = snackbar.getView();
                            sbView.setBackgroundResource(R.color.colorPrimaryDark);
                            snackbar.show();
                        }
                    }
                    catch (Exception ignored)
                    {

                    }
                } else
                {
                    Snackbar snackbar = Snackbar.make(lay_cart_main, "Order Failed, Please try again!", Snackbar.LENGTH_LONG);
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
                Snackbar snackbar = Snackbar.make(lay_cart_main, "Order Failed, Please try again!", Snackbar.LENGTH_LONG);
                View sbView = snackbar.getView();
                sbView.setBackgroundResource(R.color.colorPrimaryDark);
                snackbar.show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("customerid",customer_id);
                params.put("product_id",product_id);
                params.put("quantity",quantity);
                params.put("pay_type", pay_type);
                params.put("o_frm", o_frm);
                params.put("addressid", addressid);
                params.put("payment_id", payment_id);
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
