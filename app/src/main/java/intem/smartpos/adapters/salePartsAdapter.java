package intem.smartpos.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.sql.Connection;
import java.util.List;

import intem.smartpos.R;
import intem.smartpos.constructors.partsSaleConstructor;

public class salePartsAdapter extends BaseAdapter {

    private Connection link;
    Context context;
    List<partsSaleConstructor> list;

    public salePartsAdapter(Context context, List<partsSaleConstructor> listSaleParts) {
        this.context = context;
        this.list = listSaleParts;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return list.get(i).getId();
    }

    @Override
    public View getView(int i, View salePartsView, ViewGroup parent) {

        View viewHolder = salePartsView;
        java.text.DecimalFormat formateador = new java.text.DecimalFormat("###,###,###,###.00");


        LayoutInflater inflate = LayoutInflater.from(context);
        viewHolder = inflate.inflate(R.layout.view_row_partsale, null);

        TextView DescripProd = viewHolder.findViewById(R.id.TextView_RowPartSale_Product);
        TextView UnitPrice = viewHolder.findViewById(R.id.TextView_RowSaleParts_PriceUnit);
        TextView TextView_Amount = viewHolder.findViewById(R.id.TextView_RowSaleParts_Amount);
        TextView TextView_Qty = viewHolder.findViewById(R.id.TextView_RowSaleParts_Qty);
        TextView TextView_Offer = viewHolder.findViewById(R.id.TextView_RowSaleParts_Offer);


        String PartQty = String.valueOf(list.get(i).getQty());
        double Discount = list.get(i).getDiscount();
        double DiscountFactor = (100-Discount)/100;

        String Amount = formateador.format(list.get(i).getQty() * list.get(i).getPrice());
        String Amount_WDisc = formateador.format(list.get(i).getQty() * (list.get(i).getPrice() * DiscountFactor));
        Double ListOfPrice = Double.valueOf(list.get(i).getPriceList());

        DescripProd.setText(list.get(i).getProdDescrip());
        String PriceUnit_String = String.valueOf(formateador.format(list.get(i).getPrice()));
        String PriceUnit_St_WDis = String.valueOf(formateador.format(list.get(i).getPrice() * DiscountFactor));

        TextView_Qty.setText(PartQty);
        if(Discount > 0 ){
            UnitPrice.setText("$" + PriceUnit_String+ " - %" + Discount + " = " + PriceUnit_St_WDis);
            TextView_Amount.setText("$" + Amount_WDisc);
        }else{
            UnitPrice.setText("$" + PriceUnit_String);
            TextView_Amount.setText("$" + Amount);
        }







        if (ListOfPrice != 1){
            TextView_Offer.setText("Precio reducido, Lista: " + ListOfPrice);
        }else{
            TextView_Offer.setText("");
        }

        return viewHolder;
    }
}
