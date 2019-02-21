package com.example.user.catatanku.Fragments;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.user.catatanku.Adapter.AdapterCatatan;
import com.example.user.catatanku.Helper.MyHelper;
import com.example.user.catatanku.Helper.SPManager;
import com.example.user.catatanku.Model.ModelCatatan;
import com.example.user.catatanku.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Catatan extends android.support.v4.app.Fragment {

    List<ModelCatatan> mCatatans = new ArrayList<>();
    RecyclerView recyclerView;
    public static final String URL_CATATAN = "get_catatan.php";
    public static AdapterCatatan adapterCatatan;
    private SearchView searchView;
    private FloatingActionButton floatingActionButton;
    SPManager spManager;
    private MenuItem action_list;
    private MenuItem action_grid;

    public static Catatan newInstance(){
        return new Catatan();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_catatan,container,false);

        floatingActionButton = view.findViewById(R.id.btn_floating);
        recyclerView = view.findViewById(R.id.recycler_catatan);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        spManager = new SPManager(getActivity());

        load_catatan();

        adapterCatatan = new AdapterCatatan(getActivity(),mCatatans);
        recyclerView.setAdapter(adapterCatatan);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.content_frame,Insert.newInstance()).commit();
            }
        });

        if (spManager.get_view()){
            recyclerView.setLayoutManager(new StaggeredGridLayoutManager(
                    2,StaggeredGridLayoutManager.VERTICAL));
            recyclerView.setAdapter(adapterCatatan);
        }

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu,menu);
        action_grid = menu.findItem(R.id.action_grid);
        action_list = menu.findItem(R.id.action_list);

        action_grid.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                spManager.save_view(true);
                recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,
                        StaggeredGridLayoutManager.VERTICAL));
                recyclerView.setAdapter(adapterCatatan);
                action_grid.setVisible(false);
                action_list.setVisible(true);
                return false;
            }
        });

        action_list.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                spManager.save_view(false);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                recyclerView.setAdapter(adapterCatatan);
                action_list.setVisible(false);
                action_grid.setVisible(true);
                return false;
            }
        });

        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setQueryHint("Telusuri catatan  anda...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (adapterCatatan != null){
                    adapterCatatan.getFilter().filter(newText);
                }
                return false;
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    private String getEmail(){
        return spManager.getSpEmail();
    }

    private void load_catatan(){

        MyHelper.show_loading(getActivity(),"Memuat data...");
        StringRequest request = new StringRequest(Request.Method.POST, MyHelper.HOST_URL + URL_CATATAN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        MyHelper.hide_loading();
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for (int i=0; i<jsonArray.length(); i++){
                                JSONObject object = jsonArray.getJSONObject(i);
                                ModelCatatan modelCatatan = new ModelCatatan();
                                modelCatatan.setId_catatan(object.getString("id_catatan"));
                                modelCatatan.setId_user(object.getString("id_user"));
                                modelCatatan.setJudul(object.getString("judul"));
                                modelCatatan.setIsi(object.getString("isi"));
                                modelCatatan.setFoto(object.getString("foto"));
                                modelCatatan.setTgl_simpan(object.getString("tgl_catatan"));
                                mCatatans.add(modelCatatan);
                            }
                            adapterCatatan.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        MyHelper.hide_loading();
                        MyHelper.alert_no_action(getActivity(),error.getMessage());
                    }
                }){
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> myMap = new HashMap<>();
                myMap.put("email",getEmail());
                myMap.put("kode_api",MyHelper.KEY_API);
                return myMap;
            }
        };

        Volley.newRequestQueue(getActivity()).add(request);

    }

}
