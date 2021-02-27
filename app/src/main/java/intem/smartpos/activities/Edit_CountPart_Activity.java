package intem.smartpos.activities;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import intem.smartpos.R;
import intem.smartpos.database.connection;

public class Edit_CountPart_Activity extends AppCompatActivity {

    private TextView textView_CountPart_Descrip;
    private EditText editText_CountPart_Qty;
    private Button button_CountPart_Update;
    private Button button_CountPart_Delete;
    private connection link;
    private String IdOrder;
    private String IdPart;
    private String Descrip;
    private String Qty;


    private void BindUI(){
        textView_CountPart_Descrip = (TextView) findViewById(R.id.textView_CountPart_Descrip);
        editText_CountPart_Qty = (EditText) findViewById(R.id.editText_CountPart_Qty);
        button_CountPart_Delete = (Button) findViewById(R.id.button_CountPart_Delete);
        button_CountPart_Update = (Button) findViewById(R.id.button_CountPart_Update);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_count_part_);
        link = new connection(getApplicationContext(), "MyBusinessAndroid", null, 1);
        final SQLiteDatabase dbW = link.getWritableDatabase();
        BindUI();

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            IdOrder = bundle.getString("IdOrder");
            IdPart = bundle.getString("IdPart");
            Descrip = bundle.getString("Descrip");
            Qty = bundle.getString("Qty");

            textView_CountPart_Descrip.setText(Descrip);
            editText_CountPart_Qty.setText(Qty);
        }


        button_CountPart_Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbW.execSQL("DELETE FROM PartMovsInv WHERE id = " + IdPart);
                Intent returnOrder = new Intent(getApplicationContext(),NewInventoryMovsActivity.class);
                returnOrder.putExtra("MovId",IdOrder);
                startActivity(returnOrder);

                Toast.makeText(Edit_CountPart_Activity.this, "Se elimino la partida " + Descrip, Toast.LENGTH_SHORT).show();
            }
        });


        button_CountPart_Update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Quantity = editText_CountPart_Qty.getText().toString();
                dbW.execSQL("UPDATE PartMovsInv SET Quantity = '"+Quantity+"' WHERE id = " + IdPart);
                Intent returnOrder = new Intent(getApplicationContext(),NewInventoryMovsActivity.class);
                returnOrder.putExtra("MovId",IdOrder);
                startActivity(returnOrder);

                Toast.makeText(Edit_CountPart_Activity.this, " Se actualizo la partida " + Quantity, Toast.LENGTH_SHORT).show();
            }
        });
    }





    @Override
    public void onBackPressed() {

    }




}
