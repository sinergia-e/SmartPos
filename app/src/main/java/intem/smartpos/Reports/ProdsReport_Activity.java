package intem.smartpos.Reports;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
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
import intem.smartpos.activities.AppConfigActivity;
import intem.smartpos.activities.Reports_Activity;
import intem.smartpos.adapters.ReportProds_Adapter;
import intem.smartpos.constructors.ReportProds_Constructor;
import intem.smartpos.database.connection;

import static intem.smartpos.R.layout.view_prodsreport_dataproduct;


public class ProdsReport_Activity extends AppCompatActivity {

    connection link;

    private ImageButton SelectFilters,FindProd;
    private String ServerIp,Terminal,Condicion,DescripCondicion,InitDate,FinalDate;
    private EditText ProdToFind;
    private Integer DataSize;
    private double NumRegs;
    private ProgressDialog dialog;
    private TextView DescripFilter;
    private ArrayList<ReportProds_Constructor> ProdsDataArray;
    ReportProds_Constructor ItemReportProd;
    private ListView ListDataProdsReport;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prods_report_);
        link = new connection(getApplicationContext(), "MyBusinessAndroid", null, 1);

        DescripFilter = (TextView) findViewById(R.id.TextView_ProdsReport_DescripFilter);
        ProdToFind = (EditText) findViewById(R.id.EditText_ProdsReport_ProdToFind);
        SelectFilters = (ImageButton) findViewById(R.id.ImageButton_ReportProds_SelectFilter);
        FindProd = (ImageButton) findViewById(R.id.ImageButton_ProdsReport_FindProd);
        ListDataProdsReport = (ListView) findViewById(R.id.ListView_Reports_Prods);


        dialog = new ProgressDialog(this);
        dialog.setMessage("Recuperando informacion...");
        dialog.setTitle("Progreso");
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setCancelable(false);
        //==================================================================================================

        SQLiteDatabase dbR = link.getReadableDatabase();
        Cursor cConfig = dbR.rawQuery("SELECT ServerIP,OnlyExist,DataSize,Terminal,SendProds,ImportSucEx from AppConfig ",null);
        if(cConfig.moveToFirst()){
            ServerIp = cConfig.getString(0);
            DataSize = cConfig.getInt(2);
            Terminal = cConfig.getString(3);
        }else{
            Toast.makeText(this, "Falta la configuracion del servidor, corrigelo", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this,AppConfigActivity.class);
            startActivity(intent);
        }
        //==================================================================================================

        FindProd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Condicion = "";
                if(ProdToFind.equals("")){
                    FillDataProds("");
                }else{
                    String ParamFind = ProdToFind.getText().toString();
                    Condicion = " and Prods.Description like '%"+ ParamFind +"%' or Code like '%"+ ParamFind+"%' ";
                    FillDataProds(Condicion);
                }
            }
        });
        //==================================================================================================
        SelectFilters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent GoFilters = new Intent(getApplicationContext(),ProdsReportFilter_Activity.class);
                startActivity(GoFilters);
            }
        });

        //==================================================================================================

        Bundle Filters = getIntent().getExtras();
        if (Filters != null) {
            InitDate = Filters.getString("InitDate");
            FinalDate = Filters.getString("FinalDate");
            Condicion = Filters.getString("Condicion");
                if(InitDate.equals("") && FinalDate.equals("")){

                }else{
                    QuestionImportProd(Condicion);
                }
            DescripCondicion = Filters.getString("DescripCondicion");
            DescripFilter.setText(DescripCondicion);
            FillDataProds(Condicion);
        }else{
            Condicion = "";
            DescripCondicion = "Mostrando todos los resultados ";
            DescripFilter.setText(DescripCondicion);
            FillDataProds(Condicion);
        }





        ListDataProdsReport.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String ProdCode = ProdsDataArray.get(position).getProductCode();
                ViewOrdersPart(ProdCode);
            }
        });

    }











    //==========================================================================================================
    //***************** ****************   Confirma Recuperacion de reporte ********************* *************************

    private void QuestionImportProd(final String Condicion){
        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
        dialogo1.setTitle("Importante");
        dialogo1.setMessage("Vamos a recuperar la informacion conforme a las fechas ingresadas, de lo contrario se usara la informacion previa");
        dialogo1.setCancelable(false);

        dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
                FillDataProds("");
            }
        });
        dialogo1.setPositiveButton("Recuperalo", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
                RecoveryData();
            }
        });
        dialogo1.show();
    }



    //================================================ Llena el list de productos =================================================
    private void FillDataProds(String Condicion){

        SQLiteDatabase db = link.getReadableDatabase();
        Cursor c = db.rawQuery("Select AdditionalProductReportingData.id,ProductCode,Family,SubFam1,SubFam2,SubFam3,TotalPurchases,LastPurchaseDate,LastPurchasePrice,TotalSales,AverageSales,MainSupplier,Prods.Description,Prods.Exist from AdditionalProductReportingData INNER JOIN Prods ON Prods.Code = AdditionalProductReportingData.ProductCode where AdditionalProductReportingData.id is not null  " + Condicion , null);

        ProdsDataArray = new ArrayList<ReportProds_Constructor>();
        if(c.moveToFirst()){
            do{
                ItemReportProd = new ReportProds_Constructor(c.getInt(0), c.getString(1), c.getString(12),c.getString(2) ,c.getString(3), c.getString(4), c.getString(5), c.getDouble(6), c.getString(7),c.getString(8), c.getDouble(9), c.getDouble(10), c.getString(11),c.getDouble(13) );
                ProdsDataArray.add(ItemReportProd);
            }while(c.moveToNext());
        }else{
            Toast.makeText(this, "No hay resultados", Toast.LENGTH_LONG).show();
        }

        ReportProds_Adapter Adap = new ReportProds_Adapter(getApplicationContext(),ProdsDataArray);
        ListDataProdsReport.setAdapter(Adap);

    }












    //============================================
    private void ViewOrdersPart(String ProdToDet) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View DetProduct = LayoutInflater.from(getApplicationContext()).inflate(view_prodsreport_dataproduct, null);
        builder.setView(DetProduct);

        SQLiteDatabase db = link.getReadableDatabase();
        Cursor cDataProdAd = db.rawQuery("SELECT Prods.ProdCategory,Prods.Brand,AdditionalProductReportingData.MainSupplier,AdditionalProductReportingData.Family,AdditionalProductReportingData.SubFam1,AdditionalProductReportingData.SubFam2,AdditionalProductReportingData.SubFam3,AdditionalProductReportingData.LastPurchaseDate,AdditionalProductReportingData.LastPurchasePrice FROM AdditionalProductReportingData INNER JOIN Prods ON Prods.Code = AdditionalProductReportingData.ProductCode where AdditionalProductReportingData.ProductCode = '"+ ProdToDet + "' " , null);

        if(cDataProdAd.moveToFirst()){
            String Line = cDataProdAd.getString(0);
            TextView SetLine = DetProduct.findViewById(R.id.TextView_viewReportDtProd_Line);
            SetLine.setText(Line);

            String Brand = cDataProdAd.getString(1);
            TextView SetBrand = DetProduct.findViewById(R.id.TextView_viewReportDtProd_Brand);
            SetBrand.setText(Brand);

            String Proveed = cDataProdAd.getString(2);
            TextView SetProveed = DetProduct.findViewById(R.id.TextView_viewReportDtProd_Proveed);
            SetProveed.setText(Proveed);

            String Family = cDataProdAd.getString(3);
            TextView SetFamily = DetProduct.findViewById(R.id.TextView_viewReportDtProd_Family);
            SetFamily.setText(Family);

            String SubFamily1 = cDataProdAd.getString(4);
            TextView SetSFam1 = DetProduct.findViewById(R.id.TextView_viewReportDtProd_SubFam1);
            SetSFam1.setText(SubFamily1);

            String SubFamily2 = cDataProdAd.getString(5);
            TextView SetSFam2 = DetProduct.findViewById(R.id.TextView_viewReportDtProd_SubFam2);
            SetSFam2.setText(SubFamily2);

            String SubFamily3 = cDataProdAd.getString(6);
            TextView SetSFam3 = DetProduct.findViewById(R.id.TextView_viewReportDtProd_SubFam3);
            SetSFam3.setText(SubFamily3);

            String LastPurchase = cDataProdAd.getString(7);
            TextView SetLastPurchase = DetProduct.findViewById(R.id.TextView_viewReportDtProd_LastPurchase);
            SetLastPurchase.setText(LastPurchase);

            DecimalFormat formateador = new DecimalFormat("###,###,###,###.00");
            String LastPrPurchase = formateador.format(cDataProdAd.getDouble(8));
            TextView SetLastPRPurchase = DetProduct.findViewById(R.id.TextView_viewReportDtProd_LastPrPurch);
            SetLastPRPurchase.setText("$ " + LastPrPurchase);

        }

        AlertDialog DetProd = builder.create();
        DetProd.show();

    }














    private void RecoveryData(){
        //========================= Obtener numero de registros =====================================
        String UrlGetProdsReport = "http://"+ServerIp+"/INTEMWS/Reports/GetProdsData.php?count=true&Term=" + Terminal;
        JsonObjectRequest RequestCount = new JsonObjectRequest(Request.Method.GET, UrlGetProdsReport, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONArray json = response.optJSONArray("count");
                JSONObject RowQty = null;

                try {
                    RowQty = json.getJSONObject(0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                NumRegs = RowQty.optDouble("Qty");
                double RoundDiv = Math.ceil(NumRegs/DataSize);
                int loop = (int) Math.round(RoundDiv);
                new AsyncData().execute(loop);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ProdsReport_Activity.this, "No hay conexion al servidor", Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue CountRowsReport = Volley.newRequestQueue(getApplicationContext());
        CountRowsReport.add(RequestCount);
        //========================= Obtener numero de registros =====================================

    }








    private class AsyncData extends AsyncTask<Integer,Float,Integer> {
        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            dialog.setProgress(0);
            dialog.setMax(100);
            dialog.show();

        }

        @Override
        protected Integer doInBackground(Integer... loops) {

            String condicion = "";
            SQLiteDatabase dbW = link.getWritableDatabase();
            dbW.execSQL("delete from AdditionalProductReportingData " + condicion);
            for (int count = 0;count < loops[0]; count++ ){
                Float Prog = Float.valueOf( count * (DataSize/loops[0]));
                publishProgress(Prog);

                //======================================================================================
                String URL = "http://" + ServerIp + "/INTEMWS/Reports/GetProdsData.php?DataSize="+DataSize+"&Term="+Terminal+"&InitDate="+InitDate+"&FinalDate="+FinalDate;
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONArray json = response.optJSONArray("prodsData");
                        SQLiteDatabase dbW = link.getWritableDatabase();
                        JSONObject jsonObject = null;
                        try {
                            for(int rw=0;rw < json.length();rw++ ){
                                jsonObject = json.getJSONObject(rw);
                                String CodeProd = jsonObject.optString("Articulo");
                                String Family = jsonObject.optString("Familia");
                                String Subfam = jsonObject.optString("Subfamilia");
                                String SubFam1 = jsonObject.optString("Subfam1");
                                String SubFam2 = jsonObject.optString("Subfam2");
                                String TotalPurchases = jsonObject.optString("TotalPurchases");
                                String LastPurchase = jsonObject.optString("LastPurchase");
                                String LastPricePurchase = jsonObject.optString("LastPricePurchase");
                                String TotalSales = jsonObject.optString("TotalSales");
                                String MainSuplier = jsonObject.optString("MainSuplier");

                                dbW.execSQL("INSERT INTO AdditionalProductReportingData (ProductCode,Family,SubFam1,SubFam2,SubFam3,TotalPurchases,LastPurchaseDate,LastPurchasePrice,TotalSales,AverageSales,MainSupplier) values ('"+ CodeProd +"','"+ Family +"','"+ Subfam+"','"+ SubFam1+"','"+ SubFam2+"','"+ TotalPurchases +"','"+ LastPurchase+"','"+ LastPricePurchase+"','"+ TotalSales +"',0,'"+ MainSuplier+"')");

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
                RequestQueue request = Volley.newRequestQueue(getApplicationContext());
                request.add(jsonObjectRequest);
                //======================================================================================
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

            return null;

        }

        @Override
        protected void onProgressUpdate(Float... values) {
            int NewProg = Math.round(values[0]);
            dialog.setProgress(Integer.parseInt(String.valueOf(NewProg)));
            super.onProgressUpdate(values);
        }


        @Override
        protected void onPostExecute(Integer integer) {

            dialog.dismiss();
            Toast.makeText(ProdsReport_Activity.this, "Se copiaron los datos auxiliares", Toast.LENGTH_SHORT).show();
            FillDataProds("");
            super.onPostExecute(integer);
        }
    }










    public void onBackPressed() {
        Intent GoReportsList = new Intent(ProdsReport_Activity.this, Reports_Activity.class);
        startActivity(GoReportsList);
    }


}
