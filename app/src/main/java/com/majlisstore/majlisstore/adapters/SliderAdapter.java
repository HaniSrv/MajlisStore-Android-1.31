package com.majlisstore.majlisstore.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.majlisstore.majlisstore.R;
import com.majlisstore.majlisstore.activities.ProductSubListing;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class SliderAdapter extends PagerAdapter {

    ArrayList<HashMap<String, String>> list;
    Context context;

    public SliderAdapter(Context context, ArrayList<HashMap<String, String>> list) {
        this.context = context;
        this.list=list;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup view, int position)
    {
        LayoutInflater inflater = LayoutInflater.from(context);
        View layoutView = inflater.inflate(R.layout.list_slider, view, false);

        assert layoutView != null;
        ImageView img = layoutView.findViewById(R.id.img);

        final String slider_image = list.get(position).get("slider_image");
        final String slider_url = list.get(position).get("slider_url");
        final String section = list.get(position).get("section");

        String url;
        if (section!=null && section.equalsIgnoreCase("slider"))
        {

            url = "https://majlisstore.com/crm/public/uploads/website_slider_images/";
        }
        else
        {
            url = "https://majlisstore.com/crm/public/uploads/website_section_image/";
        }

        String sliderImage;
        if(slider_image!=null && slider_image.length()>0)
        {
            sliderImage=url+slider_image;
            Picasso.get().load(sliderImage).fit().error(R.drawable.logo_h).into(img);
        }
        else
        {
            Picasso.get().load(R.drawable.logo_h).fit().error(R.drawable.logo_h).into(img);
        }


        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try
                {
                    Intent i = new Intent(context, ProductSubListing.class);
                    i.putExtra("slider_url",slider_url);
                    i.putExtra("value","slider");
                    context.startActivity(i);
                }
                catch(Exception ignored)
                {

                }
            }
        });

        view.addView(layoutView, 0);

        return layoutView;
    }

    @Override
    public boolean isViewFromObject(View view, @NonNull Object object) {
        return view.equals(object);
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

}
