package com.example.talathiapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.talathiapp.ItemModel;
import com.example.talathiapp.R;
import com.example.talathiapp.ViewDetails;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<ItemModelHolder> {
Context context;
ArrayList<ItemModel> itemModelList;

    RecyclerAdapter(Context context, ArrayList<ItemModel> itemModelList){
        this.context=context;
        this.itemModelList=itemModelList;
    }
    @NonNull
    @Override
    public ItemModelHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(context).inflate(R.layout.ration_item, parent, false);
        return new ItemModelHolder(view);


    }

    @Override
    public void onBindViewHolder(@NonNull ItemModelHolder holder, int position) {
        holder.age.setText(itemModelList.get(position).getAge());
        holder.name.setText(itemModelList.get(position).getName());
        holder.relation.setText(itemModelList.get(position).getRelation());
        holder.business.setText(itemModelList.get(position).getBusiness());
        holder.update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( ! new ViewDetails().isSpecialChara(holder.name.getText().toString()) && ! new ViewDetails().isSpecialChara(holder.business.getText().toString()) )
                    new ViewDetails().updateRation(context,itemModelList.get(holder.getAbsoluteAdapterPosition()).getSrno(),holder.name.getText().toString(),holder.relation.getText().toString(),holder.age.getText().toString(),holder.business.getText().toString());
                else
                    Toast.makeText(context, "Error: Enter Valid Input", Toast.LENGTH_SHORT).show();  }
        });


    }

    @Override
    public int getItemCount() {
        return itemModelList.size();
    }
}

class  ItemModelHolder extends RecyclerView.ViewHolder{
EditText name,relation,age,business;
Button update;

    public ItemModelHolder(@NonNull View itemView) {
        super(itemView);
        name=itemView.findViewById(R.id.name);
        relation=itemView.findViewById(R.id.relation);
        age=itemView.findViewById(R.id.age);
        business=itemView.findViewById(R.id.business);
        update=itemView.findViewById(R.id.updateRation);
    }

}
