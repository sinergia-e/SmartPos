package intem.smartpos.activities;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import intem.smartpos.R;
import intem.smartpos.database.connection;

public class Welcome2Activity extends AppCompatActivity {
    connection link;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome2);

        link = new connection(getApplicationContext(), "MyBusinessAndroid", null, 1);

        Button DropDb = (Button) findViewById(R.id.Welcome2_DropDb);
        Button Go3 = (Button) findViewById(R.id.Welcome2_Go3);

        DropDb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DropTables();
            }
        });



        Go3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent go3 = new Intent(getApplicationContext(),welcome3Activity.class);
                startActivity(go3);
            }
        });


    }









    public void DropTables(){

        getApplicationContext().deleteDatabase("MyBusinessAndroid");

        link = new connection(getApplicationContext(), "MyBusinessAndroid", null, 1);
        SQLiteDatabase db = link.getWritableDatabase();
        finishAffinity();
    }







}
