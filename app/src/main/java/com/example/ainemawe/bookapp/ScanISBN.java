package com.example.ainemawe.bookapp;
/**
 * Created by Aine Mawe
 *
 * Scans book barcode and retrieves and displays info from Google Books API
 *
 * Some code adapted from:
 *
 * "Android SDK: Create a Book Scanning App" by Sue Smith, 26 Jun 2013
 * (http://code.tutsplus.com/tutorials/android-sdk-create-a-book-scanning-app-displaying-book-information--mobile-17880)
 *
 * "How to programmatically take a screenshot in Android?" by S. Saurel, December 22, 2014
 * (http://www.ssaurel.com/blog/how-to-programmatically-take-a-screenshot-in-android/)
 *
 * "Get screenshot of device screen in Android" by Karan Balkar March 8, 2014
 * (http://karanbalkar.com/2014/03/get-screenshot-of-device-screen-in-android/)
 */

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;


public class ScanISBN extends ActionBarActivity implements View.OnClickListener {

    private static final String GOOGLE_APIS_BOOKS_URL = "https://www.googleapis.com/books/v1/volumes?q=isbn:";
    private static final String GOOGLE_APIS_KEY = "&key=AIzaSyDaXfJzujyC2B65f3R6J7WEwNi-SMSp-R4";
    private static final String AMAZON_URL = "http://www.amazon.co.uk/s/ref=nb_sb_noss_1?url=search-alias%3Dstripbooks&field-keywords=";
    private static final String SEARCH_ACADEMIC_LIB_URL = "http://opac.ittralee.ie/search/X?SEARCH=";
    private static final String SEARCH_PUBLIC_LIB_URL = "http://opac.kerrycoco.ie/ipac20/ipac.jsp?session=1C06285Y2I930.10855&menu=search&aspect=" +
            "subtab13&npp=10&ipp=20&spp=20&profile=kweb&ri=&session=1C06285Y2I930.10855&session=1C06285Y2I930.10855&menu=search&aspect=" +
            "subtab13&npp=10&ipp=20&spp=20&profile=kweb&ri=&index=ALTITLE&term=";
    private Button linkBtn, saveBtn, amazonBtn, academicLibraryBtn, publicLibraryBtn;
    private TextView authorText, titleText, publisherText, descriptionText, dateText, isbnText, pageText, categoriesText, ratingCountText;
    private String scanContent, title, author;
    private LinearLayout starLayout;
    private ImageView thumbView;
    private ImageView[] starViews;
    private Bitmap thumbImg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.results_display);

        //scan
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureLayout(R.layout.custom_capture);
        integrator.setPrompt("Place Red Line Across Barcode To Scan");
        integrator.initiateScan();


        linkBtn = (Button) findViewById(R.id.link_btn);
        linkBtn.setVisibility(View.GONE);
        linkBtn.setOnClickListener(this);
        saveBtn = (Button) findViewById(R.id.save_btn);
        saveBtn.setVisibility(View.GONE);
        saveBtn.setOnClickListener(this);
        amazonBtn = (Button) findViewById(R.id.amazon_btn);
        amazonBtn.setVisibility(View.GONE);
        amazonBtn.setOnClickListener(this);
        academicLibraryBtn = (Button) findViewById(R.id.academic_library_btn);
        academicLibraryBtn.setVisibility(View.GONE);
        academicLibraryBtn.setOnClickListener(this);
        publicLibraryBtn = (Button) findViewById(R.id.public_library_btn);
        publicLibraryBtn.setVisibility(View.GONE);
        publicLibraryBtn.setOnClickListener(this);


        //set views for book details
        authorText = (TextView) findViewById(R.id.book_author);
        titleText = (TextView) findViewById(R.id.book_title);
        publisherText = (TextView) findViewById(R.id.book_publisher);
        isbnText = (TextView) findViewById(R.id.book_isbn);
        dateText = (TextView) findViewById(R.id.book_date);
        pageText = (TextView) findViewById(R.id.book_page);
        categoriesText = (TextView) findViewById(R.id.book_categories);
        descriptionText = (TextView) findViewById(R.id.book_description);
        thumbView = (ImageView) findViewById(R.id.thumb);


        // ratings
        ratingCountText = (TextView) findViewById(R.id.book_rating_count);
        starLayout = (LinearLayout) findViewById(R.id.star_layout);
        starViews = new ImageView[5];
        for (int s = 0; s < starViews.length; s++) {
            starViews[s] = new ImageView(this);
        }

        //save state of app
        if (savedInstanceState != null) {
            authorText.setText(savedInstanceState.getString("author"));
            titleText.setText(savedInstanceState.getString("title"));
            publisherText.setText(savedInstanceState.getString("publisher"));
            dateText.setText(savedInstanceState.getString("date"));
            pageText.setText(savedInstanceState.getString("pageCount"));
            descriptionText.setText(savedInstanceState.getString("description"));
            ratingCountText.setText(savedInstanceState.getString("ratings"));
            int numStars = savedInstanceState.getInt("stars");//zero if null
            for (int s = 0; s < numStars; s++) {
                starViews[s].setImageResource(R.drawable.star);
                starLayout.addView(starViews[s]);
            }
            starLayout.setTag(numStars);

            thumbImg = savedInstanceState.getParcelable("thumbPic");
            thumbView.setImageBitmap(thumbImg);
            if (savedInstanceState.getInt("isLink") == View.VISIBLE) {
                linkBtn.setVisibility(View.VISIBLE);
            } else
                linkBtn.setVisibility(View.GONE);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scan_isbn, menu);
        //MenuItem shareItem = menu.findItem(R.id.menu_item_share);
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



    /**retrieve scan result*/
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);

        //check result
        if (result != null) {
            scanContent = result.getContents();
            String scanFormat = result.getFormatName();
           // result.getOrientation();
            if (scanContent != null && scanFormat != null && scanFormat.equalsIgnoreCase("EAN_13")) {
                String bookSearchString = GOOGLE_APIS_BOOKS_URL + scanContent + GOOGLE_APIS_KEY;
                new GetBookDetails().execute(bookSearchString);


            } else {
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Not a valid scan!", Toast.LENGTH_SHORT);
                toast.show();
                startActivity(new Intent(getApplicationContext(), Home.class));


            }
            //Log.v("SCAN", "content: " + scanContent + " - format: " + scanFormat);
        } else {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "No scan data received!", Toast.LENGTH_SHORT);
            toast.show();
            startActivity(new Intent(getApplicationContext(), Home.class));

        }

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.link_btn) {
            //TODO: Write as separate method
            //get the url tag
            String tag = (String) v.getTag();
            //launch the url
            Intent webIntent = new Intent(Intent.ACTION_VIEW);
            webIntent.setData(Uri.parse(tag));
            startActivity(webIntent);
        }
        //save screenshot
        // TODO: save to database would be better  - BLOB
        else {
            if (v.getId() == R.id.save_btn) {
                //TODO: Write as separate method

                // check if ext storage is available to write
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    //find directory
                    File pictureFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                    File LoveBooks = new File(pictureFolder, "LoveBooks");
                    //create directory if not there
                    if (!LoveBooks.exists()) {
                        LoveBooks.mkdir();
                    }

                    //set file path
                    String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/LoveBooks/" + title + ".jpg";

                    //capture screenshot
                    View v1 = getWindow().getDecorView().findViewById(R.id.results);
                    v1.setDrawingCacheEnabled(true);

                    Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
                    v1.setDrawingCacheEnabled(false);
                    //save image to file
                    OutputStream out = null;
                    File imageFile = new File(path);
                    try {
                        out = new FileOutputStream(imageFile);
                        // compress JPEG format
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
                        out.flush();
                        Toast.makeText(getApplicationContext(), "Saved!", Toast.LENGTH_SHORT).show();


                    } catch (FileNotFoundException e) {
                        // manage exception
                        Toast.makeText(getApplicationContext(), "Error File Not Found!", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        // manage exception
                        Toast.makeText(getApplicationContext(), "Error!", Toast.LENGTH_SHORT).show();
                    } finally {

                        try {
                            if (out != null) {
                                out.close();
                            }

                        } catch (Exception exc) {
                        }
                    }
                } else
                    Toast.makeText(this, "Not Saved! /n Error! No SDCARD Found!", Toast.LENGTH_LONG).show();
            }
            // TODO: bring in address from user
            //TODO: write as separate method
            else if (v.getId() == R.id.amazon_btn) {
                //launch the url
                String url = AMAZON_URL + scanContent;
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
            // TODO: bring in address from user
            //TODO: write as separate method
            else if (v.getId() == R.id.academic_library_btn) {
                //launch the url
                String url = SEARCH_ACADEMIC_LIB_URL + title + " " + author;
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


    }


    /**
     * save state of app
     *
     * @param savedBundle
     */
    protected void onSaveInstanceState(Bundle savedBundle) {
        savedBundle.putString("title", "" + titleText.getText());
        savedBundle.putString("author", "" + authorText.getText());
        savedBundle.putString("publisher", "" + publisherText.getText());
        savedBundle.putString("pageCount", "" + pageText.getText());
        savedBundle.putString("description", "" + descriptionText.getText());
        savedBundle.putString("date", "" + dateText.getText());
        savedBundle.putString("ratings", "" + ratingCountText.getText());
        savedBundle.putParcelable("thumbPic", thumbImg);
        if (starLayout.getTag() != null)
            savedBundle.putInt("stars", Integer.parseInt(starLayout.getTag().toString()));
        //savedBundle.putBoolean("isEmbed", previewBtn.isEnabled());
        savedBundle.putInt("isLink", linkBtn.getVisibility());
        savedBundle.putInt("isLink", saveBtn.getVisibility());


    }

    /**
     * retrieve  book info
     */
    private class GetBookDetails extends AsyncTask<String, Void, String> {


        /**
         *
          * @param bookURLs
         * @return
         */
        @Override
        protected String doInBackground(String... bookURLs) {
            StringBuilder bookBuilder = new StringBuilder();
            for (String bookSearchURL : bookURLs) {
                //search urls
                HttpClient bookClient = new DefaultHttpClient();
                try {
                    //get the data
                    HttpGet bookGet = new HttpGet(bookSearchURL);
                    HttpResponse bookResponse = bookClient.execute(bookGet);
                    StatusLine bookSearchStatus = bookResponse.getStatusLine();
                    if (bookSearchStatus.getStatusCode() == 200) {
                        //we have a result
                        HttpEntity bookEntity = bookResponse.getEntity();
                        InputStream bookContent = bookEntity.getContent();
                        InputStreamReader bookInput = new InputStreamReader(bookContent);
                        BufferedReader bookReader = new BufferedReader(bookInput);

                        String lineIn;
                        while ((lineIn = bookReader.readLine()) != null) {
                            bookBuilder.append(lineIn);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            //return results
            return bookBuilder.toString();
        }

        protected void onPostExecute(String result) {
            //parse search results
            try {
                JSONObject resultObject = new JSONObject(result);
                JSONArray bookArray = resultObject.getJSONArray("items");
                //get first result
                JSONObject bookObject = bookArray.getJSONObject(0);
                JSONObject volumeObject = bookObject.getJSONObject("volumeInfo");


                //attempt to download title found

                try {
                    title = volumeObject.getString("title");
                    titleText.setText("TITLE: " + title);

                } catch (JSONException jse) {
                    titleText.setText("");
                    jse.printStackTrace();
                }

                //attempt to download author found
                StringBuilder authorBuild = new StringBuilder(""); //for more than one author
                try {
                    JSONArray authorArray = volumeObject.getJSONArray("authors");
                    for (int a = 0; a < authorArray.length(); a++) {
                        if (a > 0) authorBuild.append(", ");
                        authorBuild.append(authorArray.getString(a));
                    }
                    author = authorBuild.toString();
                    authorText.setText("AUTHOR(S): " + author);
                } catch (JSONException jse) {
                    authorText.setText("");
                    jse.printStackTrace();
                }


                //attempt to download publisher
                try {
                    publisherText.setText("PUBLISHER: " + volumeObject.getString("publisher"));
                } catch (JSONException jse) {
                    publisherText.setText("PUBLISHER: ?");
                    jse.printStackTrace();
                }


                //attempt to download publication date
                try {
                    dateText.setText("DATE PUBLISHED: " + volumeObject.getString("publishedDate"));
                } catch (JSONException jse) {
                    dateText.setText("");
                    jse.printStackTrace();
                }

                //attempt to download page number
                try {
                    pageText.setText("PAGES: " + volumeObject.getString("pageCount"));
                } catch (JSONException jse) {
                    pageText.setText("");
                    jse.printStackTrace();
                }

                //attempt to download categories found
                StringBuilder categoriesBuild = new StringBuilder(""); //for more than one author
                try {
                    JSONArray categoriesArray = volumeObject.getJSONArray("categories");
                    for (int a = 0; a < categoriesArray.length(); a++) {
                        if (a > 0) categoriesBuild.append(", ");
                        categoriesBuild.append(categoriesArray.getString(a));
                    }
                    categoriesText.setText("CATEGORIES: " + categoriesBuild.toString());
                } catch (JSONException jse) {
                    categoriesText.setText("");
                    jse.printStackTrace();
                }


                //attempt to download description
                try {
                    descriptionText.setText("DESCRIPTION: " + volumeObject.getString("description"));
                } catch (JSONException jse) {
                    descriptionText.setText("");
                    jse.printStackTrace();
                }

                //star rating
                try {
                    //set stars
                    double decNumStars = Double.parseDouble(volumeObject.getString("averageRating"));
                    int numStars = (int) decNumStars;
                    starLayout.setTag(numStars);
                    starLayout.removeAllViews();
                    for (int s = 0; s < numStars; s++) {
                        starViews[s].setImageResource(R.drawable.star);
                        starLayout.addView(starViews[s]);
                    }
                } catch (JSONException jse) {
                    starLayout.removeAllViews();
                    jse.printStackTrace();
                }
                try {
                    ratingCountText.setText(" - " + volumeObject.getString("ratingsCount") + " ratings");
                } catch (JSONException jse) {
                    ratingCountText.setText("");
                    jse.printStackTrace();
                }

                //link button
                try {
                    linkBtn.setTag(volumeObject.getString("infoLink"));
                    linkBtn.setVisibility(View.VISIBLE);
                } catch (JSONException jse) {
                    linkBtn.setVisibility(View.GONE);
                    jse.printStackTrace();
                }

                saveBtn.setVisibility(View.VISIBLE);
                amazonBtn.setVisibility(View.VISIBLE);
                academicLibraryBtn.setVisibility(View.VISIBLE);
                saveBtn.setVisibility(View.VISIBLE);
                publicLibraryBtn.setVisibility(View.VISIBLE);
                isbnText.setText("ISBN: " + scanContent);


                //attempt to download image or set placeholder
                try {
                    JSONObject imageInfo = volumeObject.getJSONObject("imageLinks");
                    new GetCover().execute(imageInfo.getString("smallThumbnail"));
                } catch (JSONException jse) {
                    //thumbView.setImageBitmap(null);
                    Drawable myDrawable = getResources().getDrawable(R.drawable.book_cover);
                    thumbView.setImageDrawable(myDrawable);
                    jse.printStackTrace();
                }

            } catch (Exception e) {
                //what to display if no result retrieved
                e.printStackTrace();
                titleText.setText("NOT FOUND");
                authorText.setText("");
                publisherText.setText("");
                descriptionText.setText("");
                pageText.setText("");
                dateText.setText("");
                starLayout.removeAllViews();
                ratingCountText.setText("");
                thumbView.setImageBitmap(null);
                //BitmapFactory.decodeFile

                linkBtn.setVisibility(View.GONE);
            }
        }

        /**
         *get cover image
         */
         private class GetCover extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... thumbURLs) {
                //attempt to download image
                try {
                    URL thumbURL = new URL(thumbURLs[0]);
                    URLConnection thumbConn = thumbURL.openConnection();
                    thumbConn.connect();
                    InputStream thumbIn = thumbConn.getInputStream();
                    BufferedInputStream thumbBuff = new BufferedInputStream(thumbIn);
                    thumbImg = BitmapFactory.decodeStream(thumbBuff);
                    thumbBuff.close();
                    thumbIn.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return "";

            }

            protected void onPostExecute(String result) {
                thumbView.setImageBitmap(thumbImg);
            }

        }

    }


}

