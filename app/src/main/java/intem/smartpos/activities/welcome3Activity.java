package intem.smartpos.activities;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import intem.smartpos.R;
import intem.smartpos.database.connection;

public class welcome3Activity extends AppCompatActivity {
    connection link;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome3);
        link = new connection(getApplicationContext(), "MyBusinessAndroid", null, 1);



        Button goLogin = (Button) findViewById(R.id.Welcome3_goLogin);
        goLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CloseSetUp();
            }
        });

    }


    private void CloseSetUp(){

        SQLiteDatabase dbW = link.getWritableDatabase();
        dbW.execSQL("update AppConfig set InitialSetup = 0 ");

        Intent goLogin = new Intent(this,LoginActivity.class);
        startActivity(goLogin);
    }


}
