package com.example.ainemawe.bookapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by AineMawe on 21/04/2015.
 *
 * adaptor
 *
 * Some code adapted from:
 *
 * "Android Display Images from SD Card Tutorial" by AndroidBegin,  15 April 2013
 * (http://www.androidbegin.com/tutorial/android-display-images-from-sd-card-tutorial/)
 */
public class ListViewAdapter extends BaseAdapter {
    private static LayoutInflater inflater = null;
    // Declare variables
    private Activity activity;
    private String[] filepath;
    private String[] filename;

    public ListViewAdapter(Activity a, String[] fpath, String[] fname) {
        activity = a;
        filepath = fpath;
        filename = fname;
        inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    public int getCount() {
        return filepath.length;

    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if (convertView == null)
            vi = inflater.inflate(R.layout.listview_item, null);
        // Locate the TextView in listview_item.xml
        TextView text = (TextView) vi.findViewById(R.id.Itemname);
        // Locate the ImageView in listview_item.xml
        ImageView image = (ImageView) vi.findViewById(R.id.image);

        // get file name and use substring to extract title and set to the TextView
        String bookTitle = filename[position];
        text.setText(bookTitle.substring(0, bookTitle.length() - 4));

//TODO Fix occasional infrequent overloads and gives java.lang.OutOfMemoryError - compress image

        // Decode the filepath with BitmapFactory followed by the position
        Bitmap bmp = BitmapFactory.decodeFile(filepath[position]);

        // Set the decoded bitmap into ImageView
        image.setImageBitmap(bmp);


        return vi;


    }
}