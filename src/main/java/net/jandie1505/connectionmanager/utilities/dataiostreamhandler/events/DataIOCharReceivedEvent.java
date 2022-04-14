package net.jandie1505.connectionmanager.utilities.dataiostreamhandler.events;

import net.jandie1505.connectionmanager.CMClient;
import net.jandie1505.connectionmanager.utilities.dataiostreamhandler.DataIOStreamHandler;

public class DataIOCharReceivedEvent extends DataIOEvent {
    private char data;

    public DataIOCharReceivedEvent(DataIOStreamHandler dataIOStreamHandler, CMClient client, char data) {
        super(dataIOStreamHandler, client);
        this.data = data;
    }

    public char getData() {
        return this.data;
    }
}
