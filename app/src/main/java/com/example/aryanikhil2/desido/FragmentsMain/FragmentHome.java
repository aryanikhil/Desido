package com.example.aryanikhil2.desido.FragmentsMain;

import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aryanikhil2.desido.R;

/**
 * Created by Nikhil on 02-07-2016.
 */
public class FragmentHome extends Fragment {
    ImageButton clickPic;
    Button uploadPic;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View rootView =  inflater.inflate(R.layout.home_frag, container, false);

        clickPic = (ImageButton)rootView.findViewById(R.id.imageButton);
        uploadPic = (Button)rootView.findViewById(R.id.button2);
        uploadPic.setPaintFlags(uploadPic.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        clickPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(),"Working1",Toast.LENGTH_LONG).show();
            }
        });

        uploadPic.setOnClickListener(new View.OnClickListener(){

            public void onClick(View view){
                Toast.makeText(getContext(),"Working2",Toast.LENGTH_LONG).show();
            }
        });

        return rootView;
    }
}
