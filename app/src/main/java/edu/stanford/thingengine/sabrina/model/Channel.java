package edu.stanford.thingengine.sabrina.model;

import android.content.Context;

import java.io.IOException;

import edu.stanford.thingengine.sabrina.events.EventSourceHandler;
import edu.stanford.thingengine.sabrina.exceptions.UnknownObjectException;

/**
 * Created by gcampagn on 4/30/15.
 */
public abstract class Channel extends ObjectPool.Object<Channel, ChannelFactory> {
    protected Channel(ChannelFactory factory, String url) {
        super(factory, url);
    }

    public Channel resolve() throws UnknownObjectException {
        return this;
    }

    public void enable(Context ctx, EventSourceHandler handler) throws IOException {

    }

    public void disable(Context ctx) throws IOException {

    }
}
