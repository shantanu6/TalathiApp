package com.example.talathiapp;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.talathiapp.numberconvertercore.ConversionResult;
import com.example.talathiapp.numberconvertercore.ConvertIND;
import com.skydoves.expandablelayout.ExpandableLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ViewDetails extends AppCompatActivity implements View.OnClickListener ,ConvertIND.CallbackConvertIND {
    ExpandableLayout expandableLayout, expandable2, expandable3, expandable4;
    Button updateIncome, updateFarm,btnVaccept,btnVreject,btnFupdate;
    EditText y1_1, incomedetails, sheti, gav, iname,edtFamnt;
    RecyclerView rationRecyclerView;
    ArrayList<ItemModel> itemList = new ArrayList<>();
    ConstraintLayout kutumb_r_child;
    LinearLayout ration_item;
    TextView viewPhoto, viewId, viewRation, viewIncome,txtWordAmnt;
    ProgressDialog progressDialog;
    static String  serviceId,appId;
    String TAG="Details";
    String wordAmnt;
    static JSONObject updateTotalJObj;
    static boolean check=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_details);
        getSupportActionBar().setTitle("Income Details");
        expandableLayout = findViewById(R.id.expandable1);
        expandable2 = findViewById(R.id.expandable2);
        expandable3 = findViewById(R.id.expandable3);
        expandable4 = findViewById(R.id.expandable4);
        updateFarm = findViewById(R.id.updateFarm);
        updateIncome = findViewById(R.id.updateIncome);
        incomedetails = findViewById(R.id.incomedetails);
        sheti = findViewById(R.id.sheti);
        gav = findViewById(R.id.gav);
        iname = findViewById(R.id.iname);
        rationRecyclerView = findViewById(R.id.rationRecyclerview);
        kutumb_r_child = findViewById(R.id.kutumb_r_child);
        ration_item = findViewById(R.id.ration_item);

        viewId = findViewById(R.id.viewId);
        viewPhoto = findViewById(R.id.viewPhoto);
        viewRation = findViewById(R.id.viewRation);
        viewIncome = findViewById(R.id.viewIncome);
        edtFamnt = findViewById(R.id.edtFamnt);
        txtWordAmnt = findViewById(R.id.txtWordAmnt);
        btnFupdate = findViewById(R.id.btnFupdate);
        btnVaccept = findViewById(R.id.btnVaccept);
        btnVreject = findViewById(R.id.btnVreject);

        // Check Is Valid Token Exists
        CheckToken ct=new CheckToken();
        ct.isExitToken(this);


        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Fetching Information");
        progressDialog.setMessage("Please Wait...");
        
        viewId.setOnClickListener(this);
        viewPhoto.setOnClickListener(this);
        viewRation.setOnClickListener(this);
        viewIncome.setOnClickListener(this);
        updateFarm.setOnClickListener(this);
        updateIncome.setOnClickListener(this);
        btnVaccept.setOnClickListener(this);
        btnVreject.setOnClickListener(this);
        btnFupdate.setOnClickListener(this);

        y1_1 = findViewById(R.id.y1_1);

        serviceId=getIntent().getStringExtra("SERVICEID").split(":")[1];
        appId=getIntent().getStringExtra("APPID");

        Log.d(TAG, "onCreate: "+serviceId+"  "+appId);
        
        expandableLayout.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (expandableLayout.isExpanded()) {
                    expandableLayout.collapse();
                } else {
                    expandableLayout.expand();
                }
            }
        });
        expandable2.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (expandable2.isExpanded()) {
                    expandable2.collapse();
                } else {
                    expandable2.expand();
                }
            }
        });
        expandable3.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (expandable3.isExpanded()) {
                    expandable3.collapse();
                } else {
                    expandable3.expand();
                }
            }
        });
        expandable4.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (expandable4.isExpanded()) {
                    expandable4.collapse();
                } else {
                    expandable4.expand();
                }
            }
        });

        try {
            getServiceDetails(serviceId);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        edtFamnt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                new ConvertIND(ViewDetails.this).convertNumberToWord(edtFamnt.getText().toString().trim());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }


    public void getServiceDetails(String serviceid) throws UnsupportedEncodingException {
        try {
            progressDialog.show();
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            String URL = getString(R.string.db_url);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    progressDialog.dismiss();
                    Log.d(TAG, "onResponse: " + response);
                    try {
                        JSONObject responseObj = new JSONObject(response);
                        if (responseObj.getInt("SUCCESS") == 1) {
                            JSONObject resultobj = responseObj.getJSONObject("RESULT");
                            JSONArray rationcardobj = responseObj.getJSONArray("RATIONCARD");

                            setDetails(resultobj, rationcardobj);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "" + error);

                }

            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {

                    Map<String, String> params = new HashMap<>();
                    params.put("SERVICE_ID", serviceid);
                    params.put("RESULT_TYPE", "GET_SERVICE_DETAILS");
                    return params;
                }
            };
            requestQueue.add(stringRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void setDetails(JSONObject result, JSONArray rationcard) {
        try {

            Log.d(TAG, "setDetails: "+result.getString("i1"));

            //expandable view 1
            for (int i = 1; i < 11; i++) {
                int id = getResources().getIdentifier("y1" + "_" + i, "id", getPackageName());
                Log.d(TAG, "setDetails: " + id);
                EditText edt = findViewById(id);
                int val = result.getInt("y1" + "_" + i);
                edt.setText(String.valueOf(val));
                Log.d(TAG, "setDetails: " + val);
            }
            TextView total = findViewById(R.id.total);
            total.setText(result.getString("i1"));
            edtFamnt.setText(result.getString("i1"));

            // set word amnount
            new ConvertIND(ViewDetails.this).convertNumberToWord(edtFamnt.getText().toString().trim());


            //expandaded view 2
            incomedetails.setText(result.getString("incomedetails"));
            sheti.setText(result.getString("sheti"));
            gav.setText(result.getString("gav"));
            iname.setText(result.getString("iname"));

            //expandable view 3

            for (int i = 0; i < rationcard.length(); i++) {
                ItemModel itemModel = new ItemModel();
                JSONObject jsonObject = new JSONObject(rationcard.getString(i));

                itemModel.setName(jsonObject.getString("rname"));
                itemModel.setAge(jsonObject.getString("age"));
                itemModel.setRelation(jsonObject.getString("relation"));
                itemModel.setBusiness(jsonObject.getString("business"));
                itemModel.setSrno(jsonObject.getString("sn"));
                itemList.add(itemModel);
            }

        /*   int viewHeight = ration_item.getHeight() * rationcard.length();
            kutumb_r_child.getLayoutParams().height = viewHeight;
         */
            RecyclerAdapter recyclerAdapter = new RecyclerAdapter(this, itemList);
            rationRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            rationRecyclerView.setAdapter(recyclerAdapter);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.updateFarm:
                if (!isSpecialChara(iname.getText().toString()) && !isSpecialChara(gav.getText().toString()))
                    updateFarm();
                else
                    Toast.makeText(this, "Error: Enter Valid Input", Toast.LENGTH_SHORT).show();
                break;

            case R.id.updateIncome:
                updateIncome("Multiple");
                break;

            case R.id.btnFupdate:
                updateIncome("Single");
                break;

            case R.id.viewId:
                startActivity(new Intent(ViewDetails.this,WebViewAct.class).putExtra("url","http://talathi.tech/admin/uploads/"+appId+"_pic.jpg"));
                break;
            case R.id.viewIncome:
                startActivity(new Intent(ViewDetails.this,WebViewAct.class).putExtra("url","http://talathi.tech/admin/uploads/"+appId+"_incomeproofpic.jpg"));
                break;
            case R.id.viewPhoto:
                startActivity(new Intent(ViewDetails.this,WebViewAct.class).putExtra("url","http://talathi.tech/admin/uploads/"+appId+"_pic.jpg"));
                break;
            case R.id.viewRation:
                startActivity(new Intent(ViewDetails.this,WebViewAct.class).putExtra("url","http://talathi.tech/admin/uploads/"+appId+"_rationcardpic.jpg"));
                break;
            case R.id.btnVaccept:
                set_params(ViewDetails.this,GavDashboard.class,"1","Application Accepted Succssfull");
                break;
            case R.id.btnVreject:
                set_params(ViewDetails.this, GavDashboard.class,"2", "Application Rejected Succssfull");
                break;
        }
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

    void set_params(Context context, Class destination, String status, String message){
        JSONObject jsonObject= new JSONObject();
        try {
            jsonObject.put("status",status);
            String whereclause="WHERE serviceid="+serviceId;

            update_status(context,destination,"UPDATE_DETAILS",jsonObject,"servicerequest", whereclause,message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    void update_status(Context context,Class destination,String REQUEST_TYPE, JSONObject DATA, String TABLE, String WHERE_CLAUSE,String MESSAGE){

        try {
            progressDialog= new ProgressDialog(context);
            progressDialog.show();
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            String URL =getString(R.string.db_url);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    progressDialog.dismiss();
                    Log.d(TAG, "onResponse: " + response);
                    try {
                        Log.d(TAG, "onResponse: " + response);
                        JSONObject jsonObject = new JSONObject(response);

                        if (jsonObject.getInt("success")==1){
                            Toast.makeText(context, ""+MESSAGE, Toast.LENGTH_SHORT).show();
                            Intent i1=new Intent(context,destination);
                            i1.putExtra("GCODE",GavDashboard.GCODE);
                            i1.putExtra("GNAME",GavDashboard.GNAME);
                            i1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            context.startActivity(i1);
                            finish();
                        }else{
                            Toast.makeText(context, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(context, "Something Went Wrong", Toast.LENGTH_SHORT).show();

                    }
                }}, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "" + error);

                }

            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("DATA",DATA.toString());
                    params.put("TABLE", TABLE);
                    params.put("WHERE_CLAUSE",WHERE_CLAUSE);
                    params.put("RESULT_TYPE", REQUEST_TYPE);
                    return params;
                }
            };
            requestQueue.add(stringRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void updateIncome(String boxes) {
        updateTotalJObj= new JSONObject();
        try {
            if(boxes.equals("Multiple")){
                int sum=0;
                for (int i = 1; i < 11; i++) {
                    int id = getResources().getIdentifier("y1" + "_" + i, "id", getPackageName());
                    EditText edt = findViewById(id);
                    String val = edt.getText().toString();
                    sum=sum+Integer.parseInt(val);
                    Log.d(TAG, "setDetails: " + Integer.parseInt(edt.getText().toString()));
                    updateTotalJObj.put("y1"+"_"+i,val);
                }
                // set word amnount
                check=true;
                new ConvertIND(ViewDetails.this).convertNumberToWord(String.valueOf(sum).trim());

                TextView total = findViewById(R.id.total);
                total.setText(String.valueOf(sum));
                edtFamnt.setText(String.valueOf(sum));
                updateTotalJObj.put("i1",String.valueOf(sum));

            }else if(boxes.equals("Single")){
                updateTotalJObj.put("i1",edtFamnt.getText().toString());
                updateTotalJObj.put("ai1",txtWordAmnt.getText().toString());
                setServiceDetails(this,"UPDATE_DETAILS",updateTotalJObj,"incomedetails","WHERE serviceid='"+serviceId+"'","Update Successful");
            }



        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void updateFarm() {
        JSONObject jsonObject= new JSONObject();
        try {

            jsonObject.put("iname",iname.getText().toString());
            jsonObject.put("gav",gav.getText().toString());
            jsonObject.put("sheti",sheti.getText().toString());
            jsonObject.put("incomedetails",incomedetails.getText().toString());
            setServiceDetails(this,"UPDATE_DETAILS",jsonObject,"sincome_byuser","WHERE serviceid='"+serviceId+"'","Update Successful");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateRation(Context context, String sn,String rname,String relation,String age, String business ) {
        JSONObject jsonObject= new JSONObject();
        try {

            jsonObject.put("rname",rname);
            jsonObject.put("relation",relation);
            jsonObject.put("age",age);
            jsonObject.put("business",business);
            String whereclause="WHERE sn='"+sn+"' AND serviceid='"+serviceId+"'";
            setServiceDetails(context,"UPDATE_DETAILS",jsonObject,"rationcardinfo_byuser", whereclause,"Update Successful");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setServiceDetails(Context context,String REQUEST_TYPE, JSONObject DATA, String TABLE, String WHERE_CLAUSE,String MESSAGE) throws UnsupportedEncodingException {
        try {
            progressDialog= new ProgressDialog(context);
            progressDialog.show();
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            String URL = context.getString(R.string.db_url);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    progressDialog.dismiss();
                    Log.d(TAG, "onResponse: " + response);
                    try {
                        Log.d(TAG, "onResponse: " + response);
                        JSONObject jsonObject = new JSONObject(response);

                        if (jsonObject.getInt("success")==1){
                            Toast.makeText(context, ""+MESSAGE, Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(context, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(context, "Something Went Wrong", Toast.LENGTH_SHORT).show();

                    }
                }}, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "" + error);

                }

            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("DATA",DATA.toString());
                    params.put("TABLE", TABLE);
                    params.put("WHERE_CLAUSE",WHERE_CLAUSE);
                    params.put("RESULT_TYPE", REQUEST_TYPE);
                    return params;
                }
            };
            requestQueue.add(stringRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public void conversionResultIND(ConversionResult conversionResult) {
        Log.d("TEXTCONVERSION", " "+conversionResult.getResult().get(0));
        wordAmnt = conversionResult.getResult().get(0);
        txtWordAmnt.setText(conversionResult.getResult().get(0));
        if(txtWordAmnt.getText().toString().equals("No number to convert.")){
            btnFupdate.setEnabled(false);
            Toast.makeText(this, "Please Enter Any Number ", Toast.LENGTH_SHORT).show();
        }else {
            btnFupdate.setEnabled(true);
            if(check){
                check=false;
                try {
                    updateTotalJObj.put("ai1",txtWordAmnt.getText().toString());
                    setServiceDetails(this,"UPDATE_DETAILS",updateTotalJObj,"incomedetails","WHERE serviceid='"+serviceId+"'","Update Successful");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}