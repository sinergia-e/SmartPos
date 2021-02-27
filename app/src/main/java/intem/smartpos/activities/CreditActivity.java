package intem.smartpos.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import intem.smartpos.R;
import intem.smartpos.database.connection;

public class CreditActivity extends AppCompatActivity {

    private TextView textView_credit_Client;
    private TextView textView_credit_saldo;
    private EditText editText_credit_amount;
    private Button button_credit_confirm;
    private String client;
    private String ServerIp;
    private Double Debt;

    private JsonObjectRequest SendPayment;
    connection link;

    private void BindUI(){

        textView_credit_Client = (TextView) findViewById(R.id.TextView_Credit_Client);
        textView_credit_saldo = (TextView) findViewById(R.id.TextView_Credit_Saldo);
        editText_credit_amount = (EditText) findViewById(R.id.editText_Credit_Amount);
        button_credit_confirm = (Button) findViewById(R.id.button_credit_pay);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit);

        link = new connection(getApplicationContext(), "MyBusinessAndroid", null, 1);
        SQLiteDatabase dbR = link.getReadableDatabase();
        BindUI();

        Cursor cConfig = dbR.rawQuery("SELECT ServerIP,OnlyExist,DataSize,Terminal,SendProds,ImportSucEx from AppConfig ",null);
        if(cConfig.moveToFirst()){
            ServerIp = cConfig.getString(0);
        }else{
            Toast.makeText(this, "Falta la configuracion del servidor, corrigelo", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this,AppConfigActivity.class);
            startActivity(intent);
        }


        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            client = bundle.getString("client");
        }


        DecimalFormat formateador = new DecimalFormat("###,###,###,###.00");

        Cursor ClientData = dbR.rawQuery("select Name,Debt from Clients where CodeMyBusiness = '" + client  + "' ",null);
        if(ClientData.moveToFirst()){

            String Name = ClientData.getString(0);
            Debt = ClientData.getDouble(1);
            String AmountDebt = String.valueOf(formateador.format(Debt));

            textView_credit_Client.setText(Name);
            textView_credit_saldo.setText("$ " + AmountDebt);
        }

        button_credit_confirm.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                String amount = editText_credit_amount.getText().toString();
                double imp = Double.parseDouble(amount);

                if(imp > Debt){
                    Toast.makeText(CreditActivity.this, "Estas tratando de abonar mas de lo que debe el cliente", Toast.LENGTH_SHORT).show();
                }else{
                    if(imp <= 0){
                        Toast.makeText(CreditActivity.this, "El valor del importe debe ser mayor a 0", Toast.LENGTH_SHORT).show();
                    }else{
                        InsertPayment(amount);
                    }
                }


            }
        });

    }







    @Override
    public void onBackPressed() {
        Intent goMenu = new Intent(this,MenuActivity.class);
        startActivity(goMenu);
    }





    @RequiresApi(api = Build.VERSION_CODES.N)
    private void InsertPayment(String amount){

        link = new connection(this, "MyBusinessAndroid", null, 1);
        SQLiteDatabase db = link.getWritableDatabase();

        //id INTEGER PRIMARY KEY AUTOINCREMENT,Client TEXT,amount REAL,date TEXT,Location TEXT,Status TEXT,Aplicated TEXT,User TEXT


        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Date date = new Date();
        String dateToInsert = dateFormat.format(date);

        ContentValues valuesCredit = new ContentValues();
        valuesCredit.put("Client",client);
        valuesCredit.put("amount",amount);
        valuesCredit.put("date",dateToInsert);
        valuesCredit.put("Location","");
        valuesCredit.put("Status","Recibido");
        valuesCredit.put("Aplicated",0);

        double debt = Double.parseDouble(amount);

        Long idResult = db.insert("Credit", "id", valuesCredit);
        db.execSQL("update Clients set Debt = Debt - "+debt+"  where CodeMyBusiness = '"+client+"' ");


        printDebt(String.valueOf(idResult));
        sendPayment(client,amount, String.valueOf(idResult));
        link.close();

    }





    private void sendPayment(final String ClCode, String amount, final String idAndroid){
        //========================= Obtener numero de registros =====================================
        String UrlSendPayment = "http://"+ ServerIp +"/INTEMWS/GetCredit.php?client="+ClCode+"&amount="+amount+"&id="+idAndroid;
        SendPayment = new JsonObjectRequest(Request.Method.GET, UrlSendPayment, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                SQLiteDatabase dbW = link.getWritableDatabase();

                JSONArray json = response.optJSONArray("result");
                JSONObject result = null;
                try {
                    result = json.getJSONObject(0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String Aplicated = result.optString("docs");
                dbW.execSQL("update Credit set Aplicated = '"+Aplicated+"',Status='Exportado' where id = " + idAndroid);

                Intent goPayments = new Intent(CreditActivity.this,PaymentsActivity.class);
                goPayments.putExtra("client",ClCode);
                startActivity(goPayments);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Intent goPayments = new Intent(CreditActivity.this,PaymentsActivity.class);
                goPayments.putExtra("client",ClCode);
                startActivity(goPayments);
                Toast.makeText(CreditActivity.this, "No se pudo enviar al servidor", Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue PaymentToServer = Volley.newRequestQueue(getApplicationContext());
        PaymentToServer.add(SendPayment);

    }








    @RequiresApi(api = Build.VERSION_CODES.N)
    private void printDebt(String IdDebt){
        SQLiteDatabase dbR = link.getReadableDatabase();
        Cursor cDebt = dbR.rawQuery("SELECT Credit.id,clients.name,Credit.Amount,Credit.Date,Credit.Aplicated FROM Credit INNER JOIN Clients ON Credit.Client = Clients.CodeMyBusiness where Credit.id = '"+IdDebt+"' " ,null);

        int Id = 0;
        String Name = null;
        Double Amount = null;
        String Date = null;
        String Aplicated = null;

        if(cDebt.moveToFirst()){
            do {
                Id = cDebt.getInt(0);
                Name = cDebt.getString(1);
                Amount = cDebt.getDouble(2);
                Date = cDebt.getString(3);
                Aplicated = cDebt.getString(4);
            }while (cDebt.moveToNext());
        }

        String PrintCont = null;

        PrintCont = "$bigh$Comercializadora DLC $big$\nDomicilio Conocido S/N Tlacocuspan\nTlatlaya Mexico\n\nTelefono:7161615127\n7221417374\nFecha:"+Date+"\n\nAbono de Credito \nCliente: \n" + Name + "\nFolio: " + Id + "\n \n\n"
                + "Detalles:  \n\n=============================\n$small$";
        PrintCont = PrintCont + Aplicated;

        PrintCont = PrintCont + "============================= \n\n$bigh$Importe de su pago :  " + Amount;
        PrintCont = PrintCont + " \n\n\n $intro$ ";

        String dataToPrint = PrintCont; //"$big$Pedido Numero:$intro$posprinterdriver.com$intro$$intro$$cut$$intro$";

        Intent intentPrint = new Intent();

        intentPrint.setAction(Intent.ACTION_SEND);
        intentPrint.putExtra(Intent.EXTRA_TEXT, dataToPrint);
        intentPrint.setType("text/plain");

        startActivity(intentPrint);

        link.close();
    }




}
