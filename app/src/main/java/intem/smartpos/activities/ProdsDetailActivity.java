package intem.smartpos.activities;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

import java.text.DecimalFormat;

import intem.smartpos.R;
import intem.smartpos.database.connection;

public class ProdsDetailActivity extends AppCompatActivity {
    public String ProdCode,qty_presentations;
    public String Articulo;
    public String Descrip;
    public Double Cost;
    public Double PriceList;
    public Double Price2;
    public String Qty2;
    public Double Price3;
    public String Qty3;
    public Double Price4;
    public String Qty4;
    public Double Price5;
    public String Qty5;
    public Double Price6;
    public String Qty6;
    public Double Price7;
    public String Qty7;
    public Double Price8;
    public String Qty8;
    public Double Price9;
    public String Qty9;
    public Double OfferPercentage;
    public RequestQueue request;
    public String imageName;
    public ImageView Im_Prod;
    private String ServerIp;
    private connection link;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prods_detail);
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
            ProdCode = Prod.getString("ProdCode");
            FillDetails(ProdCode);
        }

    }


    private void FillDetails(String Code){
        SQLiteDatabase db = link.getReadableDatabase();
        Cursor c = db.rawQuery("Select id,Code,Description,ProdCategory,Brand,Tax,Price1,Price2,Price3,Price4,Price5,Price6,Price7,Price8,Price9,Cost,Exist,ExSucursal,Export,Offer,image,Qty2,Qty3,Qty4,Qty5,Qty6,Qty7,Qty8,Qty9 from Prods where id = '"+ Code +"' " , null);

        if (c.moveToFirst()) {
            Articulo = c.getString(1);
            Descrip = c.getString(2);
            PriceList = c.getDouble(6);
            Price2 = c.getDouble(7);
            Price3 = c.getDouble(8);
            Price4 = c.getDouble(9);
            Price5 = c.getDouble(10);
            Price6 = c.getDouble(11);
            Price7 = c.getDouble(12);
            Price8 = c.getDouble(13);
            Price9 = c.getDouble(14);
            Cost = c.getDouble(15);
            OfferPercentage = c.getDouble(19);
            imageName = c.getString(20);
            Qty2 = c.getString(21);
            Qty3 = c.getString(22);
            Qty4 = c.getString(23);
            Qty5 = c.getString(24);
            Qty6 = c.getString(25);
            Qty7 = c.getString(26);
            Qty8 = c.getString(27);
            Qty9 = c.getString(28);
        }else{
            Toast.makeText(this, "Sin registros", Toast.LENGTH_SHORT).show();
        }

        String urlImage = "http://"+ServerIp+"/INTEMWS/ImageProds/"+imageName;
        urlImage = urlImage.replace(" ","%20");
        Im_Prod = (ImageView) findViewById(R.id.DetailProd_ImageView);
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


        TextView offer = (TextView) findViewById(R.id.offer);
        TextView DetailProd_Code = (TextView) findViewById(R.id.DetailProd_Code);
        TextView DetailProd_Descrip = (TextView) findViewById(R.id.DetailProd_Descrip);
        TextView DetailProd_Cost = (TextView) findViewById(R.id.DetailProd_Cost);
        TextView DetailProd_PriceList = (TextView) findViewById(R.id.DetailProd_PriceList);
        TextView DetailProd_2 = (TextView) findViewById(R.id.DetailProd_Price2);
        TextView DetailProd_3 = (TextView) findViewById(R.id.DetailProd_Price3);
        TextView DetailProd_4 = (TextView) findViewById(R.id.DetailProd_Price4);
        TextView DetailProd_5 = (TextView) findViewById(R.id.DetailProd_Price5);
        TextView DetailProd_6 = (TextView) findViewById(R.id.DetailProd_Price6);
        TextView DetailProd_7 = (TextView) findViewById(R.id.DetailProd_Price7);
        TextView DetailProd_8 = (TextView) findViewById(R.id.DetailProd_Price8);
        TextView DetailProd_9 = (TextView) findViewById(R.id.DetailProd_Price9);
        TextView qty_presentations = (TextView) findViewById(R.id.qty_presentations);
        TextView DetPrCaja= (TextView) findViewById(R.id.Box_presentations);

        DecimalFormat formateador = new DecimalFormat("###,###,###,###.00");

        if(OfferPercentage>0){
            offer.setText("En Oferta! " + OfferPercentage + "%" );
        }
        else
        {
            offer.setText("");
        }


        DetailProd_Code.setText("Codigo del producto: " + Articulo );
        DetailProd_Descrip.setText(Descrip);

        Cursor UserLevel = db.rawQuery("SELECT * From Users where Loged = 1 and Level = 'Administrador' ",null);
        String Level = "";

        if(UserLevel.moveToFirst()){
            Level = "Administrador";
        }else{
            Level = "Usuario";
        }

        String Cost_u = "";
        if( Level == "Administrador"){
            Cost_u = String.valueOf(formateador.format(Cost));
        }

        DetailProd_Cost.setText("Costo Ultimo: $" + Cost_u);

        String PricePublic = "Precio de lista: $" + String.valueOf(formateador.format(PriceList));
        DetailProd_PriceList.setText(PricePublic);

        if(Price2>0){
            String PriceII = "$" + String.valueOf(formateador.format (Price2));
            DetailProd_2.setText("A partir de " + Qty2 + " Pzas : " + PriceII);
        }else{
            DetailProd_2.setText("");
        }

        if(Price3>0){
            String PriceIII = "$" + String.valueOf(formateador.format (Price3));
            DetailProd_3.setText("A partir de " + Qty3 + " Pzas : " + PriceIII);
        }else{
            DetailProd_3.setText("");
        }

        if(Price4>0){
            String PriceIV = "$" + String.valueOf(formateador.format (Price4));
            DetailProd_4.setText("A partir de " + Qty4 + " Pzas : " + PriceIV);
        }else{
            DetailProd_4.setText("");
        }

        if(Price5>0){
            String PriceV = "$" + String.valueOf(formateador.format (Price5));
            DetailProd_5.setText("A partir de " + Qty5 + " Pzas : " + PriceV);
        }else{
            DetailProd_5.setText("");
        }

        if(Price6>0){
            String PriceVI = "$" + String.valueOf(formateador.format (Price6));
            DetailProd_6.setText("A partir de " + Qty6 + " Pzas : " + PriceVI);
        }else{
            DetailProd_6.setText("");
        }

        if(Price7>0){
            String PriceVII = "$" + String.valueOf(formateador.format (Price7));
            DetailProd_7.setText("A partir de " + Qty7 + " Pzas : " + PriceVII);
        }else{
            DetailProd_7.setText("");
        }

        if(Price8>0){
            String PriceVIII = "$" + String.valueOf(formateador.format (Price8));
            DetailProd_8.setText("A partir de " + Qty8 + " Pzas : " + PriceVIII);
        }else{
            DetailProd_8.setText("");
        }

        if(Price9>0){
            String PriceIX = "$" + String.valueOf(formateador.format (Price9));
            DetailProd_9.setText("A partir de " + Qty9 + " Pzas : " + PriceIX);
        }else{
            DetailProd_9.setText("");
        }





        Cursor QtyPr = db.rawQuery("select CodePr,DescPr,QtyPr,PricePr from Presentations where CodeProd = '"+ Articulo +"' and QtyPr = 1 ",null);

        if(QtyPr.moveToFirst()){
            qty_presentations.setText("Codigo Adicional: " + QtyPr.getString(0) );
        }else{
            qty_presentations.setText("");
        }




        Cursor PrCaja = db.rawQuery("select CodePr,DescPr,QtyPr,PricePr,QtyPr * PricePr As PriceBox from Presentations where CodeProd = '"+ Articulo.trim() +"' and QtyPr > 1 ",null);

        if(PrCaja.moveToFirst()){
            String ImportBox = String.valueOf(formateador.format(PrCaja.getDouble(4)));
            DetPrCaja.setText("" + PrCaja.getString(0) + " " + PrCaja.getString(1) + " Caja de " + PrCaja.getDouble(2) + " Pzas, " + " $" +ImportBox );
        }else{
            DetPrCaja.setText("");
        }

/*
        Cursor Presentations = db.rawQuery("select CodePr,CodeProd,QtyPr,PricePr,QtyPr * PricePr As PriceBox from Presentations where CodePr = '7802800427437' ",null);

        if(Presentations.moveToFirst()){
            Toast.makeText(this, "-" + Articulo.trim() + "-" , Toast.LENGTH_LONG).show();
            Toast.makeText(this, "Clave: -" + Presentations.getString(0) + "- Articulo: -" + Presentations.getString(1) + "- Cantidad: -" + Presentations.getString(2) +"- ", Toast.LENGTH_LONG).show();
        }
*/




    }

}
