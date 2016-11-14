package edu.soa.tp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;

import edu.soa.tp.R;


public class MenuActivity extends AppCompatActivity {
    private ImageButton ballButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        ballButton = (ImageButton) findViewById(R.id.ballButton);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Metegol");
        if(ballButton!= null)
        ballButton.setOnClickListener(handlerForBallButton);
    }

    View.OnClickListener handlerForBallButton = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getBaseContext(), IPConnectionActivity.class);
            startActivity(intent);
            finish();
        }
    };


}
