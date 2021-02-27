package intem.smartpos.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import intem.smartpos.R;
import intem.smartpos.constructors.paymentsConstructor;

public class paymentsAdapter extends BaseAdapter {

    Context context;
    List<paymentsConstructor> list;

    public paymentsAdapter(Context context, List<paymentsConstructor> list) {
        this.context = context;
        this.list = list;
    }

    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View paymentsView, ViewGroup parent) {
        View viewHolder = paymentsView;

        LayoutInflater inflate = LayoutInflater.from(context);
        viewHolder = inflate.inflate(R.layout.view_row_payments,null);

        TextView IdPayment = viewHolder.findViewById(R.id.TextView_Payments_id);
        TextView DatePayment = viewHolder.findViewById(R.id.TextView_Payments_fecha);
        TextView Amount = viewHolder.findViewById(R.id.TextView_Payments_amount);

        String StAmount = String.valueOf(list.get(position).getAmount());

        IdPayment.setText("ID del abono: " + list.get(position).getId());
        DatePayment.setText("Fecha del pago: " + list.get(position).getDate());
        Amount.setText("Abono:" + StAmount);

        return viewHolder;
    }
}
