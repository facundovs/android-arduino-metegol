package edu.soa.tp.utils;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by facundo on 20/10/16.
 */

public class HttpSenderAsyncTask extends AsyncTask<String, Void, String> {
    @Override
    protected String doInBackground(String... data) {
        return HttpUtils.post(data[0],data[1]);
    }

    @Override
    protected void onPostExecute(String result) {

    }


}