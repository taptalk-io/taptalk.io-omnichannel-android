package io.taptalk.taptalklive.helper;

import static io.taptalk.taptalklive.Const.TTLConstant.Broadcast.JSON_TASK_COMPLETED;
import static io.taptalk.taptalklive.Const.TTLConstant.Extras.JSON_STRING;
import static io.taptalk.taptalklive.Const.TTLConstant.Extras.JSON_URL;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import io.taptalk.taptalklive.TapTalkLive;

public class JsonTask extends AsyncTask<Void, Void, String> {

    public JsonTask(String jsonUrl) {
        this.jsonUrl = jsonUrl;
    }

    String jsonUrl;

    @Override
    protected String doInBackground(Void... voids) {
        HttpURLConnection urlConnection = null;
        BufferedReader bufferedReader = null;

        try {
            URL url = new URL(jsonUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuffer stringBuffer = new StringBuffer();
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line).append("\n");
            }
            if (stringBuffer.length() == 0) {
                return null;
            }
            else {
                return stringBuffer.toString();
            }
        }
        catch (IOException e) {
            return null;
        }
        finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onPostExecute(String jsonString) {
        super.onPostExecute(jsonString);

        Log.e(">>>>>>>>>>>>>", "onPostExecute: " + jsonString);
        if (jsonString != null) {
            TapTalkLive.getContentResponseMap().put(jsonUrl, jsonString);
            Intent intent = new Intent(JSON_TASK_COMPLETED);
            intent.putExtra(JSON_URL, jsonUrl);
            intent.putExtra(JSON_STRING, jsonString);
            LocalBroadcastManager.getInstance(TapTalkLive.context).sendBroadcast(intent);
        }
    }
}
