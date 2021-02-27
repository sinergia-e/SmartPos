package intem.smartpos.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import intem.smartpos.R;
import intem.smartpos.database.connection;

public class AppConfigActivity extends AppCompatActivity {

    connection link;
    private Button bt_AppConfig;
    private EditText eT_AppConfig_Enterprise,eT_AppConfig_Terminal,eT_AppConfig_Seller,eT_AppConfig_DataSizeProds,eT_AppConfig_WareHouse,eT_AdressCompany,eT_PhoneCompany;
    private Switch switch_AppConfig_SucEx,switch_AppConfig_SendProdsServer,switch_AppConfig_PricesQty,switch_AppConfig_InstantInv,switchOnlyExist,switchPendingSale,switchPr,SwitchRem,switchPrintSale,switchPrintOrder,switchOnlyUpdatePr,switchOnlyUpdateCl,switchDeleteOldData;
    private EditText IpServer;
    private EditText MailOrders;
    private Button Drop;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_config);

        link = new connection(getApplicationContext(), "MyBusinessAndroid", null, 1);
        SQLiteDatabase dbR = link.getReadableDatabase();

        Cursor cDataConfig = dbR.rawQuery("SELECT Enterprise,Terminal,Seller,ServerIP,EmailOrders,OnlyExist,ImportSucEx,SendProds,DataSize,PriceQty,InvFisInstant,PendingSale,Warehouse,PrEnabled,Remision,Adress,Phone,PrintTkSale,PrintTkOrder,OnlyUpdateProds,OnlyUpdateClients,DeleteOldData from AppConfig ",null);

        eT_AppConfig_Enterprise = (EditText) findViewById(R.id.eT_AppConfig_Enterprise);
        eT_AppConfig_Terminal = (EditText) findViewById(R.id.eT_AppConfig_Terminal);
        eT_AppConfig_Seller = (EditText) findViewById(R.id.eT_AppConfig_Seller);
        eT_AppConfig_DataSizeProds = (EditText) findViewById(R.id.eT_AppConfig_DataSizeProds);
        eT_AppConfig_WareHouse = (EditText) findViewById(R.id.eT_AppConfig_WareHouse);
        switchOnlyExist = (Switch) findViewById(R.id.switchOnlyExist);
        switch_AppConfig_SucEx = (Switch) findViewById(R.id.switch_AppConfig_SucEx);
        switch_AppConfig_SendProdsServer = (Switch) findViewById(R.id.switch_AppConfig_SendProdsServer);
        switch_AppConfig_PricesQty = (Switch) findViewById(R.id.switchPriceQty);
        switch_AppConfig_InstantInv = (Switch) findViewById(R.id.Switch_AppConfig_InvInstant);
        switchPendingSale = (Switch) findViewById(R.id.switchPendingSale);
        SwitchRem = (Switch) findViewById(R.id.switch_SendSaleRemision);
        switchPr = (Switch) findViewById(R.id.switchPresentations);
        switchPrintSale = (Switch) findViewById(R.id.switchPrintTkSale);
        switchPrintOrder = (Switch) findViewById(R.id.switchPrintTkOrder);
        switchDeleteOldData = findViewById(R.id.Switch_AppConfig_DeleteOldData);
        switchOnlyUpdateCl = findViewById(R.id.switchOnlyUpdateCl);
        switchOnlyUpdatePr = findViewById(R.id.switchOnlyUpdatePr);

        IpServer = (EditText) findViewById(R.id.eT_AppConfig_ServerIp);
        MailOrders = (EditText) findViewById(R.id.eT_AppConfig_MailOrders);
        eT_AdressCompany = (EditText) findViewById(R.id.eT_AppConfig_CompanyAdress);
        eT_PhoneCompany = (EditText) findViewById(R.id.eT_AppConfig_PhoneCompany);
        Drop = (Button) findViewById(R.id.DropDB);

        if(cDataConfig.moveToFirst()){
            //Enterprise,Terminal,Vend,ServerIP,EmailOrders,OnlyExist,ImportSucEx,SendProds,DataSize
            eT_AppConfig_Enterprise.setText(cDataConfig.getString(0));
            eT_AppConfig_Terminal.setText(cDataConfig.getString(1));
            eT_AppConfig_Seller.setText(cDataConfig.getString(2));
            IpServer.setText(cDataConfig.getString(3));
            MailOrders.setText(cDataConfig.getString(4));
            String SizePaqProds = String.valueOf(cDataConfig.getInt(8));
            eT_AppConfig_DataSizeProds.setText(SizePaqProds);
            Integer SelectWarehouse = cDataConfig.getInt(12);
            eT_AppConfig_WareHouse.setText(String.valueOf(SelectWarehouse));
            String AdressCorp = cDataConfig.getString(15);
            eT_AdressCompany.setText(AdressCorp);
            String PhoneCorp = cDataConfig.getString(16);
            eT_PhoneCompany.setText(PhoneCorp);



            Integer ValuePrintOrder = cDataConfig.getInt(18);

            // ***************  Print TK In Sale ****************************
            if(cDataConfig.getInt(17)==1){
                switchPrintSale.setChecked(true);
            }else{
                switchPrintSale.setChecked(false);
            }



            // ***************  Print Tk In orders ****************************
            if(cDataConfig.getInt(18)==1){
                switchPrintOrder.setChecked(true);
            }else{
                switchPrintOrder.setChecked(false);
            }


            // ***************  Only exist  ****************************
            if(cDataConfig.getInt(5)==1){
                switchOnlyExist.setChecked(true);
            }else{
                switchOnlyExist.setChecked(false);
            }
            //****************  Recovery Exist Suc  *************************
            if(cDataConfig.getInt(6)==1){
                switch_AppConfig_SucEx.setChecked(true);
            }else{
                switch_AppConfig_SucEx.setChecked(false);
            }

            //****************  Send Prods to Server  **************************
            if(cDataConfig.getInt(7)==1){
                switch_AppConfig_SendProdsServer.setChecked(true);
            }else{
                switch_AppConfig_SendProdsServer.setChecked(false);
            }

            //****************  Prices for Qty **************************
            if(cDataConfig.getInt(9)==1){
                switch_AppConfig_PricesQty.setChecked(true);
            }else{
                switch_AppConfig_PricesQty.setChecked(false);
            }


            //****************  Enabled Presentations **************************
            if(cDataConfig.getInt(13)==1){
                switchPr.setChecked(true);
            }else{
                switchPr.setChecked(false);
            }





            //****************  Inventary Instant **************************
            if(cDataConfig.getInt(10)==1){
                switch_AppConfig_InstantInv.setChecked(true);
            }else{
                switch_AppConfig_InstantInv.setChecked(false);
            }



            //****************  PendingSale **************************
            if(cDataConfig.getInt(11)==1){
                switchPendingSale.setChecked(true);
            }else{
                switchPendingSale.setChecked(false);
            }


            //****************  Send Remision **************************
            if(cDataConfig.getInt(14)==1){
                SwitchRem.setChecked(true);
            }else{
                SwitchRem.setChecked(false);
            }

            //****************  Only sync new prods**************************
            if(cDataConfig.getInt(19)==1){
                switchOnlyUpdatePr.setChecked(true);
            }else{
                switchOnlyUpdatePr.setChecked(false);
            }

            //****************  Only sync new Clients **************************
            if(cDataConfig.getInt(20)==1){
                switchOnlyUpdateCl.setChecked(true);
            }else{
                switchOnlyUpdateCl.setChecked(false);
            }

            //****************  Delete old data **************************
            if(cDataConfig.getInt(21)==1){
                switchDeleteOldData.setChecked(true);
            }else{
                switchDeleteOldData.setChecked(false);
            }


        }






        bt_AppConfig = (Button) findViewById(R.id.bt_AppConfig_Save);
        bt_AppConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SaveConfig();
            }
        });



        Drop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfirmDropBase();
            }
        });

    }











    private void ConfirmDropBase(){
        AlertDialog.Builder ConfirmDropDialog = new AlertDialog.Builder(this);
        ConfirmDropDialog.setTitle("¡¡¡ ALERTA !!! ");
        ConfirmDropDialog.setMessage("Atencion, esto va a eliminar la base de datos, todos los datos se perderan de forma irrecuperable, ¿estas seguro?");
        ConfirmDropDialog.setCancelable(false);

        ConfirmDropDialog.setPositiveButton("CONFIRMAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DropTables();
            }
        });

        ConfirmDropDialog.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        ConfirmDropDialog.show();
    }
















    private void SaveConfig(){

        SQLiteDatabase db = link.getWritableDatabase();

        //Enterprise,Terminal,Seller,ServerIP,EmailOrders,OnlyExist,DataSize
        String Enterprise = eT_AppConfig_Enterprise.getText().toString();
        String Terminal = eT_AppConfig_Terminal.getText().toString();
        String Seller = eT_AppConfig_Seller.getText().toString();
        String ServerAdress = IpServer.getText().toString();
        String eMailOrders = MailOrders.getText().toString();
        String DataSize = eT_AppConfig_DataSizeProds.getText().toString();
        String WareHouse = eT_AppConfig_WareHouse.getText().toString();
        String AdressComp = eT_AdressCompany.getText().toString();
        String PhoneComp = eT_PhoneCompany.getText().toString();

        //*****************   Only Exist  *********************************
        String OnlyExist = "";
        if(switchOnlyExist.isChecked()==true){
            OnlyExist = "1";
        }else{
            OnlyExist = "0";
        }
        //*****************   Only Exist  *********************************
        String SucExist = "";
        if(switch_AppConfig_SucEx.isChecked()==true){
            SucExist = "1";
        }else{
            SucExist = "0";
        }
        //*****************   SendProds  *********************************
        String SendProds = "";
        if(switch_AppConfig_SendProdsServer.isChecked()==true){
            SendProds = "1";
        }else{
            SendProds = "0";
        }

        //*****************   Precios por cantidad  *********************************
        String PriceQty = "";
        if(switch_AppConfig_PricesQty.isChecked()==true){
            PriceQty = "1";
        }else{
            PriceQty = "0";
        }



        //*****************   Precios por cantidad  *********************************
        String PrEnabled = "";
        if(switchPr.isChecked()==true){
            PrEnabled = "1";
        }else{
            PrEnabled = "0";
        }


        //*****************   Instant Inventory  *********************************
        String InvInstant = "";
        if(switch_AppConfig_InstantInv.isChecked()==true){
            InvInstant = "1";
        }else{
            InvInstant = "0";
        }

        //*****************   Instant Inventory  *********************************
        String PendingSale = "";
        if(switchPendingSale.isChecked()==true){
            PendingSale = "1";
        }else{
            PendingSale = "0";
        }


        //*****************   Instant Inventory  *********************************
        String SaleRem = "";
        if(SwitchRem.isChecked()==true){
            SaleRem = "1";
        }else{
            SaleRem = "0";
        }



        //*****************   Print Tk Sale *********************************
        Integer PrintSale = 0;
        if(switchPrintSale.isChecked()==true){
            PrintSale = 1;
        }

        //*****************   Print Tk Order *********************************
        Integer PrintOrder = 0;
        if(switchPrintOrder.isChecked()==true){
            PrintOrder = 1;
        }


        //****************  Only sync new prods**************************
        Integer OnlyUpdateProd = 0;
        if(switchOnlyUpdatePr.isChecked()==true){
            OnlyUpdateProd = 1;
        }

        //****************  Only sync new Clients **************************
        Integer OnlyUpdateClient = 0;
        if(switchOnlyUpdateCl.isChecked()==true){
            OnlyUpdateClient = 1;
        }

        //****************  Delete old data **************************
        Integer DeleteOldData = 0;
        if(switchDeleteOldData.isChecked()==true){
            DeleteOldData = 1;
        }

        db.execSQL("UPDATE AppConfig set Enterprise = '"+Enterprise+"',Adress='"+AdressComp+"',Phone='"+PhoneComp+"',Terminal = '"+Terminal+"',Seller='"+Seller+"',ImportSucEx = '"+SucExist+"',SendProds = '"+SendProds+"',DataSize = '"+DataSize+"',ServerIP = '"+ServerAdress+"',EmailOrders = '"+eMailOrders+"',OnlyExist='"+OnlyExist+"',PriceQty = '"+ PriceQty +"',InvFisInstant='"+ InvInstant +"',PendingSale = '"+PendingSale+"',Warehouse="+WareHouse+",PrEnabled='"+PrEnabled+"',Remision='"+SaleRem+"',PrintTkSale= "+PrintSale+",PrintTkOrder="+PrintOrder+",OnlyUpdateProds="+OnlyUpdateProd+",OnlyUpdateClients="+OnlyUpdateClient+",DeleteOldData="+DeleteOldData+" " );
        Toast.makeText(this, "Informacion Actualizada", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this,MenuActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        Intent goMenu = new Intent(this,MenuActivity.class);
        startActivity(goMenu);
    }

















    public void DropTables(){


        link = new connection(getApplicationContext(), "MyBusinessAndroid", null, 1);
        SQLiteDatabase db = link.getWritableDatabase();
        getApplicationContext().deleteDatabase("MyBusinessAndroid");

        Toast.makeText(this, "La base se elimino,reinicie la app", Toast.LENGTH_SHORT).show();
        finishAffinity();
    }







}
