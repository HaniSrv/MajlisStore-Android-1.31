package com.majlisstore.majlisstore.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.majlisstore.majlisstore.R;

import java.util.ArrayList;
import java.util.HashMap;

public class MyNotificationsAdapter extends RecyclerView.Adapter<MyNotificationsAdapter.ViewHolder> {

    Context context;
    ArrayList<HashMap<String, String>> getNotificationList;

    public MyNotificationsAdapter(ArrayList<HashMap<String, String>> getNotificationList, Context applicationContext){
        super();
        this.getNotificationList = getNotificationList;
        this.context = applicationContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_notification_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position)
    {
        final String notification_content = getNotificationList.get(position).get("notification_content");
        final String notification_datetime = getNotificationList.get(position).get("notification_datetime");
        final String notification_head = getNotificationList.get(position).get("notification_head");
        final String notification_id= getNotificationList.get(position).get("notification_id");

        holder.NotificationHead.setText(notification_head);
        holder.Notification_content.setText(notification_content);
        holder.Notification_datetime.setText(notification_datetime);
    }

    @Override
    public int getItemCount() {
        return getNotificationList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView NotificationHead,Notification_content,Notification_datetime;

        public ViewHolder(View itemView)
        {
            super(itemView);
            NotificationHead = itemView.findViewById(R.id.notification_head);
            Notification_content = itemView.findViewById(R.id.notification_content);
            Notification_datetime = itemView.findViewById(R.id.notification_time);

        }
    }
}

