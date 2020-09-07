package com.majlisstore.majlisstore.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.majlisstore.majlisstore.R;
import com.majlisstore.majlisstore.adapters.BrandAdapter;
import com.majlisstore.majlisstore.adapters.CategoryAdapter;
import com.majlisstore.majlisstore.adapters.SliderAdapter;
import com.majlisstore.majlisstore.helpers.CheckNetwork;
import com.majlisstore.majlisstore.helpers.CustomTypefaceSpan;
import com.majlisstore.majlisstore.helpers.SessionManager;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class Dashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    SessionManager session;

    Typeface fontRegular;

    DrawerLayout drawer_layout;
    BottomNavigationView bottom_navigation;
    NavigationView nav_view;

    Menu menu;
    ImageView imgNav;
    TextView tvNav1, tvNav2;

    LinearLayout layDashboard;
    ImageView imgMenu, imgSearch,cartid;
    TextView tvCartcount;

    LinearLayout laySlider;
    ViewPager vpSlider;
    int currentSlider = -1;

    LinearLayout layCat;
    TextView tv_cat;
    RecyclerView rvCat;
    RecyclerView.LayoutManager lmCat;

    LinearLayout laysel;
    TextView tv_sel;
    RecyclerView rvSel;
    RecyclerView.LayoutManager lmSel;

    LinearLayout laySellingProduct;
    TextView tvSellingProduct;
    RecyclerView rvSellingProduct;
    RecyclerView.LayoutManager lmSellingProduct;
    String selectedCategory_id = "0";
    int selectedSelling = -1;

    LinearLayout layFeatured;
    TextView tvFeatured;
    RecyclerView rvFeatured;
    RecyclerView.LayoutManager lmFeatured;

    LinearLayout layLoonu;
    TextView tvLoonu;
    RecyclerView rvLoonu;
    RecyclerView.LayoutManager lmLoonu;

    LinearLayout layBanner1, layBanner2;
    ViewPager vpBanner1, vpBanner2;
    int currentBanner1 = -1, currentBanner2 = -1;

    LinearLayout layBrand;
    RecyclerView rvBrand;
    RecyclerView.LayoutManager lmBrand;

    LinearLayout layRecently;
    TextView tvRecently;
    RecyclerView rvRecently;
    RecyclerView.LayoutManager lmRecently;

    Dialog dialog_pb;

    boolean doubleBackToExitPressedOnce = false;

    String customer_id, customer_name, customer_mob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLocale();
        setContentView(R.layout.activity_dashboard);

        session=new SessionManager(Dashboard.this);

        fontRegular= ResourcesCompat.getFont(this,R.font.ubuntu_medium);

        dialog_pb = new Dialog(this);
        dialog_pb.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_pb.setContentView(R.layout.progressbar);
        if (dialog_pb.getWindow() != null) {
            dialog_pb.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        drawer_layout = findViewById(R.id.drawer_layout);

        bottom_navigation = findViewById(R.id.bottom_navigation);
        bottom_navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        bottom_navigation.setSelectedItemId(R.id.bot_nav_home);
        Menu bottomMenu = bottom_navigation.getMenu();
        for (int i = 0; i < bottomMenu.size(); i++) {
            MenuItem mi = bottomMenu.getItem(i);
            applyFontToMenuItem(mi);
        }

        nav_view = findViewById(R.id.nav_view);
        nav_view.setNavigationItemSelectedListener(this);
//        nav_view.setItemIconTintList(null);
        View header = nav_view.getHeaderView(0);
        imgNav = header.findViewById(R.id.imgNav);
        tvNav1 = header.findViewById(R.id.tvNav1);
        tvNav2 = header.findViewById(R.id.tvNav2);
        menu = nav_view.getMenu();
        showMenu();

        layDashboard = findViewById(R.id.layDashboard);
        imgMenu = findViewById(R.id.imgMenu);
        imgSearch = findViewById(R.id.imgSearch);
        cartid = findViewById(R.id.cartid);
        tvCartcount = findViewById(R.id.tvCartcount);

        imgMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (drawer_layout.isDrawerOpen(Gravity.LEFT)) {
                        drawer_layout.closeDrawer(Gravity.LEFT);
                    } else {
                        drawer_layout.openDrawer(Gravity.LEFT);
                    }
                } catch (Exception ignored) {

                }
            }
        });

        imgSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent i = new Intent(Dashboard.this, Search.class);
                    startActivity(i);
                } catch (Exception ignored) {

                }
            }
        });

        cartid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (session.isLoggedIn()) {
                        Intent i = new Intent(Dashboard.this, Cart.class);
                        startActivity(i);
                    } else {
                        Snackbar snackbar = Snackbar
                                .make(layDashboard, "You are not logged in", Snackbar.LENGTH_LONG);
                        View sbView = snackbar.getView();
                        sbView.setBackgroundResource(R.color.colorPrimaryDark);
                        snackbar.show();
                    }
                } catch (Exception ignored) {

                }
            }
        });

        if (session.isLoggedIn()) {
            getuserdetails();
            getCartCount();
        } else {
            tvNav1.setText(getResources().getString(R.string.navText1));
            tvNav2.setText(getResources().getString(R.string.navText2));
        }

        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowmanager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        windowmanager.getDefaultDisplay().getMetrics(displayMetrics);
        float deviceHeight = (float) (displayMetrics.heightPixels / 3);

        laySlider = findViewById(R.id.laySlider);
        vpSlider = findViewById(R.id.vpSlider);

        vpSlider.requestLayout();
        vpSlider.getLayoutParams().height = (int) deviceHeight;

        vpSlider.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                currentSlider = position;
            }

            @Override
            public void onPageScrolled(int pos, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int pos) {
            }
        });

        layCat = findViewById(R.id.layCat);
        rvCat = findViewById(R.id.rvCat);
        tv_cat = findViewById(R.id.tv_cat);
        rvCat.setHasFixedSize(true);
        lmCat = new LinearLayoutManager(Dashboard.this, LinearLayoutManager.HORIZONTAL, false);
        rvCat.setLayoutManager(lmCat);
        rvCat.setItemViewCacheSize(20);
        rvCat.setDrawingCacheEnabled(true);
        rvCat.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        laysel = findViewById(R.id.laysel);
        tv_sel = findViewById(R.id.tv_sel);
        rvSel = findViewById(R.id.rvSel);
        rvSel.setHasFixedSize(true);
        lmSel = new LinearLayoutManager(Dashboard.this, LinearLayoutManager.HORIZONTAL, false);
        rvSel.setLayoutManager(lmSel);
        rvSel.setItemViewCacheSize(20);
        rvSel.setDrawingCacheEnabled(true);
        rvSel.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        laySellingProduct = findViewById(R.id.laySellingProduct);
        tvSellingProduct = findViewById(R.id.tvSellingProduct);
        rvSellingProduct = findViewById(R.id.rvSellingProduct);
        rvSellingProduct.setHasFixedSize(true);
        lmSellingProduct = new LinearLayoutManager(Dashboard.this, LinearLayoutManager.HORIZONTAL, false);
        rvSellingProduct.setLayoutManager(lmSellingProduct);
        rvSellingProduct.setItemViewCacheSize(20);
        rvSellingProduct.setDrawingCacheEnabled(true);
        rvSellingProduct.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        layFeatured = findViewById(R.id.layFeatured);
        tvFeatured = findViewById(R.id.tvFeatured);
        rvFeatured = findViewById(R.id.rvFeatured);
        rvFeatured.setHasFixedSize(true);
        lmFeatured = new LinearLayoutManager(Dashboard.this, LinearLayoutManager.HORIZONTAL, false);
        rvFeatured.setLayoutManager(lmFeatured);
        rvFeatured.setItemViewCacheSize(20);
        rvFeatured.setDrawingCacheEnabled(true);
        rvFeatured.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        layLoonu = findViewById(R.id.layLoonu);
        tvLoonu = findViewById(R.id.tvLoonu);
        rvLoonu = findViewById(R.id.rvLoonu);
        rvLoonu.setHasFixedSize(true);
        lmLoonu = new LinearLayoutManager(Dashboard.this, LinearLayoutManager.HORIZONTAL, false);
        rvLoonu.setLayoutManager(lmLoonu);
        rvLoonu.setItemViewCacheSize(20);
        rvLoonu.setDrawingCacheEnabled(true);
        rvLoonu.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        layBanner1 = findViewById(R.id.layBanner1);
        layBanner2 = findViewById(R.id.layBanner2);
        vpBanner1 = findViewById(R.id.vpBanner1);
        vpBanner2 = findViewById(R.id.vpBanner2);

        vpBanner1.requestLayout();
        vpBanner1.getLayoutParams().height = (int) deviceHeight;
        vpBanner2.requestLayout();
        vpBanner2.getLayoutParams().height = (int) deviceHeight;
        vpBanner1.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                currentBanner1 = position;
            }

            @Override
            public void onPageScrolled(int pos, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int pos) {
            }
        });

        vpBanner2.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                currentBanner2 = position;
            }

            @Override
            public void onPageScrolled(int pos, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int pos) {
            }
        });

        layBrand = findViewById(R.id.layBrand);
        rvBrand = findViewById(R.id.rvBrand);
        rvBrand.setHasFixedSize(true);
        lmBrand = new LinearLayoutManager(Dashboard.this, LinearLayoutManager.HORIZONTAL, false);
        rvBrand.setLayoutManager(lmBrand);
        rvBrand.setItemViewCacheSize(20);
        rvBrand.setDrawingCacheEnabled(true);
        rvBrand.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        layRecently = findViewById(R.id.layRecently);
        tvRecently = findViewById(R.id.tvRecently);
        rvRecently = findViewById(R.id.rvRecently);
        rvRecently.setHasFixedSize(true);
        lmRecently = new LinearLayoutManager(Dashboard.this, LinearLayoutManager.HORIZONTAL, false);
        rvRecently.setLayoutManager(lmRecently);
        rvRecently.setItemViewCacheSize(20);
        rvRecently.setDrawingCacheEnabled(true);
        rvRecently.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        showSlider();
        showCategory();
        showSelling();
        showBanner();
        showBrand();

        if (CheckNetwork.isInternetAvailable(Dashboard.this)) {
            if (dialog_pb != null && !dialog_pb.isShowing()) {
                dialog_pb.show();
            }

            setDashboard();

            if (session.isLoggedIn()) {
                getRecently();
            }

        } else {
            Snackbar snackbar = Snackbar
                    .make(layDashboard, "No Internet Connection", Snackbar.LENGTH_LONG);
            View sbView = snackbar.getView();
            sbView.setBackgroundResource(R.color.colorPrimaryDark);
            snackbar.show();

        }

        updateApp();

    }

    private void applyFontToMenuItem(MenuItem mi) {
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("", fontRegular), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }

    private void showMenu() {
        menu.clear();
        menu.add(101, 101, 0, getString(R.string.menu_home)).setIcon(R.drawable.m_home);
        menu.add(101, 102, 0, getString(R.string.menu_cart)).setIcon(R.drawable.m_cart);
        menu.add(101, 103, 0, getString(R.string.menu_order)).setIcon(R.drawable.m_order);
        menu.add(101, 104, 0, getString(R.string.menu_search)).setIcon(R.drawable.m_search);
        menu.add(101, 105, 0, getString(R.string.menu_shop)).setIcon(R.drawable.m_shop);
        menu.add(101, 106, 0, getString(R.string.menu_settings)).setIcon(R.drawable.m_settings);
        menu.add(101, 107, 0, getString(R.string.menu_contactus)).setIcon(R.drawable.m_contactus);
        if (session.isLoggedIn()) {
            menu.add(101, 108, 0, getString(R.string.menu_signout)).setIcon(R.drawable.m_signup);
        } else {
            menu.add(101, 108, 0, getString(R.string.menu_signin)).setIcon(R.drawable.m_signin);
        }

        for (int i = 0; i < menu.size(); i++) {
            MenuItem mi = menu.getItem(i);
            applyFontToMenuItem(mi);
        }
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
            customer_name = userdata.getString("customer_name");
            customer_mob = userdata.getString("customer_mob");
            String text = "Hi," + customer_name;
            tvNav1.setText(text);
            tvNav2.setText(customer_mob);
        } catch (Exception ignored) {

        }
    }

    private void getCartCount() {
        if (!session.getCartQuantity().equals("") && !session.getCartQuantity().equals("0")) {
            if (Integer.parseInt(session.getCartQuantity()) < 10) {
                if (Integer.parseInt(session.getCartQuantity()) == 0) {
                    tvCartcount.setVisibility(View.INVISIBLE);
                } else {
                    tvCartcount.setVisibility(View.VISIBLE);
                    tvCartcount.setText(session.getCartQuantity());
                }
            } else {
                tvCartcount.setVisibility(View.VISIBLE);
                tvCartcount.setText("9+");
            }
        } else {
            tvCartcount.setVisibility(View.INVISIBLE);
        }
    }


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Intent i;
            switch (item.getItemId()) {

                case R.id.bot_nav_home:
                    return true;

                case R.id.bot_nav_video:
                    i = new Intent(Dashboard.this, Products.class);
                    startActivity(i);
                    return true;

                case R.id.bot_nav_notification:
                    if (session.isLoggedIn()) {
                        i = new Intent(Dashboard.this, Notifications.class);
                        startActivity(i);
                        return true;
                    } else {
                        Snackbar snackbar = Snackbar
                                .make(layDashboard, "You are not logged in", Snackbar.LENGTH_LONG);
                        View sbView = snackbar.getView();
                        sbView.setBackgroundResource(R.color.colorPrimaryDark);
                        snackbar.show();
                        return false;
                    }

                case R.id.bot_nav_account:
                    i = new Intent(Dashboard.this, Accounts.class);
                    startActivity(i);
                    return true;
            }
            return false;
        }
    };

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();

        if (id == 101) {
            try {
                Intent i = new Intent(Dashboard.this, Dashboard.class);
                startActivity(i);
            } catch (Exception ignored) {

            }

        } else if (id == 102) {
            try {
                if (session.isLoggedIn()) {
                    Intent i = new Intent(Dashboard.this, Cart.class);
                    startActivity(i);
                } else {
                    Snackbar snackbar = Snackbar
                            .make(layDashboard, "You are not logged in", Snackbar.LENGTH_LONG);
                    View sbView = snackbar.getView();
                    sbView.setBackgroundResource(R.color.colorPrimaryDark);
                    snackbar.show();
                }
            } catch (Exception ignored) {

            }
        } else if (id == 103) {
            try {
                if (session.isLoggedIn()) {
                    Intent i = new Intent(Dashboard.this, Orders.class);
                    startActivity(i);
                } else {
                    Snackbar snackbar = Snackbar
                            .make(layDashboard, "You are not logged in", Snackbar.LENGTH_LONG);
                    View sbView = snackbar.getView();
                    sbView.setBackgroundResource(R.color.colorPrimaryDark);
                    snackbar.show();
                }
            } catch (Exception ignored) {

            }
        } else if (id == 104) {
            try {
                Intent i = new Intent(Dashboard.this, Search.class);
                startActivity(i);
            } catch (Exception ignored) {

            }
        } else if (id == 105) {
            try {
                Intent i = new Intent(Dashboard.this, Products.class);
                startActivity(i);
            } catch (Exception ignored) {

            }
        } else if (id == 106) {
            try {
                Intent i = new Intent(Dashboard.this, Settings.class);
                startActivity(i);
            } catch (Exception ignored) {

            }
        } else if (id == 107) {
            try {
                Intent i = new Intent(Dashboard.this, ContactUs.class);
                startActivity(i);
            } catch (Exception ignored) {

            }
        } else if (id == 108) {
            try {
                if (session.isLoggedIn()) {
                    signout();
                } else {
                    Intent i = new Intent(Dashboard.this, Login.class);
                    startActivity(i);
                }
            } catch (Exception ignored) {

            }

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;

    }

    private void signout() {
        try {
            final Dialog dialog_msg = new Dialog(Dashboard.this);
            dialog_msg.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog_msg.setContentView(R.layout.message);
            if (dialog_msg.getWindow() != null) {
                dialog_msg.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            }
            dialog_msg.setCancelable(false);
            dialog_msg.setCanceledOnTouchOutside(false);

            TextView tv1 = (TextView) dialog_msg.findViewById(R.id.tv1);
            tv1.setText(getResources().getString(R.string.signout1));
            TextView tv2 = (TextView) dialog_msg.findViewById(R.id.tv2);
            tv2.setText(getResources().getString(R.string.signout2));
            TextView tv3 = (TextView) dialog_msg.findViewById(R.id.tv3);
            tv3.setText(R.string.yes);
            TextView tv4 = (TextView) dialog_msg.findViewById(R.id.tv4);
            tv4.setText(R.string.cancel);
            tv4.setVisibility(View.VISIBLE);

            tv3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        dialog_msg.dismiss();
                        session.logoutUser();
                        finish();

                    } catch (Exception ignored) {

                    }
                }
            });

            tv4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        dialog_msg.dismiss();
                    } catch (Exception ignored) {

                    }
                }
            });

            dialog_msg.show();
        } catch (Exception ignored) {

        }
    }

    @Override
    public void onBackPressed() {
        try {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(Gravity.LEFT)) {
                drawer.closeDrawer(Gravity.LEFT);
            } else {
                if (doubleBackToExitPressedOnce) {
                    super.onBackPressed();
                    ActivityCompat.finishAffinity(this);
                } else {
                    this.doubleBackToExitPressedOnce = true;

                    Snackbar snackbar = Snackbar
                            .make(layDashboard, "Please click BACK again to exit", Snackbar.LENGTH_LONG);
                    View sbView = snackbar.getView();
                    sbView.setBackgroundResource(R.color.colorPrimaryDark);
                    snackbar.show();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            doubleBackToExitPressedOnce = false;
                        }
                    }, 5000);
                }
            }
        } catch (Exception ignored) {

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            bottom_navigation.getMenu().findItem(R.id.bot_nav_home).setChecked(true);
            nav_view.getMenu().getItem(0).setChecked(true);
            setLocale();
            showMenu();
            if (session.isLoggedIn()) {
                getuserdetails();
                getCartCount();
            } else {
                tvNav1.setText(getResources().getString(R.string.navText1));
                tvNav2.setText(getResources().getString(R.string.navText2));
            }

            if (CheckNetwork.isInternetAvailable(Dashboard.this)) {
                //setDashboard();
                if (session.isLoggedIn()) {
                    getRecently();
                }

            } else {
                Snackbar snackbar = Snackbar
                        .make(layDashboard, "No Internet Connection", Snackbar.LENGTH_LONG);
                View sbView = snackbar.getView();
                sbView.setBackgroundResource(R.color.colorPrimaryDark);
                snackbar.show();
            }
        } catch (Exception ignored) {

        }
    }

    private void setLocale() {
        Locale myLocale = new Locale("en");
        Locale.setDefault(myLocale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.locale = myLocale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
    }

    private void updateApp() {
        if(CheckNetwork.isInternetAvailable(this))
        {
            try {
                PackageManager packageManager = this.getPackageManager();
                PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(),0);
                String currentVersion = packageInfo.versionName;
                new ForceUpdateAsync(currentVersion,Dashboard.this).execute();
            } catch (PackageManager.NameNotFoundException ignored) {
            }

        }
        else
        {
            Snackbar snackbar = Snackbar
                    .make(layDashboard, "No Internet Connection", Snackbar.LENGTH_LONG);
            View sbView = snackbar.getView();
            sbView.setBackgroundResource(R.color.colorPrimaryDark);
            snackbar.show();
        }
    }

    public class ForceUpdateAsync extends AsyncTask<String, String, JSONObject> {

        private String latestVersion=null;
        private String currentVersion;
        private Context context;

        public ForceUpdateAsync(String currentVersion, Context context){
            this.currentVersion = currentVersion;
            this.context = context;
        }

        @Override
        protected JSONObject doInBackground(String... params) {

            try
            {
                latestVersion = Jsoup.connect("https://play.google.com/store/apps/details?id="+context.getPackageName()+"&hl=en")
                        .timeout(30000)
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("http://www.google.com")
                        .get()
                        .select(".hAyfc .htlgb")
                        .get(7)
                        .ownText();

            }
            catch (IOException ignored)
            {
            }
            return new JSONObject();
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            try
            {
                if(latestVersion!=null)
                {
                    if (!currentVersion.equalsIgnoreCase(latestVersion)) {
                        if (!(context instanceof SplashScreen)) {
                            if (!((Activity) context).isFinishing()) {
                                showForceUpdateDialog();
                            }
                        }
                    }
                }

            }
            catch (Exception ignored)
            {

            }

        }

        public void showForceUpdateDialog() {

            final Dialog dialog_msg = new Dialog(Dashboard.this);
            dialog_msg.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog_msg.setContentView(R.layout.message);
            dialog_msg.setCancelable(false);
            dialog_msg.setCanceledOnTouchOutside(false);

            TextView tv1 = dialog_msg.findViewById(R.id.tv1);
            tv1.setText(context.getString(R.string.youAreNotUpdatedTitle));

            TextView tv2 = dialog_msg.findViewById(R.id.tv2);
            String textUpdate=context.getString(R.string.youAreNotUpdatedMessage) + " " + latestVersion + context.getString(R.string.youAreNotUpdatedMessage1);
            tv2.setText(textUpdate);

            TextView tv3 = dialog_msg.findViewById(R.id.tv3);
            tv3.setText(R.string.updateapp);

            tv3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try
                    {
                        dialog_msg.dismiss();
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + context.getPackageName())));
                    }
                    catch (Exception ignored)
                    {

                    }
                }
            });

            dialog_msg.show();

        }
    }


    private void setDashboard()
    {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, getResources().getString(R.string.url_dashboard), new Response.Listener<String>()
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
                    try {
                        JSONObject jsonObject = new JSONObject(s);

                        String response = jsonObject.getString("response");
                        if (response.equalsIgnoreCase("success"))
                        {
                            ArrayList<HashMap<String, String>> listCat = new ArrayList<>();
                            JSONArray categories = jsonObject.getJSONArray("categories");
                            for (int i=0;i<categories.length();i++)
                            {
                                JSONObject userdata = categories.getJSONObject(i);
                                String category_id = URLDecoder.decode(userdata.getString("category_id"), "utf-8");
                                String category_name_english = URLDecoder.decode(userdata.getString("category_name_english"), "utf-8");
                                String android_image = URLDecoder.decode(userdata.getString("android_image"), "utf-8");

                                HashMap<String, String> news = new HashMap<>();
                                news.put("category_id", category_id);
                                news.put("category_name_english", category_name_english);
                                news.put("android_image", android_image);
                                listCat.add(news);

                            }

                            ArrayList<HashMap<String, String>> listSlider = new ArrayList<>();
                            JSONArray slider=jsonObject.getJSONArray("slider");
                            for (int i=0;i<slider.length();i++)
                            {
                                JSONObject userdata = slider.getJSONObject(i);
                                String slider_image = URLDecoder.decode(userdata.getString("android_mobile_banner"), "utf-8");
                                String slider_url = URLDecoder.decode(userdata.getString("slider_url"), "utf-8");
                                HashMap<String, String> news = new HashMap<>();
                                news.put("slider_image", slider_image);
                                news.put("slider_url", slider_url);
                                news.put("section", "slider");
                                listSlider.add(news);
                            }

                            ArrayList<HashMap<String, String>> listBanner = new ArrayList<>();
                            JSONArray banner = jsonObject.getJSONArray("banner");
                            for(int i=0;i<banner.length();i++)
                            {
                                JSONObject userdata = banner.getJSONObject(i);
                                String section_name = URLDecoder.decode(userdata.getString("section_image_android_app"), "utf-8");
                                String top_bottom = URLDecoder.decode(userdata.getString("section_id"), "utf-8");
                                String sectionimage_url = URLDecoder.decode(userdata.getString("sectionimage_url"), "utf-8");
                                HashMap<String, String> news = new HashMap<>();
                                news.put("section_name", section_name);
                                news.put("top_bottom", top_bottom);
                                news.put("sectionimage_url", sectionimage_url);
                                listBanner.add(news);
                            }

                            ArrayList<HashMap<String, String>> listBrand = new ArrayList<>();
                            JSONArray brand = jsonObject.getJSONArray("brand");
                            for(int i=0;i<brand.length();i++)
                            {
                                JSONObject userdata = brand.getJSONObject(i);
                                String brand_id = URLDecoder.decode(userdata.getString("brand_id"), "utf-8");
                                String brand_name_english = URLDecoder.decode(userdata.getString("brand_name_english"), "utf-8");
                                String brand_image = URLDecoder.decode(userdata.getString("brand_image"), "utf-8");

                                HashMap<String, String> news = new HashMap<>();
                                news.put("brand_id", brand_id);
                                news.put("brand_name_english", brand_name_english);
                                news.put("brand_image", brand_image);
                                listBrand.add(news);
                            }

                            ArrayList<HashMap<String, String>> listLatest = new ArrayList<>();
                            JSONArray latest_products=jsonObject.getJSONArray("latest_products");
                            for (int i=0;i<latest_products.length();i++)
                            {
                                JSONObject userdata = latest_products.getJSONObject(i);
                                String product_id = URLDecoder.decode(userdata.getString("product_id"), "utf-8");
                                String cost = URLDecoder.decode(userdata.getString("cost"), "utf-8");
                                String stock_qty = URLDecoder.decode(userdata.getString("stock_qty"), "utf-8");
                                String product_image_main = URLDecoder.decode(userdata.getString("product_image_main"), "utf-8");
                                String product_name_en = userdata.getString("product_name_en");
                                try {
                                    product_name_en = URLDecoder.decode(userdata.getString("product_name_en"), "utf-8");
                                }
                                catch (Exception ignored)
                                {

                                }
                                String mrp =URLDecoder.decode(userdata.getString("mrp"), "utf-8");

                                HashMap<String, String> news = new HashMap<>();
                                news.put("product_id", product_id);
                                news.put("product_name_en", product_name_en);
                                news.put("cost", cost);
                                news.put("stock_qty", stock_qty);
                                news.put("product_image_main", product_image_main);
                                news.put("mrp", mrp);
                                news.put("customer_id", customer_id);
                                listLatest.add(news);
                            }

                            ArrayList<HashMap<String, String>> listFeatured = new ArrayList<>();
                            JSONArray featured_products=jsonObject.getJSONArray("featured_products");
                            for (int i=0;i<featured_products.length();i++)
                            {
                                JSONObject userdata = featured_products.getJSONObject(i);
                                String product_id = URLDecoder.decode(userdata.getString("product_id"), "utf-8");
                                String cost = URLDecoder.decode(userdata.getString("cost"), "utf-8");
                                String stock_qty = URLDecoder.decode(userdata.getString("stock_qty"), "utf-8");
                                String product_image_main = URLDecoder.decode(userdata.getString("product_image_main"), "utf-8");
                                String product_name_en = userdata.getString("product_name_en");
                                try {
                                    product_name_en = URLDecoder.decode(userdata.getString("product_name_en"), "utf-8");
                                }
                                catch (Exception ignored)
                                {

                                }
                                String mrp = URLDecoder.decode(userdata.getString("mrp"), "utf-8");

                                HashMap<String, String> news = new HashMap<>();
                                news.put("product_id", product_id);
                                news.put("product_name_en", product_name_en);
                                news.put("cost", cost);
                                news.put("stock_qty", stock_qty);
                                news.put("product_image_main", product_image_main);
                                news.put("mrp", mrp);
                                news.put("customer_id", customer_id);
                                listFeatured.add(news);
                            }


                            if (!listSlider.isEmpty()) {
                                Gson gson = new Gson();
                                List<HashMap<String, String>> textList = new ArrayList<>(listSlider);
                                String jsonText = gson.toJson(textList);
                                session.createSliderSession(jsonText);
                            } else {
                                session.createSliderSession("");
                            }

                            if (!listCat.isEmpty()) {
                                Gson gson = new Gson();
                                List<HashMap<String, String>> textList = new ArrayList<>(listCat);
                                String jsonText = gson.toJson(textList);

                                session.createCategorySession(jsonText);
                                session.createSellingSession(jsonText);
                            } else {
                                session.createCategorySession("");
                                session.createSellingSession("");
                            }

                            if (!listLatest.isEmpty()) {
                                layLoonu.setVisibility(View.VISIBLE);
                                DBProductAdapter1 adLoonu = new DBProductAdapter1(Dashboard.this, listLatest);
                                rvLoonu.setAdapter(adLoonu);
                                adLoonu.notifyDataSetChanged();
                            } else {
                                layLoonu.setVisibility(View.GONE);
                            }


                            if (!listBanner.isEmpty()) {
                                Gson gson = new Gson();
                                List<HashMap<String, String>> textList = new ArrayList<>(listBanner);
                                String jsonText = gson.toJson(textList);

                                session.createBannerSession(jsonText);
                            } else {
                                session.createBannerSession("");
                            }

                            if (!listFeatured.isEmpty()) {
                                layFeatured.setVisibility(View.VISIBLE);
                                DBProductAdapter1 adapter = new DBProductAdapter1(Dashboard.this, listFeatured);
                                rvFeatured.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                            } else {
                                layFeatured.setVisibility(View.GONE);
                            }

                            if (!listBrand.isEmpty()) {
                                Gson gson = new Gson();
                                List<HashMap<String, String>> textList = new ArrayList<>(listBrand);
                                String jsonText = gson.toJson(textList);

                                session.createBrandSession(jsonText);
                            } else {
                                session.createBrandSession("");
                            }

                            showSlider();
                            showCategory();
                            showSelling();
                            showBanner();
                            showBrand();
                        }
                    } catch (Exception ignored) {
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

    private void showSlider()
    {
        Gson gson = new Gson();
        HashMap<String, String> user = session.getSliderSession();
        String json = user.get(SessionManager.KEY_SLIDER);
        ArrayList alist = gson.fromJson(json, ArrayList.class);
        final ArrayList<HashMap<String, String>> list = new ArrayList<>();
        JSONArray jsonArrA = new JSONArray(alist);
        try
        {
            for(int i=0;i<jsonArrA.length();i++)
            {
                JSONObject userdata = jsonArrA.getJSONObject(i);
                String slider_image = userdata.getString("slider_image");
                String slider_url =userdata.getString("slider_url");

                if(slider_image.length() > 0)
                {
                    HashMap<String, String> news = new HashMap<>();
                    news.put("slider_image", slider_image);
                    news.put("slider_url", slider_url);
                    news.put("section", "slider");
                    list.add(news);
                }
            }
        }
        catch (Exception ignored)
        {

        }

        if(!list.isEmpty())
        {
            laySlider.setVisibility(View.VISIBLE);
            vpSlider.setAdapter(new SliderAdapter(Dashboard.this,list));

            final Handler handler = new Handler();
            final Runnable Update = new Runnable()
            {
                public void run() {
                    if (currentSlider == list.size())
                    {
                        currentSlider= -1;
                    }
                    vpSlider.setCurrentItem(currentSlider++, true);
                }
            };

            Timer swipeTimer = new Timer();
            swipeTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.post(Update);
                }
            }, 3000, 3000);
        }
        else
        {
            laySlider.setVisibility(View.GONE);
        }
    }

    private void showCategory()
    {
        Gson gson = new Gson();
        HashMap<String, String> user = session.getCategorySession();
        String json = user.get(SessionManager.KEY_CATEGORY);
        ArrayList alist = gson.fromJson(json, ArrayList.class);
        ArrayList<HashMap<String, String>> list = new ArrayList<>();
        JSONArray jsonArrA = new JSONArray(alist);
        try
        {
            for(int i=0;i<jsonArrA.length();i++)
            {
                JSONObject userdata = jsonArrA.getJSONObject(i);
                String category_id = userdata.getString("category_id");
                String category_name_english =userdata.getString("category_name_english");
                String android_image = userdata.getString("android_image");

                HashMap<String, String> news = new HashMap<>();
                news.put("category_id", category_id);
                news.put("category_name_english", category_name_english);
                news.put("android_image", android_image);
                list.add(news);
            }
        }
        catch (Exception ignored)
        {

        }

        if(!list.isEmpty())
        {
            layCat.setVisibility(View.VISIBLE);
            CategoryAdapter adCat=new CategoryAdapter(Dashboard.this,list,layDashboard);
            rvCat.setAdapter(adCat);
            adCat.notifyDataSetChanged();
        }
        else
        {
            layCat.setVisibility(View.GONE);
        }
    }

    private void showSelling()
    {
        Gson gson = new Gson();
        HashMap<String, String> user = session.getSellingSession();
        String json = user.get(SessionManager.KEY_SELLING);
        ArrayList alist = gson.fromJson(json, ArrayList.class);
        ArrayList<HashMap<String, String>> list = new ArrayList<>();
        JSONArray jsonArrA = new JSONArray(alist);
        try
        {
            for(int i=0;i<jsonArrA.length();i++)
            {
                JSONObject userdata = jsonArrA.getJSONObject(i);
                String category_id = userdata.getString("category_id");
                String category_name_english =userdata.getString("category_name_english");
                String android_image = userdata.getString("android_image");
                if (i == 0)
                {
                    selectedCategory_id = category_id;
                    selectedSelling = i;
                }

                HashMap<String, String> news = new HashMap<>();
                news.put("category_id", category_id);
                news.put("category_name_english", category_name_english);
                news.put("android_image", android_image);
                list.add(news);
            }
        }
        catch (Exception ignored)
        {

        }

        if(!list.isEmpty())
        {
            laysel.setVisibility(View.VISIBLE);
            SellingAdapter adapter_selling = new SellingAdapter(Dashboard.this, list);
            rvSel.setAdapter(adapter_selling);
            adapter_selling.notifyDataSetChanged();

            if(CheckNetwork.isInternetAvailable(Dashboard.this))
            {
                getSellingProduct();
            }
            else
            {
                laySellingProduct.setVisibility(View.GONE);
                selectedSelling=-1;
                adapter_selling.notifyDataSetChanged();
            }
        }
        else
        {
            laysel.setVisibility(View.GONE);
        }
    }

    private void showBanner()
    {
        Gson gson = new Gson();
        HashMap<String, String> user = session.getBannerSession();
        String json = user.get(SessionManager.KEY_BANNER);
        ArrayList alist = gson.fromJson(json, ArrayList.class);
        final ArrayList<HashMap<String, String>> list1 = new ArrayList<>();
        final ArrayList<HashMap<String, String>> list2 = new ArrayList<>();
        JSONArray jsonArrA = new JSONArray(alist);
        try
        {
            for(int i=0;i<jsonArrA.length();i++)
            {
                JSONObject userdata = jsonArrA.getJSONObject(i);
                String slider_image = userdata.getString("section_name");
                String slider_url =userdata.getString("sectionimage_url");
                String top_bottom =userdata.getString("top_bottom");

                if(slider_image.length() > 0 && top_bottom.length() > 0)
                {
                    if(top_bottom.equals("1"))
                    {
                        HashMap<String, String> news1 = new HashMap<>();
                        news1.put("slider_image", slider_image);
                        news1.put("slider_url", slider_url);
                        news1.put("section", "banner");
                        list1.add(news1);
                    }
                    else
                    {
                        HashMap<String, String> news2 = new HashMap<>();
                        news2.put("slider_image", slider_image);
                        news2.put("slider_url", slider_url);
                        news2.put("section", "banner");
                        list2.add(news2);
                    }
                }
            }
        }
        catch (Exception ignored)
        {

        }

        if(!list1.isEmpty())
        {
            layBanner1.setVisibility(View.VISIBLE);
            vpBanner1.setAdapter(new SliderAdapter(Dashboard.this,list1));

            final Handler handler = new Handler();
            final Runnable Update = new Runnable()
            {
                public void run() {

                    if (currentBanner1 == list1.size())
                    {
                        currentBanner1= -1;
                    }

                    vpBanner1.setCurrentItem(currentBanner1++, true);
                }
            };

            Timer swipeTimer = new Timer();
            swipeTimer.schedule(new TimerTask() {
                @Override
                public void run()
                {
                    handler.post(Update);
                }
            }, 3000, 3000);
        }
        else
        {
            layBanner1.setVisibility(View.GONE);
        }

        if(!list2.isEmpty())
        {
            layBanner2.setVisibility(View.VISIBLE);
            vpBanner2.setAdapter(new SliderAdapter(Dashboard.this,list2));

            final Handler handler = new Handler();
            final Runnable Update = new Runnable()
            {
                public void run() {
                    if (currentBanner2 == list1.size())
                    {
                        currentBanner2= -1;
                    }

                    vpBanner2.setCurrentItem(currentBanner2++, true);
                }
            };

            Timer swipeTimer = new Timer();
            swipeTimer.schedule(new TimerTask() {
                @Override
                public void run()
                {
                    handler.post(Update);
                }
            }, 3000, 3000);
        }
        else
        {
            layBanner2.setVisibility(View.GONE);
        }
    }

    private void showBrand()
    {
        Gson gson = new Gson();
        HashMap<String, String> user = session.getBrandSession();
        String json = user.get(SessionManager.KEY_BRAND);
        ArrayList alist = gson.fromJson(json, ArrayList.class);
        ArrayList<HashMap<String, String>> list = new ArrayList<>();
        JSONArray jsonArrA = new JSONArray(alist);
        try
        {
            for(int i=0;i<jsonArrA.length();i++)
            {
                JSONObject userdata = jsonArrA.getJSONObject(i);
                String brand_id = userdata.getString("brand_id");
                String brand_name_english =userdata.getString("brand_name_english");
                String brand_image = userdata.getString("brand_image");

                HashMap<String, String> news = new HashMap<>();
                news.put("brand_id", brand_id);
                news.put("brand_name_english", brand_name_english);
                news.put("brand_image", brand_image);
                list.add(news);
            }
        }
        catch (Exception ignored)
        {

        }

        if(!list.isEmpty())
        {
            layBrand.setVisibility(View.VISIBLE);
            BrandAdapter adBrand=new BrandAdapter(Dashboard.this,list);
            rvBrand.setAdapter(adBrand);
            adBrand.notifyDataSetChanged();
        }
        else
        {
            layBrand.setVisibility(View.GONE);
        }
    }

    private void getSellingProduct()
    {
        dialog_pb.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, getResources().getString(R.string.url_selling_product), new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                if (dialog_pb.isShowing())
                {
                    dialog_pb.dismiss();
                }

                laySellingProduct.setVisibility(View.VISIBLE);

                if(response!=null && response.length()>0)
                {
                    try
                    {
                        JSONObject jsonObject = new JSONObject(response);
                        String response1 = jsonObject.getString("response");
                        if (response1.equalsIgnoreCase("success"))
                        {
                            ArrayList<HashMap<String, String>> listSelling = new ArrayList<>();
                            JSONArray jsonArray=jsonObject.getJSONArray("products");
                            for (int i=0;i<jsonArray.length();i++)
                            {
                                JSONObject userdata = jsonArray.getJSONObject(i);
                                String product_id = URLDecoder.decode(userdata.getString("product_id"), "utf-8");
                                String mrp = URLDecoder.decode(userdata.getString("mrp"), "utf-8");
                                String cost = URLDecoder.decode(userdata.getString("cost"), "utf-8");
                                String product_name_en = userdata.getString("product_name_en");
                                try
                                {
                                    product_name_en = URLDecoder.decode(userdata.getString("product_name_en"), "utf-8");
                                }
                                catch (Exception ignored)
                                {

                                }
                                String product_image_main = URLDecoder.decode(userdata.getString("product_image_main"), "utf-8");
                                String stock_qty = URLDecoder.decode(userdata.getString("stock_qty"), "utf-8");

                                HashMap<String, String> news = new HashMap<>();
                                news.put("product_id", product_id);
                                news.put("product_name_en", product_name_en);
                                news.put("cost", cost);
                                news.put("stock_qty", stock_qty);
                                news.put("product_image_main", product_image_main);
                                news.put("mrp", mrp);
                                news.put("customer_id", customer_id);
                                listSelling.add(news);
                            }

                            if (!listSelling.isEmpty())
                            {
                                tvSellingProduct.setVisibility(View.GONE);
                                rvSellingProduct.setVisibility(View.VISIBLE);
                                DBProductAdapter1 adapter=new DBProductAdapter1(Dashboard.this,listSelling);
                                rvSellingProduct.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                            }
                            else
                            {
                                rvSellingProduct.setVisibility(View.GONE);
                                tvSellingProduct.setVisibility(View.VISIBLE);
                            }
                        }
                        else
                        {
                            rvSellingProduct.setVisibility(View.GONE);
                            tvSellingProduct.setVisibility(View.VISIBLE);
                        }
                    }
                    catch (Exception ignored)
                    {

                    }
                }
                else
                {
                    rvSellingProduct.setVisibility(View.GONE);
                    tvSellingProduct.setVisibility(View.VISIBLE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (dialog_pb.isShowing())
                {
                    dialog_pb.dismiss();
                }
                rvSellingProduct.setVisibility(View.GONE);
                tvSellingProduct.setVisibility(View.VISIBLE);
            }
        })
        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(getResources().getString(R.string.param_subcategory), selectedCategory_id);
                return params;
            }
        };

        RequestQueue requestQueue   = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void getRecently()
    {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, getResources().getString(R.string.url_recently), new Response.Listener<String>()
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
                                layRecently.setVisibility(View.VISIBLE);
                                DBProductAdapter1 adapter=new DBProductAdapter1(Dashboard.this,list);
                                rvRecently.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                            }
                            else
                            {
                                layRecently.setVisibility(View.GONE);
                            }
                        }
                        else
                        {
                            layRecently.setVisibility(View.GONE);
                        }
                    }
                    catch (Exception ignored)
                    {

                    }
                }
                else
                {
                    layRecently.setVisibility(View.GONE);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (dialog_pb.isShowing())
                {
                    dialog_pb.dismiss();
                }
                layRecently.setVisibility(View.GONE);
            }
        })
        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(getResources().getString(R.string.param_recently),customer_id);
                return params;
            }
        };

        RequestQueue requestQueue   = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    private class DBProductAdapter1 extends RecyclerView.Adapter<DBProductAdapter1.ViewHolder> {

        ArrayList<HashMap<String, String>> alNameA;
        Context context;

        public DBProductAdapter1(Context context, ArrayList<HashMap<String, String>> alNameA) {
            super();
            this.context = context;
            this.alNameA = alNameA;
        }


        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.list_dbproduct, viewGroup, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
            final String product_id = alNameA.get(i).get("product_id");
            final String product_image_main = alNameA.get(i).get("product_image_main");
            final String cost = alNameA.get(i).get("cost");
            final String mrp = alNameA.get(i).get("mrp");
            final String product_name_en = alNameA.get(i).get("product_name_en");
            final String customer_id = alNameA.get(i).get("customer_id");
            try {
                if (product_image_main != null && product_image_main.length() > 1) {
                    String product_image_main1 = "https://majlisstore.com/crm/public/uploads/product_images/" + product_image_main;
                    Picasso.get().load(product_image_main1).error(R.drawable.logo_h).into(viewHolder.img);
                } else {
                    Picasso.get().load(R.drawable.logo_h).error(R.drawable.logo_h).into(viewHolder.img);
                }
            } catch (Exception ignored) {

            }

            String textCost = "QAR " + cost;
            viewHolder.tv1.setText(textCost);
            viewHolder.tv3.setText(product_name_en);

            viewHolder.lay.setOnClickListener(new View.OnClickListener() {
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

            viewHolder.tv4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (session.isLoggedIn()) {
                            if (CheckNetwork.isInternetAvailable(context)) {
                                addtocart(product_id, customer_id, "1");
                            } else {
                                Snackbar snackbar = Snackbar
                                        .make(layDashboard, "No Internet Connection", Snackbar.LENGTH_LONG);
                                View sbView = snackbar.getView();
                                sbView.setBackgroundResource(R.color.colorPrimaryDark);
                                snackbar.show();
                            }

                        } else {
                            Snackbar snackbar = Snackbar
                                    .make(layDashboard, "You are not logged in", Snackbar.LENGTH_LONG);
                            View sbView = snackbar.getView();
                            sbView.setBackgroundResource(R.color.colorPrimaryDark);
                            snackbar.show();
                        }
                    } catch (Exception ignored) {

                    }
                }
            });

            double diff2 = 0;
            if (mrp != null && mrp.length() > 0 && cost != null && cost.length() > 0) {
                double diff1 = Double.parseDouble(mrp) - Double.parseDouble(cost);
                diff2 = (diff1 / Double.parseDouble(mrp)) * 100;
            }

            if (diff2 <= 0.0 || diff2 <= 0) {
                viewHolder.lay1.setVisibility(View.GONE);
            } else {
                viewHolder.lay1.setVisibility(View.GONE);
                DecimalFormat twoDForm = new DecimalFormat("#.#");
                String discount1 = String.valueOf(twoDForm.format(diff2)) + "% Off";
                viewHolder.tv5.setText(discount1);
            }
        }

        @Override
        public int getItemCount() {
            return alNameA.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public LinearLayout lay, lay1;
            public ImageView img;
            public TextView tv1, tv2, tv3, tv4, tv5;

            public ViewHolder(View itemView) {
                super(itemView);
                lay = itemView.findViewById(R.id.lay);
                lay1 = itemView.findViewById(R.id.lay1);
                img = itemView.findViewById(R.id.img);
                tv1 = itemView.findViewById(R.id.tv1);
                tv2 = itemView.findViewById(R.id.tv2);
                tv3 = itemView.findViewById(R.id.tv3);
                tv4 = itemView.findViewById(R.id.tv4);
                tv5 = itemView.findViewById(R.id.tv5);
            }
        }
    }

    private class SellingAdapter extends RecyclerView.Adapter<SellingAdapter.ViewHolder> {

        ArrayList<HashMap<String, String>> alNameA;
        Context context;

        public SellingAdapter(Context context, ArrayList<HashMap<String, String>> alNameA) {
            super();
            this.context = context;
            this.alNameA = alNameA;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.list_selling, viewGroup, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
            final String category_id = alNameA.get(i).get("category_id");
            final String category_name_english = alNameA.get(i).get("category_name_english");

            viewHolder.tv_selling.setText(category_name_english);

            if (selectedSelling == i) {
                viewHolder.lay.setBackgroundResource(R.drawable.button_shape1);
                viewHolder.tv_selling.setTextColor(getResources().getColor(R.color.white));
            } else {
                viewHolder.lay.setBackgroundResource(R.drawable.button_shape2);
                viewHolder.tv_selling.setTextColor(getResources().getColor(R.color.black));
            }

            viewHolder.lay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (category_id != null && !category_id.equalsIgnoreCase(selectedCategory_id)) {
                            if (CheckNetwork.isInternetAvailable(context)) {
                                selectedCategory_id = category_id;
                                getSellingProduct();
                                selectedSelling = i;
                                notifyDataSetChanged();
                            } else {
                                Snackbar snackbar = Snackbar
                                        .make(layDashboard, "No Internet Connection", Snackbar.LENGTH_LONG);
                                View sbView = snackbar.getView();
                                sbView.setBackgroundResource(R.color.colorPrimaryDark);
                                snackbar.show();
                            }
                        }
                    } catch (Exception ignored) {

                    }

                }
            });
        }

        @Override
        public int getItemCount() {
            return alNameA.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public LinearLayout lay;
            public TextView tv_selling;

            public ViewHolder(View itemView) {
                super(itemView);
                tv_selling = itemView.findViewById(R.id.tv_selling);
                lay = itemView.findViewById(R.id.lay);
            }
        }
    }

    public void addtocart(final String product_id, final String customer_id, final String quantity) {
        if (!dialog_pb.isShowing() && dialog_pb != null) {
            dialog_pb.show();
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, getResources().getString(R.string.url_addtocart), new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (dialog_pb.isShowing()) {
                    dialog_pb.dismiss();
                }

                if (s != null && s.length() > 0) {
                    try {
                        JSONObject jsonObj = new JSONObject(s);
                        String response = jsonObj.getString("response");
                        if (response.equalsIgnoreCase("success")) {
                            int count = Integer.parseInt(session.getCartQuantity()) + 1;
                            session.setCartCount("" + count);
                            getCartCount();

                            Snackbar snackbar = Snackbar
                                    .make(layDashboard, "Added to Cart", Snackbar.LENGTH_LONG);
                            View sbView = snackbar.getView();
                            sbView.setBackgroundResource(R.color.green);
                            snackbar.show();

                        } else if (response.equalsIgnoreCase("cart updated")) {
                            Snackbar snackbar = Snackbar
                                    .make(layDashboard, "Already Added to Cart", Snackbar.LENGTH_LONG);
                            View sbView = snackbar.getView();
                            sbView.setBackgroundResource(R.color.colorPrimaryDark);
                            snackbar.show();
                        } else {
                            Snackbar snackbar = Snackbar
                                    .make(layDashboard, "Something went wrong!", Snackbar.LENGTH_LONG);
                            View sbView = snackbar.getView();
                            sbView.setBackgroundResource(R.color.colorPrimaryDark);
                            snackbar.show();
                        }
                    } catch (Exception e) {
                        Snackbar snackbar = Snackbar
                                .make(layDashboard, "Something went wrong!", Snackbar.LENGTH_LONG);
                        View sbView = snackbar.getView();
                        sbView.setBackgroundResource(R.color.colorPrimaryDark);
                        snackbar.show();
                    }
                } else {
                    Snackbar snackbar = Snackbar
                            .make(layDashboard, "Something went wrong!", Snackbar.LENGTH_LONG);
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
                Snackbar snackbar = Snackbar
                        .make(layDashboard, "Something went wrong!", Snackbar.LENGTH_LONG);
                View sbView = snackbar.getView();
                sbView.setBackgroundResource(R.color.colorPrimaryDark);
                snackbar.show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(getResources().getString(R.string.param_addcart1), product_id);
                params.put(getResources().getString(R.string.param_addcart2), customer_id);
                params.put(getResources().getString(R.string.param_addcart3), quantity);
                return params;

            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }
}
