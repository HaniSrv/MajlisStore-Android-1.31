package com.majlisstore.majlisstore.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
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

public class Cart extends AppCompatActivity {

    SessionManager session;
    Dialog dialog_pb,dialog_insufficient_quantity;
    TextView tv_message,tv_ok;

    LinearLayout lay_cart_main;
    ImageView imgBack,iv_search;
    TextView tvCartcount;

    LinearLayout layMain;
    TextView txtremove,total_amount_value,taxable_amount_value,payable_amount_value;
    CheckBox ckb;
    RecyclerView rc_cart;
    Button ContinueToCheckOutButton;

    LinearLayout lay2;
    ImageView noimg;
    TextView tv1,tv2;

    Snackbar sn_cart_update;

    double grand_total=0.0;
    boolean nostock=false;
    String rupee_symbol = "QAR ";
    String checkboxzz="not checked";
    ArrayList<String> arrayList_selectedItem = new ArrayList<>();
    String customer_id = "0";

    CartAdapter adapter_cart;
    ArrayList<HashMap<String,String>>listcart = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        init();

        if(session.isLoggedIn())
        {
            getuserdetails();
            getCartCount();
        }

        if(CheckNetwork.isInternetAvailable(this))
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

        tv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try
                {
                    if(CheckNetwork.isInternetAvailable(Cart.this))
                    {
                        layMain.setVisibility(View.GONE);
                        lay2.setVisibility(View.GONE);
                        getCartData();
                    }
                }
                catch (Exception ignored)
                {

                }

            }
        });
    }

    public  void init()
    {
        session=new SessionManager(this);

        dialog_pb = new Dialog(Cart.this);
        dialog_pb.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_pb.setContentView(R.layout.progressbar);
        dialog_pb.setCancelable(false);
        dialog_pb.setCanceledOnTouchOutside(false);
        if(dialog_pb.getWindow()!=null)
        {
            dialog_pb.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        dialog_insufficient_quantity = new Dialog(Cart.this);
        dialog_insufficient_quantity.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_insufficient_quantity.setContentView(R.layout.dialog_insufficient_quantity);
        dialog_insufficient_quantity.setCancelable(false);
        dialog_insufficient_quantity.setCanceledOnTouchOutside(false);
        tv_message = (TextView) dialog_insufficient_quantity.findViewById(R.id.tv_message);
        tv_ok = (TextView) dialog_insufficient_quantity.findViewById(R.id.tv_ok);
        tv_ok.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                try
                {
                    dialog_insufficient_quantity.dismiss();
                }
                catch (Exception ignored)
                {

                }
            }
        });

        lay_cart_main=findViewById(R.id.lay_cart_main);
        imgBack=findViewById(R.id.imgBack);
        iv_search=findViewById(R.id.iv_search);
        tvCartcount=findViewById(R.id.tvCartcount);

        layMain=findViewById(R.id.layMain);
        txtremove=findViewById(R.id.txtremove);
        total_amount_value=findViewById(R.id.total_amount_value);
        taxable_amount_value=findViewById(R.id.taxable_amount_value);
        payable_amount_value=findViewById(R.id.payable_amount_value);
        ckb=findViewById(R.id.ckb);

        rc_cart=findViewById(R.id.rc_cart);
        rc_cart.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutCart = new LinearLayoutManager(Cart.this,RecyclerView.VERTICAL,false);
        rc_cart.setLayoutManager(layoutCart);

        ContinueToCheckOutButton=findViewById(R.id.check_out_btn);

        lay2=findViewById(R.id.lay2);
        noimg=findViewById(R.id.noimg);
        tv1=findViewById(R.id.tv1);
        tv2=findViewById(R.id.tv2);

        sn_cart_update = Snackbar.make(lay_cart_main, "Updating...", Snackbar.LENGTH_INDEFINITE);
        View siew = sn_cart_update.getView();
        siew.setBackgroundResource(R.color.green);

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

        iv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try
                {
                    Intent i = new Intent(Cart.this, Search.class);
                    startActivity(i);
                    finish();
                }
                catch (Exception ignored)
                {
                }
            }
        });


        ckb.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                try {
                    if(checkboxzz.equals("not checked"))
                    {
                        if(CheckNetwork.isInternetAvailable(Cart.this))
                        {
                            ckb.setChecked(true);
                            checkboxzz = "checked";

                            getCartData();
                        }
                        else
                        {
                            Snackbar snackbar = Snackbar.make(lay_cart_main, "No Internet Connection", Snackbar.LENGTH_LONG);
                            View sbView = snackbar.getView();
                            sbView.setBackgroundResource(R.color.colorPrimaryDark);
                            snackbar.show();
                        }
                    }
                    else if(checkboxzz.equals("checked"))
                    {
                        if(CheckNetwork.isInternetAvailable(Cart.this))
                        {
                            ckb.setChecked(false);
                            checkboxzz="not checked";

                            getCartData();
                        }
                        else
                        {
                            Snackbar snackbar = Snackbar.make(lay_cart_main, "No Internet Connection", Snackbar.LENGTH_LONG);
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


        ContinueToCheckOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                try
                {
                    if(session.isLoggedIn())
                    {
                        if (arrayList_selectedItem.isEmpty())
                        {

                            if(!nostock)
                            {
                                Intent in = new Intent(Cart.this, Payments.class);
                                in.putExtra("key","not selected");
                                startActivity(in);
                            }
                            else
                            {
                                nostockdialog();
                            }
                        }
                        else
                        {
                            if(listcart.size()==arrayList_selectedItem.size())
                            {
                                if(!nostock)
                                {
                                    Intent in = new Intent(Cart.this, Payments.class);
                                    in.putExtra("key","not selected");
                                    startActivity(in);
                                }
                                else
                                {
                                    nostockdialog();
                                }
                            }
                            else
                            {
                                cartSelectedAdd();
                            }

                        }
                    }
                }
                catch (Exception ignored)
                {

                }

            }
        });

        txtremove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                try {
                    if(!arrayList_selectedItem.isEmpty() && checkboxzz.equals("checked")) {

                        if(CheckNetwork.isInternetAvailable(Cart.this))
                        {
                            String arrayList_item1 = arrayList_selectedItem.toString().replace("[", "").replace("]", "").replace(" ","");
                            cartDeleteAll(arrayList_item1);
                        }
                        else
                        {
                            Snackbar snackbar = Snackbar.make(lay_cart_main, "No Internet Connection", Snackbar.LENGTH_LONG);
                            View sbView = snackbar.getView();
                            sbView.setBackgroundResource(R.color.colorPrimaryDark);
                            snackbar.show();
                        }


                    }
                    else
                    {
                        Snackbar snackbar = Snackbar.make(lay_cart_main, "Please Select all items.", Snackbar.LENGTH_LONG);
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
                        if (response.equalsIgnoreCase("success"))
                        {
                            JSONArray jsonArr = jsonObj.getJSONArray("products");
                            listcart.clear();
                            listcart = new ArrayList<>();
                            grand_total=0.0;
                            for (int g = 0; g < jsonArr.length(); g++)
                            {
                                JSONObject userdata1 = jsonArr.getJSONObject(g);
                                String product_id = URLDecoder.decode(userdata1.getString("product_id"),"utf-8");
                                String cost = URLDecoder.decode(userdata1.getString("cost"),"utf-8");
                                String stock_qty = URLDecoder.decode(userdata1.getString("stock_qty"),"utf-8");
                                String product_image_main = URLDecoder.decode(userdata1.getString("product_image_main"),"utf-8");
                                String product_name_en = userdata1.getString("product_name_en");
                                try {
                                    product_name_en = URLDecoder.decode(userdata1.getString("product_name_en"), "utf-8");
                                }
                                catch (Exception ignored)
                                {

                                }
                                String category_name = URLDecoder.decode(userdata1.getString("category_name_english"),"utf-8");
                                String cart_item_quantity = URLDecoder.decode(userdata1.getString("cart_item_quantity"),"utf-8");

                                HashMap<String, String> news = new HashMap<>();
                                news.put("product_id", product_id);
                                news.put("cost", cost);
                                news.put("stock_qty", stock_qty);
                                news.put("product_image_main", product_image_main);
                                news.put("product_name_en",product_name_en);
                                news.put("category_name", category_name);
                                news.put("cart_item_quantity", cart_item_quantity);
                                listcart.add(news);

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



                            if(!listcart.isEmpty())
                            {
                                layMain.setVisibility(View.VISIBLE);
                                lay2.setVisibility(View.GONE);

                                String count = String.valueOf(listcart.size());
                                taxable_amount_value.setText(count);
                                session.setCartCount(count);
                                getCartCount();
                                String textTotal=rupee_symbol+grand_total;
                                total_amount_value.setText(textTotal);
                                payable_amount_value.setText(textTotal);
                                arrayList_selectedItem.clear();
                                adapter_cart = new CartAdapter(Cart.this, listcart);
                                rc_cart.setAdapter(adapter_cart);
                                adapter_cart.notifyDataSetChanged();

                            }
                            else
                            {
                                session.setCartCount("0");
                                getCartCount();
                                layMain.setVisibility(View.GONE);
                                lay2.setVisibility(View.VISIBLE);
                                noimg.setImageResource(R.drawable.cart_empty);
                                tv1.setText(getString(R.string.cartempty));
                                tv2.setVisibility(View.GONE);
                            }
                        }
                        else if(response.equalsIgnoreCase("failed"))
                        {
                            session.setCartCount("0");
                            getCartCount();
                            layMain.setVisibility(View.GONE);
                            lay2.setVisibility(View.VISIBLE);
                            noimg.setImageResource(R.drawable.cart_empty);
                            tv1.setText(getString(R.string.cartempty));
                            tv2.setVisibility(View.GONE);
                        }
                        else
                        {
                            layMain.setVisibility(View.GONE);
                            lay2.setVisibility(View.VISIBLE);
                            noimg.setImageResource(R.drawable.cart_empty);
                            tv1.setText(getString(R.string.cartempty));
                            tv2.setVisibility(View.GONE);
                        }
                    }catch (Exception ignored)
                    {

                    }
                }
                else
                {
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

    private class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

        ArrayList<HashMap<String, String>> alNameA;
        Context context;

        boolean keyboardCount=false;

        public CartAdapter(Context context, ArrayList<HashMap<String, String>> alNameA) {
            super();
            this.context=context;
            this.alNameA=alNameA;

        }

        @NonNull
        @Override
        public CartAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.list_cart, viewGroup, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final ViewHolder viewHolder, final int i)
        {

            final String product_id = alNameA.get(i).get("product_id");
            final String cost = alNameA.get(i).get("cost");
            final String stock_qty = alNameA.get(i).get("stock_qty");
            final String product_image_main = alNameA.get(i).get("product_image_main");
            final String product_name_en = alNameA.get(i).get("product_name_en");
            final String category_name = alNameA.get(i).get("category_name");
            final String cart_item_quantity = alNameA.get(i).get("cart_item_quantity");

            viewHolder.tv_product_name.setText(product_name_en);
            viewHolder.et_cart_quanity.setText(cart_item_quantity);
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


            if (checkboxzz.equals("checked"))
            {
                viewHolder.ckbz.setChecked(true);
                if(!arrayList_selectedItem.contains(product_id))
                {
                    arrayList_selectedItem.add(product_id);
                }
            }
            else
            {
                viewHolder.ckbz.setChecked(false);
                arrayList_selectedItem.remove(product_id);
            }

            viewHolder.ckbz.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (!b)
                    {
                        arrayList_selectedItem.remove(product_id);

                        if(arrayList_selectedItem.size()==listcart.size())
                        {
                            ckb.setChecked(true);
                            checkboxzz = "checked";
                        }
                        else
                        {
                            ckb.setChecked(false);
                            checkboxzz="not checked";
                        }
                    }
                    else
                    {
                        if(!arrayList_selectedItem.contains(product_id))
                        {
                            arrayList_selectedItem.add(product_id);
                        }

                        if(arrayList_selectedItem.size()==listcart.size())
                        {
                            ckb.setChecked(true);
                            checkboxzz = "checked";
                        }
                        else
                        {
                            ckb.setChecked(false);
                            checkboxzz="not checked";
                        }

                    }
                }
            });

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

            long productStockInt=0;
            if(stock_qty!=null && stock_qty.length()>0)
            {
                productStockInt=Long.parseLong(stock_qty);
            }
            final long finalProductStockInt = productStockInt;

            long productCartInt=0;
            if(cart_item_quantity!=null && cart_item_quantity.length()>0)
            {
                productCartInt=Long.parseLong(cart_item_quantity);
            }
            final long finalProductCartInt = productCartInt;

            if(finalProductStockInt>0)
            {
                if(finalProductCartInt>finalProductStockInt)
                {
                    viewHolder.tv_cart_sku.setTextColor(getResources().getColor(R.color.red));
                    viewHolder.tv_cart_sku.setText(getResources().getString(R.string.nostock));
                    nostock=true;
                }
                else
                {
                    viewHolder.tv_cart_sku.setTextColor(getResources().getColor(R.color.grey));
                    String textStock="Available Qty " + stock_qty;
                    viewHolder.tv_cart_sku.setText(textStock);
                }

            }
            else
            {
                viewHolder.tv_cart_sku.setTextColor(getResources().getColor(R.color.red));
                viewHolder.tv_cart_sku.setText(getResources().getString(R.string.nostock));
                nostock=true;
            }

            viewHolder.cart_minus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try
                    {
                        if(CheckNetwork.isInternetAvailable(context))
                        {
                            viewHolder.et_cart_quanity.setText(cart_item_quantity);
                            String etCount=viewHolder.et_cart_quanity.getText().toString();
                            long oldCount=Long.parseLong(etCount);
                            long newCount=oldCount-1;
                            if(newCount>=1)
                            {
                                HashMap<String, String> news = new HashMap<>();
                                news.put("product_id", product_id);
                                news.put("cost", cost);
                                news.put("stock_qty", stock_qty);
                                news.put("product_image_main", product_image_main);
                                news.put("product_name_en",product_name_en);
                                news.put("category_name", category_name);
                                news.put("cart_item_quantity", String.valueOf(newCount));

                                updatecart(product_id,String.valueOf(newCount),String.valueOf(oldCount), viewHolder.et_cart_quanity,i,news);
                            }
                        }
                        else
                        {
                            Snackbar snackbar = Snackbar.make(lay_cart_main, "No Internet Connection", Snackbar.LENGTH_LONG);
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


            viewHolder.cart_plus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try
                    {
                        if(CheckNetwork.isInternetAvailable(context))
                        {
                            viewHolder.et_cart_quanity.setText(cart_item_quantity);
                            String etCount= viewHolder.et_cart_quanity.getText().toString();
                            long oldCount=Long.parseLong(etCount);
                            long newCount=oldCount+1;
                            if(newCount<= finalProductStockInt)
                            {
                                HashMap<String, String> news = new HashMap<>();
                                news.put("product_id", product_id);
                                news.put("cost", cost);
                                news.put("stock_qty", stock_qty);
                                news.put("product_image_main", product_image_main);
                                news.put("product_name_en",product_name_en);
                                news.put("category_name", category_name);
                                news.put("cart_item_quantity", String.valueOf(newCount));

                                updatecart(product_id,String.valueOf(newCount),String.valueOf(oldCount), viewHolder.et_cart_quanity,i,news);
                            }
                            else
                            {
                                tv_message.setText(Html.fromHtml("Only " + stock_qty + " quantities are left for the product \n" + product_name_en));
                                dialog_insufficient_quantity.show();
                            }

                        }
                        else
                        {
                            Snackbar snackbar = Snackbar.make(lay_cart_main, "No Internet Connection", Snackbar.LENGTH_LONG);
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

            viewHolder.et_cart_quanity.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        try
                        {
                            String etCount=viewHolder.et_cart_quanity.getText().toString();
                            long newCount=Long.parseLong(etCount);
                            if(newCount==0)
                            {
                                viewHolder.et_cart_quanity.setText(cart_item_quantity);

                                Snackbar snackbar = Snackbar.make(lay_cart_main, "Please enter a valid quantity", Snackbar.LENGTH_LONG);
                                View sbView = snackbar.getView();
                                sbView.setBackgroundResource(R.color.colorPrimaryDark);
                                snackbar.show();
                            }
                            else if(newCount<= finalProductStockInt)
                            {
                                if(newCount!=finalProductCartInt) {

                                    keyboardCount = true;
                                    HashMap<String, String> news = new HashMap<>();
                                    news.put("product_id", product_id);
                                    news.put("cost", cost);
                                    news.put("stock_qty", stock_qty);
                                    news.put("product_image_main", product_image_main);
                                    news.put("product_name_en",product_name_en);
                                    news.put("category_name", category_name);
                                    news.put("cart_item_quantity", String.valueOf(newCount));
                                    updatecart(product_id, String.valueOf(newCount), cart_item_quantity,  viewHolder.et_cart_quanity, i,news);
                                }
                            }
                            else
                            {
                                viewHolder.et_cart_quanity.setText(cart_item_quantity);
                                tv_message.setText(Html.fromHtml("Only " + stock_qty + " quantities are left for the product \n" + product_name_en));
                                dialog_insufficient_quantity.show();

                            }
                        }
                        catch (Exception ignored)
                        {

                        }

                        InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        assert imm != null;
                        imm.hideSoftInputFromWindow(  viewHolder.et_cart_quanity.getWindowToken(),
                                InputMethodManager.RESULT_UNCHANGED_SHOWN);
                        return true;
                    }
                    return false;
                }
            });

            viewHolder.et_cart_quanity.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    Rect r=new Rect();
                    lay_cart_main.getWindowVisibleDisplayFrame(r);
                    int screenHeight=lay_cart_main.getRootView().getHeight();
                    int keyboardHeight=screenHeight-r.bottom;
                    if(keyboardHeight<screenHeight*0.15 && !keyboardCount)
                    {
                        viewHolder.et_cart_quanity.setText(cart_item_quantity);
                    }
                }
            });

            viewHolder.remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    try
                    {
                        if(CheckNetwork.isInternetAvailable(context))
                        {
                            cartDelete(product_id);
                        }
                        else
                        {
                            Snackbar snackbar = Snackbar.make(lay_cart_main, "No Internet Connection", Snackbar.LENGTH_LONG);
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

            viewHolder.lay1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try
                    {
                        Intent in=new Intent(Cart.this,ProductDetails.class);
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

        public class ViewHolder extends RecyclerView.ViewHolder
        {
            public TextView tv_product_name,tv_price,tv_cart_sku,tv_cart_category;
            public ImageView iv_cart,cart_plus,cart_minus,remove;
            LinearLayout lay1;
            EditText et_cart_quanity;
            CheckBox ckbz;

            public ViewHolder(View itemView)
            {
                super(itemView);
                tv_product_name = itemView.findViewById(R.id.tv_product_name);
                tv_price = itemView.findViewById(R.id.tv_price);
                tv_cart_sku = itemView.findViewById(R.id.tv_cart_sku);
                iv_cart=itemView.findViewById(R.id.iv_cart);
                cart_plus=itemView.findViewById(R.id.cart_plus);
                cart_minus=itemView.findViewById(R.id.cart_minus);
                remove=itemView.findViewById(R.id.remove);
                lay1=itemView.findViewById(R.id.lay1);
                et_cart_quanity = itemView.findViewById(R.id.et_cart_quanity);
                tv_cart_category = itemView.findViewById(R.id.tv_cart_category);
                ckbz = itemView.findViewById(R.id.ckbz);
            }
        }
    }

    private void updatecart(String product_id, String newCount, String oldCount, EditText et_cart_quanity, int i, HashMap<String, String> news) {

        if (!dialog_pb.isShowing() && dialog_pb!=null) {
            dialog_pb.show();
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, getResources().getString(R.string.url_addtocart), new Response.Listener<String>()
        {
            @Override
            public void onResponse(String s)
            {
                if (dialog_pb.isShowing())
                {
                    dialog_pb.dismiss();
                }

                if(s!=null && s.length()>0)
                {
                    try
                    {
                        JSONObject jsonObj = new JSONObject(s);
                        String response = jsonObj.getString("response");
                        if (response.equalsIgnoreCase("cart updated"))
                        {
                            et_cart_quanity.setText(newCount);

                            Snackbar snackbar = Snackbar
                                    .make(layMain, "Cart Updated", Snackbar.LENGTH_LONG);
                            View sbView = snackbar.getView();
                            sbView.setBackgroundResource(R.color.green);
                            snackbar.show();

                            listcart.set(i,news);
                            adapter_cart.notifyDataSetChanged();

                            getCartData();
                        }
                        else
                        {
                            et_cart_quanity.setText(oldCount);

                            Snackbar snackbar = Snackbar
                                    .make(layMain, "Something went wrong!", Snackbar.LENGTH_LONG);
                            View sbView = snackbar.getView();
                            sbView.setBackgroundResource(R.color.colorPrimaryDark);
                            snackbar.show();
                        }
                    }
                    catch (Exception e)
                    {
                        et_cart_quanity.setText(oldCount);

                        Snackbar snackbar = Snackbar
                                .make(layMain, "Something went wrong!", Snackbar.LENGTH_LONG);
                        View sbView = snackbar.getView();
                        sbView.setBackgroundResource(R.color.colorPrimaryDark);
                        snackbar.show();
                    }
                }
                else
                {
                    et_cart_quanity.setText(oldCount);

                    Snackbar snackbar = Snackbar
                            .make(layMain, "Something went wrong!", Snackbar.LENGTH_LONG);
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

                et_cart_quanity.setText(oldCount);

                Snackbar snackbar = Snackbar
                        .make(layMain, "Something went wrong!", Snackbar.LENGTH_LONG);
                View sbView = snackbar.getView();
                sbView.setBackgroundResource(R.color.colorPrimaryDark);
                snackbar.show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(getResources().getString(R.string.param_addcart1), product_id);
                params.put(getResources().getString(R.string.param_addcart2), customer_id);
                params.put(getResources().getString(R.string.param_addcart3), newCount);
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
            Intent in = new Intent(Cart.this,Dashboard.class);
            startActivity(in);
            finish();
        }
        catch (Exception ignored)
        {

        }
    }

    public void cartDelete(final String product_id)
    {
        if (!dialog_pb.isShowing() && dialog_pb!=null) {
            dialog_pb.show();
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, getResources().getString(R.string.cart_delete_url), new Response.Listener<String>()
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
                        JSONObject jsonObj = new JSONObject(s);
                        String response = jsonObj.getString("response");
                        if (response.equalsIgnoreCase("success"))
                        {
                            int count = Integer.parseInt(session.getCartQuantity()) - 1;
                            session.setCartCount("" + count);
                            getCartCount();

                            Snackbar snackbar = Snackbar.make(lay_cart_main, "Item removed", Snackbar.LENGTH_LONG);
                            View sbView = snackbar.getView();
                            sbView.setBackgroundResource(R.color.colorPrimaryDark);
                            snackbar.show();

                            getCartData();
                        }
                        else
                        {
                            Snackbar snackbar = Snackbar
                                    .make(layMain, "Something went wrong!", Snackbar.LENGTH_LONG);
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
                            .make(layMain, "Something went wrong!", Snackbar.LENGTH_LONG);
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
                params.put(getResources().getString(R.string.param_item_id), product_id);
                params.put(getResources().getString(R.string.param_customerid), customer_id);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void cartDeleteAll(final String item_ids)
    {
        if (!dialog_pb.isShowing() && dialog_pb!=null) {
            dialog_pb.show();
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, getResources().getString(R.string.cart_delete_all_url), new Response.Listener<String>()
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
                        JSONObject jsonObj = new JSONObject(s);
                        String response = jsonObj.getString("response");
                        if (response.equalsIgnoreCase("success"))
                        {
                            session.setCartCount("0");
                            getCartCount();

                            Snackbar snackbar = Snackbar.make(lay_cart_main, "Item removed", Snackbar.LENGTH_LONG);
                            View sbView = snackbar.getView();
                            sbView.setBackgroundResource(R.color.colorPrimaryDark);
                            snackbar.show();

                            getCartData();
                        }
                        else
                        {
                            Snackbar snackbar = Snackbar
                                    .make(layMain, "Something went wrong!", Snackbar.LENGTH_LONG);
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
                            .make(layMain, "Something went wrong!", Snackbar.LENGTH_LONG);
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
                params.put(getResources().getString(R.string.param_item_id), item_ids);
                params.put(getResources().getString(R.string.param_customerid), customer_id);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        try {
            if(dialog_pb!=null && dialog_pb.isShowing())
            {
                dialog_pb.dismiss();

            }
        }
        catch (Exception ignored)
        {

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if(CheckNetwork.isInternetAvailable(this))
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
        catch (Exception ignored)
        {

        }
    }

    private void nostockdialog()
    {
        try
        {
            final Dialog dialog_msg = new Dialog(Cart.this);
            dialog_msg.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog_msg.setContentView(R.layout.message);
            dialog_msg.setCancelable(false);
            dialog_msg.setCanceledOnTouchOutside(false);
            TextView tv1 = dialog_msg.findViewById(R.id.tv1);
            tv1.setText(getResources().getString(R.string.stockunavailable1));
            TextView tv2 = dialog_msg.findViewById(R.id.tv2);
            tv2.setText(getResources().getString(R.string.stockunavailable2));
            TextView tv = dialog_msg.findViewById(R.id.tv3);
            tv.setText(getString(R.string.ok));
            TextView tv4 = dialog_msg.findViewById(R.id.tv4);
            tv4.setVisibility(View.INVISIBLE);
            tv4.setText(getString(R.string.cancel));
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try
                    {
                        dialog_msg.dismiss();
                        getCartData();
                    } catch (Exception ignored) {}
                }
            });
            dialog_msg.show();
        }
        catch (Exception ignored)
        {

        }
    }


    private void cartSelectedAdd() {

        try
        {
            if(!listcart.isEmpty() && !arrayList_selectedItem.isEmpty())
            {
                boolean stockChecked=false;
                for(int i=0;i<arrayList_selectedItem.size();i++)
                {
                    String productIdSelect=arrayList_selectedItem.get(i);

                    for(int j=0;j<listcart.size();j++)
                    {
                        String productId=listcart.get(j).get("product_id");
                        if(productId!=null && productId.equalsIgnoreCase(productIdSelect))
                        {
                            String stock_qty=listcart.get(j).get("stock_qty");
                            String cart_item_quantity=listcart.get(j).get("cart_item_quantity");

                            long productStockInt=0;
                            if(stock_qty!=null && stock_qty.length()>0)
                            {
                                productStockInt=Long.parseLong(stock_qty);
                            }
                            final long finalProductStockInt = productStockInt;

                            long productCartInt=0;
                            if(cart_item_quantity!=null && cart_item_quantity.length()>0)
                            {
                                productCartInt=Long.parseLong(cart_item_quantity);
                            }
                            final long finalProductCartInt = productCartInt;

                            if(finalProductStockInt>0)
                            {
                                if(finalProductCartInt>finalProductStockInt)
                                {
                                    stockChecked=true;
                                }

                            }
                            else
                            {
                                stockChecked=true;
                            }
                        }

                    }
                }

                if(!stockChecked)
                {
                    Gson gson = new Gson();
                    ArrayList<String> textList = new ArrayList<>(arrayList_selectedItem);
                    String jsonText = gson.toJson(textList);

                    Intent in = new Intent(Cart.this, Payments.class);
                    in.putExtra("key","selected");
                    in.putExtra("cart_id",jsonText);
                    startActivity(in);
                }
                else
                {
                    nostockdialog();
                }
            }
        }
        catch (Exception ignored)
        {

        }

    }

}