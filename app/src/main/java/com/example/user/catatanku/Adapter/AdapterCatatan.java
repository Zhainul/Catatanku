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

import com.example.user.catatanku.Detail_Catatan;
import com.example.user.catatanku.Model.ModelCatatan;
import com.example.user.catatanku.R;

import java.util.ArrayList;
import java.util.List;

public class AdapterCatatan extends RecyclerView.Adapter<AdapterCatatan.MyHolder> implements Filterable {

    Context context;
    private List<ModelCatatan> modelCatatanList;
    List<ModelCatatan> catatansFilter;
    ModelCatatan modelCatatan;
    LayoutInflater inflater;

    public AdapterCatatan(Context context, List<ModelCatatan> modelCatatanList) {
        this.context = context;
        this.modelCatatanList = modelCatatanList;
        this.catatansFilter = modelCatatanList;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.model_list_catatan, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, final int position) {
        modelCatatan = catatansFilter.get(position);
        holder.judul.setText(modelCatatan.getJudul());
        holder.tanggal.setText(modelCatatan.getTgl_simpan());
        holder.isi_catatan.setText(modelCatatan.getIsi());

        if (modelCatatan.getIsi().length() < 25){
            holder.judul.setTextSize(28);
            holder.isi_catatan.setTextSize(24);
        }else if(modelCatatan.getIsi().length() > 80){
            String subStr = modelCatatan.getIsi().substring(0,45);
            holder.isi_catatan.setText(subStr+"....");
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modelCatatan = catatansFilter.get(position);
                Intent detail = new Intent(context,Detail_Catatan.class);
                detail.putExtra("id_catatan",modelCatatan.getId_catatan());
                context.startActivity(detail);
            }
        });

    }

    @Override
    public int getItemCount() {
        return catatansFilter.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString = constraint.toString();

                if (charString.isEmpty()){
                    catatansFilter = modelCatatanList;
                }else{
                    List<ModelCatatan> filterList = new ArrayList<>();
                    for (ModelCatatan modelCatatanku : modelCatatanList){
                        if (modelCatatanku.getJudul().toLowerCase().contains(charString)
                                || modelCatatanku.getIsi().toLowerCase().contains(charString)){
                            filterList.add(modelCatatanku);
                        }
                    }
                    catatansFilter = filterList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.count = catatansFilter.size();
                filterResults.values = catatansFilter;
                return  filterResults;

            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                catatansFilter = (ArrayList<ModelCatatan>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    class MyHolder extends RecyclerView.ViewHolder {

        TextView judul,tanggal,isi_catatan;

        public MyHolder(View itemView) {
            super(itemView);

            judul = itemView.findViewById(R.id.model_txt_judul);
            tanggal = itemView.findViewById(R.id.model_tgl_catatan);
            isi_catatan = itemView.findViewById(R.id.model_isi_catatan);

        }
    }
}
