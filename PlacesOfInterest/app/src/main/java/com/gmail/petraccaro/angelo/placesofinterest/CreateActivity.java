package com.gmail.petraccaro.angelo.placesofinterest;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Classe che permette l'inserimento di una nuova foto con relativi campi
 */
public class CreateActivity extends AppCompatActivity {
    private static final long POLLING_FREQ = 1000 * 10; // per coordin
    private static final float MIN_DISTANCE = 10.0f;    //per coordin
    private static int RESULT_LOAD_IMAGE = 1;   //per chiamare la galleria
    private static final int ACTIVITY_START_CAMERA_APP = 0; //per chiamare la fotocamera
    private Uri outputUri;
    private CircleImageView photoTakenImageView;
    private Button takefoto, galleria;
    private CheckBox pub, pri;
    private TextView nome, breve, desc, coord,itemtext;
    private ImageButton add;
    private EditText nom, bre, coordi;
    private boolean available = true;
    // Current best location estimate
    private Location mLastReading;
    // Reference to the LocationManager and LocationListener
    private LocationManager mLocationManager;
    private LocationListener mLocationListener;
    private FirebaseFirestore db;
    private static int max=0;
    private static int y=0;
    private double latitudine,longitudine;
    private String uriFoto;
    private ArrayList<Uri> array=new ArrayList<>();
    private LinearLayout gallery;
    private LayoutInflater inflater;
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

        takefoto = (Button) findViewById(R.id.take);
        galleria = (Button) findViewById(R.id.apri);
        pub = (CheckBox) findViewById(R.id.pubblico);
        pri = (CheckBox) findViewById(R.id.privato);
        nome = (TextView) findViewById(R.id.nome);
        breve = (TextView) findViewById(R.id.breve_desc);
        desc = (TextView) findViewById(R.id.desc);
        coord = (TextView) findViewById(R.id.text_cord);
        nom = (EditText) findViewById(R.id.nom);
        bre = (EditText) findViewById(R.id.b_desc);
        add = (ImageButton) findViewById(R.id.imageButton);
        coordi =(EditText) findViewById(R.id.edit_cord);
        gallery=findViewById(R.id.gallery);
        inflater=LayoutInflater.from(this);

        x = inflater.inflate(R.layout.item_immagine, gallery, false);
        photoTakenImageView = x.findViewById(R.id.item);

        itemtext = x.findViewById(R.id.itemtext);
        db = FirebaseFirestore.getInstance();

        db.collection("luoghi").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                    String a = document.getId().substring(5);
                                    if (Integer.parseInt(a) > max)
                                        max = Integer.parseInt(a);
                            }
                        }
                    }
                });

        takefoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto(v);
            }
        });

        pub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pub.isChecked()) {
                    pri.setEnabled(false);
                } else {
                    pri.setEnabled(true);
                }
            }
        });
        pri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pri.isChecked()) {
                    pub.setEnabled(false);
                } else {
                    pub.setEnabled(true);
                }
            }
        });

        galleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                max++;
                String uriFoto="";
                if(array.size()>=1)
                    uriFoto = array.get(0).toString();

                String IdUser = "luogo" + max;

                CollectionReference luoghi = db.collection("luoghi");
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = mAuth.getCurrentUser();
               Map<String, Object> utente1 = new HashMap<>();



                utente1.put("owner", (currentUser != null) ? currentUser.getUid() : null);
                utente1.put("Nome", nom.getText().toString());
                utente1.put("Breve_desc", bre.getText().toString());
                utente1.put("Lunga_desc", desc.getText().toString());
                //mettere if se latitudine è 0.0 caricare in firebase null
                if(latitudine==0.0 && longitudine==0.0) {
                    utente1.put("latitude", null);
                    utente1.put("longitude",null);
                }else{
                    utente1.put("latitude", latitudine+"");  //+"" perche in firebase sono stringhe
                    utente1.put("longitude",longitudine+"");
                }
                utente1.put("foto", uriFoto);

                utente1.put("didascalia",itemtext.getText()+"");

                if (pub.isChecked())
                    available = true;
                if (pri.isChecked())
                    available = false;
                utente1.put("Available", available);
                luoghi.document(IdUser).set(utente1);

               // ElementoLista el = new ElementoLista(nom.getText().toString(),bre.getText().toString(),desc.getText().toString(),
                //        latitudine, longitudine,uriFoto,itemtext.getText().toString(),"ciap",  (currentUser != null) ? currentUser.getUid() : null);

                Log.e("urifotod", uriFoto);
                Intent i = new Intent(CreateActivity.this, MainActivity.class);
                startActivity(i);
                y=0;
                latitudine=0.0;
                longitudine=0.0;
                finish();
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
                String concatena="Latitude:"+latitudine+", Longitude:"+longitudine;
                coordi.setText(concatena);
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
            itemtext.setText(savedInstanceState.getString("t"));

            y=savedInstanceState.getInt("num");
            gallery.removeAllViews();
            if(savedInstanceState.getString("uri")!=null){
                Uri u = Uri.parse(savedInstanceState.getString("uri"));
                Picasso.get().load(u).fit().centerInside().into(photoTakenImageView);
                array.add(0,u);
            }

            gallery.addView(x);
            if(array.size()>=1) { //alla 1 foto inserita disablito i bottoni e se faccio landscape si disattivano cmq
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
            Log.e("dat",data.getData()+"");
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
            if(y==0){
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
    public void addOnStorage(Uri outputUri){
        //messo qui l'alert invece che sotto a y++, elimino errore distrazione
        AlertDialog.Builder didascalia=new AlertDialog.Builder(this);
        final EditText e=new EditText(this);
        didascalia.setTitle("Inserisci la didascalia");
        didascalia.setView(e);
        didascalia.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                    itemtext.setText(e.getText());

            }
        });
        didascalia.show();
        //dopo aver salvato l'imm nella imageview la salvo nello storage di firestone
        FirebaseStorage myStorage = FirebaseStorage.getInstance();
        StorageReference rootStorageRef = myStorage.getReference();
        StorageReference documentRef = rootStorageRef.child("images");
        Uri uri= outputUri;

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
                        //scarico la url da storage firestone, per passarla poi al database firestone per metterla nel campo foto
                        array.add(uri);
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
    }

    /**
     * Memorizza lo stato da ripristinare se lo smartphone va in landscape
     * @param b stato da ripristinare
     */
    protected void onSaveInstanceState(Bundle b){
        super.onSaveInstanceState(b);
        if(array.size()>0) {
            b.putString("uri", array.get(0).toString());


        }
        b.putInt("num",y);
        b.putDouble("la",latitudine);
        b.putDouble("lo",longitudine);
        b.putString("t",itemtext.getText().toString());

    }

    /**
     * Utilizzato perchè, tornando indietro senza crere luoghi, resetta le variabili
     */
    public void onBackPressed(){
        y=0;
        latitudine=0.0;
        longitudine=0.0;
        super.onBackPressed();
    }
}