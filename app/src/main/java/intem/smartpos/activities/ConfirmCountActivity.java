package intem.smartpos.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import intem.smartpos.R;
import intem.smartpos.database.connection;

public class ConfirmCountActivity extends AppCompatActivity {

    public String IdMov,Product,Description,imageName,ServerIp,InvFisInstant,IdInvFis;
    public ProgressDialog dialog;
    public RequestQueue request;
    public connection link;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_count);

        dialog = new ProgressDialog(this);
        dialog.setMessage("Enviando los conteos al servidor...");
        dialog.setTitle("Progreso");
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setCancelable(false);

        link = new connection(getApplicationContext(), "MyBusinessAndroid", null, 1);

        SQLiteDatabase dbR = link.getReadableDatabase();
        Cursor cConfig = dbR.rawQuery("select ServerIP,OnlyExist,DataSize,Terminal,SendProds,ImportSucEx,InvFisInstant from AppConfig",null);

        if(cConfig.moveToFirst()){
            ServerIp = cConfig.getString(0);
            InvFisInstant = cConfig.getString(6);
        }else{
            Toast.makeText(this, "Falta la configuracion del servidor, corrigelo", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this,AppConfigActivity.class);
            startActivity(intent);
        }


        final EditText Num_Inventary = (EditText) findViewById(R.id.ConfirmCount_InventNum);
        TextView Product_Code = (TextView) findViewById(R.id.ConfirmCount_ProdCode);
        TextView Descrip_Prod = (TextView) findViewById(R.id.ConfirmCount_DescripProd);
        TextView Count = (TextView) findViewById(R.id.ConfirmCount_PrevCount);
        CheckBox Chk_Sum = (CheckBox) findViewById(R.id.ConfirmCount_ChkSuma);
        Button Button_Return = (Button) findViewById(R.id.ConfirmCount_InventMov);
        Button Button_SaveCount = (Button) findViewById(R.id.ConfirmCount_SaveCount);
        final ImageView Prod_Picture = (ImageView) findViewById(R.id.ConfirmCount_ImageProd);

        Bundle Inventary = getIntent().getExtras();
        if (Inventary != null) {
            IdMov = Inventary.getString("MovId");
            Product = Inventary.getString("ProdCode");

            Cursor ValidProd = dbR.rawQuery("select Code from prods where Code = '"+Product+"' ",null);
            if(ValidProd.moveToFirst()){

            }else{
                Toast.makeText(this, "El producto escaneado  no existe, debes darlo de alta primero ", Toast.LENGTH_LONG).show();
                Intent ReturnProdsToCount = new Intent(ConfirmCountActivity.this,NewInventoryMovsActivity.class);
                startActivity(ReturnProdsToCount);
            }
        }


        Cursor InvIdServer = dbR.rawQuery("select IdInServer from MovsInv where id = '"+ IdMov +"' ",null);
        if(InvIdServer.moveToFirst()){
            Double IdInvDo = InvIdServer.getDouble(0);
            if(IdInvDo > 0){
                String IdInv = String.valueOf(IdInvDo);
                Num_Inventary.setText(IdInv);
                Num_Inventary.setEnabled(false);
                IdInvFis = String.valueOf(IdInvDo);
                Toast.makeText(this, "Existe un valor de inventario", Toast.LENGTH_SHORT).show();
            }else {
                IdInvFis = "";
            }
        }



        Cursor ProductQuery = dbR.rawQuery("select Description,image from Prods where Code = '"+ Product +"' " ,null);
        if(ProductQuery.moveToFirst()){
            Description = ProductQuery.getString(0);
            imageName = ProductQuery.getString(1);
            Product_Code.setText(Product);
            Descrip_Prod.setText(Description);
        }


        Cursor PrevCount = dbR.rawQuery("select Quantity from PartMovsInv where CodeProd = '"+ Product +"' and MovId = '"+ IdMov +"' " ,null);
        if(PrevCount.moveToFirst()){
            String CountPrev = PrevCount.getString(0);
            Count.setText("Conteo previo del producto:" + CountPrev);
        }else{
            Count.setVisibility(View.INVISIBLE);
            Chk_Sum.setVisibility(View.INVISIBLE);
        }




        String urlImage = "http://"+ ServerIp +"/INTEMWS/ImageProds/"+imageName;
        urlImage = urlImage.replace(" ","%20");
        request = Volley.newRequestQueue(getApplicationContext());

        ImageRequest imageRequest = new ImageRequest(urlImage, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                Prod_Picture.setImageBitmap(response);
            }
        }, 0, 500, ImageView.ScaleType.CENTER, null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(ProdsDetailActivity.this, "No se logro cargar la imagen", Toast.LENGTH_SHORT).show();
            }
        });
        request.add(imageRequest);




        Button_Return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Return = new Intent(ConfirmCountActivity.this,NewInventoryMovsActivity.class);
                startActivity(Return);
            }
        });



        Button_SaveCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText ET_Qty_Product = (EditText) findViewById(R.id.ConfirmCount_Qty);
                String QtyProd = ET_Qty_Product.getText().toString();

                if(QtyProd.equals("")){
                    Toast.makeText(ConfirmCountActivity.this, "La cantidad no puede estar vacia", Toast.LENGTH_SHORT).show();
                }else{
                    SaveCount(Product);
                }
            }
        });




    }











    @Override
    public void onBackPressed() {

    }
















    public void SaveCount(String ProdTosave){

        SQLiteDatabase dbR = link.getReadableDatabase();
        SQLiteDatabase dbW = link.getWritableDatabase();

        EditText ET_Qty_Product = (EditText) findViewById(R.id.ConfirmCount_Qty);
        String QtyProd = ET_Qty_Product.getText().toString();
        EditText NewInvFis = (EditText) findViewById(R.id.ConfirmCount_InventNum);
        String ValueNewInvFis = NewInvFis.getText().toString();

        if(IdInvFis.equals("") && ValueNewInvFis!= ""){
            dbW.execSQL("update MovsInv set IdInServer = '"+ ValueNewInvFis +"' where id = '"+ IdMov +"' ");
            //Toast.makeText(this, "Se cuplen condiciones para actualizar el movimiento", Toast.LENGTH_SHORT).show();
        }

        Cursor ValidCode = dbR.rawQuery("SELECT Code,Description from prods where Code = '"+ ProdTosave +"' ",null);
        if(ValidCode.moveToFirst()) {
            Cursor Query_ValidPrevCount = dbR.rawQuery("select CodeProd from PartMovsInv where MovId = '"+ IdMov +"' and CodeProd = '"+ ProdTosave +"'  ",null);
            if(Query_ValidPrevCount.moveToFirst()){
                CheckBox Chk_Sum = (CheckBox) findViewById(R.id.ConfirmCount_ChkSuma);
                if(Chk_Sum.isChecked()){
                    dbW.execSQL("update PartMovsInv set Quantity = Quantity + '"+ QtyProd +"',Export=0 where MovId = '"+ IdMov +"' and CodeProd = '"+ ProdTosave +"'  ");
                }else{
                    dbW.execSQL("update PartMovsInv set Quantity = '"+ QtyProd +"',Export=0 where MovId = '"+ IdMov +"' and CodeProd = '"+ ProdTosave +"'  ");
                }
            }else{
                String DescripProd = ValidCode.getString(1);
                dbW.execSQL("INSERT INTO PartMovsInv (MovId,Quantity,CodeProd,Descrip,Export) values ('" + IdMov + "','" + QtyProd + "','" + ProdTosave + "','" + DescripProd + "',0 ) ");
            }

            if(ValueNewInvFis != ""){
                new AsyncProds().execute();
            }else{
                Intent InventaryList = new Intent(ConfirmCountActivity.this,NewInventoryMovsActivity.class);
                startActivity(InventaryList);
            }

        }else{
            Toast.makeText(ConfirmCountActivity.this, "Este producto no existe, primero debes darlo de alta ", Toast.LENGTH_SHORT).show();
        }
        link.close();
    }
























    private class AsyncProds extends AsyncTask<Integer,Float,Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setProgress(0);
            dialog.setMax(100);
            dialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Intent ProdsToCount = new Intent(ConfirmCountActivity.this,NewInventoryMovsActivity.class);
            startActivity(ProdsToCount);
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

            Cursor Query_InvProdsTosend = dbR.rawQuery("select MovId,Quantity,CodeProd,Descrip,Export from PartMovsInv where export = 0 and MovId = '"+ IdMov +"' and CodeProd = '"+Product+"' ",null);
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

                    final String UrlRegisterInventary = "http://"+ServerIp+"/INTEMWS/RegisterInventary.php?articulo="+ProdToCount+"&qty="+QtyToCount+"&numinv="+NumInvInteger+"&instantinv="+InvFisInstant+"";

                    //final Integer finalNumInvInteger = NumInvInteger;
                    JsonObjectRequest RequestUpdate = new JsonObjectRequest(Request.Method.GET, UrlRegisterInventary, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            JSONArray json = response.optJSONArray("invent");
                            JSONObject jsonObject = null;
                            try {
                                jsonObject = json.getJSONObject(0);
                                String result = jsonObject.optString("result");
                                if(result.equals("noopen")){
                                    Toast.makeText(ConfirmCountActivity.this, "Este tipo de conteo requiere un inventario abierto", Toast.LENGTH_LONG).show();
                                }else if(result.equals("noinv")){
                                    Toast.makeText(ConfirmCountActivity.this, "No existe el inventario que estas indicando o no tiene el status necesario", Toast.LENGTH_LONG).show();
                                }else {
                                    SQLiteDatabase dbW = link.getWritableDatabase();
                                    dbW.execSQL("UPDATE PartMovsInv set Export = 1 where CodeProd = '" + result + "' ");
                                    Toast.makeText(ConfirmCountActivity.this, "Registrado con exito en el servidor", Toast.LENGTH_SHORT).show();
                                }
                                Intent InventaryList = new Intent(ConfirmCountActivity.this,NewInventoryMovsActivity.class);
                                startActivity(InventaryList);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(ConfirmCountActivity.this, "No se pudo registrar el conteo en el servidor, valida el numero de inventario o la conexion " , Toast.LENGTH_LONG).show();
                            //Intent InventaryList = new Intent(ConfirmCountActivity.this,NewInventoryMovsActivity.class);
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

                }while(Query_InvProdsTosend.moveToNext());
            }

            //========================= Fin del Insert de salida =====================================
            return null;
        }
    }






}
