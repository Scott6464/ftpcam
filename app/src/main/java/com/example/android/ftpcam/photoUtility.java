package com.example.android.ftpcam;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static android.R.id.list;

/**
 * Created by Scott on 11/3/2017.
 */

public class photoUtility {

    private static final String TAG = "Photo Utility Functions";

    File sdCardPath;
    File photoPath;
    int jpgIndex;
    int dirIndex;
    Context c;
    List<String> jpgNames;
    List<String> dirNames;
    ftpUtility mftp;


    public photoUtility(Context c) {
        this.c = c;
        this.sdCardPath = c.getExternalFilesDir(null);
        jpgIndex = 0;
        mftp = new ftpUtility();

    }


    /**
     * Download photos from the ftp server and save them to the sd card.
     */

    public void download(int position) {
        dirIndex = position;
        jpgIndex = 0;
        mftp.ftpConnect();
        getDirectoryList();
        if (dirIndex == -1){dirIndex = dirNames.size() - 1;}
        mftp.ftpChangeDirectory("./" + dirNames.get(dirIndex) + "/images");
        jpgNames = mftp.ftpPrintFilesList("./");
        // create the directory on sd card
        File f = new File(sdCardPath, dirNames.get(dirIndex));
        if (!f.exists()) {
            f.mkdirs();
        }
        // download and save photos
        try {
            for (String jpgName : jpgNames) {
                File file = new File(sdCardPath + "/" + dirNames.get(dirIndex), jpgName);
                mftp.ftpDownload(jpgName,file);
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        mftp.ftpDisconnect();
        getList();
    }



    /**
     * Displays photo from arraylist jpgList of index jpgIndex.
     * Autoincrements jpgIndex and resets index when end of the arraylist of photos.
     */

    void display() {
        Log.i("path: ", sdCardPath + "/" + dirNames.get(dirIndex) + "/" + jpgNames.get(jpgIndex));
        try {
            ImageView IV = (ImageView) ((Activity) c).findViewById(R.id.iv1);
            Bitmap bMap = BitmapFactory.decodeFile(sdCardPath + "/" + dirNames.get(dirIndex) + "/" + jpgNames.get(jpgIndex));
            Log.i("photo displayed: ", jpgNames.get(jpgIndex));
            IV.setImageBitmap(bMap);
            jpgIndex++;
            if (jpgNames.size() == jpgIndex) {
                jpgIndex = 0;
            }
        } catch (Exception e) {
            Log.e("Exception", "Image open failed: " + e.toString());
        }
    }


    /**
     * @return an arraylist of the photo directories.
     */

    public List<String> getDirectoryList() {
        dirNames = new ArrayList<>();
        List<String> dirNamesTemp = mftp.ftpPrintDirectoryList("./");
        for (String d : dirNamesTemp) {
            if (d.contains("20")) {
                dirNames.add(d);
  //              Log.i("Directory1 ", d);
            } else {
  //              Log.i("File1 ", d);
            }
        }
        return dirNames;
    }


    /**
     * @return an arraylist of the photo filenames saved on the sd card.
     */

    public List<String> getList() {
        jpgNames = new ArrayList<String>();
        photoPath = new File(sdCardPath.toString(), dirNames.get(dirIndex));
        for (File f : photoPath.listFiles()) {
            if (f.isFile())
                if (f.getName().contains(".jpg")) {
                    jpgNames.add(f.getName());
                    Log.i("jpg name: ", f.getName());
                }
        }
        Log.i("Number of photos ", Integer.toString(jpgNames.size()));
        return jpgNames;
    }



}
