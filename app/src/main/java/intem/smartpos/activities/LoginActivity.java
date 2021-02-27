package intem.smartpos.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

import intem.smartpos.R;
import intem.smartpos.database.connection;

public class LoginActivity extends AppCompatActivity {

    public EditText editTextUser;
    public EditText editTextPass;
    public Button buttonLogIn,buttonDefaultUser;
    private ArrayList UsersList;
    connection link;


    private void bindUI()
    {
        editTextUser = (EditText) findViewById(R.id.editTextUser);
        editTextPass = (EditText) findViewById(R.id.editTextPass);
        buttonLogIn = (Button) findViewById(R.id.buttonLogIn);
        buttonDefaultUser = (Button) findViewById(R.id.button_default_user);
    }




    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        bindUI();
        link = new connection(getApplicationContext(), "MyBusinessAndroid", null, 1);

        SQLiteDatabase db = link.getWritableDatabase();

        Cursor SetUpIn = db.rawQuery("select InitialSetup from AppConfig ",null);


        if(SetUpIn.moveToFirst()){
            do{
                Integer StartUp = SetUpIn.getInt(0);
                //Toast.makeText(this, "Inicial:" + StartUp, Toast.LENGTH_SHORT).show();
                if(StartUp == 1){
                    Intent welcome= new Intent(this,welcome.class);
                    startActivity(welcome);
                }
            }while (SetUpIn.moveToNext());

        }else{
            DropTables();
        }




        Cursor UserSup = db.rawQuery("SELECT * from Users where Nick = 'SUP' ",null);

        if(UserSup.moveToFirst()){

        }else{
            db.execSQL("insert into Users (Nick,Name,Password,Level,OrdersAccess,MovsAccess,ProdsAccess,Clients,POS,Loged) values ('SUP','Usuario Supervisor','SUP','Administrador',1,1,1,1,1,0) ");
        }


        buttonLogIn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
            String User = editTextUser.getText().toString();
            String Pass = editTextPass.getText().toString();

            if(isLoginValid(User,Pass))
            {
                goMenu();
            }

            }
        });

        buttonDefaultUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isLoginValid("SUP","SUP"))
                {
                    goMenu();
                }

            }
        });

    }



    private boolean isLoginValid(String User,String Pass) {

        SQLiteDatabase db = link.getReadableDatabase();
        SQLiteDatabase dbW = link.getWritableDatabase();
        Cursor UsersCursor = db.rawQuery("SELECT * from Users where Nick = '"+User+"' ",null);
        Cursor PassCursor = db.rawQuery("SELECT * from Users Where Nick ='"+ User +"' and Password = '"+ Pass +"' ",null);
        db.execSQL("UPDATE Users Set Loged = 0 ");

        if(UsersCursor.moveToFirst()){
            if(PassCursor.moveToFirst()){
                dbW.execSQL("UPDATE Users Set Loged = 1 where Nick ='"+ User +"' and Password = '"+ Pass +"'  ");
                return true;
            }else{
                Toast.makeText(this, "El Password no coincide", Toast.LENGTH_SHORT).show();
            }

            link.close();
            return false;
        }else{
            Toast.makeText(this, "No existe el usuario,recuerda que el sistema reconoce mayusculas y minusculas", Toast.LENGTH_SHORT).show();
            return false;
        }


    }





    private void goMenu()
    {
        Intent goMenu = new Intent(this,MenuActivity.class);
        goMenu.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK); /* No deja regresar al login*/
        startActivity(goMenu);
    }















    public  void DropTables(){

        getApplicationContext().deleteDatabase("MyBusinessAndroid");

        link = new connection(getApplicationContext(), "MyBusinessAndroid", null, 1);
        SQLiteDatabase db = link.getWritableDatabase();

        finishAffinity();

    }







}
