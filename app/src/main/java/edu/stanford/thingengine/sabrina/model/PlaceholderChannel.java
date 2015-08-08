package edu.stanford.thingengine.sabrina.model;

import edu.stanford.thingengine.sabrina.exceptions.UnknownObjectException;

/**
 * Created by gcampagn on 5/10/15.
 */
public class PlaceholderChannel extends Channel {
    private final String text;

    public PlaceholderChannel(ChannelFactory factory, String url, String text) {
        super(factory, url);
        this.text = text;
    }

    public Channel resolve() throws UnknownObjectException {
        return ObjectDatabase.get().resolve(ChannelPool.get(), this);
    }

    @Override
    public boolean isPlaceholder() {
        return true;
    }

    @Override
    public String toHumanString() {
        return text;
    }
}
