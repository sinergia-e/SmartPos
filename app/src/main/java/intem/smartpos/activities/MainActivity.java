package intem.smartpos.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import intem.smartpos.R;

public class MainActivity extends AppCompatActivity {

    private Button btBack;
    private ImageView Logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btBack = (Button) findViewById(R.id.buttonBackMenu);
        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goMenu = new Intent(getApplicationContext(), MenuActivity.class);
                startActivity(goMenu);
            }
        });

    }
}

