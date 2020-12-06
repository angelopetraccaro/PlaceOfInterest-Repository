package com.gmail.petraccaro.angelo.placesofinterest;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Visualizza la lista degli item (foto,nome_luogo,descrizione_luogo);
 * Permette il passaggio alla CreateActivity per inserire una nuova foto;
 * Permette il passaggio alla ReadActivity per visualizzare una foto;
 *
 */
public class MainActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private CircleImageView imgviewFotoProfilo;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_user_nav);

 UserNavigationDrawer
        Intent IntentInizializzazione = getIntent();

        String nome = IntentInizializzazione.getStringExtra("nome");
        String cognome = IntentInizializzazione.getStringExtra("cognome");
        String username = IntentInizializzazione.getStringExtra("username");
        String password = IntentInizializzazione.getStringExtra("password");
        String email = IntentInizializzazione.getStringExtra("email");
        final String uriFotoDelProfilo = IntentInizializzazione.getStringExtra("uriFotoDelProfilo");
        User userLogged = new User(nome,cognome,username,email,password,uriFotoDelProfilo);

        NavigationView mynav = (NavigationView) findViewById(R.id.nav_view);

        Menu menu = (Menu) mynav.getMenu();
        menu.getItem(0).setTitle(nome);
        menu.getItem(1).setTitle(cognome);
        menu.getItem(2).setTitle(email);

        imgviewFotoProfilo = (CircleImageView) mynav.getHeaderView(0).findViewById(R.id.nav_header_imageViewFoto);
        TextView user= (TextView) mynav.getHeaderView(0).findViewById(R.id.nav_header_username);

         user.setText(username);

        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(uriFotoDelProfilo);
        Picasso.get().load(uriFotoDelProfilo).into(imgviewFotoProfilo);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
 
        setSupportActionBar(toolbar);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        TabLayout tabLayout = findViewById(R.id.tabs);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(MainActivity.this,CreateActivity.class);
                startActivity(i);
            }
        });



    }


    public boolean Avvia_Riconoscimento(MenuItem item) {
        Intent i=new Intent(this,GalleryActivity.class);
        startActivity(i);
        return true;
    }

  

    public boolean Logout(MenuItem item) {
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            mAuth.signOut();
            finish();
            Intent i=new Intent(this,LoginActivity.class);
            startActivity(i);
            return true;
    }

    /**
     * Fragment per la gestione della lista pubblica e privata di elementi
     */
    public static class PlaceholderFragment extends Fragment {
        private static final String ARG_SECTION_NUMBER = "section_number";
        private ListView ListOfPhotos;
        private ArrayList<ElementoLista> PublicList = new ArrayList<ElementoLista>();
        private ArrayList<ElementoLista> PrivateList = new ArrayList<ElementoLista>();
        private FirebaseDatabase db;
        private DatabaseReference myRef;
        private FirebaseAuth mAuth;
        private FirebaseUser currentUser;

        public PlaceholderFragment() {
        }

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        /**
         * Il metodo gestisce listener che per un click sulla lista di item lancia l'intent alla ReadActivity;
         * gestisce un listener che per un long click sulla lista di item elimina l'elemento;
         * @param container il contenitore di elementi della lista
         * @param savedInstanceState variabile che memorizza lo stato precedente
         * @return rootView, ovvero la lista aggiornata
         */
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            ListOfPhotos=(ListView)rootView.findViewById(R.id.lista);

            db = FirebaseDatabase.getInstance();
            myRef  = db.getReference("photos");
            mAuth = FirebaseAuth.getInstance();
            currentUser = mAuth.getCurrentUser();

            ListOfPhotos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent1=new Intent(getContext(),ReadActivity.class);
                if (getArguments().getInt(ARG_SECTION_NUMBER) == 1) {
                    intent1.putExtra("name",PublicList.get(position).getNome());
                    intent1.putExtra("b_desc",PublicList.get(position).getBreve_descrizione());
                    intent1.putExtra("desc",PublicList.get(position).getDescrizione());
                    intent1.putExtra("latitude",PublicList.get(position).getLatitude());
                    intent1.putExtra("longitude",PublicList.get(position).getLongitude());
                    intent1.putExtra("foto",PublicList.get(position).getUrl_foto());

                    intent1.putExtra("didascalia",PublicList.get(position).getDidascalia());

                    intent1.putExtra("owner",PublicList.get(position).getOwner());
                    intent1.putExtra("keyondb",PublicList.get(position).getKeyOnDb());
                    } else{
                    intent1.putExtra("name",PrivateList.get(position).getNome());
                    intent1.putExtra("b_desc",PrivateList.get(position).getBreve_descrizione());
                    intent1.putExtra("desc",PrivateList.get(position).getDescrizione());
                    intent1.putExtra("latitude",PrivateList.get(position).getLatitude());
                    intent1.putExtra("longitude",PrivateList.get(position).getLongitude());
                    intent1.putExtra("foto",PrivateList.get(position).getUrl_foto());

                    intent1.putExtra("didascalia",PrivateList.get(position).getDidascalia());

                    intent1.putExtra("owner",PrivateList.get(position).getOwner());
                    Log.e("key",PrivateList.get(position).getKeyOnDb() );
                    intent1.putExtra("keyondb",PrivateList.get(position).getKeyOnDb());
                    }
                startActivity(intent1);
                                          }
            });
           /** l'utente può cancellare solo le foto che ha postato**/
            ListOfPhotos.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
               public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
               AlertDialog.Builder adb= new AlertDialog.Builder(getContext());
               adb.setTitle("Delete?");
               adb.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            if(getArguments().getInt(ARG_SECTION_NUMBER) == 1){

                            if (PublicList.get(position).getOwner().equalsIgnoreCase(currentUser.getUid())) {
                                final ElementoLista el =  PublicList.get(position);
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
                           Toast.makeText(getContext(),"You are not the owner",Toast.LENGTH_LONG).show();

                           }else{
                       /** nella parte private ci sono le foto dell'utente che non ha ancora deciso di pubblicare **/
                                final ElementoLista el =  PrivateList.get(position);
                                final String k = el.getKeyOnDb();
                                StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(el.getUrl_foto());
                                 storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                          @Override
                                                                          public void onSuccess(Void aVoid) {
                                                                              myRef.child(k).removeValue();
                                                                              PrivateList.remove(el);
                                                                          }
                                                                      });
                            }
                        }
                    });
               adb.setNegativeButton("Cancel", null);
               adb.show();
               return true;
                }
            });
            return rootView;
        }

        /**
         * Caricamento e visualizazzione della lista delle foto pubbliche
         */
        public void getContactPublic(){
            PublicList.clear();
            ListOfPhotos.setAdapter(new CustomAdapter(getContext(), R.layout.list_item, PublicList));
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    PublicList.clear();
                    PrivateList.clear();
                    for(DataSnapshot ds: snapshot.getChildren()){
                            ElementoLista els = ds.getValue(ElementoLista.class);
                            if(els.getAvailable() == true)
                                PublicList.add(els);

                        }
                    ListOfPhotos.setAdapter(new CustomAdapter(getContext(), R.layout.list_item, PublicList));
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }

        /**
         * Caricamento e visualizazzione della lista delle foto privata
         */
        public void getContactPrivate(){
            PrivateList.clear();
            ListOfPhotos.setAdapter(new CustomAdapter(getContext(), R.layout.list_item, PrivateList));

            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    PrivateList.clear();
                    PublicList.clear();
                    for(DataSnapshot ds: snapshot.getChildren()){
                        ElementoLista els = ds.getValue(ElementoLista.class);
                        if(els.getAvailable()== false) PrivateList.add(els);

                    }

                    ListOfPhotos.setAdapter(new CustomAdapter(getContext(), R.layout.list_item, PrivateList));

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        public void onStart(){
            if (getArguments().getInt(ARG_SECTION_NUMBER) == 1) {
                getContactPublic();
            } else{
                getContactPrivate();
            }
            super.onStart();
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return MainActivity.PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}

