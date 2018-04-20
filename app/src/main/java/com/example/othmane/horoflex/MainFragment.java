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


//======================================================
//==============Main Fragment===========================
//======================================================

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment implements View.OnClickListener{

    //const
    private final static String FIRMWARE = "AT+R_REG=00;0001";
    private final static String STATE_INPUT = "AT+R_REG=00;0100";
    private final static String STATE_REST = "AT+R_REG=00;0101";
    private final static String TRIGGER1 = "AT+R_REG=00;0102";
    private final static String TRIGGER2 = "AT+R_REG=00;0103";
    private final static String TRIGGER3 = "AT+R_REG=00;0104";
    private final static String HORAMETRE1 = "AT+R_REG=00;0105";
    private final static String HORAMETRE2 = "AT+R_REG=00;0106";
    private final static String HORAMETRE3 = "AT+R_REG=00;0107";
    private final static String ADC1 = "AT+R_REG=00;0108";
    private final static String ADC2 = "AT+R_REG=00;0109";
    private final static String ADC3 = "AT+R_REG=00;010A";
    private final static String NO_DATA = "NO DATA";




    private Button btnreset;
    private Button btnConnection;
    private Button btnGetData;
    private TextView txt;
    private TextView txtCo;
    private TextView txt_action_getData;
    private BluetoothAdapter BA;
    BluetoothSPP bt;
    String[] temp;
    long hex;
    Date date = new Date();
    PreferenceFragment preferenceFragment = new Prefs();
    private String str_action="";
    boolean time;




    //TODO CYCLE DE VIE DE LAPPLI
    //TODO PB DE TROP LONG VALEUR DANS CONVERT
    //TODO ADD LANGUE FRANCAISE
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

        //init button
        btnreset = (Button) view.findViewById(R.id.btnReset);
        btnreset.setOnClickListener(this);

        btnConnection = (Button) view.findViewById(R.id.btnCo);
        btnConnection.setOnClickListener(this);

        btnGetData = (Button) view.findViewById(R.id.btnData);
        btnGetData.setOnClickListener(this);

        //init text
        txt = (TextView) view.findViewById(R.id.returnText);
        txtCo = (TextView) view.findViewById(R.id.textCo);
        txtCo.setText(R.string.text_Notco);
        txt_action_getData = (TextView) view.findViewById(R.id.action_getData);

        return view;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.btnReset:
                if(!(str_action.equals(NO_DATA)) && time(str_action)){
                    bt.send("AT+W_REG=00;0105;00000000",true);
                }else{
                    txt.setText(NO_DATA + " TO RESET");
                }
                System.out.println("toto");


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
                if(!(str_action.equals(NO_DATA))){
                    System.out.println("--------------------------------------------  " + str_action + " ---------------------------------");

                    bt.send(str_action, true);
                }else{
                    txt.setText(NO_DATA);
                }
                bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() {
                    public void onDataReceived(byte[] data, String message) {
                        // Do something when data incoming

                        System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$  " + str_action.split(";")[1] + " $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");

                        if( (str_action.split(";")[1].equals("0105")) || (str_action.split(";")[1].equals("0106")) || (str_action.split(";")[1].equals("0107")) ){

                            btnreset.setEnabled(true);

                            //split the data
                            temp = message.split(";");

                            //parse hexa to Long and Long to second
                            hex = Long.parseLong(temp[2],16);

                            //set Text
                            txt.setText(convert(hex));
                        }else {
                            txt.setText(message.split(";")[2]);
                        }
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
                btnreset.setEnabled(true);
                btnGetData.setEnabled(true);
                txtCo.setText(R.string.text_co);
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

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String selected = sharedPreferences.getString("chosenAction","");

        txt_action_getData.setText(selected);

        //set the btn reset false
        if(!time){
            btnreset.setEnabled(false);
        }

        applyPref();
    }

    protected String convert(long hex){
        //parse hexa to Long and Long to second
        hex = Long.parseLong(temp[2],16);
        hex = (long) Math.floor(hex * 60 * 10);

        //cast Long to date HH/MM/SS
        Date date = new Date(hex);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        System.out.println("----------------------" + sdf.format(date) + "------------------------------------------");

        return(sdf.format(date));
    }

    // Méthode for apply setting :
    protected void applyPref() {
        // - récupérer les valeurs choisies par l'utilisateur

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String selected = sharedPreferences.getString("chosenAction","");

        switch (selected){

            case "Get the Firmware":

                str_action = FIRMWARE;
                    break;

            case "State input":

                str_action = STATE_INPUT;

                    break;

            case "Rest state input":

                str_action = STATE_REST;
                    break;

            case "Trigger1":

                str_action = TRIGGER1;
                    break;

            case "Trigger2":

                str_action = TRIGGER2;

                break;

            case "Trigger3":

                str_action = TRIGGER3;

                break;

            case "Horamètre1":

                time = true;
                str_action = HORAMETRE1;


                break;

                  case "Horamètre2":

                time = true;
                str_action = HORAMETRE2;


                break;

            case "Horamètre3":

                time =true;
                str_action = HORAMETRE3;


                break;

            case "ADC1":

                str_action = ADC1;


                break;

            case "ADC2":

                str_action = ADC2;

                break;

            case "ADC3":

                str_action = ADC3;

                break;
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putAll(outState);
    }

    public boolean time(String str){
        return ((str.equals("AT+R_REG=00;0105")) || (str.equals("AT+R_REG=00;0106")) || (str.equals("AT+R_REG=00;0107")));
    }
}
