package intem.smartpos.activities;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AlertDialog.Builder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;

import intem.smartpos.R;
import intem.smartpos.adapters.usersAdapter;
import intem.smartpos.constructors.usersConstructor;
import intem.smartpos.database.connection;

import static intem.smartpos.R.layout.view_new_user;


public class UsersActivity extends AppCompatActivity {

    private FloatingActionButton FabNewUser;
    private EditText NewUser;
    private ListView UsersListView;
    private ArrayList<usersConstructor> usersArray;
    usersConstructor userItem;
    connection link;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        link = new connection(getApplicationContext(), "MyBusinessAndroid", null, 1);

        UsersListView = (ListView) findViewById(R.id.listViewUsersList);
        NewUser = (EditText) findViewById(R.id.ET_NU_Nick);
        FabNewUser = (FloatingActionButton) findViewById(R.id.FloatingActionButtonUsers);

        recoveryUsers();

        FabNewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlert("Usuarios", "Crear nuevo usuario");
            }
        });


    }

    @Override
    public void onBackPressed() {
        Intent goMenu = new Intent(getApplicationContext(),MenuActivity.class);
        startActivity(goMenu);
    }







    /***   Recupera los usuarios y los pone en el list View ***/
    /***   ====================================================================== ***/
    /***   ======================================================================  ***/
    private void recoveryUsers() {

        SQLiteDatabase db = link.getReadableDatabase();
        Cursor cursor = db.rawQuery("Select * from Users ", null);
        usersArray = new ArrayList<usersConstructor>();

        if (cursor.moveToFirst()) {
            do {
                userItem = new usersConstructor(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4),cursor.getInt(5),cursor.getInt(6),cursor.getInt(7),cursor.getInt(8),cursor.getInt(9),cursor.getInt(10));
                usersArray.add(userItem);
            } while (cursor.moveToNext());
        }

        usersAdapter adapter = new usersAdapter(getApplicationContext(), usersArray);
        UsersListView.setAdapter(adapter);

        UsersListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(UsersActivity.this,EditUserActivity.class);
                intent.putExtra("id",usersArray.get(i).getId());
                startActivity(intent);
                return false;
            }
        });

    }



/************************************************************************************/
/************************ Muestra menu dialogo  *************************************/
/************************************************************************************/

    private void showAlert(String title, String messaqe) {

        Builder builder = new Builder(this);
        if (title != null) builder.setTitle(title);
        if (messaqe != null) builder.setMessage(messaqe);

        View newUserInflated = LayoutInflater.from(this).inflate(view_new_user, null);
        builder.setView(newUserInflated);

        final EditText NwUser = (EditText) newUserInflated.findViewById(R.id.ET_NU_Nick);
        final EditText NwPass = (EditText) newUserInflated.findViewById(R.id.ET_NU_Pass);
        final EditText NwName = (EditText) newUserInflated.findViewById(R.id.ET_NU_Name);
        final Switch NwLvl = (Switch) newUserInflated.findViewById(R.id.switchSup);
        final Switch OrdersAcc = newUserInflated.findViewById(R.id.switch_Users_OrdersAccess);
        final Switch POSAccess = newUserInflated.findViewById(R.id.switch_NewUser_POS);
        final Switch ClientsAcc = newUserInflated.findViewById(R.id.switch_Users_ClientsAccess);
        final Switch ProdsAccess = newUserInflated.findViewById(R.id.switch_Users_ProdsAccess);
        final Switch MovsInvAcc = newUserInflated.findViewById(R.id.switch_Users_MovsInvAccess);
        final Switch LeaveDiscount = newUserInflated.findViewById(R.id.switch_new_user_Discount);

        builder.setPositiveButton("Nuevo Usuario", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //id ,Nick,Name,Password,Level ,OrdersAccess ,MovsAccess ,ProdsAccess ,Clients ,POS ,Loged
                String UserNick = NwUser.getText().toString().trim();
                String UserPass = NwPass.getText().toString().trim();
                String UserName = NwName.getText().toString().trim();

                //*************  Supervisor ***********************
                boolean sup = NwLvl.isChecked();
                String Lvl = null;
                if(sup==true){
                    Lvl = "Administrador";
                }else{
                    Lvl = "Usuario";
                }
                //************* OrdersAccess ***********************
                boolean Orders = OrdersAcc.isChecked();
                Integer OrdersAcc = null;
                if(Orders==true){
                    OrdersAcc = 1;
                }else{
                    OrdersAcc = 0;
                }

                //************* PosAccess ***********************
                boolean Pos = POSAccess.isChecked();
                Integer PosAcc = null;
                if(Pos==true){
                    PosAcc = 1;
                }else{
                    PosAcc = 0;
                }

                //Leave Discount =======================================
                boolean Discount = LeaveDiscount.isChecked();
                Integer LeaveDiscount = 0;
                if(Discount==true){
                    LeaveDiscount = 1;
                }else{
                    LeaveDiscount = 0;
                }


                //************* ClientsAccess ***********************
                boolean Clients = ClientsAcc.isChecked();
                Integer ClAccess = null;
                if(Clients==true){
                    ClAccess = 1;
                }else{
                    ClAccess = 0;
                }

                //************* ClientsAccess ***********************
                boolean Prods = ProdsAccess.isChecked();
                Integer ProdsAcc = null;
                if(Prods==true){
                    ProdsAcc = 1;
                }else{
                    ProdsAcc = 0;
                }

                //************* ClientsAccess ***********************
                boolean Movs = MovsInvAcc.isChecked();
                Integer MovsAcc = null;
                if(Movs==true){
                    MovsAcc = 1;
                }else{
                    MovsAcc = 0;
                }


                if (UserName.length() > 0 || UserNick.length() > 0) {
                    InsertUser(UserNick, UserName, UserPass ,Lvl,OrdersAcc,PosAcc,ClAccess,ProdsAcc,MovsAcc,LeaveDiscount);
                } else {
                    Toast.makeText(UsersActivity.this, "El nombre y el nick no pueden estar vacios!", Toast.LENGTH_LONG).show();
                }

            }
        });

        AlertDialog newUserDialog = builder.create();
        newUserDialog.show();
    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_users, menu);
        return super.onCreateOptionsMenu(menu);
    }











/************************************************************************************/
/************************ Metodo para insert usuario ********************************/
/************************************************************************************/
    private void InsertUser(String NewNick, String NewName, String NewPass, String userLvl,Integer OrdersAcc,Integer PosAcc,Integer ClAccess,Integer ProdsAcc,Integer MovsAcc,Integer LeaveDiscount) {

        link = new connection(this, "MyBusinessAndroid", null, 1);
        SQLiteDatabase db = link.getWritableDatabase();

        //id ,Nick,Name,Password,Level ,OrdersAccess ,MovsAccess ,ProdsAccess ,Clients ,POS ,Loged
        ContentValues values = new ContentValues();
        values.put("Nick", NewNick);
        values.put("Name", NewName);
        values.put("Password", NewPass);
        values.put("Level", userLvl);
        values.put("OrdersAccess",OrdersAcc);
        values.put("MovsAccess",MovsAcc);
        values.put("Clients",ClAccess);
        values.put("ProdsAccess",ProdsAcc);
        values.put("POS",PosAcc);
        values.put("Discount",LeaveDiscount);

        Long idResult = db.insert("Users", "id", values);

        recoveryUsers();
        link.close();
    }
}
