package com.tegon.download.app;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BinaryHttpResponseHandler;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.apache.http.Header;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

@EActivity
public class MainActivity extends Activity {
    ProgressDialog progressDialog;
    static final String DOWNLOAD_URL = "http://img2.clickjogos.uol.com.br/dl/test_mobile/src/emoji-quiz.zip";
    private AQuery aq;

    @Click
    void download() {
        //showDialog();
        //nativeDownload();
        //downloadManager();
        //removeDialog();
        //androidQueryDownload();
        asyncHttpDownload();
    }

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_main);
    }

    @Background
    void downloadManager(){
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(DOWNLOAD_URL));
        request.setDescription("Zip Download");
        request.setTitle("Game download");
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "game.zip");

        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
    }

    void androidQueryDownload(){
        File ext = Environment.getExternalStorageDirectory();
        System.out.println("ext " + ext);
        File target = new File(ext, "barzin.zip");
        System.out.println("target " + target);
        aq = new AQuery(this);
        System.out.println("aq " + aq);
        aq.progress(R.id.progress).download(DOWNLOAD_URL, target, new AjaxCallback<File>() {
            public void callback(String url, File file, AjaxStatus status) {
                if (file != null) {
                    System.out.println("downloaded " + file + status);
                } else {
                    System.out.println("failed " + status.getMessage());
                }
            }
        });
    }

    @Background
    void asyncHttpDownload() {
        System.out.println("//////////////////////////////");
        AsyncHttpClient client = new AsyncHttpClient();
        String[] allowedTypes = new String[]{ "application/zip" };
        client.get(DOWNLOAD_URL, new BinaryHttpResponseHandler(allowedTypes){
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] fileData) {
                System.out.println("async response " + " status " + statusCode + fileData);

                File zip = new File(Environment.getExternalStorageDirectory(), "gameZip.zip");

                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(zip.getPath());
                    fileOutputStream.write(fileData[0]);
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] fileData, Throwable error) {
                System.out.println("error " + " status " + statusCode + fileData);
            }
        });
    }

    @Background
    void nativeDownload(){
        int count;

        try {
            URL url = new URL(DOWNLOAD_URL);
            URLConnection connection = url.openConnection();
            connection.connect();
            int lengthOfFile = connection.getContentLength();

            InputStream inputStream = new BufferedInputStream(url.openStream(), 20048);

            OutputStream outputStream = new FileOutputStream("/sdcard/game2.zip");

            byte data[] = new byte[1024];

            long total = 0;

            while((count = inputStream.read(data)) != -1) {
                total += count;
                outputStream.write(data, 0, count);
            }

            outputStream.flush();
            outputStream.close();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @UiThread
    void showDialog() {
        progressDialog = ProgressDialog.show(this, "", "Carregando...", true, false);
    }

    @UiThread
    void removeDialog() {
        progressDialog.dismiss();
    }
}
