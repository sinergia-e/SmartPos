package intem.smartpos.activities;

import android.os.AsyncTask;
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

import intem.smartpos.R;

public class for_test extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_for_test);

        Button Test = (Button) findViewById(R.id.Prueba);

        Test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(for_test.this, "Se ejecuto ", Toast.LENGTH_LONG).show();
                //TestShowJson();
                new SyncSale().execute();
            }
        });

    }





    public class SendJson extends AsyncTask<String,Integer,Void>{

        @Override
        protected Void doInBackground(String... strings) {

            JSONObject SaleTest = new JSONObject();

            try {
                JSONArray SaleInfo = new JSONArray();
                SaleInfo.put("Cliente");
                SaleInfo.put("Importe");
                SaleInfo.put("impuesto");

                SaleTest.put("venta",SaleInfo);

            } catch (JSONException e) {
                e.printStackTrace();
            }


            String Url = "http://192.168.0.11:9000/INTEMWS/Test.php";

            JsonObjectRequest SendDataTest = new JsonObjectRequest(Request.Method.POST, Url, SaleTest, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Toast.makeText(getApplicationContext(), "Hubo Respuesta", Toast.LENGTH_SHORT).show();
                    //Toast.makeText(for_test.this, "", Toast.LENGTH_SHORT).show();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), "Nada", Toast.LENGTH_SHORT).show();
                }
            });

            RequestQueue Test = Volley.newRequestQueue(getApplicationContext());
            Test.add(SendDataTest);
            return null;
        }
    }






    private void TestShowJson(){

        JSONObject Venta = new JSONObject();
        JSONObject Partidas = new JSONObject();
        try {
            Venta.put("IDVenta","123456");
            Venta.put("Cliente","Demo");

            Partidas.put("Codigo","ADPT9");
            Partidas.put("Cantidad",10);
            Partidas.put("Precio",100);
            Venta.accumulate("Partidas",Partidas);
            Partidas = new JSONObject();

            Partidas.put("Codigo","SYS");
            Partidas.put("Cantidad",5);
            Partidas.put("Precio",50);
            Venta.accumulate("Partidas",Partidas);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }











    public class SyncSale extends AsyncTask<String,Integer,String>{

        @Override
        protected String doInBackground(String... strings) {

            JSONObject Venta = new JSONObject();
            JSONObject Partidas = new JSONObject();
            try {
                Venta.put("IDVenta","123456");
                Venta.put("Cliente","Demo");

                Partidas.put("Codigo","ADPT9");
                Partidas.put("Cantidad",10);
                Partidas.put("Precio",100);
                Venta.accumulate("Partidas",Partidas);
                Partidas = new JSONObject();

                Partidas.put("Codigo","SYS");
                Partidas.put("Cantidad",5);
                Partidas.put("Precio",50);
                Venta.accumulate("Partidas",Partidas);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String UrlSync = "http://192.168.0.11:9000/INTEMWS/Test.php";
            JsonObjectRequest SyncSale = new JsonObjectRequest(Request.Method.POST, UrlSync, Venta, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            RequestQueue Sync = Volley.newRequestQueue(getApplicationContext());
            Sync.add(SyncSale);
            return null;
        }
    }















}
