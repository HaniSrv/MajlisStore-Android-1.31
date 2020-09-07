package com.majlisstore.majlisstore.helpers;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;

public class CheckNetwork {

    public static  boolean isInternetAvailable(Context context)
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null)
        {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
                if (capabilities != null)
                {
                    if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
                    {
                        return true;
                    }
                    else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI))
                    {
                        return true;
                    }
                    else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET))
                    {
                        return true;
                    }
                }
            }
            else
            {
                try
                {
                    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                    if (activeNetworkInfo != null && activeNetworkInfo.isConnected())
                    {
                        return true;
                    }
                }
                catch (Exception ignored)
                {

                }
            }
        }

        return false;
    }
}
