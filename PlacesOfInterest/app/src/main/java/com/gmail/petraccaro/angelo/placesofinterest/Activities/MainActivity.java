package com.gmail.petraccaro.angelo.placesofinterest.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gmail.petraccaro.angelo.placesofinterest.Adapters.CustomAdapter;
import com.gmail.petraccaro.angelo.placesofinterest.Controllers.Contract;
import com.gmail.petraccaro.angelo.placesofinterest.Controllers.ControllerUser;
import com.gmail.petraccaro.angelo.placesofinterest.Models.Post;
import com.gmail.petraccaro.angelo.placesofinterest.Models.User;
import com.gmail.petraccaro.angelo.placesofinterest.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements Contract {

    private CircleImageView imgviewFotoProfilo;
    private User userLogged;
    private ControllerUser cp = ControllerUser.getInstance();
    private ListView ListOfPhotos;
    private ArrayList<Post> PublicList = new ArrayList<Post>();
    private FirebaseDatabase db;
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private android.widget.ProgressBar ProgressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_user_nav);

        Intent IntentInizializzazione = getIntent();
        ListOfPhotos=(ListView)findViewById(R.id.PostList1);
        ProgressBar = (ProgressBar)findViewById(R.id.progress_circle);
        db = FirebaseDatabase.getInstance();
        myRef  = db.getReference("photos");
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        cp.SetContext(this);
        String nome,cognome,username,password,email,uriFotoDelProfilo;
        if( IntentInizializzazione.getExtras() != null && IntentInizializzazione.getExtras().containsKey("email")){
            nome = IntentInizializzazione.getStringExtra("nome");
            cognome = IntentInizializzazione.getStringExtra("cognome");
            username = IntentInizializzazione.getStringExtra("username");
            password = IntentInizializzazione.getStringExtra("password");
            email = IntentInizializzazione.getStringExtra("email");
            uriFotoDelProfilo = IntentInizializzazione.getStringExtra("uriFotoDelProfilo");
            Log.e("urifotoPtofilo",uriFotoDelProfilo);
            userLogged = new User(nome,cognome,username,email,password,uriFotoDelProfilo);
            NavigationView mynav = (NavigationView) findViewById(R.id.nav_view);

            Menu menu = (Menu) mynav.getMenu();
            menu.getItem(1).setTitle(userLogged.getCognome());
            menu.getItem(2).setTitle(userLogged.getEmail());

            imgviewFotoProfilo = (CircleImageView) mynav.getHeaderView(0).findViewById(R.id.nav_header_imageViewFoto);
            TextView user= (TextView) mynav.getHeaderView(0).findViewById(R.id.nav_header_username);

            user.setText(userLogged.getUsername());

            StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(userLogged.getUriFotoDelProfilo());
            Picasso.get().load(userLogged.getUriFotoDelProfilo()).into(imgviewFotoProfilo); menu.getItem(0).setTitle(userLogged.getNome());

            getContactPublic();




            ListOfPhotos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent1=new Intent(getApplicationContext(), ReadActivity.class);

                        intent1.putExtra("name",PublicList.get(position).getNome());
                        intent1.putExtra("b_desc",PublicList.get(position).getBreve_descrizione());
                        intent1.putExtra("latitude",PublicList.get(position).getLatitude());
                        intent1.putExtra("longitude",PublicList.get(position).getLongitude());
                        intent1.putExtra("foto",PublicList.get(position).getUrl_foto());
                        intent1.putExtra("didascalia",PublicList.get(position).getDidascalia());
                        intent1.putExtra("owner",PublicList.get(position).getOwner());
                        intent1.putExtra("keyondb",PublicList.get(position).getKeyOnDb());
                        startActivity(intent1);
                }
            });
            ListOfPhotos.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                    AlertDialog.Builder adb= new AlertDialog.Builder(MainActivity.this);
                    adb.setTitle("Delete?");
                    adb.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                                   if (PublicList.get(position).getOwner().equalsIgnoreCase(currentUser.getUid())) {
                                    final Post el =  PublicList.get(position);
                                    final String key = el.getKeyOnDb();
                                    StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(el.getUrl_foto());
                                    storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            myRef.child(key).removeValue();
                                            PublicList.remove(el);
                                        }
                                    });
                                }else
                                    Toast.makeText(getApplicationContext(),"You are not the owner",Toast.LENGTH_LONG).show();



                        }
                    });
                    adb.setNegativeButton("Cancel", null);
                    adb.show();
                    return true;
                }
            });


        }
        if(savedInstanceState != null){
            nome = (String) savedInstanceState.get("nome");
            cognome = (String) savedInstanceState.get("cognome");
            username = (String) savedInstanceState.get("username");
            password = (String) savedInstanceState.get("password");
            email = (String)savedInstanceState.get("email");
            uriFotoDelProfilo = (String) savedInstanceState.get("uriFotoDelProfilo");
            userLogged = new User(nome,cognome,username,email,password,uriFotoDelProfilo);
        }



        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(MainActivity.this, CreateActivity.class);
                i.putExtra("username",userLogged.getUsername());
                i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivityForResult(i,2);
            }
        });

    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString("nome",userLogged.getNome());
        savedInstanceState.putString("cognome",userLogged.getCognome());
        savedInstanceState.putString("username",userLogged.getUsername());
        savedInstanceState.putString("password",userLogged.getPassword());
        savedInstanceState.putString("email",userLogged.getEmail());
        savedInstanceState.putString("uriFotoDelProfilo",userLogged.getUriFotoDelProfilo());
        super.onSaveInstanceState(savedInstanceState);

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode ==RESULT_OK )
            Toast.makeText(this,R.string.NuovoPost,Toast.LENGTH_LONG);
    }

    public boolean avviaRiconoscimento(MenuItem item) {
        Intent i=new Intent(this, DetectorActivity.class);
        i.putExtra("uri",userLogged.getUriFotoDelProfilo());
        startActivity(i);
        return true;
    }

    public void logout(MenuItem item) { cp.Logout(); }

    @Override
    public void OnSuccess(Object obj) {
        finish();
        Intent i=new Intent(this, LoginActivity.class);
        startActivity(i);
    }

    @Override
    public void OnError(String message) {

    }
    public void getContactPublic(){
        PublicList.clear();
        ListOfPhotos.setAdapter(new CustomAdapter(getApplicationContext(), R.layout.list_item, PublicList));
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                PublicList.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    Post els = ds.getValue(Post.class);
                    PublicList.add(els);
                }

                ListOfPhotos.setAdapter(new CustomAdapter(getApplicationContext(), R.layout.list_item, PublicList));
                ProgressBar.setVisibility(View.INVISIBLE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                ProgressBar.setVisibility(View.VISIBLE);
            }
        });
    }
}