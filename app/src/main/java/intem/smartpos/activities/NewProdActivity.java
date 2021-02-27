package intem.smartpos.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import intem.smartpos.R;
import intem.smartpos.database.connection;

import static java.lang.Float.valueOf;

public class NewProdActivity extends AppCompatActivity {

    private EditText NewCode;
    private EditText NewDescrip;
    private EditText NewPrice;
    private Switch IvaTax;
    private Button buttonNewProd;
    connection link;
    private String Users;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_prod);

        link = new connection(getApplicationContext(), "MyBusinessAndroid", null, 1);
        buttonNewProd = (Button) findViewById(R.id.buttonNewProd);

        buttonNewProd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InsertProd();
            }
        });

    }


    @Override
    public void onBackPressed() {
        Intent goMenu = new Intent(getApplicationContext(),MenuActivity.class);
        startActivity(goMenu);
    }


    private void InsertProd(){

        NewCode = (EditText) findViewById(R.id.editTextCode);
        NewDescrip = (EditText) findViewById(R.id.editTextDescrip);
        NewPrice = (EditText) findViewById(R.id.editTextPrice);
        IvaTax = (Switch) findViewById(R.id.switchTax);

        String Tax = "";
        if(IvaTax.isChecked()==true){

            Tax = "IVA";
        }else{
            Tax = "SYS";
        }

        final String ProdTax = Tax;
        final String CodeProd = NewCode.getText().toString().trim();
        final String DescriptionProd = NewDescrip.getText().toString().trim();
        final double PriceProd = Double.parseDouble(NewPrice.getText().toString());

        link = new connection(this, "MyBusinessAndroid", null, 1);
        SQLiteDatabase db = link.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("Code", CodeProd);
        values.put("Description", DescriptionProd);
        values.put("ProdCategory", "NewCategory");
        values.put("Brand", "SYS");
        values.put("Tax", ProdTax);
        values.put("Price", PriceProd);
        values.put("Exist", 0);
        values.put("Export", 0);

        Long Result = db.insert("Prods", "id", values);
        Toast.makeText(this, "Se dio de alta el producto con ID: " + Result, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getApplicationContext(),ListProdsActivity.class);
        startActivity(intent);
        //link.close();
    }
}
