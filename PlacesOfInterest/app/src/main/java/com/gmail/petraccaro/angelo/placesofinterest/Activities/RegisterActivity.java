package com.gmail.petraccaro.angelo.placesofinterest.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.gmail.petraccaro.angelo.placesofinterest.Controllers.Contract;
import com.gmail.petraccaro.angelo.placesofinterest.Controllers.ContractPhoto;
import com.gmail.petraccaro.angelo.placesofinterest.Controllers.ControllerPhoto;
import com.gmail.petraccaro.angelo.placesofinterest.Controllers.ControllerUser;
import com.gmail.petraccaro.angelo.placesofinterest.Models.User;
import com.gmail.petraccaro.angelo.placesofinterest.R;
import com.google.firebase.firestore.FirebaseFirestore;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterActivity extends AppCompatActivity implements Contract, ContractPhoto {
    private static int RESULT_LOAD_IMAGE = 1;   //per chiamare la galleria

    private EditText nome,cognome,email,password,username;
    private Button btn_registrati;
    private ImageButton imgbtnGallery;
    private ImageButton imgbtnFotocamera;
    private CircleImageView imgview;
    private FirebaseFirestore myDB = FirebaseFirestore.getInstance();
    private ControllerUser lw = ControllerUser.getInstance();
    private ControllerPhoto lw2 = ControllerPhoto.getInstance();
    Uri uri= null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        lw.SetContext(RegisterActivity.this);
        lw2.SetContext(RegisterActivity.this);
        TextView txt = findViewById(R.id.account);
        txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
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
                     ControllerUser lw = ControllerUser.getInstance();
                     ControllerPhoto lw2 = ControllerPhoto.getInstance();
                    lw.SetContext(RegisterActivity.this);
                    lw2.SetContext(RegisterActivity.this);
                    lw.register(str_nome,str_cognome,str_email,str_password,str_user,uri);
                   }


            }
        });

        imgbtnFotocamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imgbtnGallery.setEnabled(false);
                imgbtnFotocamera.setEnabled(false);
                lw2.takePhoto(view);
            }
        });
        imgbtnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgbtnGallery.setEnabled(false);
                imgbtnFotocamera.setEnabled(false);
                lw2.takePhotoFromGallery();

            }
        });

    }




    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Pair<Uri, Bitmap> pair =lw2.onActivityresult( requestCode,  resultCode,  data);
        if(pair.second != null && pair.first != null){
            imgview.setImageBitmap(pair.second);
            uri = pair.first;

        }
        imgbtnGallery.setEnabled(true);
        imgbtnFotocamera.setEnabled(true);

        /*if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
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
        }*/
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }
    private boolean isPasswordValid(String password) {
        return password.length() > 5;
    }

   /* public void takePhoto(View v) {
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
    }*/

    @Override
    public void OnSuccess(Object LoggedUser) {

        User user = (User) LoggedUser;
        Intent i = new Intent(RegisterActivity.this, MainActivity.class);
        i.putExtra("nome",user.getNome());
        i.putExtra("cognome",user.getCognome());
        i.putExtra("username",user.getUsername());
        i.putExtra("password",user.getPassword());
        i.putExtra("email",user.getEmail());
        i.putExtra("uriFotoDelProfilo",user.getUriFotoDelProfilo());
        startActivity(i);  

    }

    @Override
    public void OnError(String message) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
