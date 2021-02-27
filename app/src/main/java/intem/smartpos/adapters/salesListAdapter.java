package intem.smartpos.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import intem.smartpos.R;
import intem.smartpos.constructors.SalesConstructor;

public class salesListAdapter extends BaseAdapter {

    public salesListAdapter(Context context,List<SalesConstructor> list){
        this.context = context;
        this.list = list;
    }


    Context context;
    List<SalesConstructor> list;




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
    public View getView(int position, View convertView, ViewGroup parent) {

        View viewHolder = convertView;
        LayoutInflater inflate = LayoutInflater.from(context);
        viewHolder = inflate.inflate(R.layout.view_salelist_row,null);

        DecimalFormat formateador = new DecimalFormat("###,###,###,###.00");

        TextView idSale = viewHolder.findViewById(R.id.RowSalesList_IdSale);
        TextView Client = viewHolder.findViewById(R.id.RowSalesList_Client);
        TextView Status = viewHolder.findViewById(R.id.RowSalesList_Status);
        TextView Import = viewHolder.findViewById(R.id.RowSalesList_Import);

        String ImportTxt = String.valueOf(formateador.format(list.get(position).getImport()));
        String IdTxt = String.valueOf(list.get(position).getId());

        Status.setText("Estado del documento : " + list.get(position).getStatus());

        idSale.setText("Folio: " + IdTxt);

        Client.setText(list.get(position).getClient());
        Import.setText("$ " + ImportTxt);

        return viewHolder;
    }
}
