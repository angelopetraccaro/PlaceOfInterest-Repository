package com.gmail.petraccaro.angelo.placesofinterest.Controllers;

import android.app.Activity;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.gmail.petraccaro.angelo.placesofinterest.Models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class ControllerUser {
    private FirebaseFirestore myDB = FirebaseFirestore.getInstance();
    private static ControllerUser controller = new ControllerUser();
    Contract lw;  //serve per far dialogare controllore e activity
    Uri uri= null;


    public void Login(final String email, String password) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();

        mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener( ((Activity)lw), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {


                            myDB.collection("users")
                                    .document(email)
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            User LoggedUser = ((User)documentSnapshot.toObject(User.class));
                                            lw.OnSuccess(LoggedUser);
                                        }
                                    });
                        }else{
                            lw.OnError(task.getException().getMessage());
                        }


                    }
                });
    }


    public void Logout() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();
        lw.OnSuccess(null);
    }
    public void register(final String nome, final String cognome, final String email, final String password, final String username,final Uri uri){
        FirebaseStorage myStorage = FirebaseStorage.getInstance();
        StorageReference rootStorageRef = myStorage.getReference();
        final StorageReference documentRef = rootStorageRef.child("ProfileImages");
        final StorageReference scoreRef = documentRef.child(uri.getLastPathSegment());
        final UploadTask uploadTask = scoreRef.putFile(uri);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(((Activity)lw), "Upload Failure", Toast.LENGTH_LONG).show();
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
                                                if(usr.getEmail().equalsIgnoreCase(email)){
                                                    presente = !presente;
                                                }
                                            }

                                            if(!presente){
                                                DocumentReference myRef = myDB.collection("users").document(email);

                                                final User user = new User(nome,cognome,username,email,password, uri.toString() );
                                                myRef.set(user);
                                                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                                                mAuth.signOut();

                                                mAuth.createUserWithEmailAndPassword(email,password)
                                                        .addOnCompleteListener((Activity)lw, new OnCompleteListener<AuthResult>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                                if (task.isSuccessful()){
                                                                    lw.OnSuccess(user);
                                                                }else{
                                                                    Log.e("cosa succede", task.getException().toString());
                                                                }
                                                        }
                                                        });


                                            }else{
                                                lw.OnError("");
                                            }

                                        }

                                    }
                                });



                    }
                });

            }
        });
    }

    public void SetContext(Contract lw){
        this.lw = lw;
    }


    private ControllerUser(){ }

    public static ControllerUser getInstance(){ return  controller;}
}
