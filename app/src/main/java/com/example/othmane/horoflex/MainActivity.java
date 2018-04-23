package com.example.othmane.horoflex;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;


//=================================================================
//          ==============    Main Activity   ==============
//=================================================================
public class MainActivity extends AppCompatActivity implements MainFragment.OnFragmentInteractionListener{

    MainFragment mainFragment = new MainFragment();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragementContainer, this.mainFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }

    // Création du menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    // Listener on the menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The item the user clicked on
        int id = item.getItemId();
        // Action chosen according to the item

        return super.onOptionsItemSelected(item);

    }


    // Méthode pour appliquer les préférences :
    protected void applyPref() {
        // - récupérer les valeurs choisies par l'utilisateur


    }

    @Override
    public void onFragmentInteraction(Uri uri) {
//
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);
    }


}
