package com.example.talathiapp;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONObject;

import java.util.ArrayList;

public class InboxRecyclerView extends  RecyclerView.Adapter<InboxItemModel> {

    Context context;
    ArrayList<InboxItem> itemModel;
    RecyclerView inboxRecylerView;

    public InboxRecyclerView(Context context, ArrayList<InboxItem> itemModel, RecyclerView inboxRecylerView) {
        this.context=context;
        this.itemModel=itemModel;
        this.inboxRecylerView=inboxRecylerView;

    }

    @NonNull
    @Override
    public InboxItemModel onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.inbox_item, parent, false);
        return new InboxItemModel(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InboxItemModel holder, @SuppressLint("RecyclerView") int position) {

        holder.txtInGid.setText(itemModel.get(position).getInGavID());
        holder.txtInDate.setText(itemModel.get(position).getInDate());
        holder.txtInCertName.setText(itemModel.get(position).getInCertName());
        holder.txtInUname.setText(itemModel.get(position).getInUname());
        holder.txtInAppId.setText(itemModel.get(position).getInAppId());
        holder.txtInGcode.setText(itemModel.get(position).getInGcode());
        holder.txtIncome.setText(itemModel.get(position).getIncome());
        holder.txtInServiceNo.setText(itemModel.get(position).getInServiceNo());

        holder.inboxesCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i1=new Intent(context,ViewDetails.class);
                i1.putExtra("SERVICEID",itemModel.get(position).getInServiceNo());
                i1.putExtra("APPID",itemModel.get(position).getInAppId());
                context.startActivity(i1);
            }
        });
        holder.btnInAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateStatus( position,context,"1",itemModel.get(position).getInServiceNo().split(":")[1] ,"Application Accepted Successfully " );
            }
        });

        holder.btnInReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateStatus( position,context,"2",itemModel.get(position).getInServiceNo().split(":")[1] ,"Application Rejected Successfully " );
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemModel.size();
    }

    public void updateStatus(int position, Context context, String status, String serviceId, String message) {
        JSONObject jsonObject= new JSONObject();
        try {
            jsonObject.put("status",status);
            String whereclause=" WHERE serviceid='"+serviceId+"'";

            itemModel.remove(position);
            notifyDataSetChanged();

            new ViewDetails().setServiceDetails(context,"UPDATE_DETAILS",jsonObject,"servicerequest", whereclause,message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

 class InboxItemModel extends RecyclerView.ViewHolder {

    TextView txtInGid,txtInDate,txtInCertName,txtInUname,txtInGcode,txtInAppId,txtInServiceNo,txtIncome;
    Button btnInAccept,btnInReject;
    CardView inboxesCard;

    public InboxItemModel(@NonNull View itemView) {
        super(itemView);
        inboxesCard=itemView.findViewById(R.id.inboxesCard);
        txtInGid=itemView.findViewById(R.id.txtInGid);
        txtInDate=itemView.findViewById(R.id.txtInDate);
        txtInCertName=itemView.findViewById(R.id.txtInCertName);
        txtInUname=itemView.findViewById(R.id.txtInUname);
        txtInGcode=itemView.findViewById(R.id.txtInGcode);
        txtInAppId=itemView.findViewById(R.id.txtInAppId);
        txtInServiceNo=itemView.findViewById(R.id.txtInServiceNo);
        txtIncome=itemView.findViewById(R.id.txtIncome);
        btnInAccept=itemView.findViewById(R.id.btnInAccept);
        btnInReject=itemView.findViewById(R.id.btnInReject);
    }
}
