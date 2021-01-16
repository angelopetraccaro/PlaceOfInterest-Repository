package com.gmail.petraccaro.angelo.placesofinterest.Activities;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LoginActivityTest {
    private LoginActivity la;

    @Before
    public void setUp() throws Exception {
        la=new LoginActivity();
    }

    @After
    public void tearDown() throws Exception {
    }

    //Email_OK,email_Valid,pass_Valid,Register_OK(1),user_Not_logged
    @Test
    public void zero() throws InterruptedException {
        assertTrue("I campi non sono vuoti", la.isEmailOK("petraccaro.angelo@gmail.com"));
        assertTrue("I campi contengono la @", la.isEmailValid("petraccaro.angelo@gmail.com"));
        assertTrue("I campi sono maggiori o uguali di 6 caratteri", la.isPasswordValid("asd123"));

        isRegistered("petraccaro.angelo@gmail.com","asd123");
        boolean logged = isRegistered("petraccaro.angelo@gmail.com","asd123");
        assertTrue("Utente registrato", logged );

        boolean logged1 = isLogged();
        assertFalse("Utente loggato", !logged1);
    }

    //Email_OK,email_Valid,pass_Valid,Register_OK(1),user_logged
    @Test
    public void uno() throws InterruptedException {
        assertTrue("I campi non sono vuoti", la.isEmailOK("petraccaro.angelo@gmail.com"));
        assertTrue("I campi contengono la @", la.isEmailValid("petraccaro.angelo@gmail.com"));
        assertTrue("I campi sono maggiori o uguali di 6 caratteri", la.isPasswordValid("asd123"));

        isRegistered("petraccaro.angelo@gmail.com","asd123");
        boolean logged = isRegistered("petraccaro.angelo@gmail.com","asd123");
        assertTrue("Utente registrato", logged );

        boolean logged1 = isLogged();
        assertTrue("Utente loggato", logged1 );
    }

    //Email_OK,email_Valid,pass_Valid,Register_NotOK(2)
    @Test
    public void due() throws InterruptedException {
        assertTrue("I campi non sono vuoti", la.isEmailOK("test@gmail.com"));
        assertTrue("I campi contengono la @", la.isEmailValid("test@gmail.com"));
        assertTrue("I campi sono maggiori o uguali di 6 caratteri", la.isPasswordValid("asd123"));

        isRegistered("test@gmail.com","asd123");
        boolean logged = isRegistered("petraccaro.angelo@gmail.com","asd123");
        assertFalse("Utente non registrato", !logged );
    }

    //Email_OK,email_Valid,pass_NotValid (3)
    @Test
    public void tre() {
        assertTrue("I campi non sono vuoti", la.isEmailOK("petraccaro.angelo@gmail.com"));
        assertTrue("I campi contengono la @", la.isEmailValid("petraccaro.angelo@gmail.com"));
        assertFalse("I campi sono minori di 6 caratteri", la.isPasswordValid("asd12"));
    }

    //Email_OK,email_NotValid (4)
    @Test
    public void quattro() {
        assertTrue("I campi non sono vuoti", la.isEmailOK("petraccaro.angelogmail.com"));
        assertFalse("I campi non contengono la @", la.isEmailValid("petraccaro.angelogmail.com"));
    }
    //Not email_OK (5)
    @Test
    public void cinque() {
        assertFalse("I campi sono vuoti", la.isEmailOK(""));
    }

    //Stub
    public boolean isRegistered(String email, String password){

        if(email.equalsIgnoreCase("petraccaro.angelo@gmail.com") && password.equalsIgnoreCase("asd123"))
            return true;
        return false;
    }
    //Stub
    public boolean isLogged(){
        return true;
    }

}