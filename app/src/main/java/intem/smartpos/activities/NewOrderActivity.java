package intem.smartpos.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
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

import java.text.DecimalFormat;
import java.util.ArrayList;

import intem.smartpos.R;
import intem.smartpos.adapters.clientsSimpleAdapter;
import intem.smartpos.adapters.orderPartsAdapter;
import intem.smartpos.adapters.presentations_adapter;
import intem.smartpos.adapters.prodsAdapter;
import intem.smartpos.constructors.clientsConstructor;
import intem.smartpos.constructors.orderPartsConstructor;
import intem.smartpos.constructors.presentations_constructor;
import intem.smartpos.constructors.prodsConstructor;
import intem.smartpos.database.connection;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static intem.smartpos.R.layout.view_clients;
import static intem.smartpos.R.layout.view_orders_part;
import static intem.smartpos.R.layout.view_presentations;

public class NewOrderActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private ProgressDialog ProgressOrder;
    private ArrayList<prodsConstructor> ProdsArray;
    private ImageButton imageButton_NewOrder_FindClient,iB_NewOrder_FindByLine,iB_NewOrder_findProd,imageButton_FilterCl,imageButtonScanCodeBar;
    private ListView lV_NewOrders_ListProds;
    private TextView tV_NewOrder_LastProd,tV_NewORder_Total,tV_NewORder_IdOrder;
    private EditText eT_NewOrders_findProds,editText_NewOrder_Client,eT_NewOrder_Qty;
    private String Seller,LineSelect,IdOrder,MailContent,ServerIp,ClientSelect,ClientSend,ImportSend,Terminal,UserLoged,Warehouse,Observations;
    private String PrSelect,NumSale,IdPart,StatusCamera,UrlSendSale,ProductToFind,PriceSelect,ListOfPrice,SaleRem,PrEnabled,CodeProd,Company,AdressCompany,PhoneCompany;
    private double Price,valor;
    private boolean PrOptions;
    private FloatingActionButton FAB_NewOrders_Parts;
    private ArrayList ListClients;
    private Integer PriceByQty,LeaveDiscount,PendingSale,PriceQty,EnabledPrintTk,Remision;
    private ArrayList<clientsConstructor> ClientsArray;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 1 ;
    clientsConstructor ItemClient;

    private ArrayList<presentations_constructor> PresentationsArray;
    private presentations_constructor PresentationRow;

    private Spinner spinnerLines;
    private ArrayList LinesList;

    private ArrayList<clientsConstructor> ClientsList;

    prodsConstructor prodsItem;

    private orderPartsConstructor PartRow;
    private clientsConstructor ClientRow;
    private ArrayList<orderPartsConstructor> PartsArray;

    private connection link;

    private ZXingScannerView zXingScannerView;

    private void BindUI() {
        tV_NewORder_Total = (TextView) findViewById(R.id.tV_NewOrder_Total);
        iB_NewOrder_findProd = (ImageButton) findViewById(R.id.iB_NewOrder_FindProd);
        eT_NewOrders_findProds = (EditText) findViewById(R.id.eT_NewOrder_SearcherProd);
        eT_NewOrder_Qty = (EditText) findViewById(R.id.eT_NewOrder_Qty);
        lV_NewOrders_ListProds = (ListView) findViewById(R.id.lV_NewOrder_ListProds);
        spinnerLines = (Spinner) findViewById(R.id.spinnerLines);
        iB_NewOrder_FindByLine = (ImageButton) findViewById(R.id.iB_NewOrder_FindByLine);
        FAB_NewOrders_Parts = (FloatingActionButton) findViewById(R.id.FAB_NewOrders_Parts);
        tV_NewOrder_LastProd = (TextView) findViewById(R.id.tV_NewOrder_LastProd);
        imageButton_NewOrder_FindClient = (ImageButton) findViewById(R.id.imageButton_NewOrder_FindClient);
        editText_NewOrder_Client = (EditText) findViewById(R.id.editText_NewOrder_Client);
    }

    @Override
    public void onBackPressed() {

        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
        dialogo1.setTitle("Importante");
        dialogo1.setMessage("¿Dejas la orden como pendiente?");
        dialogo1.setCancelable(false);
        dialogo1.setNegativeButton("No,Eliminalo", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
            DeleteOrder();
            }
        });

        dialogo1.setPositiveButton("Si,Guardalo", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            public void onClick(DialogInterface dialogo1, int id) {
            UpdateOrder("Pendiente");
            Intent intent = new Intent(NewOrderActivity.this,OrdersListActivity.class);
            intent.putExtra("order",IdOrder);
            startActivity(intent);
            }
        });
        dialogo1.show();
    }



    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_order);



        ProgressOrder = new ProgressDialog(this);
        ProgressOrder.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        ProgressOrder.setCancelable(false);

        StatusCamera = "";

        BindUI();

        link = new connection(getApplicationContext(), "MyBusinessAndroid", null, 1);

        valor = 0;






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
                "Remision, " + //10
                "Seller " + //1
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
            Seller = cIp.getString(12);
        }





        //****************** Recupera Datos del server ***************************
        //****************** Recupera Datos del server ***************************
        Cursor UserLogQuery = dbR.rawQuery("select Nick,Discount from Users Where Loged = 1 ",null);

        if(UserLogQuery.moveToFirst()){
            UserLoged = UserLogQuery.getString(0);
            LeaveDiscount = UserLogQuery.getInt(1);
        }







        String cCondicion = "";
        Bundle orderData = getIntent().getExtras();
        if (orderData != null) {
            IdOrder = orderData.getString("order");
            SQLiteDatabase db = link.getReadableDatabase();
            Cursor cTotal = db.rawQuery("Select sum(Quantity * Price) As total from PartOrders where OrderId = '" + IdOrder + "' ", null);
            ArrayList results = new ArrayList();
            if (cTotal.moveToFirst()) {
                do {
                    results.add(cTotal.getDouble(0));
                } while (cTotal.moveToNext());
            }
            valor = Double.parseDouble(results.get(0).toString());
            String ClientCode = orderData.getString("client");
            //'SetClient(ClientCode);

            if (orderData.getString("find") != null){
                String find = orderData.getString("find").trim();
                Cursor cCodigo = db.rawQuery("Select Code from Prods where Code = '" + find + "' ", null);

                if (cCodigo.moveToFirst()) {
                    cCondicion = " where Code = '"+ find +"' ";
                }else{
                    Cursor AuxCodes = db.rawQuery("Select CodeProd from Presentations where CodePr = '"+ find +"' ",null);
                    if(AuxCodes.moveToFirst()){
                        cCondicion = " where Code = '"+ AuxCodes.getString(0) +"' ";
                    }
                }
            }
        }

        FillProds(cCondicion);

        getDataOrder(IdOrder);
        getClientData();

        //getSpinnerLines();



        if (orderData.getString("open") != null) {
            getSumParts();
        }


/*
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
*/




        iB_NewOrder_FindByLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cCondicion = "";

                if (LineSelect != "Filtra por linea...") {
                    cCondicion = " where ProdCategory = '" + LineSelect + "' ";
                    FillProds(cCondicion);
                }
                FillProds(cCondicion);
            }
        });





        imageButton_NewOrder_FindClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String param = editText_NewOrder_Client.getText().toString();
                ViewClients(param);
                editText_NewOrder_Client.setText("");
            }
        });













        iB_NewOrder_findProd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Parameter = eT_NewOrders_findProds.getText().toString();
                if (Parameter.length() > 1) {
                    String cCondicion = " where Description like '%" + Parameter + "%' or Code like '%" + Parameter + "%' ";
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(eT_NewOrders_findProds.getWindowToken(), 0);
                    FillProds(cCondicion);
                } else {
                    String cCondicion = "";
                    FillProds(cCondicion);
                }
            }
        });














        lV_NewOrders_ListProds.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                if(PrEnabled.equals("1")){
                    final SQLiteDatabase db = link.getReadableDatabase();
                    CodeProd = ProdsArray.get(position).getCode();
                    String Descrip = ProdsArray.get(position).getDescription();
                    String PriceProd = String.valueOf(ProdsArray.get(position).getPrice());
                    String DiscountPart = String.valueOf(ProdsArray.get(position).getOffer());

                    String Qty = "1";

                    if (eT_NewOrder_Qty.length() > 0) {
                        Qty = eT_NewOrder_Qty.getText().toString();
                    }


                    Cursor QtyPr = db.rawQuery("select id,CodePr,QtyPr As conteo from Presentations where CodeProd = '"+ CodeProd.trim() +"' and QtyPr > 1 ",null);
                    int PrCount = 0;
                    if(QtyPr.moveToFirst()){
                            PrCount = QtyPr.getCount();
                        //Toast.makeText(NewOrderActivity.this, "-"+PrCount+"- Id:"+QtyPr.getString(0) + " - QtyPr:" + QtyPr.getString(2), Toast.LENGTH_LONG).show();
                        //PrOptions = true;
                    }else{
                        //PrOptions = false;
                    }


                    if(PrCount > 0){
                        ViewPresentations(CodeProd);
                    }else{
                        InsertOrderPart(CodeProd,"BASE", Qty,Descrip,PriceProd,DiscountPart);
                    }

                }else{
                    String CodeProd = ProdsArray.get(position).getCode();
                    String Descrip = ProdsArray.get(position).getDescription();
                    String Qty = eT_NewOrder_Qty.getText().toString();
                    String PriceProd = String.valueOf(ProdsArray.get(position).getPrice());
                    String DiscountPart = String.valueOf(ProdsArray.get(position).getOffer());
                    InsertOrderPart(CodeProd,"BASE", Qty,Descrip,PriceProd,DiscountPart);
                }

            }
        });











        FAB_NewOrders_Parts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewOrdersPart();
            }
        });
        link.close();








        //Valida permisos de la app
        if (ContextCompat.checkSelfPermission(NewOrderActivity.this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(NewOrderActivity.this,
                    new String[]{Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CAMERA );

        }





    }























    // Muestra ventana con las Presentaciones

    private void ViewPresentations(final String ProdCode){

        final SQLiteDatabase db = link.getReadableDatabase();
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View Presentations = LayoutInflater.from(getApplicationContext()).inflate(view_presentations, null);
        builder.setView(Presentations);
        builder.setTitle("Presentaciones del producto");


        String Cantidadbase = "1";

        if (eT_NewOrder_Qty.length() < 1) {
            Cantidadbase = "1";
        } else {
            Cantidadbase = eT_NewOrder_Qty.getText().toString();
        }


        final String finalCantidadbase = Cantidadbase;
        builder.setNeutralButton("Ingresa como producto base", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Cursor cProd = db.rawQuery("select Description,Price1 from Prods where Code = '"+ProdCode+"' ",null);

                if(cProd.moveToFirst()){
                    InsertOrderPart(CodeProd,"BASE", finalCantidadbase,cProd.getString(0),cProd.getString(1),"0");
                }

            }
        });

        final AlertDialog adTrueDialog;
        adTrueDialog = builder.show();

        /* ============================================= Vista de Presentaciones ==============================================*/
        PresentationsArray = new ArrayList<presentations_constructor>();

        Cursor cPr = db.rawQuery("select id,CodePr,CodeProd,DescPr,QtyPr,PricePr from Presentations where CodeProd = '"+ ProdCode.trim() +"' ",null);
        if(cPr.moveToFirst()){
            do{
                PresentationRow = new presentations_constructor(cPr.getInt(0),cPr.getString(1),cPr.getString(3),cPr.getDouble(4),cPr.getDouble(5));
                PresentationsArray.add(PresentationRow);
            }while (cPr.moveToNext());
        }

        final ListView ListPr = Presentations.findViewById(R.id.Presentations_List);
        presentations_adapter adapter = new presentations_adapter(getApplicationContext(), PresentationsArray);
        ListPr.setAdapter(adapter);

        final String finalCantidadbase1 = Cantidadbase;
        ListPr.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String CodePresentation = PresentationsArray.get(position).getCodePResentation();
                InsertOrderPart(CodePresentation.trim(),"PR", finalCantidadbase1,"","0.0","0");
                adTrueDialog.dismiss();
            }
        });

    }











    //Inserta Partida de la orden
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void InsertOrderPart(String Product, String Presentation,String QtyProd, String DescripProd, String PriceProd,String DiscountPart){

 //       Toast.makeText(this, "|"+ Product  +"|" + Presentation, Toast.LENGTH_SHORT).show();

        link = new connection(getApplicationContext(), "MyBusinessAndroid", null, 1);
        SQLiteDatabase db = link.getWritableDatabase();

        Double Quantity = Double.valueOf(QtyProd);

        String CodePresentation = "";
        String CodeProduct = Product;
        Double QtyPresentation = 1.0;
        String PresentationDescrip = "";
        String ProductDescrip = DescripProd;

        Price = Double.parseDouble(PriceProd);


        if (Presentation == "PR") {
//            Toast.makeText(this, "Entra como presentacion", Toast.LENGTH_SHORT).show();
            Cursor DataPr = db.rawQuery("SELECT Presentations.CodePr,Presentations.CodeProd,Presentations.DescPr,Presentations.QtyPr,Presentations.PricePr,Prods.Description from Presentations INNER JOIN Prods ON trim(Prods.Code) = trim(Presentations.CodeProd) where CodePr = '"+  Product.trim() +"'   ", null);

            if(DataPr.moveToFirst()){
//                Toast.makeText(this, "La consulta tuvo resultados", Toast.LENGTH_SHORT).show();
                CodePresentation = DataPr.getString(0);
                CodeProduct = DataPr.getString(1);
                Quantity = Quantity * DataPr.getDouble(3);
                Price = DataPr.getDouble(4);
                QtyPresentation = DataPr.getDouble(3);
                PresentationDescrip = DataPr.getString(2);
                ProductDescrip = DataPr.getString(5);
            }else{
                Toast.makeText(this, "Atencion, La presentacion tiene conflictos, favor de reportar a sistemas", Toast.LENGTH_LONG).show();
            }
        }




        Cursor validExist = db.rawQuery("SELECT id from PartOrders where ProdId = '" + CodeProduct + "' and OrderId = '" + IdOrder + "' and PresentationClave = '" + CodePresentation + "' ", null);
        ArrayList resultValid = new ArrayList();

        if (validExist.moveToFirst()) {
            do {
                resultValid.add(validExist.getDouble(0));
            } while (validExist.moveToNext());

            db.execSQL("UPDATE PartOrders set Quantity = Quantity + '" + Quantity + "',Price = '"+ Price +"' where id = '" + resultValid.get(0).toString() + "' ");
            getSumParts();
        } else {
            ContentValues values = new ContentValues();
            values.put("OrderId", IdOrder);
            values.put("Quantity", Quantity);
            values.put("ProdId", CodeProduct);
            values.put("PresentationClave", CodePresentation);
            values.put("PresentationQty", QtyPresentation);
            values.put("PresentationDescrip", PresentationDescrip);
            values.put("Price", Price);
            values.put("Descrip", ProductDescrip);
            values.put("Export", 0);
            values.put("Discount",DiscountPart);
            Long idResult = db.insert("PartOrders", "id", values);
            getSumParts();
        }


        SQLiteDatabase dbR = link.getReadableDatabase();
        Cursor cLastPart = dbR.rawQuery("SELECT Descrip,Quantity,PresentationQty,PresentationDescrip from PartOrders where OrderId = '"+IdOrder+"' and ProdId = '"+ CodeProduct +"' ",null);
        if(cLastPart.moveToFirst()){
            String LastPart = cLastPart.getString(3) + ' ' + cLastPart.getString(0);
            String LastQty = String.valueOf(cLastPart.getDouble(1) / cLastPart.getDouble(2));
            tV_NewOrder_LastProd.setText(LastPart + "  x   " + LastQty );
        }



        link.close();
        eT_NewOrders_findProds.setText("");
        eT_NewOrder_Qty.setText("");

    }




























    private void getClientData(){

        SQLiteDatabase dbR = link.getReadableDatabase();
        Cursor cursorCl = dbR.rawQuery("SELECT Name FROM Clients INNER JOIN Orders ON Clients.CodeMyBusiness = Orders.Client WHERE Orders.id =  " + IdOrder,null);
        if(cursorCl.moveToFirst()){
            editText_NewOrder_Client.setText(cursorCl.getString(0));
        }

    }














    private void getDataOrder(String NumOrder) {

        tV_NewORder_IdOrder = (TextView) findViewById(R.id.tV_NewOrder_IdOrder);
        tV_NewORder_IdOrder.setText("Folio: " + NumOrder);
    }




    private void getSumParts() {

        SQLiteDatabase db = link.getReadableDatabase();
        Cursor cTotal = db.rawQuery("Select sum(  (Quantity * Price) * ((100-Discount)/100) ) As total from PartOrders where OrderId = '" + IdOrder + "' ", null);

        ArrayList results = new ArrayList();
        if (cTotal.moveToFirst()) {
            do {
                results.add(cTotal.getDouble(0));
            } while (cTotal.moveToNext());
        }

        valor = Double.parseDouble(results.get(0).toString());
        db.execSQL("UPDATE Orders set Amount = '" + valor + "' where id = '" + IdOrder + "'  ");

        DecimalFormat formateador = new DecimalFormat("###,###,###,###.00");
        String TotalStr = formateador.format (valor);

        tV_NewORder_Total.setText("$" + TotalStr);
        link.close();
    }





    private void FillProds(String cCondicion) {

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
        lV_NewOrders_ListProds.setAdapter(adapter);


        link.close();
    }


    private void getSpinnerLines() {

        SQLiteDatabase db = link.getReadableDatabase();
        Cursor cLines = db.rawQuery("SELECT Distinct(ProdCategory) from Prods ", null);

        LinesList = new ArrayList();
        LinesList.add("Filtra por linea...");

        if (cLines.moveToFirst()) {
            do {
                LinesList.add(cLines.getString(0));
            } while (cLines.moveToNext());
        }
        link.close();
    }












    private void ViewOrdersPart() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View OrderParts = LayoutInflater.from(getApplicationContext()).inflate(view_orders_part, null);
        builder.setView(OrderParts);

        final SQLiteDatabase db = link.getWritableDatabase();
        PartsArray = new ArrayList<orderPartsConstructor>();
        final Cursor cParts = db.rawQuery("SELECT id,OrderId,Quantity,ProdId,Price,Descrip,Discount,PresentationQty,PresentationDescrip FROM PartOrders where OrderId = '" + IdOrder + "' ", null);

        if (cParts.moveToFirst()) {
            do {
                PartRow = new orderPartsConstructor(cParts.getInt(0), cParts.getInt(1), cParts.getDouble(2), cParts.getString(3), cParts.getFloat(4), cParts.getString(5), cParts.getDouble(6),cParts.getDouble(7),cParts.getString(8));
                PartsArray.add(PartRow);
            } while (cParts.moveToNext());
        }



        final ListView ListOrderParts = OrderParts.findViewById(R.id.lV_OrdersPart_ListParts);
        orderPartsAdapter adapter = new orderPartsAdapter(getApplicationContext(), PartsArray);
        ListOrderParts.setAdapter(adapter);



        builder.setPositiveButton("Confirma el pedido", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(valor==0) {
                    Toast.makeText(getApplicationContext(), "No se puede generar un pedido de importe $0.00", Toast.LENGTH_LONG).show();
                }else{
                    ClientSelect = editText_NewOrder_Client.getText().toString();
                    if (ClientSelect.isEmpty()) {
                        Toast.makeText(getApplicationContext(), "No has indicado un cliente", Toast.LENGTH_LONG).show();
                    } else {
//                      PrintQuestion();
                        EditText OrderObservations = findViewById(R.id.editText_NewOrder_Observations);
                        String Observations = OrderObservations.getText().toString();
                        SQLiteDatabase dbW = link.getWritableDatabase();
                        dbW.execSQL("UPDATE Orders set Observations = '"+ Observations +"' where id = '"+IdOrder+"' ");
                        new SyncOrder().execute();
                    }
                }

            }
        });


        final AlertDialog OrderPartsDialog = builder.create();
        OrderPartsDialog.show();
        link.close();

        ListOrderParts.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {

                String IdPart = String.valueOf(PartsArray.get(position).getId());
                SQLiteDatabase dbW = link.getWritableDatabase();
                dbW.execSQL("delete from PartOrders where id = '"+IdPart+"' ");
                Toast.makeText(NewOrderActivity.this, "Partida Eliminada", Toast.LENGTH_SHORT).show();
                getSumParts();
                OrderPartsDialog.dismiss();
                return false;

            }
        });




    }



















    private void PrintQuestion(){
        AlertDialog.Builder printQuestion = new AlertDialog.Builder(getApplicationContext());
        printQuestion.setTitle("Pregunta");
        printQuestion.setMessage("¿Quieres imprimir un ticket?");
        printQuestion.setCancelable(false);
        printQuestion.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {

            }
        });
        printQuestion.setPositiveButton("Si,imprimelo", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            public void onClick(DialogInterface dialogo1, int id) {
                Integer Id = Integer.valueOf(IdOrder);
                printOrder(Id);
            }
        });
        printQuestion.show();

    }












































    //************** **************** ******************** Clients ********************* ******************** ********************
    private void ViewClients(String param) {

        String clCondicion = "";

        if(param.isEmpty()){
            clCondicion = "";
        }else{
            clCondicion = " where Name like '%"+param+"%' ";
        }

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View Clients = LayoutInflater.from(getApplicationContext()).inflate(view_clients, null);
        builder.setView(Clients);

        final SQLiteDatabase db = link.getReadableDatabase();
        ClientsArray = new ArrayList<clientsConstructor>();
        Cursor cClients = db.rawQuery("SELECT * FROM Clients " + clCondicion, null);

        if (cClients.moveToFirst()) {
            do {
                //id INTEGER,CodeMyBusiness TEXT,Name TEXT,Export INTEGER,Street TEXT,Colony TEXT, City TEXT,CP TEXT,RFC TEXT,Email TEXT,Debt REAL
                ClientRow = new clientsConstructor(cClients.getInt(0),cClients.getString(1),cClients.getString(2),cClients.getInt(3),cClients.getString(4),cClients.getString(5),cClients.getString(6),cClients.getString(7),cClients.getString(8),cClients.getString(9),cClients.getDouble(10));
                ClientsArray.add(ClientRow);
            } while (cClients.moveToNext());
        }

        ListView ListClients = Clients.findViewById(R.id.listView_NewOrder_CLients);
        clientsSimpleAdapter adapter = new clientsSimpleAdapter(getApplicationContext(), ClientsArray);
        ListClients.setAdapter(adapter);

        ListClients.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                String ClientCode = String.valueOf(ClientsArray.get(position).getCodeMyBusiness());
                SQLiteDatabase dbW = link.getWritableDatabase();
                dbW.execSQL("UPDATE Orders set Client = '"+ClientCode+"' where id = '"+IdOrder+"' ");
                getClientData();
                Toast.makeText(NewOrderActivity.this, "El cliente " + ClientsArray.get(position).getNameClient() + " se asigno al pedido en curso ", Toast.LENGTH_SHORT).show();
                return false;
            }
        });


        AlertDialog OrderPartsDialog = builder.create();
        OrderPartsDialog.show();
        link.close();
    }
    //********** ***************** ************** ********************* ************************* ***************** **************





    private void DeleteOrder() {

        SQLiteDatabase db = link.getWritableDatabase();
        db.execSQL("DELETE from PartOrders where OrderId = '" + IdOrder + "' ");
        db.execSQL("DELETE from Orders where id = '" + IdOrder + "' ");
        Intent intent = new Intent(NewOrderActivity.this, MenuActivity.class);
        startActivity(intent);
        link.close();
        getSumParts();
    }











    @RequiresApi(api = Build.VERSION_CODES.N)
    private void UpdateOrder(String Status){
        SQLiteDatabase db = link.getWritableDatabase();
        db.execSQL("UPDATE Orders set Status = '" + Status + "' where id = '" + IdOrder + "' ");
        Intent intent = new Intent(NewOrderActivity.this, OrdersListActivity.class);
        intent.putExtra("order", IdOrder);
        startActivity(intent);
        SendMail(IdOrder);
        link.close();
    }














    public void ScanButton(View v){

        if (ContextCompat.checkSelfPermission(NewOrderActivity.this,Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Se requiere permiso para usar la camara", Toast.LENGTH_SHORT).show();
        }else{
            zXingScannerView = new ZXingScannerView(this);
            zXingScannerView.setResultHandler(NewOrderActivity.this);
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

        Intent intent = new Intent(NewOrderActivity.this,NewOrderActivity.class);
        intent.putExtra("order",IdOrder);
        intent.putExtra("find",Codigo);
        startActivity(intent);
        StatusCamera = "";

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
//            Toast.makeText(NewOrderActivity.this, "" + OrderData.toString(), Toast.LENGTH_SHORT).show();

            String UrlSync = "http://"+ServerIp+"/INTEMWS/SyncOrder.php";
            JsonObjectRequest SyncOrder = new JsonObjectRequest(Request.Method.POST, UrlSync, OrderData, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    JSONArray ArrayResponse = response.optJSONArray("ResultServer");
                    JSONObject CompResponse = null;
                    CompResponse = ArrayResponse.optJSONObject(0);
                    String IdOrderServer = CompResponse.optString("id");
                    CloseOp(IdOrderServer);
                    //Toast.makeText(NewOrderActivity.this, "" + OrderData.toString(), Toast.LENGTH_SHORT).show();

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    ErrorOp();
                    //Toast.makeText(NewOrderActivity.this, "" + OrderData.toString(), Toast.LENGTH_SHORT).show();

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

        String Status = "Pedido en servidor : " + cadena;
        SQLiteDatabase db = link.getWritableDatabase();
        db.execSQL("UPDATE Orders set IdInServer = '" + cadena + "',Status='"+Status+"' where id = '" + IdOrder + "' ");

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setMessage("La orden " + cadena + " esta en el servidor");
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





    public void ErrorOp() {

        SQLiteDatabase db = link.getWritableDatabase();
        db.execSQL("UPDATE Orders set Status = 'Reenviar' where id = '" + IdOrder + "' ");

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setMessage("Queda Pendiente por sincronizar");
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



























    @RequiresApi(api = Build.VERSION_CODES.N)
    private void SendMail(String NoOrder){
        SQLiteDatabase dbR = link.getReadableDatabase();
        Cursor cOrderDet = dbR.rawQuery("SELECT Orders.id,Clients.Name,Orders.Date,Orders.Amount,Orders.Location,Orders.Status,Orders.IdInServer,Orders.ConsecInServer,Orders.Export,Clients.Email FROM Orders INNER JOIN Clients ON Orders.Client = Clients.CodeMyBusiness " ,null);
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


        MailContent = MailContent + " \n\n Por un total de :  $" + AmountOrder;
        MailContent = MailContent + " \n\n Del dia  :  " + DateOrder;

        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{EmailClient});
        emailIntent.putExtra(Intent.EXTRA_CC, new String[]{MailOrders});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Pedido No: " + NoOrder);
        emailIntent.putExtra(Intent.EXTRA_TEXT, MailContent);
        emailIntent.setType("message/rfc822");
        startActivity(Intent.createChooser(emailIntent, "Selecciona aplicación..."));

        link.close();
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


        PrintCont = "$bigh$Comercializadora DLC $big$\nDomicilio Conocido S/N Tlacocuspan\nTlatlaya Mexico\n\nTelefono:7161615127\n7221417374\nFecha:"+DateOrder+"\n\nNuevo pedido \nCliente: \n" + OrderClient + "\nFolio: " + IdOrder + "\n" + StatusOrder + " \n\n"
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

        PrintCont = PrintCont + "============================= \n\n$bigh$Total :  " + AmountOrder;
        PrintCont = PrintCont + " \n\n\n $intro$ ";

        String dataToPrint = PrintCont; //"$big$Pedido Numero:$intro$posprinterdriver.com$intro$$intro$$cut$$intro$";

        Intent intentPrint = new Intent();

        intentPrint.setAction(Intent.ACTION_SEND);
        intentPrint.putExtra(Intent.EXTRA_TEXT, dataToPrint);
        intentPrint.setType("text/plain");

        startActivity(intentPrint);

        link.close();
    }














    private void SetClient(String clientCode){

        SQLiteDatabase dbW = link.getWritableDatabase();

        Cursor validClient = dbW.rawQuery("select id from Clients where CodeMyBusiness = '"+ clientCode +"' ",null);

        if (validClient.moveToFirst()){
            dbW.execSQL("UPDATE Orders set Client = '"+ clientCode +"' where id = '"+IdOrder+"' ");
            getClientData();
        }else{
            Toast.makeText(this, "El cliente no existe en la base de datos, realiza busqueda manual", Toast.LENGTH_SHORT).show();
        }


        StatusCamera = "";
    }









}
