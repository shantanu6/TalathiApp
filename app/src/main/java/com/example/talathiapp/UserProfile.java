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
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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

public class UserProfile extends AppCompatActivity implements View.OnClickListener {

    static String UID ;
    ConstraintLayout UserProfileActivity;
    TextView uMobile;
    EditText uFname,uMname,uLname,uDob,uAddress;
    Button btnUpdatePass,btnSaveInfo;
    Spinner uGender;
    ArrayAdapter<String> genderAdapter ;
    ArrayList<String> genderList;
    ProgressDialog progressDialog;
    String TAG="Profile";


    final Calendar myCalendar= Calendar.getInstance();
    DatePickerDialog.OnDateSetListener date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        UserProfileActivity=findViewById(R.id.UserProfileActivity);
        uFname=findViewById(R.id.uFname);
        uMname=findViewById(R.id.uMname);
        uLname=findViewById(R.id.uLname);
        uDob=findViewById(R.id.uDob);
        uGender=findViewById(R.id.uGender);
        uAddress=findViewById(R.id.uAddress);
        uMobile=findViewById(R.id.uMobile); 
        btnSaveInfo=findViewById(R.id.btnSaveInfo);
        btnUpdatePass=findViewById(R.id.btnUpdatePass);

        // Check Is Valid Token Exists
        CheckToken ct=new CheckToken();
        ct.isExitToken(this);


        UID=getSharedPreferences("USER_DETAILS",MODE_PRIVATE).getString("UID","");

        genderList=new ArrayList<>();
        genderList.add("Male");
        genderList.add("Female");
        genderList.add("Other");
        setSpinnerItem(genderAdapter,genderList,uGender);

        progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Fetching Data");
        progressDialog.setMessage("Please Wait");

        btnSaveInfo.setOnClickListener(this);
        btnUpdatePass.setOnClickListener(this);

        uDob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDatepicker(uDob);
                new DatePickerDialog(UserProfile.this,date,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        try {
            String whereclause = " WHERE id="+UID;
            getRecords(UserProfile.this, "GET_RECORDS",  "users",whereclause,"SET_PROFILE");
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    void setSpinnerItem(ArrayAdapter<String> adapter,  ArrayList<String> arraylist, Spinner spnItem){
        adapter =new ArrayAdapter<String>(UserProfile.this,R.layout.spin_list, arraylist);
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


    void getRecords(Context context, String REQUEST_TYPE, String TABLE, String WHERE_CLAUSE, String RESULT_TYPE){
        progressDialog.show();
        RequestQueue req = Volley.newRequestQueue(context);
        String url =getString(R.string.db_url);

        StringRequest stringRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
//                Log.d(TAG, "onResponse: "+response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.getString("success").equals("1")){
                        JSONArray result = new JSONArray(jsonObject.getString("result"));
                        Log.d(TAG, "onResponse: "+result);
                        setRecords(result,RESULT_TYPE);

                        //   Snackbar.make(UserProfileActivity,result.getJSONObject.get("message"), BaseTransientBottomBar.LENGTH_SHORT).setBackgroundTint(getResources().getColor(R.color.green)).show();
                    }else {
                        Snackbar.make(UserProfileActivity, jsonObject.getString("message"), BaseTransientBottomBar.LENGTH_SHORT).setBackgroundTint(getResources().getColor(R.color.red)).show();
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
                params.put("WHERE_CLAUSE",WHERE_CLAUSE);
                return params;
            }
        };

        req.add(stringRequest);

    }



    void setRecords(JSONArray result , String RESULT_TYPE){
        try {

            switch (RESULT_TYPE){
                case "SET_PROFILE":
                    uFname.setText(result.getJSONObject(0).getString("fname"));
                    uMname.setText(result.getJSONObject(0).getString("mname"));
                    uLname.setText(result.getJSONObject(0).getString("lname"));
                    uDob.setText(new SimpleDateFormat("dd-MM-yyyy").format(new SimpleDateFormat("yyyy-MM-dd").parse(result.getJSONObject(0).getString("bdate"))));
                    uAddress.setText(result.getJSONObject(0).getString("address"));
                    uMobile.setText(result.getJSONObject(0).getString("username"));
                    uGender.setSelection(genderAdapter.getPosition(result.getJSONObject(0).getString("gender")));
                    break;
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnSaveInfo:
                hideKeyboard(this);
                saveInfo();
                break;
            case R.id.btnUpdatePass:
                hideKeyboard(this);
                Intent i1=new Intent(this, UpdatePasswordToken.class);
                i1.putExtra("RESULT","PASSWORD");
                startActivity(i1);
                break;
        }
    }
    
    public void saveInfo(){
        if(!uFname.getText().toString().equals("") && !uMname.getText().toString().equals("") && !uLname.getText().toString().equals("") &&
                !uDob.getText().toString().equals("") && !uAddress.getText().toString().equals("")   ){
            JSONObject jsonObject= new JSONObject();
            try {

                jsonObject.put("fname",uFname.getText().toString());
                jsonObject.put("mname",uMname.getText().toString());
                jsonObject.put("lname",uLname.getText().toString());
                jsonObject.put("bdate",new SimpleDateFormat("yyyy-MM-dd").format(new SimpleDateFormat("dd-MM-yyyy").parse(uDob.getText().toString())));
                jsonObject.put("gender",uGender.getSelectedItem().toString());
                jsonObject.put("address",uAddress.getText().toString());
                new ViewDetails().setServiceDetails(this,"UPDATE_DETAILS",jsonObject,"users","WHERE  id='"+UID+"'","Update Successful");

            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            Toast.makeText(this, "Enter All Fields", Toast.LENGTH_SHORT).show();
        }
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