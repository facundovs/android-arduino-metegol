package edu.soa.tp.utils;

import android.opengl.Visibility;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;


import com.koushikdutta.async.AsyncServer;
import com.koushikdutta.async.http.server.AsyncHttpServer;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;
import com.koushikdutta.async.http.server.AsyncHttpServerResponse;
import com.koushikdutta.async.http.server.HttpServerRequestCallback;

import edu.soa.tp.constants.GameConstants;

/**
 * Created by facundo on 21/10/16.
 */

public class HttpReceiverAsyncTask extends AsyncTask<Handler, Void, String> {

    private AsyncHttpServer server = new AsyncHttpServer();
    private AsyncServer mAsyncServer = new AsyncServer();
    private TextView textView;
    private boolean isGoal = false;

    private void startServer(final Handler goalHandler) {
        server.get("/goal", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                Log.v("Receiving information:","Gol was done");

                Message msg = goalHandler.obtainMessage();
                goalHandler.sendEmptyMessage(GameConstants.NEW_GOAL);
                response.send("Goal received");
            }
        });

        server.listen(mAsyncServer, GameConstants.APP_PORT);
    }


    @Override
    protected String doInBackground(Handler... params) {
        startServer(params[0]);
        return null;
    }


}
