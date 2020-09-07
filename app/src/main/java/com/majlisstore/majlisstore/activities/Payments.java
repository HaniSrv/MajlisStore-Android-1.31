package com.majlisstore.majlisstore.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
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
import com.majlisstore.majlisstore.helpers.CheckNetwork;
import com.majlisstore.majlisstore.helpers.SessionManager;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Payments extends AppCompatActivity {

    SessionManager session;

    Dialog dialog_pb, dialog_insufficient_quantity;
    TextView tv_message, tv_ok;

    LinearLayout lay_cart_main;
    ImageView backbutton;

    LinearLayout lay2;
    ImageView noimg;
    TextView tv1, tv2;

    LinearLayout layMain, lay_add, lay_item;
    RecyclerView rc_cart;
    TextView stv1, total_amount_value, taxable_amount_value, payable_amount_value;
    Button ContinueToCheckOutButton;

    ArrayList<HashMap<String, String>> listcart = new ArrayList<>();

    ArrayList<HashMap<String, String>> listcartcheckout = new ArrayList<>();

    String address_id, address_name, address_address, address_pin;

    double grand_total=0.0;
    boolean nostock=false;
    String rupee_symbol = "QAR ";
    String customer_id = "0";
    String key = "";
    String selectedId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payments);
        key = getIntent().getStringExtra("key");

        init();

        if (session.isLoggedIn()) {
            getuserdetails();
        }

        if(CheckNetwork.isInternetAvailable(Payments.this))
        {
            layMain.setVisibility(View.GONE);
            lay2.setVisibility(View.GONE);
            getCartData();
        }
        else
        {
            Snackbar snackbar = Snackbar.make(lay_cart_main, "No Internet Connection", Snackbar.LENGTH_LONG);
            View sbView = snackbar.getView();
            sbView.setBackgroundResource(R.color.colorPrimaryDark);
            snackbar.show();

            layMain.setVisibility(View.GONE);
            lay2.setVisibility(View.VISIBLE);
            noimg.setImageResource(R.drawable.no_internet);
            tv1.setText(getResources().getString(R.string.nonet));
            tv2.setVisibility(View.VISIBLE);
        }

    }

    public void init()
    {
        session = new SessionManager(this);

        dialog_pb = new Dialog(Payments.this);
        dialog_pb.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_pb.setContentView(R.layout.progressbar);
        dialog_pb.setCancelable(false);
        dialog_pb.setCanceledOnTouchOutside(false);
        if (dialog_pb.getWindow() != null) {
            dialog_pb.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        dialog_insufficient_quantity = new Dialog(Payments.this);
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
        backbutton = findViewById(R.id.imgBack);

        ContinueToCheckOutButton = findViewById(R.id.check_out_btn);

        lay2 = findViewById(R.id.lay2);
        noimg = findViewById(R.id.noimg);
        tv1 = findViewById(R.id.tv1);
        tv2 = findViewById(R.id.tv2);

        layMain = findViewById(R.id.layMain);
        lay_add = findViewById(R.id.lay_add);
        lay_item = findViewById(R.id.lay_item);
        rc_cart = findViewById(R.id.rc_cart);
        rc_cart.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutCart = new LinearLayoutManager(Payments.this, RecyclerView.VERTICAL, false);
        rc_cart.setLayoutManager(layoutCart);


        stv1 = findViewById(R.id.stv1);
        total_amount_value = findViewById(R.id.total_amount_value);
        taxable_amount_value = findViewById(R.id.taxable_amount_value);
        payable_amount_value = findViewById(R.id.payable_amount_value);

        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                    onBackPressed();
                } catch (Exception ignored) {

                }

            }
        });

        ContinueToCheckOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (!nostock)
                    {
                        if (address_id.equals("")) {
                            Snackbar snackbar = Snackbar.make(lay_cart_main, "Please Select Address", Snackbar.LENGTH_LONG);
                            View sbView = snackbar.getView();
                            sbView.setBackgroundResource(R.color.colorPrimaryDark);
                            snackbar.show();
                        } else {

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
                        String cart_id="";
                        if (key != null && key.equalsIgnoreCase("selected"))
                        {
                            cart_id = getIntent().getStringExtra("cart_id");
                        }

                        Intent in = new Intent(Payments.this, Address.class);
                        in.putExtra("value", "Payment");
                        in.putExtra("key",key);
                        in.putExtra("cart_id",cart_id);
                        startActivity(in);
                        finish();
                    } else {
                        customer_id = "0";
                    }

                } catch (Exception ignored) {
                }
            }
        });
    }

    private void getuserdetails() {
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
        if (address_id.equals("")) {
            stv1.setText("");
        } else {
            String textAddress=address_name + "\n" + address_address + ", " + address_pin + ".";
            stv1.setText(textAddress);
        }
    }

    private void getCartData()
    {
        if(dialog_pb!=null && !dialog_pb.isShowing())
        {
            dialog_pb.show();
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, getResources().getString(R.string.cart_url), new Response.Listener<String>()
        {
            @Override
            public void onResponse(String s)
            {
                if (dialog_pb.isShowing())
                    dialog_pb.dismiss();
                if (s != null && s.length() > 0) {
                    try {
                        JSONObject jsonObj = new JSONObject(s);
                        String response = jsonObj.getString("response");
                        if (response.equalsIgnoreCase("success")) {
                            JSONArray jsonArr = jsonObj.getJSONArray("products");
                            listcart.clear();
                            listcart = new ArrayList<>();
                            for (int g = 0; g < jsonArr.length(); g++) {
                                JSONObject userdata1 = jsonArr.getJSONObject(g);
                                String product_id = URLDecoder.decode(userdata1.getString("product_id"), "utf-8");
                                String cost = URLDecoder.decode(userdata1.getString("cost"), "utf-8");
                                String stock_qty = URLDecoder.decode(userdata1.getString("stock_qty"), "utf-8");
                                String product_image_main = URLDecoder.decode(userdata1.getString("product_image_main"), "utf-8");
                                String product_name_en = userdata1.getString("product_name_en");
                                try {
                                    product_name_en = URLDecoder.decode(userdata1.getString("product_name_en"), "utf-8");
                                } catch (Exception ignored) {

                                }
                                String category_name = URLDecoder.decode(userdata1.getString("category_name_english"), "utf-8");
                                String cart_item_quantity = URLDecoder.decode(userdata1.getString("cart_item_quantity"), "utf-8");

                                HashMap<String, String> news = new HashMap<>();
                                news.put("product_id", product_id);
                                news.put("cost", cost);
                                news.put("stock_qty", stock_qty);
                                news.put("product_image_main", product_image_main);
                                news.put("product_name_en", product_name_en);
                                news.put("category_name", category_name);
                                news.put("cart_item_quantity", cart_item_quantity);
                                listcart.add(news);
                            }

                            if (!listcart.isEmpty()) {
                                setCheckoutCart();
                            } else {
                                session.setCartCount("0");
                                layMain.setVisibility(View.GONE);
                                lay2.setVisibility(View.VISIBLE);
                                noimg.setImageResource(R.drawable.cart_empty);
                                tv1.setText(getString(R.string.cartempty));
                                tv2.setVisibility(View.GONE);
                            }
                        } else if (response.equalsIgnoreCase("failed")) {
                            session.setCartCount("0");
                            layMain.setVisibility(View.GONE);
                            lay2.setVisibility(View.VISIBLE);
                            noimg.setImageResource(R.drawable.cart_empty);
                            tv1.setText(getString(R.string.cartempty));
                            tv2.setVisibility(View.GONE);
                        } else {
                            layMain.setVisibility(View.GONE);
                            lay2.setVisibility(View.VISIBLE);
                            noimg.setImageResource(R.drawable.cart_empty);
                            tv1.setText(getString(R.string.cartempty));
                            tv2.setVisibility(View.GONE);
                        }
                    } catch (Exception ignored) {

                    }
                } else {
                    layMain.setVisibility(View.GONE);
                    lay2.setVisibility(View.VISIBLE);
                    noimg.setImageResource(R.drawable.cart_empty);
                    tv1.setText(getString(R.string.error));
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
                noimg.setImageResource(R.drawable.cart_empty);
                tv1.setText(getString(R.string.error));
                tv2.setVisibility(View.GONE);
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


    private void setCheckoutCart()
    {
        try
        {
            listcartcheckout.clear();
            listcartcheckout = new ArrayList<>();

            if (key != null && key.equalsIgnoreCase("selected"))
            {
                String cart_id = getIntent().getStringExtra("cart_id");

                Gson gson = new Gson();
                ArrayList alist = gson.fromJson(cart_id, ArrayList.class);
                JSONArray jsonArrA = new JSONArray(alist);

                if(!listcart.isEmpty() && jsonArrA.length()>0)
                {
                    ArrayList<String> listcartselected = new ArrayList<>();

                    for(int i=0;i<jsonArrA.length();i++)
                    {
                        String productIdSelect=jsonArrA.getString(i);
                        listcartselected.add(productIdSelect);
                        for(int j=0;j<listcart.size();j++)
                        {
                            String productId=listcart.get(j).get("product_id");
                            if(productId!=null && productId.equalsIgnoreCase(productIdSelect))
                            {
                                String cost=listcart.get(j).get("cost");
                                String stock_qty=listcart.get(j).get("stock_qty");
                                String product_image_main=listcart.get(j).get("product_image_main");
                                String product_name_en=listcart.get(j).get("product_name_en");
                                String category_name=listcart.get(j).get("category_name");
                                String cart_item_quantity=listcart.get(j).get("cart_item_quantity");

                                HashMap<String, String> news = new HashMap<>();
                                news.put("product_id", productId);
                                news.put("cost", cost);
                                news.put("stock_qty", stock_qty);
                                news.put("product_image_main", product_image_main);
                                news.put("product_name_en", product_name_en);
                                news.put("category_name", category_name);
                                news.put("cart_item_quantity", cart_item_quantity);
                                listcartcheckout.add(news);
                            }

                        }
                    }

                    selectedId= listcartselected.toString().replace("[", "").replace("]", "").replace(" ","");


                    if(listcartcheckout.isEmpty())
                    {
                        listcartcheckout.addAll(listcart);
                    }
                }
                else
                {
                    listcartcheckout.addAll(listcart);
                }

            }
            else
            {
                listcartcheckout.addAll(listcart);
            }

            grand_total = 0.0;
            for(int j=0;j<listcartcheckout.size();j++)
            {
                String productId=listcartcheckout.get(j).get("product_id");
                String cost=listcartcheckout.get(j).get("cost");
                String stock_qty=listcartcheckout.get(j).get("stock_qty");
                String product_image_main=listcartcheckout.get(j).get("product_image_main");
                String product_name_en=listcartcheckout.get(j).get("product_name_en");
                String category_name=listcartcheckout.get(j).get("category_name");
                String cart_item_quantity=listcartcheckout.get(j).get("cart_item_quantity");

                if(stock_qty!=null && stock_qty.length()>0)
                {
                    try
                    {
                        int stockqty=Integer.parseInt(stock_qty);
                        if(stockqty<1)
                        {
                            nostock=true;
                        }
                    }
                    catch(Exception ignored)
                    {

                    }

                }

                if(cost!=null && cost.length()>0 && cart_item_quantity!=null && cart_item_quantity.length()>0)
                {
                    try
                    {
                        double total_single=Double.parseDouble(cost)*Double.parseDouble(cart_item_quantity);
                        grand_total=grand_total+total_single;

                    }
                    catch(Exception ignored)
                    {

                    }
                }
            }

            layMain.setVisibility(View.VISIBLE);
            lay2.setVisibility(View.GONE);

            String count = String.valueOf(listcartcheckout.size());
            taxable_amount_value.setText(count);
            String textTotal=rupee_symbol+grand_total;
            total_amount_value.setText(textTotal);
            payable_amount_value.setText(textTotal);
            CartAdapter adapter_cart = new CartAdapter(Payments.this, listcartcheckout);
            rc_cart.setAdapter(adapter_cart);
            adapter_cart.notifyDataSetChanged();

        }
        catch (Exception ignored)
        {

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
                    if (CheckNetwork.isInternetAvailable(Payments.this))
                    {
                        if (key.equals("selected")) {

                            placeorder(customer_id, "cash", "1", address_id, selectedId, "2");

                        } else {

                            placeorder(customer_id, "cash", "1", address_id, "", "1");
                        }
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

                    final Dialog dialog_msg = new Dialog(Payments.this);
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
                                finish();

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

    class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

        ArrayList<HashMap<String, String>> alNameA;
        Context context;

        public CartAdapter(Context context, ArrayList<HashMap<String, String>> alNameA) {
            super();
            this.context = context;
            this.alNameA = alNameA;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.list_payments, viewGroup, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final ViewHolder viewHolder, final int i) {

            final String product_id = alNameA.get(i).get("product_id");
            final String cost = alNameA.get(i).get("cost");
            final String stock_qty = alNameA.get(i).get("stock_qty");
            final String product_image_main = alNameA.get(i).get("product_image_main");
            final String product_name_en = alNameA.get(i).get("product_name_en");
            final String category_name = alNameA.get(i).get("category_name");
            final String cart_item_quantity = alNameA.get(i).get("cart_item_quantity");

            viewHolder.tv_product_name.setText(product_name_en);
            String textQty="Qty " + cart_item_quantity;
            viewHolder.et_cart_quanity.setText(textQty);
            viewHolder.tv_cart_category.setText(category_name);

            if(cost!=null && cost.length()>0 && cart_item_quantity!=null && cart_item_quantity.length()>0)
            {
                try
                {
                    double total_single=Double.parseDouble(cost)*Double.parseDouble(cart_item_quantity);
                    String textTotal=rupee_symbol+total_single;
                    viewHolder.tv_price.setText(textTotal);

                }
                catch(Exception ignored)
                {

                }
            }

            try
            {
                if(product_image_main!=null && product_image_main.length()>1)
                {
                    String product_image_main1 = "https://majlisstore.com/crm/public/uploads/product_images/"+product_image_main;
                    Picasso.get().load(product_image_main1).error(R.drawable.logo_icon).into(viewHolder.iv_cart);
                }
                else
                {
                    Picasso.get().load(R.drawable.logo_icon).error(R.drawable.logo_icon).into(viewHolder.iv_cart);
                }
            }
            catch (Exception ignored)
            {

            }

            if(cart_item_quantity!=null && cart_item_quantity.length()>0 && stock_qty!=null && stock_qty.length()>0)
            {
                int cart_item_quantity1 = Integer.parseInt(cart_item_quantity);
                int stock_qty1 = Integer.parseInt(stock_qty);
                if (cart_item_quantity1 > stock_qty1)
                {
                    nostock=true;
                    viewHolder.et_cart_quanity.setTextColor(Color.rgb(197, 38, 0));

                } else {
                    viewHolder.et_cart_quanity.setTextColor(Color.rgb(118, 118, 118));
                }
            }



            viewHolder.lay1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try
                    {
                        Intent in = new Intent(Payments.this, ProductDetails.class);
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


        @Override
        public int getItemCount() {
            return alNameA.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView tv_product_name, tv_price, tv_cart_sku, tv_cart_category;
            public ImageView iv_cart;
            LinearLayout lay1;
            TextView et_cart_quanity;

            public ViewHolder(View itemView) {
                super(itemView);
                tv_product_name = itemView.findViewById(R.id.tv_product_name);
                tv_price = itemView.findViewById(R.id.tv_price);
                tv_cart_sku = itemView.findViewById(R.id.tv_cart_sku);

                iv_cart = itemView.findViewById(R.id.iv_cart);
                lay1 = itemView.findViewById(R.id.lay1);
                et_cart_quanity = itemView.findViewById(R.id.et_cart_quanity);
                tv_cart_category = itemView.findViewById(R.id.tv_cart_category);

            }
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try
        {
            Intent in = new Intent(Payments.this,Cart.class);
            startActivity(in);
            finish();
        }
        catch (Exception ignored)
        {

        }
    }


    public void placeorder (String customer_id, String pay_type, String o_frm, String addressid, String cart_ids, String cart_type) {

        if (!dialog_pb.isShowing() && dialog_pb != null) {
            dialog_pb.show();
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, getResources().getString(R.string.cart_place_order_url), new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (dialog_pb.isShowing())
                    dialog_pb.dismiss();
                if (s != null && s.length() > 0) {
                    try {
                        JSONObject jsonObj = new JSONObject(s);
                        String response = jsonObj.getString("response");
                        if (response.contains("success")) {
                            int count = listcart.size() - listcartcheckout.size();
                            session.setCartCount(String.valueOf(count));

                            final Dialog dialog_msg = new Dialog(Payments.this);
                            dialog_msg.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialog_msg.setContentView(R.layout.checkout);
                            dialog_msg.setCancelable(false);
                            dialog_msg.setCanceledOnTouchOutside(false);
                            dialog_msg.show();

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    startActivity(new Intent(Payments.this, Dashboard.class));
                                }
                            }, 2000);

                            Snackbar snackbar = Snackbar.make(lay_cart_main, "Order Placed Successfully.", Snackbar.LENGTH_LONG);
                            View sbView = snackbar.getView();
                            sbView.setBackgroundResource(R.color.green);
                            snackbar.show();

                        } else {
                            Snackbar snackbar = Snackbar.make(lay_cart_main, "Order Failed, Please try again!", Snackbar.LENGTH_LONG);
                            View sbView = snackbar.getView();
                            sbView.setBackgroundResource(R.color.colorPrimaryDark);
                            snackbar.show();
                        }
                    } catch (Exception ignored) {

                    }
                } else {
                    Snackbar snackbar = Snackbar.make(lay_cart_main, "Order Failed, Please try again!", Snackbar.LENGTH_LONG);
                    View sbView = snackbar.getView();
                    sbView.setBackgroundResource(R.color.colorPrimaryDark);
                    snackbar.show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (dialog_pb.isShowing()) {
                    dialog_pb.dismiss();
                }

                Snackbar snackbar = Snackbar.make(lay_cart_main, "Order Failed, Please try again!", Snackbar.LENGTH_LONG);
                View sbView = snackbar.getView();
                sbView.setBackgroundResource(R.color.colorPrimaryDark);
                snackbar.show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("customerid", customer_id);
                params.put("pay_type", pay_type);
                params.put("o_frm", o_frm);
                params.put("addressid", addressid);
                params.put("payment_id", "");
                params.put("item_id", cart_ids);
                params.put("type", cart_type);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

}


