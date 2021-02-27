package intem.smartpos.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import intem.smartpos.R;
import intem.smartpos.constructors.countsConstructor;

public class countsAdapter extends BaseAdapter {

    Context context;
    List<countsConstructor> list;

    public countsAdapter(Context context, List<countsConstructor> list) {
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
    public View getView(int position, View counts, ViewGroup viewGroup) {
        View viewHolder = counts;

        LayoutInflater inflater = LayoutInflater.from(context);
        viewHolder = inflater.inflate(R.layout.view_row_counts,null);

        TextView Code = viewHolder.findViewById(R.id.textView_rowcounts_code);
        TextView descript = viewHolder.findViewById(R.id.textView_rowcounts_description);
        TextView Qty = viewHolder.findViewById(R.id.textView_rowcounts_Qty);
        TextView ExportTextView = viewHolder.findViewById(R.id.textView_rowcount_exportstatus);

        String Quantity = String.valueOf(list.get(position).getQty());
        Integer ExportStInt = list.get(position).getExport();
        String ExportStString = "";

        if(ExportStInt == 1){
            ExportStString = "Registrado en servidor";
        }else{
            ExportStString = "Pendiente por enviar";
        }

        Code.setText(list.get(position).getCodeProd());
        descript.setText(list.get(position).getDescrip());
        Qty.setText(Quantity);
        ExportTextView.setText(ExportStString);
        return viewHolder;
    }
}
