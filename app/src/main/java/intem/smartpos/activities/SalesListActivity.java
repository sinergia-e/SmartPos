package intem.smartpos.activities;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
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
import java.util.ArrayList;

import intem.smartpos.R;
import intem.smartpos.adapters.salesListAdapter;
import intem.smartpos.constructors.SalesConstructor;
import intem.smartpos.database.connection;

public class SalesListActivity extends AppCompatActivity {

    private ListView SalesList;
    private TextView ResumeSales;
    private ArrayList StatusList;
    private String StatusSelect;
    private String SaleToSend,IdSaleInServ,ImportSend,ClientSend,LineSelect,NumSale,IdPart,StatusCamera,ServerIp,Terminal,Warehouse,UrlSendSale,UserLoged,ProductToFind;
    private Spinner StatusSp;
    private ImageButton FilterCorte,FilterStatus,SalesSync;
    private FloatingActionButton NewCorte;
    connection link;
    private ArrayList<SalesConstructor> SalesArray;
    SalesConstructor SalesItem;

    private void BindUI(){
        SalesList = (ListView) findViewById(R.id.SalesList_ListView);
        ResumeSales = (TextView) findViewById(R.id.SalesList_SalesResume);
        StatusSp = (Spinner) findViewById(R.id.SalesList_StatusSp);
        FilterStatus = (ImageButton) findViewById(R.id.SalesList_FilterStatus);
        FilterCorte = (ImageButton) findViewById(R.id.SalesList_FilterCorte);
        NewCorte = (FloatingActionButton) findViewById(R.id.SalesList_Fb_NewCorte);
        SalesSync = (ImageButton) findViewById(R.id.SalesLis_SyncSales);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_list);
        link = new connection(getApplicationContext(), "MyBusinessAndroid", null, 1);

        FillSales(" where corte = 0  and Status <> 'PR' and Status <> 'Pendiente' ");

        StatusList = new ArrayList();
        StatusList.add("En servidor");

        //****************** Recupera Datos del server ***************************
        SQLiteDatabase dbR = link.getReadableDatabase();

        Cursor UserLogQuery = dbR.rawQuery("select Nick from Users Where Loged = 1 ",null);
        if(UserLogQuery.moveToFirst()){
            UserLoged = UserLogQuery.getString(0);
        }

        Cursor cIp = dbR.rawQuery("SELECT ServerIP,PriceQty,Terminal,PendingSale,Warehouse from AppConfig ",null);
        if(cIp.moveToFirst()){
            ServerIp = cIp.getString(0);
            Terminal = cIp.getString(2);
            Warehouse = cIp.getString(4);
        }
        //****************** Recupera Datos del server ***************************


        ArrayAdapter<CharSequence> adaptStatus = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, StatusList);
        StatusSp.setAdapter(adaptStatus);

        StatusSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                StatusSelect = StatusList.get(position).toString();
                //Toast.makeText(SalesListActivity.this, "Seleccionado " + StatusSelect, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });





        FilterStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String NewCondicion = "";
                NewCondicion = " where corte = 1  and Status <> 'PR' and Status <> 'Pendiente' ";
                FillSales(NewCondicion);
            }
        });



        FilterCorte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cCondicion = " and Corte = 0 and status <> 'PR' ";
                FillSales(cCondicion);
            }
        });




        NewCorte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidClose();
            }
        });

        /*
        SalesSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase db = link.getReadableDatabase();
                Cursor CheckSales = db.rawQuery("select count(id) as conteo from sales where Export <> 1 and Status <> 'PE' and Status <> 'PR' ",null);

                //Integer QtySales = CheckSales.getInt(0);
                //if(QtySales>0){
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getApplicationContext());
                    dialogBuilder.setMessage("Enviar las ventas pendiente por sinconizar?");
                    dialogBuilder.setCancelable(true).setTitle("Sincronizar ventas");
                    dialogBuilder.setPositiveButton("Enviarlas", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            RecoverSalesToSync();
                        }
                    });
                    dialogBuilder.setNegativeButton("Cancelar",new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

                    dialogBuilder.create().show();
                //}else{
                //    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getApplicationContext());
                //    dialogBuilder.setMessage("No hay ventas por sincronizar");
                //    dialogBuilder.setCancelable(true).setTitle("Atencion");
                //}
                link.close();
            }
        });

        */


    }







    private void ValidClose(){
        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
        dialogo1.setTitle("Importante");
        dialogo1.setMessage("¿ Quieres Realizar el corte de caja?");
        dialogo1.setCancelable(false);

        dialogo1.setNegativeButton("No,Despues", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {

            }
        });

        dialogo1.setPositiveButton("Si, cierra la caja", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            public void onClick(DialogInterface dialogo1, int id) {
                CreateCorte();
            }
        });
        dialogo1.show();
    }












    private void CreateCorte(){

        SQLiteDatabase db = link.getWritableDatabase();

        Double Total = 0.00;
        Integer User = 0;
        String TermId  = "";
        String cCondicion = "";
        Integer ConsecId = 0;
        ArrayList<String> ContentCorte;
        ContentCorte = new ArrayList<>();
        String Condicion = "";

        Cursor TotalSales = db.rawQuery("Select Consecutive,Import,PayMethod,id from Sales where corte = 0 and Status <> 'PR' and Status <> 'Pendiente' " ,null);

        if(TotalSales.moveToFirst()){

            do{
                String RowCorte = "Folio:" + TotalSales.getString(0) + " - $ " + String.valueOf(TotalSales.getDouble(1))  + " Pagado en : " + TotalSales.getString(2);
                ContentCorte.add(RowCorte);
                Total = Total + TotalSales.getDouble(1);
                Integer IdSale = TotalSales.getInt(3);
            }while(TotalSales.moveToNext());

            Cursor UserLoged = db.rawQuery("Select id from Users where Loged = 1 ",null);
            if(UserLoged.moveToFirst()){
                User = UserLoged.getInt(0);
            }

            Cursor TermNum = db.rawQuery("select Terminal from AppConfig ",null);
            if(TermNum.moveToFirst()){
                TermId = TermNum.getString(0);
            }

            Cursor Consec = db.rawQuery("select max(Consecutive) as Consecutive from Cortes " + Condicion,null);
            if(Consec.moveToFirst()){
                ConsecId = Consec.getInt(0);
            }
            //Cortes (Consecutive INTEGER,Content TEXT,Date TEXT,TotalSales REAL,Expenses REAL,User TEXT,OtherIncomes REAL,RecoveryDebt REAL,Estacion TEXT,Android INTEGER)

            ContentValues values = new ContentValues();
            values.put("Consecutive", ConsecId);
            values.put("Content", String.valueOf(ContentCorte));
            values.put("Date", "getdate()");
            values.put("User", User);

            values.put("Estacion", TermId);
            values.put("Android", 1);

            Long IdCorte = db.insert("Cortes", "id", values);
            Integer NumCorte = Integer.valueOf(String.valueOf(IdCorte));

            do{
                db.execSQL("update Sales set corte = '" + NumCorte + "' where corte = 0 and Status <> 'PR' and Status <> 'Pendiente' ");
            }while(TotalSales.moveToNext());

            Toast.makeText(this, "Corte numero : " + NumCorte + " generado ", Toast.LENGTH_SHORT).show();

        }else{
            Toast.makeText(this, "No hay ventas que ingresar en un corte", Toast.LENGTH_SHORT).show();
        }

    }













    private void FillSales(String Condicion){
        BindUI();

        SalesArray = new ArrayList<SalesConstructor>();

        SQLiteDatabase db = link.getReadableDatabase();
        Cursor Sales = db.rawQuery("select Sales.Consecutive,Clients.Name,Sales.Status,Sales.Import from Sales INNER JOIN Clients ON Clients.CodeMyBusiness = Sales.Client  " + Condicion,null);

        if(Sales.moveToFirst()){
            do{
                SalesItem = new SalesConstructor(Sales.getInt(0),Sales.getString(1),Sales.getString(2),Sales.getDouble(3));
                SalesArray.add(SalesItem);
            }while (Sales.moveToNext());
        }

        salesListAdapter adapter = new salesListAdapter(getApplicationContext(), SalesArray);
        SalesList.setAdapter(adapter);


        Cursor Total = db.rawQuery("select sum(Import) as Total From Sales where Corte = 0 and status <> 'PR' and status <> 'Pendiente' ",null);
        String TotalTxt = "";

        if(Total.moveToFirst()){
            DecimalFormat formateador = new DecimalFormat("###,###,###,###.00");
            Double TotalSales = Total.getDouble(0);
            TotalTxt = String.valueOf(formateador.format(TotalSales));
        }
        ResumeSales.setText("Ventas desde el ultimo corte: $ " + TotalTxt );
        link.close();
    }








    public void RecoverSalesToSync(){
        SQLiteDatabase db = link.getReadableDatabase();
        Cursor CheckSales = db.rawQuery("select top(1) id from sales where Export <> 1 and Status <> 'PE' and Status <> 'PR' ",null);

        if (CheckSales.moveToFirst()) {
            do{
                SaleToSend = CheckSales.getString(0);
                new SyncSale().execute();
            }while(CheckSales.moveToNext());
        }

    }


    /*   ===========================================     Envia venta pendiente    ==========================================*/
    public class SyncSale extends AsyncTask<String,Integer,String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected String doInBackground(String... strings) {

            SQLiteDatabase db = link.getReadableDatabase();
            Cursor SaleData = db.rawQuery("select * from Sales where ID = " + NumSale,null);

            if (SaleData.moveToFirst()){
                ClientSend = SaleData.getString(2);
                ImportSend = SaleData.getString(6);
            }

            JSONObject Venta = new JSONObject();
            JSONObject Partidas = new JSONObject();
            JSONObject SecurePart = new JSONObject();

            try {
                Venta.put("AndroidID",NumSale);
                Venta.put("Client",ClientSend);
                Venta.put("Terminal",Terminal);
                Venta.put("User",UserLoged);
                Venta.put("Status",strings);
                Venta.put("Import",ImportSend);
                Venta.put("Warehouse",Warehouse);

                SecurePart.put("Codigo","DeleteME");
                SecurePart.put("Qty","0");
                SecurePart.put("Price","0");
                SecurePart.put("Discount","0");
                Venta.accumulate("Partidas",SecurePart);

                Cursor SaleParts = db.rawQuery("select * from SalesParts where SaleID = '"+ NumSale +"' ",null);
                if(SaleParts.moveToFirst()){
                    do{
                        String Codigo = SaleParts.getString(2);
                        String Cantidad = SaleParts.getString(4);
                        String PartPrice = SaleParts.getString(5);
                        String PartDiscount = SaleParts.getString(7);

                        Partidas.put("Codigo",Codigo);
                        Partidas.put("Qty",Cantidad);
                        Partidas.put("Price",PartPrice);
                        Partidas.put("Discount",PartDiscount);

                        Venta.accumulate("Partidas",Partidas);
                        Partidas = new JSONObject();

                    }while(SaleParts.moveToNext());
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }




            final String UrlSync = "http://"+ServerIp+"/INTEMWS/SyncSale.php";
            JsonObjectRequest SyncSale = new JsonObjectRequest(Request.Method.POST, UrlSync, Venta, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    JSONArray ArrayResponse = response.optJSONArray("InfoSale");
                    JSONObject CompResponse = null;
                    CompResponse = ArrayResponse.optJSONObject(0);
                    String IDRecover = CompResponse.optString("IdInServer");

                    SQLiteDatabase db = link.getWritableDatabase();
                    db.execSQL("update Sales set Consecutive='"+ IDRecover +"',Export=1 where id = '"+ SaleToSend +"' ");

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(SalesListActivity.this, "La venta " + SaleToSend + " no se pudo sincronizar", Toast.LENGTH_SHORT).show();
                }
            });
            RequestQueue Sync = Volley.newRequestQueue(getApplicationContext());
            Sync.add(SyncSale);
            return null;
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            RecoverSalesToSync();
        }

    }

/* =================================    Fin de envio de venta ===================================*/








}
