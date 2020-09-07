package com.majlisstore.majlisstore.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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
import com.majlisstore.majlisstore.helpers.CheckNetwork;
import com.majlisstore.majlisstore.helpers.SessionManager;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ProductDetails extends AppCompatActivity {

    SessionManager session;

    Dialog dialog_pb;

    ImageView imageback,imgSearch,cartid;
    TextView pditemname,tvCartcount;
    Button btn1,btn2;

    LinearLayout lay,imagelaymain,imagelayone,imagelaytwo,imagelaythree,imagelayfour;
    ImageView enlargeimage, image_main, image_one, image_two, image_three, image_four;

    TextView productname,offertag,offerprice_tv,rs,mrp_tv,destv;
    LinearLayout offertaglayout,layout_des;

    LinearLayout specifictionlayout;
    TextView specification;
    RecyclerView specificaition_rv;

    LinearLayout lay_cate,lay_sub_cate;
    TextView category,subcategory;

    LinearLayout layRecommend;
    RecyclerView reommendedproductsrv;

    LinearLayout layDetails;
    LinearLayout layError;
    ImageView imgError;
    TextView tvError,tvRetry;

    String customer_id = "0";
    String productId="0";
    String stock_qty = "0";
    String product_name_en,category_name_english,cost;
    String product_image_main,product_image_subone,product_image_subtwo,product_image_subthree,product_image_subfour;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLocale();
        setContentView(R.layout.activity_product_details);

        session = new SessionManager(ProductDetails.this);

        dialog_pb = new Dialog(this);
        dialog_pb.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_pb.setContentView(R.layout.progressbar);
        if(dialog_pb.getWindow()!=null)
        {
            dialog_pb.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }
        dialog_pb.setCancelable(false);
        dialog_pb.setCanceledOnTouchOutside(false);
        dialog_pb.show();


        imageback = findViewById(R.id.imgBack);
        imgSearch=findViewById(R.id.searchid);
        cartid=findViewById(R.id.cartid);
        pditemname = findViewById(R.id.pditemnameid);
        tvCartcount=findViewById(R.id.tvCartcount);
        btn1=findViewById(R.id.btn1);
        btn2=findViewById(R.id.btn2);

        lay=findViewById(R.id.lay);
        imagelaymain=findViewById(R.id.imagemain_layid);
        imagelayone=findViewById(R.id.image_onelayid);
        imagelaytwo=findViewById(R.id.image_twolayid);
        imagelaythree=findViewById(R.id.image_threelayid);
        imagelayfour=findViewById(R.id.image_fourlayid);
        enlargeimage = findViewById(R.id.productdetails_imageid);
        image_main = findViewById(R.id.image_main);
        image_one = findViewById(R.id.image_one);
        image_two = findViewById(R.id.image_two);
        image_three = findViewById(R.id.image_three);
        image_four = findViewById(R.id.image_four);

        productname = findViewById(R.id.productname_id);
        offertag = findViewById(R.id.offer_percentage_id);
        offerprice_tv = findViewById(R.id.offerprice_id);
        rs=findViewById(R.id.rs_id);
        mrp_tv = findViewById(R.id.mrp_id);
        destv=findViewById(R.id.des_id);
        offertaglayout = findViewById(R.id.offertag_layout);
        layout_des = findViewById(R.id.descrip_layid);
        mrp_tv.setPaintFlags(mrp_tv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        rs.setPaintFlags(rs.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        specifictionlayout = findViewById(R.id.specification_lay);
        specification = findViewById(R.id.specificationid);
        specificaition_rv = findViewById(R.id.r2_specification);
        specificaition_rv.setHasFixedSize(true);
        RecyclerView.LayoutManager lmSpec = new LinearLayoutManager(ProductDetails.this, LinearLayoutManager.VERTICAL, false);
        specificaition_rv.setLayoutManager(lmSpec);


        lay_cate=findViewById(R.id.lay_cate);
        lay_sub_cate=findViewById(R.id.lay_sub_cate);
        category=findViewById(R.id.category_id);
        subcategory=findViewById(R.id.sub_category_id);

        layRecommend=findViewById(R.id.layRecommend);
        reommendedproductsrv=findViewById(R.id.recommendedrv_id);
        reommendedproductsrv.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        reommendedproductsrv.setHasFixedSize(true);

        layDetails=findViewById(R.id.layDetails);
        layError=findViewById(R.id.layError);
        imgError=findViewById(R.id.imgError);
        tvError=findViewById(R.id.tvError);
        tvRetry=findViewById(R.id.tvRetry);

        Intent intent = getIntent();
        productId = intent.getStringExtra("product_id");

        if(session.isLoggedIn())
        {
            getuserdetails();
            getCartCount();
        }

        imageback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try
                {
                   onBackPressed();
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
                    Intent i = new Intent(ProductDetails.this, Search.class);
                    startActivity(i);
                }
                catch (Exception ignored)
                {

                }
            }
        });

        cartid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try
                {
                    if(session.isLoggedIn())
                    {
                        Intent i = new Intent(ProductDetails.this, Cart.class);
                        startActivity(i);
                    }
                    else
                    {
                        Snackbar snackbar = Snackbar
                                .make(cartid, "You are not logged in", Snackbar.LENGTH_LONG);
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

        image_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try
                {
                    if(product_image_main!=null && product_image_main.length()>0)
                    {
                        Picasso.get().load(product_image_main).error(R.drawable.logo_h).into(enlargeimage);
                    }
                    else
                    {
                        Picasso.get().load(R.drawable.logo_h).error(R.drawable.logo_h).into(enlargeimage);
                    }
                }
                catch (Exception ignored)
                {

                }


            }
        });

        image_one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try
                {
                    if(product_image_subone!=null && product_image_subone.length()>0)
                    {
                        Picasso.get().load(product_image_subone).error(R.drawable.logo_h).into(enlargeimage);
                    }
                    else
                    {
                        Picasso.get().load(R.drawable.logo_h).error(R.drawable.logo_h).into(enlargeimage);
                    }
                }
                catch (Exception ignored)
                {

                }

            }
        });

        image_two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try
                {
                    if(product_image_subtwo!=null && product_image_subtwo.length()>0)
                    {
                        Picasso.get().load(product_image_subtwo).error(R.drawable.logo_h).into(enlargeimage);
                    }
                    else
                    {
                        Picasso.get().load(R.drawable.logo_h).error(R.drawable.logo_h).into(enlargeimage);
                    }
                }
                catch (Exception ignored)
                {

                }
            }
        });

        image_three.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try
                {
                    if(product_image_subthree!=null && product_image_subthree.length()>0)
                    {
                        Picasso.get().load(product_image_subthree).error(R.drawable.logo_h).into(enlargeimage);
                    }
                    else
                    {
                        Picasso.get().load(R.drawable.logo_h).error(R.drawable.logo_h).into(enlargeimage);
                    }
                }
                catch (Exception ignored)
                {

                }

            }
        });

        image_four.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try
                {
                    if(product_image_subfour!=null && product_image_subfour.length()>0)
                    {
                        Picasso.get().load(product_image_subfour).error(R.drawable.logo_h).into(enlargeimage);
                    }
                    else
                    {
                        Picasso.get().load(R.drawable.logo_h).error(R.drawable.logo_h).into(enlargeimage);
                    }
                }
                catch (Exception ignored)
                {

                }

            }
        });

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try
                {
                    if(session.isLoggedIn())
                    {
                        if (CheckNetwork.isInternetAvailable(ProductDetails.this))
                        {
                            addtocart(productId,customer_id,"1");
                        }
                        else
                        {
                            Snackbar snackbar = Snackbar
                                    .make(layDetails, "No Internet Connection", Snackbar.LENGTH_LONG);
                            View sbView = snackbar.getView();
                            sbView.setBackgroundResource(R.color.colorPrimaryDark);
                            snackbar.show();
                        }
                    }
                    else
                    {
                        Snackbar snackbar = Snackbar
                                .make(cartid, "You are not logged in", Snackbar.LENGTH_LONG);
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

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try
                {
                    if(session.isLoggedIn())
                    {
                        int stock_qty1 = Integer.parseInt(stock_qty);
                        if (stock_qty1 == 0)
                        {
                            Snackbar snackbar = Snackbar.make(lay_cate, "No Stock Available", Snackbar.LENGTH_LONG);
                            View sbView = snackbar.getView();
                            sbView.setBackgroundResource(R.color.colorPrimaryDark);
                            snackbar.show();
                        }
                        else
                        {
                            Intent in = new Intent(ProductDetails.this,Buynow.class);
                            in.putExtra("product_id",productId);
                            in.putExtra("product_name_en",product_name_en);
                            in.putExtra("category_name_english",category_name_english);
                            in.putExtra("cost",cost);
                            in.putExtra("stock_qty",stock_qty);
                            in.putExtra("product_image_main",product_image_main);
                            in.putExtra("cart_item_quantity","1");
                            startActivity(in);
                        }
                    }
                    else
                    {
                        Snackbar snackbar = Snackbar
                                .make(cartid, "You are not logged in", Snackbar.LENGTH_LONG);
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

        tvRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try
                {
                    if(CheckNetwork.isInternetAvailable(ProductDetails.this))
                    {
                        getDetailsData();
                        recommendedproductsmethod();
                    }
                }
                catch (Exception ignored)
                {

                }
            }
        });

        if(CheckNetwork.isInternetAvailable(ProductDetails.this))
        {
            getDetailsData();
            recommendedproductsmethod();
        }
        else
        {
            layDetails.setVisibility(View.GONE);
            layError.setVisibility(View.VISIBLE);
            imgError.setImageResource(R.drawable.no_internet);
            tvError.setText(getString(R.string.nonet));
            tvRetry.setVisibility(View.VISIBLE);
        }


    }
    private void setLocale()
    {
        Locale myLocale = new Locale("en");
        Locale.setDefault(myLocale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.locale = myLocale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
    }

    private void getCartCount()
    {
        if(!session.getCartQuantity().equals("")&&!session.getCartQuantity().equals("0"))
        {
            if(Integer.parseInt(session.getCartQuantity())<10)
            {
                if (Integer.parseInt(session.getCartQuantity())==0)
                {
                    tvCartcount .setVisibility(View.INVISIBLE);
                }
                else
                {
                    tvCartcount.setVisibility(View.VISIBLE);
                    tvCartcount.setText(session.getCartQuantity());
                }
            }
            else
            {
                tvCartcount .setVisibility(View.VISIBLE);
                tvCartcount.setText("9+");
            }
        }
        else
        {
            tvCartcount .setVisibility(View.INVISIBLE);
        }
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
        }
        catch (Exception ignored)
        {

        }
    }
    private void getDetailsData() {

        if (dialog_pb!=null && !dialog_pb.isShowing())
        {
            dialog_pb.show();
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, getResources().getString(R.string.url_productdetails), new Response.Listener<String>()
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
                        String response1 = jsonObject.getString("response");
                        if (response1.equalsIgnoreCase("success"))
                        {
                            JSONObject userdata = jsonObject.getJSONObject("products");
                            String product_id = URLDecoder.decode(userdata.getString("product_id"), "utf-8");
                            product_name_en = userdata.getString("product_name_en");
                            try {
                                product_name_en = URLDecoder.decode(userdata.getString("product_name_en"), "utf-8");
                            }
                            catch (Exception ignored)
                            {

                            }
                            cost = URLDecoder.decode(userdata.getString("cost"), "utf-8");
                            stock_qty = URLDecoder.decode(userdata.getString("stock"), "utf-8");
                            String product_image_main1 = URLDecoder.decode(userdata.getString("product_image_main"), "utf-8");
                            String product_image_subone1 = URLDecoder.decode(userdata.getString("product_image_subone"), "utf-8");
                            String product_image_subtwo1 = URLDecoder.decode(userdata.getString("product_image_subtwo"), "utf-8");
                            String product_image_subthree1 = URLDecoder.decode(userdata.getString("product_image_subthree"), "utf-8");
                            String product_image_subfour1 = URLDecoder.decode(userdata.getString("product_image_subfour"), "utf-8");
                            String mrp = URLDecoder.decode(userdata.getString("mrp"), "utf-8");
                            String description = userdata.getString("description");
                            try {
                                description = URLDecoder.decode(userdata.getString("description"), "utf-8");
                            }
                            catch (Exception ignored)
                            {

                            }
                            category_name_english = URLDecoder.decode(userdata.getString("category_name_english"), "utf-8");
                            String subcategory_name_english = URLDecoder.decode(userdata.getString("subcategory_name_english"), "utf-8");

                            ArrayList<HashMap<String, String>> listSpec = new ArrayList<>();
                            JSONArray specification=userdata.getJSONArray("specification");
                            for (int i=0;i<specification.length();i++)
                            {
                                JSONObject userdata1 = specification.getJSONObject(i);
                                String specific_detail_name = URLDecoder.decode(userdata1.getString("specific_detail_name"), "utf-8");
                                String specification_name = URLDecoder.decode(userdata1.getString("specification_name"), "utf-8");
                                HashMap<String, String> news = new HashMap<>();
                                news.put("specific_detail_name", specific_detail_name);
                                news.put("specification_name", specification_name);
                                listSpec.add(news);
                            }
                            if(!listSpec.isEmpty())
                            {
                                specificaition_rv.setVisibility(View.VISIBLE);
                                SpecAdapter adSpec= new SpecAdapter(ProductDetails.this, listSpec);
                                specificaition_rv.setAdapter(adSpec);
                                adSpec.notifyDataSetChanged();
                            }
                            else
                            {
                                specificaition_rv.setVisibility(View.GONE);

                            }

                            pditemname.setText(product_name_en);
                            productname.setText(product_name_en);

                            offerprice_tv.setText(cost);

                            boolean enlargeImageStatus=false;
                            if (product_image_main1!=null && product_image_main1.length()>0 && !product_image_main1.equalsIgnoreCase("null"))
                            {
                                product_image_main = "https://majlisstore.com/crm/public/uploads/product_images/" + product_image_main1;
                                Picasso.get().load(product_image_main).error(R.drawable.logo_h).into(enlargeimage);
                                Picasso.get().load(product_image_main).error(R.drawable.logo_h).into(image_main);
                                imagelaymain.setVisibility(View.VISIBLE);
                                enlargeImageStatus=true;
                            }
                            else
                            {
                                imagelaymain.setVisibility(View.GONE);
                            }

                            if (product_image_subone1!=null && product_image_subone1.length()>0 && !product_image_subone1.equalsIgnoreCase("null"))
                            {
                                product_image_subone = "https://majlisstore.com/crm/public/uploads/product_images/" + product_image_subone1;
                                Picasso.get().load(product_image_subone).error(R.drawable.logo_h).into(image_one);
                                imagelayone.setVisibility(View.VISIBLE);
                                if(!enlargeImageStatus)
                                {
                                    Picasso.get().load(product_image_subone).error(R.drawable.logo_h).into(enlargeimage);
                                    enlargeImageStatus=true;
                                }
                            }
                            else
                            {
                                imagelayone.setVisibility(View.GONE);
                            }

                            if (product_image_subtwo1!=null && product_image_subtwo1.length()>0 && !product_image_subtwo1.equalsIgnoreCase("null"))
                            {
                                product_image_subtwo = "https://majlisstore.com/crm/public/uploads/product_images/" + product_image_subtwo1;
                                Picasso.get().load(product_image_subtwo).error(R.drawable.logo_h).into(image_two);
                                imagelaytwo.setVisibility(View.VISIBLE);
                                if(!enlargeImageStatus)
                                {
                                    Picasso.get().load(product_image_subtwo).error(R.drawable.logo_h).into(enlargeimage);
                                    enlargeImageStatus=true;
                                }
                            }
                            else
                            {
                                imagelaytwo.setVisibility(View.GONE);
                            }

                            if (product_image_subthree1!=null && product_image_subthree1.length()>0 && !product_image_subthree1.equalsIgnoreCase("null"))
                            {
                                product_image_subthree = "https://majlisstore.com/crm/public/uploads/product_images/" + product_image_subthree1;
                                Picasso.get().load(product_image_subthree).error(R.drawable.logo_h).into(image_three);
                                imagelaythree.setVisibility(View.VISIBLE);
                                if(!enlargeImageStatus)
                                {
                                    Picasso.get().load(product_image_subthree).error(R.drawable.logo_h).into(enlargeimage);
                                    enlargeImageStatus=true;
                                }
                            }
                            else
                            {
                                imagelaythree.setVisibility(View.GONE);
                            }

                            if (product_image_subfour1!=null && product_image_subfour1.length()>0  && !product_image_subfour1.equalsIgnoreCase("null"))
                            {
                                product_image_subfour = "https://majlisstore.com/crm/public/uploads/product_images/" + product_image_subfour1;
                                Picasso.get().load(product_image_subfour).error(R.drawable.logo_h).into(image_four);
                                imagelayfour.setVisibility(View.VISIBLE);
                                if(!enlargeImageStatus)
                                {
                                    Picasso.get().load(product_image_subfour).error(R.drawable.logo_h).into(enlargeimage);
                                    enlargeImageStatus=true;
                                }
                            }
                            else
                            {
                                imagelayfour.setVisibility(View.GONE);
                            }

                            if(!enlargeImageStatus)
                            {
                                Picasso.get().load(R.drawable.logo_h).error(R.drawable.logo_h).into(enlargeimage);
                            }

                            mrp_tv.setText(mrp);


                            if (description!=null && description.length()>0 && !description.equalsIgnoreCase("null"))
                            {
                                destv.setText(description);
                                layout_des.setVisibility(View.VISIBLE);
                            }
                            else
                            {
                                layout_des.setVisibility(View.GONE);
                            }

                            if (category_name_english!=null && category_name_english.length()>0)
                            {
                                category.setText(category_name_english);
                                lay_cate.setVisibility(View.VISIBLE);
                            }
                            else
                            {
                                lay_cate.setVisibility(View.GONE);
                            }

                            if (subcategory_name_english!=null && subcategory_name_english.length()>0)
                            {
                                subcategory.setText(subcategory_name_english);
                                lay_sub_cate.setVisibility(View.VISIBLE);
                            }
                            else
                            {
                                lay_sub_cate.setVisibility(View.GONE);
                            }

                            double diff2=0;
                            if(mrp!=null && mrp.length()>0 && cost!=null && cost.length()>0)
                            {
                                double diff1= Double.parseDouble(mrp)-Double.parseDouble(cost);
                                diff2=(diff1/Double.parseDouble(mrp))*100;

                                if(cost.equalsIgnoreCase(mrp))
                                {
                                    mrp_tv.setVisibility(View.GONE);
                                    rs.setVisibility(View.GONE);
                                }
                                else
                                {
                                    mrp_tv.setVisibility(View.VISIBLE);
                                    rs.setVisibility(View.VISIBLE);
                                }

                            }

                            if (diff2 <= 0.0 || diff2 <= 0)
                            {
                                offertaglayout.setVisibility(View.GONE);
                            }
                            else
                            {
                                offertaglayout.setVisibility(View.VISIBLE);
                                DecimalFormat twoDForm = new DecimalFormat("#.#");
                                String discount1 = twoDForm.format(diff2);
                                offertag.setText(discount1);
                            }


                            layDetails.setVisibility(View.VISIBLE);
                            layError.setVisibility(View.GONE);
                        }
                        else if (response.equalsIgnoreCase("failed"))
                        {
                            layDetails.setVisibility(View.GONE);
                            layError.setVisibility(View.VISIBLE);
                            imgError.setImageResource(R.drawable.product_not_found);
                            tvError.setText(getString(R.string.nodetails));
                            tvRetry.setVisibility(View.GONE);
                        }
                        else
                        {
                            layDetails.setVisibility(View.GONE);
                            layError.setVisibility(View.VISIBLE);
                            imgError.setImageResource(R.drawable.product_not_found);
                            tvError.setText(getString(R.string.error));
                            tvRetry.setVisibility(View.VISIBLE);
                        }
                    } catch (Exception ignored) {

                    }
                }
                else
                {
                    layDetails.setVisibility(View.GONE);
                    layError.setVisibility(View.VISIBLE);
                    imgError.setImageResource(R.drawable.product_not_found);
                    tvError.setText(getString(R.string.error));
                    tvRetry.setVisibility(View.VISIBLE);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (dialog_pb.isShowing())
                {
                    dialog_pb.dismiss();
                }
                session.specification("");
                layDetails.setVisibility(View.GONE);
                layError.setVisibility(View.VISIBLE);
                imgError.setImageResource(R.drawable.product_not_found);
                tvError.setText(getString(R.string.error));
                tvRetry.setVisibility(View.VISIBLE);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put(getResources().getString(R.string.param_productdetails), productId);
                params.put(getResources().getString(R.string.param_customerid), customer_id);
                return params;
            }
        };

        RequestQueue requestQueue   = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void recommendedproductsmethod()
    {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, getResources().getString(R.string.url_relatedproducts), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response!=null && response.length()>0)
                {
                    try
                    {
                        JSONObject jsonObject = new JSONObject(response);
                        String response1 = jsonObject.getString("response");
                        if (response1.contains("success"))
                        {
                            ArrayList<HashMap<String, String>> list = new ArrayList<>();
                            JSONArray jsonArray=jsonObject.getJSONArray("products");
                            for (int i=0;i<jsonArray.length();i++)
                            {
                                JSONObject userdata = jsonArray.getJSONObject(i);
                                String product_id = URLDecoder.decode(userdata.getString("product_id"), "utf-8");
                                String mrp = URLDecoder.decode(userdata.getString("mrp"), "utf-8");
                                String cost = URLDecoder.decode(userdata.getString("cost"), "utf-8");
                                String product_image_main = URLDecoder.decode(userdata.getString("product_image_main"), "utf-8");
                                String stock_qty = URLDecoder.decode(userdata.getString("stock_qty"), "utf-8");
                                String product_name_en = userdata.getString("product_name_en");
                                try {
                                    product_name_en = URLDecoder.decode(userdata.getString("product_name_en"), "utf-8");
                                }
                                catch (Exception ignored)
                                {

                                }
                                HashMap<String, String> news = new HashMap<>();
                                news.put("product_id", product_id);
                                news.put("product_name_en", product_name_en);
                                news.put("cost", cost);
                                news.put("stock_qty", stock_qty);
                                news.put("product_image_main", product_image_main);
                                news.put("mrp", mrp);
                                news.put("customer_id", customer_id);
                                list.add(news);
                            }

                            if (!list.isEmpty())
                            {
                                layRecommend.setVisibility(View.VISIBLE);
                                RecommendedAdapter adapter = new RecommendedAdapter(ProductDetails.this, list);
                                reommendedproductsrv.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                            }
                            else
                            {
                                layRecommend.setVisibility(View.GONE);
                            }
                        }
                        else
                        {
                            layRecommend.setVisibility(View.GONE);
                        }
                    }
                    catch (Exception ignored)
                    {

                    }
                }
                else
                {
                    layRecommend.setVisibility(View.GONE);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                layRecommend.setVisibility(View.GONE);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();
                params.put(getResources().getString(R.string.param_productdetails), productId);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);


    }

    public class RecommendedAdapter extends RecyclerView.Adapter<RecommendedAdapter.ViewHolder> {

        ArrayList<HashMap<String, String>> alNameA;
        Context context;

        public RecommendedAdapter( Context context,ArrayList<HashMap<String, String>> alNameA) {
            this.alNameA = alNameA;
            this.context = context;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.related_productslist, parent, false);
            return new ViewHolder(v);

        }

        @Override
        public void onBindViewHolder(@NonNull RecommendedAdapter.ViewHolder holder, int i) {

            final String product_id = alNameA.get(i).get("product_id");
            final String product_image_main = alNameA.get(i).get("product_image_main");
            final String cost = alNameA.get(i).get("cost");
            final String mrp = alNameA.get(i).get("mrp");
            final String product_name_en = alNameA.get(i).get("product_name_en");

            try {
                if (product_image_main != null && product_image_main.length() > 1) {
                    String product_image_main1 = "https://majlisstore.com/crm/public/uploads/product_images/" + product_image_main;
                    Picasso.get().load(product_image_main1).error(R.drawable.logo_h).into(holder.productimage_rp);
                } else {
                    Picasso.get().load(R.drawable.logo_h).error(R.drawable.logo_h).into(holder.productimage_rp);
                }
            } catch (Exception ignored) {

            }

            String textCost = "QAR " + cost;
            holder.offerprice_rp.setText(textCost);
            holder.productname_rp.setText(product_name_en);

            holder.lay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent i = new Intent(context, ProductDetails.class);
                        i.putExtra("product_id", product_id);
                        context.startActivity(i);
                    } catch (Exception ignored) {

                    }
                }
            });

            holder.tv4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (session.isLoggedIn()) {
                            if (CheckNetwork.isInternetAvailable(context)) {
                                addtocart(product_id, customer_id, "1");
                            } else {
                                Snackbar snackbar = Snackbar
                                        .make(layDetails, "No Internet Connection", Snackbar.LENGTH_LONG);
                                View sbView = snackbar.getView();
                                sbView.setBackgroundResource(R.color.colorPrimaryDark);
                                snackbar.show();
                            }

                        } else {
                            Snackbar snackbar = Snackbar
                                    .make(layDetails, "You are not logged in", Snackbar.LENGTH_LONG);
                            View sbView = snackbar.getView();
                            sbView.setBackgroundResource(R.color.colorPrimaryDark);
                            snackbar.show();
                        }
                    } catch (Exception ignored) {

                    }
                }
            });

            double diff2=0;
            if(mrp!=null && mrp.length()>0 && cost!=null && cost.length()>0)
            {
                double diff1= Double.parseDouble(mrp)-Double.parseDouble(cost);
                diff2=(diff1/Double.parseDouble(mrp))*100;
            }

            if(diff2<=0.0 || diff2<=0)
            {
                holder.lay1.setVisibility(View.GONE);
            }
            else
            {
                holder.lay1.setVisibility(View.GONE);
                DecimalFormat twoDForm = new DecimalFormat("#.#");
                String discount1= twoDForm.format(diff2) +"% Off";
                holder.tv5.setText(discount1);
            }
        }

        @Override
        public int getItemCount() {
            return alNameA.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView offerprice_rp,mrpprice_rp,productname_rp,tv5,tv4;
            ImageView productimage_rp;
            LinearLayout lay,lay1;

            public ViewHolder(@NonNull View itemView)
            {
                super(itemView);

                lay=itemView.findViewById(R.id.lay);
                lay1=itemView.findViewById(R.id.lay1);
                offerprice_rp=itemView.findViewById(R.id.productprice_rpid);
                mrpprice_rp=itemView.findViewById(R.id.price_rpid);
                productname_rp=itemView.findViewById(R.id.name_rpid);
                productimage_rp=itemView.findViewById(R.id.image_rpid);
                tv5=itemView.findViewById(R.id.tv5);
                tv4=itemView.findViewById(R.id.tv4);

            }
        }

    }

    public void addtocart(final String product_id, final String customer_id, final String quantity)
    {
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
                        if (response.equalsIgnoreCase("success"))
                        {
                            int count = Integer.parseInt(session.getCartQuantity()) + 1;
                            session.setCartCount("" + count);
                            getCartCount();

                            Snackbar snackbar = Snackbar
                                    .make(layDetails, "Added to Cart", Snackbar.LENGTH_LONG);
                            View sbView = snackbar.getView();
                            sbView.setBackgroundResource(R.color.green);
                            snackbar.show();

                        }
                        else if (response.equalsIgnoreCase("cart updated"))
                        {
                            Snackbar snackbar = Snackbar
                                    .make(layDetails, "Already Added to Cart", Snackbar.LENGTH_LONG);
                            View sbView = snackbar.getView();
                            sbView.setBackgroundResource(R.color.colorPrimaryDark);
                            snackbar.show();
                        }
                        else
                        {
                            Snackbar snackbar = Snackbar
                                    .make(layDetails, "Something went wrong!", Snackbar.LENGTH_LONG);
                            View sbView = snackbar.getView();
                            sbView.setBackgroundResource(R.color.colorPrimaryDark);
                            snackbar.show();
                        }
                    }
                    catch (Exception e)
                    {
                        Snackbar snackbar = Snackbar
                                .make(layDetails, "Something went wrong!", Snackbar.LENGTH_LONG);
                        View sbView = snackbar.getView();
                        sbView.setBackgroundResource(R.color.colorPrimaryDark);
                        snackbar.show();
                    }
                }
                else
                {
                    Snackbar snackbar = Snackbar
                            .make(layDetails, "Something went wrong!", Snackbar.LENGTH_LONG);
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
                        .make(layDetails, "Something went wrong!", Snackbar.LENGTH_LONG);
                View sbView = snackbar.getView();
                sbView.setBackgroundResource(R.color.colorPrimaryDark);
                snackbar.show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put(getResources().getString(R.string.param_addcart1), product_id);
                params.put(getResources().getString(R.string.param_addcart2), customer_id);
                params.put(getResources().getString(R.string.param_addcart3), quantity);
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

    public static class SpecAdapter extends RecyclerView.Adapter<SpecAdapter.ViewHolder> {

        ArrayList<HashMap<String, String>> list;
        Context context;

        public SpecAdapter(Context context, ArrayList<HashMap<String, String>> list) {
            super();
            this.context=context;
            this.list=list;
        }

        @NonNull
        @Override
        public SpecAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.list_specification, viewGroup, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final SpecAdapter.ViewHolder viewHolder, final int i)
        {
            final String specific_detail_name = list.get(i).get("specific_detail_name");
            final String specification_name = list.get(i).get("specification_name");
            viewHolder.tv1.setText(specification_name);
            viewHolder.tv2.setText(specific_detail_name);
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder
        {
            public TextView tv1,tv2;

            public ViewHolder(View itemView)
            {
                super(itemView);
                tv1 = itemView.findViewById(R.id.tv1);
                tv2 = itemView.findViewById(R.id.tv2);
            }
        }
    }
}