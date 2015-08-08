package edu.stanford.thingengine.sabrina.model;

import edu.stanford.thingengine.sabrina.channels.android.ContentProviderContactFactory;
import edu.stanford.thingengine.sabrina.channels.android.TelephoneContactFactory;
import edu.stanford.thingengine.sabrina.channels.email.EmailContactFactory;

/**
 * Created by gcampagn on 5/9/15.
 */
public class ContactPool extends ObjectPool<Contact, ContactFactory> {
    public static final String KIND = "contact";
    public static final String PLACEHOLDER_PREFIX = ObjectPool.PLACEHOLDER_PREFIX + KIND + "/";

    private static final ContactPool instance = new ContactPool();

    public static ContactPool get() {
        return instance;
    }

    private ContactPool() {
        super(KIND);

        registerFactory(new TelephoneContactFactory());
        registerFactory(new EmailContactFactory());
        registerFactory(new ContentProviderContactFactory());
    }
}
