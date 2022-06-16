package net.jandie1505.connectionmanager.events;

import net.jandie1505.connectionmanager.CMClient;

public class CMClientErrorEvent extends CMClientEvent {
    private final Exception exception;

    public CMClientErrorEvent(CMClient client, Exception exception) {
        super(client);
        this.exception = exception;
    }

    public Exception getException() {
        return exception;
    }
}
