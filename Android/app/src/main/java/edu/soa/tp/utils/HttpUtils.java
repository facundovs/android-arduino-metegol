package edu.soa.tp.utils;

import android.util.Log;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;


/**
 * Created by facundo on 20/10/16.
 */

public class HttpUtils {
    public static String post(String path, String data){
        InputStream inputStream = null;
        String result=null;
        try {
            URL url = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "text/plain");

            System.out.println("Response Code: " + conn.getResponseCode());
            result = IOUtils.toString(conn.getInputStream(), StandardCharsets.UTF_8);
            if(conn.getResponseCode() == 201){
                Log.v("Result",result);
                return "GOAL:"+ result;
            }
        } catch (Exception e) {
            Log.d("Error in request: ", e.getLocalizedMessage());
        }

        return "NO_GOAL:"+ result;
    }

    public static String createJsonObject(String data){
        JSONObject jsonObject= new JSONObject();
        try {
            jsonObject.put("data",data);
        } catch (JSONException e) {
            Log.v("Error:","Problema al parsear Json");
        }
        Log.v("JSON:",jsonObject.toString());
        return  jsonObject.toString();
    }
}
