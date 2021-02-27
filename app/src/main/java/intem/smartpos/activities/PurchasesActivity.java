package intem.smartpos.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.renderscript.ScriptGroup;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import intem.smartpos.R;
import intem.smartpos.database.connection;

class purchasesActivity extends AppCompatActivity {

    private EditText InputProvider,DocumentProvider;
    private Button PurchaseParts;
    private ImageButton ShowProviders;
    connection link;

    private void BindUI(){
        InputProvider = (EditText) findViewById(R.id.editText_purchase_FindProvider);
        DocumentProvider = (EditText) findViewById(R.id.editText_purchase_document);
        PurchaseParts = (Button) findViewById(R.id.button_purchase_register);
        ShowProviders = (ImageButton) findViewById(R.id.ImageButton_purchase_FindProvider);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchases);
        link = new connection(getApplicationContext(), "MyBusinessAndroid", null, 1);
        BindUI();

        PurchaseParts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterPurchase();
            }
        });
       }

    //Provider TEXT,Amount REAL,Date TEXT,Document TEXT,Status TEXT,Export INTEGER
    private void RegisterPurchase(){

        BindUI();
        String date = (DateFormat.format("dd-MM-yyyy hh:mm:ss", new java.util.Date()).toString());
        SQLiteDatabase db = link.getReadableDatabase();
        String ProviderCode = InputProvider.getText().toString();
        String DocumentPurchase = DocumentProvider.getText().toString();

        ContentValues values = new ContentValues();
        values.put("Provider", ProviderCode);
        values.put("Date", date);
        values.put("Amount", 0);
        values.put("Document", DocumentPurchase);
        values.put("Status", "Pendiente");
        values.put("Export", 0);
        Long idResult = db.insert("Purchases", "id", values);

        Intent PurchaseParts = new Intent(getApplicationContext(),Purchase_Parts_Activity.class);
        PurchaseParts.putExtra("Purchase","" + idResult);
        startActivity(PurchaseParts);

    }

}
