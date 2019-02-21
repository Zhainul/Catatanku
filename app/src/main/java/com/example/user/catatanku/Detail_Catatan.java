package com.example.user.catatanku;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.user.catatanku.Helper.MyHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Detail_Catatan extends AppCompatActivity {

    private Bundle bundle;
    private EditText edt_judul_detail;
    private EditText edt_isi_detail;
    private ImageView img_catatan_detail;
    private LinearLayout ln_open_camera;
    private LinearLayout ln_open_file;
    private LinearLayout ln_open_camera_img;
    private LinearLayout ln_open_file_img;
    private LinearLayout ln_delete_img;
    private AlertDialog.Builder alert_builder;
    private AlertDialog alert;
    private static final String URL_DETAIL = "detail_catatan.php";
    private static final String URL_UPDATE = "update_catatan.php";
    private static final String URL_MIGRASI = "migrasi_catatan.php";
    Bitmap bitmap;
    private static final int IMG_REQUEST = 1;
    private static final int IMG_CAPTURE = 11111;
    private Button btn_ubah_catatan;
    private Button btn_hapus_catatan;
    MenuItem menuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail__catatan);
        getSupportActionBar().setTitle("Detail Catatan");

        edt_judul_detail = findViewById(R.id.edt_judul_catatan_detail);
        edt_isi_detail = findViewById(R.id.edt_isi_catatan_detail);
        img_catatan_detail = findViewById(R.id.img_catatan_detail);
        btn_ubah_catatan = findViewById(R.id.btn_ubah_catatan);
        btn_hapus_catatan = findViewById(R.id.btn_hapus_catatan);

        load_detail();

        img_catatan_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentView = new Intent(Detail_Catatan.this,ImageViewer.class);
                intentView.putExtra("gambar",MyByte(bitmap));
                startActivity(intentView);
            }
        });

        img_catatan_detail.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                LayoutInflater mLayoutInflater = getLayoutInflater();
                View mview = mLayoutInflater.inflate(R.layout.option_picture_profil,null);
                ln_open_camera_img = mview.findViewById(R.id.link_open_camera_profil);
                ln_open_file_img = mview.findViewById(R.id.link_open_file_profil);
                ln_delete_img =mview.findViewById(R.id.delete_img_profil);

                ln_open_file_img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openDirectory();
                    }
                });

                ln_open_camera_img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        open_camera();
                    }
                });

                ln_delete_img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        img_catatan_detail.setVisibility(View.GONE);
                        alert.cancel();
                        menuItem.setVisible(true);
                    }
                });

                alert_builder = new AlertDialog.Builder(Detail_Catatan.this);
                alert_builder.setTitle("Pilihan");
                alert_builder.setView(mview);
                alert_builder.setCancelable(true);
                alert = alert_builder.create();
                alert.show();
                return false;
            }
        });

        btn_ubah_catatan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edt_judul_detail.getText().toString().isEmpty()){
                    MyHelper.alert_no_action(Detail_Catatan.this,"Judul Harus Diisi !");
                    edt_judul_detail.requestFocus();
                }else{
                    save_change();
                }
            }
        });

        btn_hapus_catatan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(Detail_Catatan.this)
                        .setTitle("konfirmasi")
                        .setMessage("Apakah anda yakin ingin menghapus ?")
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                do_delete();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) { }
                        })
                        .create().show();
            }
        });

    }

    private String get_idCatatan(){
        bundle = getIntent().getExtras();
        return bundle.getString("id_catatan");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_menu,menu);
        MenuItem menuShare = menu.findItem(R.id.share);
        menuShare.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                send_email();
                return false;
            }
        });

        menuItem = menu.findItem(R.id.tambah_image);
        menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                LayoutInflater mLayoutInflater = getLayoutInflater();
                View mview = mLayoutInflater.inflate(R.layout.option_picture,null);
                ln_open_camera = mview.findViewById(R.id.link_open_camera);
                ln_open_file = mview.findViewById(R.id.link_open_file);

                ln_open_file.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openDirectory();
                    }
                });

                ln_open_camera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        open_camera();
                    }
                });

                alert_builder = new AlertDialog.Builder(Detail_Catatan.this);
                alert_builder.setTitle("Pilihan");
                alert_builder.setView(mview);
                alert_builder.setCancelable(true);
                alert = alert_builder.create();
                alert.show();
                return false;
            }
        });
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        alert.cancel();

        if (requestCode == IMG_CAPTURE && resultCode == RESULT_OK && data != null){

            Bundle bndImage = data.getExtras();
            bitmap = (Bitmap) bndImage.get("data");
            img_catatan_detail.setImageBitmap(bitmap);
            img_catatan_detail.setVisibility(View.VISIBLE);
            this.menuItem.setVisible(false);

        } else if (requestCode == IMG_REQUEST && resultCode == RESULT_OK && data != null){
            Uri path = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),path);
                img_catatan_detail.setImageBitmap(bitmap);
                img_catatan_detail.setVisibility(View.VISIBLE);
                this.menuItem.setVisible(false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void load_detail(){

        MyHelper.show_loading(Detail_Catatan.this,"memuat detail..");
        final StringRequest requestDetail = new StringRequest(Request.Method.POST, MyHelper.HOST_URL + URL_DETAIL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        MyHelper.hide_loading();
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for (int i=0; i<jsonArray.length(); i++){
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                edt_judul_detail.setText(jsonObject.getString("judul"));
                                edt_isi_detail.setText(jsonObject.getString("isi"));

                                if (bitmap == null){
                                    bitmap = MyHelper.stringToBitmap(jsonObject.getString("foto"));
                                }

                                if (jsonObject.getString("foto").equals("")){
                                    img_catatan_detail.setVisibility(View.GONE);
                                }else{
                                    img_catatan_detail.setImageBitmap(MyHelper.stringToBitmap(
                                            jsonObject.getString("foto")
                                    ));
                                    img_catatan_detail.setVisibility(View.VISIBLE);
                                    menuItem.setVisible(false);
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
                        MyHelper.show_alert(Detail_Catatan.this,error.getMessage());
                    }
                }){
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> map = new HashMap<>();
                map.put("id_catatan",get_idCatatan());
                return map;
            }
        };

        Volley.newRequestQueue(this).add(requestDetail);

    }

    private void save_change(){

        MyHelper.show_loading(Detail_Catatan.this,"Menyimpan perubahan...");
        StringRequest requestUpdate = new StringRequest(Request.Method.POST, MyHelper.HOST_URL + URL_UPDATE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        MyHelper.hide_loading();
                        Log.d("volley", "onResponse: "+response);
                        try {
                            JSONObject object = new JSONObject(response);
                            if (object.getString("kode").equals("1")){
                                String pesan = object.getString("pesan");
                                Toast.makeText(getApplicationContext(),pesan,Toast.LENGTH_SHORT).show();
                                Home.mActivity.finish();
                                startActivity(new Intent(Detail_Catatan.this,Home.class));
                                finish();
                            }else{
                                MyHelper.show_alert(Detail_Catatan.this,object.getString("pesan"));
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
                        MyHelper.show_alert(Detail_Catatan.this,error.getMessage());
                    }
                }){
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> mapUpdate = new HashMap<>();
                mapUpdate.put("id_catatan",get_idCatatan());
                mapUpdate.put("judul",edt_judul_detail.getText().toString());
                mapUpdate.put("isi",edt_isi_detail.getText().toString());
                if (img_catatan_detail.getVisibility() == View.VISIBLE){
                    mapUpdate.put("foto",MyHelper.imageToString(bitmap));
                }else{
                    mapUpdate.put("foto","");
                }
                return mapUpdate;
            }
        };

        Volley.newRequestQueue(this).add(requestUpdate);

    }

    private void send_email(){
        String judul = edt_judul_detail.getText().toString();
        String isi = edt_isi_detail.getText().toString();

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setData(Uri.parse("mailto:"));
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT,judul);
        intent.putExtra(Intent.EXTRA_TEXT,isi);
        intent.createChooser(intent,"pilihan");
        startActivity(intent);
    }

    private void open_camera(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent,IMG_CAPTURE);
    }

    private void openDirectory(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,IMG_REQUEST);
    }

    private void do_delete(){

        MyHelper.show_loading(Detail_Catatan.this,"Menambahkan ke sampah...");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, MyHelper.HOST_URL + URL_MIGRASI,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        MyHelper.hide_loading();
                        try {

                            JSONObject jsonObject = new JSONObject(response);
                            String kode = jsonObject.getString("kode");
                            String pesan = jsonObject.getString("pesan");

                            if (kode.equals("1")){
                                Toast.makeText(getApplicationContext(),pesan,Toast.LENGTH_SHORT).show();
                                Home.mActivity.finish();
                                startActivity(new Intent(Detail_Catatan.this,Home.class));
                                finish();
                            }else{
                                Toast.makeText(getApplicationContext(),pesan,Toast.LENGTH_SHORT).show();
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
                        Log.d("volley", "onErrorResponse: "+error.getMessage());
                    }
                }){
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> map = new HashMap<>();
                map.put("id_catatan",get_idCatatan());
                return map;
            }
        };

        Volley.newRequestQueue(this).add(stringRequest);

    }

    public static byte[] MyByte(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,baos);
        return baos.toByteArray();
    }

}
