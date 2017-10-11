package com.salam.elearning.Utils;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.salam.elearning.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadTask {

    private static final String TAG = "Download_Task";
    private String downloadUrl = "", downloadFileName = "";
    private View view;

    //String Values to be Used in App
    private static final String downloadDirectory = "Salam-e-Learning";

    public DownloadTask(Context context, String downloadUrl, String downloadedFileName, View view) {
        this.downloadUrl = downloadUrl;
        this.view = view;

        String extension = getExtension(downloadUrl);
        if(extension.equalsIgnoreCase("mp4") || extension.equalsIgnoreCase("avi")||
                extension.equalsIgnoreCase("mkv") || extension.equalsIgnoreCase("flv") ||
                extension.equalsIgnoreCase("mpeg-4")) {
            downloadFileName = downloadedFileName + "." + extension;//Create file name by picking download file name from URL
            Log.e(TAG, downloadFileName);

            //Start Downloading Task
            new DownloadingTask().execute();
        }else{
            Utils.showSnackBar(view, "Invalid File.", Snackbar.LENGTH_SHORT);
        }

    }

    private class DownloadingTask extends AsyncTask<Void, Void, Void> {

        File apkStorage = null;
        File outputFile = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void result) {
            try {
                if (outputFile != null) {

                } else {

                    Utils.showSnackBar(view, "Download Failed", Snackbar.LENGTH_SHORT);

                }
            } catch (Exception e) {
                e.printStackTrace();

                Utils.showSnackBar(view, "Download Failed", Snackbar.LENGTH_SHORT);
                Log.e(TAG, e.getLocalizedMessage());

            }


            super.onPostExecute(result);
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                URL url = new URL(downloadUrl);//Create Download URl
                HttpURLConnection c = (HttpURLConnection) url.openConnection();//Open Url Connection
                c.setRequestMethod("GET");//Set Request Method to "GET" since we are grtting data
                c.connect();//connect the URL Connection

                //If Connection response is not OK then show Logs
                if (c.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    Log.e(TAG, "Server returned HTTP " + c.getResponseCode() + " " + c.getResponseMessage());

                }


                //Get File if SD card is present
                if (isSDCardPresent()) {

                    apkStorage = new File(Environment.getExternalStorageDirectory() + "/" + downloadDirectory);
                } else
                    Utils.showSnackBar(view, "Oops!! There is no SD Card.", Snackbar.LENGTH_SHORT);

                //If File is not present create directory
                if (!apkStorage.exists()) {
                    apkStorage.mkdir();
                    Log.e(TAG, "Directory Created.");
                }

                outputFile = new File(apkStorage, downloadFileName);//Create Output file in Main File

                //Create New File if not present
                if (!outputFile.exists()) {
                    outputFile.createNewFile();
                    Log.e(TAG, "File Created");
                }

                FileOutputStream fos = new FileOutputStream(outputFile);//Get OutputStream for NewFile Location

                InputStream is = c.getInputStream();//Get InputStream for connection

                byte[] buffer = new byte[1024];//Set buffer type
                int len1 = 0;//init length
                while ((len1 = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len1);//Write new file
                }

                //Close all connection after doing task
                fos.close();
                is.close();
                Utils.showSnackBar(view, "Chapter Downloaded", Snackbar.LENGTH_SHORT);

            } catch (Exception e) {

                //Read exception if something went wrong
                e.printStackTrace();
                outputFile = null;
                Utils.showSnackBar(view, "Download Failed", Snackbar.LENGTH_SHORT);
                Log.e(TAG, "Download Error Exception " + e.getMessage());
            }

            return null;
        }
    }

    private boolean isSDCardPresent() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    private String getExtension(String str){
        return str.substring(str.lastIndexOf(".") + 1);
    }
}