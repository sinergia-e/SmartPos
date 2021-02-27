package intem.smartpos.activities;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog.Builder;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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
import intem.smartpos.adapters.clientsAdapter;
import intem.smartpos.constructors.clientsConstructor;
import intem.smartpos.database.connection;

import static intem.smartpos.R.layout.view_new_clients;

public class ClientsActivity extends AppCompatActivity {

    private ProgressDialog dialog;
    private String ServerIp,Seller,Term;
    private Integer DataSize,OnlyUpdatesCondicion,DeleteOldData;
    private JsonObjectRequest jsonObjectRequest;
    private double NumRegs;
    private ListView lV_ListOfClients;
    private TextView tV_NumClients;
    private EditText eT_Clients_FindClient;
    private ImageButton iB_Clients_FindClient;
    private ImageButton imageButton_Clients_SyncClients;
    private FloatingActionButton FAB_Clients_NwClient;
    private ArrayList<clientsConstructor> ClientsArray;

    private String ClientsCondicion;
    connection link;

    clientsConstructor ClientItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clients);

        dialog = new ProgressDialog(this);
        dialog.setMessage("Copiando los clientes...");
        dialog.setTitle("Progreso");
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setCancelable(false);

        link = new connection(getApplicationContext(), "MyBusinessAndroid", null, 1);
        SQLiteDatabase dbR = link.getReadableDatabase();

        Cursor cConfig = dbR.rawQuery("SELECT ServerIP,OnlyExist,DataSize,Seller,Terminal,OnlyUpdateClients,DeleteOldData from AppConfig ",null);
        if(cConfig.moveToFirst()){
            ServerIp = cConfig.getString(0);
            Term = cConfig.getString(4);
            DataSize = cConfig.getInt(2);
            OnlyUpdatesCondicion = cConfig.getInt(5);
            DeleteOldData = cConfig.getInt(6);

            if (cConfig.getString(3).isEmpty()){
                Seller = "";
            }else{
                Seller = "&Vend="+cConfig.getString(3);
            }





        }else{
            Toast.makeText(this, "Falta la configuracion del servidor, corrigelo", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(),AppConfigActivity.class);
            startActivity(intent);
        }

        ClientsCondicion = "";

        link = new connection(getApplicationContext(), "MyBusinessAndroid", null, 1);
        lV_ListOfClients = (ListView) findViewById(R.id.lV_Clients_ListClients);
        registerForContextMenu(lV_ListOfClients);

        tV_NumClients = (TextView) findViewById(R.id.tV_ClientsList_NumClients);
        eT_Clients_FindClient = (EditText) findViewById(R.id.eT_ClientsList_FindClient);
        iB_Clients_FindClient = (ImageButton) findViewById(R.id.iB_ClientsList_FindButton);
        imageButton_Clients_SyncClients = (ImageButton) findViewById(R.id.imageButton_Clients_SyncClients);

        imageButton_Clients_SyncClients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConfirmRecovery();
            }
        });

        FAB_Clients_NwClient = (FloatingActionButton) findViewById(R.id.Fab_Clients_NewClient);
        FAB_Clients_NwClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewNewClients();
            }
        });

        iB_Clients_FindClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String client = eT_Clients_FindClient.getText().toString();
                ClientsCondicion = " where Name like '%"+client+"%' or CodeMyBusiness like '%"+client+"%' ";
                FillClients(ClientsCondicion);
            }
        });

        FillClients(ClientsCondicion);
        link.close();
    }




    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu_clients,menu);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        String Status = null;

        switch (item.getItemId()){

            case R.id.OpenClient:
                //Intent goDetails = new Intent(ListProdsActivity.this,ProdsDetailActivity.class);
                //goDetails.putExtra("ProdCode",String.valueOf(ProdsArray.get(info.position).getId()));
                //startActivity(goDetails);
                return true;

            case R.id.ViewCredit:
                String CodeCl = String.valueOf(ClientsArray.get(info.position).getCodeMyBusiness());
                Intent goPayments = new Intent(ClientsActivity.this,PaymentsActivity.class);
                goPayments.putExtra("client",CodeCl);
                startActivity(goPayments);
                return true;

            case R.id.NewPay:
                String CodeClient = String.valueOf(ClientsArray.get(info.position).getCodeMyBusiness());
                Intent goCredit = new Intent(ClientsActivity.this,CreditActivity.class);
                goCredit.putExtra("client",CodeClient);
                startActivity(goCredit);
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }








    @Override
    public void onBackPressed() {
        Intent goMenu = new Intent(getApplicationContext(),MenuActivity.class);
        startActivity(goMenu);
    }









    private void FillClients(String ClientsCondicion) {

        SQLiteDatabase db = link.getReadableDatabase();
        Cursor c = db.rawQuery("Select * from Clients " + ClientsCondicion, null);
        ClientsArray = new ArrayList<clientsConstructor>();

        if (c.moveToFirst()) {
            do {
                ClientItem = new clientsConstructor(c.getInt(0),c.getString(1),c.getString(2),c.getInt(3),c.getString(4),c.getString(5),c.getString(6),c.getString(7),c.getString(8),c.getString(9),c.getDouble(10));
                ClientsArray.add(ClientItem);
            } while (c.moveToNext());
        }

        tV_NumClients.setText("Mostrando " + ClientsArray.size() + " cliente(s)");

        clientsAdapter adapt = new clientsAdapter(getApplicationContext(),ClientsArray);
        lV_ListOfClients.setAdapter(adapt);
        link.close();
    }










    private void viewNewClients(){

        Builder builder = new Builder(this);
        final View newClient = LayoutInflater.from(getApplicationContext()).inflate(view_new_clients,null);
        builder.setView(newClient);

        builder.setPositiveButton("Registra el cliente", new DialogInterface.OnClickListener() {

            final EditText codeNwClient = newClient.findViewById(R.id.eT_NewClient_CodeClient);
            final EditText nameNwClient = newClient.findViewById(R.id.eT_NewClient_Name);
            final EditText StreetNwClient = newClient.findViewById(R.id.eT_NewClient_Street);
            final EditText ColNwClient = newClient.findViewById(R.id.eT_NewClient_Col);
            final EditText CityNwClient = newClient.findViewById(R.id.eT_NewClient_City);
            final EditText CpNwClient = newClient.findViewById(R.id.eT_NewClient_CP);
            final EditText RfcNwClient = newClient.findViewById(R.id.eT_NewClient_RFC);
            final EditText EmailNwClient = newClient.findViewById(R.id.eT_NewClient_Email);

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                String CodeName = codeNwClient.getText().toString();
                String ClientName = nameNwClient.getText().toString();
                String ClientStreet = StreetNwClient.getText().toString();
                String ClientColony = ColNwClient.getText().toString();
                String ClientCity = CityNwClient.getText().toString();
                String ClientCP = CpNwClient.getText().toString();
                String ClientRFC = RfcNwClient.getText().toString();
                String ClientEmail = EmailNwClient.getText().toString();

                InsertClient(CodeName,ClientName,ClientStreet,ClientColony,ClientCity,ClientCP,ClientRFC,ClientEmail);
            }
        });

        AlertDialog newClientDialog = builder.create();
        newClientDialog.show();

    }


    private void InsertClient(String CodeCl,String NameCl,String StreetCl,String ColonyCl,String CityCl,String CpCl,String RfcCl,String Email){

        link = new connection(this, "MyBusinessAndroid", null, 1);
        SQLiteDatabase db = link.getWritableDatabase();

        //id INTEGER PRIMARY KEY AUTOINCREMENT,Code TEXT,Description TEXT,ProdCategory TEXT,Brand TEXT,Tax TEXT,Cost REAL,Price1 REAL,Price2 REAL,Price3 REAL,Price4 REAL,Price5 REAL,Price6 REAL,Price7 REAL,Price8 REAL,Price9 REAL,Qty2 INTEGER,Qty3 INTEGER,Qty4 INTEGER,Qty5 INTEGER,Qty6 INTEGER,Qty7 INTEGER,Qty8 INTEGER,Qty9 INTEGER,Exist INTEGER,ExSucursal TEXT,Export INTEGER,Offer Real,image TEXT
        ContentValues valuesClient = new ContentValues();
        valuesClient.put("CodeMyBusiness",CodeCl);
        valuesClient.put("Name", NameCl);
        valuesClient.put("Street", StreetCl);
        valuesClient.put("Colony", ColonyCl);
        valuesClient.put("City", CityCl);
        valuesClient.put("CP", CpCl);
        valuesClient.put("RFC", RfcCl);
        valuesClient.put("Email", Email);
        valuesClient.put("Export",1);

        Long idResult = db.insert("Clients", "id", valuesClient);
        FillClients("");
        link.close();
    }












    private void SendClient(){
        //========================= Obtener numero de registros =====================================
        String UrlGetCount = "http://"+ServerIp+"/INTEMWS/GetClients.php?count=true&Term="+Term+Seller;
        JsonObjectRequest RequestCount = new JsonObjectRequest(Request.Method.GET, UrlGetCount, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                SQLiteDatabase dbW = link.getWritableDatabase();
                dbW.execSQL("DELETE from Clients where Export = 1 ");
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
                new ImportClients().execute(loop );
                Toast.makeText(ClientsActivity.this, " loops " + loop, Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ClientsActivity.this, "Existe un error en la conexion", Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue CountClients = Volley.newRequestQueue(getApplicationContext());
        CountClients.add(RequestCount);
        //========================= Obtener numero de registros =====================================
    }





























    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_users, menu);
        return super.onCreateOptionsMenu(menu);
    }











    // ====================================================================================================
    // ====================================================================================================
    //***************    *******************  Sincroniza clientes  *******************  *******************
    // ====================================================================================================
    // ====================================================================================================

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
                getCountClients();
            }
        });
        dialogo1.show();
    }








    private void getCountClients(){
        //========================= Obtener numero de registros =====================================
        String  ValueCondicion = "";
        if (OnlyUpdatesCondicion == 0){
            ValueCondicion = "";
        }else{
            ValueCondicion = "&onlyUpdate=true";
        }

        final String UrlGetCount = "http://"+ServerIp+"/INTEMWS/GetClients.php?count=true&Term="+Term+Seller+ValueCondicion;
        JsonObjectRequest RequestCount = new JsonObjectRequest(Request.Method.GET, UrlGetCount, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                SQLiteDatabase dbW = link.getWritableDatabase();

                if(DeleteOldData==1){
                    dbW.execSQL("DELETE from Clients where Export = 1 ");
                }

                JSONArray json = response.optJSONArray("count");
                JSONObject RowQty = null;
                try {
                    RowQty = json.getJSONObject(0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                NumRegs = RowQty.optDouble("Qty");
                double RoundDiv = Math.ceil(NumRegs/DataSize);
                int loop = (int) Math.round(RoundDiv);
                new ImportClients().execute(loop );
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ClientsActivity.this, "Existe un error en la conexion", Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue CountClients = Volley.newRequestQueue(getApplicationContext());
        CountClients.add(RequestCount);
        //========================= Obtener numero de registros =====================================
    }























    private class ImportClients extends AsyncTask<Integer,Float,Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setProgress(0);
            dialog.setMax(100);
            dialog.show();
        }

        @Override
        protected Void doInBackground(Integer... loops) {

            String  ValueCondicion = "";
            if (OnlyUpdatesCondicion == 0){
                ValueCondicion = "";
            }else{
                ValueCondicion = "&onlyUpdate=true";
            }

            for(int loop = 0; loop < loops[0]; loop++){
                Float Prog = Float.valueOf( loop * (100/loops[0]));
                publishProgress(Prog);
                //======================================================================================
                final String URL = "http://"+ServerIp+"/INTEMWS/GetClients.php?Size="+DataSize+"&Term="+Term+Seller+ValueCondicion;
                jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONArray json = response.optJSONArray("clients");
                        SQLiteDatabase dbW = link.getWritableDatabase();

                        try {
                            for(int i=0;i< json.length(); i++){
                                JSONObject jsonObject = null;
                                jsonObject = json.getJSONObject(i);

                                String CodeClient = jsonObject.optString("Cliente");
                                String NameCl = jsonObject.optString("nombre");
                                String Colony = jsonObject.optString("colonia" + "");
                                String Street = jsonObject.optString("calle" + "");
                                String CityCl = jsonObject.optString("ciudad" + "");
                                String CPCl = jsonObject.optString("CP" + "");
                                String RFC = jsonObject.optString("RFC" + "");
                                String Email = jsonObject.optString("correo" + "");
                                String DebtCl = jsonObject.optString("saldo" + "");

                                if (OnlyUpdatesCondicion == 1){
                                    dbW.execSQL("DELETE from Clients where CodeMyBusiness = '"+CodeClient+"' ");
                                }

                                dbW.execSQL("INSERT INTO Clients (CodeMyBusiness,Name,Export,Street,Colony,City,CP,RFC,Email,Debt) values ('"+CodeClient+"','"+NameCl+"',1,'"+Street+"','"+Colony+"','"+CityCl+"','"+CPCl+"','"+RFC+"','"+Email+"','"+DebtCl+"') ");

                            }
                        }catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //Toast.makeText(ClientsActivity.this, "" + URL, Toast.LENGTH_SHORT).show();
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
                    Thread.sleep(500);
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
            FillClients("");
            dialog.dismiss();
        }

    }












































}
