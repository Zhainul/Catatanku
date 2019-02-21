package com.example.user.catatanku.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ViewFlipper;

import com.example.user.catatanku.R;

public class About extends Fragment {

    private ViewFlipper viewFlipper;
    private Button btn_contact_us;

    public static About newInstance(){
        return new About();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about,container,false);
        viewFlipper = view.findViewById(R.id.myFlipper);
        btn_contact_us = view.findViewById(R.id.btn_contact_us);

        int image[] = {R.drawable.satu,R.drawable.dua,R.drawable.tiga,R.drawable.empat};
        for (int myImage : image){
            action_slide(myImage);
        }

        btn_contact_us.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call();
            }
        });

        return view;
    }

    public void action_slide(int images){
        ImageView imageView = new ImageView(getActivity());
        imageView.setBackgroundResource(images);
        viewFlipper.addView(imageView);
        viewFlipper.setFlipInterval(2000);
        viewFlipper.setAutoStart(true);
        viewFlipper.setInAnimation(getActivity(),android.R.anim.slide_in_left);
        viewFlipper.setOutAnimation(getActivity(),android.R.anim.slide_out_right);
    }

    private void call(){
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:085852818019"));
        startActivity(intent);
    }
}
