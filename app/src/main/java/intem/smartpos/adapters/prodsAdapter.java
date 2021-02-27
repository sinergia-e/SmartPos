
package intem.smartpos.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import intem.smartpos.R;
import intem.smartpos.constructors.prodsConstructor;

public class prodsAdapter extends BaseAdapter {

    public prodsAdapter(Context context, List<prodsConstructor> listProds){
        this.context = context;
        this.list = listProds;
    }

    Context context;
    List<prodsConstructor> list;


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
    public View getView(final int position, View prodsView, ViewGroup viewGroup) {

        View viewHolder = prodsView;

        LayoutInflater inflate = LayoutInflater.from(context);
        viewHolder = inflate.inflate(R.layout.view_row_prods, null);

        TextView DescripProd = viewHolder.findViewById(R.id.textViewProdsDescrip);
        TextView PriceProds = viewHolder.findViewById(R.id.textViewProdsPrecio);
        TextView ExistProd = viewHolder.findViewById(R.id.textViewProdExist);
        TextView SucExist = viewHolder.findViewById(R.id.textView_RowProds_ExistSuc);
        TextView OfferPrice = viewHolder.findViewById(R.id.ListProds_OfferPrice);
        ImageView offerView = viewHolder.findViewById(R.id.ProdsList_Offer);

        DecimalFormat formateador = new DecimalFormat("###,###,###,###.00");
        String Price = formateador.format(list.get(position).getPrice());

        DescripProd.setText(list.get(position).getDescription());
        PriceProds.setText("Precio de Lista: $" + Price);
        ExistProd.setText("Existencia: " + list.get(position).getExist());
        SucExist.setText(list.get(position).getExSucursal());

        if (list.get(position).getOffer() > 0) {
            Double discount = list.get(position).getOffer();
            Double Factor = 100 - discount;
            Double PriceComplete = list.get(position).getPrice();
            Double PriceDiscount = (Factor/100)*PriceComplete;
            String FinalPrice = formateador.format(PriceDiscount);
            offerView.setImageResource(R.drawable.ofertaim);
            OfferPrice.setText("Precio con descuento: $" + FinalPrice);
        }

        return viewHolder;
    }

}

