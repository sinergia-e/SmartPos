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
import intem.smartpos.constructors.presentations_constructor;


public class presentations_adapter extends BaseAdapter {

    Context context;
    List<presentations_constructor> list;

    public presentations_adapter(Context context, List<presentations_constructor> list) {
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
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View viewHolder = convertView;

        LayoutInflater inflater = LayoutInflater.from(context);
        viewHolder = inflater.inflate(R.layout.view_row_presentation,null);

        TextView ClavePR = viewHolder.findViewById(R.id.Presentations_CodePresentation);
        TextView DescripPR = viewHolder.findViewById(R.id.Presentations_DescripPr);
        TextView QtyPresentations = viewHolder.findViewById(R.id.Presentations_QtyPzas);
        TextView PricePr = viewHolder.findViewById(R.id.Presentations_Price);

        DecimalFormat formateador = new DecimalFormat("###,###,###,###.00");
        String PrecioPr = formateador.format (list.get(position).getPricePr());
        String QtyPrString = String.valueOf(list.get(position).getQtyPr());


        Double PriceTotal = list.get(position).getQtyPr() * list.get(position).getPricePr();
        String PricePrFormat = formateador.format(PriceTotal);

        ClavePR.setText(list.get(position).getCodePResentation());
        DescripPR.setText(list.get(position).getDescripPresentation());
        QtyPresentations.setText(QtyPrString + " x  $ " + PrecioPr);
        PricePr.setText("$ " + PricePrFormat);

        return viewHolder;
    }
}