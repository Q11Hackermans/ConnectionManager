package net.jandie1505.connectionmanager.client.events;

import net.jandie1505.connectionmanager.client.CMCClient;

public class CMCInputReceivedEvent extends CMCEvent {
    int data;

    public CMCInputReceivedEvent(CMCClient client, int data) {
        super(client);
        this.data = data;
    }

    public int getData() {
        return this.data;
    }
}
