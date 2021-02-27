package intem.smartpos.activities;

import intem.smartpos.R;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;
import static intem.smartpos.R.layout.view_about_us;

import com.android.volley.RequestQueue;
import intem.smartpos.database.connection;


public class MenuActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private connection link;
    private Integer ProdsAcc,OrdersAcc,NewOrderAcc,MovsAcc,ClientsAcc,PosAccess,PurchasesAccess;
    public RequestQueue request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_menu);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        link = new connection(getApplicationContext(), "MyBusinessAndroid", null, 1);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"soporte@intem.com.mx"});
                emailIntent.putExtra(Intent.EXTRA_CC, new String[]{"soluciones@intem.com.mx"});
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Solicitud de soporte");
                String textoApp = "Tenemos un problema con la app de MyBusiness Android";
                emailIntent.putExtra(Intent.EXTRA_TEXT, textoApp);
                emailIntent.setType("message/rfc822");
                startActivity(Intent.createChooser(emailIntent, "Selecciona aplicación..."));

//                Snackbar.make(view, "surtidorarj@gmail.com", Snackbar.LENGTH_SHORT)
//                .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);




    }

    @Override
    public void onBackPressed(){

    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        SQLiteDatabase dbR = link.getReadableDatabase();
        Cursor UserLevel = dbR.rawQuery("SELECT * From Users where Loged = 1 and Level = 'Administrador' ",null);
        String Level= "";

        if(UserLevel.moveToFirst()){
            Level = "Administrador";
        }else{
            Level = "Usuario";
        }

        Cursor permissions = dbR.rawQuery("SELECT OrdersAccess,MovsAccess,ProdsAccess,Clients,POS,Loged,PurchasesAccess From Users WHERE Loged = 1",null);
        if (permissions.moveToFirst()){
            PurchasesAccess = permissions.getInt(6);
            PosAccess = permissions.getInt(4);
            ClientsAcc = permissions.getInt(3);
            NewOrderAcc = permissions.getInt(0);
            OrdersAcc = permissions.getInt(0);
            ProdsAcc = permissions.getInt(2);
            MovsAcc = permissions.getInt(1);
        }

        int id = item.getItemId();

        if (id == R.id.pos) {
            if(PosAccess==1){
                Intent goPos = new Intent(getApplicationContext(),PosActivity.class);
                startActivity(goPos);
            }else{
                Toast.makeText(this, "Solicita autorizacion para esta opcion", Toast.LENGTH_SHORT).show();
            }

        }else if (id == R.id.Pos_Reg) {
            if(PosAccess==1){
                Intent goSalesList = new Intent(getApplicationContext(),SalesListActivity.class);
                startActivity(goSalesList);
            }else{
                Toast.makeText(this, "Solicita autorizacion para esta opcion", Toast.LENGTH_SHORT).show();
            }


        }else if (id == R.id.Checker) {
//            if(PosAccess==1){
                Intent Checker = new Intent(getApplicationContext(),CheckerActivity.class);
                startActivity(Checker);
//            }else{
//                Toast.makeText(this, "Solicita autorizacion para esta opcion", Toast.LENGTH_SHORT).show();
//            }

      }else if (id == R.id.purchase) {
            if(PurchasesAccess==1){
                //Intent goPurchases = new Intent(getApplicationContext(),PurchasesActivity.class);
                // startActivity(goPurchases);
            }else{
                Toast.makeText(this, "Solicita autorizacion para esta opcion", Toast.LENGTH_SHORT).show();
            }


        }else if (id == R.id.providers) {
            if(PurchasesAccess==1){
                Intent goProviders = new Intent(getApplicationContext(),ProvidersActivity.class);
                startActivity(goProviders);
            }else{
                Toast.makeText(this, "Solicita autorizacion para esta opcion", Toast.LENGTH_SHORT).show();
            }


        }else if (id == R.id.purchaseslist) {
            if(PurchasesAccess==1){
                Intent goPurchasesList = new Intent(getApplicationContext(),PurchasesListActivity.class);
                startActivity(goPurchasesList);
            }else{
                Toast.makeText(this, "Solicita autorizacion para esta opcion", Toast.LENGTH_SHORT).show();
            }


        }



        /*
        else if (id == R.id.comander) {
            //Toast.makeText(this, "Solicita la version para restaurante", Toast.LENGTH_SHORT).show();
            Intent goCommand = new Intent(getApplicationContext(),for_test.class);
            startActivity(goCommand);
        }else if (id == R.id.tables) {
            Toast.makeText(this, "Solicita la version para restaurante", Toast.LENGTH_SHORT).show();
            //Intent goPos = new Intent(getApplicationContext(),PosActivity.class);
            //startActivity(goPos);
        }*/

        else if (id == R.id.clients) {
             if(ClientsAcc==1){
                 Intent goClients = new Intent(getApplicationContext(),ClientsActivity.class);
                 startActivity(goClients);
             }else{
                 Toast.makeText(this, "Tu usuario no puede usar esta opcion", Toast.LENGTH_SHORT).show();
             }
        }else if (id == R.id.neworder) {
             if(NewOrderAcc==1){
                 NewOrderConfirm();
             }else{
                 Toast.makeText(this, "Tu usuario no puede usar esta opcion", Toast.LENGTH_SHORT).show();
             }
        } else if (id == R.id.orders) {
             if(OrdersAcc==1){
                 Intent goOrders = new Intent(getApplicationContext(),OrdersListActivity.class);
                 startActivity(goOrders);
             }else{
                 Toast.makeText(this, "Tu usuario no puede usar esta opcion", Toast.LENGTH_SHORT).show();
             }
        } else if (id == R.id.Reports) {
            Intent GoProdsReports = new Intent(getApplicationContext(),Reports_Activity.class);
            startActivity(GoProdsReports);
        } else if (id == R.id.prods) {
             if(ProdsAcc==1){
                 Intent goProdsList = new Intent(getApplicationContext(),ListProdsActivity.class);
                 startActivity(goProdsList);
             }else{
                 Toast.makeText(this, "Tu usuario no puede usar esta opcion", Toast.LENGTH_SHORT).show();
             }

        }else if (id == R.id.Inventory) {
             if(MovsAcc==1){
                 Intent goMovsList = new Intent(getApplicationContext(),MovsInvListActivity.class);
                 startActivity(goMovsList);
             }else{
                 Toast.makeText(this, "Tu usuario no puede usar esta funcion", Toast.LENGTH_SHORT).show();
             }

        }else if (id == R.id.Users) {
            if(Level == "Administrador"){
                Intent goUsers = new Intent(getApplicationContext(),UsersActivity.class);
                startActivity(goUsers);
            }else{
                Toast.makeText(this, "Esta opcion requiere un usuario supervisor", Toast.LENGTH_SHORT).show();
            }
        }else if (id == R.id.AppConfig) {
            if(Level == "Administrador"){
                Intent goUsers = new Intent(getApplicationContext(),AppConfigActivity.class);
                startActivity(goUsers);
            }else{
                Toast.makeText(this, "Esta opcion requiere un usuario supervisor", Toast.LENGTH_SHORT).show();
            }
        }else if (id == R.id.Connections) {
            if(Level == "Administrador"){
                Intent goConnections = new Intent(getApplicationContext(),ConnectionsActivity.class);
                startActivity(goConnections);
            }else{
                Toast.makeText(this, "Esta opcion requiere un usuario supervisor", Toast.LENGTH_SHORT).show();
            }
        }else if (id == R.id.DeleteDb) {
            if(Level == "Administrador"){
                DeleteDB();
            }else{
                Toast.makeText(this, "Esta opcion en muy delicada, solicitalo a un supervisor", Toast.LENGTH_LONG).show();
            }
        }else if (id == R.id.AboutUs) {
            AboutUS();
        }else if (id == R.id.CloseApp) {
             finishAffinity();
         }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }










    private void NewOrderConfirm(){
        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
        dialogo1.setTitle("Importante");
        dialogo1.setMessage("¿Vas a levantar un nuevo pedido?");
        dialogo1.setCancelable(false);
        dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {

            }
        });
        dialogo1.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
           @RequiresApi(api = Build.VERSION_CODES.N) //requerido para el formato numerico
            public void onClick(DialogInterface dialogo1, int id) {
                SQLiteDatabase db = link.getWritableDatabase();

                String date = (DateFormat.format("dd-MM-yyyy hh:mm:ss", new java.util.Date()).toString());
                ContentValues values = new ContentValues();
                values.put("Client", "");
                values.put("Date", date);
                values.put("Amount", 0);
                values.put("Location", "");
                values.put("Status", "Pendiente");
                values.put("Export", 0);

                Long idResult = db.insert("Orders", "id", values);

               Intent intent = new Intent(MenuActivity.this,NewOrderActivity.class);
               intent.putExtra("order","" + idResult);
               startActivity(intent);
            }
        });
        dialogo1.show();
    }








    private void DeleteDB(){
        android.app.AlertDialog.Builder ConfirmDropDialog = new android.app.AlertDialog.Builder(this);
        ConfirmDropDialog.setTitle(" ¡ ¡ ¡  ALERTA ! ! ! ");
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




    public void DropTables(){

        link = new connection(getApplicationContext(), "MyBusinessAndroid", null, 1);
        getApplicationContext().deleteDatabase("MyBusinessAndroid");
        Toast.makeText(this, "La base se elimino,reinicie la app", Toast.LENGTH_SHORT).show();
        finishAffinity();
    }














    private void AboutUS() {

        AlertDialog.Builder AboutUs = new AlertDialog.Builder(this);
        View AboutView = LayoutInflater.from(getApplicationContext()).inflate(view_about_us, null);
        AboutUs.setView(AboutView);

        AlertDialog ViewAbout = AboutUs.create();
        ViewAbout.show();

    }




}
