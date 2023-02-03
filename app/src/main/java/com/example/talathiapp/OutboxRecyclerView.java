package com.example.talathiapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class OutboxRecyclerView extends  RecyclerView.Adapter<OutboxItemModel> {

    Context context;
    ArrayList<OutboxItem> itemModel;
    RecyclerView outboxRecylerView;

    public OutboxRecyclerView(Context context, ArrayList<OutboxItem> itemModel, RecyclerView outboxRecylerView) {
        this.context=context;
        this.itemModel=itemModel;
        this.outboxRecylerView=outboxRecylerView;
    }

    @NonNull
    @Override
    public OutboxItemModel onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.outbox_item, parent, false);
        return new OutboxItemModel(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OutboxItemModel holder, @SuppressLint("RecyclerView") int position) {
        holder.txtOutGid.setText(itemModel.get(position).getOutGavID());
        holder.txtOutDate.setText(itemModel.get(position).getOutDate());
        holder.txtOutCertName.setText(itemModel.get(position).getOutCertName());
        holder.txtOutUname.setText(itemModel.get(position).getOutUname());
        holder.txtOutMobile.setText(itemModel.get(position).getOutMobile());
        holder.txtOutAppId.setText(itemModel.get(position).getOutAppId());
        holder.txtOutGcode.setText(itemModel.get(position).getOutGcode());
        holder.txtOutServiceNo.setText(itemModel.get(position).getOutServiceNo());
        holder.txtOutStatus.setText(itemModel.get(position).getOutStatus());

        if(itemModel.get(position).getOutStatus().equals("Accepted")){
            holder.btnOutDownload.setVisibility(View.VISIBLE);
            holder.txtOutStatus.setTextColor(context.getResources().getColor(R.color.green));
        }
        else  if(itemModel.get(position).getOutStatus().equals("Rejected")){
            holder.btnOutDownload.setVisibility(View.GONE);
            holder.txtOutStatus.setTextColor(context.getResources().getColor(R.color.red));
        }
        else{
            holder.btnOutDownload.setVisibility(View.GONE);
            holder.txtOutStatus.setTextColor(context.getResources().getColor(R.color.black));
        }

        holder.outboxesCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        holder.btnOutDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Create PDF", Toast.LENGTH_SHORT).show();
                Intent i1=new Intent(context,LoadPdf.class);
                i1.putExtra("URL","https://coachingmanagement.in/talathi/cert_view.php?SERVICEID="+holder.txtOutServiceNo.getText().toString().split(":")[1]);
                i1.putExtra("FILENAME",itemModel.get(position).getOutAppId());
                context.startActivity(i1);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemModel.size();
    }
}

class OutboxItemModel extends RecyclerView.ViewHolder{

    TextView txtOutGid,txtOutDate,txtOutCertName,txtOutUname,txtOutGcode,txtOutAppId,txtOutServiceNo,txtOutStatus,txtOutMobile;
    Button btnOutDownload;
    CardView outboxesCard;

    public OutboxItemModel(@NonNull View itemView) {
        super(itemView);
        outboxesCard=itemView.findViewById(R.id.outboxesCard);
        txtOutGid=itemView.findViewById(R.id.txtOutGid);
        txtOutDate=itemView.findViewById(R.id.txtOutDate);
        txtOutCertName=itemView.findViewById(R.id.txtOutCertName);
        txtOutUname=itemView.findViewById(R.id.txtOutUname);
        txtOutMobile=itemView.findViewById(R.id.txtOutMobile);
        txtOutGcode=itemView.findViewById(R.id.txtOutGcode);
        txtOutAppId=itemView.findViewById(R.id.txtOutAppId);
        txtOutServiceNo=itemView.findViewById(R.id.txtOutServiceNo);
        btnOutDownload=itemView.findViewById(R.id.btnOutDownload);
        txtOutStatus=itemView.findViewById(R.id.txtOutStatus);
    }
}
