package intem.smartpos.activities;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

import intem.smartpos.R;
import intem.smartpos.database.connection;

public class CheckerResultActivity extends AppCompatActivity {

    private TextView Descrip,Price1;
    private String CodeProd;
    private connection link;

    private void BindUI(){
        Descrip = (TextView) findViewById(R.id.textView_CheckerRs_DescripProd);
        Price1 = (TextView) findViewById(R.id.textView_CheckerRs_Price1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checker_result);
        link = new connection(getApplicationContext(), "MyBusinessAndroid", null, 1);
        BindUI();

        Bundle ProdData = getIntent().getExtras();
        if(ProdData != null){
            CodeProd = ProdData.getString("Code");
            SQLiteDatabase dbR = link.getReadableDatabase();
            Cursor ProdDetails = dbR.rawQuery("select Description,Price1 from Prods where Code like '%"+CodeProd+"%' ",null);

            if(ProdDetails.moveToFirst()){
                Descrip.setText(ProdDetails.getString(0));

                DecimalFormat formateador = new DecimalFormat("###,###,###,###.00");
                String PriceFr = formateador.format(Double.valueOf(ProdDetails.getString(1)));
                Price1.setText(PriceFr);

                Return();

            }else{
                Descrip.setText("No Encontramos este producto, pregunta con un asociado por favor");
                Price1.setText("");

                Return();

            }
        }


    }



    private void Return(){

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                Intent Return = new Intent(getApplicationContext(),CheckerActivity.class);
                startActivity(Return);
            }
        }, 5000);

    }

}
