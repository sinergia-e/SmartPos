package intem.smartpos.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import intem.smartpos.R;

public class welcome extends AppCompatActivity {

    Button Go;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);


        Go = (Button) findViewById(R.id.Welcome1_go);
        Go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent go2 = new Intent(getApplicationContext(),Welcome2Activity.class);
                startActivity(go2);
            }
        });

    }
}
