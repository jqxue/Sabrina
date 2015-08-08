package edu.stanford.thingengine.sabrina.omletUI;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import edu.stanford.thingengine.sabrina.channels.HTTPUtil;
import edu.stanford.thingengine.sabrina.channels.ibeacon.IBeaconDevice;
import edu.stanford.thingengine.sabrina.exceptions.UnknownObjectException;
import edu.stanford.thingengine.sabrina.model.DevicePool;

public class OmletUIService extends Service {
    public static final String WELCOME_USER = "edu.stanford.thingengine.sabrina.omlet.WELCOME_USER";
    public static final String NOTIFY_USER_NEW_DEVICE_DETECTED = "edu.stanford.thingengine.sabrina.omlet.NOTIFY_USER_NEW_DEVICE_DETECTED";
    public static final String SAY_RANDOM_QUOTES = "edu.stanford.thingengine.sabrina.omlet.SAY_RANDOM_QUOTES";


    public static final String LOG_TAG = "thingengine.sabrina.OmletUI";

    private String webHook;

    public OmletUIService() {
    }

    private boolean ensureWebHook() {
        if (webHook != null)
            return true;

        try {
            webHook = getSharedPreferences("omlet", MODE_PRIVATE).getString("webhook", null);
            if (webHook == null)
                return false;
            new URL(webHook);
            return true;
        } catch(MalformedURLException e) {
            webHook = null;
            return false;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null || intent.getAction() == null)
            return Service.START_REDELIVER_INTENT;

        if (!ensureWebHook()) {
            stopSelf(startId);
            return Service.START_REDELIVER_INTENT;
        }

        switch (intent.getAction()) {
            case WELCOME_USER:
                doWelcomeUser();
                break;

            case NOTIFY_USER_NEW_DEVICE_DETECTED:
                String url = intent.getStringExtra("URL");

                doNotifyUserNewDeviceDetected(url);
                break;
            case SAY_RANDOM_QUOTES:
                String quote = intent.getStringExtra("QUOTE");
                doSayRandomQuote(quote);
                break;

            default:
                break;
        }

        return Service.START_REDELIVER_INTENT;
    }

    private void sendMessage(final String message) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    HTTPUtil.postString(webHook, message);
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Failed to send message to Omlet!", e);
                }
            }
        });
    }

    private void doWelcomeUser() {
        sendMessage("Hello! My name is Sabrina, and I'm ready to use my magic power to help you!");
    }

    private void doNotifyUserNewDeviceDetected(String url)
    {
        try {
            IBeaconDevice ibd = (IBeaconDevice) DevicePool.get().getObject(url);
            String thingpediaURL = "https://thingpedia.stanford.edu/query/" + ibd.deviceType;
            sendMessage("Hello! I've detected a " + ibd.toHumanString() + " device with UUID " + ibd.uuid + "\n" +
                    "here is a list of spells that we could use with it " + thingpediaURL);
        } catch(UnknownObjectException e) {

        }
    }

    private void doSayRandomQuote(String quote)
    {
        sendMessage(quote);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
