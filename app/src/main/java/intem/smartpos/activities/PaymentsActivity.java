package intem.smartpos.activities;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

import intem.smartpos.R;
import intem.smartpos.database.connection;

public class PaymentsActivity extends AppCompatActivity {

    private TextView nameClient;
    private TextView saldoClient;
    private ImageButton Sync;
    private ImageButton Delete;

    connection link;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payments);

        link = new connection(getApplicationContext(), "MyBusinessAndroid", null, 1);
        SQLiteDatabase dbR = link.getReadableDatabase();

        nameClient = (TextView) findViewById(R.id.TextView_Payments_Client);
        saldoClient = (TextView) findViewById(R.id.TextView_Payments_saldo);

        Bundle client = getIntent().getExtras();
        if (client != null) {
            String ClientID = client.getString("client");


            FillPayments(ClientID);
            FillDataClient(ClientID);

        }



//        Sync.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                SyncPayments();
//            }
//        });


/*
        Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeletePayments();
            }
        });
*/
    }



    @Override
    public void onBackPressed() {
        Intent goMenu = new Intent(this,MenuActivity.class);
        startActivity(goMenu);
    }




    private void FillDataClient(String client){

        SQLiteDatabase db = link.getReadableDatabase();
        Cursor c = db.rawQuery("Select * from Clients where CodeMyBusiness = '"+client+"' " , null);
        DecimalFormat formateador = new DecimalFormat("###,###,###,###.00");

        if(c.moveToFirst()){
            String Name = c.getString(2);
            Double Saldo = c.getDouble(10);
            String amount = formateador.format(Saldo);
            nameClient.setText(Name);
            saldoClient.setText("Saldo: $ " + amount );
        }

    }




    /***   Recupera los productos y los pone en el list View ***/
    /***   ====================================================================== ***/
    private void FillPayments(String client) {
        SQLiteDatabase db = link.getReadableDatabase();
        Cursor c = db.rawQuery("Select id,amount,date from Credit where client = '"+client+"' " , null);
        DecimalFormat formateador = new DecimalFormat("###,###,###,###.00");

        ListView ListPayments = (ListView) findViewById(R.id.ListPayments);
        ArrayList<String> Payments = new ArrayList<>();

        while(c.moveToNext()){
            String Fecha = c.getString(2);
            Double Amount = c.getDouble(1);
            String F_amount = formateador.format(Amount);
            Payments.add("$" + F_amount + " Del dia: " + Fecha );
        }

        ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,Payments);
        ListPayments.setAdapter(adapter);

    }


    private void DeletePayments(){

    }


    private void SyncPayments(){

    }


}
