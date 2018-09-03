package jatdevelopment.mx.pruebable;

import android.app.Application;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;


public class BLESingleton extends Application {

    public static BLESingleton instance;
    public static String TAG = "BLETest";

    public static BLESingleton getInstance(){
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        //Inicializar logger
        Logger.addLogAdapter(new AndroidLogAdapter());

    }


}
