package com.movep.movep;

import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import static java.lang.System.out;

public class RetrieveHttp extends AsyncTask<String, Void, JSONObject> {
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected JSONObject doInBackground(String... strings) {

        try{
            URL url = new URL(strings[0]);
            HttpURLConnection client = null;
            String urlParameters = strings[2];
            byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
            client = (HttpURLConnection) url.openConnection();
            client.setRequestMethod(strings[1]);
            client.setDoOutput(false);
            client.setDoInput(true);
            client.connect();
            int status = client.getResponseCode();
            return new JSONObject("{\"status\":"+String.valueOf(status)+"}");

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
