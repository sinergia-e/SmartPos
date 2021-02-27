package intem.smartpos.activities;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import intem.smartpos.R;
import intem.smartpos.adapters.ConnectionsAdapter;
import intem.smartpos.adapters.usersAdapter;
import intem.smartpos.constructors.ConnectionsConstructor;
import intem.smartpos.constructors.usersConstructor;
import intem.smartpos.database.connection;
import static intem.smartpos.R.layout.view_new_connection;

public class ConnectionsActivity extends AppCompatActivity {

    connection link;
    private ArrayList<ConnectionsConstructor> ArrayConnections;
    ConnectionsConstructor ItemConnections;
    private ListView ConnectionsList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connections);


        link = new connection(getApplicationContext(), "MyBusinessAndroid", null, 1);
        ConnectionsList = findViewById(R.id.Connections_ListView);
        registerForContextMenu(ConnectionsList);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowAlert("Usuarios", "Crear nuevo usuario");
            }
        });


        RecoveryConnections();
    }




























    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu_connections,menu);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        String Status = null;

        switch (item.getItemId()){

            case R.id.ActiveConn:
                QuestionConnActive(String.valueOf(ArrayConnections.get(info.position).getId()));
                return true;

            case R.id.Delete:
                DeleteConn(String.valueOf(ArrayConnections.get(info.position).getId()));
                return true;



            default:
                return super.onContextItemSelected(item);
        }
    }









    private void QuestionConnActive(final String IdConn){
        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(ConnectionsActivity.this);
        dialogo1.setTitle("Importante");
        dialogo1.setMessage("¿Quieres activar esta conexion?");
        dialogo1.setCancelable(false);
        dialogo1.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {

            }
        });
        dialogo1.setPositiveButton("Si,Activala", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
                //Toast.makeText(ConnectionsActivity.this, "Activar " + IdConn, Toast.LENGTH_SHORT).show();
                ActiveConnection(IdConn);
            }
        });
        dialogo1.show();
    }



    private void ActiveConnection(String idConn){
        SQLiteDatabase db = link.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * from Connections where id = '"+ idConn +"' ",null);

        String NmConnection = "";
        String StrConnection = "";

        if (cursor.moveToFirst()){
            NmConnection = cursor.getString(1);
            StrConnection = cursor.getString(2);
            db.execSQL("update AppConfig set ServerIP = '"+ StrConnection +"' ");
            link.close();
        }

        Toast.makeText(this, "Conexion activa: " + NmConnection, Toast.LENGTH_SHORT).show();
    }

























    private void DeleteConn(final String IdConn){
        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(ConnectionsActivity.this);
        dialogo1.setTitle("Importante");
        dialogo1.setMessage("¿Quieres ELIMINAR esta conexion?");
        dialogo1.setCancelable(false);
        dialogo1.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {

            }
        });
        dialogo1.setPositiveButton("Si,Borrar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
                //Toast.makeText(ConnectionsActivity.this, "Eliminar " + IdConn, Toast.LENGTH_SHORT).show();
                DeleteConnect(IdConn);
            }
        });
        dialogo1.show();

    }



    private void DeleteConnect(String id){
        SQLiteDatabase db = link.getWritableDatabase();
        db.execSQL("delete from Connections where id = '"+id+"' ");
        RecoveryConnections();
        link.close();
    }













    private void RecoveryConnections(){

        SQLiteDatabase db = link.getReadableDatabase();
        Cursor cursor = db.rawQuery("Select id,NameConnection,StringConn from Connections ", null);
        Integer conteo = 0;

        ArrayConnections = new ArrayList<ConnectionsConstructor>();
        if (cursor.moveToFirst()) {
            do {
                ItemConnections = new ConnectionsConstructor(cursor.getInt(0), cursor.getString(1), cursor.getString(2));
                ArrayConnections.add(ItemConnections);
                conteo = conteo + 1;
            } while (cursor.moveToNext());
        }

        ConnectionsAdapter adapter = new ConnectionsAdapter(getApplicationContext(), ArrayConnections);
        ConnectionsList.setAdapter(adapter);
        Toast.makeText(this, "conexiones : " + conteo, Toast.LENGTH_SHORT).show();
/*
        UsersListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(UsersActivity.this,EditUserActivity.class);
                intent.putExtra("id",usersArray.get(i).getId());
                startActivity(intent);
                return false;
            }
        });
*/

    }






    private void ShowAlert(String title,String messaqe){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (title != null) builder.setTitle(title);
        if (messaqe != null) builder.setMessage(messaqe);

        View newConnectionInflated = LayoutInflater.from(this).inflate(view_new_connection, null);
        builder.setView(newConnectionInflated);

        final EditText NameConnection = (EditText) newConnectionInflated.findViewById(R.id.editText_NameConnection);
        final EditText StringConnection = (EditText) newConnectionInflated.findViewById(R.id.editText_StringConnection);
        final EditText ObservConnection = (EditText) newConnectionInflated.findViewById(R.id.editText_ObservConnection);

        builder.setPositiveButton("Guardarlo", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String Name = NameConnection.getText().toString();
                String String = StringConnection.getText().toString();
                String ObservConn = ObservConnection.getText().toString();

                if (Name.length() > 0 || String.length() > 0) {
                    InsertConnection(Name,String,ObservConn);
                } else {
                    Toast.makeText(ConnectionsActivity.this, "El nombre y la cadena de conexion no pueden estar vacios!", Toast.LENGTH_LONG).show();
                }
            }
        });

        AlertDialog newConnDialog = builder.create();
        newConnDialog.show();

        ConnectionsList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                return false;
            }
        });


    }















    private void InsertConnection(String NmConnn, String StrConn,String ObservConn){
        link = new connection(this, "MyBusinessAndroid", null, 1);
        SQLiteDatabase db = link.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("NameConnection", NmConnn);
        values.put("StringConn", StrConn);
        values.put("Observ", ObservConn);
        Long idResult = db.insert("Connections", "id", values);

        RecoveryConnections();
        link.close();
    }













    @Override
    public void onBackPressed() {
        Intent goMenu = new Intent(getApplicationContext(),MenuActivity.class);
        startActivity(goMenu);
    }




}
