package intem.smartpos.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.renderscript.ScriptGroup;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

import intem.smartpos.R;
import intem.smartpos.adapters.purchasesAdapter;
import intem.smartpos.constructors.purchasesConstructor;
import intem.smartpos.database.connection;

public class PurchasesListActivity extends AppCompatActivity {

    private ImageButton SyncPurchases,FindPurchase;
    private EditText InputFindPurchase;
    private ListView listPurchases;
    private ProgressDialog dialog;
    private String IdPurchase,ServerIp;
    ArrayList<purchasesConstructor> ArrayPurchase;
    purchasesConstructor RowPurchases;
    connection link;

    private void BindUI(){
        SyncPurchases = (ImageButton) findViewById(R.id.imageButton_sync_purchases);
        FindPurchase = (ImageButton) findViewById(R.id.imageButton_find_purchase);
        InputFindPurchase = (EditText) findViewById(R.id.editText_listpurchases_find_purchase);
        listPurchases = (ListView) findViewById(R.id.ListView_Purchases);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchases_list);


        dialog = new ProgressDialog(this);
        dialog.setMessage("Estamos sincronizando compras...");
        dialog.setTitle("Progreso");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(false);


        //****************** Recupera Datos del server ***************************
        SQLiteDatabase dbR = link.getReadableDatabase();
        Cursor cIp = dbR.rawQuery("SELECT ServerIP from AppConfig ",null);
        if(cIp.moveToFirst()){
            ServerIp = cIp.getString(0);
        }
        //****************** Recupera Datos del server ***************************


        link = new connection(getApplicationContext(), "MyBusinessAndroid", null, 1);
        BindUI();


        FindPurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String param = InputFindPurchase.getText().toString();
                String cCondicion = " where Document like '%"+ param +"%' ";
                FillPurchases(cCondicion);
            }
        });

        String CondicionPurchases = "";

        Bundle orderData = getIntent().getExtras();
        if (orderData != null) {
            String PurchaseToShow = orderData.getString("PurchaseID");
            CondicionPurchases = " where id = '"+PurchaseToShow+"' ";
        }

        FillPurchases(CondicionPurchases);

    }
















    private void FillPurchases(String cCondicion){

        BindUI();

        SQLiteDatabase db = link.getReadableDatabase();
        Cursor c = db.rawQuery("Select * from Purchases " + cCondicion, null);
        ArrayPurchase = new ArrayList<purchasesConstructor>();
        if(c.moveToFirst()){
            do{
                RowPurchases = new purchasesConstructor(c.getInt(0),c.getString(1),c.getFloat(2),c.getString(3),c.getString(4),c.getString(5),c.getInt(6));
                ArrayPurchase.add(RowPurchases);
            }while(c.moveToNext());
        }

        purchasesAdapter adapter = new purchasesAdapter(getApplicationContext(),ArrayPurchase);
        listPurchases.setAdapter(adapter);

    }










    private void QuestionSync(){
        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(PurchasesListActivity.this);
        dialogo1.setTitle("Importante");
        dialogo1.setMessage("Se va a sincronizar con el servidor, puede tomar tiempo depende de cuantos compras se tengan que enviar ¿Comenzamos?");
        dialogo1.setCancelable(false);
        dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {

            }
        });
        dialogo1.setPositiveButton("Adelante", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
                StarSync();
            }
        });
        dialogo1.show();
    }












    private void StarSync(){
        dialog.show();
        SQLiteDatabase dbR = link.getReadableDatabase();
        Cursor cursor = dbR.rawQuery("SELECT id from Purchases where Status = 'Por Enviar' Limit 1 ",null);

        if (cursor.moveToFirst()){
            IdPurchase = String.valueOf(cursor.getInt(0));
            //new PurchasesListActivity.SendToServer().execute();
        }

        link.close();
    }

















/*
    private class SendToServer extends AsyncTask<Void,Float,Void> {
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected void onProgressUpdate(Float... values) {
            super.onProgressUpdate(values);
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected Void doInBackground(Void... voids) {

            //************************ Envia Orden ******************************************************
            SQLiteDatabase dbR = link.getReadableDatabase();
            Cursor cPurchase = dbR.rawQuery("SELECT id,Client,Date,Amount,Location,Status,IdInServer,ConsecInServer,Export FROM Orders WHERE id = '"+IdOrder+"' ",null);
            String UrlSendOrder = "";
            if(cPurchase.moveToFirst()){
                UrlSendOrder = "http://"+ServerIp+"/INTEMWS/SaveOrders.php?IdOrder="+cOrder.getInt(0)+"&Client="+cOrder.getString(1)+"&Amount="+cOrder.getString(3)+"&DateOrder="+cOrder.getString(2);
            }

            JsonObjectRequest SendOrder = new JsonObjectRequest(Request.Method.GET, UrlSendOrder, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    JSONArray json = response.optJSONArray("order");
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = json.getJSONObject(0);
                        String IdInServer = jsonObject.optString("IdServer");
                        String ConsecInServer = jsonObject.optString("ConsecPed");
                        SQLiteDatabase dbW = link.getWritableDatabase();
                        dbW.execSQL("UPDATE Orders set IdInServer = '"+IdInServer+"',ConsecInServer = '"+ConsecInServer+"' where id = '" + IdOrder + "' ");
                        //******************************  Envia Partidas *******************************************
                        SQLiteDatabase dbR = link.getReadableDatabase();
                        Cursor OrderParts = dbR.rawQuery("SELECT id,OrderId,Quantity,ProdId,Price,Descrip,Export,Discount FROM PartOrders where OrderId = '"+IdOrder+"' ",null);
                        if(OrderParts.moveToFirst()){
                            do{
                                String UrlSendPart = "http://"+ServerIp+"/INTEMWS/SaveOrders.php?" +
                                        "IdOrderInServer="+IdInServer+"&" +
                                        "CodeProd="+OrderParts.getString(3)+"&" +
                                        "Quantity="+OrderParts.getString(2)+"&" +
                                        "PriceProd="+OrderParts.getString(4)+"&" +
                                        "Descrip=" + OrderParts.getString(5) + "&" +
                                        "Discount=" + OrderParts.getString(7);

                                JsonObjectRequest SendPart = new JsonObjectRequest(Request.Method.GET, UrlSendPart, null, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {

                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {

                                    }
                                });
                                RequestQueue QueuePart = Volley.newRequestQueue(getApplicationContext());
                                QueuePart.add(SendPart);
                                try {
                                    Thread.sleep(350);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }while (OrderParts.moveToNext());
                        }
                        //******************************  Envia Partidas *******************************************

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    UpdateOrder("Pendiente en Servidor");
                    StarSync();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    UpdateOrder("Reenviar al Servidor");
                }
            });
            RequestQueue QueueOrder = Volley.newRequestQueue(getApplicationContext());
            QueueOrder.add(SendOrder);
            link.close();
            return null;
        }
    }

*/











}
