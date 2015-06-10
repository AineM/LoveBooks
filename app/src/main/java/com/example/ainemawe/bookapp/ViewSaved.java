package com.example.ainemawe.bookapp;

/**
 * Created by AineMawe.
 *
 * Displays saved screenshots in grid view
 *
 * Some code adapted from:
 * "Android Display Images from SD Card Tutorial" by AndroidBegin,  15 April 2013
 * (http://www.androidbegin.com/tutorial/android-display-images-from-sd-card-tutorial/)
 */

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;

public class ViewSaved extends ActionBarActivity {

    ListView list;
    ListViewAdapter adapter;
    File file;

    private String[] FilePathStrings;
    private String[] FileNameStrings;
    private File[] listFile;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview_main);

        // Check for SD Card
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, "Error! No SDCARD Found!", Toast.LENGTH_LONG)
                    .show();
        } else {
            // Locate the image folder in your SD Card
            file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/LoveBooks");
            // Create a new folder if no folder named LoveBooks exist
            file.mkdirs();
        }

        if (file.isDirectory()) {
            listFile = file.listFiles();
            // Create a String array for FilePathStrings
            FilePathStrings = new String[listFile.length];
            // Create a String array for FileNameStrings
            FileNameStrings = new String[listFile.length];

            for (int i = 0; i < listFile.length; i++) {
                // Get the path of the image file
                FilePathStrings[i] = listFile[i].getAbsolutePath();
                // Get the name image file
                FileNameStrings[i] = listFile[i].getName();
            }
        }

        // Locate the ListView in listview_main.xml
        list = (ListView) findViewById(R.id.list);
        // Pass String arrays to Adapter Class
        adapter = new ListViewAdapter(this, FilePathStrings, FileNameStrings);
        // Set the Adapter to the listView
        list.setAdapter(adapter);

        // Capture listview item click
        list.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                Intent i = new Intent(ViewSaved.this, ViewImage.class);
                // Pass String arrays FilePathStrings
                i.putExtra("filepath", FilePathStrings);
                // Pass String arrays FileNameStrings
                i.putExtra("filename", FileNameStrings);
                // Pass click position
                i.putExtra("position", position);
                startActivity(i);
            }

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

}