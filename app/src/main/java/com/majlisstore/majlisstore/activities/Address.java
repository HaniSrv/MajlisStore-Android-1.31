package com.majlisstore.majlisstore.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Address extends AppCompatActivity {

    SessionManager session;

    Dialog dialog_pb;

    LinearLayout lay_address,lay_add;
    ImageView imgBack,imgSearch,cartid;
    TextView tvCartcount;
    LinearLayout lay_add_plus,lay_add_view;
    RecyclerView recyclerView;
    RecyclerView recyclerView1;
    TextView ed_add_state,tv_add;
    EditText ed_add_name,ed_add_addr,ed_add_pin;

    String address_id,value;
    String state_idz="0",customer_id = "0";
    String product_name_en,category_name,cost,stock_qty,product_image_main,cart_item_quantity,product_id;
    String key="",cart_id="";

    ArrayList<HashMap<String, String>> listState = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

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

        if(getIntent().hasExtra("value"))
        {
            value = getIntent().getStringExtra("value");

            if (value!=null && value.equals("Buynow"))
            {
                product_name_en =  getIntent().getStringExtra("product_name_en");
                category_name =  getIntent().getStringExtra("category_name_english");
                cost =  getIntent().getStringExtra("cost");
                stock_qty =  getIntent().getStringExtra("stock_qty");
                product_image_main =  getIntent().getStringExtra("product_image_main");
                cart_item_quantity =  getIntent().getStringExtra("cart_item_quantity");
                product_id =  getIntent().getStringExtra("product_id");
            }
            else if (value!=null && value.equals("Payment"))
            {
                key =  getIntent().getStringExtra("key");
                cart_id =  getIntent().getStringExtra("cart_id");
            }
        }


        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager lmanager = new LinearLayoutManager(Address.this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(lmanager);
        recyclerView.setNestedScrollingEnabled(false);

        recyclerView1 = findViewById(R.id.recyclerView1);
        recyclerView1.setHasFixedSize(true);
        RecyclerView.LayoutManager lmanager1 = new LinearLayoutManager(Address.this, LinearLayoutManager.VERTICAL, false);
        recyclerView1.setLayoutManager(lmanager1);
        recyclerView1.setNestedScrollingEnabled(false);

        lay_address= findViewById(R.id.lay_address);
        lay_add= findViewById(R.id.lay_add);
        imgBack=findViewById(R.id.imgBack);
        imgSearch= findViewById(R.id.imgSearch);
        cartid= findViewById(R.id.cartid);
        tvCartcount=findViewById(R.id.tvCartcount);
        lay_add_plus= findViewById(R.id.lay_add_plus);
        lay_add_view= findViewById(R.id.lay_add_view);

        ed_add_state= findViewById(R.id.ed_add_state);
        ed_add_name= findViewById(R.id.ed_add_name);
        ed_add_addr= findViewById(R.id.ed_add_addr);
        ed_add_pin= findViewById(R.id.ed_add_pincode);
        tv_add= findViewById(R.id.tv_add);

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
                        Intent i = new Intent(Address.this, Cart.class);
                        startActivity(i);
                    }
                    else
                    {
                        Snackbar snackbar = Snackbar
                                .make(lay_address, "You are not logged in", Snackbar.LENGTH_LONG);
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
                    Intent i = new Intent(Address.this, Search.class);
                    startActivity(i);
                }
                catch (Exception ignored)
                {
                }
            }
        });


        ed_add_state.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (CheckNetwork.isInternetAvailable(Address.this))
                    {
                        if(listState.isEmpty())
                        {
                            getStateData();
                        }
                        else
                        {
                            showState();
                        }
                    }
                    else
                    {

                        Snackbar snackbar = Snackbar.make(lay_address, "No Internet Connection", Snackbar.LENGTH_LONG);
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


        lay_add_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try
                {
                    if (lay_add_view.getVisibility() == View.VISIBLE)
                    {
                        lay_add_view.setVisibility(View.GONE);
                        InputMethodManager imm=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(ed_add_name.getWindowToken(),InputMethodManager.RESULT_UNCHANGED_SHOWN);
                    }else{
                        lay_add_view.setVisibility(View.VISIBLE);
                        ed_add_addr.setText("");
                        ed_add_name.setText("");
                        ed_add_state.setText("");
                        state_idz="0";
                        ed_add_pin.setText("");
                        tv_add.setText(getResources().getString(R.string.submit));
                    }
                }
                catch (Exception ignored)
                {
                }
            }
        });


        if(session.isLoggedIn())
        {
            getuserdetails();
            getCartCount();
        }

        showAddress();
        if (CheckNetwork.isInternetAvailable(Address.this)) {

            getAddressData();

        }
        else
        {
            Snackbar snackbar = Snackbar.make(lay_address, "No Internet Connection", Snackbar.LENGTH_LONG);
            View sbView = snackbar.getView();
            sbView.setBackgroundResource(R.color.colorPrimaryDark);
            snackbar.show();

        }


        lay_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{

                    InputMethodManager imm=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(ed_add_name.getWindowToken(),InputMethodManager.RESULT_UNCHANGED_SHOWN);

                    String name = ed_add_name.getText().toString();
                    String addressz = ed_add_addr.getText().toString();
                    String pin = ed_add_pin.getText().toString();
                    String state = ed_add_state.getText().toString();

                    if (name.length()==0){
                        Snackbar snackbar = Snackbar.make(lay_address, "Please Enter Name", Snackbar.LENGTH_LONG);
                        View sbView = snackbar.getView();
                        sbView.setBackgroundResource(R.color.colorPrimaryDark);
                        snackbar.show();

                        ed_add_name.requestFocus();
                    }
                    else if (addressz.length()==0){
                        Snackbar snackbar = Snackbar.make(lay_address, "Please Enter Address", Snackbar.LENGTH_LONG);
                        View sbView = snackbar.getView();
                        sbView.setBackgroundResource(R.color.colorPrimaryDark);
                        snackbar.show();

                        ed_add_addr.requestFocus();
                    }
                    else if (pin.length()==0){
                        Snackbar snackbar = Snackbar.make(lay_address, "Please Enter Pincode", Snackbar.LENGTH_LONG);
                        View sbView = snackbar.getView();
                        sbView.setBackgroundResource(R.color.colorPrimaryDark);
                        snackbar.show();

                        ed_add_pin.requestFocus();
                    }

                    else if (state.length()==0){
                        Snackbar snackbar = Snackbar.make(lay_address, "Please Enter Pincode", Snackbar.LENGTH_LONG);
                        View sbView = snackbar.getView();
                        sbView.setBackgroundResource(R.color.colorPrimaryDark);
                        snackbar.show();
                    }
                    else
                    {
                        if(CheckNetwork.isInternetAvailable(Address.this))
                        {
                            String val = tv_add.getText().toString();
                            if (val.equalsIgnoreCase("SUBMIT")) {

                                addAddress(name, addressz, state_idz, pin);
                            }else{

                                editAddress(address_id,name, addressz, state_idz, pin);
                            }
                        }
                        else
                        {
                            Snackbar snackbar = Snackbar.make(lay_address, "No Internet Connection", Snackbar.LENGTH_LONG);
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

    @Override
    public void onBackPressed()
    {
        try {
            if (value.equals("Payment"))
            {
                Intent in = new Intent(Address.this,Payments.class);
                in.putExtra("key",key);
                in.putExtra("cart_id",cart_id);
                startActivity(in);
                finish();

            }
            else if (value.equals("Buynow"))
            {
                Intent in = new Intent(Address.this,Buynow.class);
                in.putExtra("product_id",product_id);
                in.putExtra("product_name_en",product_name_en);
                in.putExtra("category_name_english",category_name);
                in.putExtra("cost",cost);
                in.putExtra("stock_qty",stock_qty);
                in.putExtra("product_image_main",product_image_main);
                in.putExtra("cart_item_quantity",cart_item_quantity);
                startActivity(in);
                finish();

            }
            else
            {
                super.onBackPressed();
                finish();
            }

        }
        catch (Exception ignored)
        {

        }
    }



    private void getAddressData() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, getResources().getString(R.string.shipping_view_url), new Response.Listener<String>()
        {
            @Override
            public void onResponse(String s)
            {

                ArrayList<HashMap<String, String>> listA = new ArrayList<>();

                if (s != null && s.length()>0)
                {
                    try
                    {
                        JSONObject jsonObj = new JSONObject(s);
                        String response = jsonObj.getString("Response");
                        if (response.contains("Success"))
                        {
                            JSONArray jsonArr = jsonObj.getJSONArray("Result");
                            for (int g = 0; g < jsonArr.length(); g++)
                            {
                                JSONObject userdata1 = jsonArr.getJSONObject(g);

                                String customer_id = URLDecoder.decode(userdata1.getString("customer_id"), "utf-8");
                                String customer_address = URLDecoder.decode(userdata1.getString("customer_address"), "utf-8");
                                String address_name = URLDecoder.decode(userdata1.getString("address_name"), "utf-8");
                                String shipping_address = URLDecoder.decode(userdata1.getString("shipping_address"), "utf-8");
                                String address_pin = URLDecoder.decode(userdata1.getString("address_pin"), "utf-8");
                                String address_state = URLDecoder.decode(userdata1.getString("address_state"), "utf-8");
                                String state_name = URLDecoder.decode(userdata1.getString("state_name"), "utf-8");
                                String default_address = URLDecoder.decode(userdata1.getString("default_address"), "utf-8");
                                String addressid = URLDecoder.decode(userdata1.getString("addressid"), "utf-8");

                                HashMap<String, String> news = new HashMap<>();
                                news.put("customer_id", customer_id);
                                news.put("customer_address", customer_address);
                                news.put("address_name", address_name);
                                news.put("shipping_address", shipping_address);
                                news.put("address_pin", address_pin);
                                news.put("address_state", address_state);
                                news.put("state_name", state_name);
                                news.put("default_address", default_address);
                                news.put("addressid", addressid);

                                listA.add(news);

                            }

                            session.createAddressSession("");

                            Gson gson = new Gson();
                            List<HashMap<String, String>> textListA = new ArrayList<>(listA);
                            String jsonTextA = gson.toJson(textListA);

                            session.createAddressSession(jsonTextA);
                        }
                        else if (response.contains("Empty"))
                        {
                            session.createAddressSession("");
                        }
                    }
                    catch (Exception ignored)
                    {
                    }
                }

                showAddress();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showAddress();
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


    private void showAddress()
    {
        Gson gson = new Gson();
        HashMap<String, String> user1 = session.getAddressSession();
        String jsonA = user1.get(SessionManager.KEY_ADDRESS);
        ArrayList alistA = gson.fromJson(jsonA, ArrayList.class);

        if (alistA != null && !alistA.isEmpty()) {

            ArrayList<HashMap<String, String>> list1 = new ArrayList<>();
            try {

                JSONArray jsonArrA = new JSONArray(alistA);
                for (int g= 0; g < alistA.size();g++)
                {
                    JSONObject userdata1 = jsonArrA.getJSONObject(g);
                    String customer_id = userdata1.getString("customer_id");
                    String customer_address = userdata1.getString("customer_address");
                    String address_name = userdata1.getString("address_name");
                    String shipping_address = userdata1.getString("shipping_address");
                    String address_pin = userdata1.getString("address_pin");
                    String address_state = userdata1.getString("address_state");
                    String state_name = userdata1.getString("state_name");
                    String default_address = userdata1.getString("default_address");
                    String addressid = userdata1.getString("addressid");

                    if(default_address.equals("1"))
                    {
                        session.createDefaultAddress(addressid,address_name,shipping_address,address_pin,address_state,state_name);
                    }

                    HashMap<String, String> news = new HashMap<>();
                    news.put("customer_id", customer_id);
                    news.put("customer_address", customer_address);
                    news.put("address_name", address_name);
                    news.put("shipping_address", shipping_address);
                    news.put("address_pin", address_pin);
                    news.put("address_state", address_state);
                    news.put("state_name", state_name);
                    news.put("default_address", default_address);
                    news.put("addressid", addressid);
                    list1.add(news);

                }

                RLVAdapters adapter = new RLVAdapters(Address.this, list1);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            }
            catch (Exception ignored)
            {
            }


        }

    }

    private class RLVAdapters extends RecyclerView.Adapter<RLVAdapters.ViewHolder> {

        ArrayList<HashMap<String, String>> alNameA;
        Context context;

        public RLVAdapters(Context context,ArrayList<HashMap<String, String>> alNameA) {
            super();
            this.context=context;
            this.alNameA=alNameA;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.list_address, viewGroup, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i)
        {
            final String customer_id = alNameA.get(i).get("customer_id");
            final String customer_address = alNameA.get(i).get("customer_address");
            final String address_name = alNameA.get(i).get("address_name");
            final String shipping_address = alNameA.get(i).get("shipping_address");
            final String address_pin = alNameA.get(i).get("address_pin");
            final String address_state = alNameA.get(i).get("address_state");
            final String state_name = alNameA.get(i).get("state_name");
            final String default_address = alNameA.get(i).get("default_address");
            final String addressid = alNameA.get(i).get("addressid");


            if(default_address!=null && default_address.equals("1"))
            {
                recyclerView.scrollToPosition(i);
                viewHolder.rb.setChecked(true);
            }
            else
            {
                viewHolder.rb.setChecked(false);
            }

            viewHolder.tv1.setText(address_name);
            String textAddress=shipping_address+", "+address_pin;
            viewHolder.tv4.setText(textAddress);

            viewHolder.lay2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try{

                        lay_add_view.setVisibility(View.VISIBLE);
                        ed_add_addr.setText(shipping_address);
                        ed_add_name.setText(address_name);
                        ed_add_state.setText(state_name);
                        ed_add_pin.setText(address_pin);
                        address_id = addressid;
                        state_idz = address_state;
                        tv_add.setText(getResources().getString(R.string.edit_address));
                    }
                    catch (Exception ignored)
                    {
                    }
                }
            });

            viewHolder.lay3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try
                    {
                        if(CheckNetwork.isInternetAvailable(Address.this))
                        {
                            if(default_address!=null && default_address.equals("1"))
                            {
                                Snackbar snackbar = Snackbar.make(lay_address, "Default address cannot be removed.", Snackbar.LENGTH_LONG);
                                View sbView = snackbar.getView();
                                sbView.setBackgroundResource(R.color.colorPrimaryDark);
                                snackbar.show();
                            }
                            else
                            {
                                remove(customer_id, addressid);
                            }
                        }
                        else
                        {
                            Snackbar snackbar = Snackbar.make(lay_address, "No Internet Connection", Snackbar.LENGTH_LONG);
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

            viewHolder.lay_click.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try
                    {
                        if(CheckNetwork.isInternetAvailable(Address.this))
                        {
                            if(default_address!=null && !default_address.equals("1"))
                            {
                                makeDefault(customer_id, addressid);
                            }
                        }
                        else
                        {
                            Snackbar snackbar = Snackbar.make(lay_address, "No Internet Connection", Snackbar.LENGTH_LONG);
                            View sbView = snackbar.getView();
                            sbView.setBackgroundResource(R.color.colorPrimaryDark);
                            snackbar.show();
                        }
                    }catch (Exception ignored)
                    {

                    }
                }
            });
        }


        @Override
        public int getItemCount() {
            return alNameA.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder
        {
            public TextView tv1,tv2,tv3,tv4;
            public LinearLayout lay,lay1,lay2,lay3,lay_click;
            public RadioButton rb;

            public ViewHolder(View itemView) {
                super(itemView);

                rb = itemView.findViewById(R.id.rb);
                lay = itemView.findViewById(R.id.lay);
                lay1 = itemView.findViewById(R.id.lay1);
                lay2 =  itemView.findViewById(R.id.lay2);
                lay3 =  itemView.findViewById(R.id.lay3);
                lay_click =  itemView.findViewById(R.id.lay_click);
                tv1 =  itemView.findViewById(R.id.tv1);
                tv2 = itemView.findViewById(R.id.tv2);
                tv3 = itemView.findViewById(R.id.tv3);
                tv4 = itemView.findViewById(R.id.tv4);
            }
        }
    }

    private void remove(String cid, String address_id)
    {

        if (!dialog_pb.isShowing() && dialog_pb!=null) {
            dialog_pb.show();
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, getResources().getString(R.string.shipping_addr_remove_url), new Response.Listener<String>()
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
                        if (response.contains("Success"))
                        {
                            Snackbar snackbar = Snackbar
                                    .make(lay_address, "Removed Successfully", Snackbar.LENGTH_LONG);
                            View sbView = snackbar.getView();
                            sbView.setBackgroundResource(R.color.colorPrimaryDark);
                            snackbar.show();

                            if (CheckNetwork.isInternetAvailable(Address.this))
                            {
                                getAddressData();

                            }

                        }
                        else {

                            Snackbar snackbar = Snackbar
                                    .make(lay_address, "Please Try Again.", Snackbar.LENGTH_LONG);
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
                            .make(lay_address, "Something went wrong!", Snackbar.LENGTH_LONG);
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
                        .make(lay_address, "Something went wrong!", Snackbar.LENGTH_LONG);
                View sbView = snackbar.getView();
                sbView.setBackgroundResource(R.color.colorPrimaryDark);
                snackbar.show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(getResources().getString(R.string.param_addressid), address_id);
                params.put(getResources().getString(R.string.param_customerid), cid);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    private void makeDefault(String cid, String address_id)
    {
        if (!dialog_pb.isShowing() && dialog_pb!=null) {
            dialog_pb.show();
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, getResources().getString(R.string.shipping_addr_default_url), new Response.Listener<String>()
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
                        if (response.contains("Success"))
                        {

                            Snackbar snackbar = Snackbar
                                    .make(lay_address, "Updated Successfully", Snackbar.LENGTH_LONG);
                            View sbView = snackbar.getView();
                            sbView.setBackgroundResource(R.color.colorPrimaryDark);
                            snackbar.show();

                            if (CheckNetwork.isInternetAvailable(Address.this))
                            {
                                getAddressData();

                            }
                        }
                        else
                        {
                            Snackbar snackbar = Snackbar
                                    .make(lay_address, "Please Try Again.", Snackbar.LENGTH_LONG);
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
                            .make(lay_address, "Something went wrong!", Snackbar.LENGTH_LONG);
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
                        .make(lay_address, "Something went wrong!", Snackbar.LENGTH_LONG);
                View sbView = snackbar.getView();
                sbView.setBackgroundResource(R.color.colorPrimaryDark);
                snackbar.show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(getResources().getString(R.string.param_addressid), address_id);
                params.put(getResources().getString(R.string.param_customerid), cid);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void addAddress(String name,String addressz,String state,String pin)
    {
        if (!dialog_pb.isShowing() && dialog_pb!=null) {
            dialog_pb.show();
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, getResources().getString(R.string.shipping_addr_add_url), new Response.Listener<String>()
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
                        if (response.contains("Success"))
                        {
                            lay_add_view.setVisibility(View.GONE);
                            ed_add_addr.setText("");
                            ed_add_name.setText("");
                            ed_add_state.setText("");
                            state_idz="0";
                            ed_add_pin.setText("");
                            tv_add.setText(getResources().getString(R.string.submit));

                            Snackbar snackbar = Snackbar
                                    .make(lay_address, "Address Added", Snackbar.LENGTH_LONG);
                            View sbView = snackbar.getView();
                            sbView.setBackgroundResource(R.color.colorPrimaryDark);
                            snackbar.show();

                            if (CheckNetwork.isInternetAvailable(Address.this)) {

                                getAddressData();

                            }
                        }
                        else
                        {
                            Snackbar snackbar = Snackbar
                                    .make(lay_address, "Please Try Again.", Snackbar.LENGTH_LONG);
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
                            .make(lay_address, "Something went wrong!", Snackbar.LENGTH_LONG);
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
                        .make(lay_address, "Something went wrong!", Snackbar.LENGTH_LONG);
                View sbView = snackbar.getView();
                sbView.setBackgroundResource(R.color.colorPrimaryDark);
                snackbar.show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("customerid", customer_id);
                params.put("name",name);
                params.put("deliveryaddress",addressz);
                params.put("state",state);
                params.put("pincode",pin);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    private void editAddress(String addressid,String name,String addressz,String state,String pin)
    {
        if (!dialog_pb.isShowing() && dialog_pb!=null) {
            dialog_pb.show();
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, getResources().getString(R.string.shipping_addr_edit_url), new Response.Listener<String>()
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
                        if (response.contains("Success"))
                        {
                            lay_add_view.setVisibility(View.GONE);
                            ed_add_addr.setText("");
                            ed_add_name.setText("");
                            ed_add_state.setText("");
                            state_idz="0";
                            ed_add_pin.setText("");
                            tv_add.setText(getResources().getString(R.string.submit));

                            Snackbar snackbar = Snackbar
                                    .make(lay_address, "Address Updated", Snackbar.LENGTH_LONG);
                            View sbView = snackbar.getView();
                            sbView.setBackgroundResource(R.color.colorPrimaryDark);
                            snackbar.show();

                            if (CheckNetwork.isInternetAvailable(Address.this)) {

                                getAddressData();

                            }
                        }
                        else
                        {
                            Snackbar snackbar = Snackbar
                                    .make(lay_address, "Please Try Again.", Snackbar.LENGTH_LONG);
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
                            .make(lay_address, "Something went wrong!", Snackbar.LENGTH_LONG);
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
                        .make(lay_address, "Something went wrong!", Snackbar.LENGTH_LONG);
                View sbView = snackbar.getView();
                sbView.setBackgroundResource(R.color.colorPrimaryDark);
                snackbar.show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("addressid", addressid);
                params.put("name",name);
                params.put("deliveryaddress",addressz);
                params.put("state",state);
                params.put("pincode",pin);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void getStateData()
    {
        if (!dialog_pb.isShowing() && dialog_pb!=null) {
            dialog_pb.show();
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, getResources().getString(R.string.state_url), new Response.Listener<String>()
        {
            @Override
            public void onResponse(String s)
            {
                if(dialog_pb.isShowing())
                {
                    dialog_pb.dismiss();
                }

                listState.clear();

                if (s != null && s.length()>0)
                {
                    try
                    {
                        JSONObject jsonObj = new JSONObject(s);
                        String response = jsonObj.getString("Response");
                        if (response.contains("Success"))
                        {
                            JSONArray jsonArr = jsonObj.getJSONArray("Result");

                            for (int g = 0; g < jsonArr.length(); g++)
                            {
                                JSONObject userdata1 = jsonArr.getJSONObject(g);

                                String id = URLDecoder.decode(userdata1.getString("id"), "utf-8");
                                String name = URLDecoder.decode(userdata1.getString("name"), "utf-8");

                                HashMap<String, String> news = new HashMap<>();
                                news.put("id", id);
                                news.put("name", name);
                                listState.add(news);

                            }

                            session.createStateSession("");
                            Gson gson = new Gson();
                            List<HashMap<String, String>> textListA = new ArrayList<>(listState);
                            String jsonTextA = gson.toJson(textListA);
                            session.createStateSession(jsonTextA);

                        }
                        else if (response.contains("Empty"))
                        {
                            session.createStateSession("");
                        }
                    }
                    catch (Exception ignored)
                    {

                    }
                }
                else
                {
                    Snackbar snackbar = Snackbar
                            .make(lay_address, "Something went wrong!", Snackbar.LENGTH_LONG);
                    View sbView = snackbar.getView();
                    sbView.setBackgroundResource(R.color.colorPrimaryDark);
                    snackbar.show();
                }

                showState();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (dialog_pb.isShowing())
                {
                    dialog_pb.dismiss();
                }
                Snackbar snackbar = Snackbar
                        .make(lay_address, "Something went wrong!", Snackbar.LENGTH_LONG);
                View sbView = snackbar.getView();
                sbView.setBackgroundResource(R.color.colorPrimaryDark);
                snackbar.show();
                showState();
                showState();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                return new HashMap<>();
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void showState()
    {
        Gson gson = new Gson();
        HashMap<String, String> user1 = session.getStateSSession();
        String jsonA = user1.get(SessionManager.KEY_STATE);
        ArrayList alistA = gson.fromJson(jsonA, ArrayList.class);
        if (alistA != null && !alistA.isEmpty())
        {
            ArrayList<HashMap<String, String>> list1 = new ArrayList<>();
            try {

                JSONArray jsonArrA = new JSONArray(alistA);
                for (int g= 0; g < alistA.size();g++)
                {
                    JSONObject userdata1 = jsonArrA.getJSONObject(g);
                    String id = userdata1.getString("id");
                    String name = userdata1.getString("name");

                    HashMap<String, String> news = new HashMap<>();
                    news.put("id", id);
                    news.put("name", name);
                    list1.add(news);

                }

                recyclerView1.setVisibility(View.VISIBLE);
                RLVAdapters1 adapter1 = new RLVAdapters1(Address.this, list1);
                recyclerView1.setAdapter(adapter1);
                adapter1.notifyDataSetChanged();
            }
            catch (Exception ignored)
            {

            }

        }
        else
        {
            recyclerView1.setVisibility(View.GONE);
        }

    }

    private class RLVAdapters1 extends RecyclerView.Adapter<RLVAdapters1.ViewHolder> {

        ArrayList<HashMap<String, String>> alNameA;
        Context context;

        public RLVAdapters1(Context context,ArrayList<HashMap<String, String>> alNameA) {
            super();
            this.context=context;
            this.alNameA=alNameA;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.list_state, viewGroup, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final ViewHolder viewHolder, final int i)
        {
            final String id = alNameA.get(i).get("id");
            final String name = alNameA.get(i).get("name");

            viewHolder.tv4.setText(name);

            viewHolder.lay_click.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try
                    {
                        state_idz=id;
                        ed_add_state.setText(name);
                        recyclerView1.setVisibility(View.GONE);
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

        public class ViewHolder extends RecyclerView.ViewHolder
        {
            public TextView tv4;
            public LinearLayout lay_click;

            public ViewHolder(View itemView) {
                super(itemView);

                lay_click = itemView.findViewById(R.id.lay_click);
                tv4 = itemView.findViewById(R.id.tv4);

            }
        }
    }

}
