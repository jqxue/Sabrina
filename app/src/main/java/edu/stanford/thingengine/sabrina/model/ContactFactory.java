package edu.stanford.thingengine.sabrina.model;

/**
 * Created by gcampagn on 5/9/15.
 */
public abstract class ContactFactory extends ObjectPool.ObjectFactory<Contact> {
    protected ContactFactory(String prefix) {
        super(prefix);
    }
}
