package intem.smartpos.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.Result;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import intem.smartpos.R;
import intem.smartpos.adapters.prodsAdapter;
import intem.smartpos.constructors.prodsConstructor;
import intem.smartpos.database.connection;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static intem.smartpos.R.layout.view_new_prod;

public class ListProdsActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private String SucExistCondicion,OnlyUpdatesCondicion,Level,StatusCamera,ExistCondicion,Terminal,ServerIp,SingleUpdateCondicion;
    private Integer DataSize,SendProds,SucExist,OnlyExist,OnlyUpdates,DeleteOldData;
    private double NumRegs;

    private ProgressDialog dialog;
    private ImageButton iB_FindProds,imageButton_ListProds_SyncProds;
    private EditText eT_FindProds;
    private ListView lV_ListProds;
    private TextView CountProds;
    connection link;
    private Button OfferFilter;
    private FloatingActionButton NewProd;
    private ZXingScannerView zXingScannerView;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 1 ;
    private ProgressDialog progressDialog;

    private ArrayList<prodsConstructor> ProdsArray;
    prodsConstructor prodsItem;



    private void BindUI(){

        iB_FindProds = (ImageButton) findViewById(R.id.iB_FindProd);
        eT_FindProds = (EditText) findViewById(R.id.eT_FindProds);
        lV_ListProds = (ListView) findViewById(R.id.lV_ListProds);
        CountProds = (TextView) findViewById(R.id.tV_ProdsList_CountProds);
        imageButton_ListProds_SyncProds = (ImageButton) findViewById(R.id.imageButton_ListProds_SyncProds);
        OfferFilter = (Button) findViewById(R.id.ListProds_Offers);
        NewProd = (FloatingActionButton) findViewById(R.id.ListProds_NewProd);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_prods);

        link = new connection(getApplicationContext(), "MyBusinessAndroid", null, 1);

        StatusCamera = "";
        SingleUpdateCondicion = "";

        SQLiteDatabase dbR = link.getReadableDatabase();
        Cursor cConfig = dbR.rawQuery("SELECT ServerIP,OnlyExist,DataSize,Terminal,SendProds,ImportSucEx,OnlyUpdateProds,DeleteOldData from AppConfig ",null);
        if(cConfig.moveToFirst()){
            ServerIp = cConfig.getString(0);
            OnlyExist = cConfig.getInt(1);
            DataSize = cConfig.getInt(2);
            Terminal = cConfig.getString(3);
            SendProds = cConfig.getInt(4);
            SucExist = cConfig.getInt(5);
            OnlyUpdates = cConfig.getInt(6);
            DeleteOldData = cConfig.getInt(7);

            if(OnlyExist==1){
                ExistCondicion = "&OnExist=true";
            }else{
                ExistCondicion = "";
            }

            if (SucExist==1){
                SucExistCondicion = "&SucExistCondicion=true";
            }else{
                SucExistCondicion = "";
            }

            if (OnlyUpdates==1){
                OnlyUpdatesCondicion = "&OnlyUpdates=true";
            }else{
                OnlyUpdatesCondicion = "";
            }




            Cursor UserLevel = dbR.rawQuery("SELECT * From Users where Loged = 1 and Level = 'Administrador' ",null);
            if(UserLevel.moveToFirst()){
                Level = "Administrador";
            }else{
                Level = "Usuario";
            }

        }else{
            Toast.makeText(this, "Falta la configuracion del servidor, corrigelo", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this,AppConfigActivity.class);
            startActivity(intent);
        }

        BindUI();
        registerForContextMenu(lV_ListProds);

        String cCondicion = "";
        FillProds(cCondicion);

        iB_FindProds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Parameter = eT_FindProds.getText().toString();
                if (Parameter.length()>1) {
                    final SQLiteDatabase dbR = link.getReadableDatabase();

                    Cursor cClave = dbR.rawQuery("select Code from Prods where Code = '"+Parameter+"' or Code like '"+Parameter+"' or Description like '%"+Parameter+"%' ",null);
                    if(cClave.moveToFirst()){
                        FillProds(" where Code like '%"+Parameter+"%' or Code ='"+Parameter+"' or Description like '%"+Parameter+"%' ");
                    }else{
                        Cursor cClaveAdd = dbR.rawQuery("select CodeProd from presentations where CodePr = '"+Parameter+"' ",null);
                        if(cClaveAdd.moveToFirst()){
                            String ProductOfClaveAdd = cClaveAdd.getString(0);
                            FillProds(" where Code like '%"+ProductOfClaveAdd+"%' or Code ='"+ProductOfClaveAdd+"' or Description like '%"+ProductOfClaveAdd+"%' ");
                        }else{
                            FillProds("");
                        }
                    }
                }else{
                    String cCondicion = "";
                    FillProds(cCondicion);
                }
            }
        });

        OfferFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cCondicion = " where Offer > 0 ";
                FillProds(cCondicion);
            }
        });

        imageButton_ListProds_SyncProds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(OnlyUpdates==1){
                    new GetUpdates().execute();
                }else{
                    QuestionImportProd();
                }
            }
        });

        //Nuevo producto
        NewProd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewNewProd();
            }
        });

        Bundle movData = getIntent().getExtras();

        if (movData != null) {
            String find = movData.getString("ProdCode");
            Cursor ArticuloQuery = dbR.rawQuery("select Code from Prods where Code = '"+find+"' or Code like '"+find+"' or Description like '%"+find+"%' ",null);

            String ProdCode = "";

            if(ArticuloQuery.moveToFirst()){
                ProdCode = ArticuloQuery.getString(0);
            }else{
                Cursor AuxArticulo = dbR.rawQuery("select CodeProd from Presentations where CodePr = '"+find+"' ",null);
                if (AuxArticulo.moveToFirst()){
                    ProdCode = AuxArticulo.getString(0);
                }
            }

            String FillCondicion = " where Code like '%"+ ProdCode.trim() +"%' ";
            FillProds(FillCondicion);
        }

        //Valida permisos de la app
        if (ContextCompat.checkSelfPermission(ListProdsActivity.this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ListProdsActivity.this,
                    new String[]{Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CAMERA );

        }
    }







    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                }else{

                }
                return;
            }
        }
    }










    private void viewNewProd(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View newProd = LayoutInflater.from(getApplicationContext()).inflate(view_new_prod,null);
        builder.setView(newProd);

        builder.setPositiveButton("Registra el Producto", new DialogInterface.OnClickListener() {

            EditText CodeNewProd = newProd.findViewById(R.id.NewProd_Code);
            EditText DescripNewProd = newProd.findViewById(R.id.NewProd_Descrip);
            EditText TaxNewProd = newProd.findViewById(R.id.NewProd_Tax);
            EditText CostNewProd = newProd.findViewById(R.id.NewProd_Costo_u);
            EditText Price1NewProd = newProd.findViewById(R.id.NewProd_Precio1);
            EditText Price2NewProd = newProd.findViewById(R.id.NewProd_Precio2);
            EditText Price3NewProd = newProd.findViewById(R.id.NewProd_Precio3);
            EditText Price4NewProd = newProd.findViewById(R.id.NewProd_Precio4);
            EditText Price5NewProd = newProd.findViewById(R.id.NewProd_Precio5);
            EditText Price6NewProd = newProd.findViewById(R.id.NewProd_Precio6);
            EditText Price7NewProd = newProd.findViewById(R.id.NewProd_Precio7);
            EditText Price8NewProd = newProd.findViewById(R.id.NewProd_Precio8);
            EditText Price9NewProd = newProd.findViewById(R.id.NewProd_Precio9);

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                String CodeToInsert = CodeNewProd.getText().toString();
                String DescripToInsert = DescripNewProd.getText().toString();
                String TaxToInsert = TaxNewProd.getText().toString();
                String CostToInsert = CostNewProd.getText().toString();
                String Price1ToInsert = Price1NewProd.getText().toString();
                String Price2ToInsert = Price2NewProd.getText().toString();
                String Price3ToInsert = Price3NewProd.getText().toString();
                String Price4ToInsert = Price4NewProd.getText().toString();
                String Price5ToInsert = Price5NewProd.getText().toString();
                String Price6ToInsert = Price6NewProd.getText().toString();
                String Price7ToInsert = Price7NewProd.getText().toString();
                String Price8ToInsert = Price8NewProd.getText().toString();
                String Price9ToInsert = Price9NewProd.getText().toString();


                InsertProd(CodeToInsert,DescripToInsert,TaxToInsert,CostToInsert,Price1ToInsert,Price2ToInsert,Price3ToInsert,Price4ToInsert,Price5ToInsert,Price6ToInsert,Price7ToInsert,Price8ToInsert,Price9ToInsert);
            }
        });

        AlertDialog newProdDialog = builder.create();
        newProdDialog.show();
    }




    private void InsertProd(String CodePr,String DescripPr,String Tax,String CostProd,String Price1,String Price2,String Price3,String Price4,String Price5,String Price6,String Price7,String Price8,String Price9){

        link = new connection(this, "MyBusinessAndroid", null, 1);
        SQLiteDatabase db = link.getWritableDatabase();

        Cursor Valid = db.rawQuery("SELECT id from prods where Code = '"+CodePr+"' " ,null);
        if(Valid.moveToFirst()){
            Toast.makeText(this, "El codigo ya esta registrado, no se registra el nuevo producto, en su lugar actualizalo", Toast.LENGTH_LONG).show();
        }else {

            //id,Code ,Description ,ProdCategory ,Brand ,Tax ,Cost ,Price1 ,Price2 ,Price3 ,Price4 ,Price5 ,Price6 ,Price7 ,Price8 ,Price9 ,Qty2 ,Qty3 ,Qty4 ,Qty5 ,Qty6 ,Qty7 ,Qty8 ,Qty9 ,Exist ,ExSucursal ,Export ,Offer ,image
            ContentValues valuesClient = new ContentValues();
            valuesClient.put("Code", CodePr);
            valuesClient.put("Description", DescripPr);
            valuesClient.put("ProdCategory", "SYS");
            valuesClient.put("Brand", "SYS");
            valuesClient.put("Tax", Tax);
            valuesClient.put("Cost", CostProd);
            valuesClient.put("Price1", Price1);
            valuesClient.put("Price2", Price2);
            valuesClient.put("Price3", Price3);
            valuesClient.put("Price4", Price4);
            valuesClient.put("Price5", Price5);
            valuesClient.put("Price6", Price6);
            valuesClient.put("Price7", Price7);
            valuesClient.put("Price8", Price8);
            valuesClient.put("Price9", Price9);
            valuesClient.put("Qty2", 0);
            valuesClient.put("Qty3", 0);
            valuesClient.put("Qty4", 0);
            valuesClient.put("Qty5", 0);
            valuesClient.put("Qty6", 0);
            valuesClient.put("Qty7", 0);
            valuesClient.put("Qty8", 0);
            valuesClient.put("Qty9", 0);
            valuesClient.put("Exist", 0);
            valuesClient.put("ExSucursal", 0);
            valuesClient.put("Export", 1);
            valuesClient.put("Offer", 0);
            valuesClient.put("image", "");
            Long idResult = db.insert("Prods", "id", valuesClient);

            FillProds("");
            link.close();
        }
    }





    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu_prods,menu);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        String Status = null;

        switch (item.getItemId()){

            case R.id.OpenDetail:
                Intent goDetails = new Intent(ListProdsActivity.this,ProdsDetailActivity.class);
                goDetails.putExtra("ProdCode",String.valueOf(ProdsArray.get(info.position).getId()));
                startActivity(goDetails);
                return true;

            case R.id.Update:
                SingleUpdateCondicion = "?articulo=100227";
                new GetUpdates().execute();
                return true;

            case R.id.EditProd:
                if(Level == "Administrador"){
                    Intent goEdit = new Intent(ListProdsActivity.this,EditProdActivity.class);
                    goEdit.putExtra("ProdCode",String.valueOf(ProdsArray.get(info.position).getId()));
                    startActivity(goEdit);
                    return true;
                }else{
                    Toast.makeText(this, "Esta opcion requiere un usuario supervisor", Toast.LENGTH_SHORT).show();
                }

            default:
                return super.onContextItemSelected(item);
        }
    }








    @Override
    public void onBackPressed() {

        if(StatusCamera.equals("")){
            Intent goMenu = new Intent(getApplicationContext(),MenuActivity.class);
            startActivity(goMenu);
        }else{
            Intent goProds = new Intent(getApplicationContext(),ListProdsActivity.class);
            startActivity(goProds);
        }

    }






    /***   Recupera los productos y los pone en el list View ***/
    /***   ====================================================================== ***/
    private void FillProds(String cCondicion) {
        BindUI();
        SQLiteDatabase db = link.getReadableDatabase();
        Cursor c = db.rawQuery("Select id,Code,Description,ProdCategory,Brand,Tax,Price1,Exist,ExSucursal,Export,Offer from Prods " + cCondicion, null);

        Cursor PrCount = db.rawQuery("Select count(id) from Presentations ", null);
        if(PrCount.moveToFirst()){
            Toast.makeText(this, "Presentaciones : " + PrCount.getString(0), Toast.LENGTH_SHORT).show();
        }


        ProdsArray = new ArrayList<prodsConstructor>();
        if (c.moveToFirst()) {
            do {
                prodsItem = new prodsConstructor(c.getInt(0),c.getString(1),c.getString(2),c.getString(3),c.getString(4),c.getString(5),c.getDouble(6),c.getString(7),c.getString(8),c.getInt(9),c.getDouble(10) );
                ProdsArray.add(prodsItem);
            } while (c.moveToNext());
        }else{
            Toast.makeText(this, "No hay ningun producto", Toast.LENGTH_SHORT).show();
        }


        prodsAdapter adapter = new prodsAdapter(getApplicationContext(), ProdsArray);
        lV_ListProds.setAdapter(adapter);

        int countProds = ProdsArray.size();
        CountProds.setText("Tenemos " + countProds + " producto(s) en la lista");
    }












    //==========================================================================================================
    //==========================================================================================================
    //***************** ****************   Confirma Sincronizacion de productos  ********************* *************************

    private void QuestionImportProd(){
        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
        dialogo1.setTitle("Importante");
        dialogo1.setMessage("Actualizar productos?");
        dialogo1.setCancelable(false);

        dialogo1.setNeutralButton("Recupera Productos", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
                SQLiteDatabase dbw = link.getWritableDatabase();
                dbw.execSQL("DELETE from Prods");
                new GetPrCodes().execute();
            }
        });

        dialogo1.setNegativeButton("Recupera Presentaciones", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
                SQLiteDatabase dbw = link.getWritableDatabase();
                dbw.execSQL("DELETE from Presentations");
                new GetAuxCodes().execute();
            }
        });

        dialogo1.show();
    }









    private class GetPrCodes extends AsyncTask</*Parametros*/String,/*Progreso*/Integer,/*Resultado*/String>{

        protected void onPreExecute() {
            //super.onPreExecute();
            dialog = new ProgressDialog(ListProdsActivity.this);
            dialog.setTitle("Productos con cambios");
            dialog.setMessage("Actualizando...");
            dialog.setProgressStyle(dialog.STYLE_SPINNER);
            dialog.setCancelable(false);
            dialog.setProgress(0);
            dialog.setMax(100);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... prodFindAdds) {
            String UrlGetAdds = "http://" + ServerIp + "/INTEMWS/GetPrCodes.php";
            JsonObjectRequest getProdCodes = new JsonObjectRequest(Request.Method.GET, UrlGetAdds, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    SQLiteDatabase dbw = link.getWritableDatabase();
                    JSONArray Prods = response.optJSONArray("ProdCodes");
                    JSONObject ClaveRow = new JSONObject();

                    for (int row = 0; row < Prods.length(); row++) {
                        if(row >= Prods.length()-1){
                            dialog.dismiss();
                            FillProds("");
                        }

                        try {

                            ClaveRow = Prods.getJSONObject(row);
                            String Code = ClaveRow.optString("A");
                            String Descrip = ClaveRow.optString("B");
                            String Price = ClaveRow.optString("C");
                            String Exist = ClaveRow.optString("D");
                            dbw.execSQL("insert into Prods (Code,Description,Price1,Exist) values ('"+Code+"','"+Descrip+"','"+Price+"','"+Exist+"') ");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });

            RequestQueue request = Volley.newRequestQueue(getApplicationContext());
            getProdCodes.setRetryPolicy(
                    new DefaultRetryPolicy(120000,
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)) ;
            request.add(getProdCodes);
            return null;
        }

        //-------------------------------------------------------------------------------------------------------------------

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            if( dialog.getProgress() < dialog.getMax() ){
                dialog.setProgress(values[0]);
            }
        }
        //-------------------------------------------------------------------------------------------------------------------



        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            FillProds("");
        }
    }







    private class GetAuxCodes extends AsyncTask</*Parametros*/String,/*Progreso*/Integer,/*Resultado*/String>{

        protected void onPreExecute() {
            //super.onPreExecute();
            dialog = new ProgressDialog(ListProdsActivity.this);
            dialog.setTitle("Productos con cambios");
            dialog.setMessage("Actualizando...");
            dialog.setProgressStyle(dialog.STYLE_SPINNER);
            dialog.setCancelable(false);
            dialog.setProgress(0);
            dialog.setMax(100);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... prodFindAdds) {

            String UrlGetAdds = "http://" + ServerIp + "/INTEMWS/GetClavesAdd.php";
            JsonObjectRequest getClavesAdd = new JsonObjectRequest(Request.Method.GET, UrlGetAdds, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    SQLiteDatabase dbw = link.getWritableDatabase();
                    JSONArray ClavesAddArray = response.optJSONArray("AuxCodes");
                    JSONObject ClaveRow = new JSONObject();

                    for (int row = 0; row < ClavesAddArray.length(); row++) {
                        if(row >= ClavesAddArray.length()-1){
                            dialog.dismiss();
                            FillProds("");
                        }

                        try {

                            ClaveRow = ClavesAddArray.getJSONObject(row);
                            String Articulo = ClaveRow.optString("A");
                            String Clave = ClaveRow.optString("B");
                            String Price = ClaveRow.optString("C");
                            String Qty = ClaveRow.optString("D");

                            dbw.execSQL("insert into Presentations (CodePr,CodeProd,DescPr,QtyPr,PricePr) values ('"+Clave+"','"+Articulo+"','CAJA DE "+Qty+" PZAS ','"+Qty+"','"+Price+"') ");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });

            RequestQueue request = Volley.newRequestQueue(getApplicationContext());
            getClavesAdd.setRetryPolicy(
                    new DefaultRetryPolicy(120000,
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)) ;
            request.add(getClavesAdd);
            return null;
        }


        //-------------------------------------------------------------------------------------------------------------------

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            if( dialog.getProgress() < dialog.getMax() ){
                dialog.setProgress(values[0]);
            }
        }
        //-------------------------------------------------------------------------------------------------------------------



        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            FillProds("");
        }
    }































    public class GetUpdates extends AsyncTask<String ,Integer, Integer>{

        @Override
        protected Integer doInBackground(String... cCondicion) {
            String UrlGetUpdates = "http://" + ServerIp + "/INTEMWS/TestUpdates.php" + SingleUpdateCondicion;
            JsonObjectRequest UpdateProds = new JsonObjectRequest(Request.Method.GET, UrlGetUpdates, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    SQLiteDatabase dbw = link.getWritableDatabase();
                    JSONArray ProdsArray = response.optJSONArray("prods");
                    JSONObject ProdsRow = new JSONObject();

                    for (int row = 0; row < ProdsArray.length(); row++) {

                        if(row >= ProdsArray.length() - 1){
                            dialog.dismiss();
                            SingleUpdateCondicion = "";
                            FillProds("");
                        }

                        try {

                            ProdsRow = ProdsArray.getJSONObject(row);
                            String Code = ProdsRow.optString("articulo");
                            String Descrip = ProdsRow.optString("descrip");
                            String Price = ProdsRow.optString("precio");
                            String Exist = ProdsRow.optString("existencia");

                            dbw.execSQL("Delete from Prods where Code = '"+Code+"' ");
                            dbw.execSQL("delete from Presentations where CodeProd = '"+ Code +"' ");

                            dbw.execSQL("insert into Prods (Code,Description,Price1,Exist) values ('"+Code+"','"+Descrip+"','"+Price+"','"+Exist+"') ");

                            JSONArray Presentaciones = ProdsRow.optJSONArray("clavesadd");
                            //Toast.makeText(ListProdsActivity.this, "Code : " + Code + ",descrip " + Descrip + ", Precio: " + Price + ", Existencia: " + Exist + "" , Toast.LENGTH_LONG).show();
                            for(int i = 0; i < Presentaciones.length(); i++){

                                JSONObject PrRow = Presentaciones.getJSONObject(i);
                                String clave = PrRow.optString("clave");
                                String precio = PrRow.optString("precio");
                                String Qty = PrRow.optString("cantidad");
                                String Unit = PrRow.optString("unidad");
                                String PresentationDescrip = Unit +" DE "+ Qty + " PZAS" ;
                                dbw.execSQL("insert into Presentations (CodePr,CodeProd,DescPr,QtyPr,PricePr) values ('"+clave+"','"+Code+"','"+ PresentationDescrip + "','"+ Qty +"','"+ precio +"') ");
                                //Toast.makeText(ListProdsActivity.this, "" + clave + " - " + Unit + " DE "+ Qty + " PZAS ", Toast.LENGTH_SHORT).show();

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(ListProdsActivity.this, "Sin conexion", Toast.LENGTH_SHORT).show();
                }
            });

            RequestQueue request = Volley.newRequestQueue(getApplicationContext());
            UpdateProds.setRetryPolicy(
                    new DefaultRetryPolicy(120000,
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)) ;

            request.add(UpdateProds);
            return null;
        }

        //-------------------------------------------------------------------------------------------------------------------

        @Override
        protected void onPreExecute() {
            //super.onPreExecute();
            dialog = new ProgressDialog(ListProdsActivity.this);
            dialog.setTitle("Productos con cambios");
            dialog.setMessage("Actualizando...");
            dialog.setProgressStyle(dialog.STYLE_SPINNER);
            dialog.setCancelable(false);
            dialog.setProgress(0);
            dialog.setMax(100);
            dialog.show();
        }
        //-------------------------------------------------------------------------------------------------------------------

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
        }
        //-------------------------------------------------------------------------------------------------------------------

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            if( dialog.getProgress() < dialog.getMax() ){
                dialog.setProgress(values[0]);
            }
        }
        //-------------------------------------------------------------------------------------------------------------------


    }



































    private void UploadNewProds(){

        SQLiteDatabase db = link.getReadableDatabase();
        Cursor c = db.rawQuery("Select id,Code,Description,ProdCategory,Brand,Tax,Price1,Price2,Price3,Price4,Price5,Price6,Price7,Price8,Price9,Cost,Exist,ExSucursal,Export,Offer,image from Prods where Export = 1 " , null);

        if (c.moveToFirst()) {
            do {
                String CodeProd = c.getString(1);
                String Descrip = c.getString(2);
                Descrip = Descrip.replace(" ","%20");
                Double Tax = c.getDouble(5);

                Double PriceList = c.getDouble(6);
                PriceList = PriceList / (1 + (Tax/100));

                Double Price2 = c.getDouble(7);
                Price2 = Price2/ (1+(Tax/100));

                Double Price3 = c.getDouble(8);
                Price3 = Price3 / (1+(Tax/100));

                Double Price4 = c.getDouble(9);
                Price4 = Price4/ (1+(Tax/100));

                Double Price5 = c.getDouble(10);
                Price5 = Price5/ (1+(Tax/100));

                Double Price6 = c.getDouble(11);
                Price6 = Price6/ (1+(Tax/100));

                Double Price7 = c.getDouble(12);
                Price7 = Price7/ (1+(Tax/100));

                Double Price8 = c.getDouble(13);
                Price8 = Price8/ (1+(Tax/100));

                Double Price9 = c.getDouble(14);
                Price9 = Price9/ (1+(Tax/100));

                Double Cost = c.getDouble(15);
                Cost = Cost/ (1+(Tax/100));

                final String UrlSendData = "http://" + ServerIp + "/INTEMWS/SaveProds.php?articulo="+CodeProd+"&descrip="+Descrip+"&costo_u=" + Cost + "&precio1=" +PriceList+ "&precio2=" +Price2+ "&precio3=" +Price3+ "&precio4=" +Price4+ "&precio5=" +Price5+ "&precio6=" +Price6+ "&precio7=" +Price7+ "&precio8=" +Price8+ "&precio9=" +Price9+ "&Tax="+Tax+" ";

                final String finalDescrip = Descrip;
                JsonObjectRequest RequestUpdate = new JsonObjectRequest(Request.Method.GET, UrlSendData, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONArray json = response.optJSONArray("Prod");
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = json.getJSONObject(0);
                            String code = jsonObject.optString("code");
                            SQLiteDatabase dbW = link.getWritableDatabase();

                            dbW.execSQL("UPDATE Prods set Export = 0 where code = '" + code + "' ");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ListProdsActivity.this, UrlSendData , Toast.LENGTH_SHORT).show();
                    }
                });

                RequestQueue UpdateProd = Volley.newRequestQueue(getApplicationContext());
                UpdateProd.add(RequestUpdate);

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }while (c.moveToNext());

            //RecoveryCount();
        }else{
            //RecoveryCount();
        }
        link.close();
    }












    public void ScanButton(View v){

        if (ContextCompat.checkSelfPermission(ListProdsActivity.this,Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Se requiere permiso para usar la camara", Toast.LENGTH_SHORT).show();
        }else{
            zXingScannerView = new ZXingScannerView(this);
            zXingScannerView.setResultHandler(ListProdsActivity.this);
            setContentView(zXingScannerView);
            zXingScannerView.startCamera();
            StatusCamera = "Activated";
        }
    }

    @Override
    public void handleResult(Result result) {
        String Codigo = result.getText();
        setContentView(R.layout.activity_new_inventory);
        zXingScannerView.stopCamera();
        Intent intent = new Intent(ListProdsActivity.this,ListProdsActivity.class);
        intent.putExtra("ProdCode",Codigo);
        startActivity(intent);
        StatusCamera = "";
    }



}
