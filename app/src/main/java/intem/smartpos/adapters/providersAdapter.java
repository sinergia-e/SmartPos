package intem.smartpos.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import intem.smartpos.R;
import intem.smartpos.constructors.providersConstructor;

/**
 * Created by soluc on 23/04/2019.
 */

public class providersAdapter extends BaseAdapter {

    Context context;
    List<providersConstructor> list;

    public providersAdapter(Context context, List<providersConstructor> list) {
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
    public View getView(int position, View providersView, ViewGroup parent) {
        View viewHolder = providersView;

        LayoutInflater inflate = LayoutInflater.from(context);
        viewHolder = inflate.inflate(R.layout.view_row_providers, null);

        TextView codeProvider = viewHolder.findViewById(R.id.textView_rowproviders_code);
        TextView nameProvider = viewHolder.findViewById(R.id.textView_row_provider_name);

        codeProvider.setText(list.get(position).getProviderCode());
        nameProvider.setText(list.get(position).getProviderName());

        return viewHolder;
    }
}
