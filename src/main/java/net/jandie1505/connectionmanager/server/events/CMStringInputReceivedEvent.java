package net.jandie1505.connectionmanager.server.events;

import net.jandie1505.connectionmanager.server.CMServerClient;

public class CMStringInputReceivedEvent extends CMEvent {
    private String input;

    public CMStringInputReceivedEvent(CMServerClient client, String input) {
        super(client);
        this.input = input;
    }

    public String getInput() {
        return input;
    }
}
