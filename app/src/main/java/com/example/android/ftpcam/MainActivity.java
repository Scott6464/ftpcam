package com.example.android.ftpcam;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static android.R.attr.password;
import static android.R.attr.phoneNumber;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivityFunctions";
    photoUtility photos;
    boolean spinnerHack = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        photos = new photoUtility(this);
        new DownloadTask(this, photos, -1).execute();
    }


    public class DownloadTask extends AsyncTask<String, Void, String> {

        Context c;
        photoUtility photos;
        int position;

        public DownloadTask(Context c, photoUtility photos, int position) {
            this.c = c;
            this.photos = photos;
            this.position = position;
            spinnerHack = false;
        }

        @Override
        protected String doInBackground(String... params) {
            photos.download(position);
            return "hi";
        }

        @Override
        protected void onPostExecute(String githubSearchResults) {
            Spinner spinner = (Spinner) findViewById(R.id.spDirectories);
            ArrayAdapter adapter = new ArrayAdapter(c, R.layout.support_simple_spinner_dropdown_item, photos.dirNames);
            spinner.setAdapter(adapter);
            spinner.setSelection(photos.dirIndex);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    if (spinnerHack) {
                        TextView tv = (TextView) findViewById(R.id.TV1);
                        tv.setText("Loading " + photos.dirNames.get(position));
                        new DownloadTask(c, photos, position).execute();
                    } else {
                        spinnerHack = true;
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // your code here
                }
            });
            photos.display();
            TextView tv = (TextView) findViewById(R.id.TV1);
            tv.setText("Loaded " + photos.jpgNames.size() + " photos");
        }
    }


    public void nextJPG(View v) {
        //List<String> jpgNames = photos.getList();
        photos.display();
        //Toast.makeText(this, Integer.toString(photos.jpgIndex), Toast.LENGTH_LONG).show();
    }


    public void writeToFile(String data) {
        File root = getExternalFilesDir(null);
        File file = new File(root, "jpgs.txt");
        try {
            file.createNewFile();
            FileOutputStream fOut = new FileOutputStream(file);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append(data);
            myOutWriter.close();
            fOut.flush();
            fOut.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }


}