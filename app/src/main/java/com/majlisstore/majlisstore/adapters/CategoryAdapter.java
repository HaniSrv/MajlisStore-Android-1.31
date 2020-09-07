package com.majlisstore.majlisstore.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.majlisstore.majlisstore.R;
import com.majlisstore.majlisstore.activities.ProductSubListing;
import com.majlisstore.majlisstore.activities.SubCategory;
import com.majlisstore.majlisstore.helpers.CheckNetwork;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    ArrayList<HashMap<String, String>> list;
    Context context;
    LinearLayout layDashboard;

    public CategoryAdapter(Context context, ArrayList<HashMap<String, String>> list, LinearLayout layDashboard) {
        super();
        this.context=context;
        this.list=list;
        this.layDashboard=layDashboard;
    }

    @NonNull
    @Override
    public CategoryAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_category, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final CategoryAdapter.ViewHolder viewHolder, final int i)
    {
        final String category_id = list.get(i).get("category_id");
        final String category_name_english = list.get(i).get("category_name_english");
        final String android_image = list.get(i).get("android_image");

        if(android_image!=null && android_image.length()>0)
        {
            String android_image1 = "https://majlisstore.com/crm/public/uploads/app_category_images/"+android_image;
            Picasso.get().load(android_image1).fit().error(R.drawable.logo_icon).into(viewHolder.img);
        }
        else
        {
            Picasso.get().load(R.drawable.logo_icon).fit().error(R.drawable.logo_icon).into(viewHolder.img);
        }

        viewHolder.tv.setText(category_name_english);

        viewHolder.lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try
                {
                    if(CheckNetwork.isInternetAvailable(context))
                    {
                        getSubcategory(category_id,category_name_english);
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
                catch (Exception ignored)
                {

                }
            }
        });
    }

    private void getSubcategory(String category_id, String category_name_english)
    {
        Dialog dialog_pb = new Dialog(context);
        dialog_pb.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_pb.setContentView(R.layout.progressbar);
        if(dialog_pb.getWindow()!=null)
        {
            dialog_pb.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }
        dialog_pb.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, context.getString(R.string.url_subcategory), new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                if(dialog_pb.isShowing())
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
                            Intent i = new Intent(context, SubCategory.class);
                            i.putExtra("category_id", category_id);
                            i.putExtra("category_name", category_name_english);
                            context.startActivity(i);
                        }
                        else
                        {
                            Intent i = new Intent(context, ProductSubListing.class);
                            i.putExtra("value","category");
                            i.putExtra("category_id", category_id);
                            i.putExtra("category_name", category_name_english);
                            context.startActivity(i);
                        }
                    }
                    catch (Exception ignored)
                    {

                    }
                }
                else
                {
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
                params.put("category_id",category_id);
                return params;
            }
        };

        RequestQueue requestQueue   = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        public LinearLayout lay;
        public ImageView img;
        public TextView tv;

        public ViewHolder(View itemView)
        {
            super(itemView);
            lay = itemView.findViewById(R.id.lay);
            img = itemView.findViewById(R.id.img);
            tv = itemView.findViewById(R.id.tv);
        }
    }
}
