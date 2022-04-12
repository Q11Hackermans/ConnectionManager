package net.jandie1505.connectionmanager.server.events;

import net.jandie1505.connectionmanager.server.CMSClient;

public class CMSClientByteReceivedEvent extends CMSClientEvent {
    int data;

    public CMSClientByteReceivedEvent(CMSClient client, int data) {
        super(client);
        this.data = data;
    }

    public int getData() {
        return this.data;
    }
}
