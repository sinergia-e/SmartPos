package intem.smartpos.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

import java.util.ArrayList;

import intem.smartpos.R;
import intem.smartpos.constructors.orderConstructor;
import intem.smartpos.database.connection;

public class SendOrderActivity extends AppCompatActivity {

    private ProgressDialog dialog;
    private int OrderInServer;
    private int ConsecInServer;
    private String IdInAndroid;
    private String ServerIp;
    private Integer Factor;
    private JsonObjectRequest SendOrder;
    private JsonObjectRequest SendPart;
    private JsonObjectRequest UpdateOrders;
    private Button button_SendOrders_Send;
    private Button button_SendOrders_RecoveryStatus;
    private Button button_SendOrders_DeleteClose;
    private ArrayList<orderConstructor> PedsToSend;
    connection link;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_order);

        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(false);

        link = new connection(getApplicationContext(), "MyBusinessAndroid", null, 1);
        SQLiteDatabase dbR = link.getReadableDatabase();
        Cursor cIp = dbR.rawQuery("SELECT ServerIP from AppConfig ",null);
        if(cIp.moveToFirst()){
            ServerIp = cIp.getString(0);
        }
        link.close();

        button_SendOrders_Send = (Button) findViewById(R.id.button_SendOrders_Send);
        button_SendOrders_Send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConfirmSend();
            }
        });



        button_SendOrders_RecoveryStatus = (Button) findViewById(R.id.button_SendOrders_RecoveryStatus);
        button_SendOrders_RecoveryStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConfirmUpdateStatus();
            }
        });



        button_SendOrders_DeleteClose = (Button) findViewById(R.id.button_SendOrders_DeleteClose);
        button_SendOrders_DeleteClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConfirmDelete();
            }
        });

    }








    //====================   Inicia Bloque de Confimaciones ====================================================
    private void ConfirmSend(){
        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
        dialogo1.setTitle("Importante");
        dialogo1.setMessage("Este es un proceso pesado y puede tardar inclusive minutos, no apages el equipo ni cierres la conexion de red ¿comenzamos?");
        dialogo1.setCancelable(false);
        dialogo1.setNegativeButton("Mejor despues", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {

            }
        });

        dialogo1.setPositiveButton("Envialos", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
                SQLiteDatabase dbW = link.getWritableDatabase();
                dbW.execSQL("Update Orders set Status = 'Confirmado' ");
                new SendOrders().execute();
            }
        });
        dialogo1.show();
    }



    private void ConfirmDelete(){
        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
        dialogo1.setTitle("Importante");
        dialogo1.setMessage("Esto va a eliminar de android los pedidos que se hallan cerrado en el servidor ¿Procedemos?");
        dialogo1.setCancelable(false);
        dialogo1.setNegativeButton("Conservalos", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {

            }
        });
        dialogo1.setPositiveButton("Adelante", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
                DeleteCloseOrders();
            }
        });
        dialogo1.show();
    }

    private void ConfirmUpdateStatus(){
        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
        dialogo1.setTitle("Importante");
        dialogo1.setMessage("Mientras se trabaja no apages el equipo ni cierres la conexion de red ¿Actualizamos?");
        dialogo1.setCancelable(false);
        dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {

            }
        });
        dialogo1.setPositiveButton("Actualizalos", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
                UpdateOrders();
            }
        });
        dialogo1.show();

    }

    //====================   Fin Bloque de Confimaciones ====================================================







    //====================   Envia las ordenes al servidor ====================================================
    private class SendOrders extends AsyncTask<Void,Float,Void>{

        @Override
        protected void onPreExecute() {
            dialog.setTitle("Trabajando");
            dialog.setMessage("Estamos enviando los pedidos...");
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            SQLiteDatabase dbR = link.getReadableDatabase();
            Cursor cOrders = dbR.rawQuery("SELECT id,Client,Date,Amount,Location,Status,IdInServer,ConsecInServer,Export FROM Orders WHERE Status = 'Confirmado' ",null);

            if(cOrders.moveToFirst()){
                do{
                    IdInAndroid = String.valueOf(cOrders.getInt(0));
                    String UrlOrder = "http://"+ServerIp+"/INTEMWS/SaveOrders.php?IdOrder="+cOrders.getInt(0)+"&Client="+cOrders.getString(1)+"&Amount="+cOrders.getString(3)+"&DateOrder="+cOrders.getString(2);
                    SendOrder = new JsonObjectRequest(Request.Method.GET, UrlOrder, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            JSONArray json = response.optJSONArray("order");
                            try {
                                JSONObject jsonObject = json.getJSONObject(0);
                                String IdInServer = jsonObject.optString("IdServer");
                                String ConsecInServer = jsonObject.optString("ConsecPed");

                                //=================************* Inicio Envia las partidas *****************==========================
                                SQLiteDatabase dbR = link.getReadableDatabase();
                                Cursor cParts = dbR.rawQuery("SELECT id,OrderId,Quantity,ProdId,Price,Descrip,Export FROM PartOrders where OrderId = '"+IdInAndroid+"' ",null);
                                if(cParts.moveToFirst()){
                                    do{
                                        String UrlSendPart = "http://"+ServerIp+"/INTEMWS/SaveOrders.php?IdOrderInServer="+IdInServer+"&CodeProd="+cParts.getString(3)+"&Quantity="+cParts.getString(2)+"&PriceProd="+cParts.getString(4)+"&Descrip=" + cParts.getString(5);
                                        SendPart = new JsonObjectRequest(Request.Method.GET, UrlSendPart, null, new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                try {
                                                    Thread.sleep(200);
                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                try {
                                                    Thread.sleep(150);
                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        });

                                        RequestQueue Parts = Volley.newRequestQueue(getApplicationContext());
                                        Parts.add(SendPart);
                                    }while (cParts.moveToNext());
                                }
                                //=================************* Fin Envia las partidas *****************==========================

                                //SendOrderParts(IdInServer,IdInAndroid,ConsecInServer);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    });
                    RequestQueue OrderToServer = Volley.newRequestQueue(getApplicationContext());
                    OrderToServer.add(SendOrder);
                    Cursor partsCount = dbR.rawQuery("SELECT id from PartOrders where OrderId = '"+IdInAndroid+"' ",null);
                    Factor = partsCount.getCount();
                    int timeToSleep = Factor * 300;
                    try {
                        Thread.sleep(timeToSleep);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }while (cOrders.moveToNext());
            }


            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            dialog.dismiss();
        }
    }










    private void SendOrderParts(String IdInServ,String IdInApp,String ConsecInServer){



    }


















    //====================   Inicia Actualiza estatus de pedidos ====================================================
    private void UpdateOrders(){
        String urlUp = "http://"+ServerIp+"/INTEMWS/OrdersStatus.php";
        UpdateOrders = new JsonObjectRequest(Request.Method.GET, urlUp, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONArray ordersStatus = response.optJSONArray("status");
                try {
                    for (int i=0;i < ordersStatus.length(); i++){
                        SQLiteDatabase dbW = link.getWritableDatabase();
                        JSONObject jsonObject = ordersStatus.getJSONObject(i);
                        int IdOr = Integer.parseInt(jsonObject.optString("pedido"));
                        String Status = jsonObject.optString("estado");
                        dbW.execSQL("UPDATE Orders set Status='"+Status+"' where IdInServer = "+ IdOr);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Toast.makeText(SendOrderActivity.this, "Se actualizaron los estatus", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(SendOrderActivity.this, "No hay pedidos en el servidor o existe un error en la conexion", Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue UpOrders = Volley.newRequestQueue(this);
        UpOrders.add(UpdateOrders);
        link.close();
    }

    //====================   Finaliza Actualizacion de estatus de pedidos ====================================================




    //====================   Borra ordenes cerradas en server ====================================================

    private void DeleteCloseOrders(){
        SQLiteDatabase dbW = link.getWritableDatabase();
        Cursor cCloseOrders = dbW.rawQuery("SELECT id from Orders where Status = 'Cerrado en Servidor' ",null);

        if(cCloseOrders.moveToFirst()){
            do{
                int OrderDelete = cCloseOrders.getInt(0);
                dbW.execSQL("DELETE from PartOrders where OrderId = '"+OrderDelete+"' ");
                dbW.execSQL("DELETE from Orders where id = '"+OrderDelete+"' ");

            }while (cCloseOrders.moveToNext());
        }
    }

    //====================   Fin Borra ordenes ====================================================



}
