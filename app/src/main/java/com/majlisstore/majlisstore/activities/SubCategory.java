package com.majlisstore.majlisstore.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.majlisstore.majlisstore.adapters.SubCategoryAdapter;
import com.majlisstore.majlisstore.helpers.CheckNetwork;
import com.majlisstore.majlisstore.helpers.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SubCategory extends AppCompatActivity {

    SessionManager session;

    Dialog dialog_pb;

    LinearLayout laySubCat,layMain;
    ImageView imgBack,imgSearch,cartid;
    TextView tvSubCat,tvCartcount;

    LinearLayout lay2;
    ImageView noimg;
    TextView tv1,tv2;

    RecyclerView recyclerView;

    String customer_id="0";

    String category_id,category_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLocale();
        setContentView(R.layout.activity_sub_category);

        session=new SessionManager(SubCategory.this);

        dialog_pb = new Dialog(this);
        dialog_pb.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_pb.setContentView(R.layout.progressbar);
        if(dialog_pb.getWindow()!=null)
        {
            dialog_pb.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        dialog_pb.setCancelable(false);
        dialog_pb.setCanceledOnTouchOutside(false);

        category_id=getIntent().getStringExtra("category_id");
        category_name=getIntent().getStringExtra("category_name");

        laySubCat=findViewById(R.id.lay11);
        layMain = findViewById(R.id.layMain);

        imgBack=findViewById(R.id.imgBack);
        imgSearch=findViewById(R.id.searchid);
        cartid=findViewById(R.id.cartid);

        tvSubCat=findViewById(R.id.tvSubCat);
        tvSubCat.setText(category_name);

        tvCartcount=findViewById(R.id.tvCartcount);

        lay2=findViewById(R.id.lay2);
        noimg=findViewById(R.id.noimg);
        tv1=findViewById(R.id.tv1);
        tv2=findViewById(R.id.tv2);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutRv = new GridLayoutManager(SubCategory.this, 2);
        recyclerView.setLayoutManager(layoutRv);

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
                        Intent i = new Intent(SubCategory.this, Cart.class);
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
                    Intent i = new Intent(SubCategory.this, Search.class);
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
            getSubcategory();
        }
        else
        {
            Snackbar snackbar = Snackbar.make(laySubCat, "No Internet Connection", Snackbar.LENGTH_LONG);
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
                    if(CheckNetwork.isInternetAvailable(SubCategory.this))
                    {
                        layMain.setVisibility(View.GONE);
                        lay2.setVisibility(View.GONE);
                        getSubcategory();
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


    private void getSubcategory()
    {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, getResources().getString(R.string.url_subcategory), new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                if (dialog_pb.isShowing())
                {
                    dialog_pb.dismiss();
                }

                if(response!=null && response.length()>0)
                {
                    try
                    {
                        JSONObject jsonObject = new JSONObject(response);
                        String response1 = jsonObject.getString("Response");
                        if (response1.contains("Success"))
                        {
                            ArrayList<HashMap<String, String>> list = new ArrayList<>();

                            JSONArray jsonArray=jsonObject.getJSONArray(getResources().getString(R.string.response_result1));

                            for (int i=0;i<jsonArray.length();i++)
                            {
                                JSONObject userdata = jsonArray.getJSONObject(i);

                                String subcategory_name_english = URLDecoder.decode(userdata.getString("subcategory_name_english"), "utf-8");
                                String category_id = URLDecoder.decode(userdata.getString("category_id"), "utf-8");
                                String subcategory_id = URLDecoder.decode(userdata.getString("subcategory_id"), "utf-8");
                                String android_sub_image_name = URLDecoder.decode(userdata.getString("android_sub_image_name"), "utf-8");

                                HashMap<String,String> map=new HashMap<>();
                                map.put("subcategory_name_english",subcategory_name_english);
                                map.put("category_id",category_id);
                                map.put("subcategory_id",subcategory_id);
                                map.put("android_sub_image_name",android_sub_image_name);
                                list.add(map);

                            }
                            if (!list.isEmpty())
                            {
                                lay2.setVisibility(View.GONE);
                                layMain.setVisibility(View.VISIBLE);
                                SubCategoryAdapter adapter=new SubCategoryAdapter(SubCategory.this,list);
                                recyclerView.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                            }
                            else
                            {

                                Intent i = new Intent(SubCategory.this, ProductSubListing.class);
                                i.putExtra("value","sub_category");
                                i.putExtra("category_id", category_id);
                                i.putExtra("category_name", category_name);
                                startActivity(i);
                                finish();

                            }
                        }
                        else
                        {
                            Intent i = new Intent(SubCategory.this, ProductSubListing.class);
                            i.putExtra("value","sub_category");
                            i.putExtra("category_id", category_id);
                            i.putExtra("category_name", category_name);
                            startActivity(i);
                            finish();
                        }
                    }
                    catch (Exception ignored)
                    {

                    }
                }
                else
                {
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
                params.put(getResources().getString(R.string.param_subcategory),category_id);
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

    @Override
    protected void onResume()
    {
        super.onResume();
        try
        {
            setLocale();
        }
        catch (Exception ignored)
        {

        }

    }

    private void setLocale() {
        Locale myLocale = new Locale("en");
        Locale.setDefault(myLocale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.locale = myLocale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
    }

}
