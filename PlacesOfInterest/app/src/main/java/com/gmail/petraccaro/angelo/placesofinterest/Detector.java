package com.gmail.petraccaro.angelo.placesofinterest;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import java.util.ArrayList;


public class Detector extends AppCompatActivity {
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
                            final ElementoLista els = ds.getValue(ElementoLista.class);
                            arrayofuri.add(els.getUrl_foto());
                        }

                        /** faccio partire l'intent che attiva il servizio di riconoscimento *
                         *
                         */
                        Intent i2 = new Intent(Detector.this, ConvertitoreUriToBitmap.class);
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
        unregisterReceiver(myReceiver);
        CustomAdapter1 adapter = (CustomAdapter1) gridView.getAdapter();
        adapter.clearData();

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

                Intent i3 = new Intent(Detector.this, Riconoscitore.class);
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

                CustomAdapter1 customAdapter=new CustomAdapter1(arrayFilteredBitmap,FilteredDistance,getApplicationContext());
                gridView.setAdapter(customAdapter);

            }
        }
    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    public class CustomAdapter1 extends BaseAdapter {

        private Context context;
        private LayoutInflater layoutInflater;
        private ArrayList<Bitmap> bitmap;
        private ArrayList<String> distanza;

        public CustomAdapter1(ArrayList<Bitmap> bitmap, ArrayList<String> distanza, Context applicationContext) {
            this.bitmap=bitmap;
            this.distanza=distanza;
            this.context=applicationContext;
            this.layoutInflater = (LayoutInflater) applicationContext.getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return distanza.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {

            if(view == null){
                view=layoutInflater.inflate(R.layout.row_item,viewGroup,false);
            }

            TextView textGrid = view.findViewById(R.id.textGrid);
            ImageView imageGrid = view.findViewById(R.id.imageGrid);

            textGrid.setText("Distanza: "+distanza.get(position));
            imageGrid.setImageBitmap(bitmap.get(position));

            return view;
        }


        public void clearData(){
            this.bitmap.clear();
            this.distanza.clear();
        }
    }
}