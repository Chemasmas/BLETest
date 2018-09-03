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

public class BLEServiceViewAdapter extends RecyclerView.Adapter<BLEServiceViewAdapter.ViewHolder> {

    private List<BluetoothGattService> viewList;




    public BLEServiceViewAdapter(List<BluetoothGattService> viewList) {
        this.viewList = viewList;
    }

    @NonNull
    @Override
    public BLEServiceViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.bledevice_view,parent,false);
        ButterKnife.bind(itemView);

        BLEServiceViewAdapter.ViewHolder vh = new BLEServiceViewAdapter.ViewHolder(itemView);
        vh.context = parent.getContext();

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull BLEServiceViewAdapter.ViewHolder holder, int position) {
        BluetoothGattService bd = viewList.get(position);

        holder.service = bd;
        holder.nombre.setText(bd.getUuid().toString());

    }

    @Override
    public int getItemCount() {
        return viewList.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {

        BluetoothGattService service;
        public Context context;

        @BindView(R.id.nombre)
        TextView nombre;


        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }

        @OnClick(R.id.conectar)
        public void conectar() {
            service.getCharacteristics();
        }

    }
}
