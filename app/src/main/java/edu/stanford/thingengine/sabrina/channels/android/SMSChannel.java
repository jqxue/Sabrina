package edu.stanford.thingengine.sabrina.channels.android;

import edu.stanford.thingengine.sabrina.model.Channel;

/**
 * Created by gcampagn on 5/1/15.
 */
public class SMSChannel extends Channel {
    public static final String ID = "sms";

    private SMSEventSource eventSource;

    public SMSChannel(SMSChannelFactory factory, String url) {
        super(factory, url);
    }

    @Override
    public String toHumanString() {
        return "a text";
    }

    private void ensureEventSource() {
        if (eventSource != null)
            return;
        eventSource = new SMSEventSource();
    }

    public SMSEventSource getEventSource() {
        ensureEventSource();
        return eventSource;
    }
}
