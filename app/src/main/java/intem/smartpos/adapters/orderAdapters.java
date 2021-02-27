package intem.smartpos.adapters;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import intem.smartpos.R;
import intem.smartpos.constructors.orderConstructor;

/**
 * id INTEGER PRIMARY KEY AUTOINCREMENT,Client TEXT,Date TEXT,Amount REAl,Location TEXT,Status TEXT,Export INTEGER
 */

public class orderAdapters extends BaseAdapter {

    Context context;
    List<orderConstructor> list;

    public orderAdapters(Context context, List<orderConstructor> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return list.get(position).getId();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View getView(int position, View ordersView, ViewGroup viewGroup) {

        View viewHolder = ordersView;

        LayoutInflater inflate = LayoutInflater.from(context);
        viewHolder = inflate.inflate(R.layout.view_row_orders,null);

        TextView Folio = viewHolder.findViewById(R.id.tV_OrdersRow_Folio);
        TextView ClientName = viewHolder.findViewById(R.id.tV_OrdersRow_Client);
        TextView DateOrder = viewHolder.findViewById(R.id.tV_OrdersRow_Date);
        TextView Amount = viewHolder.findViewById(R.id.tV_OrdersRow_Amount);
        TextView Estatus = viewHolder.findViewById(R.id.tV_OrdersRow_Estatus);
        TextView IdOrders = viewHolder.findViewById(R.id.tV_OrdersRow_IdServer);

        java.text.DecimalFormat formateador = new java.text.DecimalFormat("###,###,###,###.00");

        String OrderID = String.valueOf(list.get(position).getId());
        String IdInServer = String.valueOf(list.get(position).getIdInServer());
        String ConsecServer = String.valueOf(list.get(position).getConsecInServer());
        String FinalAmount = formateador.format(list.get(position).getAmount());

        Folio.setText("Folio: " + OrderID);
        ClientName.setText(list.get(position).getClient());
        DateOrder.setText(list.get(position).getDate());
        Amount.setText("$" + FinalAmount);
        Estatus.setText(list.get(position).getStatus());
        IdOrders.setText("En el servidor es el pedido " + ConsecServer );

        return viewHolder;

    }
}
