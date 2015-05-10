package edu.stanford.braincat.rulepedia.channels;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;

import edu.stanford.braincat.rulepedia.model.Channel;
import edu.stanford.braincat.rulepedia.model.ChannelFactory;

/**
 * Created by gcampagn on 5/8/15.
 */
public abstract class ScriptableChannel extends Channel {
    private final Context ctx;
    private final Scriptable global;

    public ScriptableChannel(ChannelFactory factory, String url) {
        super(factory, url);

        ctx = Context.enter();
        global = ctx.initSafeStandardObjects();
    }

    public Function compileFunction(String body) {
        return ctx.compileFunction(global, body, "channels.json", 1, null);
    }

    public Object callFunction(Function function, Object... args) {
        return function.call(ctx, global, null, args);
    }
}