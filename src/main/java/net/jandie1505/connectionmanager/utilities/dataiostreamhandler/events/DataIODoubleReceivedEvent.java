package net.jandie1505.connectionmanager.utilities.dataiostreamhandler.events;

import net.jandie1505.connectionmanager.CMClient;
import net.jandie1505.connectionmanager.utilities.dataiostreamhandler.DataIOStreamHandler;

public class DataIODoubleReceivedEvent extends DataIOEvent {
    private double data;

    public DataIODoubleReceivedEvent(DataIOStreamHandler dataIOStreamHandler, CMClient client, double data) {
        super(dataIOStreamHandler, client);
        this.data = data;
    }

    public double getData() {
        return this.data;
    }
}
