package jatdevelopment.mx.pruebable;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.orhanobut.logger.Logger;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Splash extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 4242;
    private static final int REQUEST_COARSE_LOCATION = 2424;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothManager bluetoothManager;
    private static final long SCAN_PERIOD = 10000;

    private String SERVICE_UUID = "FFF0";
    private String BASE_UUID_16 = "0000{uuid}-0000-1000-8000-00805F9B34FB";
    private String BASE_UUID_32 = "{uuid}-0000-1000-8000-00805F9B34FB";

    private boolean mScanning;
    private Handler mHandler;
    private UUID[] uiids = { UUID.fromString(BASE_UUID_16.replace("{uuid}",SERVICE_UUID)) };
    static boolean enable = true;



    @BindView(R.id.rv1)
    RecyclerView rv1;

    @BindView(R.id.rv2)
    RecyclerView rv2;

    @BindView(R.id.pdfView)
    PDFView pdfView;

    BLEDeviceViewAdapter viewAdapter;
    List<BluetoothDevice> devices;

    BLEServiceViewAdapter serviceAdapter;
    List<BluetoothGattService> services;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Logger.d("Iniciando");
        setContentView(R.layout.activity_splash);

        ButterKnife.bind(this);

        InputStream is = getResources().openRawResource(R.raw.paginas);
        pdfView.fromStream(is).load();

        mHandler = new Handler();
        devices = new ArrayList<>();
        viewAdapter = new BLEDeviceViewAdapter(devices);

        services = new ArrayList<>();
        serviceAdapter = new BLEServiceViewAdapter(services);

        RecyclerView.LayoutManager manager = new LinearLayoutManager(getApplicationContext());
        rv1.setLayoutManager(manager);
        rv1.setItemAnimator(new DefaultItemAnimator());
        rv1.setAdapter(viewAdapter);
        viewAdapter.setRefAdapter(serviceAdapter,services);

        RecyclerView.LayoutManager manager2 = new LinearLayoutManager(getApplicationContext());
        rv2.setLayoutManager(manager2);
        rv2.setItemAnimator(new DefaultItemAnimator());
        rv2.setAdapter(serviceAdapter);


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_COARSE_LOCATION);
        }

        bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        if(bluetoothManager != null){
            mBluetoothAdapter = bluetoothManager.getAdapter();
            if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
            else{
                Toast.makeText(this, "Enabled", Toast.LENGTH_SHORT).show();
            }

        }

    }

    protected void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_COARSE_LOCATION);
        } else {
            if (mBluetoothAdapter.isDiscovering()) {
                mBluetoothAdapter.cancelDiscovery();
            }
            Toast.makeText(this, "Buscando", Toast.LENGTH_SHORT).show();
            mBluetoothAdapter.startDiscovery();
        }
    }


    @OnClick(R.id.scan)
    void scanLE() {
        if(enable){
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                }
            },SCAN_PERIOD);
            mScanning = true;
            //mBluetoothAdapter.startLeScan(mLeScannCallback);
            devices.clear();
            mBluetoothAdapter.startLeScan(uiids,mLeScanCallback);
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(resultCode){
            case REQUEST_ENABLE_BT:

                Toast.makeText(Splash.this, "Permiso Concecido", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, int rssi,
                                     byte[] scanRecord) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if(!devices.contains(device))
                            {
                                devices.add(device);
                                viewAdapter.notifyDataSetChanged();
                            }
                            Log.d("ok",device.getName());
                            Log.d("ok",device.toString());

                        }
                    });
                }
            };

}

