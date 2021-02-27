package intem.smartpos.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import intem.smartpos.R;

public class Rest_NewComandActivity extends AppCompatActivity {

    private Button Rest_button_OpenTable;

    private void BindUI(){
        Rest_button_OpenTable = (Button) findViewById(R.id.Rest_button_OpenTable);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rest__new_comand);

        BindUI();



        Rest_button_OpenTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goCommand = new Intent(getApplicationContext(),Rest_CaptureCommandActivity.class);
                startActivity(goCommand);
            }
        });

    }





}
