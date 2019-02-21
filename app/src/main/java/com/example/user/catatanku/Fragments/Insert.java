package com.example.user.catatanku.Fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.user.catatanku.Helper.MyHelper;
import com.example.user.catatanku.Helper.SPManager;
import com.example.user.catatanku.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Insert extends Fragment {

    private EditText edt_judul;
    private EditText edt_isi;
    private ImageView img_catatan;
    Bitmap bitmap;
    public static final int IMG_REQUEST = 1;
    public static final int IMG_CAPTURE = 11111;
    private static final String URL_SAVE = "insert_catatan.php";
    private LinearLayout ln_open_camera;
    private LinearLayout ln_open_file;
    private LinearLayout ln_open_file_img;
    private LinearLayout ln_open_camera_img;
    private LinearLayout ln_delete_img;
    private AlertDialog.Builder alert_option;
    private AlertDialog alertDialog;
    private Button btn_insert;
    SPManager spManager;
    MenuItem itemImage;

    public static Insert newInstance(){
        return new Insert();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tambah,container,false);
        edt_judul = view.findViewById(R.id.edt_judul_catatan);
        edt_isi =view.findViewById(R.id.edt_isi_catatan);
        img_catatan = view.findViewById(R.id.img_catatan);
        btn_insert = view.findViewById(R.id.btn_insert_catatan);
        spManager = new SPManager(getActivity());

        img_catatan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                        img_catatan.setVisibility(View.GONE);
                        alertDialog.cancel();
                        itemImage.setVisible(true);
                    }
                });

                alert_option = new AlertDialog.Builder(getActivity());
                alert_option.setTitle("Pilihan");
                alert_option.setView(mview);
                alert_option.setCancelable(true);
                alertDialog = alert_option.create();
                alertDialog.show();
            }
        });

        btn_insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edt_judul.getText().toString().isEmpty()){
                    MyHelper.alert_no_action(getActivity(),"Judul Harus Diisi !");
                    edt_judul.requestFocus();
                }else{
                    do_insert();
                }
            }
        });

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.add_image,menu);
        itemImage = menu.findItem(R.id.tambah_image);
        itemImage.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
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

                alert_option = new AlertDialog.Builder(getActivity());
                alert_option.setTitle("Pilihan");
                alert_option.setView(mview);
                alert_option.setCancelable(true);
                alertDialog = alert_option.create();
                alertDialog.show();
                return false;
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        alertDialog.cancel();
        if (requestCode == IMG_CAPTURE && resultCode == getActivity().RESULT_OK && data != null){
            Bundle bndImage = data.getExtras();
            bitmap = (Bitmap) bndImage.get("data");
            img_catatan.setImageBitmap(bitmap);
            img_catatan.setVisibility(View.VISIBLE);
            this.itemImage.setVisible(false);
        }else if (requestCode == IMG_REQUEST && resultCode == getActivity().RESULT_OK && data != null){
            Uri path = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),path);
                img_catatan.setImageBitmap(bitmap);
                img_catatan.setVisibility(View.VISIBLE);
                this.itemImage.setVisible(false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void do_insert(){

        MyHelper.show_loading(getActivity(),"Menyimpan catatan");
        StringRequest request = new StringRequest(Request.Method.POST, MyHelper.HOST_URL + URL_SAVE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        MyHelper.hide_loading();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String kode = jsonObject.getString("kode");
                            String pesan = jsonObject.getString("pesan");

                            if (kode.equals("1")){
                                getActivity().getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.content_frame,Catatan.newInstance()).commit();
                            }else{
                                Toast.makeText(getActivity().getApplicationContext(),pesan,Toast.LENGTH_LONG).show();
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
                        Log.d("error", "onErrorResponse: " + error.getMessage());
                    }
                }){
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> dataMap = new HashMap<>();
                dataMap.put("id_user",MyHelper.get_Id_user(getActivity(),get_email()));
                dataMap.put("judul",edt_judul.getText().toString());
                dataMap.put("isi",edt_isi.getText().toString());
                if (img_catatan.getVisibility() == View.VISIBLE){
                    dataMap.put("foto",MyHelper.imageToString(bitmap));
                }else{
                    dataMap.put("foto","");
                }
                dataMap.put("tanggal",get_tanggal());
                return dataMap;
            }
        };
        int x = 2;
        request.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy
                .DEFAULT_TIMEOUT_MS * 48,x,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(getActivity()).add(request);

    }

    private String get_email(){
        return spManager.getSpEmail();
    }

    public void openDirectory(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,IMG_REQUEST);
    }

    public void open_camera(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent,IMG_CAPTURE);
    }

    private String get_tanggal(){
        Date mDate = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formatDate = df.format(mDate);
        return formatDate;
    }
}
