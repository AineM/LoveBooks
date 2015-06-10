package com.example.ainemawe.bookapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

/**
 * Created by AineMawe
 * Provides buttons to launch scanISBN activity, EnterBookISBN and ViewSaved activities
 */

public class Home extends ActionBarActivity {

    public final static String EXTRA_MESSAGE = "com.example.ainemawe.bookapp.MESSAGE";
    Button savedBooksBtn, scanISBNBtn, enterDetails;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }


    public void viewGallery(View v) {
        savedBooksBtn = (Button) v;
        startActivity(new Intent(getApplicationContext(), ViewSaved.class));
    }

    /**
     * Called when the user clicks the scan button - begins scanning
     */
    public void scanISBN(View v) {
        scanISBNBtn = (Button) v;
        startActivity(new Intent(getApplicationContext(), ScanISBN.class));
    }

    /**
     * Called when the user clicks the enter book details button - opens details page
     */
    public void enterDetails(View v) {
        enterDetails = (Button) v;
        startActivity(new Intent(getApplicationContext(), EnterBook.class));




    }
}
