package com.example.talathiapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Register extends AppCompatActivity implements AdapterView.OnItemSelectedListener, TextWatcher, View.OnClickListener {

    ConstraintLayout regActivity;
    Spinner spnDivision,spnDistrict,spnTaluka,spnDesignation,spnGender;
    EditText regFname,regMname,regLname,regDob,regAddress,regMobile,regSevarthId,regPass;
    TextView txtFullName;
    Button regSubmit;
    ArrayAdapter<String> genderAdapter,designationAdapter,talukaAdapter,districtAdapter,divisionAdapter;
    ArrayList<String> genderList;
    JSONArray talukaList,districtList,divisionList;
    ProgressDialog progressDialog;

    String TAG="Reg";
    static  boolean isData;

    final Calendar myCalendar= Calendar.getInstance();
    DatePickerDialog.OnDateSetListener date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        regActivity=findViewById(R.id.regActivity);
        spnDivision=findViewById(R.id.spnDivision);
        spnDistrict=findViewById(R.id.spnDistrict);
        spnTaluka=findViewById(R.id.spnTaluka);
        spnDesignation=findViewById(R.id.spnDesignation);
        spnGender=findViewById(R.id.spnGender);
        regFname=findViewById(R.id.regFname);
        regMname=findViewById(R.id.regMname);
        regLname=findViewById(R.id.regLname);
        regDob=findViewById(R.id.regDob);
        regAddress=findViewById(R.id.regAddress);
        regMobile=findViewById(R.id.regMobile);
        regPass=findViewById(R.id.regPass);
        regSevarthId=findViewById(R.id.regSevarthId);
        txtFullName=findViewById(R.id.txtFullName);
        regSubmit=findViewById(R.id.regSubmit);

        divisionList= new JSONArray();
        talukaList= new JSONArray();

        spnDivision.setOnItemSelectedListener(this);
        spnDistrict.setOnItemSelectedListener(this);
        spnTaluka.setOnItemSelectedListener(this);

        regMobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                       if(editable.toString().length()==10){
                            hideKeyboard(Register.this);
                           if(!spnDistrict.getSelectedItem().toString().equals("Select District")){
                               Log.d(TAG, "onClick: "+districtList.getJSONObject(spnDistrict.getSelectedItemPosition()).getString("did"));

                               String whereclause = " WHERE mobileno="+"'"+regMobile.getText().toString().trim()+"'"+ " AND sevarth= " + "'"+regSevarthId.getText().toString().trim()+"'"+
                                    " AND district=" + "'"+districtList.getJSONObject(spnDistrict.getSelectedItemPosition()).getString("did").trim()+"' ";
                            Log.d(TAG, "afterTextChanged: "+whereclause);
                            getRecords(Register.this, "GET_RECORDS",  "user_verification",whereclause,"SET_SEVARTH_NAME");
                        }else {
                             Toast.makeText(Register.this, "Please Select District First", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        txtFullName.setText("");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        regSevarthId.addTextChangedListener(this);
        regSubmit.setOnClickListener(this);

        genderList=new ArrayList<>();
        genderList.add("Male");
        genderList.add("Female");
        genderList.add("Other");
       setSpinnerItem(genderAdapter,genderList,spnGender);

        ArrayList<String> designationarraylist = new ArrayList<>();
        designationarraylist.add("Talathi");
        setSpinnerItem(designationAdapter, designationarraylist, spnDesignation);

        ArrayList<String> emptyDistrictArrayList= new ArrayList<>();
        emptyDistrictArrayList.add("Select District");
        setSpinnerItem(districtAdapter,emptyDistrictArrayList,spnDistrict);

        ArrayList<String> emptytalukaArrayList= new ArrayList<>();
        emptytalukaArrayList.add("Select Taluka");
        setSpinnerItem(talukaAdapter,emptytalukaArrayList,spnTaluka);


        progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Fetching Data");
        progressDialog.setMessage("Please Wait");


        regDob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDatepicker(regDob);
                new DatePickerDialog(Register.this,date,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        try {
            String whereclause = " WHERE 1=1";
            getRecords(Register.this, "GET_RECORDS",  "division",whereclause,"SET_DIVISION");
        }catch (Exception e){
            e.printStackTrace();
        }


    }



    void getRecords(Context context, String REQUEST_TYPE, String TABLE, String WHERE_CLAUSE,String RESULT_TYPE){
        progressDialog.show();
        RequestQueue req = Volley.newRequestQueue(context);
        String url =getString(R.string.db_url);

        StringRequest stringRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                Log.d(TAG, "onResponse: "+response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.getString("success").equals("1")){
                        JSONArray result = new JSONArray(jsonObject.getString("result"));

                         setRecords(result,RESULT_TYPE);

                    //   Snackbar.make(regActivity,result.getJSONObject.get("message"), BaseTransientBottomBar.LENGTH_SHORT).setBackgroundTint(getResources().getColor(R.color.green)).show();
                    }else {
                        Snackbar.make(regActivity, jsonObject.getString("message"),BaseTransientBottomBar.LENGTH_SHORT).setBackgroundTint(getResources().getColor(R.color.red)).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onError: "+error.getMessage());
            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap params = new HashMap<String,String>();
//                params.put("DATA",DATA.toString());
                params.put("RESULT_TYPE", REQUEST_TYPE);
                params.put("TABLE", TABLE);
                params.put("WHERE_CLAUSE",WHERE_CLAUSE);
                return params;
            }
        };

        req.add(stringRequest);

    }


    void setRecords( JSONArray result , String RESULT_TYPE){
     try {

        switch (RESULT_TYPE){
            case "SET_DIVISION":
                ArrayList<String> divArrayList= new ArrayList<>();

                divArrayList.add("Select Division");
                divisionList.put(-1);
                for (int i=0;i<result.length();i++){
                    divArrayList.add( result.getJSONObject(i).getString("dvname"));
                    divisionList.put(result.getJSONObject(i));
                }
                setSpinnerItem(divisionAdapter,divArrayList,spnDivision);
                break;

            case "SET_DISTRICT":
                ArrayList<String> districtArrayList= new ArrayList<>();
                districtList= new JSONArray();

                districtArrayList.add("Select District");
                districtList.put(-1);
                for (int i=0;i<result.length();i++){
                    districtArrayList.add( result.getJSONObject(i).getString("dname"));
                    districtList.put(result.getJSONObject(i));
                }
                setSpinnerItem(districtAdapter,districtArrayList,spnDistrict);

                //Incase no Taluka Exists
                ArrayList<String> emptytalukaArrayList= new ArrayList<>();
                emptytalukaArrayList.add("Select Taluka");
                talukaList= new JSONArray();
                setSpinnerItem(talukaAdapter,emptytalukaArrayList,spnTaluka);

                break;


            case "SET_TALUKA":
                ArrayList<String> talukaArrayList= new ArrayList<>();
                talukaList= new JSONArray();
                talukaArrayList.add("Select Taluka");
                talukaList.put(-1);
                for (int i=0;i<result.length();i++){
                    talukaArrayList.add( result.getJSONObject(i).getString("tname"));
                    talukaList.put(result.getJSONObject(i));
                }
                setSpinnerItem(talukaAdapter,talukaArrayList,spnTaluka);
                break;

            case "SET_SEVARTH_NAME":
                String efullname=result.getJSONObject(0).getString("full_name");
                txtFullName.setText(efullname);
                break;
        }

     }catch (Exception e){
         e.printStackTrace();
     }
    }


    void setSpinnerItem(ArrayAdapter<String> adapter,  ArrayList<String> arraylist, Spinner spnItem){
        adapter =new ArrayAdapter<String>(Register.this,R.layout.spin_list, arraylist);
        spnItem.setAdapter(adapter);
        adapter.notifyDataSetChanged();
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


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
try {
    String whereclause;
    switch (adapterView.getId()){
            case R.id.spnDivision:
                ArrayList<String> emptyDistrictArrayList= new ArrayList<>();
                emptyDistrictArrayList.add("Select District");
                setSpinnerItem(districtAdapter,emptyDistrictArrayList,spnDistrict);

                ArrayList<String> emptytalukaArrayList= new ArrayList<>();
                emptytalukaArrayList.add("Select Taluka");
                setSpinnerItem(talukaAdapter,emptytalukaArrayList,spnTaluka);

                if (i!=0){
                JSONObject dvobj = new JSONObject(divisionList.get(i).toString());
                whereclause = " WHERE dvid="+dvobj.get("dvid");
                getRecords(Register.this, "GET_RECORDS",  "district",whereclause,"SET_DISTRICT");
                }
             break;

            case R.id.spnDistrict:
              /*  if (i!=0)
                spnDistrict.setEnabled(false);*/
                ArrayList<String> emptytalukaArrayList2= new ArrayList<>();
                emptytalukaArrayList2.add("Select Taluka");
                setSpinnerItem(talukaAdapter,emptytalukaArrayList2,spnTaluka);

                regSevarthId.setText("");
                txtFullName.setText("");

                if (i!=0) {
                JSONObject districtobj = new JSONObject(districtList.get(i).toString());
                whereclause = " WHERE did=" + districtobj.get("did");
                getRecords(Register.this, "GET_RECORDS", "taluka", whereclause, "SET_TALUKA");
                }
                break;

        }

    }catch (Exception e){
        e.printStackTrace();
    }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        try {
            if(editable.toString().length()==11){
                hideKeyboard(Register.this);
                if(!spnDistrict.getSelectedItem().toString().equals("Select District")){
//                    Log.d(TAG, "onClick: "+districtList.getJSONObject(spnDistrict.getSelectedItemPosition()).getString("did"));

                    String whereclause = " WHERE mobileno="+"'"+regMobile.getText().toString().trim()+"'"+ " AND sevarth= " + "'"+regSevarthId.getText().toString().trim()+"'"+
                            " AND district=" + "'"+districtList.getJSONObject(spnDistrict.getSelectedItemPosition()).getString("did").trim()+"' ";
                    Log.d(TAG, "afterTextChanged: "+whereclause);
                    getRecords(Register.this, "GET_RECORDS",  "user_verification",whereclause,"SET_SEVARTH_NAME");
                }else {
                    Toast.makeText(Register.this, "Please Select District First", Toast.LENGTH_SHORT).show();
                }
            }else{
                txtFullName.setText("");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.regSubmit:

                  if(!spnDivision.getSelectedItem().toString().equals("Select Division") && !spnDistrict.getSelectedItem().toString().equals("Select District") &&
                        !spnTaluka.getSelectedItem().toString().equals("Select Taluka") &&  !regFname.getText().toString().equals("") && !regMname.getText().toString().equals("") &&
                        !regLname.getText().toString().equals("") && !regDob.getText().toString().equals("") && !regAddress.getText().toString().equals("") && !regMobile.getText().toString().equals("") &&
                        !regPass.getText().toString().equals("") && !regSevarthId.getText().toString().equals("") && !txtFullName.getText().toString().equals("")){

                      if(!isSpecialChara(regFname.getText().toString() ) && !isSpecialChara(regMname.getText().toString() ) && !isSpecialChara(regLname.getText().toString() )){

                        progressDialog=new ProgressDialog(this);
                        progressDialog.setCancelable(false);
                        progressDialog.setTitle("Please Wait");
                        progressDialog.setMessage("Checking Info");
                        progressDialog.show();

                      String whereclause = " WHERE username="+"'"+regMobile.getText().toString().trim()+"' " + " AND sevarth=" + "'"+regSevarthId.getText().toString().trim()+"' " +
                              " AND dist=" + "'"+spnDistrict.getSelectedItem().toString().trim()+"' ";
                      isDataExits(Register.this, "GET_RECORDS",  "users",whereclause);

                      }else {
                          Toast.makeText(this, "Error: Please Enter Valid Input", Toast.LENGTH_SHORT).show();
                      }

                }else{
                    Toast.makeText(this, "Enter All Fields", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    void isDataExits(Context context, String REQUEST_TYPE, String TABLE, String WHERE_CLAUSE){

        RequestQueue req = Volley.newRequestQueue(context);
        String url =getString(R.string.db_url);

        StringRequest stringRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                Log.d(TAG, "onResponse: "+response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.getString("success").equals("1")){
                        JSONArray result = new JSONArray(jsonObject.getString("result"));
//                        Toast.makeText(context, "Data Exits", Toast.LENGTH_SHORT).show();
                        Snackbar.make(regActivity, "User Already Exit.",BaseTransientBottomBar.LENGTH_LONG).setBackgroundTint(getResources().getColor(R.color.red)).show();
                    }else {
                        submitForm();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onError: "+error.getMessage());
            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap params = new HashMap<String,String>();
//                params.put("DATA",DATA.toString());
                params.put("RESULT_TYPE", REQUEST_TYPE);
                params.put("TABLE", TABLE);
                params.put("WHERE_CLAUSE",WHERE_CLAUSE);
                return params;
            }
        };

        req.add(stringRequest);

    }

    void submitForm(){
        JSONObject jsonObject= new JSONObject();
        try {
//                jsonObject.put("id",3);
                jsonObject.put("regdate",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()));
                jsonObject.put("username",regMobile.getText().toString());
                jsonObject.put("password",regPass.getText().toString());
                jsonObject.put("fname",regFname.getText().toString());
                jsonObject.put("mname",regMname.getText().toString());
                jsonObject.put("lname",regLname.getText().toString());
                jsonObject.put("bdate",new SimpleDateFormat("yyyy-MM-dd").format(new SimpleDateFormat("dd-MM-yyyy").parse(regDob.getText().toString())));
                jsonObject.put("gender",spnGender.getSelectedItem().toString());
                jsonObject.put("phone",regMobile.getText().toString());
                jsonObject.put("address",regAddress.getText().toString());
                jsonObject.put("division",spnDivision.getSelectedItem().toString());
                jsonObject.put("dist",spnDistrict.getSelectedItem().toString());
                jsonObject.put("taluka",spnTaluka.getSelectedItem().toString());
                jsonObject.put("efullname",txtFullName.getText().toString());
                jsonObject.put("sevarth",regSevarthId.getText().toString());
                Log.d(TAG, "submitForm: "+jsonObject.toString());
//                Toast.makeText(this,  "Success", Toast.LENGTH_SHORT).show();

                insertRecords(this,jsonObject.toString(),"users","INSERT_RECORDS");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public void insertRecords(Context context, String DATA, String TABLE, String REQUEST_TYPE){
        progressDialog.show();
        RequestQueue req = Volley.newRequestQueue(context);
        String url =getString(R.string.db_url);

        StringRequest stringRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                Log.d(TAG, "onResponse: "+response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.getString("success").equals("1")){
                        startActivity(new Intent(context,MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        finish();
                        Toast.makeText(Register.this, "Register Success", Toast.LENGTH_SHORT).show();

                    }else {
                        Snackbar.make(regActivity, jsonObject.getString("message"),BaseTransientBottomBar.LENGTH_SHORT).setBackgroundTint(getResources().getColor(R.color.red)).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onError: "+error.getMessage());
            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap params = new HashMap<String,String>();
                params.put("RESULT_TYPE", REQUEST_TYPE);
                params.put("TABLE", TABLE);
                params.put("DATA",DATA);
                return params;
            }
        };

        req.add(stringRequest);
    }

    public  boolean isSpecialChara(String s){

        if (s == null || s.trim().isEmpty()) {
            return true;
        }
        Pattern p = Pattern.compile ("[0-9<>:,!@#$%&*()_+=|<>?{}\\[\\]~-]");
        Matcher m = p.matcher(s);
//             boolean b = m.matches();
        boolean b = m.find();
        if (b)
            return  true;
        else
            return false;

    }

    public  void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}