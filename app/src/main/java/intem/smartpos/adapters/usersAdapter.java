package intem.smartpos.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import intem.smartpos.R;
import intem.smartpos.constructors.usersConstructor;

public class usersAdapter extends BaseAdapter{

    public usersAdapter(Context context, List<usersConstructor> list) {
        this.context = context;
        this.list = list;
    }

    Context context;
    List<usersConstructor> list;


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

    /*   vista de cada item */
    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        View viewHolder = convertView;

        LayoutInflater inflate = LayoutInflater.from(context);
        viewHolder = inflate.inflate(R.layout.view_row_user,null);

        TextView userName = viewHolder.findViewById(R.id.txtName);
        TextView nickName = viewHolder.findViewById(R.id.txtNick);
        TextView userLvl = viewHolder.findViewById(R.id.txtLevel);

        userName.setText(list.get(position).getName());
        nickName.setText(list.get(position).getId() + " - " + list.get(position).getNick());
        userLvl.setText(list.get(position).getLevel());

        return viewHolder;
    }




    @Override
    public CharSequence[] getAutofillOptions() {
        return new CharSequence[0];
    }
}
