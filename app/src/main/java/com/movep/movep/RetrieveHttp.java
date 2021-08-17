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
            client.setRequestProperty("Content-Type",
                    "application/json");
            client.setRequestProperty("auth-token", strings[3]);
            client.setDoOutput(true);
            client.setDoInput(true);
            try (DataOutputStream wr = new DataOutputStream(client.getOutputStream())) {
                wr.write(postData);
            }
            StringBuilder content;
            out.println(strings[0]+ "\n" + strings[1]+ "\n" + strings[2] + "\n" + strings[3]);
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(client.getInputStream()))) {

                String line;
                content = new StringBuilder();

                while ((line = br.readLine()) != null) {
                    content.append(line);
                    content.append(System.lineSeparator());
                }
                client.disconnect();
            }
            Log.e("\n\n\nRetrive\n\n\n", "Json=  \n\n\n" + content.toString() +"\n\n\n");
            return new JSONObject(content.toString());

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
