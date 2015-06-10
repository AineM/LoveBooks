package com.example.ainemawe.bookapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by AineMawe
 * Activity that takes user input and sends as intent to Search activity
 */
public class EnterBook extends ActionBarActivity implements View.OnClickListener {

    public final static String EXTRA_MESSAGE = "com.example.ainemawe.bookapp.MESSAGE";
    EditText editISBN;
    Button buttonSearch, buttonCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_book);


        editISBN = (EditText) findViewById(R.id.editISBN);

        //buttons
        buttonSearch = (Button) findViewById(R.id.buttonSearch);
        buttonSearch.setOnClickListener(this);
        buttonCancel = (Button) findViewById(R.id.buttonCancel);
        buttonCancel.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.buttonSearch) {
            //Use inputted text for search
            String queryISBN = editISBN.getText().toString();
            if (queryISBN.length() == 13 || queryISBN.length() == 10) {
                Intent intent = new Intent(EnterBook.this, Search.class);
                intent.putExtra(EXTRA_MESSAGE, queryISBN);
                startActivity(intent);
            } else {
                new AlertDialog.Builder(EnterBook.this)
                        .setTitle("ISBN Not Valid")
                        .setMessage("ISBN must be 10 or 13 digits")
                        .setPositiveButton(android.R.string.yes,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

            }
        } else if (v.getId() == R.id.buttonCancel) {
            startActivity(new Intent(getApplicationContext(), Home.class));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu and Adds items to the action bar
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

}