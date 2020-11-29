package com.gmail.petraccaro.angelo.placesofinterest;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * Visualizza la lista degli item (foto,nome_luogo,descrizione_luogo);
 * Permette il passaggio alla CreateActivity per inserire una nuova foto;
 * Permette il passaggio alla ReadActivity per visualizzare una foto;
 *
 */
public class MainActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(MainActivity.this,CreateActivity.class);
                startActivity(i);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
        private ListView lista;
        private ArrayList<ElementoLista> list = new ArrayList<ElementoLista>(); //pubblici
        private ArrayList<ElementoLista> list1 = new ArrayList<ElementoLista>(); //privati
        private FirebaseFirestore db;
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
            lista=(ListView)rootView.findViewById(R.id.lista);
            db = FirebaseFirestore.getInstance();

            //dettagli elemento lista pubb e pri
            mAuth = FirebaseAuth.getInstance();
            currentUser = mAuth.getCurrentUser();
            lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent1=new Intent(getContext(),ReadActivity.class);
                if (getArguments().getInt(ARG_SECTION_NUMBER) == 1) {
                    intent1.putExtra("name",list.get(position).getNome());
                    intent1.putExtra("b_desc",list.get(position).getBreve_descrizione());
                    intent1.putExtra("desc",list.get(position).getDescrizione());
                    intent1.putExtra("latitude",list.get(position).getLatitude());
                    intent1.putExtra("longitude",list.get(position).getLongitude());
                    intent1.putExtra("foto",list.get(position).getUrl_foto());

                    intent1.putExtra("didascalia",list.get(position).getDidascalia());

                    intent1.putExtra("owner",list.get(position).getOwner());
                    intent1.putExtra("nome_doc",list.get(position).getNome_documento());
                    } else{
                    intent1.putExtra("name",list1.get(position).getNome());
                    intent1.putExtra("b_desc",list1.get(position).getBreve_descrizione());
                    intent1.putExtra("desc",list1.get(position).getDescrizione());
                    intent1.putExtra("latitude",list1.get(position).getLatitude());
                    intent1.putExtra("longitude",list1.get(position).getLongitude());
                    intent1.putExtra("foto",list1.get(position).getUrl_foto());

                    intent1.putExtra("didascalia",list1.get(position).getDidascalia());

                    intent1.putExtra("owner",list1.get(position).getOwner());
                    intent1.putExtra("nome_doc",list1.get(position).getNome_documento());
                    }
                startActivity(intent1);
                                          }
            });

            lista.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
               public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
               AlertDialog.Builder adb= new AlertDialog.Builder(getContext());
               adb.setTitle("Delete?");
               adb.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                   if(getArguments().getInt(ARG_SECTION_NUMBER) == 1){
                       if (list.get(position).getOwner().equalsIgnoreCase(currentUser.getUid())) {
                             db.collection("luoghi").document(list.get(position).getNome_documento()).delete()
                                  .addOnSuccessListener(new OnSuccessListener<Void>() {
                                      @Override
                                      public void onSuccess(Void aVoid) {
                                          getContactPublic();
                                          Log.d("ciao", "deleted place!");
                                      }
                                      })
                                     .addOnFailureListener(new OnFailureListener() {
                                          @Override
                                          public void onFailure(@NonNull Exception e) {
                                            Log.d("ciao", "Error deleting place ");
                                          }
                                     });
                       }else{
                           Toast.makeText(getContext(),"You are not the owner",Toast.LENGTH_LONG).show();
                       }
                   }else{
                       db.collection("luoghi").document(list1.get(position).getNome_documento()).delete()
                               .addOnSuccessListener(new OnSuccessListener<Void>() {
                                   @Override
                                   public void onSuccess(Void aVoid) {
                                       getContactPrivate();
                                       Log.d("ciao", "deleted place!");
                                   }
                               })
                               .addOnFailureListener(new OnFailureListener() {
                                      @Override
                                       public void onFailure(@NonNull Exception e) {
                                          Log.d("ciao", "Error deleting place");
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
         * Caricamento lista pubblica
         */
        public void getContactPublic(){
            list.clear();
            lista.setAdapter(new CustomAdapter(getContext(), R.layout.list_item, list));
            db.collection("luoghi")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    if (document.get("Available").toString().equals("true")) {
                                        Log.e("cc",document.getId());
                                        list.add(new ElementoLista(document.getString("Nome"), document.getString("Breve_desc"), document.getString("Lunga_desc"), document.getString("latitude"), document.getString("longitude"), document.getString("foto"),document.getString("didascalia"),document.getId(), document.getString("owner")));
                                        lista.setAdapter(new CustomAdapter(getContext(), R.layout.list_item, list));
                                    }
                                }
                            } else {
                                Log.e("ciao", "Get failed: ");
                            }
                        }
                    });
        }

        /**
         * Caricamento lista privata
         */
        public void getContactPrivate(){
            list1.clear();
            lista.setAdapter(new CustomAdapter(getContext(), R.layout.list_item, list1));
            db.collection("luoghi")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    if ((document.get("Available").toString().equals("false"))&& (document.get("owner").toString().equals(currentUser.getUid()))) {
                                        list1.add(new ElementoLista(document.getString("Nome"), document.getString("Breve_desc"), document.getString("Lunga_desc"), document.getString("latitude"),document.getString("longitude"), document.getString("foto"),document.getString("didascalia"),document.getId(), document.getString("owner")));
                                        lista.setAdapter(new CustomAdapter(getContext(), R.layout.list_item, list1));
                                    }
                                }
                            } else {
                                Log.e("ciao", "Get failed: ");
                            }
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

