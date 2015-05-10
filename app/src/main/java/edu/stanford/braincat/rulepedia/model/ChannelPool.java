package edu.stanford.braincat.rulepedia.model;

import edu.stanford.braincat.rulepedia.channels.omdb.OMDBChannelFactory;
import edu.stanford.braincat.rulepedia.channels.android.SMSChannelFactory;
import edu.stanford.braincat.rulepedia.channels.time.TimerFactory;

/**
 * Created by gcampagn on 5/9/15.
 */
public class ChannelPool extends ObjectPool<Channel, ChannelFactory> {
    public static final String KIND = "channel";
    public static final String PREDEFINED_PREFIX = ObjectPool.PREDEFINED_PREFIX + KIND;
    public static final String PLACEHOLDER_PREFIX = ObjectPool.PLACEHOLDER_PREFIX + KIND;

    private static final ChannelPool instance = new ChannelPool();

    public static ChannelPool get() {
        return instance;
    }

    public ChannelPool() {
        super(KIND);

        registerFactory(new SMSChannelFactory());
        registerFactory(new TimerFactory());
        registerFactory(new OMDBChannelFactory());
    }


}
