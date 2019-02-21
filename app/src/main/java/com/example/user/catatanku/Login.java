package com.example.user.catatanku;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.user.catatanku.Helper.MyHelper;
import com.example.user.catatanku.Helper.SPManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {

    public static final String API_LOGIN = "login.php";
    private EditText edt_email;
    private EditText edt_password;
    private Button btn_login,btn_register_link;
    SPManager spManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edt_email = findViewById(R.id.edt_log_email);
        edt_password = findViewById(R.id.edt_log_password);
        btn_login = findViewById(R.id.btn_login);
        btn_register_link = findViewById(R.id.btn_register_link);
        spManager = new SPManager(this);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edt_email.getText().toString().isEmpty()){
                    MyHelper.alert_no_action(Login.this,"Email harus diisi");
                    edt_email.requestFocus();
                }else if (edt_password.getText().toString().isEmpty()){
                    MyHelper.alert_no_action(Login.this,"Password harus diisi");
                    edt_password.requestFocus();
                }else{
                    do_login();
                }
            }
        });
        btn_register_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this,Register.class));
            }
        });

        if (spManager.sudah_login()){
            startActivity(new Intent(Login.this,Home.class));
            finish();
        }

    }

    private void do_login(){

        MyHelper.show_loading(Login.this,"Verifikasi...");
        StringRequest request = new StringRequest(Request.Method.POST, MyHelper.HOST_URL + API_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        MyHelper.hide_loading();
                        try {
                            JSONObject object = new JSONObject(response);
                            String kode = object.getString("kode");
                            String pesan = object.getString("pesan");
                            if (kode.equals("1")){
                                spManager.save_email(edt_email.getText().toString());
                                spManager.save_login(spManager.SP_EXIT,true);
                                new AlertDialog.Builder(Login.this)
                                        .setTitle("Peringatan")
                                        .setMessage(pesan)
                                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                startActivity(new Intent(Login.this,Home.class));
                                                finish();
                                            }
                                        }).create().show();
                            }else{
                                MyHelper.alert_no_action(Login.this,pesan);
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
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> data_login = new HashMap<>();
                data_login.put("email",edt_email.getText().toString());
                data_login.put("password",edt_password.getText().toString());
                return data_login;
            }
        };

        Volley.newRequestQueue(this).add(request);

    }
}
