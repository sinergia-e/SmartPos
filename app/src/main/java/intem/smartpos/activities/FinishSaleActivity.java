package intem.smartpos.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
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
import intem.smartpos.database.connection;

public class FinishSaleActivity extends AppCompatActivity {

    private Spinner SpMethods;
    private EditText AmountPay;
    private ImageButton Return,FinishSale;
    private ArrayList MethodsList;
    public String ImportSl,IDSale,ServerIp,methodSelect,UrlSendSale,SaleInServer,Terminal,UserLoged,Warehouse,TypeSale,ClientSend,ImportSend,Company,AdressCompany,PhoneCompany,ObservToSend;
    public Double SaleImport;
    public Integer PendingSale,PartsRegister,EnabledPrintTk,Remision;
    private connection link;

    private ProgressDialog ProgressSendSale;

    private void BindUI(){
        SpMethods = (Spinner) findViewById(R.id.finish_sale_spinner_pay_methods);
        AmountPay = (EditText) findViewById(R.id.finish_sale_amountpay);
        FinishSale = (ImageButton) findViewById(R.id.finish_sale_accept);
        Return = (ImageButton) findViewById(R.id.finish_sale_return);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish_sale);

        BindUI();

        ProgressSendSale = new ProgressDialog(this);
        ProgressSendSale.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        ProgressSendSale.setCancelable(false);

        link = new connection(getApplicationContext(), "MyBusinessAndroid", null, 1);
        //****************** Recupera Datos del server ***************************
        SQLiteDatabase dbR = link.getReadableDatabase();
        Cursor cIp = dbR.rawQuery("SELECT ServerIP,PriceQty,Terminal,PendingSale,Warehouse,Enterprise,PrintTkSale,Adress,Phone,Remision from AppConfig ",null);
        if(cIp.moveToFirst()){
            ServerIp = cIp.getString(0);
            Terminal = cIp.getString(2);
            PendingSale = cIp.getInt(3);
            Warehouse = cIp.getString(4);
            EnabledPrintTk = cIp.getInt(6);
            Company = cIp.getString(5);
            AdressCompany = cIp.getString(7);
            PhoneCompany = cIp.getString(8);
            Remision = cIp.getInt(9);
        }
        //****************** Recupera Datos del server ***************************

        Cursor UserLogQuery = dbR.rawQuery("select Nick from Users Where Loged = 1 ",null);

        if(UserLogQuery.moveToFirst()){
            UserLoged = UserLogQuery.getString(0);
           // Toast.makeText(FinishSaleActivity.this, "Usuario logeado = " + UserLoged, Toast.LENGTH_SHORT).show();
        }





        //****************** Recupera Datos del Bundle ***************************
        Bundle SaleID = getIntent().getExtras();
        if (SaleID != null) {
            IDSale = SaleID.getString("NumSale");
            Cursor SaleData = dbR.rawQuery("select sum(  (Qty*Price) * (100 - Discount)/100 ) from SalesParts where SaleID = " + IDSale,null);
            if(SaleData.moveToFirst()){
                SaleImport = SaleData.getDouble(0);

                java.text.DecimalFormat formateador = new java.text.DecimalFormat("###,###,###,###.00");
                String AmountSale = formateador.format(SaleImport);

                AmountPay.setText(String.valueOf(AmountSale));
            }
        }




        MethodsList = new ArrayList();
        MethodsList.add("Efectivo");
        MethodsList.add("Tarjeta");
        MethodsList.add("Transferencia");
        MethodsList.add("Cheque");
        MethodsList.add("Otros");

        ArrayAdapter<CharSequence> methodsSp = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, MethodsList);
        SpMethods.setAdapter(methodsSp);
        SpMethods.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                methodSelect = MethodsList.get(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });











    FinishSale.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ConfirmSale();
            //Test();
        }
    });











    Return.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent goPos = new Intent(getApplicationContext(),PosActivity.class);
            startActivity(goPos);
        }
    });



    }






    private void ConfirmSale(){
        BindUI();
        String ImportPay = AmountPay.getText().toString();

        ImportPay = ImportPay.replace(",","");
        Double DbImportPay = Double.valueOf(ImportPay);

        SQLiteDatabase db = link.getWritableDatabase();


        Integer Consecutive = 0;

        Cursor ConsecC = db.rawQuery("select max(Consecutive) as Consecutive From Sales ",null);
        if(ConsecC.moveToFirst()){
            Consecutive = ConsecC.getInt(0);
        }
        Consecutive = Consecutive + 1;


        if(Math.floor(SaleImport )> DbImportPay){
            Toast.makeText(this, "El pago que indicaste no cubre la cuenta", Toast.LENGTH_SHORT).show();
        }else{

            db.execSQL("update Sales set PayMethod = '" + methodSelect +"',Consecutive='"+Consecutive+"',Status='Cerrada',ImportPay = '"+ ImportPay +"' where id = '"+ IDSale +"' ");

            Cursor PartsToSale = db.rawQuery("select ProdCode,Qty from SalesParts where SaleID = '"+ IDSale +"'  ",null);
            if(PartsToSale.moveToFirst()){
                do{
                    Double QtyPart = PartsToSale.getDouble(1);
                    String CodeProduct = PartsToSale.getString(0);
                    db.execSQL("update Prods set Exist = Exist - '"+ QtyPart +"' where Code = '"+ CodeProduct +"' ");
                }while (PartsToSale.moveToNext());
            }


            java.text.DecimalFormat formateador = new java.text.DecimalFormat("###,###,###,###.00");
            Double Diff = DbImportPay - SaleImport;

            String StrDiff = formateador.format(Diff);

            Toast.makeText(this, "El cambio del Ticket " + Consecutive + " Son $" + StrDiff, Toast.LENGTH_SHORT).show();
            link.close();


            if(PendingSale==1){
                TypeSale = "PE";
            }else{
                TypeSale = "CO";
            }



            new SyncSale().execute(TypeSale);
        }
    }


























    /*   ===========================================     Envia venta pendiente    ==========================================*/
    public class SyncSale extends AsyncTask<String,Integer,String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected String doInBackground(String... strings) {

            SQLiteDatabase db = link.getReadableDatabase();
            Cursor SaleData = db.rawQuery("select * from Sales where ID = " + IDSale,null);

            if (SaleData.moveToFirst()){
                ClientSend = SaleData.getString(2);
                ImportSend = SaleData.getString(6);
                ObservToSend = SaleData.getString(12);
            }

            final JSONObject Venta = new JSONObject();
            JSONObject Partidas = new JSONObject();
            JSONObject SecurePart = new JSONObject();

            try {
                Venta.put("AndroidID",IDSale);
                Venta.put("Client",ClientSend);
                Venta.put("Terminal",Terminal);
                Venta.put("User",UserLoged);
                Venta.put("Status","CO");
                Venta.put("Import",ImportSend);
                Venta.put("Warehouse",Warehouse);
                Venta.put("Remision",Remision);
                Venta.put("Observ","" + ObservToSend);

                SecurePart.put("Codigo","DeleteME");
                SecurePart.put("Qty","0");
                SecurePart.put("Price","0");
                SecurePart.put("Discount","0");
                SecurePart.put("PrClave","");
                SecurePart.put("PrDescrip","");
                SecurePart.put("PrQty","");
                SecurePart.put("weight","");
                Venta.accumulate("Partidas",SecurePart);

                Cursor SaleParts = db.rawQuery("select SalesParts.ProdCode,SalesParts.Qty,SalesParts.Price,SalesParts.Discount,SalesParts.PrPrice,SalesParts.PrDescrip,SalesParts.PrQty,SalesParts.weight from SalesParts where SaleID = '"+ IDSale +"' ",null);
                if(SaleParts.moveToFirst()){
                    do{

                        String Codigo = SaleParts.getString(0);
                        String Cantidad = SaleParts.getString(1);
                        String PartPrice = SaleParts.getString(2);
                        String PartDiscount = SaleParts.getString(3);
                        String PartClavePR = "";
                        String PartDescripPR = SaleParts.getString(5);
                        String PartQtyPr = SaleParts.getString(6);
                        String PartWeight = SaleParts.getString(7);


                        Cursor DataAdd = db.rawQuery("select CodePr from Presentations where DescPr = '"+SaleParts.getString(5)+"' ",null);

                        if(DataAdd.moveToFirst()){
                            PartClavePR = DataAdd.getString(0);
                        }else{
                            PartClavePR = "";
                        }


                        Partidas.put("Codigo",Codigo);
                        Partidas.put("Qty",Cantidad);
                        Partidas.put("Price",PartPrice);
                        Partidas.put("Discount",PartDiscount);
                        Partidas.put("PrClave",PartClavePR);
                        Partidas.put("PrDescrip",PartDescripPR);
                        Partidas.put("PrQty",PartQtyPr);
                        Partidas.put("weigth",PartWeight);
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
                    CloseOp(IDRecover);
                    //Toast.makeText(FinishSaleActivity.this, "" + Venta.toString(), Toast.LENGTH_SHORT).show();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //Toast.makeText(FinishSaleActivity.this, "" + Venta.toString(), Toast.LENGTH_SHORT).show();
                    ErrorOp("La venta se queda en el dispositivo, no se pudo enviar al servidor" );
                }
            });
            RequestQueue Sync = Volley.newRequestQueue(getApplicationContext());
            Sync.add(SyncSale);
            return null;
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }

    }






/* =================================    Fin de envio de venta ===================================*/

    public void CloseOp(final String cadena) {

        SQLiteDatabase db = link.getWritableDatabase();
        db.execSQL("update Sales set Consecutive='"+ cadena +"',Export=1 where id = '"+ IDSale +"' ");

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setMessage("La venta " + cadena + " esta cerrada en el servidor");
        dialogBuilder.setCancelable(true).setTitle("Estado de la sincronizacion");
        dialogBuilder.setNeutralButton("Listo,Siguiente Venta", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent goPos = new Intent(getApplicationContext(),PosActivity.class);
                startActivity(goPos);

                if(EnabledPrintTk==1){
                    printTk(Integer.valueOf(IDSale));
                }

            }
        });
        //dialogBuilder.create().show();

        Intent goPos = new Intent(getApplicationContext(),PosActivity.class);
        startActivity(goPos);
        printTk(Integer.valueOf(IDSale));
    }



    public void ErrorOp(String cadena) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setMessage(cadena);
        dialogBuilder.setCancelable(true).setTitle("Estado de la sincronizacion");
        dialogBuilder.setNeutralButton("Ok , siguiente venta", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent goPos = new Intent(getApplicationContext(),PosActivity.class);
                startActivity(goPos);
                printTk(Integer.valueOf(IDSale));
            }
        });
        //dialogBuilder.create().show();
        Intent goPos = new Intent(getApplicationContext(),PosActivity.class);
        startActivity(goPos);
        printTk(Integer.valueOf(IDSale));

    }























    private void printTk_origina(Integer Sale){

        SQLiteDatabase dbR = link.getReadableDatabase();
        Cursor cSale = dbR.rawQuery("SELECT Sales.id,Clients.Name,Sales.import,Sales.Date FROM Sales INNER JOIN Clients ON Sales.Client = Clients.CodeMyBusiness where Sales.id = '"+Sale+"' " ,null);
        Cursor cSaleParts = dbR.rawQuery("SELECT SalesParts.Qty,SalesParts.ProdDescrip,SalesParts.Price from SalesParts where SaleID = " + Sale,null);

        String PrintCont = "";

        String S_Client = "";
        Double S_Import = 0.0;
        String S_Date = "";
        String S_Status = "";

        if(cSale.moveToFirst()){
            do {
                S_Client = cSale.getString(1);
                S_Import = cSale.getDouble(2);
                S_Date = cSale.getString(3);
                S_Status = "Venta " + IDSale;
            }while (cSale.moveToNext());
        }


        PrintCont = "$bigh$"+Company+"$big$\n"+AdressCompany+"\n\nTelefono:"+ PhoneCompany +"\nFecha:"+S_Date+"\n\n\n" +
                "Cliente: \n" + S_Client + "\nFolio: " + Sale + "\n\n"
                + "Productos :  \n\n=============================\n$small$";

        if(cSaleParts.moveToFirst()){
            do{
                String QtyProd = String.valueOf(cSaleParts.getDouble(0));
                String Descrip = cSaleParts.getString(1);
                String Price = cSaleParts.getString(2);
                String partImp = String.valueOf(cSaleParts.getDouble(0) * cSaleParts.getDouble(2));
                PrintCont = PrintCont + Descrip + "\n" + QtyProd + "  *  " + Price + " = " + partImp + "\n-  -  -  -  -  -  -  -  -  -  -\n";
            }while (cSaleParts.moveToNext());
        }

        java.text.DecimalFormat formateador = new java.text.DecimalFormat("###,###,###,###.00");
        String FinalImport = formateador.format(S_Import);

        PrintCont = PrintCont + "============================= \n\n$bigh$ Total : $ " + FinalImport;
        PrintCont = PrintCont + " \n\n\n $intro$ ";

        String dataToPrint = PrintCont;
        Intent intentPrint = new Intent();

        intentPrint.setAction(Intent.ACTION_SEND);
        intentPrint.putExtra(Intent.EXTRA_TEXT, dataToPrint);
        intentPrint.setType("text/plain");

        startActivity(intentPrint);

        link.close();
    }





    private void printTk(Integer Sale){

        SQLiteDatabase dbR = link.getReadableDatabase();
        Cursor cSale = dbR.rawQuery("SELECT Sales.id,Clients.Name,Sales.import,Sales.Date FROM Sales INNER JOIN Clients ON Sales.Client = Clients.CodeMyBusiness where Sales.id = '"+Sale+"' " ,null);
        Cursor cSaleParts = dbR.rawQuery("SELECT SalesParts.Qty,SalesParts.ProdDescrip,SalesParts.Price from SalesParts where SaleID = " + Sale,null);

        String PrintCont = "";

        String S_Client = "";
        Double S_Import = 0.0;
        String S_Date = "";
        String S_Status = "";

        if(cSale.moveToFirst()){
            do {
                S_Client = cSale.getString(1);
                S_Import = cSale.getDouble(2);
                S_Date = cSale.getString(3);
                S_Status = "Venta " + IDSale;
            }while (cSale.moveToNext());
        }


        PrintCont = "$bigh$        "+Company+"$big$\n\nCentro Joyero de Iguala\nCalle Obregon esq Reforma\nCol Centro CP 40000\nLocal 98 PA\nIguala de la Independencia \nGuerrero\n\n\nTelefono:"+ PhoneCompany +"\nWhatsApp:733 118 68 66\nFacebook:/JoyasAurium \nInstagram:@joyeria_aurium \nwww.aurium.com.mx \n\nFecha:"+S_Date+"\n\n\n" +
                "Cliente: \n" + S_Client + "\nFolio: " + Sale + "\n\n"
                + "Productos :  \n\n=============================\n$small$";

        if(cSaleParts.moveToFirst()){
            do{
                String QtyProd = String.valueOf(cSaleParts.getDouble(0));
                String Descrip = cSaleParts.getString(1);
                String Price = cSaleParts.getString(2);
                String partImp = String.valueOf(cSaleParts.getDouble(0) * cSaleParts.getDouble(2));
                PrintCont = PrintCont + Descrip + "\n" + QtyProd + "  *  " + Price + " = " + partImp + "\n-  -  -  -  -  -  -  -  -  -  -\n";
            }while (cSaleParts.moveToNext());
        }

        java.text.DecimalFormat formateador = new java.text.DecimalFormat("###,###,###,###.00");
        String FinalImport = formateador.format(S_Import);

        PrintCont = PrintCont + "============================= \n\n$bigh$ Total : $ " + FinalImport;
        PrintCont = PrintCont + " \n $intro$ ";
        PrintCont = PrintCont + " \n $small$No hay cambios ni devoluciones \n en broqueles ni articulos \n de bisuteria $intro$ \n";

        String dataToPrint = PrintCont;
        Intent intentPrint = new Intent();

        intentPrint.setAction(Intent.ACTION_SEND);
        intentPrint.putExtra(Intent.EXTRA_TEXT, dataToPrint);
        intentPrint.setType("text/plain");

        startActivity(intentPrint);

        link.close();
    }













/*

    public class SendSaleToServer extends AsyncTask<String,Integer,Void>{

        @Override
        protected Void doInBackground(final String... strings) {

            SQLiteDatabase db = link.getReadableDatabase();
            Cursor SaleData = db.rawQuery("select * from Sales where ID = " + IDSale,null);

            if (SaleData.moveToFirst()){
                String Client = SaleData.getString(2);
                UrlSendSale = "http://"+ServerIp+"/INTEMWS/SaveSales.php?AndroidId="+IDSale+"&Client="+Client+"&Terminal="+Terminal+"&User="+UserLoged+"&Vend="+UserLoged+"&Status="+strings+"&Import="+SaleData.getString(6)+"&Warehouse=1";
            }

            JsonObjectRequest SendSale = new JsonObjectRequest(Request.Method.GET, UrlSendSale, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {


                    JSONArray json = response.optJSONArray("InfoSale");
                    JSONObject jsonObject = null;

                    try {
                        jsonObject = json.getJSONObject(0);
                        String SaleInServer = jsonObject.optString("IdInServer");

                        SQLiteDatabase db = link.getWritableDatabase();
                        db.execSQL("update Sales set Status='En servidor Con id "+SaleInServer+"' where id = '"+ IDSale +"' ");

                        Cursor SaleParts = db.rawQuery("select * from SalesParts where SaleID = '"+ IDSale +"' ",null);
                        PartsRegister = 0;

                        if(SaleParts.moveToFirst()){
                            do{
                                String PartPrice = SaleParts.getString(5);
                                String PartDiscount = SaleParts.getString(7);

                                String UrlSendPart = "http://"+ServerIp+"/INTEMWS/SaveSales.php?IdSale="+SaleInServer+"&Product="+SaleParts.getString(2)+"&Qty="+SaleParts.getString(4)+"&Price="+PartPrice+"&Discount="+PartDiscount+"&Terminal="+Terminal+"&Vend="+UserLoged+"&Status="+strings+"&Warehouse="+Warehouse+"&IdAndroid="+IDSale;
                                JsonObjectRequest SendPart = new JsonObjectRequest(Request.Method.GET, UrlSendPart, null, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        PartsRegister = PartsRegister + 1;
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(FinishSaleActivity.this, "La Partida no se pudo registrar en el servidor" , Toast.LENGTH_SHORT).show();
                                    }
                                });

                                RequestQueue QueueParts = Volley.newRequestQueue(getApplicationContext());
                                QueueParts.add(SendPart);

                                try {
                                    Thread.sleep(100);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                            }while(SaleParts.moveToNext());

                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }



                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(FinishSaleActivity.this, "Sin respuesta " + UrlSendSale, Toast.LENGTH_SHORT).show();
                }
            });
            RequestQueue QueueSale = Volley.newRequestQueue(getApplicationContext());
            QueueSale.add(SendSale);
            return null;
        }





        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Intent goPos = new Intent(getApplicationContext(),PosActivity.class);
            startActivity(goPos);
        }

    }

*/





















}
