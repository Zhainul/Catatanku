package com.example.user.catatanku.Fragments;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.user.catatanku.Adapter.AdapterSampah;
import com.example.user.catatanku.Helper.MyHelper;
import com.example.user.catatanku.Helper.SPManager;
import com.example.user.catatanku.Model.ModelSampah;
import com.example.user.catatanku.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Sampah extends Fragment {

    private RecyclerView recyclerViewSampah;
    private SearchView searchView;
    private AdapterSampah adapterSampah;
    private List<ModelSampah> modelSampahList = new ArrayList<>();
    private static final String URL_SAMPAH = "get_sampah.php";
    SPManager spManager;

    public static Sampah newInstance(){
        return new Sampah();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_sampah,container,false);
        spManager = new SPManager(getActivity());
        recyclerViewSampah = view.findViewById(R.id.recycler_sampah);
        recyclerViewSampah.setLayoutManager(new LinearLayoutManager(getActivity()));

        load_sampah();

        adapterSampah = new AdapterSampah(getActivity(),modelSampahList);
        recyclerViewSampah.setAdapter(adapterSampah);

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_sampah,menu);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.search_sampah).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setQueryHint("Telusuri sampah  anda...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (adapterSampah != null){
                    adapterSampah.getFilter().filter(newText);
                }
                return false;
            }
        });
    }

    private String get_email(){
        return spManager.getSpEmail();
    }

    private void load_sampah(){

        MyHelper.show_loading(getActivity(),"Memuat sampah...");
        StringRequest requestSampah = new StringRequest(Request.Method.POST, MyHelper.HOST_URL + URL_SAMPAH,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        MyHelper.hide_loading();
                        Log.d("volley", "onResponse: "+response);
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for (int i=0; i<jsonArray.length(); i++){
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                ModelSampah modelSampah = new ModelSampah();
                                modelSampah.setId_sampah(jsonObject.getString("id_catatan_sampah"));
                                modelSampah.setId_user(jsonObject.getString("id_user"));
                                modelSampah.setJudul_sampah(jsonObject.getString("judul_catatan_sampah"));
                                modelSampah.setIsi_sampah(jsonObject.getString("isi_catatan_sampah"));
                                modelSampah.setFoto_sampah(jsonObject.getString("foto_sampah"));
                                modelSampah.setTgl_sampah(jsonObject.getString("tgl_sampah"));
                                modelSampahList.add(modelSampah);
                            }
                            adapterSampah.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        MyHelper.hide_loading();
                        Log.d("error", "onErrorResponse: "+error.getMessage());
                    }
                }){
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> myMap = new HashMap<>();
                myMap.put("email",get_email());
                myMap.put("kode_api",MyHelper.KEY_API);
                return myMap;
            }
        };

        Volley.newRequestQueue(getActivity()).add(requestSampah);

    }

}
