package com.majlisstore.majlisstore.activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.majlisstore.majlisstore.R;
import com.majlisstore.majlisstore.helpers.CustomTypefaceSpan;
import com.majlisstore.majlisstore.helpers.SessionManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class Accounts extends AppCompatActivity {

    SessionManager session;

    Typeface fontRegular;

    LinearLayout layAccount, layAccountSettings, layAddress, layWallet, laySignin, laySignout;
    TextView tvCartcount, tvUser, tvAccountSettings, tvAddress, tvWallet, tvSignin, tvSignout;
    ImageView imgBack, imgUser, imgSearch, cartid;
    BottomNavigationView bottom_navigation;
    String customer_id = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLocale();
        setContentView(R.layout.activity_accounts);

        session = new SessionManager(Accounts.this);

        fontRegular = ResourcesCompat.getFont(this, R.font.ubuntu_medium);

        layAccount = findViewById(R.id.layAccount);
        layAccountSettings = findViewById(R.id.layAccountSettings);
        layAddress = findViewById(R.id.layAddress);
        layWallet = findViewById(R.id.layWallet);
        laySignin = findViewById(R.id.laySignin);
        laySignout = findViewById(R.id.laySignout);

        tvCartcount = findViewById(R.id.tvCartcount);
        tvUser = findViewById(R.id.tvUser);
        tvAccountSettings = findViewById(R.id.tvAccountSettings);
        tvAddress = findViewById(R.id.tvAddress);
        tvWallet = findViewById(R.id.tvWallet);
        tvSignin = findViewById(R.id.tvSignin);
        tvSignout = findViewById(R.id.tvSignout);

        imgSearch = findViewById(R.id.imgSearch);
        cartid = findViewById(R.id.cartid);
        imgBack = findViewById(R.id.imgBack);
        imgUser = findViewById(R.id.imgUser);

        bottom_navigation = findViewById(R.id.bottom_navigation);
        bottom_navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        bottom_navigation.setSelectedItemId(R.id.bot_nav_account);
        Menu bottomMenu = bottom_navigation.getMenu();
        for (int i = 0; i < bottomMenu.size(); i++) {
            MenuItem mi = bottomMenu.getItem(i);
            applyFontToMenuItem(mi);
        }

        if (session.isLoggedIn()) {
            getuserdetails();
            getCartCount();
            laySignin.setVisibility(View.GONE);
            laySignout.setVisibility(View.VISIBLE);
        } else {
            laySignin.setVisibility(View.VISIBLE);
            laySignout.setVisibility(View.GONE);
            tvUser.setText(getResources().getString(R.string.guest));
        }

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    onBackPressed();
                } catch (Exception ignored) {

                }
            }
        });

        cartid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    if (session.isLoggedIn()) {
                        Intent i = new Intent(Accounts.this, Cart.class);
                        startActivity(i);
                    } else {
                        Snackbar snackbar = Snackbar
                                .make(layAccount, "You are not logged in", Snackbar.LENGTH_LONG);
                        View sbView = snackbar.getView();
                        sbView.setBackgroundResource(R.color.colorPrimaryDark);
                        snackbar.show();
                    }

                } catch (Exception ignored) {

                }

            }
        });

        imgSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent i = new Intent(Accounts.this, Search.class);
                    startActivity(i);
                } catch (Exception ignored) {
                }
            }
        });


        layAccountSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (session.isLoggedIn()) {

                        Intent i = new Intent(Accounts.this, Profile.class);
                        i.putExtra("customer_id", customer_id);
                        startActivity(i);
                    } else {
                        Snackbar snackbar = Snackbar
                                .make(layAccount, "You are not logged in", Snackbar.LENGTH_LONG);
                        View sbView = snackbar.getView();
                        sbView.setBackgroundResource(R.color.colorPrimaryDark);
                        snackbar.show();
                    }
                } catch (Exception ignored) {

                }
            }
        });

        layAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (session.isLoggedIn()) {

                        Intent i = new Intent(Accounts.this, Address.class);
                        i.putExtra("value", "Account");
                        startActivity(i);
                    } else {
                        Snackbar snackbar = Snackbar
                                .make(layAccount, "You are not logged in", Snackbar.LENGTH_LONG);
                        View sbView = snackbar.getView();
                        sbView.setBackgroundResource(R.color.colorPrimaryDark);
                        snackbar.show();
                    }
                } catch (Exception ignored) {

                }
            }
        });


        layWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (session.isLoggedIn()) {

                        Intent i = new Intent(Accounts.this, Settings.class);
                        startActivity(i);
                    } else {
                        Intent i = new Intent(Accounts.this, Settings.class);
                        startActivity(i);

                        Snackbar snackbar = Snackbar
                                .make(layAccount, "You are not logged in", Snackbar.LENGTH_LONG);
                        View sbView = snackbar.getView();
                        sbView.setBackgroundResource(R.color.colorPrimaryDark);
                        snackbar.show();
                    }
                } catch (Exception ignored) {

                }
            }
        });


        laySignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent i = new Intent(Accounts.this, Login.class);
                    startActivity(i);
                } catch (Exception ignored) {

                }
            }
        });


        laySignout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    signout();
                } catch (Exception ignored) {

                }
            }
        });
    }

    private void signout() {
        try {
            final Dialog dialog_msg = new Dialog(Accounts.this);
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

    private void getuserdetails() {
        Gson gson = new Gson();
        HashMap<String, String> user = session.getLoginSession();
        String json = user.get(SessionManager.KEY_LOGIN);
        ArrayList alist = gson.fromJson(json, ArrayList.class);

        JSONArray jsonArrA = new JSONArray(alist);
        try {
            JSONObject userdata = jsonArrA.getJSONObject(0);
            customer_id = userdata.getString("customer_id");
            String customer_name = userdata.getString("customer_name");
            tvUser.setText(customer_name);
        } catch (Exception ignored) {

        }
    }

    private void getCartCount() {
        if (!session.getCartQuantity().equals("") && !session.getCartQuantity().equals("0")) {
            if (Integer.parseInt(session.getCartQuantity()) < 10) {
                tvCartcount.setVisibility(View.VISIBLE);
                tvCartcount.setText(session.getCartQuantity());
            } else {
                tvCartcount.setVisibility(View.VISIBLE);
                tvCartcount.setText("9+");
            }
        } else {
            tvCartcount.setVisibility(View.INVISIBLE);
        }

    }


    private void applyFontToMenuItem(MenuItem mi) {
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("", fontRegular), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Intent i;
            switch (item.getItemId()) {
                case R.id.bot_nav_home:
                    i = new Intent(Accounts.this, Dashboard.class);
                    startActivity(i);
                    return true;
                case R.id.bot_nav_video:
                    i = new Intent(Accounts.this, Products.class);
                    startActivity(i);
                    return true;
                case R.id.bot_nav_notification:
                    if (session.isLoggedIn()) {
                        i = new Intent(Accounts.this, Notifications.class);
                        startActivity(i);
                        return true;
                    } else {
                        Snackbar snackbar = Snackbar
                                .make(layAccount, "You are not logged in", Snackbar.LENGTH_LONG);
                        View sbView = snackbar.getView();
                        sbView.setBackgroundResource(R.color.colorPrimaryDark);
                        snackbar.show();
                        return false;
                    }
                case R.id.bot_nav_account:
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        try {
            bottom_navigation.getMenu().findItem(R.id.bot_nav_account).setChecked(true);
            setLocale();
            if (session.isLoggedIn()) {
                getuserdetails();
                getCartCount();
                laySignin.setVisibility(View.GONE);
                laySignout.setVisibility(View.VISIBLE);
            } else {
                laySignin.setVisibility(View.VISIBLE);
                laySignout.setVisibility(View.GONE);
                tvUser.setText(getResources().getString(R.string.guest));
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}