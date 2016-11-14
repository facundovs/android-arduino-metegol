package edu.soa.tp.services;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by sbogado on 17/10/16.
 */

public class UDPConectionService extends IntentService{

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public UDPConectionService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        
    }
}

