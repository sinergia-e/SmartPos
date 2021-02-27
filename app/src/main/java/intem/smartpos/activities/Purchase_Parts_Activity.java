package intem.smartpos.activities;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.InstrumentationInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.renderscript.ScriptGroup;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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

import java.text.DecimalFormat;
import java.util.ArrayList;

import intem.smartpos.R;
import intem.smartpos.adapters.prodsAdapter;
import intem.smartpos.adapters.purchasesAdapter;
import intem.smartpos.constructors.ReportProds_Constructor;
import intem.smartpos.constructors.prodsConstructor;
import intem.smartpos.constructors.purchasesConstructor;
import intem.smartpos.database.connection;

public class Purchase_Parts_Activity extends AppCompatActivity {

    private EditText InputFindProd,QtyProdToInsert;
    private ImageButton FindProd,ScanBar;
    private ListView ListProds;
    private TextView TotalQtyParts,ShowAmountPurchase;
    private Button ShowParts,ConfirmPurchase;
    private String ServerIp,PurchaseID,ProviderSend,ImportSend,Terminal,Warehouse,PrEnabled,SaleRem,Company,AdressCompany,PhoneCompany,UserLoged,AmountPurchase;
    private Integer PriceQty,PendingSale,EnabledPrintTk,Remision,LeaveDiscount;
    private ProgressDialog dialog;
    private ArrayList<prodsConstructor> ProdsArray;
    prodsConstructor prodsItem;
    connection link;

    private void BindUI(){

        InputFindProd = (EditText) findViewById(R.id.editText_PurchaseParts_FindProd);
        FindProd = (ImageButton) findViewById(R.id.imageButton_PurchaseParts_FindProd);
        ScanBar = (ImageButton) findViewById(R.id.imageButton_ScannerBar);
        ListProds = (ListView) findViewById(R.id.ListView_PurchaseParts_ListProds);
        TotalQtyParts = (TextView) findViewById(R.id.textView_PurchaseParts_QtyParts);
        ShowAmountPurchase = (TextView) findViewById(R.id.textView_PuchaseParts_Amount);
        ShowParts = (Button) findViewById(R.id.button_PurchaseParts_ShowParts);
        ConfirmPurchase = (Button) findViewById(R.id.button_PurchaseParts_ConfirmPurchase);
        QtyProdToInsert = (EditText) findViewById(R.id.editText_purchaseParts_QtyPart);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase__parts_);
        link = new connection(getApplicationContext(), "MyBusinessAndroid", null, 1);


        dialog = new ProgressDialog(this);
        dialog.setMessage("Estamos sincronizando compras...");
        dialog.setTitle("Progreso");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(false);


        //****************** Recupera Datos del server ***************************
        final SQLiteDatabase dbR = link.getReadableDatabase();
        String ConfigCondition = "";
        Cursor cIp = dbR.rawQuery("SELECT " +
                "ServerIP," + //0
                "PriceQty," + //1
                "Terminal," + //2
                "PendingSale," + //3
                "Warehouse," + //4
                "Remision," + //5
                "PrEnabled," + //6
                "PrintTkSale, " + //7
                "Enterprise, " + //8
                "Adress, " + //9
                "Phone, " + //10
                "Remision " + //10
                "from AppConfig " + ConfigCondition,null);

        if(cIp.moveToFirst()){
            ServerIp = cIp.getString(0);
            PriceQty = cIp.getInt(1);
            Terminal = cIp.getString(2);
            PendingSale = cIp.getInt(3);
            Warehouse = cIp.getString(4);
            SaleRem = cIp.getString(5);
            PrEnabled = cIp.getString(6);
            EnabledPrintTk = cIp.getInt(7);
            Company = cIp.getString(8);
            AdressCompany = cIp.getString(9);
            PhoneCompany = cIp.getString(10);
            Remision = cIp.getInt(11);
        }
        //****************** Recupera Datos del server ***************************
        Cursor UserLogQuery = dbR.rawQuery("select Nick,Discount from Users Where Loged = 1 ",null);

        if(UserLogQuery.moveToFirst()){
            UserLoged = UserLogQuery.getString(0);
            LeaveDiscount = UserLogQuery.getInt(1);
        }





        Bundle orderData = getIntent().getExtras();
        if (orderData != null) {
            PurchaseID = orderData.getString("Purchase");
            getTotal();
        }


        FillProds("");

        FindProd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String param = InputFindProd.getText().toString();
                String cCondicion = " where Description like '%"+ param +"%' or Code like '%"+ param +"%'  ";
                FillProds(cCondicion);
            }
        });




        ListProds.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BindUI();

                String CodeProd = ProdsArray.get(position).getCode();
                String Descrip = ProdsArray.get(position).getDescription();
                String PriceProd = String.valueOf(ProdsArray.get(position).getPrice());
                String DiscountPart = String.valueOf(ProdsArray.get(position).getOffer());
                String TaxPart = ProdsArray.get(position).getTax();

                String QtyProdToPurchase = QtyProdToInsert.getText().toString();

                if(Double.valueOf(QtyProdToPurchase ) > 0){
                    RegisterPart(CodeProd,QtyProdToPurchase,Descrip,DiscountPart,PriceProd,TaxPart);
                }else{
                    Toast.makeText(Purchase_Parts_Activity.this, "La cantidad a comprar debe ser mayor a cero", Toast.LENGTH_SHORT).show();
                }

            }
        });




        ConfirmPurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QuestionSync();
            }
        });

    }














    @Override
    public void onBackPressed() {
        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
        dialogo1.setTitle("Importante");
        dialogo1.setMessage("¿Dejas la compra pendiente?");
        dialogo1.setCancelable(false);
        dialogo1.setNegativeButton("No,Eliminalo", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
                DeletePurchase();
            }
        });
        dialogo1.setPositiveButton("Si,Guardalo", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            public void onClick(DialogInterface dialogo1, int id) {
                Intent intent = new Intent(Purchase_Parts_Activity.this,PurchasesListActivity.class);
                intent.putExtra("PurchaseID",PurchaseID);
                startActivity(intent);
            }
        });
        dialogo1.show();
    }















    private void FillProds(String cCondicion) {

        BindUI();
        SQLiteDatabase db = link.getReadableDatabase();
        Cursor c = db.rawQuery("Select id,Code,Description,ProdCategory,Brand,Tax,Price1,Exist,ExSucursal,Export,Offer from Prods " + cCondicion, null);

        ProdsArray = new ArrayList<prodsConstructor>();

        if (c.moveToFirst()) {
            do {
                prodsItem = new prodsConstructor(c.getInt(0), c.getString(1), c.getString(2), c.getString(3), c.getString(4), c.getString(5), c.getDouble(6), c.getString(7), c.getString(8),c.getInt(9),c.getDouble(10));
                ProdsArray.add(prodsItem);
            } while (c.moveToNext());
        } else {
            Toast.makeText(this, "No hay ningun producto", Toast.LENGTH_SHORT).show();
        }

        prodsAdapter adapter = new prodsAdapter(getApplicationContext(), ProdsArray);
        ListProds.setAdapter(adapter);
        link.close();
    }





    private void RegisterPart(String CodePart, String QtyPart, String DescripPart, String DiscountPart,String PriceProd,String TaxPart){

        SQLiteDatabase db = link.getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put("PurchaseId", PurchaseID);
        values.put("Prod", CodePart);
        values.put("Qty", QtyPart);
        values.put("DescripProd", DescripPart);
        values.put("Price", PriceProd);
        values.put("Discount", DiscountPart);
        values.put("Tax", TaxPart);
        Long idResult = db.insert("PurchasesPart", "id", values);

        getTotal();

    }








    private void getTotal(){

        BindUI();

        SQLiteDatabase db = link.getReadableDatabase();
        Cursor cTotal = db.rawQuery("Select sum(Qty * Price) As total from PurchasesPart where PurchaseId = '" + PurchaseID + "' ", null);

        Double Result = 0.00;
        if (cTotal.moveToFirst()) {
            Result =  cTotal.getDouble(0);
        }

        DecimalFormat formateador = new DecimalFormat("###,###,###,###.00");
        String TotalStr = formateador.format (Result);
        ShowAmountPurchase.setText("$ " + TotalStr);

        db.execSQL("UPDATE Purchases set Amount = '"+Result+"' where id = '"+PurchaseID+"' ");
        AmountPurchase = String.valueOf(Result);



        Cursor cc = db.rawQuery("Select sum(Qty) As total from PurchasesPart where PurchaseId = '" + PurchaseID + "' ", null);
        Double TtQtyParts = 0.00;
        if(cc.moveToFirst()){
            TtQtyParts = cc.getDouble(0);
        }



        TotalQtyParts.setText(String.valueOf(TtQtyParts) + " Productos en total");
    }




    public void DeletePurchase(){

        SQLiteDatabase db = link.getReadableDatabase();
        db.execSQL("DELETE from PurchasesPart where PurchaseId = '"+PurchaseID+"' ");
        db.execSQL("DELETE from Purchases where id = '"+PurchaseID+"' ");

        Toast.makeText(this, "La compra con Id " + PurchaseID + " fue eliminada", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Purchase_Parts_Activity.this,MenuActivity.class);
        startActivity(intent);


    }








    private void QuestionSync(){
        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(Purchase_Parts_Activity.this);
        dialogo1.setTitle("Importante");
        dialogo1.setMessage("Quieres enviar la compra al servidor?");
        dialogo1.setCancelable(false);
        dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {

            }
        });
        dialogo1.setPositiveButton("Adelante", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
                new Purchase_Parts_Activity.SendToServer().execute();
            }
        });
        dialogo1.show();
    }



























    /*   ===========================================     Envia Compra como pendiente    ==========================================*/
    public class SendToServer extends AsyncTask<String,Integer,String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected String doInBackground(String... strings) {

            SQLiteDatabase db = link.getReadableDatabase();
            Cursor SaleData = db.rawQuery("select * from Purchases where id = " + PurchaseID,null);

            if (SaleData.moveToFirst()){
                ProviderSend = SaleData.getString(1);
                ImportSend = SaleData.getString(2);
            }

            JSONObject Purchase = new JSONObject();
            JSONObject Partidas = new JSONObject();
            JSONObject SecurePart = new JSONObject();

            try {
                Purchase.put("AndroidID",PurchaseID);
                Purchase.put("Provider",ProviderSend);
                Purchase.put("Terminal",Terminal);
                Purchase.put("User",UserLoged);
                Purchase.put("Status","PE");
                Purchase.put("Amount", AmountPurchase);
                Purchase.put("Warehouse",Warehouse);

                SecurePart.put("Codigo","DeleteME");
                SecurePart.put("Qty","0");
                SecurePart.put("Price","0");
                SecurePart.put("Discount","0");
                SecurePart.put("PrClave","");
                SecurePart.put("PrDescrip","");
                SecurePart.put("PrQty","");
                Purchase.accumulate("Partidas",SecurePart);

                Cursor PurchaseParts = db.rawQuery("select Prod,Qty,Price,Discount from PurchasesPart where PurchaseId = '"+ PurchaseID +"' ",null);
                if(PurchaseParts.moveToFirst()){
                    do{

                        String Codigo = PurchaseParts.getString(0);
                        String Cantidad = PurchaseParts.getString(1);
                        String PartPrice = PurchaseParts.getString(2);
                        String PartDiscount = PurchaseParts.getString(3);

                        Partidas.put("Codigo",Codigo);
                        Partidas.put("Qty",Cantidad);
                        Partidas.put("Price",PartPrice);
                        Partidas.put("Discount",PartDiscount);
                        Purchase.accumulate("Partidas",Partidas);
                        Partidas = new JSONObject();

                    }while(PurchaseParts.moveToNext());
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            final String UrlSync = "http://"+ServerIp+"/INTEMWS/SavePurchase.php";
            JsonObjectRequest SendPurchase = new JsonObjectRequest(Request.Method.POST, UrlSync, Purchase, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    JSONArray ArrayResponse = response.optJSONArray("InfoPurchase");
                    JSONObject CompResponse = null;
                    CompResponse = ArrayResponse.optJSONObject(0);
                    String IDRecover = CompResponse.optString("IdInServer");
                    CloseOp(IDRecover);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    String cadena = "sin_enviar";
                    CloseOp("");
                }
            });
            RequestQueue Sync = Volley.newRequestQueue(getApplicationContext());
            Sync.add(SendPurchase);
            return null;
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Toast.makeText(Purchase_Parts_Activity.this, "Se sincroniza", Toast.LENGTH_SHORT).show();
        }

    }

/* =================================    Fin de envio de compra ===================================*/
















    public void CloseOp(final String cadena) {

        SQLiteDatabase db = link.getWritableDatabase();

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setMessage("La venta " + cadena + " esta registrada como pendiente en el servidor");
        dialogBuilder.setCancelable(false).setTitle("Estado de la sincronizacion");
        dialogBuilder.setNeutralButton("Listo,Siguiente Venta", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            Intent goPos = new Intent(getApplicationContext(),PosActivity.class);
            startActivity(goPos);
            }
        });
        dialogBuilder.create().show();
    }





















}
