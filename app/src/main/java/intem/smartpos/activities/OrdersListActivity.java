package intem.smartpos.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.icu.text.NumberFormat;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import java.util.Locale;

import intem.smartpos.R;
import intem.smartpos.adapters.orderAdapters;
import intem.smartpos.constructors.orderConstructor;
import intem.smartpos.database.connection;

public class OrdersListActivity extends AppCompatActivity {

    private ProgressDialog dialog;
    private ListView lV_Orders_OrdersList;
    private EditText eT_OrderList_FindClientOrder;
    private ImageButton imageButton_OrdersList_Sync,iB_OrderList_FindClientOrder,iB_OrderList_FilterOrderStatus;
    private Spinner spinnerStatus;
    private String StatusSelect,ClientSend,ImportSend,Terminal,UserLoged,Warehouse,ServerIp,cCondicion,IdOrder,Observations,Seller;
    private ArrayList<orderConstructor> OrdersList;
    private orderConstructor ItemOrder;
    private ArrayList StatusList;
    private Integer PriceByQty,LeaveDiscount;
    connection link;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders_list);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        dialog = new ProgressDialog(this);
        dialog.setMessage("Estamos sincronizando los pedidos...");
        dialog.setTitle("Progreso");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(false);

        link = new connection(getApplicationContext(), "MyBusinessAndroid", null, 1);
        lV_Orders_OrdersList = (ListView) findViewById(R.id.lV_OrdersList_List);
        eT_OrderList_FindClientOrder = (EditText) findViewById(R.id.eT_OrdersList_FindOrderClient);
        iB_OrderList_FindClientOrder = (ImageButton) findViewById(R.id.iB_OrdersList_FindOrder);
        iB_OrderList_FilterOrderStatus = (ImageButton) findViewById(R.id.iB_OrdersList_FilterStatus);
        spinnerStatus = (Spinner) findViewById(R.id.spinner_OrderList_Status);
        imageButton_OrdersList_Sync = (ImageButton) findViewById(R.id.imageButton_OrdersList_Sync);

        registerForContextMenu(lV_Orders_OrdersList);
        cCondicion = "";

        Bundle bundle = getIntent().getExtras();

        if(bundle != null){
            if(bundle.getString("order") != null){
                //Toast.makeText(this, "" + bundle.getString("order"), Toast.LENGTH_SHORT).show();
                cCondicion = " where Orders.id = '"+ bundle.getString("order") +"' ";
            }

            if(bundle.getString("client")!=null){
                cCondicion = " where Clients.name like '%"+bundle.getString("client")+"%' ";
            }
        }


        //****************** Recupera Datos del server ***************************
        SQLiteDatabase dbR = link.getReadableDatabase();
        Cursor cIp = dbR.rawQuery("SELECT ServerIP,PriceQty,Terminal,Warehouse,Seller from AppConfig ",null);
        if(cIp.moveToFirst()){
            ServerIp = cIp.getString(0);
            PriceByQty = cIp.getInt(1);
            Terminal = cIp.getString(2);
            Warehouse = cIp.getString(3);
            Seller = cIp.getString(4);
        }
        //****************** Recupera Datos del server ***************************
        //****************** Recupera Datos del server ***************************
        Cursor UserLogQuery = dbR.rawQuery("select Nick,Discount from Users Where Loged = 1 ",null);

        if(UserLogQuery.moveToFirst()){
            UserLoged = UserLogQuery.getString(0);
            LeaveDiscount = UserLogQuery.getInt(1);
        }


        getSpinnerStatus();

        ArrayAdapter<CharSequence> adaptSpinnerStatus = new ArrayAdapter<CharSequence>(getApplicationContext(),android.R.layout.simple_spinner_item,StatusList);
        spinnerStatus.setAdapter(adaptSpinnerStatus);

        iB_OrderList_FindClientOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String clientParameter = eT_OrderList_FindClientOrder.getText().toString();
                cCondicion = " where Clients.Name like '%"+ clientParameter +"%' ";
                FillOrders(cCondicion);
            }
        });


        spinnerStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                StatusSelect = StatusList.get(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        imageButton_OrdersList_Sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                QuestionSync();
            }
        });

        iB_OrderList_FilterOrderStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(StatusSelect.equals("Sincronizado")){
                    cCondicion = " where Orders.Status like '%Pedido en servidor%' ";
                }else if(StatusSelect.equals("Reenviar")){
                    cCondicion = " where Orders.Status = 'Reenviar' ";
                }else if (StatusSelect.equals("Pendiente")){
                    cCondicion = " where Orders.Status = 'Pendiente' ";
                }else{
                    cCondicion = " where Orders.Status = 'Reenviar' or Orders.Status = 'Pendiente'";
                }

                FillOrders(cCondicion);
            }
        });
        FillOrders(" Where Orders.Status = 'Reenviar' ");
    }







    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu_orders,menu);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        String Status = null;

        switch (item.getItemId()){
            case R.id.OpenOrder:
                QuestionOpenOrder(String.valueOf(OrdersList.get(info.position).getId()));
                return true;

            case R.id.DeleteOrder:
                QuestionDeleteOrder(String.valueOf(OrdersList.get(info.position).getId()));
                return true;

            case R.id.SendMail:
                int OrderId = OrdersList.get(info.position).getId();
                SendMail(OrderId);
                return true;

            case R.id.PrintOrder:
                OrderId = OrdersList.get(info.position).getId();
                printOrder(OrderId);

            default:
            return super.onContextItemSelected(item);
        }
    }





    private void FillOrders(String cCondicion){

        SQLiteDatabase db = link.getReadableDatabase();
        Cursor cOrders = db.rawQuery("SELECT Orders.id,Clients.Name,Orders.Date,Orders.Amount,Orders.Location,Orders.Status,Orders.IdInServer,Orders.ConsecInServer,Orders.Export FROM Orders INNER JOIN Clients ON Orders.Client = Clients.CodeMyBusiness " + cCondicion,null);
        OrdersList = new ArrayList<orderConstructor>();

        if(cOrders.moveToFirst()){
            do{
                ItemOrder = new orderConstructor(cOrders.getInt(0),cOrders.getString(1),cOrders.getString(2),cOrders.getDouble(3),cOrders.getString(4),cOrders.getString(5),cOrders.getInt(6),cOrders.getInt(7),cOrders.getInt(8));
                OrdersList.add(ItemOrder);
            }while(cOrders.moveToNext());
        }

        orderAdapters adapt = new orderAdapters(getApplicationContext(),OrdersList);
        lV_Orders_OrdersList.setAdapter(adapt);
        registerForContextMenu(lV_Orders_OrdersList);
    }






    @Override
    public void onBackPressed() {
        Intent goMenu = new Intent(OrdersListActivity.this,MenuActivity.class);
        startActivity(goMenu);
    }




    private void QuestionOpenOrder(final String IdOrder){
        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(OrdersListActivity.this);
        dialogo1.setTitle("Importante");
        dialogo1.setMessage("¿Quieres editar este pedido?");
        dialogo1.setCancelable(false);
        dialogo1.setNegativeButton("Lo pensare mejor", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {

            }
        });
        dialogo1.setPositiveButton("Si,Abrelo", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
            Intent intent = new Intent(OrdersListActivity.this,NewOrderActivity.class);
            intent.putExtra("order",IdOrder);
            intent.putExtra("open","true");
            startActivity(intent);
            }
        });
        dialogo1.show();
    }





    private void QuestionDeleteOrder(final String IdOrder){
        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(OrdersListActivity.this);
        dialogo1.setTitle("Importante");
        dialogo1.setMessage("¿Eliminamos este pedido?");
        dialogo1.setCancelable(false);
        dialogo1.setNegativeButton("Lo pensare mejor", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {

            }
        });
        dialogo1.setPositiveButton("Si,Eliminalo", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
                DeleteOrder(IdOrder);
            }
        });
        dialogo1.show();
    }




    private void QuestionSync(){
        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(OrdersListActivity.this);
        dialogo1.setTitle("Importante");
        dialogo1.setMessage("Se va a sincronizar con el servidor, puede tomar tiempo depende de cuantos pedidos se tengan que enviar ¿Comenzamos?");
        dialogo1.setCancelable(false);
        dialogo1.setNegativeButton("Lo pensare mejor", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {

            }
        });
        dialogo1.setPositiveButton("Adelante", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
                StarSync();
            }
        });
        dialogo1.show();
    }






    private void getSpinnerStatus() {
        SQLiteDatabase db = link.getReadableDatabase();
        Cursor cStatus = db.rawQuery("SELECT Distinct(Status) from Orders ", null);

        StatusList = new ArrayList();
        StatusList.add("Filtra por estado del pedido...");
        StatusList.add("Reenviar");
        StatusList.add("Pendiente");
        StatusList.add("Sincronizado");

        /*
        if (cStatus.moveToFirst()) {
            do {
                StatusList.add(cStatus.getString(0));
            } while (cStatus.moveToNext());
        }
        */

        link.close();
    }















    @RequiresApi(api = Build.VERSION_CODES.N)
    private void SendMail(Integer NoOrder){
        SQLiteDatabase dbR = link.getReadableDatabase();
        Cursor cOrderDet = dbR.rawQuery("SELECT Orders.id,Clients.Name,Orders.Date,Orders.Amount,Orders.Location,Orders.Status,Orders.IdInServer,Orders.ConsecInServer,Orders.Export,Clients.Email FROM Orders INNER JOIN Clients ON Orders.Client = Clients.CodeMyBusiness where Orders.id = '"+NoOrder+"' " ,null);
        Cursor cOrderParts = dbR.rawQuery("SELECT * from PartOrders where OrderId = " + NoOrder,null);

        String MailOrders = null;

        Cursor cMail = dbR.rawQuery("SELECT EmailOrders from AppConfig ",null);
        if(cMail.moveToFirst()){
            MailOrders = cMail.getString(0);
        }

        String OrderClient = null;
        Double AmountOrder = null;
        String DateOrder = null;
        String StatusOrder = null;
        String EmailClient = null;

        if(cOrderDet.moveToFirst()){
            do {
                OrderClient = cOrderDet.getString(1);
                AmountOrder = cOrderDet.getDouble(3);
                DateOrder = cOrderDet.getString(2);
                StatusOrder = cOrderDet.getString(5);
                EmailClient = cOrderDet.getString(9);
            }while (cOrderDet.moveToNext());
        }
            
        
        String MailContent = "Se recibio un nuevo pedido del cliente "+ OrderClient +" con Folio: "+ NoOrder +" y en estatus de "+StatusOrder+" \n\n\n\n\n"
                +"El pedido incluye :  \n\n";

        if(cOrderParts.moveToFirst()){
            do{
                String QtyProd = String.valueOf(cOrderParts.getDouble(2));
                String Descrip = cOrderParts.getString(5);
                MailContent = MailContent + QtyProd + "  " + Descrip + " \n" ;
            }while (cOrderParts.moveToNext());
        }

        String pattern = "###,###,###,###,###,###.##";
        Locale locale  = new Locale("es", "MX");
        android.icu.text.DecimalFormat decimalFormat = (android.icu.text.DecimalFormat) NumberFormat.getNumberInstance(locale);
        decimalFormat.applyPattern(pattern);
        String Total = decimalFormat.format(AmountOrder);

        MailContent = MailContent + " \n\n Por un total de :  $" + Total;
        MailContent = MailContent + " \n\n Del dia  :  " + DateOrder;

        printOrder(NoOrder);

        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{EmailClient});
        emailIntent.putExtra(Intent.EXTRA_CC, new String[]{MailOrders});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Pedido No: " + NoOrder);
        emailIntent.putExtra(Intent.EXTRA_TEXT, MailContent);
        emailIntent.setType("message/rfc822");
        startActivity(Intent.createChooser(emailIntent, "Selecciona aplicación..."));


    }




    @RequiresApi(api = Build.VERSION_CODES.N)
    private void printOrder(Integer IdOrder){
        SQLiteDatabase dbR = link.getReadableDatabase();
        Cursor cOrderDet = dbR.rawQuery("SELECT Orders.id,Clients.Name,Orders.Date,Orders.Amount,Orders.Location,Orders.Status,Orders.IdInServer,Orders.ConsecInServer,Orders.Export,Clients.Email FROM Orders INNER JOIN Clients ON Orders.Client = Clients.CodeMyBusiness where Orders.id = '"+IdOrder+"' " ,null);
        Cursor cOrderParts = dbR.rawQuery("SELECT * from PartOrders where OrderId = " + IdOrder,null);

        String PrintCont = null;

        String OrderClient = null;
        Double AmountOrder = null;
        String DateOrder = null;
        String StatusOrder = null;

        if(cOrderDet.moveToFirst()){
            do {
                OrderClient = cOrderDet.getString(1);
                AmountOrder = cOrderDet.getDouble(3);
                DateOrder = cOrderDet.getString(2);
                StatusOrder = cOrderDet.getString(5);
            }while (cOrderDet.moveToNext());
        }


        PrintCont = "$bigh$Comercializadora DLC $big$\nDomicilio Conocido S/N \nTlacocuspan Tlatlaya Mexico \n\n Telefonos:\n7161615127\n7221417374\n7165964309\n7223687716 \nFecha:"+DateOrder+"\n\nNuevo pedido \nCliente: \n" + OrderClient + "\nFolio: " + IdOrder + "\n" + StatusOrder + " \n\n"
                + "Partidas :  \n\n=============================\n$small$";

        if(cOrderParts.moveToFirst()){
            do{
                String QtyProd = String.valueOf(cOrderParts.getDouble(2));
                String Descrip = cOrderParts.getString(5);
                String Price = cOrderParts.getString(4);
                String partImp = String.valueOf(cOrderParts.getDouble(2) * cOrderParts.getDouble(4));

                PrintCont = PrintCont + Descrip + "\n" + QtyProd + "  *  " + Price + " = " + partImp + "\n-  -  -  -  -  -  -  -  -  -  -\n";
            }while (cOrderParts.moveToNext());
        }

//        String pattern = "###,###,###,###,###,###.##";
//       Locale locale  = new Locale("es", "MX");
//        android.icu.text.DecimalFormat decimalFormat = (android.icu.text.DecimalFormat) NumberFormat.getNumberInstance(locale);
//        decimalFormat.applyPattern(pattern);
//        String Total = decimalFormat.format(AmountOrder);

        PrintCont = PrintCont + "============================= \n\n$bigh$Total :  " + AmountOrder;
        PrintCont = PrintCont + " \n\n\n $intro$ ";

        String dataToPrint = PrintCont; //"$big$Pedido Numero:$intro$posprinterdriver.com$intro$$intro$$cut$$intro$";

        Intent intentPrint = new Intent();

        intentPrint.setAction(Intent.ACTION_SEND);
        intentPrint.putExtra(Intent.EXTRA_TEXT, dataToPrint);
        intentPrint.setType("text/plain");

        startActivity(intentPrint);
    }



















    private void StarSync(){

        SQLiteDatabase dbR = link.getReadableDatabase();
        Cursor cursor = dbR.rawQuery("SELECT id from Orders where Status = 'Reenviar' ",null);

        if (cursor.moveToFirst()){
            Toast.makeText(this, "" + String.valueOf(cursor.getInt(0)), Toast.LENGTH_SHORT).show();
            IdOrder = String.valueOf(cursor.getInt(0));
            new SyncOrder().execute();
        }else{
            //FillOrders("  Where Orders.Status = 'Reenviar' ");
            Toast.makeText(this, "No hay pedidos por enviar", Toast.LENGTH_SHORT).show();
        }
        link.close();
    }


















    private class SyncOrder extends AsyncTask<Void,Float,Void>{

        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(Void... voids) {

            SQLiteDatabase db = link.getReadableDatabase();
            Cursor OrderCursor = db.rawQuery("select Client,Date,Amount,Location,Status,IdInServer,ConsecInServer,Export,Observations from Orders where id = "+IdOrder ,null);

            if (OrderCursor.moveToFirst()){
                ClientSend = OrderCursor.getString(0);
                ImportSend = OrderCursor.getString(6);
                Observations = OrderCursor.getString(8);
            }

            final JSONObject OrderData = new JSONObject();
            JSONObject OrderPartsOb = new JSONObject();
            JSONObject SecureOrPart = new JSONObject();

            try {
                OrderData.put("AndroidID",IdOrder);
                OrderData.put("DateOrder",OrderCursor.getString(1));
                OrderData.put("Client",ClientSend);
                OrderData.put("Seller",Seller);
                OrderData.put("Terminal",Terminal);
                OrderData.put("User",UserLoged);
                OrderData.put("Import",ImportSend);
                OrderData.put("Observations",Observations);
                OrderData.put("Warehouse",Warehouse);

                SecureOrPart.put("CodeProd","DeleteME");
                SecureOrPart.put("Quantity","0");
                SecureOrPart.put("PriceProd","0");
                SecureOrPart.put("ObservPart","");
                SecureOrPart.put("Discount","0");
                SecureOrPart.put("PrClave","");
                SecureOrPart.put("PrDescrip","");
                SecureOrPart.put("PrQty","");
                OrderData.accumulate("Partidas",SecureOrPart);

                Cursor OrderParts = db.rawQuery("select id,OrderId,Quantity,ProdId,Price,Descrip,Export,Discount,PresentationClave,PresentationDescrip,PresentationQty,Observations FROM PartOrders where OrderId = " + IdOrder,null);
                if(OrderParts.moveToFirst()){
                    do{
                        String Codigo = OrderParts.getString(3);
                        String Cantidad = OrderParts.getString(2);
                        String PartPrice = OrderParts.getString(4);
                        String PartObservations = OrderParts.getString(11);
                        String PartDiscount = OrderParts.getString(7);
                        String PartClavePR = OrderParts.getString(8);
                        String PartDescripPR = OrderParts.getString(9);
                        String PartQtyPr = OrderParts.getString(10);

                        OrderPartsOb.put("CodeProd",Codigo);
                        OrderPartsOb.put("Quantity",Cantidad);
                        OrderPartsOb.put("PriceProd",PartPrice);
                        OrderPartsOb.put("ObservPart",PartObservations + "");
                        OrderPartsOb.put("Discount",PartDiscount);
                        OrderPartsOb.put("PrClave",PartClavePR + "");
                        OrderPartsOb.put("PrDescrip",PartDescripPR + "");
                        OrderPartsOb.put("PrQty",PartQtyPr + "");
                        OrderData.accumulate("Partidas",OrderPartsOb);

                        OrderPartsOb = new JSONObject();

                    }while(OrderParts.moveToNext());
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            String UrlSync = "http://"+ServerIp+"/INTEMWS/SyncOrder.php";
            JsonObjectRequest SyncOrder = new JsonObjectRequest(Request.Method.POST, UrlSync, OrderData, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    JSONArray ArrayResponse = response.optJSONArray("ResultServer");
                    JSONObject CompResponse = null;
                    CompResponse = ArrayResponse.optJSONObject(0);
                    String IdOrderServer = CompResponse.optString("id");
                    CloseOp(IdOrderServer);
                    //Toast.makeText(OrdersListActivity.this, "Id recibido " + IdOrderServer, Toast.LENGTH_SHORT).show();

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //ErrorOp();
                    Toast.makeText(OrdersListActivity.this, "Sin Respuesta   ......    " + OrderData.toString(), Toast.LENGTH_SHORT).show();

                }
            });
            RequestQueue Sync = Volley.newRequestQueue(getApplicationContext());
            Sync.add(SyncOrder);
            return null;
        }


        protected void onProgressUpdate(Float... values) {
            super.onProgressUpdate(values);
        }

        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }


    }

















    public void CloseOp(String cadena) {

        String Status = "Pedido en servidor " + cadena;
        SQLiteDatabase db = link.getWritableDatabase();
        db.execSQL("UPDATE Orders set IdInServer = '" + cadena + "',Status='"+Status+"' where id = '" + IdOrder + "' ");

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setMessage("La orden " + cadena + " esta en el servidor");
        dialogBuilder.setCancelable(false).setTitle("Estado de la sincronizacion");
        dialogBuilder.setNeutralButton("Entendido", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                StarSync();
            }
        });
        dialogBuilder.create().show();

    }





    public void ErrorOp() {

        SQLiteDatabase db = link.getWritableDatabase();
        db.execSQL("UPDATE Orders set Status = 'Reenviar' where id = '" + IdOrder + "' ");

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setMessage("No se puede sincronizar, Revisa la conexion a internet");
        dialogBuilder.setCancelable(false).setTitle("Estado de la sincronizacion");
        dialogBuilder.setNeutralButton("Entendido", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent goMenu = new Intent(getApplicationContext(),MenuActivity.class);
                startActivity(goMenu);
            }
        });
        dialogBuilder.create().show();

    }
























/*
    private class SendToServer extends AsyncTask<Void,Float,Void> {
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected void onProgressUpdate(Float... values) {
            super.onProgressUpdate(values);
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected Void doInBackground(Void... voids) {

            //************************ Envia Orden ******************************************************
            SQLiteDatabase dbR = link.getReadableDatabase();
            Cursor cOrder = dbR.rawQuery("SELECT id,Client,Date,Amount,Location,Status,IdInServer,ConsecInServer,Export FROM Orders WHERE id = '"+IdOrder+"' ",null);
            String UrlSendOrder = "";
            if(cOrder.moveToFirst()){
                UrlSendOrder = "http://"+ServerIp+"/INTEMWS/SaveOrders.php?IdOrder="+cOrder.getInt(0)+"&Client="+cOrder.getString(1)+"&Amount="+cOrder.getString(3)+"&DateOrder="+cOrder.getString(2);
            }
            JsonObjectRequest SendOrder = new JsonObjectRequest(Request.Method.GET, UrlSendOrder, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    JSONArray json = response.optJSONArray("order");
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = json.getJSONObject(0);
                        String IdInServer = jsonObject.optString("IdServer");
                        String ConsecInServer = jsonObject.optString("ConsecPed");
                        SQLiteDatabase dbW = link.getWritableDatabase();
                        dbW.execSQL("UPDATE Orders set IdInServer = '"+IdInServer+"',ConsecInServer = '"+ConsecInServer+"' where id = '" + IdOrder + "' ");
                        //******************************  Envia Partidas *******************************************
                        SQLiteDatabase dbR = link.getReadableDatabase();
                        Cursor OrderParts = dbR.rawQuery("SELECT id,OrderId,Quantity,ProdId,Price,Descrip,Export,Discount FROM PartOrders where OrderId = '"+IdOrder+"' ",null);
                        if(OrderParts.moveToFirst()){
                            do{
                                String UrlSendPart = "http://"+ServerIp+"/INTEMWS/SaveOrders.php?" +
                                        "IdOrderInServer="+IdInServer+"&" +
                                        "CodeProd="+OrderParts.getString(3)+"&" +
                                        "Quantity="+OrderParts.getString(2)+"&" +
                                        "PriceProd="+OrderParts.getString(4)+"&" +
                                        "Descrip=" + OrderParts.getString(5) + "&" +
                                        "Discount=" + OrderParts.getString(7);

                                JsonObjectRequest SendPart = new JsonObjectRequest(Request.Method.GET, UrlSendPart, null, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {

                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {

                                    }
                                });
                                RequestQueue QueuePart = Volley.newRequestQueue(getApplicationContext());
                                QueuePart.add(SendPart);
                                try {
                                    Thread.sleep(350);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }while (OrderParts.moveToNext());
                        }
                        //******************************  Envia Partidas *******************************************

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    UpdateOrder("Pendiente en Servidor");
                    StarSync();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    UpdateOrder("Reenviar al Servidor");
                }
            });
            RequestQueue QueueOrder = Volley.newRequestQueue(getApplicationContext());
            QueueOrder.add(SendOrder);
            link.close();
            return null;
        }
    }

*/
























    //====================   Inicia Actualiza estatus de pedidos ====================================================
    private void UpdateOrders(){
        String urlUp = "http://"+ServerIp+"/INTEMWS/OrdersStatus.php";
        JsonObjectRequest UpdateOrders = new JsonObjectRequest(Request.Method.GET, urlUp, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONArray ordersStatus = response.optJSONArray("status");
                try {
                    for (int i=0;i < ordersStatus.length(); i++){
                        SQLiteDatabase dbW = link.getWritableDatabase();
                        JSONObject jsonObject = ordersStatus.getJSONObject(i);
                        int IdOr = Integer.parseInt(jsonObject.optString("pedido"));
                        String Status = jsonObject.optString("estado");
                        dbW.execSQL("UPDATE Orders set Status='"+Status+"' where IdInServer = "+ IdOr);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Toast.makeText(OrdersListActivity.this, "Se actualizaron los estatus", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(OrdersListActivity.this, "No hay pedidos en el servidor o existe un error en la conexion", Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue UpOrders = Volley.newRequestQueue(this);
        UpOrders.add(UpdateOrders);
        FillOrders("");
        link.close();
        dialog.dismiss();
        Toast.makeText(this, "Se sincronizaron las ordenes", Toast.LENGTH_SHORT).show();

    }

    //====================   Finaliza Actualizacion de estatus de pedidos ====================================================













    private void UpdateOrder(String Status){

        cCondicion = "";
        SQLiteDatabase db = link.getWritableDatabase();
        db.execSQL("UPDATE Orders set Status = '" + Status + "' where id = '" + IdOrder + "' ");
        FillOrders(cCondicion);
        link.close();
        dialog.dismiss();
    }







    private void DeleteOrder(String NoOrder) {

        SQLiteDatabase db = link.getWritableDatabase();
        db.execSQL("DELETE from PartOrders where OrderId = '" + NoOrder + "' ");
        db.execSQL("DELETE from Orders where id = '" + NoOrder + "' ");
        FillOrders("");
        link.close();
    }









}
