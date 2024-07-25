package com.example.retrofitjava.adaptor;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.retrofitjava.R;
import com.example.retrofitjava.model.CryptoModel;
import com.example.retrofitjava.view.DetailActivity;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdaptor extends RecyclerView.Adapter<RecyclerViewAdaptor.RowHolder> {

    private Context context;
    public List<CryptoModel.PositionData> cryptoList;

    public RecyclerViewAdaptor(Context context, List<CryptoModel.PositionData> cryptoList) {
        this.context = context;
        this.cryptoList = cryptoList;
    }

    public void setData(List<CryptoModel.PositionData> cryptoList) {
        this.cryptoList = cryptoList;
        notifyDataSetChanged();

    }

    @NonNull
    @Override
    public RowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout, parent, false);
        return new RowHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RowHolder holder, int position) {
        CryptoModel.PositionData positionData = cryptoList.get(position);
        holder.bind(positionData);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("positionData", (Parcelable) positionData);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cryptoList.size();
    }

    public class RowHolder extends RecyclerView.ViewHolder {

        private TextView inst_id;
        private TextView last;

        public RowHolder(@NonNull View itemView) {
            super(itemView);
            inst_id = itemView.findViewById(R.id.inst_id);
            last = itemView.findViewById(R.id.last);
        }

        public void bind(CryptoModel.PositionData positionData) {
            inst_id.setText(positionData.getInstrumentId());
            last.setText(positionData.getLastPrice());
        }
    }
}
