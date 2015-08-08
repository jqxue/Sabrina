package edu.stanford.thingengine.sabrina.channels.generic;

import android.content.Context;

import edu.stanford.thingengine.sabrina.exceptions.RuleExecutionException;

/**
 * Created by gcampagn on 5/18/15.
 */
public interface RuleRunnable {
    void run(Context ctx, GenericChannel channel) throws RuleExecutionException;
}
