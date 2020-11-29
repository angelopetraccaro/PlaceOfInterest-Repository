package com.gmail.petraccaro.angelo.placesofinterest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    private EditText nome,cognome,email,password,username;
    private Button btn_registrati;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        TextView txt = findViewById(R.id.account);
        txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
            }
        });

        nome = (EditText)findViewById(R.id.nome);
        cognome = (EditText)findViewById(R.id.cognome);
        email = (EditText)findViewById(R.id.email1);
        password = (EditText)findViewById(R.id.password1);
        username = (EditText)findViewById(R.id.username);
        btn_registrati = (Button) findViewById(R.id.btnRegistrati);

        btn_registrati.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str_nome=nome.getText().toString();
                String str_cognome=cognome.getText().toString();
                String str_email=email.getText().toString();
                String str_password=password.getText().toString();
                String str_user=username.getText().toString();

                System.out.println(str_nome+str_cognome+str_email+str_password+str_user); //ok

                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                mAuth.signOut();
                /*  mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.e(TAG, "createUserWithEmail:success");
                                Intent i = new Intent(RegisterActivity.this, MainActivity.class);   //accedo direttamente all'app come da implementazione?
                                startActivity(i);                                                  //o passo a login activity e poi reinserisco le credenziali appena create e accedo?
                            } else {
                                Log.e(TAG, "createUserWithEmail:failure", task.getException());
                            }
                        }
                    });  */
            }
        });
    }
}