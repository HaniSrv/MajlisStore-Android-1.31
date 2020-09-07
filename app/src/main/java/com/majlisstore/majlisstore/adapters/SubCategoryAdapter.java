package com.majlisstore.majlisstore.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.majlisstore.majlisstore.R;
import com.majlisstore.majlisstore.activities.ProductSubListing;


import java.util.ArrayList;
import java.util.HashMap;

public class SubCategoryAdapter extends RecyclerView.Adapter<SubCategoryAdapter.ViewHolder> {

    ArrayList<HashMap<String, String>> list;
    Context context;

    public SubCategoryAdapter(Context context, ArrayList<HashMap<String, String>> list) {
        super();
        this.context=context;
        this.list=list;
    }

    @NonNull
    @Override
    public SubCategoryAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_subcategory, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final SubCategoryAdapter.ViewHolder viewHolder, final int i)
    {

        final String subcategory_name_english = list.get(i).get("subcategory_name_english");
        final String category_id = list.get(i).get("category_id");
        final String subcategory_id = list.get(i).get("subcategory_id");
        final String android_sub_image_name = list.get(i).get("android_sub_image_name");
        final String category_name = list.get(i).get("category_name");

        if(android_sub_image_name!=null && android_sub_image_name.length()>0)
        {
            String android_sub_image_name1 ="https://majlisstore.com/crm/public/uploads/app_subcategory_images/"+android_sub_image_name;
            Picasso.get().load(android_sub_image_name1).error(R.drawable.logo_icon).into(viewHolder.img);
        }
        else
        {
            Picasso.get().load(R.drawable.logo_icon).fit().error(R.drawable.logo_icon).into(viewHolder.img);
        }

        viewHolder.tv.setText(subcategory_name_english);

        viewHolder.lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try
                {
                    Intent i = new Intent(context, ProductSubListing.class);
                    i.putExtra("value","subcategory");
                    i.putExtra("subcategory_id", subcategory_id);
                    i.putExtra("subcategory_name_english", subcategory_name_english);
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
        public FrameLayout lay;
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
