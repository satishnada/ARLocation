package ar.location.app;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

public class ArLocationApplication extends MultiDexApplication {

    private static ArLocationApplication mInstance;

    public static synchronized ArLocationApplication getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(ArLocationApplication.this);
    }
}
