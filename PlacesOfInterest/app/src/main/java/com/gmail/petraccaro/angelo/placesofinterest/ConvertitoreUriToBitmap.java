package com.gmail.petraccaro.angelo.placesofinterest;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.auth.User;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class ConvertitoreUriToBitmap extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     *
     */
    public static final String FILTER_ACTION_KEY = "convertitore";
    private ArrayList<String>ArrayUri = null;
    private ArrayList<String> paths = null;
    final int[] i = {0};


    private ArrayList<Bitmap>arrayOfBitmap ;
    public ConvertitoreUriToBitmap() {
      super("convertitore");
        arrayOfBitmap = new ArrayList<>();
        paths = new ArrayList<>();
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.e("vengo chiamato","sono il servizo");
        String echoMessage = "IntentService after a pause of 3 seconds echoes " ;
       ArrayUri = intent.getStringArrayListExtra("arrayurifoto");

        for(String key: ArrayUri)
            convetBitmap(key);

    }
    private void convetBitmap(final String uri) {
        final Bitmap[] bitmap = {null};


                   // bitmap[0] = Glide.with(getApplicationContext()).asBitmap().load(uri).into(100, 100).get();



                 Glide.with(this)
                .asBitmap()
                .load(uri)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        i[0]++;

                        FirebaseUser us = FirebaseAuth.getInstance().getCurrentUser();
                        Log.e("us",us.getEmail());
                        arrayOfBitmap.add(resource);
                        File outputDir = getApplicationContext().getCacheDir();
                        File imageFile = new File(outputDir, String.valueOf(i[0]) + ".jpg");

                        OutputStream os;
                        try {
                            os = new FileOutputStream(imageFile);
                            resource.compress(Bitmap.CompressFormat.JPEG, 100, os);
                            os.flush();
                            os.close();
                        } catch (Exception e) {
                            Log.e(getApplicationContext().getClass().getSimpleName(), "Error writing file", e);
                        }

                        paths.add(imageFile.getAbsolutePath());
                        if(i[0] == ArrayUri.size() ){
                            sendCashbackInfoToClient("IntentService after a pause of 3 seconds echoes ");
                        }

                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }
                });







    }
    private void sendCashbackInfoToClient(String msg){
        Intent intent = new Intent();
        intent.setAction(FILTER_ACTION_KEY);

       intent.putStringArrayListExtra("arrayFiles",paths);
        sendBroadcast(intent);
    }
}
