
package intem.smartpos.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.RequestQueue;

import java.text.DecimalFormat;
import java.util.List;

import intem.smartpos.R;
import intem.smartpos.constructors.prodsConstructor;

public class FoodAdapter extends BaseAdapter {


    public RequestQueue request;
    public String imageName;
    private String ServerIp;

    public FoodAdapter(Context context, List<prodsConstructor> listProds){
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
    public View getView(int position, View prodsView, ViewGroup viewGroup) {

        View viewHolder = prodsView;

        LayoutInflater inflate = LayoutInflater.from(context);
        viewHolder = inflate.inflate(R.layout.view_row_food, null);

        TextView DescripProd = viewHolder.findViewById(R.id.Rest_textView_foodDescrip);
        TextView PriceProds = viewHolder.findViewById(R.id.Rest_textView_foodPrice);

        DecimalFormat formateador = new DecimalFormat("###,###,###,###.00");
        String Price = formateador.format(list.get(position).getPrice());

        DescripProd.setText(list.get(position).getDescription());
        PriceProds.setText("$" + Price);




        return viewHolder;


    }

}
