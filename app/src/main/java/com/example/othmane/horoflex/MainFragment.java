package com.example.othmane.horoflex;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceFragment;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Handler;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment implements View.OnClickListener{



    private Button btnreset;
    private Button btnConnection;
    private Button btnGetData;
    private TextView txt;
    private TextView txtCo;
    private BluetoothAdapter BA;
    BluetoothSPP bt;
    String[] temp;
    long hex;
    Date date = new Date();
    PreferenceFragment preferenceFragment = new Prefs();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        super.onCreate(savedInstanceState);

        bt = new BluetoothSPP(getContext());
        bt.setupService();
        bt.enable();
        btnreset = (Button) view.findViewById(R.id.btnReset);
        btnreset.setEnabled(false);
        btnreset.setOnClickListener(this);


        btnConnection = (Button) view.findViewById(R.id.btnCo);
        btnConnection.setOnClickListener(this);

        btnGetData = (Button) view.findViewById(R.id.btnData);
        btnGetData.setEnabled(false);
        btnGetData.setOnClickListener(this);

        txt = (TextView) view.findViewById(R.id.returnText);
        txt.setOnClickListener(this);

        txtCo = (TextView) view.findViewById(R.id.textCo);
        txt.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.btnReset:
                System.out.println("toto");
                bt.send("AT+W_REG=00;0105;00000000",true);

                bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() {
                    public void onDataReceived(byte[] data, String message) {
                        //split the data
                        temp = message.split(";");

                        //parse hexa to Long and Long to second
                        hex = Long.parseLong(temp[2],16);
                        txt.setText(convert(hex));

                    }
                });
                break;

            case R.id.btnData:
                bt.send("AT+R_REG=00;0105", true);
                bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() {
                    public void onDataReceived(byte[] data, String message) {
                        // Do something when data incoming

                        System.out.println(temp);
                        //split the data
                        temp = message.split(";");

                        //parse hexa to Long and Long to second
                        hex = Long.parseLong(temp[2],16);

                        //set Text
                        txt.setText(convert(hex));

                    }
                });
                break;

            case R.id.btnCo:
                bt.startService(BluetoothState.DEVICE_OTHER);
                Intent intent = new Intent(getActivity(), DeviceList.class);
                intent.putExtra("bluetooth_devices", "Bluetooth devices");
                intent.putExtra("no_devices_found", "No device");
                intent.putExtra("scanning", "Scanning");
                intent.putExtra("scan_for_devices", "Search");
                intent.putExtra("select_device", "Select");
                startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
                break;
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        alert();
        if(requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if(resultCode == Activity.RESULT_OK)
                bt.connect(data);
        } else if(requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if(resultCode == Activity.RESULT_OK) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_ANDROID);
            }
        }
    }

    //Loader for receipt the data
    private void alert() {

        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Wait");
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please wait for the receipt of the data");
        progressDialog.show();

        Runnable progressRunnable = new Runnable() {

            @Override
            public void run() {
                progressDialog.cancel();
            }
        };

        Handler pdCanceller = new Handler();
        pdCanceller.postDelayed(progressRunnable, 5000);

    }


    @Override
    public void onResume() {
        super.onResume();
        if(bt.isBluetoothEnabled() && bt.getPairedDeviceAddress() !=null){
            btnreset.setEnabled(true);
            btnGetData.setEnabled(true);
            txtCo.setText(R.string.text_co);

        }else{
            btnreset.setEnabled(false);
            btnGetData.setEnabled(false);
            txtCo.setText(R.string.text_Notco);

        }
        applyPref();
    }

    protected String convert(long hex){
        //parse hexa to Long and Long to second
        hex = Long.parseLong(temp[2],16);
        hex = (long) Math.floor(hex * 60 * 1000);

        //cast Long to date HH/MM/SS
        Date date = new Date(hex);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));

        return(sdf.format(date));
    }

    // Méthode pour appliquer les préférences :
    protected void applyPref() {
        // - récupérer les valeurs choisies par l'utilisateur


    }
}
