package com.majlisstore.majlisstore.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.majlisstore.majlisstore.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class OrderedItemsListAdapter extends RecyclerView.Adapter<OrderedItemsListAdapter.ViewHolder> {

    Context context;
    ArrayList<HashMap<String, String>> getNotificationList;

    public OrderedItemsListAdapter(ArrayList<HashMap<String, String>> getNotificationList, Context applicationContext){
        super();
        this.getNotificationList = getNotificationList;
        this.context = applicationContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_ordered_item_list, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position)
    {
        final String category_name_english = getNotificationList.get(position).get("category_name_english");
        final String product_id= getNotificationList.get(position).get("product_id");
        final String product_name_en = getNotificationList.get(position).get("product_name_en");
        final String o_item_qty= getNotificationList.get(position).get("o_item_qty");
        final String o_item_amount= getNotificationList.get(position).get("o_item_amount");
        final String product_image_main= getNotificationList.get(position).get("product_image_main");

        holder.ItemName.setText(product_name_en);
        String textQty="Qty: "+o_item_qty;
        holder.ItemQuantity.setText(textQty);
        String textCategory="Category: "+category_name_english;
        holder.ItemCategory.setText(textCategory);
        String textPrice="Price(QAR) : "+o_item_amount;
        holder.ItemPrice.setText(textPrice);

        try
        {
            if(product_image_main!=null && product_image_main.length()>1)
            {
                String product_image_main1 = "https://majlisstore.com/crm/public/uploads/product_images/"+product_image_main;
                Picasso.get().load(product_image_main1).error(R.drawable.logo_h).into(holder.ItemImage);
            }
            else
            {
                Picasso.get().load(R.drawable.logo_h).error(R.drawable.logo_h).into(holder.ItemImage);
            }
        }
        catch (Exception ignored)
        {

        }

    }

    @Override
    public int getItemCount() {
        return getNotificationList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView ItemName,ItemQuantity,ItemCategory,ItemPrice;
        public LinearLayout OrderedProductSingleItem;
        public ImageView ItemImage;
        public ViewHolder(View itemView) {

            super(itemView);

            ItemName = itemView.findViewById(R.id.item_name);
            ItemQuantity = itemView.findViewById(R.id.item_quantity);
            ItemCategory = itemView.findViewById(R.id.item_category);
            ItemPrice = itemView.findViewById(R.id.item_price);
            OrderedProductSingleItem = itemView.findViewById(R.id.single_ordered_item_layout);
            ItemImage = itemView.findViewById(R.id.item_image);

        }
    }
}

