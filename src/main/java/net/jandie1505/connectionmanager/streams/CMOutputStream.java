package net.jandie1505.connectionmanager.streams;

import net.jandie1505.connectionmanager.interfaces.ByteSender;

import java.io.IOException;
import java.io.OutputStream;

public class CMOutputStream extends OutputStream {
    ByteSender client;

    public CMOutputStream(ByteSender client) {
        this.client = client;
    }

    @Override
    public void write(int b) throws IOException {
        this.client.sendByte(b);
    }
}
