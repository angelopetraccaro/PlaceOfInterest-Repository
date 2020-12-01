package com.gmail.petraccaro.angelo.placesofinterest;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Visualizza la/le foto (max 3) ed i relativi parametri;
 * Permette di visualizzare la posizione aprendo la mappa;
 * Permette di sostituire le foto caricate e/o pubblicare
 * nella parte pubblica la foto (se questa risiede in privato) e viceversa.
 * Inoltre permette la modifica dei campi dell'elemento.
 */
public class ReadActivity extends AppCompatActivity {
    private final String TAG = "lifecycle";
    private static int RESULT_LOAD_IMAGE = 1;   //per chiamare la galleria
    private static final int ACTIVITY_START_CAMERA_APP = 0; //per chiamare la fotocamera
    private ImageButton takefoto,galleria,gps;
    private CheckBox pub,pri;
    private TextView nome,breve,desc,itemtext;
    private EditText nom,bre,latitude,longitude;
    private boolean available=true;
    private static int i=0;
    private CircleImageView photoTakenImageView,photoTakenImageView1,photoTakenImageView2;
    private Uri outputUri;
    private FloatingActionButton fab;
    private String userLog,nome_doc;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private String uriFoto;
    private String keyOfSelectElement;
    private LinearLayout gallery;
    private LayoutInflater inflater;
    private View x;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);
        takefoto = (ImageButton) findViewById(R.id.scatta);
        galleria=(ImageButton) findViewById(R.id.gallery_b);
        gps=(ImageButton) findViewById(R.id.gps);
        pub=(CheckBox) findViewById(R.id.pubblico);
        pri=(CheckBox) findViewById(R.id.privato);
        nome=(TextView) findViewById(R.id.nome);
        breve=(TextView) findViewById(R.id.breve_desc);
        desc=(TextView) findViewById(R.id.desc);
        latitude=(EditText) findViewById(R.id.text_cord);
        longitude=(EditText) findViewById(R.id.edit_cord);
        nom=(EditText) findViewById(R.id.nom);
        bre=(EditText) findViewById(R.id.b_desc);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        gallery=findViewById(R.id.gallery);
        inflater=LayoutInflater.from(this);
        x = inflater.inflate(R.layout.item_immagine, gallery, false);

        final Intent intent=getIntent();
        nom.setText(intent.getStringExtra("name"));
        bre.setText(intent.getStringExtra("b_desc"));
        desc.setText(intent.getStringExtra("desc"));
        latitude.setText(intent.getStringExtra("latitude"));
        longitude.setText(intent.getStringExtra("longitude"));
        uriFoto=intent.getStringExtra("foto");

        userLog=intent.getStringExtra("owner");      //usato per vedere se l'utente può modificare o no
        nome_doc=intent.getStringExtra("nome_doc");
        photoTakenImageView = x.findViewById(R.id.item);

        itemtext = x.findViewById(R.id.itemtext);


        itemtext.setText(intent.getStringExtra("didascalia"));
        keyOfSelectElement = intent.getStringExtra("keyondb");

        //picasso per passare da uri a img
        Uri myUri=Uri.parse(uriFoto);

        Picasso.get().load(myUri).into(photoTakenImageView);

        gallery.addView(x);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase mydb = FirebaseDatabase.getInstance();
                DatabaseReference dbref = mydb.getReference();

                if(userLog.equalsIgnoreCase(currentUser.getUid())) {
                    if (pub.isChecked())
                        available = true;
                    if (pri.isChecked())
                        available = false;
                    ElementoLista el = new ElementoLista(nom.getText().toString(),bre.getText().toString(),desc.getText().toString(),
                            latitude.getText().toString(), longitude.getText().toString(),uriFoto,
                            itemtext.getText().toString(),  (currentUser != null) ? currentUser.getUid() : null,keyOfSelectElement, available);

                    dbref.child("photos").child(keyOfSelectElement).setValue(el);
                    Intent in = new Intent(ReadActivity.this, MainActivity.class);
                    startActivity(in);
                }else{
                    Toast.makeText(getBaseContext(),"You are not the owner",Toast.LENGTH_LONG).show();
                }
                finish();
            }
        });

        pub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pub.isChecked()) {
                    pri.setEnabled(false);
                }else{
                    pri.setEnabled(true);
                }
            }
        });
        pri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pri.isChecked()) {
                    pub.setEnabled(false);
                }else{
                    pub.setEnabled(true);
                }
            }
        });

        //copia dei metodi di create activity, usati per modificare l'elemento
        takefoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto(v);
            }
        });

       galleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });

        gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!(latitude.getText().toString().isEmpty()&&longitude.getText().toString().isEmpty())) {
                    Intent inte = new Intent(ReadActivity.this, ShowMapActivity.class);
                    Double latitu = Double.parseDouble(latitude.getText().toString());
                    Double longitu = Double.parseDouble(longitude.getText().toString());
                    inte.putExtra("Latitude", latitu);
                    inte.putExtra("Longitude", longitu);
                    startActivity(inte);
                }else
                    Toast.makeText(ReadActivity.this,"Latitudine e Longitudine Assenti",Toast.LENGTH_LONG).show();
            }
        });
        /** reaload foto **/
        if (savedInstanceState != null){

            itemtext.setText(savedInstanceState.getString("descrizione"));
            gallery.removeAllViews();
            uriFoto =savedInstanceState.getString("Uri");
            Uri ConvertedUri =Uri.parse(uriFoto);

            Picasso.get().load(ConvertedUri).into(photoTakenImageView);

            gallery.addView(x);
        }
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
           // fare il caricamento quando serve
          //  addOnStorage(selectedImage);
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
                } catch (IOException e) {
                    Log.e("uri",Log.getStackTraceString(e));
                }

         //   addOnStorage(outputUri);
        }
    }

    /**
     * Creazione di un file per salvare l'immagine
     * Funzione che specifica la locazione ed il nome del file che vogliamo creare
     * @return il nuovo file path
     */
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmSS").format(new Date());
        String imageFileName = "IMAGE_" + timeStamp+".jpg";
        File externalDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
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
        //messo qui l'alert cosi anche se capita di cliccare la foto e nn salvarla nn appare la didascalia
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
                            uriFoto = uri.toString();
                    }
                });
                fab.setEnabled(true);
                takefoto.setEnabled(false);
                galleria.setEnabled(false);

            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                fab.setEnabled(false);
                takefoto.setEnabled(false);
                galleria.setEnabled(false);
            }
        });
    }

    /**
     * Memorizza lo stato da ripristinare se lo smartphone va in landscape
     * @param b bundle che incapsula lo stato corrente
     */
    protected void onSaveInstanceState(Bundle b) {
        super.onSaveInstanceState(b);
        b.putString("Uri", uriFoto);
        b.putString("descrizione",itemtext.getText().toString());

    }
}