package intem.smartpos.adapters;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import intem.smartpos.R;
import intem.smartpos.constructors.orderPartsConstructor;

public class orderPartsAdapter extends BaseAdapter{

    Context context;
    List<orderPartsConstructor> listParts;

    public orderPartsAdapter(Context context, List<orderPartsConstructor> list) {
        this.context = context;
        this.listParts = list;
    }

    @Override
    public int getCount() {
        return listParts.size();
    }

    @Override
    public Object getItem(int position) {
        return listParts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return listParts.get(position).getId();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View getView(int position, View ordersPart, ViewGroup viewGroup) {

        View viewHolder = ordersPart;

        LayoutInflater inflate = LayoutInflater.from(context);
        viewHolder = inflate.inflate(R.layout.view_row_order_part,null);

        TextView DescripPart = viewHolder.findViewById(R.id.OrderParts_Descrip);
        TextView QtyPart = viewHolder.findViewById(R.id.tV_OrderParts_Qty);
        TextView TotalPart = viewHolder.findViewById(R.id.tV_OrdersPart_Total);
        TextView DiscountPrice = viewHolder.findViewById(R.id.OrderPart_Discount);

        java.text.DecimalFormat formateador = new java.text.DecimalFormat("###,###,###,###.##");

        float Total = (float) (listParts.get(position).getPrice() * listParts.get(position).getQty());
        Double Price = listParts.get(position).getPrice() * listParts.get(position).getQtyPr();

        formateador = new DecimalFormat("###,###,###,###.00");
        String Final = formateador.format (Total);
        String FinalAmount = String.valueOf(Final);
        String FormatPrice = formateador.format(Price);


        String Quantity = String.valueOf(listParts.get(position).getQty()  / listParts.get(position).getQtyPr() );



        DescripPart.setText(listParts.get(position).getDescripPr() + ' ' + listParts.get(position).getDescrip());
        QtyPart.setText(Quantity + " X " + FormatPrice);
        TotalPart.setText("$" + FinalAmount);

        if( listParts.get(position).getDiscount() > 0 ){

            Double Percentage = Double.valueOf(listParts.get(position).getDiscount());
            Double Precio = Double.valueOf(listParts.get(position).getPrice());
            Double Cantidad = Double.valueOf(listParts.get(position).getQty());
            Double PriceDiscount = (Precio*Cantidad) * ((100-Percentage)/100);

            DiscountPrice.setText("Precio con descuento al " + Percentage + "% = $" + PriceDiscount );
        }

        return viewHolder;
    }
}
