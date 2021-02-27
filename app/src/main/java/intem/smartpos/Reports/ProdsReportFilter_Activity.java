package intem.smartpos.Reports;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;

import java.util.ArrayList;

import intem.smartpos.R;
import intem.smartpos.database.connection;

public class ProdsReportFilter_Activity extends AppCompatActivity {

    connection link;
    public CheckBox ChkLine,ChkBrand,ChkProveed,ChkFam,ChkSfam1,ChkSfam2,ChkSfam3;
    public Spinner SpnLine,SpnBrand,SpnProveed,SpnFam,SpnSfam1,SpnSfam2,SpnSfam3,SpnOrder;
    public Button SendFilters,Cancel;
    public String Condicion,DescripCondicion,LineSelect,BrandSelect,ProvSelect,FamSelect,SfamSelect,Sfam1Select,Sfam2Select,Sfam3Select,OrderSelect;
    private ArrayList LinesList,BrandList,ProvList,FamList,Sfam1List,Sfam2List,Sfam3List,OrderList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prods_report_filter_);

        link = new connection(getApplicationContext(), "MyBusinessAndroid", null, 1);

        ChkLine = (CheckBox) findViewById(R.id.ProdReport_checkBoxLine);
        ChkBrand = (CheckBox) findViewById(R.id.ProdReport_checkBoxBrand);
        ChkProveed = (CheckBox) findViewById(R.id.ProdReport_checkBoxProveed);
        ChkFam = (CheckBox) findViewById(R.id.ProdReport_checkBoxFamily);
        ChkSfam1 = (CheckBox) findViewById(R.id.ProdReport_checkBoxSubFamily1);
        ChkSfam2 = (CheckBox) findViewById(R.id.ProdReport_checkBoxSubFamily2);
        ChkSfam3 = (CheckBox) findViewById(R.id.ProdReport_checkBoxSubFamily3);
        SpnLine = (Spinner) findViewById(R.id.ProdsReport_spinnerLine);
        SpnBrand = (Spinner) findViewById(R.id.ProdsReport_spinnerBrand);
        SpnProveed = (Spinner) findViewById(R.id.ProdsReport_spinnerProveed);
        SpnFam = (Spinner) findViewById(R.id.ProdsReport_spinnerFamily);
        SpnSfam1 = (Spinner) findViewById(R.id.ProdsReport_spinnerSubFamily1);
        SpnSfam2 = (Spinner) findViewById(R.id.ProdsReport_spinnerSubFamily2);
        SpnSfam3 = (Spinner) findViewById(R.id.ProdsReport_spinnerSubFamily3);
        SpnOrder  = (Spinner) findViewById(R.id.ProdsReport_spinnerOrder);
        SendFilters = (Button) findViewById(R.id.Button_ProdsReport_SendFilters);
        Cancel = (Button) findViewById(R.id.Button_ProdsReport_Cancel);




        getSpinnnersData();


        ArrayAdapter<CharSequence> adapSpinnerLn = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, LinesList);
        SpnLine.setAdapter(adapSpinnerLn);
        SpnLine.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                LineSelect = LinesList.get(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        ArrayAdapter<CharSequence> adapSpinnerBn = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, BrandList);
        SpnBrand.setAdapter(adapSpinnerBn);
        SpnBrand.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                BrandSelect = BrandList.get(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    // =================================================== Spinner del proveedor ===========================================
        ArrayAdapter<CharSequence> adapSpinnerPv = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, ProvList);
        SpnProveed.setAdapter(adapSpinnerPv);
        SpnProveed.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                ProvSelect = ProvList.get(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // =================================================== Spinner de la Familia ===========================================
        ArrayAdapter<CharSequence> adapSpinnerFm = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, FamList);
        SpnFam.setAdapter(adapSpinnerFm);
        SpnFam.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                FamSelect = FamList.get(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // =================================================== Spinner de la SubFamilia 1 ===========================================
        ArrayAdapter<CharSequence> adapSpinnerSf1 = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, Sfam1List);
        SpnSfam1.setAdapter(adapSpinnerSf1);
        SpnSfam1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                Sfam1Select = Sfam1List.get(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });




        // =================================================== Spinner de la SubFamilia 2 ===========================================
        ArrayAdapter<CharSequence> adapSpinnerSf2 = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, Sfam2List);
        SpnSfam2.setAdapter(adapSpinnerSf2);
        SpnSfam2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                Sfam2Select = Sfam2List.get(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        // =================================================== Spinner de la SubFamilia 3 ===========================================
        ArrayAdapter<CharSequence> adapSpinnerSf3 = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, Sfam3List);
        SpnSfam3.setAdapter(adapSpinnerSf3);
        SpnSfam3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                Sfam3Select = Sfam3List.get(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        // =================================================== Spinner de Orden ===========================================
        ArrayAdapter<CharSequence> adapSpinnerOrd = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, OrderList);
        SpnOrder.setAdapter(adapSpinnerOrd);
        SpnOrder.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                OrderSelect = OrderList.get(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });




        //=============================================== Butons ==================================================
        SendFilters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GenerateFilter();
            }
        });

        //=============================================== Butons ==================================================
        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent GoProdsReport = new Intent(ProdsReportFilter_Activity.this,ProdsReport_Activity.class);
                startActivity(GoProdsReport);
            }
        });







    }




















    private void getSpinnnersData() {

        SQLiteDatabase db = link.getReadableDatabase();


        Cursor cLines = db.rawQuery("SELECT Distinct(ProdCategory) from Prods where ProdCategory is not null and ProdCategory <> '' ", null);
        LinesList = new ArrayList();
        //LinesList.add("Filtra por linea...");

        if (cLines.moveToFirst()) {
            do {
                LinesList.add(cLines.getString(0));
            } while (cLines.moveToNext());
        }




        Cursor cBrands = db.rawQuery("SELECT Distinct(Brand) from Prods where Brand is not null and Brand <> '' ", null);
        BrandList = new ArrayList();
        //BrandList.add("Filtra por Marca...");

        if (cBrands.moveToFirst()) {
            do {
                BrandList.add(cBrands.getString(0));
            } while (cBrands.moveToNext());
        }





        Cursor cProveed = db.rawQuery("SELECT Distinct(MainSupplier) from AdditionalProductReportingData where MainSupplier is not null and MainSupplier <> '' ", null);
        ProvList = new ArrayList();
        //ProvList.add("Proveedor...");

        if (cProveed.moveToFirst()) {
            do {
                ProvList.add(cProveed.getString(0));
            } while (cProveed.moveToNext());
        }





        Cursor cFamily = db.rawQuery("SELECT Distinct(Family) from AdditionalProductReportingData where Family is not null and Family <> '' ", null);
        FamList = new ArrayList();
        //FamList.add("Familia...");

        if (cFamily.moveToFirst()) {
            do {
                FamList.add(cFamily.getString(0));
            } while (cFamily.moveToNext());
        }




        Cursor cSubFamily1 = db.rawQuery("SELECT Distinct(SubFam1) from AdditionalProductReportingData where SubFam1 is not null and SubFam1 <> '' ", null);
        Sfam1List = new ArrayList();
        //FamList.add("Familia...");

        if (cSubFamily1.moveToFirst()) {
            do {
                Sfam1List.add(cSubFamily1.getString(0));
            } while (cSubFamily1.moveToNext());
        }


        Cursor cSubFamily2 = db.rawQuery("SELECT Distinct(SubFam2) from AdditionalProductReportingData where SubFam2 is not null and SubFam2 <> '' ", null);
        Sfam2List = new ArrayList();
        //FamList.add("Familia...");

        if (cSubFamily2.moveToFirst()) {
            do {
                Sfam2List.add(cSubFamily2.getString(0));
            } while (cSubFamily2.moveToNext());
        }






        Cursor cSubFamily3 = db.rawQuery("SELECT Distinct(SubFam3) from AdditionalProductReportingData where SubFam3 is not null and SubFam3 <> '' ", null);
        Sfam3List = new ArrayList();
        //FamList.add("Familia...");

        if (cSubFamily3.moveToFirst()) {
            do {
                Sfam3List.add(cSubFamily3.getString(0));
            } while (cSubFamily3.moveToNext());
        }



        OrderList = new ArrayList();
        OrderList.add("Mas vendidos");
        OrderList.add("Por Existencia");




        link.close();
    }




















    private void GenerateFilter(){

        Condicion = "";
        DescripCondicion = "Filtrando resultados por ";

        if(ChkLine.isChecked()==false && ChkBrand.isChecked()==false && ChkProveed.isChecked()==false && ChkFam.isChecked()==false && ChkSfam1.isChecked()==false && ChkSfam2.isChecked()==false && ChkSfam3.isChecked()==false){
            DescripCondicion = "Mostrando todos los resultados";
        }

        if(ChkLine.isChecked()==true){
            Condicion = Condicion + " and Prods.ProdCategory = '"+ LineSelect +"' ";
            DescripCondicion = DescripCondicion + " " + LineSelect;
        }

        if(ChkBrand.isChecked()==true){
            Condicion = Condicion + " and Prods.Brand = '"+ BrandSelect +"' ";
            DescripCondicion = DescripCondicion + " - " + BrandSelect;
        }


        if(ChkProveed.isChecked()==true){
            Condicion = Condicion + " and AdditionalProductReportingData.MainSupplier = '"+ ProvSelect +"' ";
            DescripCondicion = DescripCondicion + " - " + ProvSelect;
        }


        if(ChkFam.isChecked()==true){
            Condicion = Condicion + " and AdditionalProductReportingData.Family = '"+ FamSelect +"' ";
            DescripCondicion = DescripCondicion + " - " + FamSelect;
        }

        if(ChkSfam1.isChecked()==true){
            Condicion = Condicion + " and AdditionalProductReportingData.SubFam1 = '"+ Sfam1Select +"' ";
            DescripCondicion = DescripCondicion + " - " + Sfam1Select;
        }

        if(ChkSfam2.isChecked()==true){
            Condicion = Condicion + " and AdditionalProductReportingData.SubFam2 = '"+ Sfam2Select +"' ";
            DescripCondicion = DescripCondicion + " - " + Sfam2Select;
        }

        if(ChkSfam3.isChecked()==true){
            Condicion = Condicion + " and AdditionalProductReportingData.SubFam3 = '"+ Sfam3Select +"' ";
            DescripCondicion = DescripCondicion + " - " + Sfam3Select;
        }


        String valueOrder = "";
        if(OrderSelect.equals("Mas vendidos")){
            valueOrder = "AdditionalProductReportingData.TotalSales";
        }else if(OrderSelect.equals("Por Existencia")){
            valueOrder = "Prods.Exist";
        }


        Condicion = Condicion + " order by " + valueOrder + " desc";


        Intent GoProdsReport = new Intent(ProdsReportFilter_Activity.this,ProdsReport_Activity.class);
        GoProdsReport.putExtra("Condicion",Condicion);
        GoProdsReport.putExtra("InitDate","");
        GoProdsReport.putExtra("FinalDate","");
        GoProdsReport.putExtra("DescripCondicion",DescripCondicion);
        startActivity(GoProdsReport);

    }



}
