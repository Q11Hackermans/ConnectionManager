package net.jandie1505.connectionmanager.streams;

import net.jandie1505.connectionmanager.CMClient;

import java.io.IOException;
import java.io.OutputStream;

public class CMOutputStream extends OutputStream {
    CMClient client;

    public CMOutputStream(CMClient client) {
        this.client = client;
    }

    @Override
    public void write(int b) throws IOException {
        this.client.sendByte(b);
    }
}
