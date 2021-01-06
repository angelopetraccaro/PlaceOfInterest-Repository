package com.gmail.petraccaro.angelo.placesofinterest.Activities;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.gmail.petraccaro.angelo.placesofinterest.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Visualizza la/le foto (max 3) ed i relativi parametri;
 * Permette di visualizzare la posizione aprendo la mappa;
 * Permette di sostituire le foto caricate e/o pubblicare
 * nella parte pubblica la foto (se questa risiede in privato) e viceversa.
 * Inoltre permette la modifica dei campi dell'elemento.
 */
public class ReadActivity extends AppCompatActivity {
    private final String TAG = "lifecycle";
    private ImageButton gps;
    private CheckBox pub,pri;
    private TextView nome,breve,desc,nom,bre;
    private boolean available=true;
    private static int i=0;
    private ImageView photoTakenImageView;
    private Uri outputUri;
    private FloatingActionButton fab;
    private String userLog,nome_doc;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private String uriFoto;
    private String keyOfSelectElement;
    private TextView loc;
    private String lt ,lg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);
        gps=(ImageButton) findViewById(R.id.gps);
        //pub=(CheckBox) findViewById(R.id.pubblico);
        //pri=(CheckBox) findViewById(R.id.privato);
        nome=(TextView) findViewById(R.id.nome);
        breve=(TextView) findViewById(R.id.breve_desc);
        desc=(TextView) findViewById(R.id.Didascalia);
        nom=(TextView) findViewById(R.id.NomeText);
        bre=(TextView) findViewById(R.id.b_desc);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        loc =(TextView) findViewById(R.id.loc);
        final Intent intent=getIntent();
        nom.setText(intent.getStringExtra("name"));
        bre.setText(intent.getStringExtra("b_desc"));
        desc.setText(intent.getStringExtra("didascalia"));
        lt = intent.getStringExtra("latitude");
        lg = intent.getStringExtra("longitude");
        uriFoto=intent.getStringExtra("foto");
        userLog=intent.getStringExtra("owner");      //usato per vedere se l'utente può modificare o no
        nome_doc=intent.getStringExtra("nome_doc");
        photoTakenImageView = findViewById(R.id.horizontal);
        keyOfSelectElement = intent.getStringExtra("keyondb");


        //picasso per passare da uri a img
        Uri myUri=Uri.parse(uriFoto);
        Picasso.get().load(myUri).into(photoTakenImageView);


        //Get location from latitude and longitude
        Geocoder geo = new Geocoder(ReadActivity.this, Locale.getDefault());
        Double lat = Double.parseDouble(lt);
        Double lon = Double.parseDouble(lg);
        try {
            List<Address> addresses = geo.getFromLocation(lat,lon,1);
            loc.setText(addresses.get(0).getLocality());

        } catch (IOException e) {
            e.printStackTrace();
        }

        gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!(lt.isEmpty()&&lg.isEmpty())) {
                    Intent inte = new Intent(ReadActivity.this, ShowMapActivity.class);

                    Double latitu = Double.parseDouble(lt);
                    Double longitu = Double.parseDouble(lg);
                    inte.putExtra("Latitude", latitu);
                    inte.putExtra("Longitude", longitu);
                    startActivity(inte);
                }else
                    Toast.makeText(ReadActivity.this,"Latitudine e Longitudine Assenti",Toast.LENGTH_LONG).show();
            }
        });


        /** reaload foto **/
        if (savedInstanceState != null){
            uriFoto =savedInstanceState.getString("Uri");
            Uri ConvertedUri =Uri.parse(uriFoto);
            Picasso.get().load(ConvertedUri).into(photoTakenImageView);
        }
    }

    /**
     * Memorizza lo stato da ripristinare se lo smartphone va in landscape
     * @param b bundle che incapsula lo stato corrente
     */
    protected void onSaveInstanceState(Bundle b) {
        super.onSaveInstanceState(b);
        b.putString("Uri", uriFoto);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}