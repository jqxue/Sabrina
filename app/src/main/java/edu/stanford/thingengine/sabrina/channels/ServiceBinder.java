package edu.stanford.thingengine.sabrina.channels;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import edu.stanford.thingengine.sabrina.events.EventSourceHandler;

/**
 * Created by gcampagn on 5/26/15.
 */
public class ServiceBinder implements ServiceConnection {
    private final Intent intent;
    private volatile IBinder service;

    public ServiceBinder(Intent intent) {
        this.intent = intent;
    }

    public IBinder getService() {
        return service;
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder binder) {
        service = binder;
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        service = null;
    }

    public void enable(Context ctx, EventSourceHandler handler) {
        ctx.bindService(intent, this, Context.BIND_AUTO_CREATE);
    }

    public void disable(Context ctx) {
        ctx.unbindService(this);
    }
}
