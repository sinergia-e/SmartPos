package intem.smartpos.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
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
import com.google.zxing.Result;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


import intem.smartpos.R;
import intem.smartpos.adapters.countsAdapter;
import intem.smartpos.adapters.prodsAdapter;
import intem.smartpos.constructors.countsConstructor;
import intem.smartpos.constructors.prodsConstructor;
import intem.smartpos.database.connection;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static intem.smartpos.R.layout.view_counts;

public class NewInventoryMovsActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler{

    private ProgressDialog dialog;
    private String StatusCamera;
    private ArrayList<prodsConstructor> ProdsArray;
    private FloatingActionButton floatingActionButton_InventoryParts;
    private ImageButton imageButton_NewInventory_FindProd,InventoryFilterLines;
    private EditText editText_NewInventory_FindProd;
    private ListView listView_NewInventory_ListProds;
    private connection link;
    private prodsConstructor prodsItem;
    private String IdMov;
    private TextView textView_NewInventoryMov_Folio;
    private String ServerIp;
    private String Terminal;
    private String ExistCondicion;
    private ArrayList LinesList;
    private String LineSelect;
    private String IdMovInServer;
    private String IdEntry;
    private Integer InvFisInstant;
    private ArrayList<countsConstructor> countsArray;
    private countsConstructor countRow;
    private Spinner spinnerLines;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 1 ;


    private ZXingScannerView zXingScannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_inventory);

        link = new connection(getApplicationContext(), "MyBusinessAndroid", null, 1);


        dialog = new ProgressDialog(this);
        dialog.setMessage("Enviando el progreso...");
//        dialog.setIcon(R.drawable.logoapp);
        dialog.setTitle("Progreso");
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setCancelable(false);

        StatusCamera = "";
        floatingActionButton_InventoryParts = (FloatingActionButton) findViewById(R.id.floatingActionButton_InventoryParts);
        imageButton_NewInventory_FindProd = (ImageButton) findViewById(R.id.imageButton_NewInventory_FindProd);
        editText_NewInventory_FindProd = (EditText) findViewById(R.id.editText_NewInventory_FindProd);
        listView_NewInventory_ListProds = (ListView) findViewById(R.id.listView_NewInventory_ListProds);
        textView_NewInventoryMov_Folio = (TextView) findViewById(R.id.textView_NewInventoryMov_Folio);
        spinnerLines = (Spinner) findViewById(R.id.spinner_newivmov_lines);
        InventoryFilterLines = (ImageButton) findViewById(R.id.InventoryFilter);


        FillProds("");


        SQLiteDatabase dbR = link.getReadableDatabase();


        //****************** Recupera Datos del server ***************************
        Cursor cConfig = dbR.rawQuery("SELECT ServerIP,OnlyExist,DataSize,Terminal,InvFisInstant from AppConfig ",null);
        if(cConfig.moveToFirst()){
            ServerIp = cConfig.getString(0);
            Terminal = cConfig.getString(3);
            InvFisInstant = cConfig.getInt(4);
            if(cConfig.getInt(1)==1){
                ExistCondicion = "&OnExist=true";
            }else{
                ExistCondicion = "";
            }
        }else{
            Toast.makeText(this, "Falta la configuracion del servidor, corrigelo", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this,AppConfigActivity.class);
            startActivity(intent);
        }



        Cursor cInventary = dbR.rawQuery("select id from MovsInv where TypeMov = 'Process' ",null);
        if(cInventary.moveToFirst()){
            IdMov = cInventary.getString(0);
            textView_NewInventoryMov_Folio.setText("Folio : " + IdMov);
        }

        Bundle movData = getIntent().getExtras();
        if (movData != null) {
            String ProdCode = movData.getString("ProdCode") + "";
            String ProdCondicion = " Where Code like '"+ ProdCode +"' or Description like '"+ ProdCode +"' ";
            FillProds(ProdCondicion);
        }

        imageButton_NewInventory_FindProd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String parameter = editText_NewInventory_FindProd.getText().toString();
                FillProds(" where Description like '%"+parameter+"%' or Code like '%"+parameter+"%' ");
            }
        });




        InventoryFilterLines.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LineSelect != "Filtra por linea...") {
                    String cCondicion = " where ProdCategory = '" + LineSelect + "' ";
                    FillProds(cCondicion);
                }else{
                    FillProds("");
                }
            }
        });




        listView_NewInventory_ListProds.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String CodeProd = ProdsArray.get(position).getCode();
//                editText_NewInventory_CodeAdd.setText(CodeProd);
                Intent intent = new Intent(NewInventoryMovsActivity.this,ConfirmCountActivity.class);
                intent.putExtra("MovId",IdMov);
                intent.putExtra("ProdCode",CodeProd);
                startActivity(intent);
            }
        });




        /* ========================  Vista de conteos ========================== */
        floatingActionButton_InventoryParts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewCount(IdMov);
            }
        });




        //Valida permisos de la app
        if (ContextCompat.checkSelfPermission(NewInventoryMovsActivity.this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(NewInventoryMovsActivity.this,
                    new String[]{Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CAMERA );
        }


        SpinnerLines();
        ArrayAdapter<CharSequence> adapSpinnerLn = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, LinesList);
        spinnerLines.setAdapter(adapSpinnerLn);

        spinnerLines.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                LineSelect = LinesList.get(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });




    }















    private void SpinnerLines() {

        SQLiteDatabase db = link.getReadableDatabase();
        Cursor cLines = db.rawQuery("SELECT Distinct(ProdCategory) from Prods ", null);

        LinesList = new ArrayList();
        LinesList.add("Filtra una linea...");

        if (cLines.moveToFirst()) {
            do {
                LinesList.add(cLines.getString(0));
            } while (cLines.moveToNext());
        }
        link.close();
    }











    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                }else{

                }
                return;
            }
        }
    }









    public void ScanButton(View v){

        if (ContextCompat.checkSelfPermission(NewInventoryMovsActivity.this,Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "No permitiste usar la camara", Toast.LENGTH_SHORT).show();
        }else{
            zXingScannerView = new ZXingScannerView(this);
            zXingScannerView.setResultHandler(NewInventoryMovsActivity.this);
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
        Intent intent = new Intent(NewInventoryMovsActivity.this,NewInventoryMovsActivity.class);
        intent.putExtra("ProdCode",Codigo);
        startActivity(intent);
        StatusCamera = "";
    }












    private void FillProds(String cCondicion) {

        SQLiteDatabase db = link.getReadableDatabase();
        Cursor c = db.rawQuery("Select  id,Code,Description,ProdCategory,Brand,Tax,Price1,Exist,ExSucursal,Export,Offer from Prods " + cCondicion, null);

        ProdsArray = new ArrayList<prodsConstructor>();

        if (c.moveToFirst()) {
            do {
                prodsItem = new prodsConstructor(c.getInt(0), c.getString(1), c.getString(2), c.getString(3), c.getString(4), c.getString(5), c.getDouble(6), c.getString(7),c.getString(8), c.getInt(9),c.getDouble(10));
                ProdsArray.add(prodsItem);
            } while (c.moveToNext());
        } else {
            Toast.makeText(this, "No hay ningun producto", Toast.LENGTH_SHORT).show();
        }

        prodsAdapter adapter = new prodsAdapter(getApplicationContext(), ProdsArray);
        listView_NewInventory_ListProds.setAdapter(adapter);

        link.close();
    }























    //************** **************** ******************** Conteos ********************* ******************** ********************
    private void ViewCount(String mCondicion){

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View Counts = LayoutInflater.from(getApplicationContext()).inflate(view_counts, null);
        builder.setView(Counts);

        SQLiteDatabase db = link.getReadableDatabase();
        countsArray = new ArrayList<countsConstructor>();
        //id ,MovId INTEGER,Quantity REAL,CodeProd TEXT,Descrip TEXT
        Cursor cMovsInv = db.rawQuery("SELECT id,MovId,Quantity,CodeProd,Descrip,Export FROM PartMovsInv where MovId = '"+IdMov+"' ", null);
        if (cMovsInv.moveToFirst()) {
            do {
                countRow = new countsConstructor(cMovsInv.getInt(0),cMovsInv.getInt(1),cMovsInv.getDouble(2),cMovsInv.getString(3),cMovsInv.getString(4),cMovsInv.getInt(5));
                countsArray.add(countRow);
            } while (cMovsInv.moveToNext());
        }

        ListView ListCounts = Counts.findViewById(R.id.listView_MovsInv_Count);
        countsAdapter adapter = new countsAdapter(getApplicationContext(), countsArray);
        ListCounts.setAdapter(adapter);

        ListCounts.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                String IdPartOfMov = String.valueOf(countsArray.get(i).getId());
                String DescripPart = countsArray.get(i).getDescrip();
                String QtyCountPart = String.valueOf(countsArray.get(i).getQty());

                Intent goEdit = new Intent(NewInventoryMovsActivity.this,Edit_CountPart_Activity.class);
                goEdit.putExtra("IdPart", IdPartOfMov);
                goEdit.putExtra("IdOrder", IdMov);
                goEdit.putExtra("Descrip", DescripPart);
                goEdit.putExtra("Qty", QtyCountPart);
                startActivity(goEdit);

                return false;
            }
        });



        builder.setNegativeButton("Deja pendiente", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                KeepMov();
            }
        });

        builder.setPositiveButton("Procesar", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                SQLiteDatabase db = link.getReadableDatabase();
                Cursor ValidInvFis = db.rawQuery("select IdInServer from MovsInv where id = '"+ IdMov +"' ",null);
                Integer NumInv = 0;
                if (ValidInvFis.moveToFirst()){
                    NumInv = ValidInvFis.getInt(0);
                }

                if( NumInv == 0){
                    alertThreeButtons();
                }else{
                    CloseMov();
                    Toast.makeText(NewInventoryMovsActivity.this, "El movimiento se cerro como Inventario Fisico", Toast.LENGTH_LONG).show();
                }
            }
        });


        AlertDialog CountsPart = builder.create();
        CountsPart.show();
        link.close();
    }



    //********** ***************** ************** ********************* ************************* ***************** **************

    /*
 * AlertDialog with three button choices.
 *
 * We also set the ninja icon here.
 */
    public void alertThreeButtons() {
        new AlertDialog.Builder(NewInventoryMovsActivity.this)
                .setTitle("Seleccion de movimiento")
                .setMessage("Al no haber numero de inventario, el movimiento se cierra como entrada o salida? ")
                .setIcon(R.drawable.logoapp)

                .setNeutralButton("Revisar",
                        new DialogInterface.OnClickListener() {
                            @TargetApi(11)
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                .setPositiveButton("Salida",
                        new DialogInterface.OnClickListener() {
                            @TargetApi(11)
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                new AsyncOut().execute();
                            }
                        })

                .setNegativeButton("Entrada",
                        new DialogInterface.OnClickListener() {
                            @TargetApi(11)
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                new AsyncEntry().execute();
                            }
                        })
                .show();
    }
































private void KeepMov(){

    SQLiteDatabase dbW = link.getWritableDatabase();
    dbW.execSQL("update MovsInv set TypeMov = 'Pendiente' where id = '"+IdMov+"' ");
    Toast.makeText(NewInventoryMovsActivity.this, "El conteo se queda pendiente", Toast.LENGTH_LONG).show();
    Intent goMovsList = new Intent(NewInventoryMovsActivity.this,MovsInvListActivity.class);
    startActivity(goMovsList);

}


private void CloseMov(){

    SQLiteDatabase dbW = link.getWritableDatabase();
    dbW.execSQL("update MovsInv set TypeMov = 'Cerrado' where id = '"+IdMov+"' ");
    Toast.makeText(NewInventoryMovsActivity.this, "El inventario fue cerrado", Toast.LENGTH_LONG).show();
    Intent goMovsList = new Intent(NewInventoryMovsActivity.this,MovsInvListActivity.class);
    startActivity(goMovsList);

}





    private class AsyncOut extends AsyncTask<Integer,Float,Void>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setProgress(0);
            dialog.setMax(100);
//            dialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Toast.makeText(NewInventoryMovsActivity.this, "Se ingreso la salida en el servidor", Toast.LENGTH_SHORT).show();
            Intent goMovsList = new Intent(NewInventoryMovsActivity.this,MenuActivity.class);
            startActivity(goMovsList);
            super.onPostExecute(aVoid);
        }

        @Override
        protected void onProgressUpdate(Float... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected Void doInBackground(Integer... Movim) {
            String dateMov = "";
            SQLiteDatabase dbR = link.getReadableDatabase();
            Cursor cMov = dbR.rawQuery("SELECT id,TypeMov,DateMov,IdInServer,Folio,User FROM MovsInv WHERE id = '"+IdMov+"' ",null);
            if (cMov.moveToFirst()){
                dateMov = cMov.getString(2);
            }
            //========================= Inserta Salida de inventario =====================================
            final String UrlInsertOut = "http://"+ServerIp+"/INTEMWS/InsertOutInventary.php?dateExit="+ dateMov +"&Term="+ Terminal;
            JsonObjectRequest RequestInsertOut = new JsonObjectRequest(Request.Method.GET, UrlInsertOut, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    JSONArray json = response.optJSONArray("salida");
                    JSONObject RowOutInventary = null;
                    try {
                        RowOutInventary = json.getJSONObject(0);
                        IdMovInServer = RowOutInventary.optString("Id");
                        String Folio = RowOutInventary.optString("folio");

                        SQLiteDatabase dbR = link.getReadableDatabase();
                        SQLiteDatabase dbW = link.getWritableDatabase();

                        dbW.execSQL("UPDATE MovsInv SET IdInServer = '"+IdMovInServer+"',Folio='"+Folio+"',TypeMov = 'Salida pendiente en servidor' WHERE id = '"+IdMov+"' ");

                        Cursor c = dbR.rawQuery("SELECT id,MovId,Quantity,CodeProd,Descrip FROM PartMovsInv where MovId = '"+IdMov+"'  ",null);
                        if(c.moveToFirst()) {
                            do {
                                String UrlInsertOut = "http://"+ServerIp+"/INTEMWS/InsertOutInventary.php?IdOrderInServer="+IdMovInServer+"&Quantity="+c.getString(2)+"&CodeProd="+c.getString(3);
                                JsonObjectRequest RequestInsertOut = new JsonObjectRequest(Request.Method.GET, UrlInsertOut, null, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {

                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {

                                    }
                                });
                                RequestQueue InsertOutInventory = Volley.newRequestQueue(getApplicationContext());
                                InsertOutInventory.add(RequestInsertOut);
                                try {
                                    Thread.sleep(350);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            } while (c.moveToNext());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    dialog.dismiss();

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(NewInventoryMovsActivity.this, "No hay conexion con el servidor", Toast.LENGTH_SHORT).show();
                }
            });
            RequestQueue InsertOutInventory = Volley.newRequestQueue(getApplicationContext());
            InsertOutInventory.add(RequestInsertOut);
            //========================= Fin del Insert de salida =====================================
            return null;
        }
    }














    private class AsyncEntry extends AsyncTask<Integer,Float,Void>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setProgress(0);
            dialog.setMax(100);
            dialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Toast.makeText(NewInventoryMovsActivity.this, "Se ingreso la entrada en el servidor", Toast.LENGTH_SHORT).show();
            Intent goMovsList = new Intent(NewInventoryMovsActivity.this,MenuActivity.class);
            startActivity(goMovsList);
            dialog.dismiss();
            super.onPostExecute(aVoid);
        }

        @Override
        protected void onProgressUpdate(Float... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected Void doInBackground(Integer... Movim) {
            String dateMov = "";
            SQLiteDatabase dbR = link.getReadableDatabase();
            Cursor cMov = dbR.rawQuery("SELECT id,TypeMov,DateMov,IdInServer,Folio,User FROM MovsInv WHERE id = '"+IdMov+"' ",null);
            if (cMov.moveToFirst()){
                dateMov = cMov.getString(2);
            }
            //========================= Inserta Entrada de inventario =====================================
            String UrlInsertEntry = "http://"+ServerIp+"/INTEMWS/InsertEntryInventary.php?dateEntry=" + dateMov;
            JsonObjectRequest RequestInsertEntry = new JsonObjectRequest(Request.Method.GET, UrlInsertEntry, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    JSONArray json = response.optJSONArray("entrada");
                    JSONObject RowEntryInventary = null;
                    try {
                        RowEntryInventary = json.getJSONObject(0);
                        IdEntry = RowEntryInventary.optString("Id");
                        SQLiteDatabase dbW = link.getWritableDatabase();
                        dbW.execSQL("UPDATE MovsInv SET IdInServer = '"+ IdEntry +"',TypeMov = 'Entrada pendiente en servidor' WHERE id = '"+IdMov+"' ");
                        // ********************************  Entrada de partidas  *****************************************
                        SQLiteDatabase dbR = link.getReadableDatabase();
                        Cursor c = dbR.rawQuery("SELECT id,MovId,Quantity,CodeProd,Descrip FROM PartMovsInv where MovId = '"+IdMov+"'  ",null);
                        if(c.moveToFirst()) {
                            do {
                                String UrlInsertEntryPart = "http://"+ServerIp+"/INTEMWS/InsertEntryInventary.php?IdOrderInServer="+IdEntry+"&Quantity="+c.getString(2)+"&CodeProd="+c.getString(3);
                                JsonObjectRequest RequestInsertEntryPart = new JsonObjectRequest(Request.Method.GET, UrlInsertEntryPart, null, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {

                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {

                                    }
                                });
                                RequestQueue InsertEntryPart = Volley.newRequestQueue(getApplicationContext());
                                InsertEntryPart.add(RequestInsertEntryPart);

                                try {
                                    Thread.sleep(350);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            } while (c.moveToNext());
                        }
                        //******************************** Fin entrada de partidas ********************************************

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    dialog.dismiss();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            RequestQueue InsertOutInventory = Volley.newRequestQueue(getApplicationContext());
            InsertOutInventory.add(RequestInsertEntry);
            //========================= Fin del Insert de Entrada =====================================
            return null;
        }
    }























    @Override
    public void onBackPressed() {

    if(StatusCamera.equals("")){

        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
        dialogo1.setTitle("Importante");
        dialogo1.setMessage("¿Dejas el movimiento pendiente?");
        dialogo1.setCancelable(false);

        dialogo1.setNegativeButton("No,Eliminalo", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
                DeleteInvMov();
            }
        });

        dialogo1.setPositiveButton("Si,Guardalo", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            public void onClick(DialogInterface dialogo1, int id) {
                KeepMov();
            }
        });
        dialogo1.show();

    }else{
        Intent intent = new Intent(getApplicationContext(),NewInventoryMovsActivity.class);
        startActivity(intent);
    }
/*

*/
    }





    private void DeleteInvMov(){
        SQLiteDatabase dbW = link.getWritableDatabase();
        dbW.execSQL("DELETE from PartMovsInv WHERE MovId = " + IdMov);
        dbW.execSQL("DELETE from MovsInv WHERE id = " + IdMov);
        Toast.makeText(this, "Se elimino el movimiento", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getApplicationContext(),MenuActivity.class);
        startActivity(intent);
    }




















}
