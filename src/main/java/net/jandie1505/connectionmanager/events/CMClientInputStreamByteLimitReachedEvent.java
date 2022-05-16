package net.jandie1505.connectionmanager.events;

import net.jandie1505.connectionmanager.CMClient;
import net.jandie1505.connectionmanager.streams.CMInputStream;

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
