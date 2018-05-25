package com.phobetor.promad;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Locale;

import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.text.PDFTextStripper;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    String address = null;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {android.Manifest.permission.READ_EXTERNAL_STORAGE};
    public static StringBuffer strContent;
    public static String text = null;
    TextToSpeech tts;
    DatabaseHelper db;
    public boolean flag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        verifyStoragePermissions(this);
        db = new DatabaseHelper(this);

        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR)
                {
                    tts.setLanguage(Locale.ENGLISH);
                }
            }
        });
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_add_circle_black_24dp);



        /****Adding action to the Floating Button******/
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                // 2. pick image only
                intent.setType("*/*");
                // 3. start activity
                startActivityForResult(intent, 0);
            }
        });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        RecentFiles myNotes = new RecentFiles();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragid, myNotes, "NOTE").commit();
    }


    public static void verifyStoragePermissions(Activity activity)
    {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if(permission != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        }
    }

    protected void onActivityResult(int reqCode, int resCode, Intent data)
    {
      //  Toast.makeText(this, "File Selected", Toast.LENGTH_SHORT).show();

        Uri uri = data.getData();
        address = getRealPathFromURI(uri);
        String[] splicer = address.split("/");
        boolean status = db.insertData(address, splicer[splicer.length-1]);
       // Toast.makeText(this, "" + status, Toast.LENGTH_SHORT).show();


        new ReadFile().execute(address);
        buildAlertBox("Your file is being loaded. Click OK to proceed");


       /* FileSelected fileSelected = new FileSelected();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragid, fileSelected, fileSelected.getTag()).commit();*/
    }

    private String getRealPathFromURI(Uri contentURI )
    {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if(cursor == null)
        {
            result = contentURI.getPath();
        }
        else
        {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    public String getStrContent()
    {
        return text;
    }

    public void sayMyName(String string)
    {

        tts.speak(string, TextToSpeech.QUEUE_FLUSH, null, null);
    }


    protected void buildAlertBox(String string)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(string);
        builder.setNeutralButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(flag==true) {
                            Intent intent = new Intent(MainActivity.this, TextLoaded.class);
                            intent.putExtra("TEXT", text);
                            startActivity(intent);
                        }
                        else
                        {
                            Toast.makeText(getBaseContext(), "Format not supported", Toast.LENGTH_LONG).show();
                            flag=true;
                        }
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    

    @SuppressWarnings("StatementWithEmptyBody")
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if(id == R.id.home)
        {
            RecentFiles myNotes = new RecentFiles();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.fragid, myNotes, "NOTE").commit();

        }
        else if (id == R.id.about) {
            Toast.makeText(this, "About", Toast.LENGTH_LONG).show();

        } else if (id == R.id.share) {
            Toast.makeText(this, "Share", Toast.LENGTH_LONG).show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public class ReadFile extends AsyncTask<String, String, String>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {


        }

        @Override
        protected String doInBackground(String... params) {

            try
            {
                int ch;
                 strContent = new StringBuffer("");
                File file = new File(params[0]);
                if(params[0].contains(".txt"))
                {
                    FileInputStream inputStream = new FileInputStream(file);
                    while ((ch = inputStream.read()) != -1) {
                        strContent.append((char) ch);
                    }
                    text = strContent.toString();
                    inputStream.close();

                }
                else if (params[0].contains(".pdf"))
                {
                    PDDocument document = PDDocument.load(file);
                    PDFTextStripper stripper = new PDFTextStripper();
                    text = stripper.getText(document);
                    document.close();

                }
                else
                {
                    flag=false;
                }

            }
            catch (Exception e)
            {
                //Toast.makeText(, e.getMessage(), Toast.LENGTH_LONG).show();
            }
            return null;
        }
    }
}
