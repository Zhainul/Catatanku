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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.user.catatanku.Detail_Catatan;
import com.example.user.catatanku.Helper.MyHelper;
import com.example.user.catatanku.Helper.SPManager;
import com.example.user.catatanku.Home;
import com.example.user.catatanku.ImageViewer;
import com.example.user.catatanku.R;
import com.example.user.catatanku.Register;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfil extends Fragment {

    SPManager spManager;
    private CircleImageView img_circle_profil;
    private EditText edt_nama;
    private EditText edt_email;
    private Button btn_save_profil;
    private AlertDialog.Builder alert_option;
    private AlertDialog alertDialog;
    private LinearLayout ln_open_camera;
    private LinearLayout ln_open_file;
    private Bitmap bitmap;
    private static final String URL_UPDATE = "update_user.php";

    public static EditProfil newInstance(){
        return new EditProfil();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profil,container,false);
        img_circle_profil = view.findViewById(R.id.img_profil_detail);
        edt_nama = view.findViewById(R.id.edt_nama_user);
        edt_email = view.findViewById(R.id.edt_email_user);
        btn_save_profil = view.findViewById(R.id.btn_save_profil);
        spManager = new SPManager(getActivity());

        load_user();

        img_circle_profil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),ImageViewer.class);
                intent.putExtra("gambar",Detail_Catatan.MyByte(bitmap));
                getActivity().startActivity(intent);
            }
        });

        img_circle_profil.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
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

        btn_save_profil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if (edt_nama.getText().toString().isEmpty()){
                   MyHelper.alert_no_action(getActivity(),"Nama Harus Diisi !");
                   edt_nama.requestFocus();
               }else if (edt_email.getText().toString().isEmpty()){
                   MyHelper.alert_no_action(getActivity(),"Email Harus Diisi !");
                   edt_email.requestFocus();
               } else{
                   do_update();
               }
            }
        });

        edt_email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                if (Register.validation(s.toString())){
                    MyHelper.show_toast(getActivity(),"Alamat E-mail Valid !");
                }else{
                    MyHelper.show_toast(getActivity(),"Alamat E-mail Invalid !");
                }
            }
        });

        return view;
    }

    private String get_email(){
        return spManager.getSpEmail();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public void openDirectory(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,Insert.IMG_REQUEST);
    }

    public void open_camera(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent,Insert.IMG_CAPTURE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        alertDialog.cancel();
        if (requestCode == Insert.IMG_CAPTURE && resultCode == getActivity().RESULT_OK && data != null){
            Bundle bndImage = data.getExtras();
            bitmap = (Bitmap) bndImage.get("data");
            img_circle_profil.setImageBitmap(bitmap);
        }else if (requestCode == Insert.IMG_REQUEST && resultCode == getActivity().RESULT_OK && data !=null){
            Uri path = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),path);
                img_circle_profil.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void load_user(){

        MyHelper.show_loading(getActivity(),"memuat data user...");
        StringRequest requestUser = new StringRequest(Request.Method.POST, MyHelper.HOST_URL + Home.URL_USER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        MyHelper.hide_loading();
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for (int i=0; i<jsonArray.length(); i++){
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String foto = jsonObject.getString("foto");
                                if (foto.equals("")){
                                    img_circle_profil.setBackgroundResource(R.drawable.profil);
                                }else{
                                    img_circle_profil.setImageBitmap(MyHelper.stringToBitmap(foto));
                                    if (bitmap == null){
                                        bitmap = MyHelper.stringToBitmap(foto);
                                    }
                                }
                                edt_nama.setText(jsonObject.getString("nama"));
                                edt_email.setText(jsonObject.getString("email"));
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
                map.put("email",get_email());
                return map;
            }
        };
        Volley.newRequestQueue(getActivity()).add(requestUser);
    }

    private void do_update(){
        MyHelper.show_loading(getActivity(),"Menyimpan perubahan...");
        StringRequest requestUpdate = new StringRequest(Request.Method.POST, MyHelper.HOST_URL + URL_UPDATE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("volley", "onResponse: "+response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String kode = jsonObject.getString("kode");
                            String pesan = jsonObject.getString("pesan");
                            if (kode.equals("1")){
                                spManager.save_email(edt_email.getText().toString());
                                Toast.makeText(getActivity(), pesan, Toast.LENGTH_SHORT).show();
                                Intent intent = getActivity().getIntent();
                                getActivity().finish();
                                getActivity().startActivity(intent);
                            }else{
                                Toast.makeText(getActivity(), pesan, Toast.LENGTH_SHORT).show();
                                MyHelper.hide_loading();
                            }
                        } catch (JSONException e) {

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        MyHelper.hide_loading();
                    }
                }){
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> map = new HashMap<>();
                map.put("id_user",MyHelper.get_Id_user(getActivity(),get_email()));
                map.put("nama",edt_nama.getText().toString());
                map.put("email",edt_email.getText().toString());
                map.put("foto",MyHelper.imageToString(bitmap));
                return map;
            }
        };
        Volley.newRequestQueue(getActivity()).add(requestUpdate);
    }
}
