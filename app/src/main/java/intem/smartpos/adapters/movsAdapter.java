package intem.smartpos.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import intem.smartpos.R;
import intem.smartpos.constructors.movsConstructor;

public class movsAdapter extends BaseAdapter {

    Context context;
    List<movsConstructor> list;

    public movsAdapter(Context context, List<movsConstructor> list) {
        this.context = context;
        this.list = list;
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
    public View getView(int i, View movsInv, ViewGroup viewGroup) {
        View viewHolder = movsInv;

        LayoutInflater inflater = LayoutInflater.from(context);
        viewHolder = inflater.inflate(R.layout.view_row_movs,null);

        TextView Folio = viewHolder.findViewById(R.id.textView_MovsRow_Folio);
        TextView Date = viewHolder.findViewById(R.id.textView_MovsRow_Date);
        TextView Status = viewHolder.findViewById(R.id.textView_MovsRow_Status);

        String FolioMov = "";

        if(list.get(i).getIdInServer()>0){
            FolioMov = ", con numero de inventario : " + String.valueOf(list.get(i).getIdInServer());
        }

        Folio.setText("Folio del movimiento:" + list.get(i).getId());
        Date.setText("Fecha: " + list.get(i).getDateMov());
        Status.setText(list.get(i).getTypeMov() + FolioMov );

        return viewHolder;
    }
}
