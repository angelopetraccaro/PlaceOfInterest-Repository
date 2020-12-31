package com.gmail.petraccaro.angelo.placesofinterest.Activities;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.GridView;
import android.widget.ImageView;

import com.gmail.petraccaro.angelo.placesofinterest.Adapters.CustumAdapterRecognizedPhotos;
import com.gmail.petraccaro.angelo.placesofinterest.Controllers.Services.UriToBitmapService;
import com.gmail.petraccaro.angelo.placesofinterest.Controllers.Services.RecognizerService;
import com.gmail.petraccaro.angelo.placesofinterest.Models.Post;
import com.gmail.petraccaro.angelo.placesofinterest.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import java.util.ArrayList;


public class DetectorActivity extends AppCompatActivity {
    public ArrayList<String> distanza=new ArrayList<>();
    ArrayList<Bitmap> bitmapArray = new ArrayList<>();
    private ImageView ImgViewFotoProfilo;
    private final ArrayList<String> arrayofuri = new ArrayList<>();
    private  MyReceiver myReceiver;
    GridView gridView;

    public static final String FILTER_ACTION_KEY = "convertitore";
    public static final String FILTER_ACTION_KEY2 = "Riconoscitore";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detect);
        gridView=findViewById(R.id.grid);
        ImgViewFotoProfilo = findViewById(R.id.imgFace);

        Intent i = getIntent();
        final String[] uriProfilo = {i.getStringExtra("uri")};

        Picasso.get().load(uriProfilo[0]).into( new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                Picasso.get().load(uriProfilo[0]).into(ImgViewFotoProfilo);
                final Bitmap BitMapFotoDelProfilo = ((BitmapDrawable)ImgViewFotoProfilo.getDrawable()).getBitmap();

                DatabaseReference myRef;
                FirebaseDatabase db  = FirebaseDatabase.getInstance();
                myRef  = db.getReference("photos");

                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        arrayofuri.clear();
                        arrayofuri.add(uriProfilo[0]);
                        for(DataSnapshot ds: snapshot.getChildren()){
                            final Post els = ds.getValue(Post.class);
                            arrayofuri.add(els.getUrl_foto());
                        }

                        /** faccio partire l'intent che attiva il servizio di riconoscimento *
                         *
                         */
                        Intent i2 = new Intent(DetectorActivity.this, UriToBitmapService.class);
                        i2.setAction(FILTER_ACTION_KEY);
                        i2.putStringArrayListExtra("arrayurifoto",arrayofuri);
                        startService(i2);

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

            }
            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });

        if(ImgViewFotoProfilo.getDrawable() == null) Log.e("null","null");
    }

    @Override
    protected void onStart() {
        setReceivers();

        super.onStart();
    }

    @Override
    protected void onStop() {
        if(myReceiver != null) unregisterReceiver(myReceiver);
        super.onStop();
    }

    private void setReceivers() {
        myReceiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(FILTER_ACTION_KEY);
        IntentFilter intentFilter2 = new IntentFilter();
        intentFilter.addAction(FILTER_ACTION_KEY2);

        registerReceiver(myReceiver, intentFilter);
        registerReceiver(myReceiver,intentFilter2);
    }

    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {


            if(intent.getAction().equalsIgnoreCase("convertitore")){
                ArrayList<String> paths;

                paths = intent.getStringArrayListExtra("arrayFiles");
                Log.e("ricevo dalla conversione",String.valueOf(paths.size()));

                Intent i3 = new Intent(DetectorActivity.this, RecognizerService.class);
                i3.setAction(FILTER_ACTION_KEY2);

                i3.putStringArrayListExtra("paths",paths);
                startService(i3);
            }else if (intent.getAction().equalsIgnoreCase("Riconoscitore")){
                ArrayList<String> Filteredpaths = intent.getStringArrayListExtra("arrayFilesFiltered");
                ArrayList<String> FilteredDistance = intent.getStringArrayListExtra( "arrayDistanceFiltered");


                ArrayList<Bitmap> arrayFilteredBitmap = new ArrayList<>();
                for(String path: Filteredpaths){

                    Bitmap bitmap = BitmapFactory.decodeFile(path);
                    arrayFilteredBitmap.add(bitmap);
                }

                CustumAdapterRecognizedPhotos customAdapter=new CustumAdapterRecognizedPhotos(arrayFilteredBitmap,FilteredDistance,getApplicationContext());
                gridView.setAdapter(customAdapter);

            }
        }
    }

/*

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }*/

    @Override
    public void onBackPressed() {
        Log.e("vengo","vengo chiamato");

        stopService(new Intent(this, RecognizerService.class));

        super.onBackPressed();


    }
}