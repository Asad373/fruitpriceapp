package com.android.fruitpriceapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.fruitpriceapp.Utils.DataUtils;
import com.android.fruitpriceapp.databinding.FuriteviewlayoutBinding;
import com.android.fruitpriceapp.model.MyModel;

import java.util.ArrayList;

public class FruitAdapter extends RecyclerView.Adapter<FruitAdapter.ViewHolder> {
    private final ArrayList<MyModel> furitearray;
    Context context;
    updateItem update;
    deleteItem delete;
    public FruitAdapter(Context context, ArrayList<MyModel> uploads) {
        this.furitearray = uploads;
        this.context = context;
    }

    @NonNull
    @Override
    public FruitAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        FuriteviewlayoutBinding binding = FuriteviewlayoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new FruitAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(FruitAdapter.ViewHolder holder, int position) {
        MyModel mitem = furitearray.get(position);
        holder.binding.nameF.setText(mitem.getName());
        holder.binding.desF.setText(mitem.getDes());
        holder.binding.PriceF.setText(mitem.getPrice() + " " + "PKR" );
        holder.binding.number.setText(String.valueOf(position + 1));

        if(DataUtils.Usertype.equals("Admin")){
            holder.binding.updateDelete.setVisibility(View.VISIBLE);
        }else{
            holder.binding.updateDelete.setVisibility(View.GONE);
        }
        holder.binding.update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                update.editClickListner(position);
            }
        });
        holder.binding.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                delete.deleteClickListner(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return furitearray.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public FuriteviewlayoutBinding binding;

        public ViewHolder(FuriteviewlayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }

    public void setUpdate(updateItem update) {
        this.update = update;
    }

    public interface updateItem{
        void editClickListner(int position);
    }

    public void setDelete(deleteItem delete) {
        this.delete = delete;
    }

    public interface deleteItem{
        void deleteClickListner(int position);
    }
}
