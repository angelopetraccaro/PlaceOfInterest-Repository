package com.gmail.petraccaro.angelo.placesofinterest.Controllers;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import com.gmail.petraccaro.angelo.placesofinterest.BuildConfig;
import com.gmail.petraccaro.angelo.placesofinterest.Models.Post;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ControllerPhoto {

    private static ControllerPhoto controller = new ControllerPhoto();
    Uri uri = null;
    private Uri outputUri;
    private static int RESULT_LOAD_IMAGE = 1;
    private static final int ACTIVITY_START_CAMERA_APP = 0; //per chiamare la fotocamera
    ContractPhoto lw;

    private ControllerPhoto() {
    }

    public static ControllerPhoto getInstance() {
        return controller;
    }


    public void SetContext(ContractPhoto lw) {
        this.lw = lw;
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
                (Activity) lw,
                BuildConfig.APPLICATION_ID + ".provider",
                photoFile);
        callCameraApplicationIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
        callCameraApplicationIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        ((Activity) lw).startActivityForResult(callCameraApplicationIntent, ACTIVITY_START_CAMERA_APP);
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmSS").format(new Date());
        String imageFileName = "IMAGE_" + timeStamp + ".jpg";
        File externalDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return new File(externalDir.toString() + "/" + imageFileName);
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(outputUri);
        ((Activity) lw).sendBroadcast(mediaScanIntent);
    }

    public Pair<Uri, Bitmap> onActivityresult(int requestCode, int resultCode, Intent data) {
        Bitmap imageBitmap = null;
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == ((Activity) lw).RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = ((Activity) lw).getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            uri = selectedImage;

            imageBitmap = BitmapFactory.decodeFile(picturePath);
        }
        if (requestCode == ACTIVITY_START_CAMERA_APP && resultCode == ((Activity) lw).RESULT_OK) {
            Bitmap mImageBitmap = null;

            try {
                mImageBitmap = MediaStore.Images.Media.getBitmap(((Activity) lw).getContentResolver(), outputUri);
                if (mImageBitmap != null) {

                    uri = outputUri;

                    galleryAddPic();
                    imageBitmap = mImageBitmap;
                }
            } catch (IOException e) {
                Log.e("uri", Log.getStackTraceString(e));
            }
        }

        Pair<Uri, Bitmap> pair = new Pair<Uri, Bitmap>(uri, imageBitmap);
        return pair;
    }


    public void takePhotoFromGallery() {
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        ((Activity) lw).startActivityForResult(i, RESULT_LOAD_IMAGE);
    }


    public void addOnStorage(final Uri outputUri, final String breveDescrizione, final String nome, final String didascalia, final Double latitudine, final Double longitudine, final String username) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final FirebaseUser currentUser = mAuth.getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseStorage myStorage = FirebaseStorage.getInstance();
        StorageReference rootStorageRef = myStorage.getReference();
        StorageReference documentRef = rootStorageRef.child("images");
        final DatabaseReference myRefToDb = database.getReference("photos");

        Uri uri = outputUri;


        final StorageReference scoreRef = documentRef.child(uri.getLastPathSegment());
        final UploadTask uploadTask = scoreRef.putFile(uri);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(((Activity) lw), "Upload Failure", Toast.LENGTH_LONG).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                Toast.makeText(((Activity) lw), "File uploaded on storage", Toast.LENGTH_LONG).show();
                scoreRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Uri uriFoto = uri;


                        String uploadId = myRefToDb.push().getKey();
                        Post el = new Post(nome, breveDescrizione,
                                Double.toString(latitudine), Double.toString(longitudine), uriFoto.toString(),
                                didascalia, (currentUser != null) ? currentUser.getUid() : null, uploadId, username);
                        myRefToDb.child(uploadId).setValue(el);
                        lw.OnSuccess(el);


                    }
                });

            }
        });
    }

}