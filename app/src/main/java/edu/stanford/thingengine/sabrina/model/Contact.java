package edu.stanford.thingengine.sabrina.model;

import edu.stanford.thingengine.sabrina.exceptions.UnknownObjectException;

/**
 * Created by gcampagn on 5/9/15.
 */
public abstract class Contact extends ObjectPool.Object<Contact, ContactFactory> {
    protected Contact(ContactFactory factory, String url) {
        super(factory, url);
    }

    public Contact resolve() throws UnknownObjectException {
        return this;
    }
}
