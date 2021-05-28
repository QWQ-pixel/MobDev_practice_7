package com.mirea.kuznetsova.practice7;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {
    private String TAG = MainActivity.class.getSimpleName();
    private TextView mTextView;
    private String host = "time-a.nist.gov";
    private int port = 13;
    private int REQUEST_CODE_PERMISSION_INTERNET = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mTextView = findViewById(R.id.textView);

        int permissionStatus = ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET);

        if (permissionStatus != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.INTERNET},
                    REQUEST_CODE_PERMISSION_INTERNET);
        }
        
    }

    public void onClick(View view) {

        GetTimeTask timeTask = new GetTimeTask();

        timeTask.execute();
    }

    private class GetTimeTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {

            String timeResult = "";

            try {

                Socket socket = new Socket(host, port);

                BufferedReader reader = SocketUtils.getReader(socket);

                reader.readLine();

                timeResult = reader.readLine();

                Log.d(TAG,timeResult);

                socket.close();

            }  catch (IOException e) {
                e.printStackTrace();
            }

            return timeResult;
        }

        @Override
        protected void onPostExecute(String result) {

            super.onPostExecute(result);

            mTextView.setText(result);

        }

    }
}