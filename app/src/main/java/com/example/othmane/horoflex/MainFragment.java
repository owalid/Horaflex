package com.example.othmane.horoflex;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceFragment;
import android.app.Fragment;
import android.text.InputType;
import android.util.TimeUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
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
public class MainFragment extends Fragment implements View.OnClickListener {

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
    private final static int REQUEST_CODE_ENABLE_BLUETOOTH = 0;


    private final String params = "";


    private Button btnreset, btnConnection, btnGetData, btnPref, btnPara;
    private TextView txt, txt_action_getData;
    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();


    BluetoothSPP bt = new BluetoothSPP(getContext());
    String[] temp;
    long hex;
    Date date = new Date();
    private String str_action = "";
    boolean time;

    //---TODO CYCLE DE VIE DE LAPPLI..done
    //-----TODO PB DE TROP LONG VALEUR DANS CONVERT...done
    //--------TODO ADD LANGUE FRANCAISE..done
    //----------TODO pouvoir parameterer hora et trigger..done


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        //init buttons
        btnreset = (Button) view.findViewById(R.id.btnReset);
        btnreset.setOnClickListener(this);

        btnConnection = (Button) view.findViewById(R.id.btnCo);
        btnConnection.setOnClickListener(this);

        btnGetData = (Button) view.findViewById(R.id.btnData);
        btnGetData.setEnabled(false);
        btnGetData.setOnClickListener(this);

        btnPref = (Button) view.findViewById(R.id.btnPref);
        btnPref.setEnabled(false);
        btnPref.setOnClickListener(this);

        btnPara = (Button) view.findViewById(R.id.btnPara);
        btnPara.setEnabled(false);
        btnPara.setOnClickListener(this);

        //init text
        txt = (TextView) view.findViewById(R.id.returnText);
        txt_action_getData = (TextView) view.findViewById(R.id.action_getData);

        //start the connection
        bt.setupService();
        bt.enable();


        if (bluetoothAdapter == null)
            Toast.makeText(getActivity(), "Pas de Bluetooth",
                    Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(getActivity(), "Bluetooth activé",
                    Toast.LENGTH_SHORT).show();

        bt.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() {
            public void onDeviceConnected(String name, String address) {
                String btnConnectionText = getResources().getString(R.string.Connected_to) + " " + name;
                btnConnection.setText(btnConnectionText);
                btnPref.setEnabled(true);
                btnGetData.setEnabled(true);
            }

            public void onDeviceDisconnected() {
                btnConnection.setText(R.string.Unable_to_connect);
                btnGetData.setEnabled(false);
                btnreset.setEnabled(false);
                btnPref.setEnabled(false);
            }

            public void onDeviceConnectionFailed() {
                btnConnection.setText(R.string.Connection_lost);
                btnGetData.setEnabled(false);
                btnreset.setEnabled(false);
                btnPref.setEnabled(false);
            }
        });

        return view;
    }



    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btnReset:
                if (!(str_action.equals(NO_DATA)) && time(str_action)) {
                    sendMessage("AT+W_REG=00;0105;00000000");
                } else {
                    txt.setText(NO_DATA + " TO RESET");
                }
                break;

            case R.id.btnData:
                if (!(str_action.equals(NO_DATA))) {
                    sendMessage(str_action);
                } else {
                    txt.setText(NO_DATA);
                }
                break;

            case R.id.btnCo:
                verifBluetooth();
                break;

            case R.id.btnPref:
                onCreateDialog();
                break;

            case R.id.btnPara:
                alertConfig();
                break;
        }
    }
        public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            alert();
            if (resultCode == Activity.RESULT_OK)
                bt.connect(data);
        }
    }

    //=================================MODALES==========================
    //Loader for receipt the data
    private void alert() {
        String content = "";
        content = getResources().getString(R.string.wait_popup_content);
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle(R.string.wait_popup_title);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(content);
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

    public void onCreateDialog() {
        final String[] mSelectedItems;// Where we track the selected items
        mSelectedItems = getResources().getStringArray(R.array.read_pref);
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Set the dialog title
        builder.setTitle(R.string.setting_action)
                // Specify the list array, the items to be selected by default (null for none),
                // and the listener through which to receive callbacks when items are selected
                .setSingleChoiceItems(R.array.read_pref, -1,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                txt_action_getData.setText(mSelectedItems[which]);
                                switch (which){


                                    case 0  :
                                        str_action = FIRMWARE;
                                        btnreset.setEnabled(false);
                                        btnPara.setEnabled(false);

                                        break;

                                    case 1:
                                        str_action = STATE_INPUT;
                                        btnreset.setEnabled(false);
                                        btnPara.setEnabled(false);

                                        break;

                                    case 2:
                                        str_action = STATE_REST;
                                        btnreset.setEnabled(false);
                                        btnPara.setEnabled(false);

                                        break;


                                    case 3:
                                        str_action = TRIGGER1;
                                        btnreset.setEnabled(false);
                                        btnPara.setEnabled(true);
                                        break;

                                    case 4:
                                        str_action = TRIGGER2;
                                        btnreset.setEnabled(false);
                                        btnPara.setEnabled(true);

                                        break;

                                    case 5:
                                        str_action = TRIGGER3;
                                        btnreset.setEnabled(false);
                                        btnPara.setEnabled(true);

                                        break;

                                    case 6:
                                        str_action = HORAMETRE1;
                                        btnreset.setEnabled(true);
                                        btnPara.setEnabled(true);

                                        break;

                                    case 7:
                                        str_action = HORAMETRE2;
                                        btnreset.setEnabled(true);
                                        btnPara.setEnabled(true);

                                        break;

                                    case 8:
                                        str_action = HORAMETRE3;
                                        btnreset.setEnabled(true);
                                        btnPara.setEnabled(true);

                                        break;

                                    case 9:
                                        str_action = ADC1;
                                        btnreset.setEnabled(false);
                                        btnPara.setEnabled(false);

                                        break;

                                    case 10:
                                        str_action = ADC2;
                                        btnreset.setEnabled(false);
                                        btnPara.setEnabled(false);

                                        break;

                                    case 11:
                                        str_action = ADC3;
                                        btnreset.setEnabled(false);
                                        btnPara.setEnabled(false);

                                        break;
                                }

                            }
                        })
                // Set the action buttons
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("RETOUR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
         builder.show();
    }

    private void alertConfig(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        final String title = getResources().getString(R.string.pram_title_alert) + " " + txt_action_getData.getText().toString();
        builder.setTitle(title);

// Set up the input
        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

// Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {



                if(input.getText().toString().equals("")){
                    Toast.makeText(getActivity(), getResources().getString(R.string.error_param_ok),
                            Toast.LENGTH_SHORT).show();
                }else{
                    System.out.println();
                    //convert to hexa and create a query
                    String cmd = "";
                    char[] cmdTab;
                    cmd = str_action + ";"+toOct(toHex(input.getText().toString()));
                    cmdTab = cmd.toCharArray();
                    cmdTab[3] = 'W';
                    cmd = String.valueOf(cmdTab).toUpperCase();
                    sendMessage(cmd);
                }

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }
    //===========================END MODALES====================================



    //====================================UTILITIES==================================



    //================== SEND A MESSAGE str WITH BLUETOOTH================================
    public void sendMessage(final String str) {

        bt.send(str, true);
        bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() {
            public void onDataReceived(byte[] data, String message) {
                // Do something when data incoming
                if ((str.split(";")[1].equals("0105")) || (str.split(";")[1].equals("0106")) || (str.split(";")[1].equals("0107"))) {
                    btnreset.setEnabled(true);

                    //split the data
                    temp = message.split(";");
                    txt.setText(convert());

                    //set Text
                } else {
                    txt.setText(message.split(";")[2]);
                }
            }
        });
    }

    //==================== CONVERT HEXA TO DAY LIKE DD:HH:MM:SS================================
    protected String convert(){
        long hexad=0;
            //parse hexa to Long and Long to second
            hexad = Long.parseLong(temp[2],16);
            System.out.println("hexad:::::::::::" + hexad);

        //hexad = (long) Math.floor(hexad);

            //cast Long to date dd/HH/MM/SS

            long days = hexad / (86400);
            hexad -= days * 86400;
            long hours = hexad / 3600 ;
            hexad -= hours * 3600;
            long minutes = hexad / 60;
            hexad -= minutes * 60;
            long seconds = hexad;

        String varReturn = "";
            if(days == 1)
                varReturn = days + " jour " + hours + " h " + minutes + " mn " + seconds + " sec";
            else if (days == 0){
                varReturn = hours + " h " + minutes + " mn " + seconds + " sec";
            }
            else {
               varReturn = days + " jours " + hours + " h " + minutes + " mn " + seconds + " sec";
            }
        return(varReturn);
    }

    //======================= Convert string to hexa===============================================
    protected static String toHex(String str){

       return Long.toHexString(Integer.parseInt(str));
    }

    //========================= Convert string to bytes================================================
    protected static String toOct(String str){
        String zero = "";
        for (int i = str.length(); i< 8; i++){
            zero += "0";
        }
        return zero + str;
    }

    //=========================== TIME OR NOT ? ============================================================
    public boolean time(String str){
        return ((str.equals("AT+R_REG=00;0105")) || (str.equals("AT+R_REG=00;0106")) || (str.equals("AT+R_REG=00;0107")));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public void verifBluetooth(){
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBlueTooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBlueTooth, REQUEST_CODE_ENABLE_BLUETOOTH);
        }
        else{

           if(bt.getServiceState() != BluetoothState.REQUEST_CONNECT_DEVICE){
                bt.startService(BluetoothState.DEVICE_OTHER);
                Intent intent = new Intent(getActivity(), DeviceList.class);
                intent.putExtra("bluetooth_devices", R.string.intent_connect_blue);
                intent.putExtra("no_devices_found", R.string.intent_connect_no_device);
                intent.putExtra("scanning", R.string.intent_connect_scann);
                intent.putExtra("scan_for_devices", R.string.intent_connect_scann_for_device);
                intent.putExtra("select_device", R.string.intent_connect_select_device);
                startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
            }

        }

    }

    //Life cycle
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onResume() {
        super.onResume();
        //set the btn reset false
        if(!time){
            btnreset.setEnabled(false);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
