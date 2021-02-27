package intem.smartpos.activities;

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

import java.util.ArrayList;

import intem.smartpos.R;
import intem.smartpos.constructors.usersConstructor;
import intem.smartpos.database.connection;

public class EditUserActivity extends AppCompatActivity {

    connection link;
    private ArrayList<usersConstructor> usersArray;
    usersConstructor userItem;
    private EditText NickText;
    private EditText NameText;
    private EditText PassText;
    private Button UpdateUser;
    private Button DeleteUser;

    private Switch switch_EditUser_PosAccess;
    private Switch switch_EditUser_OrdersAcc;
    private Switch switch_EditUser_ProdsAcc;
    private Switch switch_EditUser_Clients;
    private Switch switch_EditUser_MovAccess;
    private Switch switch_Leave_Discount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);


        link = new connection(getApplicationContext(), "MyBusinessAndroid", null, 1);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){

        }

        final int idUser = bundle.getInt("id");

        SQLiteDatabase db = link.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Users where id = " + idUser + " ",null);
        usersArray = new ArrayList<usersConstructor>();


        //id,Nick,Name,Password ,Level ,OrdersAccess ,MovsAccess ,ProdsAccess ,Clients ,POS ,Loged ,Discount ,PricesList,SelectPriceWindow

        Integer EnabledDiscount = 0;
        if (cursor.moveToFirst()) {
            EnabledDiscount = cursor.getInt(11);
            do {
                userItem = new usersConstructor(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4),cursor.getInt(5),cursor.getInt(6),cursor.getInt(7),cursor.getInt(8),cursor.getInt(9),cursor.getInt(10));
                usersArray.add(userItem);
            } while (cursor.moveToNext());
        }

        NickText = (EditText) findViewById(R.id.EditNick);
        NameText = (EditText) findViewById(R.id.EditName);
        PassText = (EditText) findViewById(R.id.EditPassword);
        switch_EditUser_PosAccess = (Switch) findViewById(R.id.switch_EditUser_PosAccess);
        switch_EditUser_OrdersAcc = (Switch) findViewById(R.id.switch_EditUser_OrdersAcc);
        switch_EditUser_ProdsAcc = (Switch) findViewById(R.id.switch_EditUser_ProdsAcc);
        switch_EditUser_Clients = (Switch) findViewById(R.id.switch_EditUser_Clients);
        switch_EditUser_MovAccess = (Switch) findViewById(R.id.switch_EditUser_MovAccess);
        switch_Leave_Discount = (Switch) findViewById(R.id.switch_EditUser_Discount);

        NickText.setText(usersArray.get(0).getNick());
        NameText.setText(usersArray.get(0).getName());
        PassText.setText(usersArray.get(0).getPass());


        //Enabled Discount ------------------------
        if(EnabledDiscount==1){
            switch_Leave_Discount.setChecked(true);
        }else{
            switch_Leave_Discount.setChecked(false);
        }



        //POS Access garanted ****************************
        if(usersArray.get(0).getPOSAccess()==1){
            switch_EditUser_PosAccess.setChecked(true);
        }else{
            switch_EditUser_PosAccess.setChecked(false);
        }

        //Orders Access garanted ****************************
        if(usersArray.get(0).getOrdersAccess()==1){
            switch_EditUser_OrdersAcc.setChecked(true);
        }else{
            switch_EditUser_OrdersAcc.setChecked(false);
        }

        //Clients Access garanted ****************************
        if(usersArray.get(0).getClientsAccess()==1){
            switch_EditUser_Clients.setChecked(true);
        }else{
            switch_EditUser_Clients.setChecked(false);
        }

        //prods Access garanted ****************************
        if(usersArray.get(0).getProdsAccess()==1){
            switch_EditUser_ProdsAcc.setChecked(true);
        }else{
            switch_EditUser_ProdsAcc.setChecked(false);
        }

        //movs Access garanted ****************************
        if(usersArray.get(0).getMovsAccess()==1){
            switch_EditUser_MovAccess.setChecked(true);
        }else{
            switch_EditUser_MovAccess.setChecked(false);
        }



        DeleteUser = (Button) findViewById(R.id.buttonDeleteUsers);
        DeleteUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                QueryDelete(idUser);
            }
        });

        UpdateUser = (Button) findViewById(R.id.buttonEditUser);
        UpdateUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                QueryUpdate(idUser);
            }
        });

    }















    private void QueryDelete(int idUser){

        SQLiteDatabase db = link.getWritableDatabase();
        db.execSQL("DELETE FROM Users where id = " + idUser);

        Toast.makeText(EditUserActivity.this, "El registro se elimino correctamente", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(EditUserActivity.this,UsersActivity.class);
        startActivity(intent);
        finish();
        link.close();
    }












    private void QueryUpdate(int idUser){

        SQLiteDatabase db = link.getWritableDatabase();

        String NickNew = NickText.getText().toString();
        String NameNew = NameText.getText().toString();
        String PassNew = PassText.getText().toString();

        //Enabled Discount =====================================================
        Integer LeaveDiscount = 0;
        if(switch_Leave_Discount.isChecked()==true){
            LeaveDiscount = 1;
        }



        //POS Access garanted ****************************
        Integer PosAcc = null;
        if(switch_EditUser_PosAccess.isChecked()==true){
            PosAcc = 1;
        }else{
            PosAcc = 0;
        }

        //Orders Access garanted ****************************
        Integer OrdersAcc = null;
        if(switch_EditUser_OrdersAcc.isChecked()==true){
            OrdersAcc = 1;
        }else{
            OrdersAcc = 0;
        }

        //Clients Access garanted ****************************
        Integer ClientsAcc = null;
        if(switch_EditUser_Clients.isChecked()==true){
            ClientsAcc = 1;
        }else{
            ClientsAcc = 0;
        }

        //prods Access garanted ****************************
        Integer ProdsAcc = null;
        if(switch_EditUser_ProdsAcc.isChecked()==true){
            ProdsAcc = 1;
        }else{
            ProdsAcc = 0;
        }

        //movs Access garanted ****************************
        Integer MovsAcc = null;
        if(switch_EditUser_MovAccess.isChecked()==true){
            MovsAcc = 1;
        }else{
            MovsAcc = 0;
        }

        db.execSQL("UPDATE Users set Nick = '"+ NickNew +"',Name = '"+ NameNew +"',Password = '"+PassNew+"'," +
                "OrdersAccess="+OrdersAcc+",MovsAccess="+MovsAcc+",ProdsAccess="+ProdsAcc+",Clients="+ClientsAcc+",POS="+PosAcc+",Discount='"+LeaveDiscount+"' where id = " + idUser);
        Intent intent = new Intent(EditUserActivity.this,UsersActivity.class);
        startActivity(intent);
        Toast.makeText(EditUserActivity.this, "Actualizamos el registro correctamente ", Toast.LENGTH_LONG).show();
        finish();
        link.close();
    }
}
