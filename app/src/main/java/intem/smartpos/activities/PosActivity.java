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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import intem.smartpos.R;
import intem.smartpos.adapters.clientsSimpleAdapter;
import intem.smartpos.adapters.presentations_adapter;
import intem.smartpos.adapters.prodsAdapter;
import intem.smartpos.adapters.salePartsAdapter;
import intem.smartpos.constructors.clientsConstructor;
import intem.smartpos.constructors.partsSaleConstructor;
import intem.smartpos.constructors.presentations_constructor;
import intem.smartpos.constructors.prodsConstructor;
import intem.smartpos.database.connection;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static intem.smartpos.R.layout.sales_observ;
import static intem.smartpos.R.layout.view_clients;
import static intem.smartpos.R.layout.view_part_options;
import static intem.smartpos.R.layout.view_presentations;

public class PosActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private connection link;
    private ProgressDialog progressDialog;
    private ListView ListView_Pos_ListProds;
    private ListView ListView_Pos_ListParts;
    private ListView Lv_Presentations;

    private ArrayList<prodsConstructor> ProdsArray;
    prodsConstructor prodsItem;

    private ArrayList<presentations_constructor> PresentationsArray;
    private presentations_constructor PresentationRow;

    private ArrayList<clientsConstructor> ClientsArray;
    private clientsConstructor ClientRow;

    private ArrayList<partsSaleConstructor> PartsArray;
    partsSaleConstructor partsItem;

    private Spinner spinnerLines;
    private ArrayList LinesList;
    private Double TotalSale;
    private EditText ClientSale,ProdToFind;
    private ImageButton ImageButton_FindProd,FilterLine,ImageButton_Pos_ObservSale;
    private String PrSelect,IdSaleInServ,ImportSend,ClientSend,LineSelect,NumSale,IdPart,StatusCamera,ServerIp,Terminal,Warehouse,UrlSendSale,UserLoged,ProductToFind,PriceSelect,ListOfPrice,SaleRem,PrEnabled,CodeProd,ObservContent,ObservToSend;
    private Integer EnabledPrintTk,Remision;

    private ZXingScannerView zXingScannerView;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 1 ;

    //Detalles de producto
    private String DescripProd,Price1,Price2,Price3,Price4,Price5,Price6,Price7,Price8,Price9,Company,AdressCompany,PhoneCompany,PayMethod;
    private Double Qty2,PricePart;
    private Double Qty3;
    private Double Qty4;
    private Double Qty5;
    private Double Qty6;
    private Double Qty7;
    private Double Qty8;
    private Double Qty9;
    private Integer PendingSale,PriceQty,LeaveDiscount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pos);
        link = new connection(getApplicationContext(), "MyBusinessAndroid", null, 1);



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



        NumSale = "";
        ProductToFind = "";
        StatusCamera = "";

        //****************** Recupera Datos del Bundle ***************************
        Bundle SaleData = getIntent().getExtras();
        if (SaleData != null) {
            NumSale = SaleData.getString("NumSale");
            ProductToFind = SaleData.getString("ProdCode");
        }

        ClientSale = (EditText) findViewById(R.id.EditText_Pos_Client);
        ListView_Pos_ListProds = (ListView) findViewById(R.id.ListView_Pos_Prods);
        ListView_Pos_ListParts = (ListView) findViewById(R.id.ListView_Pos_Parts);
        FilterLine = (ImageButton) findViewById(R.id.ImageButton_Pos_FilterLine);
        ImageButton_FindProd = (ImageButton) findViewById(R.id.ImageButton_Pos_FindProd);
        ImageButton ImBt_FindClient = (ImageButton) findViewById(R.id.ImageButton_Pos_FindClient);
        ImageButton ImBt_FinishSale = (ImageButton) findViewById(R.id.ImageButton_Pos_CloseSale);
        spinnerLines = (Spinner) findViewById(R.id.spinner_Pos_Lines);
        Lv_Presentations = (ListView) findViewById(R.id.Presentations_List);
        ImageButton_Pos_ObservSale = (ImageButton) findViewById(R.id.ImageButton_Pos_ObservSale);

        if(ProductToFind.equals("")){
            FillProds("");
        }else{

            Cursor cClave = dbR.rawQuery("select Code from Prods where Code = '"+ProductToFind+"' or Code like '"+ProductToFind+"' or Description like '%"+ProductToFind+"%' ",null);
            if(cClave.moveToFirst()){
                FillProds(" where Code like '%"+ProductToFind+"%' or Code ='"+ProductToFind+"' or Description like '%"+ProductToFind+"%' ");
            }else{
                Cursor cClaveAdd = dbR.rawQuery("select CodeProd from Presentations where CodePr = '"+ProductToFind+"' ",null);
                if(cClaveAdd.moveToFirst()){
                    String ProductOfClaveAdd = cClaveAdd.getString(0);
                    FillProds(" where Code like '%"+ProductOfClaveAdd+"%' or Code ='"+ProductOfClaveAdd+"' or Description like '%"+ProductOfClaveAdd+"%' ");
                }else{
                    FillProds("");
                }
            }
        }

        SQLiteDatabase db = link.getReadableDatabase();
        Cursor QuerySale = db.rawQuery("select id from Sales where Status = 'PR' ",null);

        if(QuerySale.moveToFirst()){
            NumSale = QuerySale.getString(0);
            String PartsCondicion = " where SaleID = '"+ NumSale +"' ";
            FillParts(PartsCondicion);
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







        ImBt_FindClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String param = ClientSale.getText().toString();
                ViewClients(param);
                ClientSale.setText("");
            }
        });



        ImBt_FinishSale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(NumSale.equals("")){

                }else{
                    if(TotalSale > 0){
                        FinishSale();
                        //Test();
                    }else{
                        Toast.makeText(PosActivity.this, "La venta no puede confirmarse sin partidas o en costo 0", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });





        ImageButton_Pos_ObservSale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ObservToSale();
            }
        });







        ImageButton_FindProd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProdToFind = (EditText) findViewById(R.id.EditText_Pos_FindProd);
                String ProdParam = ProdToFind.getText().toString();
                if(ProdParam.length() > 0 ){
                    SQLiteDatabase Datab = link.getReadableDatabase();
                    Cursor ValidProd = Datab.rawQuery("select Code from prods where Description like '%"+ProdParam+"%' or Code like '%"+ProdParam+"%' ",null);
                    if(ValidProd.moveToFirst()){
                        String ProdCondicion = " where Description like '%"+ProdParam+"%' or Code like '%"+ProdParam+"%' ";
                    }else{
                        Cursor CodeFromPr = Datab.rawQuery("select CodeProd from Presentations where CodePr like '%"+ProdParam+"%' or DescPr like '%"+ProdParam+"%' ",null);
                        //Toast.makeText(PosActivity.this, "Parametro de busqueda: " + ProdParam, Toast.LENGTH_SHORT).show();
                        if(CodeFromPr.moveToFirst()){
                            String CodeFromProdPr = CodeFromPr.getString(0);
                            String ProdCondicion = " where Description like '%"+CodeFromProdPr+"%' or Code like '%"+CodeFromProdPr+"%' ";
                            FillProds(ProdCondicion);
                            //Toast.makeText(PosActivity.this, "Se encontro coincidencia : " + ProdCondicion, Toast.LENGTH_LONG).show();
                        }else{
                            //Toast.makeText(PosActivity.this, "Sin coincidencia", Toast.LENGTH_SHORT).show();
                            FillProds("");
                        }
                    }
                }else{
                    FillProds("");
                }
                ProdToFind.setText("");
            }
        });


        FilterLine.setOnClickListener(new View.OnClickListener() {
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




        ListView_Pos_ListProds.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                String CodeProd = ProdsArray.get(position).getCode();

                if(PrEnabled.equals("1")){
                    SQLiteDatabase dbc = link.getReadableDatabase();
                    Cursor CountAdds = dbc.rawQuery("select count(id) as conteo from Presentations Where CodeProd = '"+CodeProd+"'  ",null);
                    Integer Conteo = 0;
                    if(CountAdds.moveToFirst()){
                        Conteo = CountAdds.getInt(0);
                    }
                    if(Conteo > 0){
                        ViewPresentations(CodeProd);
                    }else{
                        InsertPart(CodeProd,"base");
                    }
                }else{
                    InsertPart(CodeProd,"base");
                }
            }
        });


        //Valida permisos de la app
        if (ContextCompat.checkSelfPermission(PosActivity.this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(PosActivity.this,
                    new String[]{Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CAMERA );

        }


        if(NumSale.equals("")){
            TextView TextView_Total = (TextView) findViewById(R.id.TextView_Pos_Total);
            TextView_Total.setText("");
        }else{
            getTotal();
        }





        if(NumSale.equals("")){

        }else{
            getClientData();
        }


    }//===================================================  Final de OnCreate ==================================================









    //Segunda parte permisos de camara
    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                }else{
                    Toast.makeText(this, "No podemos usar la camara, el scanner no esta disponible", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }


























    private void ViewPresentations(final String ProdCode){

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View Presentations = LayoutInflater.from(getApplicationContext()).inflate(view_presentations, null);
        builder.setView(Presentations);
        builder.setTitle("Presentaciones del producto");
        builder.setNeutralButton("Ingresa como producto base", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                InsertPart(ProdCode,"base");
            }
        });
        final AlertDialog adTrueDialog;
        adTrueDialog = builder.show();

        /* ============================================= Vista de Presentaciones ==============================================*/

        final SQLiteDatabase db = link.getReadableDatabase();
        PresentationsArray = new ArrayList<presentations_constructor>();

        Cursor cPr = db.rawQuery("select id,CodePr,CodeProd,DescPr,QtyPr,PricePr from Presentations where CodeProd = '"+ProdCode+"' and DescPr <> ''  ",null);
        if(cPr.moveToFirst()){
            do{
                PresentationRow = new presentations_constructor(cPr.getInt(0),cPr.getString(1),cPr.getString(3),cPr.getDouble(4),cPr.getDouble(5));
                PresentationsArray.add(PresentationRow);
            }while (cPr.moveToNext());
        }

        final ListView ListPr = Presentations.findViewById(R.id.Presentations_List);
        presentations_adapter adapter = new presentations_adapter(getApplicationContext(), PresentationsArray);
        ListPr.setAdapter(adapter);


        ListPr.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String CodePresentation = PresentationsArray.get(position).getCodePResentation();
                InsertPart(CodePresentation,"PR");
                adTrueDialog.dismiss();
            }
        });

    }






















    private void InsertPart(String Codigo,String Presentation){


        link = new connection(getApplicationContext(), "MyBusinessAndroid", null, 1);
        SQLiteDatabase db = link.getWritableDatabase();

        String PresentationCode = "";
        String PrDescrip = "";
        Double PrPrice = 0.0;
        Double PrQty = 0.0;
        Double QtyPart = 0.0;
        String CodePart = "";

        // ============================ Valida si es una presentacion ==================================
        if(Presentation.equals("PR")){
            Cursor DataAdd = db.rawQuery("select id,CodePr,CodeProd,DescPr,QtyPr,PricePr from Presentations where CodePr = '"+Codigo+"' ",null);
            if(DataAdd.moveToFirst()){
                PresentationCode = DataAdd.getString(1);
                CodePart = DataAdd.getString(2);
                PrDescrip = DataAdd.getString(3);
                PrPrice = DataAdd.getDouble(5);
                PrQty = DataAdd.getDouble(4);
                QtyPart = PrQty;
            }
        }else{
            PresentationCode = "";
            PrDescrip = "";
            PrPrice = 0.0;
            PrQty = 0.0;
            QtyPart = 1.0;
            CodePart = Codigo;
        }



        if(NumSale.equals("")){
            InsertSale("SYS");
        }

        Cursor QueryProd = db.rawQuery("select id,Code,Description,ProdCategory,Brand,Tax,Cost,Price1,Price2,Price3,Price4,Price5,Price6,Price7,Price8,Price9,Qty2,Qty3,Qty4,Qty5,Qty6,Qty7,Qty8,Qty9,Exist,ExSucursal,Export,Offer,image from prods where Code = '"+ CodePart +"' ",null);

        if(QueryProd.moveToFirst()){
            DescripProd = QueryProd.getString(2);
            Price1 = String.valueOf(QueryProd.getDouble(7));
            Price2 = String.valueOf(QueryProd.getDouble(8));
            Price3 = String.valueOf(QueryProd.getDouble(9));
            Price4 = String.valueOf(QueryProd.getDouble(10));
            Price5 = String.valueOf(QueryProd.getDouble(11));
            Price6 = String.valueOf(QueryProd.getDouble(12));
            Price7 = String.valueOf(QueryProd.getDouble(13));
            Price8 = String.valueOf(QueryProd.getDouble(14));
            Price9 = String.valueOf(QueryProd.getDouble(15));

            Qty2 = QueryProd.getDouble(16);
            Qty3 = QueryProd.getDouble(17);
            Qty4 = QueryProd.getDouble(18);
            Qty5 = QueryProd.getDouble(19);
            Qty6 = QueryProd.getDouble(20);
            Qty7 = QueryProd.getDouble(21);
            Qty8 = QueryProd.getDouble(22);
            Qty9 = QueryProd.getDouble(23);
        }

        //Valida si el precio es por cantidad vendida y no es una presentacion
        if(Presentation.equals("base")){
            PricePart = Double.valueOf(Price1);
        }else{
            PricePart = PrPrice;
        }

        Cursor ValidSalePart = db.rawQuery("select Id,ProdCode,PrCode,PrDescrip from SalesParts where SaleID = '"+NumSale+"' and ProdCode = '"+ CodePart +"' and PrCode = '"+PresentationCode+"' ",null);

        if(ValidSalePart.moveToFirst()) {
            IdPart = ValidSalePart.getString(0);
            db.execSQL("update SalesParts set Qty = Qty + '"+QtyPart+"' where Id = '"+ IdPart +"' ");
        }else{
            ContentValues values = new ContentValues();
            values.put("SaleID", NumSale);
            values.put("ProdCode", CodePart);
            values.put("ProdDescrip", DescripProd);
            values.put("Qty", QtyPart);
            values.put("Price",PricePart);
            values.put("PriceList", 1);
            values.put("Discount", 0);
            values.put("PrCode", PresentationCode);
            values.put("PrDescrip", PrDescrip);
            values.put("PrQty", PrQty);
            values.put("PrPrice", PrPrice);
            Long IdPartInsert = db.insert("SalesParts", "id", values);
            IdPart = String.valueOf(IdPartInsert);
        }


        if(PriceQty == 1){
            Cursor QtyValue = db.rawQuery("select Qty from SalesParts where Id = '"+ IdPart +"' " ,null);
            Double QtyVal = 0.00;

            if(QtyValue.moveToFirst()){
                QtyVal = QtyValue.getDouble(0);
            }


            Integer AsignatedPrice = 0;

            if(Qty2 > 0){
                if(QtyVal >= Qty2){
                    AsignatedPrice = 2;
                }
            }

            if(Qty3 > 0){
                if(QtyVal >= Qty3){
                    AsignatedPrice = 3;
                }
            }

            if(Qty4 > 0){
                if(QtyVal >= Qty4){
                    AsignatedPrice = 4;
                }
            }

            if(Qty5 > 0){
                if(QtyVal >= Qty5){
                    AsignatedPrice = 5;
                }
            }


            if(Qty6 > 0){
                if(QtyVal >= Qty6){
                    AsignatedPrice = 6;
                }
            }


            if(Qty7 > 0){
                if(QtyVal >= Qty7){
                    AsignatedPrice = 7;
                }
            }


            if(Qty8 > 0){
                if(QtyVal >= Qty8){
                    AsignatedPrice = 8;
                }
            }

            if(Qty9 > 0){
                if(QtyVal >= Qty9){
                    AsignatedPrice = 9;
                }
            }

            String PriceToUse = "";

            if(AsignatedPrice==0){
                PriceToUse = Price1;
            }else if(AsignatedPrice==2){
                PriceToUse = Price2;
                Toast.makeText(this, "Se asigna precio "+ AsignatedPrice +" por el volumen de compra ", Toast.LENGTH_SHORT).show();
            }else if(AsignatedPrice==3){
                PriceToUse = Price3;
                Toast.makeText(this, "Se asigna precio "+ AsignatedPrice +" por el volumen de compra ", Toast.LENGTH_SHORT).show();
            }else if(AsignatedPrice==4){
                PriceToUse = Price4;
                Toast.makeText(this, "Se asigna precio "+ AsignatedPrice +" por el volumen de compra ", Toast.LENGTH_SHORT).show();
            }else if(AsignatedPrice==5){
                PriceToUse = Price5;
                Toast.makeText(this, "Se asigna precio "+ AsignatedPrice +" por el volumen de compra ", Toast.LENGTH_SHORT).show();
            }else if(AsignatedPrice==6){
                PriceToUse = Price6;
                Toast.makeText(this, "Se asigna precio "+ AsignatedPrice +" por el volumen de compra ", Toast.LENGTH_SHORT).show();
            }else if(AsignatedPrice==7){
                PriceToUse = Price7;
                Toast.makeText(this, "Se asigna precio "+ AsignatedPrice +" por el volumen de compra ", Toast.LENGTH_SHORT).show();
            }else if(AsignatedPrice==8){
                PriceToUse = Price8;
                Toast.makeText(this, "Se asigna precio "+ AsignatedPrice +" por el volumen de compra ", Toast.LENGTH_SHORT).show();
            }else if(AsignatedPrice==9){
                PriceToUse = Price9;
                Toast.makeText(this, "Se asigna precio "+ AsignatedPrice +" por el volumen de compra ", Toast.LENGTH_SHORT).show();
            }

            db.execSQL("update SalesParts set price = '"+ PriceToUse +"' where Id = '"+ IdPart +"' ");

        }

        String PartsCondicion = " where SaleID = '"+ NumSale +"' ";
        FillParts(PartsCondicion);
        link.close();
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

                if(NumSale.equals("")){
                    InsertSale(ClientCode);
                }

                dbW.execSQL("UPDATE Sales set Client = '" + ClientCode + "' where id = '"+NumSale+"' ");

                getClientData();
                Toast.makeText(PosActivity.this, "El cliente " + ClientsArray.get(position).getNameClient() + " se asigno la venta actual", Toast.LENGTH_SHORT).show();
                return false;
            }
        });


        AlertDialog OrderPartsDialog = builder.create();
        OrderPartsDialog.show();
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

                Cursor presentation = db.rawQuery("Select id,Code,Description,ProdCategory,Brand,Tax,Price1,Exist,ExSucursal,Export,Offer from Prods INNER JOIN Presentations ON Presentations.CodeProd = Prods.Code " , null);


            Toast.makeText(this, "No hay ningun producto", Toast.LENGTH_SHORT).show();
        }

        prodsAdapter adapter = new prodsAdapter(getApplicationContext(), ProdsArray);
        ListView_Pos_ListProds.setAdapter(adapter);
        link.close();
    }
































    private void getClientData(){

        SQLiteDatabase dbR = link.getReadableDatabase();
        Cursor cursorCl = dbR.rawQuery("SELECT Name FROM Clients INNER JOIN Sales ON Clients.CodeMyBusiness = Sales.Client WHERE Sales.id =  " + NumSale,null);
        if(cursorCl.moveToFirst()){
            ClientSale.setText(cursorCl.getString(0));
        }

    }


    //********** ***************** ************** ********************* ************************* ***************** **************















    //****************************************************************************************************************************
    private void PartOptions(final Integer IdPart){

        SQLiteDatabase db = link.getWritableDatabase();
        Cursor Partdata = db.rawQuery("SELECT " +
                "SalesParts.id, " +
                "SalesParts.ProdDescrip, " +
                "SalesParts.Qty, " +
                "SalesParts.Discount, " +
                "Prods.Price1, " +
                "prods.Price2, " +
                "prods.Price3, " +
                "prods.Price4, " +
                "prods.Price5," +
                "prods.Price6," +
                "prods.Price7," +
                "prods.Price8," +
                "prods.Price9, " +
                "SalesParts.PrDescrip, " +
                "SalesParts.PrQty, " +
                "SalesParts.weight, " +
                "SalesParts.price " +
                "FROM SalesParts " +
                "INNER JOIN Prods ON Prods.Code = SalesParts.ProdCode " +
                "where SalesParts.id = " + IdPart,null);

        Double QtyValue = 0.00;
        Integer DiscountValue = 0;
        Integer Pr1 = 0;
        Integer Pr2 = 0;
        Integer Pr3 = 0;
        Integer Pr4 = 0;
        Integer Pr5 = 0;
        Integer Pr6 = 0;
        Integer Pr7 = 0;
        Integer Pr8 = 0;
        Integer Pr9 = 0;
        String PrDescrip = "";
        Double PrQty = 0.0;
        Double RegWeight = 0.0;
        Double RegPrice = 0.0;
        String ProductDescrip = "";

        if(Partdata.moveToFirst()){
            ProductDescrip = Partdata.getString(1);
            QtyValue = Partdata.getDouble(2);
            DiscountValue = Partdata.getInt(3);
            Pr1 = Partdata.getInt(4);
            Pr2 = Partdata.getInt(5);
            Pr3 = Partdata.getInt(6);
            Pr4 = Partdata.getInt(7);
            Pr5 = Partdata.getInt(8);
            Pr6 = Partdata.getInt(9);
            Pr7 = Partdata.getInt(10);
            Pr8 = Partdata.getInt(11);
            Pr9 = Partdata.getInt(12);
            PrDescrip = Partdata.getString(13);
            PrQty = Partdata.getDouble(14);
            RegWeight = Partdata.getDouble(15);
            RegPrice = Partdata.getDouble(16);
        }


        if(PrQty > 0){
            QtyValue = QtyValue/PrQty;
        }else{
            QtyValue = QtyValue;
        }



        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View PartsOptions = LayoutInflater.from(getApplicationContext()).inflate(view_part_options, null);
        builder.setView(PartsOptions);

        final EditText DiscountEt = PartsOptions.findViewById(R.id.PartOption_Discount);
        final EditText Qty = PartsOptions.findViewById(R.id.PartOption_Qty);
        final EditText weight = PartsOptions.findViewById(R.id.partOptions_weight);
        final EditText FreePrice = PartsOptions.findViewById(R.id.partOptions_FreePrice);
        final EditText partObserv = PartsOptions.findViewById(R.id.partOptions_PartObserv);



        final ArrayList<String> Prices = new ArrayList<>();
        Prices.add(String.valueOf(Pr1));

        ListOfPrice = "1";

        if(Pr2>0){
            Prices.add(String.valueOf(Pr2));
            if(String.valueOf(Pr2)==PriceSelect){
                ListOfPrice="2";
            }
        }


        if(Pr3>0){
            Prices.add(String.valueOf(Pr3));
            if(String.valueOf(Pr3)==PriceSelect){
                ListOfPrice="3";
            }
        }


        if(Pr4>0){
            Prices.add(String.valueOf(Pr4));
            if(String.valueOf(Pr4)==PriceSelect){
                ListOfPrice="4";
            }
        }



        if(Pr5>0){
            Prices.add(String.valueOf(Pr5));
            if(String.valueOf(Pr5)==PriceSelect){
                ListOfPrice="5";
            }
        }

        if(Pr6>0){
            Prices.add(String.valueOf(Pr6));
            if(String.valueOf(Pr6)==PriceSelect){
                ListOfPrice="6";
            }
        }

        if(Pr7>0){
            Prices.add(String.valueOf(Pr7));
            if(String.valueOf(Pr7)==PriceSelect){
                ListOfPrice="7";
            }
        }




        if(Pr8>0){
            Prices.add(String.valueOf(Pr8));
            if(String.valueOf(Pr8)==PriceSelect){
                ListOfPrice="8";
            }
        }







        if(Pr9>0){
            Prices.add(String.valueOf(Pr9));
            if(String.valueOf(Pr9)==PriceSelect){
                ListOfPrice="9";
            }
        }



        Spinner PriceOptions = PartsOptions.findViewById(R.id.spinner_priceOptions);
        ArrayAdapter<CharSequence> adapSpinnerPrices = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, Prices);
        PriceOptions.setAdapter(adapSpinnerPrices);

        PriceOptions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                PriceSelect = Prices.get(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });










        DiscountEt.setText(String.valueOf(DiscountValue));
        Qty.setText(String.valueOf(QtyValue));
        weight.setText(String.valueOf(RegWeight));
        FreePrice.setText(String.valueOf(RegPrice));




        final Double finalPrQty = PrQty;
        final String finalProductDescrip = ProductDescrip;

        builder.setPositiveButton("Actualiza la partida", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String NewDiscount = DiscountEt.getText().toString();
                String NewQty = Qty.getText().toString();
                String ObservPart = partObserv.getText().toString();
                Double PriceMn = Double.valueOf(FreePrice.getText().toString());
                Double NwWeight = Double.valueOf(weight.getText().toString());


                if(finalPrQty >0){
                    NewQty = String.valueOf(Double.valueOf(NewQty)*finalPrQty);
                }

                if(Integer.valueOf(NewDiscount) > 99){
                    Toast.makeText(PosActivity.this, "No puedes dar descuentos mayores a 100%", Toast.LENGTH_SHORT).show();
                }else{
                    UpdatePart(NewQty,NewDiscount,PriceSelect,ListOfPrice,IdPart,PriceMn,NwWeight,ObservPart, finalProductDescrip);
                }
            }
        });


        builder.setNegativeButton("Elimina la partida", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SQLiteDatabase dbw = link.getWritableDatabase();
                dbw.execSQL("delete from SalesParts where id = " + IdPart);
                String PartsCondicion = " where SaleID = '"+ NumSale +"' ";
                FillParts(PartsCondicion);
            }
        });

        AlertDialog PartOptions = builder.create();
        PartOptions.show();
    }








































    //****************************************************************************************************************************
    private void ObservToSale(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View SalesObserv = LayoutInflater.from(getApplicationContext()).inflate(sales_observ, null);
        builder.setView(SalesObserv);

        SQLiteDatabase db = link.getReadableDatabase();
        Cursor ObservCursor = db.rawQuery("select Observ from Sales where id = '"+ NumSale +"' ",null);
        final EditText ObservEditText = SalesObserv.findViewById(R.id.Pos_editText_observSale);

        if(ObservCursor.moveToFirst()){
            String ObservReg = ObservCursor.getString(0);
            ObservEditText.setText(ObservReg);
        }

        builder.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(PosActivity.this, "Observacion: " + ObservEditText.getText().toString(), Toast.LENGTH_SHORT).show();
                SaveObserv(ObservEditText.getText().toString());
            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(PosActivity.this, "Las observaciones no se guardaron", Toast.LENGTH_SHORT).show();
            }
        });

        AlertDialog InsertObserv = builder.create();
        InsertObserv.show();
    }

    private void SaveObserv(String ObservToReg){

        SQLiteDatabase dbw = link.getWritableDatabase();
        dbw.execSQL("update Sales set Observ = '"+ ObservToReg +"' where id = '" + NumSale + "'");
        //Toast.makeText(this, "" + ObservToReg + " En venta " + NumSale, Toast.LENGTH_SHORT).show();
    }







































    public void UpdatePart(String Qty, String Discount, String PriceSelect, String ListPrice, Integer IdPartida,Double FreePrice,Double weight,String ObservPart,String ProdDescrip){
        SQLiteDatabase dbw = link.getWritableDatabase();

        String UpPrice = "";

        if (FreePrice > 0) {
            UpPrice = String.valueOf(FreePrice);
        }else{
            UpPrice = PriceSelect;
        }

        String NwProdDescrip = ProdDescrip + " " + ObservPart;


        dbw.execSQL("update SalesParts set Qty='"+ Qty +"',Discount='"+Discount+"',Price='"+UpPrice+"',PriceList='"+ListPrice+"',weight='"+ weight +"',ProdDescrip = '"+ NwProdDescrip +"' where id = " + IdPartida);

        String PartsCondicion = " where SaleID = '"+ NumSale +"' ";
        FillParts(PartsCondicion);
    }















    //*********************************************************************************************************************************
    //*********************************************    Metodos de Pago    *************************************************************

    private void FinishSale(){

        if(PendingSale > 0){

            SQLiteDatabase db = link.getWritableDatabase();

            Cursor PartsToSale = db.rawQuery("select ProdCode,Qty from SalesParts where SaleID = '"+ NumSale +"'  ",null);
            if(PartsToSale.moveToFirst()){
                do{
                    Double QtyPart = PartsToSale.getDouble(1);
                    String CodeProduct = PartsToSale.getString(0);
                    db.execSQL("update Prods set Exist = Exist - '"+ QtyPart +"' where Code = '"+ CodeProduct +"' ");
                }while (PartsToSale.moveToNext());
            }

            db.execSQL("update Sales set PayMethod = 'Pendiente', Consecutive='PE',Status='Cerrada',ImportPay = '0' where id = '"+ NumSale +"' ");
            new SyncSale().execute();

        }else{

            Intent GoFinish = new Intent(this,FinishSaleActivity.class);
            GoFinish.putExtra("NumSale",NumSale);
            startActivity(GoFinish);

        }
    }











    /*   ===========================================     Envia venta pendiente    ==========================================*/
    public class SyncSale extends AsyncTask<String,Integer,String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(PosActivity.this, "Registrando", "Espere, por favor...");

        }


        @Override
        protected String doInBackground(String... strings) {

            SQLiteDatabase db = link.getReadableDatabase();
            Cursor SaleData = db.rawQuery("select * from Sales where ID = " + NumSale,null);

            if (SaleData.moveToFirst()){
                ClientSend = SaleData.getString(2);
                ImportSend = SaleData.getString(6);
                PayMethod = SaleData.getString(10);
                ObservToSend = SaleData.getString(12);
            }


            final JSONObject Venta = new JSONObject();
            JSONObject Partidas = new JSONObject();
            JSONObject SecurePart = new JSONObject();

            try {
                Venta.put("AndroidID",NumSale);
                Venta.put("Client",ClientSend);
                Venta.put("Terminal",Terminal);
                Venta.put("User",UserLoged);
                Venta.put("Status","PE");
                Venta.put("Import",ImportSend);
                Venta.put("Warehouse",Warehouse);
                Venta.put("Remision",Remision);
                Venta.put("PayMethod","TAR");
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

                Cursor SaleParts = db.rawQuery("select SalesParts.ProdCode,SalesParts.Qty,SalesParts.Price,SalesParts.Discount,SalesParts.PrPrice,SalesParts.PrDescrip,SalesParts.PrQty,SalesParts.weight from SalesParts where SaleID = '"+ NumSale +"' ",null);
                if(SaleParts.moveToFirst()){
                    do{

                        String Codigo = SaleParts.getString(0);
                        String Cantidad = SaleParts.getString(1);
                        String PartPrice = SaleParts.getString(2);
                        String PartDiscount = SaleParts.getString(3);
                        String PartClavePR = "";
                        String PartDescripPR = SaleParts.getString(5);
                        String PartQtyPr = SaleParts.getString(6);
                        String weight = SaleParts.getString(7);


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
                        Partidas.put("weight",weight);
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
                    //Toast.makeText(PosActivity.this, "" + Venta.toString(), Toast.LENGTH_SHORT).show();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //Toast.makeText(PosActivity.this, "" + Venta.toString(), Toast.LENGTH_SHORT).show();
                    CloseOp("");
                    //ErrorOp("La venta se queda en el dispositivo, no se pudo enviar al servidor" );
                }
            });
            RequestQueue Sync = Volley.newRequestQueue(getApplicationContext());
            Sync.add(SyncSale);
            return null;
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();

        }

    }

/* =================================    Fin de envio de venta ===================================*/




















    public void CloseOp(final String cadena) {

        SQLiteDatabase db = link.getWritableDatabase();
        db.execSQL("update Sales set Consecutive='"+ cadena +"',Export = 1 where id = '"+ NumSale +"' ");

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setMessage("La venta " + cadena + " esta registrada como pendiente en el servidor");
        dialogBuilder.setCancelable(false).setTitle("Estado de la sincronizacion");
        dialogBuilder.setNeutralButton("Listo,Siguiente Venta", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent goPos = new Intent(getApplicationContext(),PosActivity.class);
                startActivity(goPos);

                if(EnabledPrintTk==1){
                    printTk(Integer.valueOf(NumSale), Integer.valueOf(cadena));
                }

            }
        });
        dialogBuilder.create().show();
    }










    public void ErrorOp(String cadena) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setMessage(cadena);
        dialogBuilder.setCancelable(false).setTitle("Estado de la sincronizacion");
        dialogBuilder.setNeutralButton("Ok , siguiente venta", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent goPos = new Intent(getApplicationContext(),PosActivity.class);
                startActivity(goPos);

            }
        });
        dialogBuilder.create().show();
    }



















/*
    private void  Test(){

        SQLiteDatabase db = link.getReadableDatabase();
        Cursor SaleData = db.rawQuery("select * from Sales where ID = " + NumSale,null);

        if (SaleData.moveToFirst()){
            ClientSend = SaleData.getString(2);
            ImportSend = SaleData.getString(6);
        }

        JSONObject Venta = new JSONObject();
        JSONObject Partidas = new JSONObject();
        JSONObject SecurePart = new JSONObject();

        try {
            Venta.put("AndroidID",NumSale);
            Venta.put("Client",ClientSend);
            Venta.put("Terminal",Terminal);
            Venta.put("User",UserLoged);
            Venta.put("Status","PE");
            Venta.put("Import",ImportSend);
            Venta.put("Warehouse",Warehouse);

            SecurePart.put("Codigo","DeleteME");
            SecurePart.put("Qty","0");
            SecurePart.put("Price","0");
            SecurePart.put("Discount","0");
            Venta.accumulate("Partidas",SecurePart);


            Cursor SaleParts = db.rawQuery("select * from SalesParts where SaleID = '"+ NumSale +"' ",null);
            if(SaleParts.moveToFirst()){
                do{
                    String Codigo = SaleParts.getString(2);
                    String Cantidad = SaleParts.getString(4);
                    String PartPrice = SaleParts.getString(5);
                    String PartDiscount = SaleParts.getString(7);

                    Partidas.put("Codigo",Codigo);
                    Partidas.put("Qty",Cantidad);
                    Partidas.put("Price",PartPrice);
                    Partidas.put("Discount",PartDiscount);

                    Venta.accumulate("Partidas",Partidas);


                    Partidas = new JSONObject();

                }while(SaleParts.moveToNext());
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }




        String UrlSync = "http://"+ServerIp+"/INTEMWS/SyncSale.php";

        Toast.makeText(this, "Url: " + UrlSync, Toast.LENGTH_SHORT).show();

        Toast.makeText(this, "" + Venta.toString(), Toast.LENGTH_SHORT).show();

}

*/




























































    private void InsertSale(String Client){

        link = new connection(getApplicationContext(), "MyBusinessAndroid", null, 1);
        SQLiteDatabase db = link.getWritableDatabase();

        SimpleDateFormat SaleDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Date date = new Date();
        String S_Date = SaleDate.format(date);

        ContentValues values = new ContentValues();
        values.put("Client", Client);
        values.put("Date", S_Date);
        values.put("Status", "PR");
        values.put("Export", 0);
        values.put("Import", 0);
        values.put("Corte", 0);
        values.put("Credit", 0);
        values.put("DebtID", 0);
        values.put("PayMethod", "");

        Long IdSale = db.insert("Sales", "id", values);
        NumSale = String.valueOf(IdSale);

        getClientData();
        link.close();

    }




















    private void SpinnerLines() {

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





























    private void FillParts(String cCondicion) {

        SQLiteDatabase db = link.getReadableDatabase();
        Cursor c = db.rawQuery("Select SalesParts.id," +
                "SalesParts.saleID," +
                "SalesParts.prodCode," +
                "CASE When SalesParts.PrQty > 0 Then SalesParts.Qty/SalesParts.PrQty ELSE SalesParts.Qty end AS Qty," +
                "CASE WHEN SalesParts.PrPrice > 0 THEN SalesParts.PrPrice*SalesParts.PrQty ELSE SalesParts.price END AS Price," +
                "SalesParts.priceList," +
                "SalesParts.discount," +
                "CASE When SalesParts.PrQty > 0 Then SalesParts.PrDescrip else SalesParts.ProdDescrip END As descrip " +
                "from SalesParts "+ cCondicion +" ", null);

        PartsArray = new ArrayList<partsSaleConstructor>();

        if (c.moveToFirst()){
            do {
                partsItem = new partsSaleConstructor(c.getInt(0),c.getInt(1),c.getString(2),c.getDouble(3),c.getDouble(4),c.getInt(5),c.getDouble(6),c.getString(7));
                PartsArray.add(partsItem);
            } while (c.moveToNext());
        }

        salePartsAdapter PartsAdapter = new salePartsAdapter(PosActivity.this, PartsArray);
        ListView_Pos_ListParts.setAdapter(PartsAdapter);


        ListView_Pos_ListParts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Integer IdPart = PartsArray.get(position).getId();
                PartOptions(IdPart);
            }
        });

        getTotal();
        link.close();
    }






















    private void getTotal(){
        TextView TextView_Total = (TextView) findViewById(R.id.TextView_Pos_Total);
        SQLiteDatabase db = link.getReadableDatabase();
        Cursor ImportSale = db.rawQuery("select sum(Qty * (Price * ((100 - Discount)/100) ) ) as sum from SalesParts where SaleID = '"+ NumSale +"' ",null);
        if(ImportSale.moveToNext()){
            TotalSale = ImportSale.getDouble(0);
            db.execSQL("update Sales set Import = '"+TotalSale+"' where id = " + NumSale);
            java.text.DecimalFormat formateador = new java.text.DecimalFormat("###,###,###,###.00");
            String AmountSale = formateador.format(ImportSale.getDouble(0));
            TextView_Total.setText("$ " + AmountSale);
        }else{
            TextView_Total.setText("");
        }

    }















    public void ScanButton(View v){

        if (ContextCompat.checkSelfPermission(PosActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "No permitiste usar la camara,el scanner no esta disponible", Toast.LENGTH_SHORT).show();
        }else{
            zXingScannerView = new ZXingScannerView(this);
            zXingScannerView.setResultHandler(PosActivity.this);
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
        Intent intent = new Intent(PosActivity.this,PosActivity.class);
        intent.putExtra("ProdCode",Codigo);
        intent.putExtra("NumSale",NumSale);
        startActivity(intent);
        StatusCamera = "";
    }


    @Override
    public void onBackPressed() {
        if(StatusCamera.equals("")){
            if(NumSale.equals("")){
                Intent goMenu = new Intent(getApplicationContext(),MenuActivity.class);
                startActivity(goMenu);
            }else{
                ValidExit();
            }
        }else{
            Intent returnPos = new Intent(getApplicationContext(),PosActivity.class);
            startActivity(returnPos);
        }
    }






    private void ValidExit(){
        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
        dialogo1.setTitle("Importante");
        dialogo1.setMessage("Existe una venta abierta, ¿ Quieres conservarla ?");
        dialogo1.setCancelable(false);

        dialogo1.setNegativeButton("No,Borrala", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
                DeleteSale();
            }
        });

        dialogo1.setPositiveButton("Guardala pendiente", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            public void onClick(DialogInterface dialogo1, int id) {
                KeepSale();
            }
        });
        dialogo1.show();
    }







    private void DeleteSale(){
        SQLiteDatabase dbW = link.getWritableDatabase();
        dbW.execSQL("delete from SalesParts where SaleID = '"+ NumSale +"' ");
        dbW.execSQL("delete from Sales where id = '"+ NumSale +"' ");
        Toast.makeText(this, "La venta fue eliminada del mobil", Toast.LENGTH_SHORT).show();
        Intent goMenu = new Intent(getApplicationContext(),MenuActivity.class);
        startActivity(goMenu);
    }








    private void KeepSale(){
        SQLiteDatabase dbW = link.getWritableDatabase();
        dbW.execSQL("update Sales set Status = 'Pendiente' where id = '"+ NumSale +"' ");
        Toast.makeText(this, "La venta " + NumSale + " quedo pendiente", Toast.LENGTH_SHORT).show();
        Intent goMenu = new Intent(getApplicationContext(),MenuActivity.class);
        startActivity(goMenu);
    }
























    private void printTk(Integer Sale,Integer IdInServer){
        SQLiteDatabase dbR = link.getReadableDatabase();
        Cursor cSale = dbR.rawQuery("SELECT Sales.id,Clients.Name,Sales.import,Sales.Date FROM Sales INNER JOIN Clients ON Sales.Client = Clients.CodeMyBusiness where Sales.id = '"+Sale+"' " ,null);
        Cursor cSaleParts = dbR.rawQuery("SELECT SalesParts.Qty,SalesParts.ProdDescrip,SalesParts.Price from SalesParts where SaleID = " + Sale,null);

        String PrintCont = "";

        String S_Client = null;
        Double S_Import = null;
        String S_Date = null;
        String S_Status = null;

        if(cSale.moveToFirst()){
            do {
                S_Client = cSale.getString(1);
                S_Import = cSale.getDouble(2);
                S_Date = cSale.getString(3);
                S_Status = "Venta pendiente en Servidor: " + IdInServer;
            }while (cSale.moveToNext());
        }


        PrintCont = "$bigh$"+Company+"$big$\n"+AdressCompany+"\n\nTelefono:"+ PhoneCompany +"\nFecha:"+S_Date+"\n\n Venta Pendiente \n" +
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

        PrintCont = PrintCont + "============================= \n\n$bigh$Total :  " + S_Import;
        PrintCont = PrintCont + " \n\n\n $intro$ ";

        String dataToPrint = PrintCont;
        Intent intentPrint = new Intent();

        intentPrint.setAction(Intent.ACTION_SEND);
        intentPrint.putExtra(Intent.EXTRA_TEXT, dataToPrint);
        intentPrint.setType("text/plain");

        startActivity(intentPrint);

        link.close();
    }















}
