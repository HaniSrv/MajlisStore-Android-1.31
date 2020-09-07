package com.majlisstore.majlisstore.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;

public class ProductSubListing extends AppCompatActivity {

    SessionManager session;

    Dialog dialog_pb;

    LinearLayout lay11,layMain;
    ImageView imgBack,imgSearch,cartid;
    TextView tvHeader,tvCartcount;

    RecyclerView rv_id;
    ProductsAdapter adapter;

    LinearLayout lay_filter_sort_main,lay_filter,lay_Sort;

    LinearLayout lay2;
    ImageView noimg;
    TextView tv1,tv2;

    RecyclerView rv_filtersub;
    CategoryAdapter categoryAdapter;
    BrandsAdapter brandsAdapter;

    String customer_id="0";
    int sort_type = 1; //1-new,2-lh,3hl
    int filter_done=0;
    int start=0,end=30;
    boolean loading=false;

    ArrayList<HashMap<String, String>> listProduct = new ArrayList<>();
    ArrayList<HashMap<String, String>> listFilteredProducts = new ArrayList<>();

    ArrayList<HashMap<String, String>> listBrands = new ArrayList<>();
    ArrayList<HashMap<String, String>> listCategory = new ArrayList<>();

    ArrayList<HashMap<String, String>> listFilterCategory = new ArrayList<>();
    ArrayList<HashMap<String, String>> listFilterBrand = new ArrayList<>();
    ArrayList<HashMap<String, String>> listFilterCategoryNew = new ArrayList<>();
    ArrayList<HashMap<String, String>> listFilterBrandNew  = new ArrayList<>();

    String typeId="0";
    String typeType="0";
    String headerName="Products";
    String value="0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_sub_listing);

        session=new SessionManager(ProductSubListing.this);

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
        tvHeader=findViewById(R.id.pditemnameid);
        tvCartcount=findViewById(R.id.tvCartcount);

        rv_id = findViewById(R.id.rv_id);
        rv_id.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutRv = new GridLayoutManager(ProductSubListing.this, 2, GridLayoutManager.VERTICAL, false);
        rv_id.setLayoutManager(layoutRv);

        lay_filter_sort_main = findViewById(R.id.lay_filtersortmain);
        lay_filter = findViewById(R.id.filterlay_id);
        lay_Sort = findViewById(R.id.sortlay_id);

        lay2=(LinearLayout) findViewById(R.id.lay2);
        noimg=(ImageView) findViewById(R.id.noimg);
        tv1=(TextView) findViewById(R.id.tv1);
        tv2=(TextView)findViewById(R.id.tv2);

        lay_filter_sort_main.setVisibility(View.GONE);
        layMain.setVisibility(View.GONE);
        lay2.setVisibility(View.GONE);

        if (session.isLoggedIn())
        {
            getuserdetails();
            getCartCount();

        }

        value=getIntent().getStringExtra("value");

        if (value!=null && value.equals("brand")){
            typeType="3";
            typeId = getIntent().getStringExtra("brand_id");
            headerName = getIntent().getStringExtra("brand_name_english");

        }else if (value!=null && value.equals("category")) {
            typeType="1";
            typeId = getIntent().getStringExtra("category_id");
            headerName = getIntent().getStringExtra("category_name");
        }
        else if (value!=null && value.equals("subcategory"))
        {
            typeType="2";
            typeId = getIntent().getStringExtra("subcategory_id");
            headerName = getIntent().getStringExtra("subcategory_name_english");
        }
        else if (value!=null && value.equals("slider"))
        {
            typeType="4";
            typeId = getIntent().getStringExtra("slider_url");
            headerName="Products";
        }
        tvHeader.setText(headerName);

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

                        Intent i = new Intent(ProductSubListing.this, Cart.class);
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
                    Intent i = new Intent(ProductSubListing.this, Search.class);
                    startActivity(i);
                }
                catch (Exception ignored)
                {
                }
            }
        });

        lay_Sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    try
                    {
                        final Dialog dialogs = new Dialog(ProductSubListing.this);
                        dialogs.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        if(dialogs.getWindow()!=null)
                        {
                            dialogs.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            dialogs.getWindow().setGravity(Gravity.CENTER);
                            dialogs.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                        }
                        dialogs.setContentView(R.layout.sort_dialog);
                        dialogs.setCancelable(false);
                        dialogs.setCanceledOnTouchOutside(false);
                        LinearLayout lay_close = dialogs.findViewById(R.id.lay_close);

                        lay_close.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {

                                    dialogs.dismiss();
                                }
                                catch (Exception ignored)
                                {

                                }
                            }
                        });
                        final TextView tv1 = dialogs.findViewById(R.id.tv1_new);
                        final TextView tv2 = dialogs.findViewById(R.id.tv2_lh);
                        final TextView tv3 = dialogs.findViewById(R.id.tv3_hl);


                        tv1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    if (CheckNetwork.isInternetAvailable(ProductSubListing.this))
                                    {
                                        sort_type = 1;
                                        resetProducts();
                                    }
                                    else
                                    {
                                        Snackbar snackbar = Snackbar
                                                .make(layMain, "No Internet Connection", Snackbar.LENGTH_LONG);
                                        View sbView = snackbar.getView();
                                        sbView.setBackgroundResource(R.color.colorPrimaryDark);
                                        snackbar.show();
                                    }

                                    dialogs.dismiss();
                                }
                                catch (Exception ignored)
                                {

                                }
                            }
                        });

                        tv2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    if (CheckNetwork.isInternetAvailable(ProductSubListing.this))
                                    {
                                        sort_type = 2;
                                        resetProducts();
                                    }
                                    else
                                    {
                                        Snackbar snackbar = Snackbar
                                                .make(layMain, "No Internet Connection", Snackbar.LENGTH_LONG);
                                        View sbView = snackbar.getView();
                                        sbView.setBackgroundResource(R.color.colorPrimaryDark);
                                        snackbar.show();
                                    }

                                    dialogs.dismiss();
                                }
                                catch (Exception ignored)
                                {

                                }

                            }
                        });

                        tv3.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {

                                    if (CheckNetwork.isInternetAvailable(ProductSubListing.this))
                                    {
                                        sort_type = 3;
                                        resetProducts();
                                    }
                                    else
                                    {
                                        Snackbar snackbar = Snackbar
                                                .make(layMain, "No Internet Connection", Snackbar.LENGTH_LONG);
                                        View sbView = snackbar.getView();
                                        sbView.setBackgroundResource(R.color.colorPrimaryDark);
                                        snackbar.show();
                                    }

                                    dialogs.dismiss();
                                }
                                catch (Exception ignored)
                                {

                                }

                            }
                        });

                        dialogs.show();

                    }
                    catch (Exception ignored)
                    {

                    }

                }
            }
        });

        lay_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try
                {
                    listFilterBrandNew.clear();
                    listFilterCategoryNew.clear();
                    listFilterBrandNew.addAll(listFilterBrand);
                    listFilterCategoryNew.addAll(listFilterCategory);

                    final Dialog dialogs = new Dialog(ProductSubListing.this, R.style.AppTheme_NoActionBar);
                    dialogs.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    if( dialogs.getWindow()!=null)
                    {
                        dialogs.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialogs.getWindow().setGravity(Gravity.CENTER);
                        dialogs.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                    }
                    dialogs.setContentView(R.layout.filter);
                    dialogs.setCancelable(false);
                    dialogs.setCanceledOnTouchOutside(false);
                    dialogs.show();

                    LinearLayout layback = dialogs.findViewById(R.id.lay_close);
                    TextView cancel = dialogs.findViewById(R.id.tv_clear);
                    TextView done = dialogs.findViewById(R.id.tv_done);
                    TextView clear=dialogs.findViewById(R.id.clear_filter);

                    TextView brands = dialogs.findViewById(R.id.brandstv_Id);
                    rv_filtersub = dialogs.findViewById(R.id.rv_sublist);

                    TextView category = dialogs.findViewById(R.id.categorytv_Id);
                    RecyclerView.LayoutManager layoutManager1;

                    rv_filtersub.setHasFixedSize(true);
                    layoutManager1 = new LinearLayoutManager(ProductSubListing.this, LinearLayoutManager.VERTICAL, false);
                    rv_filtersub.setLayoutManager(layoutManager1);
                    rv_filtersub.setItemAnimator(new DefaultItemAnimator());
                    rv_filtersub.setItemViewCacheSize(20);
                    rv_filtersub.setDrawingCacheEnabled(true);
                    rv_filtersub.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

                    clear.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try
                            {
                                listFilterCategoryNew.clear();
                                listFilterBrandNew.clear();
                                if(brandsAdapter!=null)
                                {
                                    brandsAdapter.notifyDataSetChanged();
                                }
                                if(categoryAdapter!=null)
                                {
                                    categoryAdapter.notifyDataSetChanged();
                                }
                            }
                            catch (Exception ignored)
                            {

                            }

                        }
                    });

                    done.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try
                            {
                                listFilterBrand.clear();
                                listFilterCategory.clear();
                                listFilterBrand.addAll(listFilterBrandNew);
                                listFilterCategory.addAll(listFilterCategoryNew);
                                listFilterBrandNew.clear();
                                listFilterCategoryNew.clear();
                                if(listFilterBrand.isEmpty() && listFilterCategory.isEmpty())
                                {
                                    filter_done=0;
                                }
                                else
                                {
                                    filter_done=1;
                                }
                                filteredproducts(listFilterBrand,listFilterCategory,listProduct);
                                dialogs.dismiss();

                            }
                            catch (Exception ignored)
                            {

                            }

                        }
                    });

                    category.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try
                            {
                                categorylisting();
                            }
                            catch (Exception ignored)
                            {

                            }

                        }
                    });
                    try
                    {
                        brandslisting();
                    }
                    catch (Exception ignored)
                    {

                    }
                    brands.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try
                            {
                                brandslisting();
                            }
                            catch (Exception ignored)
                            {

                            }

                        }
                    });

                    layback.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try
                            {
                                dialogs.dismiss();
                            }
                            catch (Exception ignored)
                            {

                            }

                        }
                    });

                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try
                            {
                                dialogs.dismiss();
                            }
                            catch (Exception ignored)
                            {

                            }

                        }
                    });
                }
                catch (Exception ignored)
                {

                }
            }
        });

        rv_id.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    try
                    {
                        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                        if(layoutManager!=null)
                        {
                            int totalItemCount = layoutManager.getItemCount();
                            int lastVisible = layoutManager.findLastVisibleItemPosition();

                            boolean endHasBeenReached = lastVisible+ 5  >= totalItemCount;
                            if (totalItemCount > 0 && endHasBeenReached) {
                                if (loading)
                                {
                                    if (CheckNetwork.isInternetAvailable(ProductSubListing.this))
                                    {
                                        start = start + 30;
                                        end = end + 30;
                                        loading=false;
                                        getProducts();
                                    }
                                }
                            }
                        }

                    } catch (Exception ignored) {

                    }
                }
            }
        });

        tv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try
                {
                    if(CheckNetwork.isInternetAvailable(ProductSubListing.this))
                    {
                        lay_filter_sort_main.setVisibility(View.GONE);
                        layMain.setVisibility(View.GONE);
                        lay2.setVisibility(View.GONE);
                        getProducts();
                    }
                }
                catch (Exception ignored)
                {

                }

            }
        });

        if(CheckNetwork.isInternetAvailable(ProductSubListing.this))
        {
            getProducts();
        }
        else
        {
            layMain.setVisibility(View.GONE);
            lay2.setVisibility(View.VISIBLE);
            noimg.setImageResource(R.drawable.no_internet);
            tv1.setText(getResources().getString(R.string.nonet));
            tv2.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try {
            finish();
        }
        catch (Exception ignored)
        {

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

    public void brandslisting()
    {
        if(listBrands.isEmpty())
        {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, getResources().getString(R.string.brands_url), new Response.Listener<String>()
            {
                @Override
                public void onResponse(String s)
                {

                    if(s!=null && s.length()>0)
                    {
                        try
                        {
                            JSONObject jsonObject = new JSONObject(s);
                            String response= jsonObject.getString("Response");
                            if(response.contains("Success"))
                            {
                                listBrands.clear();
                                JSONArray result=jsonObject.getJSONArray("Result");
                                for (int i=0;i<result.length();i++)
                                {
                                    JSONObject data = result.getJSONObject(i);
                                    String brand_name_english = URLDecoder.decode(data.getString("brand_name_english"),"utf-8");
                                    String brand_id = URLDecoder.decode(data.getString("brand_id"),"utf-8");

                                    HashMap<String, String> map = new HashMap<>();
                                    map.put("brand_name_english", brand_name_english);
                                    map.put("brand_id", brand_id);
                                    listBrands.add(map);
                                }

                                if (!listBrands.isEmpty())
                                {
                                    Collections.sort(listBrands, new Comparator<HashMap<String, String>>() {
                                        @Override
                                        public int compare(HashMap<String, String> s1, HashMap<String, String> s2) {
                                            return s1.get("brand_name_english").compareToIgnoreCase(s2.get("brand_name_english"));
                                        }
                                    });

                                    brandsAdapter=new BrandsAdapter(ProductSubListing.this,listBrands);
                                    rv_filtersub.setAdapter(brandsAdapter);
                                    brandsAdapter.notifyDataSetChanged();
                                }
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


                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    return new HashMap<>();
                }
            };
            RequestQueue requestQueue   = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);
        }
        else
        {
            brandsAdapter=new BrandsAdapter(ProductSubListing.this,listBrands);
            rv_filtersub.setAdapter(brandsAdapter);
            brandsAdapter.notifyDataSetChanged();
        }

    }

    private class BrandsAdapter extends RecyclerView.Adapter<BrandsAdapter.ViewHolder> {

        ArrayList<HashMap<String, String>> alNameA;
        Context context;

        public BrandsAdapter(Context context, ArrayList<HashMap<String, String>> alNameA) {
            super();
            this.context=context;
            this.alNameA=alNameA;
        }
        @NonNull
        @Override
        public BrandsAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.list_filter_sub, viewGroup, false);
            return new BrandsAdapter.ViewHolder(v);
        }
        @Override
        public void onBindViewHolder(final BrandsAdapter.ViewHolder viewHolder, final int i)
        {
            final String brand_name_english = alNameA.get(i).get("brand_name_english");
            final String brand_id = alNameA.get(i).get("brand_id");

            viewHolder.name.setText(brand_name_english);

            if (!listFilterBrandNew.isEmpty())
            {
                for(int j=0;j<listFilterBrandNew.size();j++)
                {
                    String filterBrand=listFilterBrandNew.get(j).get("brand_id");
                    if (filterBrand != null && filterBrand.equalsIgnoreCase(brand_id))
                    {
                        viewHolder.cb_filter_sub.setChecked(true);
                    }
                }
            }

            if(alNameA.size()==1)
            {
                viewHolder.cb_filter_sub.setVisibility(View.GONE);
            }
            else
            {
                viewHolder.cb_filter_sub.setVisibility(View.VISIBLE);
            }

            viewHolder.cb_filter_sub.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
            {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    try {
                        if (isChecked) {
                            HashMap<String, String> news1 = new HashMap<>();
                            news1.put("brand_id", brand_id);
                            news1.put("type", "" + i);
                            news1.put("name", brand_name_english);
                            listFilterBrandNew.add(news1);

                            HashSet<HashMap<String, String>> hashSetC = new HashSet<HashMap<String, String>>(listFilterBrandNew);
                            listFilterBrandNew.clear();
                            listFilterBrandNew.addAll(hashSetC);

                        } else {
                            HashMap<String, String> news1 = new HashMap<>();
                            news1.put("brand_id", brand_id);
                            news1.put("type", "" + i);
                            news1.put("name", brand_name_english);
                            listFilterBrandNew.remove(news1);

                            HashSet<HashMap<String, String>> hashSetC = new HashSet<HashMap<String, String>>(listFilterBrandNew);
                            listFilterBrandNew.addAll(hashSetC);
                        }
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
            TextView name;
            LinearLayout lay_main;
            CheckBox cb_filter_sub;

            public ViewHolder(View itemView)
            {
                super(itemView);
                name=itemView.findViewById(R.id.nameid);
                lay_main=itemView.findViewById(R.id.lay_main1);
                cb_filter_sub=itemView.findViewById(R.id.cb_filter_sub1);
            }
        }
    }

    public void categorylisting()
    {
        if(listCategory.isEmpty())
        {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, getResources().getString(R.string.url_category), new Response.Listener<String>()
            {
                @Override
                public void onResponse(String s)
                {

                    if(s!=null && s.length()>0)
                    {
                        try
                        {
                            JSONObject jsonObj = new JSONObject(s);
                            String response = jsonObj.getString("Response");
                            if (response.contains("Success"))
                            {
                                listCategory.clear();
                                JSONArray result=jsonObj.getJSONArray("Result");

                                for (int i=0;i<result.length();i++)
                                {
                                    JSONObject data = result.getJSONObject(i);
                                    String category_name_english = URLDecoder.decode(data.getString("category_name_english"),"utf-8");
                                    String category_id = URLDecoder.decode(data.getString("category_id"),"utf-8");

                                    HashMap<String, String> map = new HashMap<>();
                                    map.put("category_name_english", category_name_english);
                                    map.put("category_id", category_id);
                                    listCategory.add(map);

                                }

                                if (!listCategory.isEmpty())
                                {
                                    Collections.sort(listCategory, new Comparator<HashMap<String, String>>() {
                                        @Override
                                        public int compare(HashMap<String, String> s1, HashMap<String, String> s2) {
                                            return s1.get("category_name_english").compareToIgnoreCase(s2.get("category_name_english"));
                                        }
                                    });

                                    categoryAdapter=new CategoryAdapter(listCategory,ProductSubListing.this);
                                    rv_filtersub.setAdapter(categoryAdapter);
                                    categoryAdapter.notifyDataSetChanged();
                                }
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


                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    return new HashMap<>();
                }
            };
            RequestQueue requestQueue   = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);
        }
        else
        {
            categoryAdapter=new CategoryAdapter(listCategory,ProductSubListing.this);
            rv_filtersub.setAdapter(categoryAdapter);
            categoryAdapter.notifyDataSetChanged();

        }

    }

    class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder>
    {
        ArrayList<HashMap<String, String>> alNameA;
        Context context;

        public CategoryAdapter(ArrayList<HashMap<String, String>> alNameA, Context context) {
            this.alNameA = alNameA;
            this.context = context;
        }

        @NonNull
        @Override
        public CategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_filter_sub, parent, false);
            return new CategoryAdapter.ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull CategoryAdapter.ViewHolder viewHolder, int i) {
            {
                final String category_id = alNameA.get(i).get("category_id");
                final String category_name_english = alNameA.get(i).get("category_name_english");

                viewHolder.name.setText(category_name_english);

                if (!listFilterCategoryNew.isEmpty())
                {
                    for(int j=0;j<listFilterCategoryNew.size();j++)
                    {
                        String filterCat=listFilterCategoryNew.get(j).get("category_id");
                        if (filterCat!=null && filterCat.equalsIgnoreCase(category_id))
                        {
                            viewHolder.cb_filter_sub.setChecked(true);
                        }
                    }
                }

                if(alNameA.size()==1)
                {
                    viewHolder.cb_filter_sub.setVisibility(View.GONE);
                }
                else
                {
                    viewHolder.cb_filter_sub.setVisibility(View.VISIBLE);
                }

                viewHolder.cb_filter_sub.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
                {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
                    {
                        try
                        {
                            if ( isChecked )
                            {
                                HashMap<String, String> news1 = new HashMap<>();
                                news1.put("category_id", category_id);
                                news1.put("type", ""+i);
                                news1.put("category_name_english", category_name_english);
                                listFilterCategoryNew.add(news1);

                                HashSet<HashMap<String, String>> hashSetC = new HashSet<HashMap<String, String>>(listFilterCategoryNew);
                                listFilterCategoryNew.clear();
                                listFilterCategoryNew.addAll(hashSetC);
                            }
                            else
                            {
                                HashMap<String, String> news1 = new HashMap<>();
                                news1.put("category_id", category_id);
                                news1.put("type", ""+i);
                                news1.put("category_name_english", category_name_english);
                                listFilterCategoryNew.remove(news1);

                                HashSet<HashMap<String, String>> hashSetC = new HashSet<HashMap<String, String>>(listFilterCategoryNew);
                                listFilterCategoryNew.addAll(hashSetC);
                            }

                        }
                        catch (Exception ignored)
                        {

                        }
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return alNameA.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public TextView name;
            LinearLayout lay_main;
            CheckBox cb_filter_sub;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                name=itemView.findViewById(R.id.nameid);
                lay_main=itemView.findViewById(R.id.lay_main1);
                cb_filter_sub=itemView.findViewById(R.id.cb_filter_sub1);

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
                                    .make(layMain, "Added to Cart", Snackbar.LENGTH_LONG);
                            View sbView = snackbar.getView();
                            sbView.setBackgroundResource(R.color.green);
                            snackbar.show();

                        }
                        else if (response.equalsIgnoreCase("cart updated"))
                        {
                            Snackbar snackbar = Snackbar
                                    .make(layMain, "Already Added to Cart", Snackbar.LENGTH_LONG);
                            View sbView = snackbar.getView();
                            sbView.setBackgroundResource(R.color.colorPrimaryDark);
                            snackbar.show();
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
                    catch (Exception e)
                    {
                        Snackbar snackbar = Snackbar
                                .make(layMain, "Something went wrong!", Snackbar.LENGTH_LONG);
                        View sbView = snackbar.getView();
                        sbView.setBackgroundResource(R.color.colorPrimaryDark);
                        snackbar.show();
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
        })
        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(getResources().getString(R.string.param_addcart1), product_id);
                params.put(getResources().getString(R.string.param_addcart2), customer_id);
                params.put(getResources().getString(R.string.param_addcart3), quantity);
                return params;
            }
        };

        RequestQueue requestQueue   = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    private void resetProducts() {

        start=0;
        end=30;
        loading=false;

        lay_filter_sort_main.setVisibility(View.GONE);
        layMain.setVisibility(View.GONE);
        lay2.setVisibility(View.GONE);

        listFilterBrand.clear();
        listFilterCategory.clear();

        listProduct.clear();
        listFilteredProducts.clear();

        getProducts();

    }

    private void getProducts() {

        if (dialog_pb!=null && !dialog_pb.isShowing() && start==0) {
            dialog_pb.show();
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, getResources().getString(R.string.url_products), new Response.Listener<String>()
        {
            @Override
            public void onResponse(String s)
            {
                if (dialog_pb.isShowing()) {
                    dialog_pb.dismiss();
                }
                if (s != null && s.length()>0)
                {
                    try {
                        JSONObject jsonObject = new JSONObject(s);
                        String response = jsonObject.getString("response");
                        if (response.equalsIgnoreCase("success"))
                        {
                            JSONArray result = jsonObject.getJSONArray("products");
                            for (int i = 0; i < result.length(); i++) {
                                JSONObject data = result.getJSONObject(i);
                                String product_id = URLDecoder.decode(data.getString("product_id"), "utf-8");
                                String mrp = URLDecoder.decode(data.getString("mrp"), "utf-8");
                                String stock_qty = URLDecoder.decode(data.getString("stock_qty"), "utf-8");
                                String cost = URLDecoder.decode(data.getString("cost"), "utf-8");
                                String product_name_en = data.getString("product_name_en");
                                try {
                                    product_name_en = URLDecoder.decode(data.getString("product_name_en"), "utf-8");
                                }
                                catch (Exception ignored)
                                {

                                }
                                String product_image_main = URLDecoder.decode(data.getString("product_image_main"), "utf-8");
                                String cat_id = URLDecoder.decode(data.getString("category_id"), "utf-8");
                                String brand_id = URLDecoder.decode(data.getString("brand_id"), "utf-8");

                                HashMap<String, String> news = new HashMap<>();
                                news.put("product_id", product_id);
                                news.put("mrp", mrp);
                                news.put("stock_qty", stock_qty);
                                news.put("cost", cost);
                                news.put("product_name_en", product_name_en);
                                news.put("product_image_main", product_image_main);
                                news.put("cat_id", cat_id);
                                news.put("brand_id", brand_id);
                                listProduct.add(news);
                            }

                            loading=true;
                        }
                        else if(response.equalsIgnoreCase("failed"))
                        {
                            loading=false;
                            if(start==0)
                            {
                                lay_filter_sort_main.setVisibility(View.GONE);
                                Snackbar snackbar = Snackbar
                                        .make(layMain, "No products found!!!", Snackbar.LENGTH_LONG);
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
                else
                {
                    if(start==0)
                    {
                        lay_filter_sort_main.setVisibility(View.GONE);
                        Snackbar snackbar = Snackbar
                                .make(layMain, "No products found!!!", Snackbar.LENGTH_LONG);
                        View sbView = snackbar.getView();
                        sbView.setBackgroundResource(R.color.colorPrimaryDark);
                        snackbar.show();
                    }

                }

                if (!listProduct.isEmpty())
                {
                    lay_filter_sort_main.setVisibility(View.VISIBLE);
                    layMain.setVisibility(View.VISIBLE);


                    if(start==0)
                    {
                        listFilteredProducts.addAll(listProduct);
                        adapter = new ProductsAdapter(getApplicationContext(), listFilteredProducts);
                        rv_id.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }
                    else
                    {
                        filteredproducts(listFilterBrand,listFilterCategory,listProduct);
                    }


                }
                else
                {
                    if(start==0)
                    {

                        lay_filter_sort_main.setVisibility(View.GONE);
                        layMain.setVisibility(View.GONE);
                        lay2.setVisibility(View.VISIBLE);
                        noimg.setImageResource(R.drawable.product_not_found);
                        tv1.setText(getResources().getString(R.string.no_products_found));
                        tv2.setVisibility(View.GONE);
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (dialog_pb.isShowing())
                {
                    dialog_pb.dismiss();
                }
                if(start==0)
                {

                    lay_filter_sort_main.setVisibility(View.GONE);
                    layMain.setVisibility(View.GONE);
                    lay2.setVisibility(View.VISIBLE);
                    noimg.setImageResource(R.drawable.product_not_found);
                    tv1.setText(getResources().getString(R.string.no_products_found));
                    tv2.setVisibility(View.GONE);
                }
            }
        })
        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", typeId);
                params.put("type", typeType);
                params.put("start", String.valueOf(start));
                params.put("end", String.valueOf(end));
                params.put("sort_type", String.valueOf(sort_type));
                return params;
            }
        };

        RequestQueue requestQueue   = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    private class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ViewHolder> {

        ArrayList<HashMap<String, String>> alNameA;
        Context context;

        public ProductsAdapter(Context context, ArrayList<HashMap<String, String>> alNameA) {
            this.alNameA = alNameA;
            this.context = context;
        }

        @NonNull
        @Override
        public ProductsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_search1, parent, false);
            return new ProductsAdapter.ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ProductsAdapter.ViewHolder holder, int i) {

            String product_id =alNameA.get(i).get("product_id");
            String mrp = alNameA.get(i).get("mrp");
            String cost = alNameA.get(i).get("cost");
            String product_name_en =alNameA.get(i).get("product_name_en");
            String product_image_main = alNameA.get(i).get("product_image_main");

            try
            {
                if(product_image_main!=null && product_image_main.length()>1)
                {
                    String product_image_main1 = "https://majlisstore.com/crm/public/uploads/product_images/"+product_image_main;
                    Picasso.get().load(product_image_main1).error(R.drawable.logo_h).into(holder.imageView);
                }
                else
                {
                    Picasso.get().load(R.drawable.logo_h).error(R.drawable.logo_h).into(holder.imageView);
                }
            }
            catch (Exception ignored)
            {

            }

            String textCost="QAR " + cost;
            holder.productoffprice.setText(textCost);
            holder.name.setText(product_name_en);
            holder.mrp.setPaintFlags(holder.mrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            String textMrp="QAR " + mrp;
            holder.mrp.setText(textMrp);

            holder.linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try
                    {
                        Intent intent = new Intent(ProductSubListing.this, ProductDetails.class);
                        intent.putExtra("product_id", product_id);
                        startActivity(intent);
                    }
                    catch (Exception ignored)
                    {

                    }

                }
            });

            holder.addtocart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if(session.isLoggedIn())
                        {
                            if(CheckNetwork.isInternetAvailable(context))
                            {
                                addtocart(product_id,customer_id,"1");
                            }
                            else
                            {
                                Snackbar snackbar = Snackbar
                                        .make(layMain, "No Internet Connection", Snackbar.LENGTH_LONG);
                                View sbView = snackbar.getView();
                                sbView.setBackgroundResource(R.color.colorPrimaryDark);
                                snackbar.show();
                            }

                        }
                        else
                        {
                            Snackbar snackbar = Snackbar
                                    .make(layMain, "You are not logged in", Snackbar.LENGTH_LONG);
                            View sbView = snackbar.getView();
                            sbView.setBackgroundResource(R.color.colorPrimaryDark);
                            snackbar.show();

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    try
                                    {
                                        Intent i = new Intent(ProductSubListing.this, Login.class);
                                        startActivity(i);
                                        overridePendingTransition(R.anim.slide_in1, R.anim.slide_out1);
                                    }
                                    catch (Exception ignored)
                                    {

                                    }
                                }
                            }, 500);
                        }
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

            ImageView imageView;
            TextView addtocart, productoffprice, mrp, name;
            LinearLayout linearLayout;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                addtocart = itemView.findViewById(R.id.tv_cart);
                productoffprice = itemView.findViewById(R.id.productoffprice_ls);
                mrp = itemView.findViewById(R.id.price_ls);
                imageView = itemView.findViewById(R.id.image_ls);
                name = itemView.findViewById(R.id.name_ls);
                linearLayout = itemView.findViewById(R.id.lay_item);
            }
        }
    }

    public void filteredproducts(ArrayList<HashMap<String,String>> brand,ArrayList<HashMap<String,String>> category,ArrayList<HashMap<String,String>> products)
    {
        listFilteredProducts.clear();

        if(!brand.isEmpty())
        {
            ArrayList<HashMap<String,String>> listBrandProd=new ArrayList<>();
            for(int i=0;i<products.size();i++)
            {
                String product_b_id=products.get(i).get("brand_id");

                for(int j=0;j<brand.size();j++)
                {
                    String b_id=brand.get(j).get("brand_id");

                    if(b_id!=null &&  b_id.equals(product_b_id))
                    {
                        HashMap<String, String> news = new HashMap<>();
                        news.put("product_id", products.get(i).get("product_id"));
                        news.put("mrp", products.get(i).get("mrp"));
                        news.put("stock_qty", products.get(i).get("stock_qty"));
                        news.put("cost", products.get(i).get("cost"));
                        news.put("product_name_en", products.get(i).get("product_name_en"));
                        news.put("product_image_main", products.get(i).get("product_image_main"));
                        news.put("cat_id", products.get(i).get("cat_id"));
                        news.put("brand_id", products.get(i).get("brand_id"));
                        listBrandProd.add(news);
                    }
                }
            }

            if(!listBrandProd.isEmpty())
            {
                listFilteredProducts.addAll(listBrandProd);
            }

        }

        if(!category.isEmpty())
        {
            ArrayList<HashMap<String,String>> listCatProd=new ArrayList<>();
            for(int k=0;k<products.size();k++)
            {
                String product_c_id=products.get(k).get("cat_id");

                for(int j=0;j<category.size();j++)
                {
                    String c_id=category.get(j).get("category_id");

                    if(c_id != null && c_id.equalsIgnoreCase(product_c_id))
                    {
                        HashMap<String, String> news = new HashMap<>();
                        news.put("product_id", products.get(k).get("product_id"));
                        news.put("mrp", products.get(k).get("mrp"));
                        news.put("stock_qty", products.get(k).get("stock_qty"));
                        news.put("cost", products.get(k).get("cost"));
                        news.put("product_name_en", products.get(k).get("product_name_en"));
                        news.put("product_image_main", products.get(k).get("product_image_main"));
                        news.put("cat_id", products.get(k).get("cat_id"));
                        news.put("brand_id", products.get(k).get("brand_id"));
                        listCatProd.add(news);
                    }
                }
            }
            if(!listCatProd.isEmpty())
            {
                listFilteredProducts.addAll(listCatProd);
            }
        }

        if (listFilteredProducts.isEmpty())
        {
            listFilterBrand.clear();
            listFilterCategory.clear();
            if(filter_done==1)
            {
                Snackbar snackbar = Snackbar
                        .make(layMain, "No products found!!!", Snackbar.LENGTH_LONG);
                View sbView = snackbar.getView();
                sbView.setBackgroundResource(R.color.colorPrimaryDark);
                snackbar.show();
                filter_done=0;
            }

            listFilteredProducts.addAll(products);
        }

        HashSet<HashMap<String, String>> hashSetC = new HashSet<>(listFilteredProducts);
        listFilteredProducts.clear();
        listFilteredProducts.addAll(hashSetC);

        if(sort_type==1)
        {
            Collections.sort(listFilteredProducts,new Comparator<HashMap<String,String>>(){
                public int compare(HashMap<String,String> mapping1,
                                   HashMap<String,String> mapping2){
                    return Float.compare(Float.parseFloat(mapping2.get("product_id")),Float.parseFloat(mapping1.get("product_id")));
                }});
        }
        else if(sort_type==2)
        {
            Collections.sort(listFilteredProducts,new Comparator<HashMap<String,String>>(){
                public int compare(HashMap<String,String> mapping1,
                                   HashMap<String,String> mapping2){
                    return Float.compare(Float.parseFloat(mapping1.get("cost")),Float.parseFloat(mapping2.get("cost")));

                }});
        }
        else if(sort_type==3)
        {
            Collections.sort(listFilteredProducts,new Comparator<HashMap<String,String>>(){
                public int compare(HashMap<String,String> mapping1,
                                   HashMap<String,String> mapping2){
                    return Float.compare(Float.parseFloat(mapping2.get("cost")),Float.parseFloat(mapping1.get("cost")));

                }});
        }

        adapter.notifyDataSetChanged();

        if(listFilteredProducts.size()<5 && loading)
        {
            if (CheckNetwork.isInternetAvailable(ProductSubListing.this))
            {
                start = start + 30;
                end = end + 30;
                loading=false;
                getProducts();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
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
