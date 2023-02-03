package com.example.talathiapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class DashboardRecyclerView extends  RecyclerView.Adapter<DashboardItemModel> {

    Context context;
    ArrayList<GavItemModel> itemModel;
    RecyclerView dashboardRecyclerView;

    public DashboardRecyclerView(Context context, ArrayList<GavItemModel> itemModel, RecyclerView dashboardRecyclerView) {
        this.context=context;
        this.itemModel=itemModel;
        this.dashboardRecyclerView=dashboardRecyclerView;
    }

    @NonNull
    @Override
    public DashboardItemModel onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.gavitem, parent, false);
        return new DashboardItemModel(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DashboardItemModel holder, @SuppressLint("RecyclerView") int position) {

        holder.txtGav.setText(itemModel.get(position).getGavName());
        holder.txtGcode.setText(itemModel.get(position).getGavCode());
        holder.txtGavCount.setText(itemModel.get(position).getGavCount());
        holder.gavListCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i1=new Intent(context,GavDashboard.class);
                i1.putExtra("GCODE",itemModel.get(position).getGavCode());
                i1.putExtra("GNAME",itemModel.get(position).getGavName());
                context.startActivity(i1);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemModel.size();
    }
}

class DashboardItemModel extends RecyclerView.ViewHolder {

    CardView gavListCard;
    TextView txtGav,txtGcode,txtGavCount;

    public DashboardItemModel(@NonNull View itemView) {
        super(itemView);
        gavListCard=itemView.findViewById(R.id.gavListCard);
        txtGav=itemView.findViewById(R.id.txtGav);
        txtGcode=itemView.findViewById(R.id.txtGcode);
        txtGavCount=itemView.findViewById(R.id.txtGavCount);
    }
}
