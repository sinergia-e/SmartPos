package intem.smartpos.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.Image;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telecom.Call;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
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

import java.util.ArrayList;

import intem.smartpos.R;
import intem.smartpos.adapters.providersAdapter;
import intem.smartpos.constructors.providersConstructor;
import intem.smartpos.database.connection;

public class ProvidersActivity extends AppCompatActivity {

    private ProgressDialog dialog;
    private String ServerIp,Term,DataSize;
    private double NumRegs;
    private ImageButton SyncProviders,FindProvider;
    private EditText InputFindProvider;
    private ListView listProviders;
    private ArrayList<providersConstructor> ArrayProviders;
    private JsonObjectRequest jsonObjectRequest;
    providersConstructor ProviderRow;
    connection link;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_providers);
        link = new connection(getApplicationContext(), "MyBusinessAndroid", null, 1);
        SQLiteDatabase dbR = link.getReadableDatabase();

        dialog = new ProgressDialog(this);
        dialog.setMessage("Copiando proveedores...");
        dialog.setTitle("Progreso");
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setCancelable(false);


        String condicion = " where ServerIp is not null ";
        Cursor cConfig = dbR.rawQuery("SELECT ServerIP,OnlyExist,DataSize,Seller,Terminal from AppConfig " + condicion+ " ",null);
        if(cConfig.moveToFirst()){
            ServerIp = cConfig.getString(0);
            DataSize = cConfig.getString(2);
            Term = cConfig.getString(4);
        }else{
            Toast.makeText(this, "Falta la configuracion del servidor, corrigelo", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(),AppConfigActivity.class);
            startActivity(intent);
        }

        SyncProviders = (ImageButton) findViewById(R.id.Button_ProvidersSync);
        FindProvider = (ImageButton) findViewById(R.id.imageButton_Providers_Find);
        InputFindProvider = (EditText) findViewById(R.id.editText_Providers_FindProvider);
        listProviders = (ListView) findViewById(R.id.ListView_Providers);

        SyncProviders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(ProvidersActivity.this, "Sincroniza", Toast.LENGTH_SHORT).show();
                ConfirmRecovery();
            }
        });


        FindProvider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String param = InputFindProvider.getText().toString();
                String cCondicion = " where ProviderName like '%"+param+"%' or ProviderCode = '%"+ param +"%' ";
                FillProviders(cCondicion);
            }
        });

        FillProviders("");

    }



    private void FillProviders(String cCondicion){

        SQLiteDatabase db = link.getReadableDatabase();
        Cursor cProv = db.rawQuery("Select * from Providers " + cCondicion, null);
        ArrayProviders = new ArrayList<providersConstructor>();
        Integer count = 0;

        if(cProv.moveToFirst()){
            do {
                ProviderRow = new providersConstructor(cProv.getInt(0), cProv.getString(1), cProv.getString(2));
                ArrayProviders.add(ProviderRow);
                count++;
            }while(cProv.moveToNext());
        }

        TextView contador = (TextView) findViewById(R.id.textView_Providers_count);
        contador.setText(count + " proveedores registrados");

        providersAdapter adapter = new providersAdapter(getApplicationContext(),ArrayProviders);
        listProviders.setAdapter(adapter);
        link.close();
    }










    private void ConfirmRecovery(){
        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
        dialogo1.setTitle("Importante");
        dialogo1.setMessage("Este es un proceso pesado, puede llevar incluso minutos, no debes interrumpir apagar el equipo ni cortar las conexiones ¿Empezamos?");
        dialogo1.setCancelable(false);
        dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {

            }
        });
        dialogo1.setPositiveButton("Empieza", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
                getCountProviders();
            }
        });
        dialogo1.show();
    }






    private void getCountProviders(){

        //========================= Obtener numero de registros =====================================
        String UrlGetCount = "http://"+ServerIp+"/INTEMWS/GetProviders.php?Term="+Term + "&count=true" ;
        JsonObjectRequest RequestCount = new JsonObjectRequest(Request.Method.GET, UrlGetCount, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                SQLiteDatabase dbW = link.getWritableDatabase();
                dbW.execSQL("DELETE from Providers ");

                JSONArray json = response.optJSONArray("count");
                JSONObject RowQty = null;
                try {
                    RowQty = json.getJSONObject(0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                NumRegs = RowQty.optDouble("Qty");
                double RoundDiv = Math.ceil(NumRegs/300);
                int loop = (int) Math.round(RoundDiv);
                new ProvidersActivity.ImportProviders().execute(loop);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ProvidersActivity.this, "No podemos realizar la sincronizacion, revisa las conexiones por favor", Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue CountProviders = Volley.newRequestQueue(getApplicationContext());
        CountProviders.add(RequestCount);
        //========================= Obtener numero de registros =====================================
    }











    private class ImportProviders extends AsyncTask<Integer,Float,Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setProgress(0);
            dialog.setMax(100);
            dialog.show();
        }

        @Override
        protected Void doInBackground(Integer... loops) {
            for(int loop = 0;loop < loops[0]; loop++){
                Float Prog = Float.valueOf( loop * (100/loops[0]));
                publishProgress(Prog);
                //======================================================================================
                String URL = "http://"+ServerIp+"/INTEMWS/GetProviders.php?Term="+Term+"&DataSize="+DataSize;
                jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONArray json = response.optJSONArray("providers");
                        SQLiteDatabase dbW = link.getWritableDatabase();

                        try {
                            for(int i=0;i< json.length(); i++){
                                JSONObject jsonObject = null;
                                jsonObject = json.getJSONObject(i);
                                String providerCode = jsonObject.optString("proveedor");
                                String providerName = jsonObject.optString("nombre");
                                dbW.execSQL("INSERT INTO Providers (ProviderCode,ProviderName) values ('"+providerCode+"','"+providerName+"') ");
                            }
                        }catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
                RequestQueue request = Volley.newRequestQueue(getApplicationContext());
                request.add(jsonObjectRequest);
                //======================================================================================
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Float... values) {
            int NewProg = Math.round(values[0]);
            dialog.setProgress(Integer.parseInt(String.valueOf(NewProg)));
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            dialog.dismiss();
            FillProviders("");
        }

    }


















}
