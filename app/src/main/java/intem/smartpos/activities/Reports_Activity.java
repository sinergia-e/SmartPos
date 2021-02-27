package intem.smartpos.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.Calendar;

import intem.smartpos.R;
import intem.smartpos.Reports.ProdsReport_Activity;

public class Reports_Activity extends AppCompatActivity implements View.OnClickListener {

    private EditText InitialDate,FinalDate;
    private ImageButton ImButton_ShowInitial,ImButton_ShowFinal;
    private Button Button_ProdSales_Report;
    private int dd,mm,yy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        InitialDate = (EditText) findViewById(R.id.EditText_ProdsReports_InitialDate);
        FinalDate = (EditText) findViewById(R.id.EditText_ProdsReports_FinalDate);
        ImButton_ShowInitial = (ImageButton) findViewById(R.id.ImageButton_ProdsReports_InitialDate);
        ImButton_ShowFinal = (ImageButton) findViewById(R.id.ImageButton_ProdsReports_FinalDate);
        Button_ProdSales_Report = (Button) findViewById(R.id.Button_Reports_TopSales);

        ImButton_ShowInitial.setOnClickListener(this);
        ImButton_ShowFinal.setOnClickListener(this);
        Button_ProdSales_Report.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v==ImButton_ShowInitial){
            final Calendar c = Calendar.getInstance();
            dd = c.get(Calendar.DAY_OF_MONTH);
            mm = c.get(Calendar.MONTH);
            yy = c.get(Calendar.YEAR);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener(){
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    InitialDate.setText(dayOfMonth + "/" + month + "/" + year);
                }
            },yy,mm,dd);
            datePickerDialog.show();
        }else if (v==ImButton_ShowFinal){
            final Calendar c = Calendar.getInstance();
            dd = c.get(Calendar.DAY_OF_MONTH);
            mm = c.get(Calendar.MONTH);
            yy = c.get(Calendar.YEAR);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener(){
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    FinalDate.setText(dayOfMonth + "/" + month + "/" + year);
                }
            },yy,mm,dd);
            datePickerDialog.show();
        }else if(v == Button_ProdSales_Report){
            if(InitialDate.getText().toString().equals("")){
                Toast.makeText(this, "La fecha inicial es requerida", Toast.LENGTH_SHORT).show();
            }

            if(FinalDate.getText().toString().equals("")){
                Toast.makeText(this, "La fecha Final es necesaria", Toast.LENGTH_SHORT).show();
            }

            if(FinalDate.getText().toString().equals("") && InitialDate.getText().toString().equals("")){

            }else{
                Intent GoProdsReport = new Intent(Reports_Activity.this, ProdsReport_Activity.class);
                GoProdsReport.putExtra("InitDate",InitialDate.getText().toString());
                GoProdsReport.putExtra("FinalDate",FinalDate.getText().toString());
                GoProdsReport.putExtra("Condicion","");
                GoProdsReport.putExtra("DescripCondicion","Mostrando Todos los resultados");
                startActivity(GoProdsReport);
            }

        }
    }












    public void onBackPressed() {
        Intent GoMenu = new Intent(Reports_Activity.this, MenuActivity.class);
        startActivity(GoMenu);
    }








}
