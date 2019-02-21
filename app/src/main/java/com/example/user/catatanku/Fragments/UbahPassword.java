package com.example.user.catatanku.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

import java.util.HashMap;
import java.util.Map;

public class UbahPassword extends Fragment {

    private EditText edt_password_lama;
    private EditText edt_password_baru;
    private Button btn_change_password;
    SPManager spManager;
    private static final String URL_CHANGE = "ubah_password.php";

    public static UbahPassword newInstance(){
        return new UbahPassword();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ubah_password,container,false);
        spManager = new SPManager(getActivity());
        edt_password_lama = view.findViewById(R.id.edt_password_lama);
        edt_password_baru = view.findViewById(R.id.edt_password_baru);
        final EditText edt_confirm_password = view.findViewById(R.id.edt_konfirmasi_password);
        btn_change_password = view.findViewById(R.id.btn_change_password);

        edt_confirm_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().equals(edt_password_baru.getText().toString())){
                    Toast.makeText(getActivity(), "Password cocok !", Toast.LENGTH_SHORT).show();
                    btn_change_password.setEnabled(true);
                    btn_change_password.setBackgroundResource(R.drawable.bg_btn);
                }else{
                    Toast.makeText(getActivity(), "Konfirmasi password salah !", Toast.LENGTH_SHORT).show();
                    btn_change_password.setEnabled(false);
                    btn_change_password.setBackgroundResource(R.drawable.bg_enable);
                }
            }
        });

        btn_change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edt_password_lama.getText().toString().isEmpty()){
                    MyHelper.alert_no_action(getActivity(),"Password lama harus diisi !");
                    edt_password_lama.requestFocus();
                }else if (edt_password_baru.getText().toString().isEmpty()){
                    MyHelper.alert_no_action(getActivity(),"Password baru harus diisi !");
                    edt_password_baru.requestFocus();
                }else if (edt_confirm_password.getText().toString().isEmpty()){
                    MyHelper.alert_no_action(getActivity(),"Konfirmasi password baru anda !");
                    edt_confirm_password.requestFocus();
                }else{
                    change_password();
                }
            }
        });

        return view;
    }

    private String get_email(){
        return spManager.getSpEmail();
    }

    private void change_password(){

        MyHelper.show_loading(getActivity(),"Menyimpan perubahan...");
        StringRequest requestPassword = new StringRequest(Request.Method.POST, MyHelper.HOST_URL + URL_CHANGE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        MyHelper.hide_loading();
                        Log.d("volley", "onResponse: "+response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String kode = jsonObject.getString("kode");
                            String pesan = jsonObject.getString("pesan");
                            if (kode.equals("1")){
                                Toast.makeText(getActivity(), pesan, Toast.LENGTH_SHORT).show();
                                Intent intent = getActivity().getIntent();
                                getActivity().finish();
                                getActivity().startActivity(intent);
                            }else{
                                Toast.makeText(getActivity(), pesan, Toast.LENGTH_SHORT).show();
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
                map.put("id_user",MyHelper.get_Id_user(getActivity(),get_email()));
                map.put("password_lama",edt_password_lama.getText().toString());
                map.put("password_baru",edt_password_baru.getText().toString());
                return map;
            }
        };
        Volley.newRequestQueue(getActivity()).add(requestPassword);
    }
}
