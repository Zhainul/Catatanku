package com.example.user.catatanku;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.user.catatanku.Fragments.About;
import com.example.user.catatanku.Fragments.Catatan;
import com.example.user.catatanku.Fragments.EditProfil;
import com.example.user.catatanku.Fragments.Sampah;
import com.example.user.catatanku.Fragments.UbahPassword;
import com.example.user.catatanku.Helper.MyHelper;
import com.example.user.catatanku.Helper.SPManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String URL_USER = "get_user.php";
    public static Activity mActivity;
    boolean doubleToExit = false;
    TextView txt_nama;
    TextView txt_email;
    CircleImageView img_profil;
    LinearLayout ln_container;
    SPManager spManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mActivity = this;

        spManager = new SPManager(this);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View view = navigationView.getHeaderView(0);
        txt_nama = view.findViewById(R.id.txt_nama_user);
        txt_email = view.findViewById(R.id.txt_email_user);
        img_profil = view.findViewById(R.id.img_profil);
        ln_container = view.findViewById(R.id.ln_container);

        ln_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Home.this.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.content_frame,EditProfil.newInstance()).commit();
                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
            }
        });

        load_user();

        if (savedInstanceState == null){
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.content_frame,Catatan.newInstance()).commit();
        }

    }

    private String get_email(){
        return spManager.getSpEmail();
    }

    private void logout(){
        spManager.save_login(spManager.SP_EXIT,false);
        spManager.clearSP();
        Intent intent = new Intent(Home.this,Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0){
            getSupportFragmentManager().popBackStack();
        }else if (!doubleToExit){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame,Catatan.newInstance()).commit();
            this.doubleToExit = true;
            Toast.makeText(getApplicationContext(), "Press back again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleToExit = false;
                }
            },2000);
        }else{
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_catatan){
            Home.this.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame,Catatan.newInstance()).commit();
        }else if (id == R.id.nav_about){
            Home.this.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame,About.newInstance()).commit();
        }else if (id == R.id.nav_sampah){
            Home.this.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame,Sampah.newInstance()).commit();
        }else if (id == R.id.nav_edit_profil){
            Home.this.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame,EditProfil.newInstance()).commit();
        }else if (id == R.id.nav_change_password){
            Home.this.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame,UbahPassword.newInstance()).commit();
        }else if (id == R.id.nav_logout){
            new AlertDialog.Builder(Home.this)
                    .setTitle("Konfirmasi")
                    .setMessage("Apakah anda yakin ingin keluar ?")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            logout();
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).create().show();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void load_user(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, MyHelper.HOST_URL + URL_USER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for (int i=0; i<response.length(); i++){
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String nama = jsonObject.getString("nama");
                                String email = jsonObject.getString("email");
                                String foto = jsonObject.getString("foto");
                                if (foto.equals("")){
                                    img_profil.setImageResource(R.drawable.profil);
                                }else{
                                    Bitmap bitmap = MyHelper.stringToBitmap(foto);
                                    img_profil.setImageBitmap(bitmap);
                                }
                                txt_nama.setText(nama);
                                txt_email.setText(email);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        MyHelper.alert_no_action(Home.this,error.getMessage());
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map = new HashMap<>();
                map.put("email",get_email());
                return map;
            }
        };

        Volley.newRequestQueue(this).add(stringRequest);

    }


}
