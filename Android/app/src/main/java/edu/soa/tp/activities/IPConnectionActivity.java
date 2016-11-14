package edu.soa.tp.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import edu.soa.tp.R;
import edu.soa.tp.constants.GameConstants;

public class IPConnectionActivity extends AppCompatActivity {
    private EditText ipText;
    private TextView titleTextView;
    private Button acceptButton;
    private Button defaultIpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ipconnection);
        acceptButton = (Button)findViewById(R.id.acceptButton);
        defaultIpButton = (Button)findViewById(R.id.defaultIpButton);
        titleTextView = (TextView) findViewById(R.id.titleTextView);
        titleTextView.setTextColor(Color.WHITE);
        ipText  = (EditText)findViewById(R.id.ipText);
        ipText.setHint(R.string.ingrese_la_ip_de_la_placa_galileo);
        acceptButton.setOnClickListener(handlerForAcceptButton);
        defaultIpButton.setOnClickListener(handlerForDefaultIpButton);
        Typeface type = Typeface.createFromAsset(getAssets(),"fonts/redzone.ttf");
        titleTextView.setTypeface(type);
        titleTextView.setTextSize(29);
    }


    View.OnClickListener handlerForAcceptButton = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String ip = ipText.getText().toString();
            Log.v("IP input", ipText.getText().toString());
            if(ip.isEmpty()){
                Log.v("IP error","The value is null");
                Toast.makeText(getApplicationContext(),"Por favor ingrese una IP válida",Toast.LENGTH_LONG).show();
            }else{
                configureUrl(ip);
            }

        }
    };
    View.OnClickListener handlerForDefaultIpButton = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.v("Info:","Default IP button selected");
            configureUrl(GameConstants.GALILEO_DEFAULT_IP);
        }
    };

    public void configureUrl(String ip){
        if(isConnected()){
            Intent intent = new Intent(this, GameActivity.class);
            intent.putExtra("IP",ip);
            startActivity(intent);
            finish();
        }
        else{
            Toast.makeText(getApplicationContext(), "No tenés conexión a Internet. Por favor intentá más tarde", Toast.LENGTH_SHORT).show();
        }

    }


    private boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

}
