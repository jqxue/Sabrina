package edu.stanford.thingengine.sabrina.channels.android;

import edu.stanford.thingengine.sabrina.model.Channel;

/**
 * Created by gcampagn on 5/9/15.
 */
public class NotificationManagerChannel extends Channel {
    public NotificationManagerChannel(NotificationManagerChannelFactory factory, String url) {
        super(factory, url);
    }

    @Override
    public String toHumanString() {
        return "notifications";
    }
}
