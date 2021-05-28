package com.mirea.kuznetsova.httpurlconnection;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private int REQUEST_CODE_PERMISSION_ACCESS_NETWORK_STATE = 1;
    private TextView ip;
    private TextView county;
    private TextView region;
    private TextView zip;
    private final String url = "http://ip-api.com/json/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        int permissionStatus = ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET);

        int permissionStatusInt = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE);

        if (permissionStatus != PackageManager.PERMISSION_GRANTED && permissionStatusInt != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET},
                    REQUEST_CODE_PERMISSION_ACCESS_NETWORK_STATE);
        }

        ip = findViewById(R.id.ip);

        county = findViewById(R.id.country);

        region = findViewById(R.id.region);

        zip = findViewById(R.id.zip);

    }

    public void onClick(View view) {

        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkinfo = null;

        if (connectivityManager != null) {

            networkinfo = connectivityManager.getActiveNetworkInfo();
        }


        if (networkinfo != null && networkinfo.isConnected()) {

            new DownloadPageTask().execute(url);

        } else {

            Toast.makeText(this, "Нет интернета", Toast.LENGTH_SHORT).show();

        }
    }

    private class DownloadPageTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {

            super.onPreExecute();

            ip.setText("Загружаем...");

        }

        @Override
        protected String doInBackground(String... urls) {

            try {

                return downloadIpInfo(urls[0]);

            } catch (IOException e) {

                e.printStackTrace();

                return "error";

            }
        }

        @Override
        protected void onPostExecute(String result) {

            Log.d(MainActivity.class.getSimpleName(), result);

            try {

                JSONObject responseJson = new JSONObject(result);

                String ipStr = responseJson.getString("query");

                String countyStr = responseJson.getString("country");

                String regionStr = responseJson.getString("regionName");

                String zipStr = responseJson.getString("zip");

                ip.setText(ipStr);

                county.setText(countyStr);

                region.setText(regionStr);

                zip.setText(zipStr);

                Log.d(MainActivity.class.getSimpleName(), ipStr);

            } catch (JSONException e) {

                e.printStackTrace();

            }

            super.onPostExecute(result);
        }
    }

    private String downloadIpInfo(String address) throws IOException {

        InputStream inputStream = null;

        String data = "";

        try {

            URL url = new URL(address);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setReadTimeout(100000);

            connection.setConnectTimeout(100000);

            connection.setRequestMethod("GET");

            connection.setInstanceFollowRedirects(true);

            connection.setUseCaches(false);

            connection.setDoInput(true);

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {

                inputStream = connection.getInputStream();

                ByteArrayOutputStream bos = new ByteArrayOutputStream();

                int read = 0;

                while ((read = inputStream.read()) != -1) {

                    bos.write(read);

                }

                byte[] result = bos.toByteArray();

                bos.close();

                data = new String(result);

            } else {

                data = connection.getResponseMessage() + " . Error Code : " + responseCode;

            }

            connection.disconnect();

        } catch (MalformedURLException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();

        } finally {

            if (inputStream != null) {

                inputStream.close();

            }

        }

        return data;
    }
}