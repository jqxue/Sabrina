package edu.stanford.thingengine.sabrina.omletUI;

import android.app.Service;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import edu.stanford.thingengine.sabrina.channels.HTTPUtil;
import edu.stanford.thingengine.sabrina.channels.ibeacon.IBeaconDevice;
import edu.stanford.thingengine.sabrina.channels.omlet.OmletMessage;
import edu.stanford.thingengine.sabrina.exceptions.UnknownObjectException;
import edu.stanford.thingengine.sabrina.model.DevicePool;
import mobisocial.osm.IOsmService;

public class OmletUIService extends Service {
    public static final String WELCOME_USER = "edu.stanford.thingengine.sabrina.omlet.WELCOME_USER";
    public static final String NOTIFY_USER_NEW_DEVICE_DETECTED = "edu.stanford.thingengine.sabrina.omlet.NOTIFY_USER_NEW_DEVICE_DETECTED";
    public static final String SAY_RANDOM_QUOTES = "edu.stanford.thingengine.sabrina.omlet.SAY_RANDOM_QUOTES";

    public static final String LOG_TAG = "sabrina.OmletUI";

    private ServiceConnection omletServiceConnection;
    private IOsmService omletService;
    private String webHook;
    private String feedUri;
    private String userName;
    private final Set<Long> userOmletIds = new HashSet<>();

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

    private boolean ensureOmletFeed() {
        if (feedUri != null)
            return true;

        feedUri = getSharedPreferences("omlet", MODE_PRIVATE).getString("feedUri", null);
        return feedUri != null;
    }

    private boolean ensureUserId() {
        if (!userOmletIds.isEmpty())
            return true;

        ContentResolver resolver = getContentResolver();
        try (Cursor cursor = resolver.query(Uri.parse("content://mobisocial.osm/identities"), new String[] { "id", "principal", "name", "hasApp" },
                "owned = 1", null, null)) {
            if (cursor == null) {
                Log.e(LOG_TAG, "Can't get cursor to identities list");
                return false;
            }

            if (!cursor.moveToFirst()) {
                Log.e(LOG_TAG, "Can't find Omlet owner in identities list");
                return false;
            }

            while (!cursor.isAfterLast()) {
                Log.i(LOG_TAG, "Sabrina owner is " + cursor.getString(1) + ", " + cursor.getString(2));
                Log.i(LOG_TAG, "Sabrina owner has id " + cursor.getLong(0) + ", has app " + cursor.getInt(3));
                userOmletIds.add(cursor.getLong(0));
                if (userName == null)
                    userName = cursor.getString(2);
                cursor.moveToNext();
            }
        }

        return !userOmletIds.isEmpty();
    }

    private void sabrinaCommandReceived(String command) {
        // do something with command
        Log.i(LOG_TAG, "Received command to Sabrina chat: " + command);

        if (command.trim().equalsIgnoreCase("hi sabrina"))
            sendMessage("Hello " + userName + "!");
    }

    private static class OmletMessageHandler extends Handler {
        private static final int OBJECT_ADDED = 1;

        // we need a strong reference, not a weak one, or we'll lose
        // the connection to Omlet!
        private OmletUIService self;

        public OmletMessageHandler(OmletUIService owner) {
            self = owner;
        }

        private void messageAdded(OmletMessage message) {
            if (!self.ensureOmletFeed() || !self.ensureUserId() || !self.ensureWebHook())
                return;

            if (!message.getFeedUri().equals(self.feedUri))
                return;


            if (!message.cacheAll(self))
                return;
            long senderId = message.getSenderId(null);
            Log.d(LOG_TAG, "Received message on Sabrina chat from: " + senderId);
            if (!self.userOmletIds.contains(senderId))
                return;

            if (message.getType().equals("text"))
                self.sabrinaCommandReceived(message.getText(null));
        }

        @Override
        public void handleMessage(Message message) {
            if (message.what == OBJECT_ADDED)
                messageAdded(OmletMessage.fromBundle(message.getData()));
        }
    }

    private void setOmletService(IOsmService service) {
        omletService = service;
    }

    private class OmletServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            setOmletService(IOsmService.Stub.asInterface(service));
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            setOmletService(null);
        }
    }

    @Override
    public void onCreate() {
        Intent intent = new Intent("mobisocial.intent.action.BIND_SERVICE");
        intent.setPackage("mobisocial.omlet");
        intent.putExtra("mobisocial.intent.extra.OBJECT_RECEIVER", new Messenger(new OmletMessageHandler(this)));
        omletServiceConnection = new OmletServiceConnection();
        bindService(intent, omletServiceConnection, BIND_AUTO_CREATE);
    }

    @Override
    public void onDestroy() {
        unbindService(omletServiceConnection);
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

    private void doNotifyUserNewDeviceDetected(String url) {
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
