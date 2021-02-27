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


public class clientsSimpleAdapter extends BaseAdapter {

    Context context;
    List<clientsConstructor> list;

    public clientsSimpleAdapter(Context context, List<clientsConstructor> list) {
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
    public View getView(int position, View clients, ViewGroup viewGroup) {
        View viewHolder = clients;

        LayoutInflater inflater = LayoutInflater.from(context);
        viewHolder = inflater.inflate(R.layout.view_row_simple_client,null);

        TextView Code = viewHolder.findViewById(R.id.textView_SimpleClients_CodeCl);
        TextView Name = viewHolder.findViewById(R.id.textView_SimpleClients_NameCl);

        Code.setText(list.get(position).getCodeMyBusiness());
        Name.setText(list.get(position).getNameClient());

        return viewHolder;
    }
}
