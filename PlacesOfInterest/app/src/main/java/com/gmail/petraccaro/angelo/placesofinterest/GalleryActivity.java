package com.gmail.petraccaro.angelo.placesofinterest;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.GridView;

public class GalleryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        GridView gallery=findViewById(R.id.grid_view);

        AlertDialog.Builder builder=new AlertDialog.Builder(GalleryActivity.this);
        builder.setTitle("Attenzione");
        builder.setMessage("Per mancanza foto, la galleria è vuota!");
        builder.show();
    }
}