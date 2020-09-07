package com.majlisstore.majlisstore.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.majlisstore.majlisstore.R;
import com.majlisstore.majlisstore.activities.OrderDetails;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class MyOrdersAdapter extends RecyclerView.Adapter<MyOrdersAdapter.ViewHolder> {

    Context context;
    ArrayList<HashMap<String, String>> list;

    public MyOrdersAdapter(ArrayList<HashMap<String, String>> list, Context applicationContext){
        super();
        this.list = list;
        this.context = applicationContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_my_orders_item, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public int getItemViewType(int position) {

        return position;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position)
    {

        final String o_number = list.get(position).get("o_number");
        final String o_date = list.get(position).get("o_date");
        final String o_total= list.get(position).get("o_total");
        final String o_payment = list.get(position).get("o_payment");
        final String o_pay_paid= list.get(position).get("o_pay_paid");
        final String tracking_status= list.get(position).get("tracking_status");
        final String o_id = list.get(position).get("o_id");
        final String order_cancel= list.get(position).get("order_cancel");
        final String delivery_date_time= list.get(position).get("delivery_date_time");

        String textOrderUniqueId="Ref: "+o_number;
        holder.order_unique_id.setText(textOrderUniqueId);
        String textTotal="Grand Total: QAR "+ o_total;
        holder.total.setText(textTotal);

        if(o_payment!=null && o_payment.equals("cash"))
        {
            holder.order_mode.setText(context.getResources().getString(R.string.paymode_cod));
        }
        else
        {
            holder.order_mode.setText(context.getResources().getString(R.string.paymode_online));
        }

        if (o_pay_paid!=null && o_pay_paid.equals("0"))
        {
            holder.order_payment.setText(context.getResources().getString(R.string.pay_unpaid));
        }
        else
        {
            holder.order_payment.setText(context.getResources().getString(R.string.pay_paid));
        }

        String ordDate=o_date;
        try
        {
            SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("MMM dd,yyyy", Locale.ENGLISH);

            if(o_date!=null && o_date.length()>0)
            {
                Date ordDate1=simpleDateFormat1.parse(o_date);
                if(ordDate1!=null)
                {
                    ordDate=simpleDateFormat2.format(ordDate1);
                }
            }
        }
        catch (Exception ignored)
        {
        }

        holder.order_date.setText(ordDate);

        String tracking_status_name;
        if(tracking_status!=null && tracking_status.length()>0)
        {
            switch (tracking_status) {
                case "0":
                    holder.img.setImageResource(R.drawable.status_box);
                    tracking_status_name = "Packed";
                    break;
                case "1":
                    holder.img.setImageResource(R.drawable.delivery);
                    tracking_status_name = "Shipped";
                    break;
                case "2":
                    holder.img.setImageResource(R.drawable.delivery_truck);
                    tracking_status_name = "Out for Delivery";
                    break;
                case "3":
                    holder.img.setImageResource(R.drawable.paid_box);
                    tracking_status_name = "Delivered";
                    break;
                case "10":
                    holder.img.setImageResource(R.drawable.canceled);
                    tracking_status_name = "Cancelled";

                    break;
                default:
                    holder.img.setImageResource(R.drawable.status_box);
                    tracking_status_name = "Order Placed";
                    break;
            }
        }
        else
        {
            holder.img.setImageResource(R.drawable.status_box);
            tracking_status_name="Order Placed";
        }

        if (order_cancel!=null && order_cancel.equals("1"))
        {
            holder.img.setImageResource(R.drawable.canceled);
            tracking_status_name="Cancelled";
        }

        holder.order_status.setText(tracking_status_name);

        holder.MyOrdersSingleItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try
                {
                    Intent i = new Intent(context, OrderDetails.class);
                    i.putExtra("o_id", o_id);
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

    static class ViewHolder extends RecyclerView.ViewHolder{

        public LinearLayout MyOrdersSingleItem,lay_status;
        public ImageView img;
        public TextView order_unique_id,total,order_mode,order_payment,order_status,order_date;

        public ViewHolder(View itemView) {

            super(itemView);
            MyOrdersSingleItem = itemView.findViewById(R.id.orders_single_item);
            order_unique_id = itemView.findViewById(R.id.order_unique_id);
            total = itemView.findViewById(R.id.total);
            order_mode = itemView.findViewById(R.id.order_mode);
            order_payment = itemView.findViewById(R.id.order_payment);
            order_status = itemView.findViewById(R.id.order_status);
            order_date = itemView.findViewById(R.id.order_date);

            img = itemView.findViewById(R.id.img);
            lay_status = itemView.findViewById(R.id.lay_status);

        }
    }
}

