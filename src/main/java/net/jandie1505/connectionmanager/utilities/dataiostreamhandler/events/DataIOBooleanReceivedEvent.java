package net.jandie1505.connectionmanager.utilities.dataiostreamhandler.events;

import net.jandie1505.connectionmanager.CMClient;
import net.jandie1505.connectionmanager.utilities.dataiostreamhandler.DataIOStreamHandler;

public class DataIOBooleanReceivedEvent extends DataIOEvent {
    private boolean data;

    public DataIOBooleanReceivedEvent(DataIOStreamHandler dataIOStreamHandler, CMClient client, boolean data) {
        super(dataIOStreamHandler, client);
        this.data = data;
    }

    public boolean getData() {
        return data;
    }
}
