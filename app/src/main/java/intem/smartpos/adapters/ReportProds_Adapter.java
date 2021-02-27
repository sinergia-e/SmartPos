package intem.smartpos.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import intem.smartpos.R;
import intem.smartpos.constructors.ReportProds_Constructor;

/**
 * Created by soluc on 04/06/2018.
 */

public class ReportProds_Adapter extends BaseAdapter {


    public ReportProds_Adapter(Context context, List<ReportProds_Constructor> listProdsData){
        this.context = context;
        this.list = listProdsData;
    }

    Context context;
    List<ReportProds_Constructor> list;





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
    public View getView(int position, View convertView, ViewGroup parent) {

        View viewHolder = convertView;
        LayoutInflater vista = LayoutInflater.from(context);

        viewHolder = vista.inflate(R.layout.view_row_prodsdata_report,null);

        TextView Descrip = viewHolder.findViewById(R.id.TextView_ProdsDataReport_DescriptProd);
        TextView TotalSales = viewHolder.findViewById(R.id.TextView_ProdsData_TotalSales);
        TextView ExistProd = viewHolder.findViewById(R.id.TextView_ProdsData_Exist);

        String SalesCv = String.valueOf(list.get(position).getTotalSales());

        Descrip.setText(list.get(position).getDescripProd());
        TotalSales.setText("Ventas Totales: " + SalesCv);
        ExistProd.setText("Existencia: " + list.get(position).getExistencia());

        return viewHolder;
    }
}
