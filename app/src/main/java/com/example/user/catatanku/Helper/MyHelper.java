package com.example.user.catatanku.Helper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class MyHelper {
    public static final String HOST_URL = "http://192.168.43.103/catat_api/";
    private static final String URL_USER = "get_user.php";
    public static final String KEY_API = "170699";
    private static ProgressDialog mProgressDialog;
    private static String id_user=null;


    public static void show_loading(Activity mActivity, String message){
        mProgressDialog = new ProgressDialog(mActivity);
        mProgressDialog.setMessage(message);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    public static void hide_loading(){
        mProgressDialog.dismiss();
    }

    public static void show_alert(final Activity mActivity, String message){
        AlertDialog.Builder myAlert = new AlertDialog.Builder(mActivity);
        myAlert.setTitle("Peringatan");
        myAlert.setMessage(message);
        myAlert.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mActivity.finish();
            }
        });
        myAlert.create().show();
    }

    public static void alert_no_action(final Activity mActivity,String message){
        AlertDialog.Builder alertNoAction = new AlertDialog.Builder(mActivity);
        alertNoAction.setTitle("Peringatan");
        alertNoAction.setMessage(message);
        alertNoAction.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) { }
        });
        alertNoAction.create().show();
    }

    public static void show_toast(Context context,String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static String imageToString(Bitmap mbitmap){
        ByteArrayOutputStream mStream = new ByteArrayOutputStream();
        mbitmap.compress(Bitmap.CompressFormat.JPEG,100,mStream);
        byte[] imaBytes = mStream.toByteArray();
        return Base64.encodeToString(imaBytes,Base64.DEFAULT);
    }

    public static Bitmap stringToBitmap(String baseString){
        byte[] decode = Base64.decode(baseString,Base64.DEFAULT);
        Bitmap mBitmap = BitmapFactory.decodeByteArray(decode,0,decode.length);
        return mBitmap;
    }

    public static String get_Id_user(Activity activity,final String email){
        StringRequest stringGet = new StringRequest(Request.Method.POST, MyHelper.HOST_URL + URL_USER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("volley", "onResponse: "+response);
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for (int i=0; i<jsonArray.length(); i++){
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                id_user = jsonObject.getString("id_user");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("getting_id", "onErrorResponse: "+error.getMessage());
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map = new HashMap<>();
                map.put("email",email);
                return map;
            }
        };
        Volley.newRequestQueue(activity).add(stringGet);
        return id_user;
    }

}
