package net.jandie1505.connectionmanager.server.utilities;

import net.jandie1505.connectionmanager.server.CMSClient;

import java.io.IOException;
import java.io.PrintWriter;

public class CMSStringMessageSender {
    private CMSClient client;
    private PrintWriter out;

    public CMSStringMessageSender(CMSClient client) throws IOException {
        this.client = client;
        out = new PrintWriter(this.client.getOutputStream(), true);
    }

    public CMSClient getClient() {
        return this.client;
    }

    public void sendStringMessage(String message) {
        out.println(message);
    }

    public void close() {
        out.close();
    }

    public static void sendMessage(CMSClient client, String message) throws IOException {
        CMSStringMessageSender messageSender = new CMSStringMessageSender(client);
        messageSender.sendStringMessage(message);
        messageSender.close();
    }
}
