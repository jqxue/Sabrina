package edu.stanford.thingengine.sabrina.channels.googlefit;

import java.util.Map;

import edu.stanford.thingengine.sabrina.exceptions.TriggerValueTypeException;
import edu.stanford.thingengine.sabrina.exceptions.UnknownChannelException;
import edu.stanford.thingengine.sabrina.exceptions.UnknownObjectException;
import edu.stanford.thingengine.sabrina.model.Action;
import edu.stanford.thingengine.sabrina.model.Channel;
import edu.stanford.thingengine.sabrina.model.ChannelFactory;
import edu.stanford.thingengine.sabrina.model.ChannelPool;
import edu.stanford.thingengine.sabrina.model.PlaceholderChannel;
import edu.stanford.thingengine.sabrina.model.Trigger;
import edu.stanford.thingengine.sabrina.model.Value;

/**
 * Created by gcampagn on 5/13/15.
 */
public class GoogleFitChannelFactory extends ChannelFactory {
    public static final String ID = "google-fit";
    public static final String END_ACTIVITY = "end-activity";
    public static final String FETCH_HISTORY = "fetch-history-data";
    public static final String FETCH_CURRENT = "fetch-current-data";
    public static final String DATA_TYPE = "data-type";
    public static final String AGGREGATE_PERIOD = "aggregate-period";
    public static final String ACTIVITY_FILTER = "activity-filter";

    public GoogleFitChannelFactory() {
        super(ChannelPool.PREDEFINED_PREFIX + ID);
    }

    @Override
    public Class<? extends Value> getParamType(String method, String name) throws UnknownChannelException, TriggerValueTypeException {
        switch (method) {
            case END_ACTIVITY:
                throw new TriggerValueTypeException("unknown parameter " + name);
            case FETCH_HISTORY:
                switch (name) {
                    case DATA_TYPE:
                        return HistoryDataTypeValue.class;
                    case AGGREGATE_PERIOD:
                        return Value.Number.class;
                    case ACTIVITY_FILTER:
                        return Value.Text.class;
                    default:
                        throw new TriggerValueTypeException("unknown parameter " + name);
                }
            case FETCH_CURRENT:
                switch (name) {
                    case DATA_TYPE:
                        return CurrentDataTypeValue.class;
                    default:
                        throw new TriggerValueTypeException("unknown parameter " + name);
                }
            default:
                throw new UnknownChannelException(method);
        }
    }

    @Override
    public Trigger createTrigger(Channel channel, String method, Map<String, Value> params) throws UnknownObjectException, UnknownChannelException, TriggerValueTypeException {
        switch (method) {
            case END_ACTIVITY:
                return new EndActivityTrigger(channel);
            default:
                throw new UnknownChannelException(method);
        }
    }

    @Override
    public Action createAction(Channel channel, String method, Map<String, Value> params) throws UnknownObjectException, UnknownChannelException, TriggerValueTypeException {
        switch (method) {
            case FETCH_HISTORY:
                return new FetchHistoryDataAction(channel, (HistoryDataTypeValue) params.get(DATA_TYPE).resolve(null),
                        params.get(AGGREGATE_PERIOD), params.get(ACTIVITY_FILTER));
            case FETCH_CURRENT:
                return new FetchCurrentDataAction(channel, (CurrentDataTypeValue) params.get(DATA_TYPE).resolve(null));
            default:
                throw new UnknownChannelException(method);
        }
    }

    @Override
    public Channel create(String url) throws UnknownObjectException {
        if (getPrefix().equals(url))
            return new GoogleFitChannel(this, url);
        else
            throw new UnknownObjectException(url);
    }

    @Override
    public Channel createPlaceholder(String url) {
        return new PlaceholderChannel(this, url, "Google Fit");
    }

    @Override
    public String getName() {
        return ID;
    }
}
