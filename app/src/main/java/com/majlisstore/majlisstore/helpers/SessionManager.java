package com.majlisstore.majlisstore.helpers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.majlisstore.majlisstore.activities.Login;

import java.util.HashMap;

public class SessionManager {

    SharedPreferences pref;

    SharedPreferences.Editor editor;

    Context _context;

    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "MajlisStore";

    public static final String IS_LOGIN = "IsLoggedIn";
    public static final String KEY_LOGIN = "login";

    public static final String KEY_ADDRESS= "address";
    public static final String KEY_ADDR_ID= "address_id";
    public static final String KEY_ADDR_SHIPPING_ADDR= "address_shipp_adr";
    public static final String KEY_ADDR_NAME= "address_name";
    public static final String KEY_ADDR_PIN= "address_pin";
    public static final String KEY_ADDR_STATE= "address_state";
    public static final String KEY_ADDR_STATE_NAME= "address_state_name";

    public static final String KEY_STATE= "state";

    public static final String KEY_CATEGORY= "category";

    public static final String KEY_SELLING= "selling";

    public static final String KEY_SLIDER= "slider";

    public static final String KEY_BANNER= "banner";

    public static final String CART_COUNT = "cart_count";

    public static final String KEY_BRAND= "brand";

    public  static  final String KEY_SPECIFICATION="specification";

    public SessionManager(Context context)
    {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
        editor.apply();
    }

    public void createLoginSession(String s)
    {
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_LOGIN, s);
        editor.commit();

    }

    public HashMap<String,String> getLoginSession()
    {
        HashMap<String,String>user=new HashMap<>();
        user.put(KEY_LOGIN,pref.getString(KEY_LOGIN,null));
        return user;
    }

    public void createCategorySession(String s)
    {
        editor.putString(KEY_CATEGORY, s);
        editor.commit();

    }

    public HashMap<String,String> getCategorySession()
    {
        HashMap<String,String>user=new HashMap<>();
        user.put(KEY_CATEGORY,pref.getString(KEY_CATEGORY,null));
        return user;
    }

    public void createSellingSession(String s)
    {
        editor.putString(KEY_SELLING, s);
        editor.commit();

    }

    public HashMap<String,String> getSellingSession()
    {
        HashMap<String,String>user=new HashMap<>();
        user.put(KEY_SELLING,pref.getString(KEY_SELLING,null));
        return user;
    }

    public void createSliderSession(String s)
    {
        editor.putString(KEY_SLIDER, s);
        editor.commit();

    }

    public HashMap<String,String> getSliderSession()
    {
        HashMap<String,String>user=new HashMap<>();
        user.put(KEY_SLIDER,pref.getString(KEY_SLIDER,null));
        return user;
    }


    public void createBannerSession(String s)
    {
        editor.putString(KEY_BANNER, s);
        editor.commit();

    }

    public HashMap<String,String> getBannerSession()
    {
        HashMap<String,String>user=new HashMap<>();
        user.put(KEY_BANNER,pref.getString(KEY_BANNER,null));
        return user;
    }

    public  void setCartCount(String count)
    {
        editor.putString(CART_COUNT,count);
        editor.commit();
    }
    public  String getCartQuantity()
    {
        return pref.getString(CART_COUNT,"0");
    }

    public void createBrandSession(String s)
    {
        editor.putString(KEY_BRAND, s);
        editor.commit();

    }

    public HashMap<String,String> getBrandSession()
    {
        HashMap<String,String>user=new HashMap<>();
        user.put(KEY_BRAND,pref.getString(KEY_BRAND,null));
        return user;
    }


    public void createAddressSession(String a1)
    {
        editor.putString(KEY_ADDRESS,a1);
        editor.commit();

    }

    public HashMap<String,String> getAddressSession()
    {
        HashMap<String,String>user=new HashMap<>();
        user.put(KEY_ADDRESS,pref.getString(KEY_ADDRESS,null));
        return user;
    }
   public void createStateSession(String a1)
    {
        editor.putString(KEY_STATE,a1);
        editor.commit();

    }

    public HashMap<String,String> getStateSSession()
    {
        HashMap<String,String>user=new HashMap<>();
        user.put(KEY_STATE,pref.getString(KEY_STATE,null));
        return user;
    }

    public void createDefaultAddress(String addressid,String address_name,String shipping_address,String address_pin,String address_state,String state_name)
    {
        editor.putString(KEY_ADDR_ID,addressid);
        editor.putString(KEY_ADDR_NAME,address_name);
        editor.putString(KEY_ADDR_SHIPPING_ADDR,shipping_address);
        editor.putString(KEY_ADDR_PIN,address_pin);
        editor.putString(KEY_ADDR_STATE,address_state);
        editor.putString(KEY_ADDR_STATE_NAME,state_name);
        editor.commit();

    }

    public HashMap<String,String> getDefaultAddress()
    {
        HashMap<String,String>user=new HashMap<>();
        user.put(KEY_ADDR_ID,pref.getString(KEY_ADDR_ID,""));
        user.put(KEY_ADDR_NAME,pref.getString(KEY_ADDR_NAME,""));
        user.put(KEY_ADDR_SHIPPING_ADDR,pref.getString(KEY_ADDR_SHIPPING_ADDR,""));
        user.put(KEY_ADDR_PIN,pref.getString(KEY_ADDR_PIN,""));
        user.put(KEY_ADDR_STATE,pref.getString(KEY_ADDR_STATE,""));
        user.put(KEY_ADDR_STATE_NAME,pref.getString(KEY_ADDR_STATE_NAME,""));
        return user;
    }
    

    public void logoutUser()
    {
        editor.clear();
        editor.commit();
        Intent i = new Intent(_context, Login.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        _context.startActivity(i);
    }

    public  void specification(String spec_data)
    {
        editor.putString(KEY_SPECIFICATION,spec_data);
        editor.commit();
    }
    
    public boolean isLoggedIn()
    {
        return pref.getBoolean(IS_LOGIN, false);
    }

}