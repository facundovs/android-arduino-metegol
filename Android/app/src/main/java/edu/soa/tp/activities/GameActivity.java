package edu.soa.tp.activities;


import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.koushikdutta.async.AsyncServer;
import com.koushikdutta.async.http.server.AsyncHttpServer;

import java.text.DecimalFormat;

import edu.soa.tp.R;
import edu.soa.tp.constants.GameConstants;
import edu.soa.tp.utils.HttpReceiverAsyncTask;
import edu.soa.tp.utils.HttpUtils;


public class GameActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor light;
    private Sensor accelerometer;
    private TextView ejeY;
    private ImageView leftArrow;
    private ImageView rightArrow;
    private ImageView leftArrowBlack;
    private ImageView rightArrowBlack;
    private TextView luzTextView;
    private TextView scoreTextView;
    private AsyncHttpServer server = new AsyncHttpServer();
    private AsyncServer mAsyncServer = new AsyncServer();
    private TextView goalText ;
    private int delay = 0;
    private int lightCount = 0;
    private LightThread lightThread;
    private Boolean restart = false;
    private Boolean threadPaused;
    private Integer score =0;
    private String ip=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_main);

        ejeY = (TextView)findViewById(R.id.YtextView);
        ejeY.setVisibility(View.INVISIBLE);

        luzTextView = (TextView)findViewById(R.id.luzTextView);
        luzTextView.setVisibility(View.INVISIBLE);

        goalText = (TextView) findViewById(R.id.goalTextView);
        goalText.setText("");

        scoreTextView = (TextView)findViewById(R.id.scoreTextView);
        scoreTextView.setText("0");

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        light = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        sensorManager.registerListener(this,light, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        rightArrow = (ImageView) findViewById(R.id.flecha_der);
        leftArrow = (ImageView) findViewById(R.id.flecha_izq);
        leftArrowBlack = (ImageView) findViewById(R.id.flecha_izq_black);
        rightArrowBlack = (ImageView) findViewById(R.id.flecha_der_black);

       setIp();

      }

    private void setIp(){
        ip = getIntent().getStringExtra("IP");
        Toast.makeText(getApplicationContext(),"Conectado a la IP: " + ip,Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        sensorManager.unregisterListener(this, light);
        sensorManager.unregisterListener(this, accelerometer);
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        sensorManager.registerListener(this,light, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this,light, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    //Responde al evento del gol, pero no logra actualizar la UI. Solo lo informa vía log.
    private final Handler goalHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == GameConstants.NEW_GOAL) {
                Log.v("Info", "New Goal");
                goalText.post(new Runnable() {
                                  public void run() {
                                      goalText.setText("GOOOOOOOOOOOOL");
                                      goalText.setVisibility(View.VISIBLE);
                                  }
                              } );
            }
        }
    };




    @Override
    public void onSensorChanged(SensorEvent event) {
        int sensorDelay = delay%200;

        if(event.sensor.getType() == Sensor.TYPE_LIGHT){
            if(event.values[0] < 5){
                if(lightThread == null){
                    lightThread = new LightThread();
                    lightThread.start();
                }
                threadPaused = false;
            }else{
                threadPaused=true;
                lightCount = 0;
            }
        }
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            //ejeY.setText("Eje Y: " + Float.toString(event.values[1]));

                String path = getHost()+"sensors?position="+getServoValue(event.values[1])+"&&reset/";
                if(restart){
                    new HttpSenderAsyncTask().execute(path + "1","");
                    Log.v("Sending request",path + "1");
                    restart = false;
                }
                else{
                    new HttpSenderAsyncTask().execute(path+ "0","");
                    Log.v("Sending request",path + "0");
                }
            if(event.values[1] < -1){
                leftArrowBlack.setVisibility(View.VISIBLE);
                leftArrow.setVisibility(View.INVISIBLE);
                rightArrowBlack.setVisibility(View.INVISIBLE);
                rightArrow.setVisibility(View.VISIBLE);
            }
            else if(event.values[1] > 1){
                leftArrowBlack.setVisibility(View.INVISIBLE);
                rightArrowBlack.setVisibility(View.VISIBLE);
                rightArrow.setVisibility(View.INVISIBLE);
                leftArrow.setVisibility(View.VISIBLE);
            }
            else{
                leftArrowBlack.setVisibility(View.INVISIBLE);
                rightArrowBlack.setVisibility(View.INVISIBLE);
                rightArrow.setVisibility(View.VISIBLE);
                leftArrow.setVisibility(View.VISIBLE);
            }


        }
    }

    private String getHost(){

        return "http://" + ip + ":" + GameConstants.GALILEO_PORT+"/";
    }

    private String getServoValue (float value){
        int result = (int) (value *16 + 80);
        if(result > 160){
            result = 160;
        }
        if(result <0){
            result = 0;
        }
        String resultString =Integer.toString(result);
        if(resultString.length() ==1){
            return "00" +resultString;
        }

        if(resultString.length() == 2){
            return "0" + resultString;
        }

        Log.v("Servo Value: ",resultString);
        return resultString;
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

/*    private class DelayedThread extends Thread{

        @Override
        public void run() {
            int seg;
            while(true){
                try {
                    sleep(1);
                    delay++;
                    //Log.v("Info", Integer.toString(delay));
                    *//*seg = delay % 1000;
                    if(seg == 0){
                        Log.v("Info", "Enviar request!");
                    } *//*

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }


        }
    }*/

    private class LightThread extends Thread{

        @Override
        public void run() {
            while(true){
                lightCount = 0;
                    try {
                        while(lightCount < 2000) {
                            sleep(1);
                            lightCount++;
                        }
                        //new HttpSenderAsyncTask().execute(getHost() + GameConstants.RESTART_MARKER_PATH, "restart");
                        if(!threadPaused) {
                            restart = true;
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
            }
        }


        }
    class HttpSenderAsyncTask extends AsyncTask<String, Void, String> {
        String result = "";
        @Override
        protected String doInBackground(String... data) {
            result = HttpUtils.post(data[0],data[1]);
            return result;
        }

        @Override
        protected void onPostExecute(String a) {
            runOnUiThread(new Runnable() {
                public void run() {
                    Log.v("ON POST EXECUTE",result);
                    if(result.contains("GOAL") && ! result.contains("NO")) {
                        score = getScore(result);
                        scoreTextView.setText(score.toString());
                        Log.v("Info", "New Goal");
                        goalText.setText("GOOOOOOOL");
                        final Animation anim = new AlphaAnimation(0.0f, 1.0f);
                        anim.setDuration(70);
                        anim.setStartOffset(20);
                        anim.setRepeatMode(Animation.REVERSE);
                        anim.setRepeatCount(Animation.INFINITE);
                        goalText.startAnimation(anim);
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                anim.cancel();
                                goalText.setText("");
                            }
                        }, 2000);
                    }
                    if(result.contains("NO_GOAL")){
                        score = getScore(result);
                        if(score == 0)
                            scoreTextView.setText(score.toString());
                    }

                    }
            });
        }

        private int getScore(String data){
            String [] vector = data.split(":");
            return Integer.parseInt(vector[1]);
        }

    }
    }








