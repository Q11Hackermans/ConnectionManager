package net.jandie1505.connectionmanager.events;

import net.jandie1505.connectionmanager.CMClient;
import net.jandie1505.connectionmanager.streams.CMInputStream;
import net.jandie1505.connectionmanager.streams.CMTimedInputStream;

public class CMClientInputStreamByteLimitReachedEvent extends CMClientEvent {
    private final CMInputStream stream;

    public CMClientInputStreamByteLimitReachedEvent(CMClient client, CMInputStream inputStream) {
        super(client);
        this.stream = inputStream;
    }

    public CMInputStream getStream() {
        return stream;
    }
}
