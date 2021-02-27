package intem.smartpos.activities;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import intem.smartpos.R;
import intem.smartpos.adapters.FoodAdapter;
import intem.smartpos.constructors.prodsConstructor;
import intem.smartpos.database.connection;

public class Rest_CaptureCommandActivity extends AppCompatActivity {

    public ListView lV_ListProds;
    private ArrayList<prodsConstructor> ProdsArray;
    private connection link;
    prodsConstructor prodsItem;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rest__capture_command);
        link = new connection(getApplicationContext(), "MyBusinessAndroid", null, 1);


        FillProds("");

    }




    private void BindUI(){
        lV_ListProds = (ListView) findViewById(R.id.Rest_ListView_Parts);
    }







    /***   Recupera los productos y los pone en el list View ***/
    /***   ====================================================================== ***/
    private void FillProds(String cCondicion) {
        BindUI();
        SQLiteDatabase db = link.getReadableDatabase();
        Cursor c = db.rawQuery("Select id,Code,Description,ProdCategory,Brand,Tax,Price1,Exist,ExSucursal,Export,Offer from Prods " + cCondicion, null);

        ProdsArray = new ArrayList<prodsConstructor>();
        if (c.moveToFirst()) {
            do {
                //id,Code,Description,ProdCategory,Brand,Tax,Price,Exist,ExSucursal,Export
                prodsItem = new prodsConstructor(c.getInt(0),c.getString(1),c.getString(2),c.getString(3),c.getString(4),c.getString(5),c.getDouble(6),c.getString(7),c.getString(8),c.getInt(9),c.getDouble(10) );
                ProdsArray.add(prodsItem);
            } while (c.moveToNext());
        }else{
            Toast.makeText(this, "No hay ningun producto", Toast.LENGTH_SHORT).show();
        }


        FoodAdapter adapter = new FoodAdapter(getApplicationContext(), ProdsArray);
        lV_ListProds.setAdapter(adapter);

        int countProds = ProdsArray.size();
    }








}
