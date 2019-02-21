package com.example.user.catatanku.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.example.user.catatanku.Detail_Sampah;
import com.example.user.catatanku.Model.ModelSampah;
import com.example.user.catatanku.R;

import java.util.ArrayList;
import java.util.List;

public class AdapterSampah extends RecyclerView.Adapter<AdapterSampah.MyHolder>
        implements Filterable {

    Context context;
    private List<ModelSampah> modelSampahList;
    private List<ModelSampah> modelSampahFilter;
    LayoutInflater inflater;
    ModelSampah modelSampah;

    public AdapterSampah(Context context, List<ModelSampah> modelSampahList) {
        this.context = context;
        this.modelSampahList = modelSampahList;
        this.modelSampahFilter = modelSampahList;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.model_list_catatan,parent,false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, final int position) {
        modelSampah = modelSampahFilter.get(position);
        holder.txt_judul.setText(modelSampah.getJudul_sampah());
        holder.txt_isi_catatan.setText(modelSampah.getIsi_sampah());
        holder.txt_tanggal.setText(modelSampah.getTgl_sampah());

        if (modelSampah.getIsi_sampah().length() < 25){
            holder.txt_judul.setTextSize(28);
            holder.txt_isi_catatan.setTextSize(24);
        }else if (modelSampah.getIsi_sampah().length() > 80){
            String subStr = modelSampah.getIsi_sampah().substring(0,45);
            holder.txt_isi_catatan.setText(subStr+"....");
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modelSampah = modelSampahFilter.get(position);
                Intent intentSampah = new Intent(context,Detail_Sampah.class);
                intentSampah.putExtra("id",modelSampah.getId_sampah());
                context.startActivity(intentSampah);
            }
        });
    }

    @Override
    public int getItemCount() {
        return modelSampahFilter.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String myString = constraint.toString();

                if (myString.isEmpty()){
                    modelSampahFilter = modelSampahList;
                }else{
                    List<ModelSampah> filterSampahList = new ArrayList<>();
                    for (ModelSampah modelSampah : modelSampahList){
                        if (modelSampah.getJudul_sampah().toLowerCase().contains(myString) ||
                                modelSampah.getIsi_sampah().toLowerCase().contains(myString)){
                            filterSampahList.add(modelSampah);
                        }
                    }
                    modelSampahFilter = filterSampahList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.count = modelSampahFilter.size();
                filterResults.values = modelSampahFilter;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                modelSampahFilter = (ArrayList<ModelSampah>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    class MyHolder extends RecyclerView.ViewHolder {

        TextView txt_judul,txt_tanggal,txt_isi_catatan;

        public MyHolder(View itemView) {
            super(itemView);
            txt_judul = itemView.findViewById(R.id.model_txt_judul);
            txt_tanggal = itemView.findViewById(R.id.model_tgl_catatan);
            txt_isi_catatan = itemView.findViewById(R.id.model_isi_catatan);
        }
    }
}
