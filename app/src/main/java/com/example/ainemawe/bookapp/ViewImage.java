package com.example.ainemawe.bookapp;

/**
 * Created by AineMawe on 19/04/2015.
 */

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class ViewImage extends ActionBarActivity implements View.OnClickListener {

    private static final String AMAZON_URL = "http://www.amazon.co.uk/s/ref=nb_sb_noss_1?url=search-alias%3Dstripbooks&field-keywords=";
    private static final String SEARCH_ACADEMIC_LIB_URL = "http://opac.ittralee.ie/search/X?SEARCH=";
    private static final String SEARCH_PUBLIC_LIB_URL = "http://opac.kerrycoco.ie/ipac20/ipac.jsp?session=1C06285Y2I930.10855&menu=search&aspect=" +
            "subtab13&npp=10&ipp=20&spp=20&profile=kweb&ri=&session=1C06285Y2I930.10855&session=1C06285Y2I930.10855&menu=search&aspect=" +
            "subtab13&npp=10&ipp=20&spp=20&profile=kweb&ri=&index=ALTITLE&term=";


    TextView text;
    ImageView imageview;
    int position;
    String title;
    ShareActionProvider myShareActionProvider;
    private Button emailBtn, deleteBtn, amazonBtn, academicLibraryBtn, publicLibraryBtn;
    private String[] filepath;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the view from view_image.xml
        setContentView(R.layout.view_image);

        //buttons
        emailBtn = (Button) findViewById(R.id.email_btn);
        emailBtn.setOnClickListener(ViewImage.this);
        deleteBtn = (Button) findViewById(R.id.trash_btn);
        deleteBtn.setOnClickListener(ViewImage.this);
        amazonBtn = (Button) findViewById(R.id.amazon_btn);
        amazonBtn.setOnClickListener(this);
        academicLibraryBtn = (Button) findViewById(R.id.academic_library_btn);
        academicLibraryBtn.setOnClickListener(this);
        publicLibraryBtn = (Button) findViewById(R.id.public_library_btn);
        publicLibraryBtn.setOnClickListener(this);


        // Retrieve data from ViewSaved on ListView item click
        Intent i = getIntent();
        // Get the position
        position = i.getExtras().getInt("position");
        // Get String arrays FilePathStrings
        filepath = i.getStringArrayExtra("filepath");
        // Get String arrays FileNameStrings
        String[] filename = i.getStringArrayExtra("filename");
        title = filename[position].substring(0, filename[position].length() - 4);


        // Locate the ImageView in view_image.xml
        imageview = (ImageView) findViewById(R.id.full_image_view);
        // Decode the filepath with BitmapFactory followed by the position
        Bitmap bmp = BitmapFactory.decodeFile(filepath[position]);
        // Set the decoded bitmap into ImageView
        imageview.setImageBitmap(bmp);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem shareItem = menu.findItem(R.id.menu_item_share);
        if (shareItem != null) {
            myShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);
        }
        setShareIntent();

        return true;
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.trash_btn) {
//check for confirmation and delete
            new AlertDialog.Builder(ViewImage.this)
                    .setTitle("Delete entry")
                    .setMessage("Are you sure you want to delete this entry?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            File image = new File(filepath[position]);
                            image.delete();
                            Toast.makeText(getApplicationContext(), "Deleted!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();


        } else if (v.getId() == R.id.email_btn) {
            //set email settings
            Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
            emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "I think you may find this book interesting");
            emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Book Recommendation");
            emailIntent.setData(Uri.parse("mailto:" + " "));
            emailIntent.setType("text/html");
            File image = new File("file://" + filepath[position]);
            Uri uri = Uri.parse("file://" + image);
            emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
            startActivity(Intent.createChooser(emailIntent, "Choose an Email client"));

        }

        // TODO: bring in address from user
        //TODO: write as separate method
        else if (v.getId() == R.id.amazon_btn) {
            //launch the url
            String url = AMAZON_URL + title;
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        }
        // TODO: bring in address from user
        //TODO: write as separate method
        else if (v.getId() == R.id.academic_library_btn) {
            //launch the url
            String url = SEARCH_ACADEMIC_LIB_URL + title;
            Intent in = new Intent(Intent.ACTION_VIEW);
            in.setData(Uri.parse(url));
            startActivity(in);
        }
        // TODO: bring in address from user
        //TODO: write as separate method
        else if (v.getId() == R.id.public_library_btn) {
            //launch the url
            String url = SEARCH_PUBLIC_LIB_URL + title;
            Intent in = new Intent(Intent.ACTION_VIEW);
            in.setData(Uri.parse(url));
            startActivity(in);
        }

    }

    private void setShareIntent() {

        if (myShareActionProvider != null) {
            // create an Intent with the contents
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            File image = new File("file://" + filepath[position]);
            Uri uri = Uri.parse("file://" + image);
            shareIntent.setType("text/html");
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Book Recommendation");
            myShareActionProvider.setShareIntent(shareIntent);
        }
    }
}