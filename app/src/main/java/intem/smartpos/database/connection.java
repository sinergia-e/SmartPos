package intem.smartpos.database;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.support.annotation.RequiresApi;

import intem.smartpos.utilities.functions_db;

public class connection extends SQLiteOpenHelper {
    public connection(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    public connection(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(functions_db.CreateSales);
        db.execSQL(functions_db.CreateSalesParts);
        db.execSQL(functions_db.CreateProviders);
        db.execSQL(functions_db.CreatePurchases);
        db.execSQL(functions_db.CreatePurchasePart);
        db.execSQL(functions_db.CreateCortes);
        db.execSQL(functions_db.CreateProds);
        db.execSQL(functions_db.CreatePresentations);
        db.execSQL(functions_db.CreateUsers);
        db.execSQL(functions_db.CreateClients);
        db.execSQL(functions_db.CreateCredit);
        db.execSQL(functions_db.CreateOrders);
        db.execSQL(functions_db.CreatePartOrders);
        db.execSQL(functions_db.CreateAppConfig);
        db.execSQL(functions_db.CreateMovsInv);
        db.execSQL(functions_db.CreatePartMovsInv);
        db.execSQL(functions_db.CreateAdditionalProductReportingData);
        db.execSQL(functions_db.CreateConnections);
        db.execSQL(functions_db.InsertUser);
        db.execSQL(functions_db.InsertConfig);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1 ) {
        db.execSQL("DROP TABLE IF EXISTS Sales");
        db.execSQL("DROP TABLE IF EXISTS SalesParts");
        db.execSQL("DROP TABLE IF EXISTS Purchases");
        db.execSQL("DROP TABLE IF EXISTS Providers");
        db.execSQL("DROP TABLE IF EXISTS PurchasePart");
        db.execSQL("DROP TABLE IF EXISTS CreateCortes");
        db.execSQL("DROP TABLE IF EXISTS Prods");
        db.execSQL("DROP TABLE IF EXISTS Presentations");
        db.execSQL("DROP TABLE IF EXISTS Users");
        db.execSQL("DROP TABLE IF EXISTS Clients");
        db.execSQL("DROP TABLE IF EXISTS Credit");
        db.execSQL("DROP TABLE IF EXISTS Orders");
        db.execSQL("DROP TABLE IF EXISTS PartOrder");
        db.execSQL("DROP TABLE IF EXISTS MovsInv");
        db.execSQL("DROP TABLE IF EXISTS PartMovsInv");
        db.execSQL("DROP TABLE IF EXISTS AppConfig");
        db.execSQL("DROP TABLE IF EXISTS Connections");
        db.execSQL("DROP TABLE IF EXISTS AdditionalProductReportingData");
        onCreate(db);
    }
}
