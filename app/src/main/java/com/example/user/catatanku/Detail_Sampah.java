package com.example.user.catatanku;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.user.catatanku.Helper.MyHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Detail_Sampah extends AppCompatActivity {

    private static final String URL_RESTORE = "restore_sampah.php";
    private static final String URL_LOAD = "detail_sampah.php";
    private static final String URL_DELETE = "delete_sampah.php";
    private TextView txt_judul;
    private TextView txt_isi;
    private ImageView img_sampah;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_sampah);
        txt_judul = findViewById(R.id.txt_judul_sampah);
        txt_isi = findViewById(R.id.txt_isi_sampah);
        img_sampah = findViewById(R.id.img_detail_sampah);
        load_detail();
    }

    private String get_id(){
        Bundle data = getIntent().getExtras();
        return data.getString("id");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_sampah,menu);
        MenuItem menuRestore = menu.findItem(R.id.btn_restore);
        MenuItem menuDelete = menu.findItem(R.id.delete_sampah);
        menuRestore.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                restore();
                return false;
            }
        });
        menuDelete.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                delete();
                return false;
            }
        });
        return true;
    }

    private void load_detail(){
        MyHelper.show_loading(Detail_Sampah.this,"Memuat detail sampah...");
        StringRequest requestDetail = new StringRequest(Request.Method.POST, MyHelper.HOST_URL + URL_LOAD,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        MyHelper.hide_loading();
                        Log.d("volley", "onResponse: "+response);
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for (int i=0; i<jsonArray.length(); i++){
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String judul = jsonObject.getString("judul_catatan_sampah");
                                String isi = jsonObject.getString("isi_catatan_sampah");
                                String foto = jsonObject.getString("foto_sampah");
                                txt_judul.setText(judul);
                                txt_isi.setText(isi);
                                if (foto.equals("")){
                                    img_sampah.setVisibility(View.GONE);
                                }else{
                                    img_sampah.setImageBitmap(MyHelper.stringToBitmap(foto));
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        MyHelper.hide_loading();
                        Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> map = new HashMap<>();
                map.put("id",get_id());
                return map;
            }
        };
        Volley.newRequestQueue(this).add(requestDetail);
    }

    private void restore(){
        MyHelper.show_loading(Detail_Sampah.this,"Menyimpan ke catatan...");
        StringRequest requestRestore = new StringRequest(Request.Method.POST, MyHelper.HOST_URL + URL_RESTORE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String kode = jsonObject.getString("kode");
                            String pesan = jsonObject.getString("pesan");
                            if (kode.equals("1")){
                                Toast.makeText(Detail_Sampah.this, pesan, Toast.LENGTH_SHORT).show();
                                Home.mActivity.finish();
                                startActivity(new Intent(Detail_Sampah.this,Home.class));
                                finish();
                            }else{
                                MyHelper.alert_no_action(Detail_Sampah.this,pesan);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        MyHelper.hide_loading();
                        MyHelper.alert_no_action(Detail_Sampah.this,error.getMessage());
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map = new HashMap<>();
                map.put("id_catatan",get_id());
                return map;
            }
        };
        Volley.newRequestQueue(this).add(requestRestore);
    }

    private void delete(){
        MyHelper.show_loading(Detail_Sampah.this,"Menghapus sampah...");
        StringRequest requestDelete = new StringRequest(Request.Method.POST, MyHelper.HOST_URL + URL_DELETE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        MyHelper.hide_loading();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String kode = jsonObject.getString("kode");
                            String pesan = jsonObject.getString("pesan");

                            if (kode.equals("1")){
                                Toast.makeText(Detail_Sampah.this, pesan, Toast.LENGTH_SHORT).show();
                                Home.mActivity.finish();
                                startActivity(new Intent(Detail_Sampah.this,Home.class));
                                finish();
                            }else{
                                MyHelper.alert_no_action(Detail_Sampah.this,pesan);
                                MyHelper.hide_loading();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        MyHelper.hide_loading();
                        MyHelper.alert_no_action(Detail_Sampah.this,error.getMessage());
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map = new HashMap<>();
                map.put("id_sampah",get_id());
                return map;
            }
        };
        Volley.newRequestQueue(this).add(requestDelete);
    }
}
