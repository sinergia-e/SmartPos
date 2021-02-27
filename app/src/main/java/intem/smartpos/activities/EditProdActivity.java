package intem.smartpos.activities;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

public class EditProdActivity extends AppCompatActivity {

    private connection link;
    public String ProdId;
    public String Descrip;
    public Double Cost;
    public Double PriceList;
    public Double Price2;
    public Double Price3;
    public Double Price4;
    public Double Price5;
    public Double Price6;
    public Double Price7;
    public Double Price8;
    public Double Price9;
    public Double OfferPercentage;
    public RequestQueue request;
    public String imageName;
    public ImageView Im_Prod;
    private String ServerIp;


    private EditText Description;
    private EditText Costo;
    private EditText Precio1;
    private EditText Precio2;
    private EditText Precio3;
    private EditText Precio4;
    private EditText Precio5;
    private EditText Precio6;
    private EditText Precio7;
    private EditText Precio8;
    private EditText Precio9;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_prod);
        link = new connection(getApplicationContext(), "MyBusinessAndroid", null, 1);

        SQLiteDatabase dbR = link.getReadableDatabase();
        Cursor cConfig = dbR.rawQuery("SELECT ServerIP,OnlyExist,DataSize,Terminal,SendProds,ImportSucEx from AppConfig ",null);

        if(cConfig.moveToFirst()){
            ServerIp = cConfig.getString(0);
        }else{
            Toast.makeText(this, "Falta la configuracion del servidor, corrigelo", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this,AppConfigActivity.class);
            startActivity(intent);
        }


        Bundle Prod = getIntent().getExtras();
        if (Prod != null) {
            ProdId = Prod.getString("ProdCode");
            ProdInfo();
        }

        Button SaveProd = (Button) findViewById(R.id.EditProd_ButtonSave);
        Button Cancel = (Button) findViewById(R.id.EditProd_Cancel);


        SaveProd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveData();
            }
        });

        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent GoMenu = new Intent(getApplicationContext(),MainActivity.class);
            }
        });



        Description = (EditText) findViewById(R.id.EditProd_Description);
        Costo = (EditText) findViewById(R.id.EditProd_Costo);
        Precio1 = (EditText) findViewById(R.id.EditProd_Precio1);
        Precio2 = (EditText) findViewById(R.id.EditProd_Precio2);
        Precio3 = (EditText) findViewById(R.id.EditProd_Precio3);
        Precio4 = (EditText) findViewById(R.id.EditProd_Precio4);
        Precio5 = (EditText) findViewById(R.id.EditProd_Precio5);
        Precio6 = (EditText) findViewById(R.id.EditProd_Precio6);
        Precio7 = (EditText) findViewById(R.id.EditProd_Precio7);
        Precio8 = (EditText) findViewById(R.id.EditProd_Precio8);
        Precio9 = (EditText) findViewById(R.id.EditProd_Precio9);

    }


    private void ProdInfo(){

        SQLiteDatabase db = link.getReadableDatabase();
        Cursor c = db.rawQuery("Select id,Code,Description,ProdCategory,Brand,Tax,Price1,Price2,Price3,Price4,Price5,Price6,Price7,Price8,Price9,Cost,Exist,ExSucursal,Export,Offer,image from Prods where id = '"+ ProdId +"' " , null);

        if (c.moveToFirst()) {

            Description = (EditText) findViewById(R.id.EditProd_Description);
            Costo = (EditText) findViewById(R.id.EditProd_Costo);
            Precio1 = (EditText) findViewById(R.id.EditProd_Precio1);
            Precio2 = (EditText) findViewById(R.id.EditProd_Precio2);
            Precio3 = (EditText) findViewById(R.id.EditProd_Precio3);
            Precio4 = (EditText) findViewById(R.id.EditProd_Precio4);
            Precio5 = (EditText) findViewById(R.id.EditProd_Precio5);
            Precio6 = (EditText) findViewById(R.id.EditProd_Precio6);
            Precio7 = (EditText) findViewById(R.id.EditProd_Precio7);
            Precio8 = (EditText) findViewById(R.id.EditProd_Precio8);
            Precio9 = (EditText) findViewById(R.id.EditProd_Precio9);

            Descrip = c.getString(2);
            Description.setText(Descrip);

            PriceList = c.getDouble(6);
            Precio1.setText(String.valueOf(PriceList));

            Price2 = c.getDouble(7);
            Precio2.setText(String.valueOf(Price2));

            Price3 = c.getDouble(8);
            Precio3.setText(String.valueOf(Price3));

            Price4 = c.getDouble(9);
            Precio4.setText(String.valueOf(Price4));

            Price5 = c.getDouble(10);
            Precio5.setText(String.valueOf(Price5));

            Price6 = c.getDouble(11);
            Precio6.setText(String.valueOf(Price6));

            Price7 = c.getDouble(12);
            Precio7.setText(String.valueOf(Price7));

            Price8 = c.getDouble(13);
            Precio8.setText(String.valueOf(Price8));

            Price9 = c.getDouble(14);
            Precio9.setText(String.valueOf(Price9));

            Cost = c.getDouble(15);
            Costo.setText(String.valueOf(Cost));

            OfferPercentage = c.getDouble(19);
            imageName = c.getString(20);

            String urlImage = "http://"+ ServerIp +"/INTEMWS/ImageProds/" + imageName;
            urlImage = urlImage.replace(" ","%20");
            Im_Prod = (ImageView) findViewById(R.id.EditProd_ImageProd);
            request = Volley.newRequestQueue(getApplicationContext());

            ImageRequest imageRequest = new ImageRequest(urlImage, new Response.Listener<Bitmap>() {
                @Override
                public void onResponse(Bitmap response) {
                    Im_Prod.setImageBitmap(response);
                }
            }, 0, 500, ImageView.ScaleType.CENTER, null, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //Toast.makeText(ProdsDetailActivity.this, "No se logro cargar la imagen", Toast.LENGTH_SHORT).show();
                }
            });

            request.add(imageRequest);

        }else{
            Toast.makeText(this, "No hay informacion del codigo ingresado", Toast.LENGTH_SHORT).show();
        }
        link.close();
    }




    private void SaveData(){

        String DescriptionProd = Description.getText().toString();
        Double CostProd = Double.valueOf(Costo.getText().toString());
        Double Price1Prod = Double.valueOf(Precio1.getText().toString());
        Double Price2Prod = Double.valueOf(Precio2.getText().toString());
        Double Price3Prod = Double.valueOf(Precio3.getText().toString());
        Double Price4Prod = Double.valueOf(Precio4.getText().toString());
        Double Price5Prod = Double.valueOf(Precio5.getText().toString());
        Double Price6Prod = Double.valueOf(Precio6.getText().toString());
        Double Price7Prod = Double.valueOf(Precio7.getText().toString());
        Double Price8Prod = Double.valueOf(Precio8.getText().toString());
        Double Price9Prod = Double.valueOf(Precio9.getText().toString());

        SQLiteDatabase dbW = link.getWritableDatabase();
        dbW.execSQL("UPDATE prods set Description = '"+ DescriptionProd +"',Cost = '"+ CostProd +"',Price1='"+ Price1Prod +"',Price2='"+ Price2Prod +"',Price3='"+ Price3Prod +"',Price4='"+ Price4Prod +"',Price5 = '"+ Price5Prod +"',Price6 = '"+ Price6Prod +"',Price7 = '"+ Price7Prod +"',Price8='"+ Price8Prod +"',Price9='"+ Price9Prod +"',Export = 1 WHERE id = '"+ ProdId +"'   ");
        link.close();

        Intent intent = new Intent(EditProdActivity.this,ListProdsActivity.class);
        startActivity(intent);

        Toast.makeText(this, "Se registraron los cambios en " + ProdId + "  " + DescriptionProd, Toast.LENGTH_LONG).show();

        SendData();

    }







    private void SendData(){

        String SendArticulo = "";
        String SendDescrip = "";
        String SendCost_u = "";
        String SendPrecio1 = "";
        String SendPrecio2 = "";
        String SendPrecio3 = "";
        String SendPrecio4 = "";
        String SendPrecio5 = "";
        String SendPrecio6 = "";
        String SendPrecio7 = "";
        String SendPrecio8 = "";
        String SendPrecio9 = "";

        //========================= Obtener Informacion del producto a actualizar =====================================
        final SQLiteDatabase dbR = link.getReadableDatabase();
        Cursor c = dbR.rawQuery("Select id,Code,Description,ProdCategory,Brand,Tax,Price1,Price2,Price3,Price4,Price5,Price6,Price7,Price8,Price9,Cost,Exist,ExSucursal,Export,Offer,image from Prods where id = '"+ ProdId +"' " , null);

        if(c.moveToFirst()){
            SendArticulo =  c.getString(1);
            SendDescrip = c.getString(2);
            SendDescrip = SendDescrip.replace(" ","%20");
            SendCost_u  = c.getString(15);
            SendPrecio1 = c.getString(6);
            SendPrecio2 = c.getString(7);
            SendPrecio3 = c.getString(8);
            SendPrecio4 = c.getString(9);
            SendPrecio5 = c.getString(10);
            SendPrecio6 = c.getString(11);
            SendPrecio7 = c.getString(12);
            SendPrecio8 = c.getString(13);
            SendPrecio9 = c.getString(14);
        }

        String UrlSendData = "http://"+ServerIp+"/INTEMWS/SaveProds.php?articulo="+ SendArticulo +"&descrip="+SendDescrip+"&costo_u="+ SendCost_u +"&precio1="+ SendPrecio1+"&precio2="+ SendPrecio2+"&precio3="+ SendPrecio3+"&precio4="+ SendPrecio4+"&precio5="+ SendPrecio5+"&precio6="+ SendPrecio6+"&precio7="+ SendPrecio7+"&precio8="+ SendPrecio8+"&precio9="+ SendPrecio9+" ";
        //Toast.makeText(this, "" + UrlSendData, Toast.LENGTH_LONG).show();


        final String finalSendArticulo = SendArticulo;
        final String finalSendDescrip = SendDescrip;
        JsonObjectRequest RequestUpdate = new JsonObjectRequest(Request.Method.GET, UrlSendData, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String UpdateStatus = "";

                JSONArray json = response.optJSONArray("Prod");
                JSONObject jsonObject = null;

                    try {
                        jsonObject = json.getJSONObject(0);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                String UpdateSt = jsonObject.optString("estado");

                if (UpdateSt == "Actualizado"){
                    dbR.execSQL("update prods set Export = 0 where Code = '"+ finalSendArticulo +"' ");
                    Toast.makeText(EditProdActivity.this, finalSendDescrip + " se actualizo en el servidor ", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(EditProdActivity.this, "Se queda pendiente actualizar en el servidor", Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue UpdateProd = Volley.newRequestQueue(getApplicationContext());
        UpdateProd.add(RequestUpdate);
    }

}
