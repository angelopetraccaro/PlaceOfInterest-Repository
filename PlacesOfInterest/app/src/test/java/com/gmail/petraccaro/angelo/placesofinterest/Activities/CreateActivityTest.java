package com.gmail.petraccaro.angelo.placesofinterest.Activities;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class CreateActivityTest {
    private CreateActivity ca;

    @Before
    public void setUp() throws Exception {
        ca=new CreateActivity();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void zero() throws InterruptedException {
        assertTrue("Foto selezionata",photoSelected());
        assertTrue("Nome foto inserito",isFieldOk("nome1"));
        assertTrue("Breve descrizione inserita",isFieldOk("breve descrizione 1"));
        assertTrue("Didascalia inserita",isFieldOk("didascalia 1"));
        assertTrue("Gps attivo",gpsActive());
        assertTrue("Utente loggato",userLogged());
    }

    @Test
    public void uno() throws InterruptedException {
        assertTrue("Foto selezionata",photoSelected());
        assertTrue("Nome foto inserito",isFieldOk("nome2"));
        assertTrue("Breve descrizione inserita",isFieldOk("breve descrizione 2"));
        assertTrue("Didascalia inserita",isFieldOk("didascalia 2"));
        assertFalse("Gps attivo",!gpsActive());
    }

    @Test
    public void due() throws InterruptedException {
        assertTrue("Foto selezionata",photoSelected());
        assertTrue("Nome foto inserito",isFieldOk("nome2"));
        assertTrue("Breve descrizione inserita",isFieldOk("breve descrizione 2"));
        assertFalse("Didascalia inserita",isFieldOk(""));
    }

    @Test
    public void tre() throws InterruptedException {
        assertTrue("Foto selezionata",photoSelected());
        assertTrue("Nome foto inserito",isFieldOk("nome2"));
        assertFalse("Breve descrizione inserita",isFieldOk(""));
    }

    @Test
    public void quattro() throws InterruptedException {
        assertTrue("Foto selezionata",photoSelected());
        assertFalse("Nome foto inserito",isFieldOk(""));
    }

    @Test
    public void cinque() throws InterruptedException {
        assertFalse("Foto selezionata",!photoSelected());
    }

    //driver
    public boolean photoSelected(){
        return true;
    }

    public boolean userLogged(){
        return true;
    }

    public boolean gpsActive(){
        return true;
    }

    public boolean isFieldOk(String name){
        if(name.length()>0)
            return true;
        return  false;
    }

    @Test
    public void addOnStorage() {
    }
}