package intem.smartpos.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import intem.smartpos.R;
import intem.smartpos.constructors.ConnectionsConstructor;

public class ConnectionsAdapter extends BaseAdapter {

    public ConnectionsAdapter(Context context, List<ConnectionsConstructor> list) {
        this.context = context;
        this.list = list;
    }

    Context context;
    List<ConnectionsConstructor> list;


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
        viewHolder = inflate.inflate(R.layout.view_row_connection,null);

        TextView NameConnection = viewHolder.findViewById(R.id.textView_NameConnection);
        TextView StringConnection = viewHolder.findViewById(R.id.textView_StringConnection);

        NameConnection.setText(list.get(position).getName());
        StringConnection.setText(list.get(position).getServerAdress());

        return viewHolder;
    }
}
