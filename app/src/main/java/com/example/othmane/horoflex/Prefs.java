package com.example.othmane.horoflex;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;

//======================================================
//==============Menu pref===============================
//======================================================

public class Prefs extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Affichage à partir du fichier XML
        addPreferencesFromResource(R.xml.prefs);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        ((MainActivity) getActivity()).applyPref();

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
