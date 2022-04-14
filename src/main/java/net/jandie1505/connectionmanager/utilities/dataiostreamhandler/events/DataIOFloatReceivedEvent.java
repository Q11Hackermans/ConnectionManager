package net.jandie1505.connectionmanager.utilities.dataiostreamhandler.events;

import net.jandie1505.connectionmanager.CMClient;
import net.jandie1505.connectionmanager.utilities.dataiostreamhandler.DataIOStreamHandler;

public class DataIOFloatReceivedEvent extends DataIOEvent {
    private float data;

    public DataIOFloatReceivedEvent(DataIOStreamHandler dataIOStreamHandler, CMClient client, float data) {
        super(dataIOStreamHandler, client);
        this.data = data;
    }

    public float getData() {
        return this.data;
    }
}
