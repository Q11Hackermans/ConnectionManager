package net.jandie1505.connectionmanager.server.events;

import net.jandie1505.connectionmanager.server.CMSClient;

public class CMSClientStringInputReceivedEvent extends CMSClientEvent {
    private String input;

    public CMSClientStringInputReceivedEvent(CMSClient client, String input) {
        super(client);
        this.input = input;
    }

    public String getInput() {
        return input;
    }
}
