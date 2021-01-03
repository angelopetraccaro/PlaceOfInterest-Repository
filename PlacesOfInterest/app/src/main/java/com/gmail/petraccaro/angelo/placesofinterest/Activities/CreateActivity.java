package com.gmail.petraccaro.angelo.placesofinterest.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.gmail.petraccaro.angelo.placesofinterest.Controllers.ContractPhoto;
import com.gmail.petraccaro.angelo.placesofinterest.Controllers.ControllerPhoto;
import com.gmail.petraccaro.angelo.placesofinterest.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Classe che permette l'inserimento di una nuova foto con relativi campi
 */
public class CreateActivity extends AppCompatActivity implements ContractPhoto {
    private static final long POLLING_FREQ = 1000 * 10; // per coordin
    private static final float MIN_DISTANCE = 10.0f;    //per coordin
    private static int RESULT_LOAD_IMAGE = 1;   //per chiamare la galleria
    private ImageView photoTakenImageView;
    private ImageButton takefoto, galleria;
    private Switch pub, pri;
    private TextView  didascalia, coord,coordi;
    private Button add;
    private EditText nom, b_desc;
    private boolean available = true;
    private Location mLastReading;
    private LocationManager mLocationManager;
    private LocationListener mLocationListener;
    private double latitudine,longitudine;
    private Uri uriFoto;
    private LinearLayout gallery;
    private LayoutInflater inflater;
    private ControllerPhoto cp;
    View x;

    /**
     * Il metodo crea la CreateActivity, inizializzando tutti i campi necessari.
     * Dopodichè recupera la uri di firestone per aggiungere le foto caricate.
     * Poi si accupa di recuperare le coordinate gps da assegnare alla foto.
     * Infine si occupa del ripristino dello stato se lo smartphone va in landscape
     * @param savedInstanceState, variabile che memorizza lo stato precedente
     */
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cp = ControllerPhoto.getInstance();
        cp.SetContext((ContractPhoto) this);

        setContentView(R.layout.activity_create);
        takefoto = (ImageButton) findViewById(R.id.take);
        galleria = (ImageButton) findViewById(R.id.apri);
        pub = (Switch) findViewById(R.id.pubblico);
        pri = (Switch) findViewById(R.id.privato);
        coord = (TextView) findViewById(R.id.text_cord);
        add = (Button) findViewById(R.id.imageButton);
        coordi =(TextView) findViewById(R.id.edit_cord);
        nom = (EditText) findViewById(R.id.NomeText);
        b_desc = (EditText) findViewById(R.id.b_desc);
        didascalia = (TextView) findViewById(R.id.Didascalia);



        gallery=findViewById(R.id.gallery);
        inflater=LayoutInflater.from(this);
        add.setEnabled(true);

        x = inflater.inflate(R.layout.item_immage_create, gallery, false);
        photoTakenImageView = x.findViewById(R.id.item);

        takefoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cp.takePhoto(v);
            }
        });
        pub.setChecked(true);
        pub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pub.isChecked()== false) {
                    pri.setChecked(true);
                    pri.setEnabled(true);
                    Toast.makeText(CreateActivity.this, "Post privato", Toast.LENGTH_SHORT).show();
                } else {
                    pub.setEnabled(true);
                }
            }
        });


        galleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cp.takePhotoFromGallery();
            }
        });

        //per il gps
        // Acquire reference to the LocationManager
        if (null == (mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE)))
            finish();

        // Display last reading information
        // Update display e,all'occorrenza chiama l'address service per costruire l'indirizzo
        if (null != mLastReading) {
            latitudine=mLastReading.getLatitude();
            longitudine=mLastReading.getLongitude();
            String concatena="Latitude:"+latitudine+", Longitude:"+longitudine;
            coordi.setText(concatena);
        } else {
            coordi.setText("No Initial Location Available");
        }

        mLocationListener = new LocationListener() {
            // Called back when location changes
            public void onLocationChanged(Location location) {
                mLastReading = bestKnownLocation();
                latitudine=mLastReading.getLatitude();
                longitudine=mLastReading.getLongitude();
                //String concatena="Latitude:"+latitudine+", Longitude:"+longitudine;
                Geocoder geo = new Geocoder(CreateActivity.this, Locale.getDefault());
               // Double lat = Double.parseDouble(lat);
                //  Double lon = Double.parseDouble(lg);
                try {
                    List<Address> addresses = geo.getFromLocation(latitudine,longitudine,1);
                    coordi.setText(addresses.get(0).getLocality());

                } catch (IOException e) {
                    e.printStackTrace();
                }

                //coordi.setText(concatena);
            }
            public void onStatusChanged(String provider, int status, Bundle extras) {
                // NA
            }
            public void onProviderEnabled(String provider) {
                // NA
            }
            public void onProviderDisabled(String provider) {
                // NA
            }
        };

        if(savedInstanceState!=null) {
            gallery.removeAllViews();
            if(savedInstanceState.getString("uri")!=null){
                Uri u = Uri.parse(savedInstanceState.getString("uri"));
                Picasso.get().load(u).fit().centerInside().into(photoTakenImageView);
                uriFoto = u;
            }

            gallery.addView(x);
            if(uriFoto !=null) { //alla 1 foto inserita disablito i bottoni e se faccio landscape si disattivano cmq
                takefoto.setEnabled(false);
                galleria.setEnabled(false);
            }// per evitare di aspettare per riprendere la posizione
            latitudine=savedInstanceState.getDouble("la");
            longitudine=savedInstanceState.getDouble("lo");
        }
    }

    @Override //usate per gps
    protected void onResume() {
        super.onResume();
        //registro 2 listener, che mi aggiornano per tempo, ecc
        // Register for network location updates
        if (null != mLocationManager.getProvider(LocationManager.NETWORK_PROVIDER)) {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, POLLING_FREQ,
                    MIN_DISTANCE, mLocationListener);
        }

        // Register for GPS location updates
        //listener se cambia il GPS
        if (null != mLocationManager.getProvider(LocationManager.GPS_PROVIDER)) {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, POLLING_FREQ,
                    MIN_DISTANCE, mLocationListener);
        }
    }

    // Unregister location listeners , usate per gps, chiudo il manager
    @Override
    protected void onPause() {
        super.onPause();
        mLocationManager.removeUpdates(mLocationListener);
        Log.i("angelo", "ACTIVITY MESSA IN PAUSA...onPause");
    }

    // Get the last known location from all providers
    // return best reading
    private Location bestKnownLocation() {
        Location bestResult = null;
        float bestAccuracy = Float.MAX_VALUE;

        List<String> matchingProviders = mLocationManager.getAllProviders();
        for (String provider : matchingProviders) {
            Location location = mLocationManager.getLastKnownLocation(provider);
            if (location != null) {
                float accuracy = location.getAccuracy();
                if (accuracy < bestAccuracy) {
                    bestResult = location;
                    bestAccuracy = accuracy;
                }
            }
        }
        return bestResult;
    }



    /**
     * Il metodo viene richiamato quando l'intent viene evaso
     * @param requestCode gestisce il tipo di intent lanciato
     * @param resultCode esito del risultato
     * @param data dati restituiti
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Pair<Uri, Bitmap> pair = cp.onActivityresult(requestCode,resultCode,data);

        if(pair.second != null && pair.first != null){
            gallery.addView(x);

            uriFoto = pair.first;
            photoTakenImageView.setImageBitmap(pair.second);


             addOnStorage(pair.first);
        }


    }

    /**
     * Carica la foto sullo storage di firebase
     * @param outputUri uri della foto da salvare su firebase storage
     */
    public void addOnStorage(final Uri outputUri){
        Intent inUs = getIntent();
        final String userName = inUs.getStringExtra("username");

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String breve = b_desc.getText().toString().trim();
                final String nm = nom.getText().toString().trim();
                final String ds = didascalia.getText().toString().trim();


                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                final FirebaseUser currentUser = mAuth.getCurrentUser();
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    FirebaseStorage myStorage = FirebaseStorage.getInstance();
                    StorageReference rootStorageRef = myStorage.getReference();
                    StorageReference documentRef = rootStorageRef.child("images");
                    final DatabaseReference myRefToDb = database.getReference("photos");

                    Uri uri= outputUri;
                if( !TextUtils.isEmpty(breve) && !TextUtils.isEmpty(nm) && !TextUtils.isEmpty(ds) ) {
                    add.setEnabled(false);
                    if(pub.isChecked()) available = true;
                    if(pri.isChecked()) available = false;
                    cp.addOnStorage(uriFoto,nm,breve,ds,available,latitudine,longitudine,userName);

                }else{
                    if(uriFoto == null)
                        Toast.makeText(CreateActivity.this, "Attenzione,scegli o scatta una foto", Toast.LENGTH_SHORT).show();
                    if(TextUtils.isEmpty(b_desc.getText()))
                        b_desc.setError("Attenzione, aggiungi una descrizione!");
                    if(TextUtils.isEmpty(nm))
                        nom.setError("Attenzione, aggiungi un nome!");
                    if(TextUtils.isEmpty(ds))
                        didascalia.setError("Attenzione, aggiungi una didascalia!");

                }
            }
        });



    }

    /**
     * Memorizza lo stato da ripristinare se lo smartphone va in landscape
     * @param b stato da ripristinare
     */
    protected void onSaveInstanceState(Bundle b){
        super.onSaveInstanceState(b);

        if(uriFoto != null && latitudine != 0.0) {
            b.putString("uri", uriFoto.toString());
            b.putDouble("la", latitudine);
            b.putDouble("lo", longitudine);
        }
    }

    /**
     * Utilizzato perchè, tornando indietro senza crere luoghi, resetta le variabili
     */
    public void onBackPressed(){
        latitudine=0.0;
        longitudine=0.0;
        super.onBackPressed();
    }

    @Override
    public void OnSuccess(Object obj) {
        latitudine = 0.0;
        longitudine = 0.0;
        add.setEnabled(true);
        Intent i = new Intent(CreateActivity.this, MainActivity.class);
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK,returnIntent);
        finish();


    }

    @Override
    public void OnError(String message) {

    }
}