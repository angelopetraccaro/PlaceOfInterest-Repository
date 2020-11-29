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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
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
import java.util.Map;

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
    private TextView nome,breve,desc,itemtext,itemtext1,itemtext2;
    private EditText nom,bre,latitude,longitude;
    private boolean available=true;
    private static int i=0;
    private CircleImageView photoTakenImageView,photoTakenImageView1,photoTakenImageView2;
    private Uri outputUri;
    private FloatingActionButton fab;
    private String userLog,nome_doc;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private static int y,z;
    private ArrayList<String> uri_foto_iniziali=new ArrayList<>();
    private LinearLayout gallery;
    private LayoutInflater inflater;
    private View x;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        y=0;
        z=0;
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
        db= FirebaseFirestore.getInstance();
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
        String urlFoto=intent.getStringExtra("foto");
        String urlFoto1=intent.getStringExtra("foto1");
        String urlFoto2=intent.getStringExtra("foto2");
        uri_foto_iniziali.add(urlFoto);
        uri_foto_iniziali.add(urlFoto1);
        uri_foto_iniziali.add(urlFoto2);
        userLog=intent.getStringExtra("owner");      //usato per vedere se l'utente può modificare o no
        nome_doc=intent.getStringExtra("nome_doc");
        photoTakenImageView = x.findViewById(R.id.item);
        photoTakenImageView1 = x.findViewById(R.id.item1);
        photoTakenImageView2 = x.findViewById(R.id.item2);
        itemtext = x.findViewById(R.id.itemtext);

        itemtext.setText(intent.getStringExtra("didascalia"));

        //picasso per passare da uri a img
        Uri myUri=Uri.parse(uri_foto_iniziali.get(0));

        Picasso.get().load(myUri).into(photoTakenImageView);

        gallery.addView(x);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //in questo caso (arraylist string) uri perchè non cambio la foto
                CollectionReference luoghi = db.collection("luoghi");
                Map<String, Object> utente1 = new HashMap<>();

                if(userLog.equalsIgnoreCase(currentUser.getUid())) {
                    utente1.put("owner", (currentUser != null) ? currentUser.getUid() : null);
                    utente1.put("Nome", nom.getText().toString());
                    utente1.put("Breve_desc", bre.getText().toString());
                    utente1.put("Lunga_desc", desc.getText().toString());
                    utente1.put("latitude", latitude.getText().toString());
                    utente1.put("longitude", longitude.getText().toString());
                    utente1.put("foto", uri_foto_iniziali.get(0));

                    utente1.put("didascalia",itemtext.getText()+"");

                    if (pub.isChecked())
                        available = true;
                    if (pri.isChecked())
                        available = false;
                    utente1.put("Available", available);
                    luoghi.document(nome_doc).set(utente1);

                    Intent in = new Intent(ReadActivity.this, MainActivity.class);
                    startActivity(in);
                }else{
                    Toast.makeText(getBaseContext(),"You are not the owner",Toast.LENGTH_LONG).show();
                }
                y=0;z=0;
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
        Log.i(TAG, "CREAZIONE ACTIVITY...onCreate");

        if (savedInstanceState != null){ //si ricorda la foto caricata
            y=savedInstanceState.getInt("num");
            z=savedInstanceState.getInt("num1");
            itemtext.setText(savedInstanceState.getString("t"));
            itemtext1.setText(savedInstanceState.getString("t1"));
            itemtext2.setText(savedInstanceState.getString("t2"));
            gallery.removeAllViews();
            uri_foto_iniziali.set(0,savedInstanceState.getString("u"));

            Uri myUrix=Uri.parse(uri_foto_iniziali.get(0));

            Picasso.get().load(myUrix).into(photoTakenImageView);

            gallery.addView(x);
           //disab bottoni
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
            if(y==0){
                photoTakenImageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                gallery.addView(x);
            }
            if(y==1){
                photoTakenImageView1.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                gallery.addView(x);
            }
            if(y==2){
                photoTakenImageView2.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                gallery.addView(x);
            }
            y++;
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
                } catch (IOException e) {
                    Log.e("uri",Log.getStackTraceString(e));
                }
            }
            if(y==1){
                try {
                    mImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), outputUri);
                    if(mImageBitmap!=null){
                        gallery.removeAllViews();
                        photoTakenImageView1.setImageBitmap(mImageBitmap);
                        gallery.addView(x);
                        galleryAddPic();
                    }
                } catch (IOException e) {
                    Log.e("uri",Log.getStackTraceString(e));
                }
            }
            if(y==2){
                try {
                    mImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), outputUri);
                    if(mImageBitmap!=null){
                        gallery.removeAllViews();
                        photoTakenImageView2.setImageBitmap(mImageBitmap);
                        gallery.addView(x);
                        galleryAddPic();
                    }
                } catch (IOException e) {
                    Log.e("uri",Log.getStackTraceString(e));
                }
            }
            y++;
            addOnStorage(outputUri);
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
                if(y-1==0)
                    itemtext.setText(e.getText());
                if(y-1==1)
                    itemtext1.setText(e.getText());
                if(y-1==2)
                    itemtext2.setText(e.getText());
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
                        //array.add(uri); se arraylist di uri.
                        if(z==0){
                            uri_foto_iniziali.remove(z);
                            uri_foto_iniziali.add(z,uri.toString());
                        }
                       else if(z==1){
                            uri_foto_iniziali.remove(z);
                            uri_foto_iniziali.add(z,uri.toString());
                        }
                       else if(z==2){
                            uri_foto_iniziali.remove(z);
                            uri_foto_iniziali.add(z,uri.toString());
                        }
                        z++;
                    }
                });
                fab.setEnabled(true);
                if(y-1==2) {
                    takefoto.setEnabled(false);
                    galleria.setEnabled(false);
                }else{
                takefoto.setEnabled(true);
                galleria.setEnabled(true);
                }
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                //Toast.makeText(getApplicationContext(), "On progress...", Toast.LENGTH_LONG).show();
                fab.setEnabled(false);
                takefoto.setEnabled(false);
                galleria.setEnabled(false);
            }
        });
    }

    /**
     * Memorizza lo stato da ripristinare se lo smartphone va in landscape
     * @param b stato da ripristinare
     */
    protected void onSaveInstanceState(Bundle b) {
        super.onSaveInstanceState(b);
        b.putString("u", uri_foto_iniziali.get(0));

        b.putInt("num",y);
        b.putInt("num1",z);
        b.putString("t",itemtext.getText().toString());

    }
}