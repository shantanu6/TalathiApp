
package com.example.talathiapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Report extends AppCompatActivity {

    static String GCODE,GNAME;
    EditText edtFdate,edtTdate;
    Button btnViewReport;
    final Calendar myCalendar= Calendar.getInstance();
    DatePickerDialog.OnDateSetListener date;
    String TAG="Report";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        // Check Is Valid Token Exists
        CheckToken ct=new CheckToken();
        ct.isExitToken(this);

        GCODE=getIntent().getStringExtra("GCODE");
        GNAME=getIntent().getStringExtra("GNAME");
        getSupportActionBar().setTitle("Report - "+GNAME);

        edtFdate=findViewById(R.id.edtFdate);
        edtTdate=findViewById(R.id.edtTdate);
        btnViewReport=findViewById(R.id.btnViewReport);

        edtFdate.setOnClickListener(new View.OnClickListener() {
            @Override
              public void onClick(View view) {
                getDatepicker(edtFdate);
                edtTdate.setEnabled(true);
//                Toast.makeText(Report.this, ""+edtFdate.getText(), Toast.LENGTH_SHORT).show();
                new DatePickerDialog(Report.this,date,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        edtTdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDatepicker(edtTdate);
//                Toast.makeText(Report.this, ""+edtTdate.getText(), Toast.LENGTH_SHORT).show();
                new DatePickerDialog(Report.this,date,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        btnViewReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!edtFdate.getText().toString().equals("") && !edtTdate.getText().toString().equals("")){
                    Intent i1=new Intent(Report.this,LoadPdf.class);
                    i1.putExtra("URL","https://coachingmanagement.in/talathi/report.php?FDATE="+edtFdate.getText()+"&TDATE="+edtTdate.getText()+"&GCODE="+GCODE);
                    i1.putExtra("FILENAME",edtFdate.getText()+"_"+edtTdate.getText());
                    startActivity(i1);
                }else {
                    Toast.makeText(Report.this, "Please Select Both Dates", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    void getDatepicker(EditText edtdate){
        date =new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH,month);
                myCalendar.set(Calendar.DAY_OF_MONTH,day);
                updateLabel(edtdate);
            }
        };
    }

    private void updateLabel(EditText edtDate){
        String myFormat="dd-MM-yyyy";
        SimpleDateFormat dateFormat=new SimpleDateFormat(myFormat, Locale.getDefault());
        edtDate.setText(dateFormat.format(myCalendar.getTime()));
    }

}
