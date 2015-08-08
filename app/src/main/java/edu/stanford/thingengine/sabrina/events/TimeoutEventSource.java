package edu.stanford.thingengine.sabrina.events;

import android.content.Context;
import android.os.Handler;

/**
 * Created by gcampagn on 4/30/15.
 */
public class TimeoutEventSource implements EventSource, Runnable {
    private final long timeout;
    private Handler handler;
    private boolean triggered;

    public TimeoutEventSource(long timeout) {
        if (timeout <= 0)
            throw new IllegalArgumentException("timeout must be positive");

        this.timeout = timeout;
    }

    @Override
    public void run() {
        if (handler != null)
            triggered = true;
    }

    private void post() {
        triggered = false;
        handler.postDelayed(this, timeout);
    }

    @Override
    public void install(Context ctx, EventSourceHandler handler) {
        this.handler = handler;
        post();
    }

    @Override
    public void uninstall(Context ctx) {
        handler = null;
        triggered = false;
    }

    @Override
    public boolean checkEvent() {
        return triggered;
    }

    @Override
    public void updateState() {
        if (checkEvent()) {
            post();
        }
    }
}
