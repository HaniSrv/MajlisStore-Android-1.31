package com.majlisstore.majlisstore.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.majlisstore.majlisstore.R;
import com.majlisstore.majlisstore.activities.ProductSubListing;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class BrandAdapter extends RecyclerView.Adapter<BrandAdapter.ViewHolder> {

    ArrayList<HashMap<String, String>> list;
    Context context;

    public BrandAdapter(Context context, ArrayList<HashMap<String, String>> list) {
        super();
        this.context=context;
        this.list=list;
    }

    @NonNull
    @Override
    public BrandAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_brand, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final BrandAdapter.ViewHolder viewHolder, final int i)
    {
        final String brand_id = list.get(i).get("brand_id");
        final String brand_name_english = list.get(i).get("brand_name_english");
        final String brand_image = list.get(i).get("brand_image");

        if(brand_image!=null && brand_image.length()>0)
        {
            String brand_image1 ="https://majlisstore.com/crm/public/uploads/brand_logos/"+brand_image;
            Picasso.get().load(brand_image1).error(R.drawable.logo_icon).into(viewHolder.img);
        }
        else
        {
            Picasso.get().load(R.drawable.logo_icon).fit().error(R.drawable.logo_icon).into(viewHolder.img);
        }

        viewHolder.lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try
                {
                    Intent i = new Intent(context, ProductSubListing.class);
                    i.putExtra("value","brand");
                    i.putExtra("brand_id",brand_id);
                    i.putExtra("brand_name_english",brand_name_english);
                    context.startActivity(i);
                }
                catch (Exception ignored)
                {

                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        public LinearLayout lay;
        public ImageView img;

        public ViewHolder(View itemView)
        {
            super(itemView);
            lay = itemView.findViewById(R.id.lay);
            img = itemView.findViewById(R.id.img);
        }
    }
}
