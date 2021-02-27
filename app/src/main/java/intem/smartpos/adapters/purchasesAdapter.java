package intem.smartpos.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import intem.smartpos.R;
import intem.smartpos.constructors.purchasesConstructor;

public class purchasesAdapter extends BaseAdapter {


    Context context;
    List<purchasesConstructor> list;

    public purchasesAdapter(Context context, List<purchasesConstructor> list) {
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
    public View getView(int position, View purchasesView, ViewGroup parent) {
        View viewHolder = purchasesView;

        java.text.DecimalFormat formateador = new java.text.DecimalFormat("###,###,###,###.00");

        LayoutInflater inflate = LayoutInflater.from(context);
        viewHolder = inflate.inflate(R.layout.view_row_purchase, null);

        TextView Folio = viewHolder.findViewById(R.id.textView_ListPurchases_Id);
        TextView Document = viewHolder.findViewById(R.id.textView_purchaseslist_document);
        TextView provider = viewHolder.findViewById(R.id.textView_purchaseslist_provider);
        TextView Amount = viewHolder.findViewById(R.id.textView_purchaseslist_amount);
        TextView status = viewHolder.findViewById(R.id.textView_rowpurchase_status);

        Folio.setText("Folio:" + String.valueOf(list.get(position).getId()));
        Document.setText("Factura o nota:" + String.valueOf(list.get(position).getDocument()));
        provider.setText("Proveedor: " + list.get(position).getProvider());
        status.setText(list.get(position).getStatus());

        String AmountStr = String.valueOf(formateador.format(list.get(position).getAmount()));
        Amount.setText("$ " + AmountStr);

        return viewHolder;
    }
}
