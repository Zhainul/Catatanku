package com.example.user.catatanku;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.user.catatanku.Helper.MyHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Register extends AppCompatActivity {

    public static final String API_REGISTER = "register.php";
    private EditText edt_nama;
    private EditText edt_email;
    private EditText edt_password;
    private Button btn_register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edt_nama = findViewById(R.id.edt_reg_nama);
        edt_email = findViewById(R.id.edt_reg_email);
        edt_password = findViewById(R.id.edt_reg_password);
        btn_register = findViewById(R.id.btn_regis_user);

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edt_nama.getText().toString().isEmpty()){
                    show_warning("Nama harus diisi !");
                    edt_nama.requestFocus();
                }else if (edt_email.getText().toString().isEmpty()){
                    show_warning("Email harus diisi");
                    edt_email.requestFocus();
                }else if (edt_password.getText().toString().isEmpty()){
                    show_warning("Password harus diisi !");
                    edt_password.requestFocus();
                }else{
                    do_register();
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
                String email = s.toString();
                if (validation(email)){
                    show_toast("Alamat Email Valid !");
                }else{
                    show_toast("Alamat Email Invalid !");
                }
            }
        });
        
    }

    public static boolean validation(String mString){
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(mString);
        return matcher.matches();
    }

    private void show_warning(String message){
        MyHelper.alert_no_action(Register.this,message);
    }

    private void show_loading(String message){
        MyHelper.show_loading(this,message);
    }

    private void show_toast(String message){
        MyHelper.show_toast(Register.this,message);
    }

    private void do_register(){

        show_loading("Meyimpan data...");

        StringRequest mRequest = new StringRequest(Request.Method.POST, MyHelper.HOST_URL + API_REGISTER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        MyHelper.hide_loading();

                        try {
                            JSONObject object = new JSONObject(response);
                            String kode = object.getString("kode");
                            String pesan = object.getString("pesan");

                            if (kode.equals("1")){
                                clear_data();
                                new AlertDialog.Builder(Register.this)
                                        .setTitle("Peringatan")
                                        .setMessage(pesan)
                                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                finish();
                                            }
                                        }).create().show();
                            }else{
                                show_warning(pesan);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        show_warning(error.getMessage());
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map = new HashMap<>();
                map.put("nama",edt_nama.getText().toString());
                map.put("email",edt_email.getText().toString());
                map.put("password",edt_password.getText().toString());
                return map;
            }
        };

        Volley.newRequestQueue(this).add(mRequest);

    }

    private void clear_data() {
        edt_nama.setText(null);
        edt_email.setText(null);
        edt_password.setText(null);
    }
}
