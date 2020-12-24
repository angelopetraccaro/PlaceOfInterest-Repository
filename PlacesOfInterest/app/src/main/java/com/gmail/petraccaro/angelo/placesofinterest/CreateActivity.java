package com.gmail.petraccaro.angelo.placesofinterest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Classe che permette l'inserimento di una nuova foto con relativi campi
 */
public class CreateActivity extends AppCompatActivity {
    private static final long POLLING_FREQ = 1000 * 10; // per coordin
    private static final float MIN_DISTANCE = 10.0f;    //per coordin
    private static int RESULT_LOAD_IMAGE = 1;   //per chiamare la galleria
    private static final int ACTIVITY_START_CAMERA_APP = 0; //per chiamare la fotocamera
    private Uri outputUri;
    private ImageView photoTakenImageView;
    private ImageButton takefoto, galleria;
    private Switch pub, pri;
    private TextView nome, breve, didascalia, coord,coordi;
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
    private String lt ,lg;
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
        setContentView(R.layout.activity_create);
        Intent inUs = getIntent();
        String userName = inUs.getStringExtra("username");


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

        x = inflater.inflate(R.layout.item_immage_create, gallery, false);
        photoTakenImageView = x.findViewById(R.id.item);

        takefoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto(v);
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
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
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
     * Lancia l'intent che richiama la fotocamera dello smartphone
     */
    public void takePhoto(View v) {
        Intent callCameraApplicationIntent = new Intent();
        callCameraApplicationIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        outputUri = FileProvider.getUriForFile(
                this,
                BuildConfig.APPLICATION_ID + ".provider",
                photoFile);
        callCameraApplicationIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
        callCameraApplicationIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(callCameraApplicationIntent, ACTIVITY_START_CAMERA_APP);
    }

    /**
     * Il metodo viene richiamato quando l'intent viene evaso
     * @param requestCode gestisce il tipo di intent lanciato
     * @param resultCode esito del risultato
     * @param data dati restituiti
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //per la galleria in on activity result controllo:
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            gallery.removeAllViews(); //facendo cosi posso anche uscire dalla galleria senza selezionare foto. e incremento y nel metodo
                photoTakenImageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                gallery.addView(x);

            addOnStorage(selectedImage);
        }

        //per la fotocamera
        if (requestCode == ACTIVITY_START_CAMERA_APP && resultCode == RESULT_OK) {
            Bitmap mImageBitmap = null;
                try {
                    mImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), outputUri);
                    if(mImageBitmap!=null){
                        gallery.removeAllViews();
                        photoTakenImageView.setImageBitmap(mImageBitmap);
                        gallery.addView(x);
                        galleryAddPic();
                    }
                } catch (Exception e) {
                    Log.e("uri",Log.getStackTraceString(e));
                }


            addOnStorage(outputUri);
        }

    }

    /**
     * Creazione di un file per salvare l'immagine
     * Funzione che specifica la locazione ed il nome del file che vogliamo creare
     * @return ritorna il nuovo file path
     */
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmSS").format(new Date());
        String imageFileName = "IMAGE_" + timeStamp+".jpg";
        File externalDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        //File externalDir = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        //Log.e("ciao",externalDir.toString()+"/"+imageFileName);
        return new File(externalDir.toString()+"/"+imageFileName);
    }

    /**
     * Trasmissione di uno scanner multimediale con l'intento di far apparire l'immagine nella galleria
     */
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(outputUri);
        this.sendBroadcast(mediaScanIntent);
    }

    /**
     * Carica la foto sullo storage di firebase
     * @param outputUri uri della foto da salvare su firebase storage
     */
    public void addOnStorage(final Uri outputUri){

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

                    final StorageReference scoreRef = documentRef.child(uri.getLastPathSegment());
                    final UploadTask uploadTask = scoreRef.putFile(uri);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Toast.makeText(getApplicationContext(), "Upload Failure", Toast.LENGTH_LONG).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                            Toast.makeText(getApplicationContext(), "File uploaded on storage", Toast.LENGTH_LONG).show();
                            scoreRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    uriFoto = uri;
                                        if (pub.isChecked())
                                            available = true;
                                        if (pri.isChecked())
                                            available = false;

                                        String uploadId = myRefToDb.push().getKey();
                                        ElementoLista el = new ElementoLista( nm,breve,
                                                Double.toString(latitudine), Double.toString(longitudine), uriFoto.toString(),
                                                ds, (currentUser != null) ? currentUser.getUid() : null, uploadId, available);
                                        myRefToDb.child(uploadId).setValue(el);
                                        Intent i = new Intent(CreateActivity.this, MainActivity.class);
                                        Intent returnIntent = new Intent();
                                        setResult(Activity.RESULT_OK,returnIntent);
                                        finish();

                                        latitudine = 0.0;
                                        longitudine = 0.0;


                                }
                            });
                            add.setEnabled(true);

                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //Toast.makeText(getApplicationContext(), "On progress...", Toast.LENGTH_LONG).show();
                            add.setEnabled(false);
                            takefoto.setEnabled(false);
                            galleria.setEnabled(false);
                        }
                    });
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
}