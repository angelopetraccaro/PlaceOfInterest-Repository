package com.gmail.petraccaro.angelo.placesofinterest.Activities;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.gmail.petraccaro.angelo.placesofinterest.Controllers.Contract;
import com.gmail.petraccaro.angelo.placesofinterest.Controllers.ControllerUser;
import com.gmail.petraccaro.angelo.placesofinterest.Models.User;
import com.gmail.petraccaro.angelo.placesofinterest.R;
import com.google.firebase.firestore.FirebaseFirestore;


/**
 * Classe che si occupa del login
 */
public class LoginActivity extends AppCompatActivity implements Contract {
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private final String TAG = "ANGELO.PETRACCARO";
    private FirebaseFirestore myDB = FirebaseFirestore.getInstance();
    private ControllerUser cl = ControllerUser.getInstance();

    /**
     * Il metodo crea la LoginActivity, inizializza le variabili di email e password
     * e richiama il metodo attemptLogin.
     * @param savedInstanceState, variabile che memorizza lo stato precedente
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);



        cl.SetContext(this);
        TextView txt = findViewById(R.id.txt_no_account);
        txt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);

        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                    attemptLogin();
            }
        });
    }

    /**
     * Verifica la validità delle credenziali e se corrette, recupera il profilo firebase
     * ed effettua l'accesso alla MainActivity
     */
    private void attemptLogin() {
        final String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
        }
        else if (!isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
        }
        else if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
        }
        else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
        }

        else {

            cl.Login(email,password);
         /*   FirebaseAuth mAuth = FirebaseAuth.getInstance();
            mAuth.signOut();

            mAuth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {


                                myDB.collection("users")
                                        .document(email)
                                        .get()
                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {

                                            }
                                        });



                            } else
                                Toast.makeText(LoginActivity.this, R.string.UtenteNonregistrato, Toast.LENGTH_LONG).show();

                        }
                    });*/


        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 5;
    }

    @Override
    public void OnSuccess(Object LoggedUs) {

        User LoggedUser = (User)LoggedUs;
        Intent i = new Intent(LoginActivity.this, MainActivity.class);
        i.putExtra("nome",LoggedUser.getNome());
        i.putExtra("cognome",LoggedUser.getCognome());
        i.putExtra("username",LoggedUser.getUsername());
        i.putExtra("password",LoggedUser.getPassword());
        i.putExtra("email",LoggedUser.getEmail());
        i.putExtra("uriFotoDelProfilo",LoggedUser.getUriFotoDelProfilo());
        startActivity(i);
    }

    @Override
    public void OnError(String message) {
        Log.e("Errore_login",message);
        Toast.makeText(LoginActivity.this, R.string.UtenteNonregistrato, Toast.LENGTH_LONG).show();

    }
}