package net.jandie1505.connectionmanager.server;

import net.jandie1505.connectionmanager.CMClient;
import net.jandie1505.connectionmanager.CMClientEventListener;

import java.net.Socket;
import java.util.List;
import java.util.UUID;

/**
 * Server-side client (CMS = ConnectionManager Server)
 */
public class CMSClient extends CMClient {
    UUID id;

    public CMSClient(Socket socket) {
        super(socket);
    }

    public CMSClient(Socket socket, List<CMClientEventListener> listeners) {
        super(socket, listeners);
    }

    /**
     * Get the unique ID of the client
     * @return Unique ID (UUID)
     */
    public UUID getUniqueId() {
        return this.id;
    }
}
