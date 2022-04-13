package net.jandie1505.connectionmanager;

import net.jandie1505.connectionmanager.server.CMSClient;

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
