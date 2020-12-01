package com.gmail.petraccaro.angelo.placesofinterest;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


/**
 * Classe che si occupa del login
 */
public class LoginActivity extends AppCompatActivity  {
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private final String TAG = "ANGELO.PETRACCARO";

    /**
     * Il metodo crea la LoginActivity, inizializza le variabili di email e password
     * e richiama il metodo attemptLogin.
     * @param savedInstanceState, variabile che memorizza lo stato precedente
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        TextView txt = findViewById(R.id.txt_no_account);
        txt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
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
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

       /* if (TextUtils.isEmpty(password)) {
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

        else {*/
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            mAuth.signOut();

            mAuth.signInWithEmailAndPassword("antonio@gmail.com","ciaociao")
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(i);
                                Log.e(TAG, "signInWithEmail:success");
                            } else {
                                Toast.makeText(LoginActivity.this, task.getException().toString(), Toast.LENGTH_LONG).show();
                               // Toast.makeText(LoginActivity.this, "The email address is already in use, password incorrect", Toast.LENGTH_LONG).show();
                                Log.e(TAG, "signInWithEmail:FAIL");
                            }
                        }
                    });

      //  }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 5;
    }
}