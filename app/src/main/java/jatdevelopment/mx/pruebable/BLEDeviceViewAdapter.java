package jatdevelopment.mx.pruebable;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static jatdevelopment.mx.pruebable.BLESingleton.TAG;

public class BLEDeviceViewAdapter extends RecyclerView.Adapter<BLEDeviceViewAdapter.ViewHolder> {

    private List<BluetoothDevice> viewList;

    static BLEServiceViewAdapter refAdapter;
    static List<BluetoothGattService> refService;

    public void setRefAdapter(BLEServiceViewAdapter refAdapter,List<BluetoothGattService> refService) {
        this.refAdapter = refAdapter;
        this.refService = refService;
    }


    public BLEDeviceViewAdapter(List<BluetoothDevice> viewList) {
        this.viewList = viewList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.bledevice_view,parent,false);
        ButterKnife.bind(itemView);

        ViewHolder vh = new ViewHolder(itemView);
        vh.context = parent.getContext();

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BluetoothDevice bd = viewList.get(position);

        holder.device = bd;
        holder.nombre.setText(bd.getName());

    }

    @Override
    public int getItemCount() {
        return viewList.size();
    }


    private int mConnectionState = STATE_DISCONNECTED;

    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;

    public final static String ACTION_GATT_CONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA =
            "com.example.bluetooth.le.EXTRA_DATA";


    static class ViewHolder extends RecyclerView.ViewHolder {

        BluetoothDevice device;
        BluetoothGatt mBluetoothGatt;
        public Context context;

        @BindView(R.id.nombre)
        TextView nombre;


        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }

        @OnClick(R.id.conectar)
        public void conectar() {
            mBluetoothGatt = device.connectGatt(context, false, mGattCallback);

        }

        private final BluetoothGattCallback mGattCallback =
                new BluetoothGattCallback() {
                    @Override
                    public void onConnectionStateChange(BluetoothGatt gatt, int status,
                                                        int newState) {
                        String intentAction;
                        if (newState == BluetoothProfile.STATE_CONNECTED) {
                            intentAction = ACTION_GATT_CONNECTED;
                            Log.i(TAG, "Connected to GATT server.");
                            Log.i(TAG, "Attempting to start service discovery:" +
                                    mBluetoothGatt.discoverServices());


                        } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                            intentAction = ACTION_GATT_DISCONNECTED;

                            Log.i(TAG, "Disconnected from GATT server.");

                        }
                    }

                    @Override
                    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                        super.onServicesDiscovered(gatt, status);
                        if(status== BluetoothGatt.GATT_SUCCESS){
                            Log.i(TAG,"Exito");
                        }
                        if(status == BluetoothGatt.GATT_INSUFFICIENT_AUTHENTICATION){
                            Log.i(TAG,"Faltan permisos");
                        }

                        Log.i(TAG, "Service discovered" );
                        for(BluetoothGattService service : gatt.getServices()){
                            Log.i(TAG, service.getUuid().toString() );

                            if(service.getType() ==  BluetoothGattService.SERVICE_TYPE_PRIMARY){
                                Log.i(TAG, "Primario" );
                            }else{
                                Log.i(TAG, "Secundario" );
                            }
                            refService.add(service);
                            refAdapter.notifyDataSetChanged();
                            /*Log.i(TAG, "\tCaracteristica discovered" );
                            for(BluetoothGattCharacteristic caracteristica : service.getCharacteristics()){

                                Log.i(TAG, "\t"+caracteristica.getUuid().toString() );
                                Log.i(TAG, "\t"+Integer.parseInt(caracteristica.getProperties() + "" , 16) + "" );
                                Log.i(TAG, "\t"+Integer.parseInt(caracteristica.getProperties() + "" , 10) + "" );

                                Log.i(TAG, "\t\tDescriptores Discovered" );
                                for(BluetoothGattDescriptor descriptor: caracteristica.getDescriptors() ){
                                    Log.i(TAG, "\t\t"+descriptor.getUuid().toString() );
                                    Log.i(TAG, "\t\t"+descriptor.describeContents() + "" );
                                    Log.i(TAG, "\t\t"+descriptor.getPermissions() +"" );
                                    descriptor.getValue();
                                }

                            }*/
                        }

                    }

                    @Override
                    public void onPhyUpdate(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
                        super.onPhyUpdate(gatt, txPhy, rxPhy, status);
                    }

                    @Override
                    public void onPhyRead(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
                        super.onPhyRead(gatt, txPhy, rxPhy, status);
                    }

                    @Override
                    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                        super.onCharacteristicRead(gatt, characteristic, status);
                        Log.i(TAG, "Read From GATT server.");

                    }

                    @Override
                    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                        super.onCharacteristicWrite(gatt, characteristic, status);
                        Log.i(TAG, "Exritura to GATT server.");
                    }

                    @Override
                    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                        super.onCharacteristicChanged(gatt, characteristic);
                        Log.i(TAG, "CAmbio to GATT server.");
                    }

                    @Override
                    public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                        super.onDescriptorRead(gatt, descriptor, status);
                        Log.i(TAG, "Lectura to GATT server.");
                    }

                    @Override
                    public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                        super.onDescriptorWrite(gatt, descriptor, status);
                        Log.i(TAG, "Escritura to GATT server.");
                    }

                    @Override
                    public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
                        super.onReliableWriteCompleted(gatt, status);

                    }

                    @Override
                    public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
                        super.onReadRemoteRssi(gatt, rssi, status);
                    }

                    @Override
                    public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
                        super.onMtuChanged(gatt, mtu, status);
                    }
                };
    }
}
