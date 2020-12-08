package com.gmail.petraccaro.angelo.placesofinterest;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterActivity extends AppCompatActivity {
    private static int RESULT_LOAD_IMAGE = 1;   //per chiamare la galleria
    private static final int ACTIVITY_START_CAMERA_APP = 0; //per chiamare la fotocamera
    private EditText nome,cognome,email,password,username;
    private Button btn_registrati;
    private ImageButton imgbtnGallery;
    private ImageButton imgbtnFotocamera;
    private CircleImageView imgview;
    private FirebaseFirestore myDB = FirebaseFirestore.getInstance();
    Uri uri= null;
    private  Uri outputUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        TextView txt = findViewById(R.id.account);
        txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
            }
        });

        nome = (EditText)findViewById(R.id.nome);
        cognome = (EditText)findViewById(R.id.cognome);
        email = (EditText)findViewById(R.id.email1);
        password = (EditText)findViewById(R.id.password1);
        username = (EditText)findViewById(R.id.username);
        btn_registrati = (Button) findViewById(R.id.btnRegistrati);
        imgbtnGallery = (ImageButton) findViewById(R.id.galleria);
        imgbtnFotocamera = (ImageButton) findViewById(R.id.Scattafoto);
        final AlertDialog.Builder builder = new AlertDialog.Builder(getBaseContext());
        imgview = (CircleImageView) findViewById(R.id.circleImageView);
        builder.setTitle(R.string.dialog_title);
        btn_registrati.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String str_nome=nome.getText().toString();
                final String str_cognome=cognome.getText().toString();
                final String str_email=email.getText().toString();
                final String str_password=password.getText().toString();
                final String str_user=username.getText().toString();
                if (TextUtils.isEmpty(str_nome)) {
                    nome.setError(getString(R.string.error_field_required));
                }
                else if (TextUtils.isEmpty(str_cognome))
                    cognome.setError(getString(R.string.error_field_required));

                else  if (TextUtils.isEmpty(str_email))
                    email.setError(getString(R.string.error_field_required));

                else  if (TextUtils.isEmpty(str_password))
                    password.setError(getString(R.string.error_field_required));

                else if (TextUtils.isEmpty(str_user))
                    username.setError(getString(R.string.error_field_required));

                else if(!isPasswordValid(str_password))
                    password.setError(getString(R.string.error_invalid_password));

                else if(!isPasswordValid(str_user))
                    username.setError(getString(R.string.error_invalid_password));

                else if (!isEmailValid(str_email))
                    email.setError(getString(R.string.error_invalid_email));
                else if( uri == null)
                    Toast.makeText(RegisterActivity.this, R.string.FotoNonAggiunta, Toast.LENGTH_LONG).show();

                else{
                    FirebaseStorage myStorage = FirebaseStorage.getInstance();
                    StorageReference rootStorageRef = myStorage.getReference();
                    final StorageReference documentRef = rootStorageRef.child("ProfileImages");
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
                            scoreRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(final Uri uri) {
                                    myDB.collection("users")
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    boolean  presente = false;

                                                    if(task.isSuccessful()){
                                                        for(QueryDocumentSnapshot qds: task.getResult()){
                                                           User usr= qds.toObject(User.class);
                                                            if(usr.getEmail().equalsIgnoreCase(str_email)){
                                                                presente = !presente;
                                                            }
                                                        }

                                                        if(!presente){
                                                            DocumentReference myRef = myDB.collection("users").document(str_email);

                                                            final User user = new User(str_nome,str_cognome,str_user,str_email,str_password, uri.toString() );
                                                            myRef.set(user);
                                                            FirebaseAuth mAuth = FirebaseAuth.getInstance();
                                                            mAuth.signOut();

                                                            mAuth.createUserWithEmailAndPassword(str_email,str_password)
                                                                    .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                                                            if (task.isSuccessful()){

                                                                                Intent i = new Intent(RegisterActivity.this, MainActivity.class);
                                                                                i.putExtra("nome",user.getNome());
                                                                                i.putExtra("cognome",user.getCognome());
                                                                                i.putExtra("username",user.getUsername());
                                                                                i.putExtra("password",user.getPassword());
                                                                                i.putExtra("email",user.getEmail());
                                                                                i.putExtra("uriFotoDelProfilo",user.getUriFotoDelProfilo());
                                                                                startActivity(i);
                                                                            }else{
                                                                                Log.e("cosa succede", task.getException().toString());
                                                                            }


                                                                        }
                                                                    });


                                                        }else{
                                                            Toast.makeText(RegisterActivity.this, R.string.UtenteGiaregistrato, Toast.LENGTH_LONG).show();
                                                            imgbtnGallery.setEnabled(true);
                                                            imgbtnFotocamera.setEnabled(true);
                                                        }

                                                    }

                                                }
                                            });



                                }
                            });

                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            imgbtnGallery.setEnabled(false);
                            imgbtnFotocamera.setEnabled(false);

                        }
                    });
                }


            }
        });

        imgbtnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });

    }



    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            uri = selectedImage;
            imgview.setImageBitmap(BitmapFactory.decodeFile(picturePath));
        }
        if (requestCode == ACTIVITY_START_CAMERA_APP && resultCode == RESULT_OK) {
            Bitmap mImageBitmap = null;

            try {
                mImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), outputUri);
                if(mImageBitmap!=null){
                    imgview.setImageBitmap(mImageBitmap);
               uri = outputUri;
                    galleryAddPic();
                }
            } catch (IOException e) {
                Log.e("uri",Log.getStackTraceString(e));
            }
        }
    }
    private boolean isEmailValid(String email) {
        return email.contains("@");
    }
    private boolean isPasswordValid(String password) {
        return password.length() > 5;
    }

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

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmSS").format(new Date());
        String imageFileName = "IMAGE_" + timeStamp+".jpg";
        File externalDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return new File(externalDir.toString()+"/"+imageFileName);
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(outputUri);
        this.sendBroadcast(mediaScanIntent);
    }

}
