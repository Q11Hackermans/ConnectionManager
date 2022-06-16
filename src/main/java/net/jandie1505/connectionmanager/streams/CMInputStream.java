package net.jandie1505.connectionmanager.streams;

import net.jandie1505.connectionmanager.interfaces.StreamOwner;

import java.io.InputStream;

public abstract class CMInputStream extends InputStream {
    private final StreamOwner owner;


    public CMInputStream(StreamOwner owner) {
        this.owner = owner;
    }

    @Override
    public abstract int read();

    @Deprecated
    public abstract void send(int b);

    public StreamOwner getOwner() {
        return this.owner;
    }
}
