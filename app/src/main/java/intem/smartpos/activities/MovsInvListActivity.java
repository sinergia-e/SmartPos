package intem.smartpos.activities;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
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
import intem.smartpos.adapters.movsAdapter;
import intem.smartpos.constructors.movsConstructor;
import intem.smartpos.database.connection;

public class MovsInvListActivity extends AppCompatActivity {

    private connection link;

    private movsConstructor movsRow;
    private ArrayList<movsConstructor> MovsArray;

    private ListView listView_MovsInv_Movs;
    private ImageButton imageButton_MovsInv_Filter;
    private Spinner spinner_MovsInv_Status;
    private Spinner spinnerStatus;
    private String StatusSelect;
    private ArrayList StatusList;
    private String cCondicion;
    private ProgressDialog dialog;
    public RequestQueue request;
    private String ServerIp;
    private String IdMov;
    public String InvFisInstant;
    private FloatingActionButton floatingActionButton_MovsInvList_AddMov;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movs_inv_list);

        dialog = new ProgressDialog(this);
        dialog.setMessage("Enviando los conteos al servidor...");
        dialog.setTitle("Progreso");
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setCancelable(false);

        link = new connection(getApplicationContext(), "MyBusinessAndroid", null, 1);
        SQLiteDatabase dbR = link.getReadableDatabase();
        SQLiteDatabase dbW = link.getWritableDatabase();

        dbW.execSQL("update MovsInv set TypeMov = 'Pendiente' where TypeMov = 'Process' ");

        Cursor cConfig = dbR.rawQuery("select ServerIP,OnlyExist,DataSize,Terminal,SendProds,ImportSucEx,InvFisInstant from AppConfig",null);

        if(cConfig.moveToFirst()){
            ServerIp = cConfig.getString(0);
            InvFisInstant = cConfig.getString(6);
        }else{
            Toast.makeText(this, "Falta la configuracion del servidor, corrigelo", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this,AppConfigActivity.class);
            startActivity(intent);
        }

        cCondicion = "";

        imageButton_MovsInv_Filter = (ImageButton) findViewById(R.id.imageButton_MovsInv_FilterByStatus);
        spinner_MovsInv_Status = (Spinner) findViewById(R.id.spinner_MovsInv_Status);

        listView_MovsInv_Movs = (ListView) findViewById(R.id.listView_MovsInv_Movs);
        registerForContextMenu(listView_MovsInv_Movs);


        floatingActionButton_MovsInvList_AddMov = (FloatingActionButton) findViewById(R.id.floatingActionButton_MovsInvList_AddMov);
        floatingActionButton_MovsInvList_AddMov.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConfirmNewMov();
            }
        });


        getSpinnerStatus();
        ArrayAdapter<CharSequence> adaptSpinnerStatus = new ArrayAdapter<CharSequence>(getApplicationContext(),android.R.layout.simple_spinner_item,StatusList);
        spinner_MovsInv_Status.setAdapter(adaptSpinnerStatus);

        spinner_MovsInv_Status.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                StatusSelect = StatusList.get(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        FillMovs("");

        imageButton_MovsInv_Filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cCondicion = " where MovsInv.TypeMov = '"+StatusSelect+"' ";
                FillMovs(cCondicion);
            }
        });

    }






    @Override
    public void onBackPressed() {
        Intent GoMenu = new Intent(MovsInvListActivity.this,MenuActivity.class);
        startActivity(GoMenu);
    }
















    private void getSpinnerStatus() {
        SQLiteDatabase db = link.getReadableDatabase();
        Cursor cStatus = db.rawQuery("SELECT Distinct(TypeMov) from MovsInv ", null);

        StatusList = new ArrayList();
        StatusList.add("Filtra por estado del movimiento...");

        if (cStatus.moveToFirst()) {
            do {
                StatusList.add(cStatus.getString(0));
            } while (cStatus.moveToNext());
        }
        link.close();
    }




    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu_movsinv,menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();

        switch (item.getItemId()){

            case R.id.OpenMov:
                String TypeMov = MovsArray.get(info.position).getTypeMov();
                if(TypeMov.equals("Cerrado")){
                    Toast.makeText(this, "Este conteo esta cerrado no puede editarse", Toast.LENGTH_SHORT).show();
                }else{
                    OpenMov(MovsArray.get(info.position).getId());
                }
                return true;

            case R.id.SyncInv:
                new MovsInvListActivity.AsyncProdsCount().execute();
                return true;


            case R.id.DeleteMov:
                DeleteMov(MovsArray.get(info.position).getId());
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }




    private void OpenMov(Integer Id){
        String IdMov = String.valueOf(Id);
        SQLiteDatabase dbW = link.getWritableDatabase();
        dbW.execSQL("update MovsInv set TypeMov = 'Process' where id = '"+Id+"' ");
        Intent intent = new Intent(MovsInvListActivity.this,NewInventoryMovsActivity.class);
        startActivity(intent);
    }

    private void DeleteMov(Integer IdMov){
        SQLiteDatabase dbW = link.getWritableDatabase();
        dbW.execSQL("DELETE from PartMovsInv where MovId = " + IdMov);
        dbW.execSQL("DELETE from MovsInv where id = " + IdMov);
        FillMovs("");
        Toast.makeText(this, "Se elimino el movimiento seleccionado ", Toast.LENGTH_SHORT).show();
    }



    private void FillMovs(String cCodicion) {

        SQLiteDatabase db = link.getReadableDatabase();
        Cursor c = db.rawQuery("Select * from MovsInv " + cCodicion, null);

        MovsArray = new ArrayList<movsConstructor>();

        if (c.moveToFirst()) {
            do {
                movsRow = new movsConstructor(c.getInt(0),c.getString(1),c.getString(2),c.getInt(3),c.getInt(4),c.getString(5));
                MovsArray.add(movsRow);
            } while (c.moveToNext());
        }

        movsAdapter adapt = new movsAdapter(getApplicationContext(),MovsArray);
        listView_MovsInv_Movs.setAdapter(adapt);
        link.close();
    }


    private void ConfirmNewMov(){
        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
        dialogo1.setTitle("Importante");
        dialogo1.setMessage("¿Quieres registrar un nuevo conteo para movimiento de inventario? ");
        dialogo1.setCancelable(false);
        dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {

            }
        });
        dialogo1.setPositiveButton("Si registralo", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
                SQLiteDatabase dbW = link.getWritableDatabase();

                String date = (DateFormat.format("dd-MM-yyyy hh:mm:ss", new java.util.Date()).toString());
                ContentValues values = new ContentValues();
                values.put("TypeMov", "Process");
                values.put("DateMov", date);
                values.put("User", "");

                Long idResult = dbW.insert("MovsInv", "id", values);

                Intent intent = new Intent(MovsInvListActivity.this,NewInventoryMovsActivity.class);
                startActivity(intent);
            }
        });
        dialogo1.show();
    }













    private class AsyncProdsCount extends AsyncTask<Integer,Float,Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setProgress(0);
            dialog.setMax(100);
            dialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Toast.makeText(MovsInvListActivity.this, "Se sincronizaron los productos faltantes", Toast.LENGTH_SHORT).show();
//            Intent ProdsToCount = new Intent(ConfirmCountActivity.this,NewInventoryMovsActivity.class);
//            startActivity(ProdsToCount);
            //
             dialog.cancel();
            super.onPostExecute(aVoid);
        }

        @Override
        protected void onProgressUpdate(Float... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected Void doInBackground(Integer... Movim) {

            //========================= Envia conteo de inventario =====================================

            SQLiteDatabase dbR = link.getReadableDatabase();
            SQLiteDatabase dbW = link.getWritableDatabase();

            Cursor Query_InvProdsTosend = dbR.rawQuery("select MovId,Quantity,CodeProd,Descrip,Export from PartMovsInv where export = 0 and MovId = '"+ IdMov +"' ",null);
            if(Query_InvProdsTosend.moveToFirst()){
                do{
                    String ProdToCount = Query_InvProdsTosend.getString(2);
                    String QtyToCount = Query_InvProdsTosend.getString(1);
                    EditText Num_InventaryInServer = (EditText) findViewById(R.id.ConfirmCount_InventNum);

                    Integer NumInvInteger = 0;
                    //String InventaryNumber = String.valueOf(NumInvInteger);

                    Cursor GetNumInv = dbR.rawQuery("select IdInServer from MovsInv where id = '"+IdMov+"' ",null);
                    if(GetNumInv.moveToFirst()){
                        NumInvInteger = GetNumInv.getInt(0);
                    }

                    final String UrlRegisterInventary = "http://" + ServerIp + "/INTEMWS/RegisterInventary.php?articulo="+ProdToCount+"&qty="+QtyToCount+"&numinv="+ NumInvInteger +"&instantinv="+InvFisInstant+" ";

                    final Integer finalNumInvInteger = NumInvInteger;
                    JsonObjectRequest RequestUpdate = new JsonObjectRequest(Request.Method.GET, UrlRegisterInventary, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            JSONArray json = response.optJSONArray("invent");
                            JSONObject jsonObject = null;
                            try {
                                jsonObject = json.getJSONObject(0);
                                String result = jsonObject.optString("result");
                                if(result.equals("noopen")){
                                    //Toast.makeText(MovsInvListActivity.this, "Este tipo de conteo requiere un inventario abierto", Toast.LENGTH_LONG).show();
                                }else if(result.equals("noinv")){
                                    //Toast.makeText(MovsInvListActivity.this, "No existe el inventario que estas indicando o no tiene el status necesario", Toast.LENGTH_LONG).show();
                                }else {
                                    SQLiteDatabase dbW = link.getWritableDatabase();
                                    dbW.execSQL("UPDATE PartMovsInv set Export = 1 where CodeProd = '" + result + "' ");
                                    //Toast.makeText(MovsInvListActivity.this, "Registrado con exito en el servidor", Toast.LENGTH_SHORT).show();
                                    Intent InventaryList = new Intent(MovsInvListActivity.this,NewInventoryMovsActivity.class);
                                    startActivity(InventaryList);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //Toast.makeText(MovsInvListActivity.this, "No se pudo registrar el conteo en el servidor, valida el numero de inventario o la conexion " + finalNumInvInteger, Toast.LENGTH_LONG).show();
                            //Intent InventaryList = new Intent(MovsInvListActivity.this,NewInventoryMovsActivity.class);
                            //startActivity(InventaryList);
                        }
                    });

                    RequestQueue UpdateProd = Volley.newRequestQueue(getApplicationContext());
                    UpdateProd.add(RequestUpdate);

                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    dialog.setProgress(0);

                }while(Query_InvProdsTosend.moveToNext());
            }

            //========================= Fin del Insert de salida =====================================
            return null;
        }
    }







}
