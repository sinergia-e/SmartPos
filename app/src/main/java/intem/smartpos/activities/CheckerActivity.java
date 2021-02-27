package intem.smartpos.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import intem.smartpos.R;

public class CheckerActivity extends AppCompatActivity {

    public EditText InputProdToFind;

    private void BindUI(){

        InputProdToFind = (EditText) findViewById(R.id.editText_Checker_ProdToFind);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checker);
        BindUI();

        InputProdToFind.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                String CodeToFind = "";
                CodeToFind = InputProdToFind.getText().toString();
                if(hasFocus){

                }else{
                    Intent ShowData = new Intent(getApplicationContext(),CheckerResultActivity.class);
                    ShowData.putExtra("Code",CodeToFind);
                    startActivity(ShowData);
                }

            }
        });

    }










    @Override
    public void onBackPressed() {

        Intent Menu = new Intent(getApplicationContext(),MenuActivity.class);
        startActivity(Menu);

    }










}
