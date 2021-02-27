package intem.smartpos.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import intem.smartpos.R;
import intem.smartpos.constructors.clientsConstructor;

/**
 * Created by soluc on 13/09/2017.
 */

public class clientsAdapter extends BaseAdapter {
    Context context;
    List<clientsConstructor> list;

    public clientsAdapter(Context context, List<clientsConstructor> list) {
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

    @Override
    public View getView(int position, View clientsView, ViewGroup viewGroup) {
        View viewHolder = clientsView;

        java.text.DecimalFormat formateador = new java.text.DecimalFormat("###,###,###,###.00");

        LayoutInflater inflate = LayoutInflater.from(context);
        viewHolder = inflate.inflate(R.layout.view_row_clients,null);

        TextView Name = viewHolder.findViewById(R.id.tV_ListClients_Name);
        TextView Adress = viewHolder.findViewById(R.id.tV_ListClients_Adress);
        TextView RFC = viewHolder.findViewById(R.id.tV_ListClients_RFC);
        TextView Email = viewHolder.findViewById(R.id.tV_ListClients_Email);
        TextView Debt = viewHolder.findViewById(R.id.textView_RowClients_Debt);

        Name.setText(list.get(position).getNameClient());
        Adress.setText(list.get(position).getStreetClient() + "," + list.get(position).getColonyClient() + " " + list.get(position).getCityClient() + " " + list.get(position).getCpClient());
        RFC.setText(list.get(position).getRFCClient());
        Email.setText(list.get(position).getEmailClient());

        String DebtStr = String.valueOf(formateador.format(list.get(position).getDebt()));
        Debt.setText("El saldo del Cliente es: $ " + DebtStr);

        return viewHolder;
    }

}
