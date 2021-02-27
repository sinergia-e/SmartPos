package intem.smartpos.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
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

public class Edit_PartOrder_Activity extends AppCompatActivity {

    connection link;

    private TextView tV_EditOrderPart_Descrip,eT_OrderPartPrice1,eT_OrderPartPrice2,eT_OrderPartPrice3;
    private EditText eT_EditOrderPart_Qty,eT_PartPrice_Edited,eT_ObservationsOrderPart;
    private Button bT_EditORderPart_Delete,bT_EditOrderPart_Update,ButtonAssignsPrice1,ButtonAssignsPrice2,ButtonAssignsPrice3;
    private Integer ServerIp,PriceByQty;
    private String Qty,Descrip,IdPart,IdOrder,CodeProd,PriceReg;
    private Double QtyPart,Price,ValuePriceMin;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_part_order_);

        link = new connection(getApplicationContext(), "MyBusinessAndroid", null, 1);



        SQLiteDatabase dbR = link.getReadableDatabase();
        Cursor cIp = dbR.rawQuery("SELECT ServerIP,PriceQty from AppConfig ",null);

        if(cIp.moveToFirst()){
            PriceByQty = cIp.getInt(1);
        }

        tV_EditOrderPart_Descrip = (TextView) findViewById(R.id.tV_EditOrderPart_Descrip);
        eT_EditOrderPart_Qty = (EditText) findViewById(R.id.eT_EditOrderPart_Qty);
        bT_EditORderPart_Delete = (Button) findViewById(R.id.bT_EditOrderPart_Delete);
        bT_EditOrderPart_Update = (Button) findViewById(R.id.bT_EditOrderPart_Update);
        eT_PartPrice_Edited = (EditText) findViewById(R.id.eT_PartPrice_Orders);
        eT_ObservationsOrderPart = findViewById(R.id.editText_OrderPartEdit_Observ);

        eT_OrderPartPrice1 = findViewById(R.id.editOrderPart_Price1);
        eT_OrderPartPrice2 = findViewById(R.id.editOrderPart_Price2);
        eT_OrderPartPrice3 = findViewById(R.id.editOrderPart_Price3);

        ButtonAssignsPrice1 = findViewById(R.id.editOrderPart_buttonPrice1);
        ButtonAssignsPrice2 = findViewById(R.id.editOrderPart_buttonPrice2);
        ButtonAssignsPrice3 = findViewById(R.id.editOrderPart_buttonPrice3);




        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            IdOrder = bundle.getString("IdOrder");
            IdPart = bundle.getString("IdPart");
            Descrip = bundle.getString("Descrip");
            PriceReg = bundle.getString("Price");
            eT_PartPrice_Edited.setText(PriceReg);
            Qty = bundle.getString("Qty");
            tV_EditOrderPart_Descrip.setText(Descrip);
        }


        Cursor DtaPart = dbR.rawQuery("select PartOrders.Observations,PartOrders.Price,Prods.Price1,Prods.Price2,Prods.Price3,PartOrders.PresentationQty from PartOrders INNER JOIN prods ON PartOrders.ProdId = Prods.Code where PartOrders.id = '"+ IdPart +"' ",null);

        if (DtaPart.moveToFirst()){
            //eT_ObservationsOrderPart.setText("" + DtaPart.getString(0));
            Toast.makeText(this, "Observacion:" + DtaPart.getString(0) + "    Precio: " + DtaPart.getString(1), Toast.LENGTH_SHORT).show();

            String PrecioPart = String.valueOf(DtaPart.getDouble(1) * DtaPart.getDouble(5));
            eT_PartPrice_Edited.setText("$" + PrecioPart);
            eT_ObservationsOrderPart.setText(DtaPart.getString(0));

            eT_OrderPartPrice1.setText(DtaPart.getString(2));
            eT_OrderPartPrice2.setText(DtaPart.getString(3));
            eT_OrderPartPrice3.setText(DtaPart.getString(4));
            ValuePriceMin = Double.valueOf(DtaPart.getString(4));

            eT_EditOrderPart_Qty.setText(Qty);

        }




        ButtonAssignsPrice1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ValuePrice1 = eT_OrderPartPrice1.getText().toString();
                eT_PartPrice_Edited.setText(ValuePrice1);
            }
        });


        ButtonAssignsPrice2.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               String ValuePrice2 = eT_OrderPartPrice2.getText().toString();
               eT_PartPrice_Edited.setText(ValuePrice2);
           }
       });


        ButtonAssignsPrice3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ValuePrice3 = eT_OrderPartPrice3.getText().toString();
                eT_PartPrice_Edited.setText(ValuePrice3);
            }
        });




                bT_EditOrderPart_Update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        double PriceEdited = Double.parseDouble(eT_PartPrice_Edited.getText().toString());

                        if (PriceEdited >= ValuePriceMin && PriceEdited > 0) {

                            String eT_Qty = eT_EditOrderPart_Qty.getText().toString();
                            String NewObservation = eT_ObservationsOrderPart.getText().toString();
                            double Quantity = Double.parseDouble(eT_Qty);

                            SQLiteDatabase db = link.getWritableDatabase();
                            db.execSQL("UPDATE PartOrders set Quantity = " + Quantity + ",Observations='" + NewObservation + "' where id = '" + IdPart + "' ");

                            if (PriceByQty == 1) {
                                eT_PartPrice_Edited.setEnabled(false);

                                Cursor DtaPart = db.rawQuery("SELECT ProdId,Quantity,Price from PartOrders where id = " + IdPart, null);
                                if (DtaPart.moveToFirst()) {
                                    Price = DtaPart.getDouble(2);
                                    CodeProd = DtaPart.getString(0);
                                    QtyPart = DtaPart.getDouble(1);
                                }

                                Cursor PricesProd = db.rawQuery("SELECT Price1,Price2,Price3,Price4,Price5,Price6,Price7,Price8,Price9,Qty2,Qty3,Qty4,Qty5,Qty6,Qty7,Qty8,Qty9 from Prods where Code = '" + CodeProd + "' ", null);
                                if (PricesProd.moveToFirst()) {
                                    Double Price1 = PricesProd.getDouble(0);
                                    Double Price2 = PricesProd.getDouble(1);
                                    Double Price3 = PricesProd.getDouble(2);
                                    Double Price4 = PricesProd.getDouble(3);
                                    Double Price5 = PricesProd.getDouble(4);
                                    Double Price6 = PricesProd.getDouble(5);
                                    Double Price7 = PricesProd.getDouble(6);
                                    Double Price8 = PricesProd.getDouble(7);
                                    Double Price9 = PricesProd.getDouble(8);

                                    Integer Qty2 = PricesProd.getInt(9);
                                    Integer Qty3 = PricesProd.getInt(10);
                                    Integer Qty4 = PricesProd.getInt(11);
                                    Integer Qty5 = PricesProd.getInt(12);
                                    Integer Qty6 = PricesProd.getInt(13);
                                    Integer Qty7 = PricesProd.getInt(14);
                                    Integer Qty8 = PricesProd.getInt(15);
                                    Integer Qty9 = PricesProd.getInt(16);

                                    Double QtyPart = Double.valueOf(0);

                                    Cursor CantPart = db.rawQuery("SELECT Quantity from PartOrders where ProdId = '" + CodeProd + "' and OrderId = '" + IdOrder + "' ", null);
                                    if (CantPart.moveToFirst()) {
                                        QtyPart = CantPart.getDouble(0);
                                    }


                                    if (QtyPart < Qty2) {
                                        Price = Price1;
                                    }

                                    if (Qty2 > 0) {
                                        if (Qty3 > 0) {
                                            if (QtyPart >= Qty2 && QtyPart < Qty3) {
                                                Price = Price2;
                                                Toast.makeText(getApplicationContext(), "Se asigno el precio 2 por el volumen de compra", Toast.LENGTH_SHORT).show();
                                            } else if (QtyPart >= Qty2) {
                                                Price = Price2;
                                            }
                                        } else {
                                            if (QtyPart >= Qty2) {
                                                Price = Price2;
                                                Toast.makeText(getApplicationContext(), "Se asigno el precio 2 por el volumen de compra", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }

                                    if (Qty3 > 0) {
                                        if (Qty4 > 0) {
                                            if (QtyPart >= Qty3 && QtyPart < Qty4) {
                                                Price = Price3;
                                                Toast.makeText(getApplicationContext(), "Se asigno el precio 3 por el volumen de compra", Toast.LENGTH_SHORT).show();
                                            } else if (QtyPart >= Qty3) {
                                                Price = Price3;
                                            }
                                        } else {
                                            if (QtyPart >= Qty3) {
                                                Price = Price3;
                                                Toast.makeText(getApplicationContext(), "Se asigno el precio 3 por el volumen de compra", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }


                                    if (Qty4 > 0) {
                                        if (Qty5 > 0) {
                                            if (QtyPart >= Qty4 && QtyPart < Qty5) {
                                                Price = Price4;
                                                Toast.makeText(getApplicationContext(), "Se asigno el precio 4 por el volumen de compra", Toast.LENGTH_SHORT).show();
                                            } else if (QtyPart >= Qty4) {
                                                Price = Price4;
                                            }
                                        } else {
                                            if (QtyPart >= Qty4) {
                                                Price = Price4;
                                                Toast.makeText(getApplicationContext(), "Se asigno el precio 4 por el volumen de compra", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }

                                    if (Qty5 > 0) {
                                        if (Qty6 > 0) {
                                            if (QtyPart >= Qty5 && QtyPart < Qty6) {
                                                Price = Price5;
                                                Toast.makeText(getApplicationContext(), "Se asigno el precio 5 por el volumen de compra", Toast.LENGTH_SHORT).show();
                                            } else if (QtyPart >= Qty5) {
                                                Price = Price5;
                                            }
                                        } else {
                                            if (QtyPart >= Qty5) {
                                                Price = Price5;
                                                Toast.makeText(getApplicationContext(), "Se asigno el precio 5 por el volumen de compra", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }


                                    if (Qty6 > 0) {
                                        if (Qty7 > 0) {
                                            if (QtyPart >= Qty6 && QtyPart < Qty7) {
                                                Price = Price6;
                                                Toast.makeText(getApplicationContext(), "Se asigno el precio 6 por el volumen de compra", Toast.LENGTH_SHORT).show();
                                            } else if (QtyPart >= Qty6) {
                                                Price = Price6;
                                            }
                                        } else {
                                            if (QtyPart >= Qty6) {
                                                Price = Price6;
                                                Toast.makeText(getApplicationContext(), "Se asigno el precio 6 por el volumen de compra", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }

                                    if (Qty7 > 0) {
                                        if (Qty8 > 0) {
                                            if (QtyPart >= Qty7 && QtyPart < Qty8) {
                                                Price = Price7;
                                                Toast.makeText(getApplicationContext(), "Se asigno el precio 7 por el volumen de compra", Toast.LENGTH_SHORT).show();
                                            } else if (QtyPart >= Qty7) {
                                                Price = Price7;
                                            }
                                        } else {
                                            if (QtyPart >= Qty7) {
                                                Price = Price7;
                                                Toast.makeText(getApplicationContext(), "Se asigno el precio 7 por el volumen de compra", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }

                                    if (Qty8 > 0) {
                                        if (Qty9 > 0) {
                                            if (QtyPart >= Qty8 && QtyPart < Qty9) {
                                                Price = Price8;
                                                Toast.makeText(getApplicationContext(), "Se asigno el precio 8 por el volumen de compra", Toast.LENGTH_SHORT).show();
                                            } else if (QtyPart >= Qty8) {
                                                Price = Price8;
                                            }
                                        } else {
                                            if (QtyPart >= Qty8) {
                                                Price = Price8;
                                                Toast.makeText(getApplicationContext(), "Se asigno el precio 8 por el volumen de compra", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }

                                    if (Qty9 > 0) {
                                        Price = Price9;
                                        if (QtyPart <= Qty9) {
                                            Toast.makeText(getApplicationContext(), "Se asigno el precio 9 por el volumen de compra", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                                db.execSQL("UPDATE PartOrders set Price = " + Price + " where id = '" + IdPart + "' ");
                            } else {
                                Price = Double.valueOf(eT_PartPrice_Edited.getText().toString());
                                db.execSQL("UPDATE PartOrders set Price = " + Price + " where id = '" + IdPart + "' ");
                            }

                            Intent returnOrder = new Intent(Edit_PartOrder_Activity.this, NewOrderActivity.class);
                            returnOrder.putExtra("order", IdOrder);
                            returnOrder.putExtra("open", "true");
                            startActivity(returnOrder);
                            Toast.makeText(Edit_PartOrder_Activity.this, "Se actualizo la partida", Toast.LENGTH_SHORT).show();


                        } else {
                            Toast.makeText(Edit_PartOrder_Activity.this, "No se puede asignar un precio inferior al 3", Toast.LENGTH_SHORT).show();
                        }


                    }
                });













        bT_EditORderPart_Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SQLiteDatabase db = link.getWritableDatabase();
                db.execSQL("DELETE from PartOrders where id = '"+ IdPart +"' ");
                Intent returnOrder = new Intent(Edit_PartOrder_Activity.this,NewOrderActivity.class);
                returnOrder.putExtra("order",IdOrder);
                returnOrder.putExtra("open","true");
                startActivity(returnOrder);
                Toast.makeText(Edit_PartOrder_Activity.this, "Se elimino la partida", Toast.LENGTH_SHORT).show();
            }
        });

    }



    @Override
    public void onBackPressed() {

    }

}
