package com.example.othmane.horoflex;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnreset;
    private Button btnConnection;
    private BluetoothAdapter BA;

    BluetoothAdapter bluetoothAdapter;
    BluetoothDevice bluetoothDevice;

    static final int STATE_LISTENING = 1;
    static final int STATE_CONNECTING = 2;
    static final int STATE_CONNECTED = 3;
    static final int STATE_CONNECTION_FAILLED = 4;
    static final int STATE_MESSAGE_RECEIVED = 5;
    int REQUEST_ENABLE_BLUETOOTH = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnreset = (Button) findViewById(R.id.btnReset);
        btnreset.setOnClickListener(this);

        btnConnection = (Button) findViewById(R.id.btnCo);
        btnConnection.setOnClickListener(this);

        BA = BluetoothAdapter.getDefaultAdapter();


        if(!bluetoothAdapter.isEnabled()){
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BLUETOOTH);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.btnReset:

                //todo reset

                break;

            case R.id.btnCo:

                on();
                //todo connection with bluetooth

                break;
        }
    }

    public void on(){
        if (!BA.isEnabled()) {
            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOn, 0);
            Toast.makeText(getApplicationContext(), "Turned on",Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "Already on", Toast.LENGTH_LONG).show();
        }
    }

    public void off(View v){
        BA.disable();
        Toast.makeText(getApplicationContext(), "Turned off" ,Toast.LENGTH_LONG).show();
    }
}
