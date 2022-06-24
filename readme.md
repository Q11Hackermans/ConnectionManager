# ConnectionManager

![](https://img.shields.io/badge/Java-11-blue)
[![](https://jitpack.io/v/jandie1505/ConnectionManager.svg)](https://jitpack.io/#jandie1505/ConnectionManager)
[![CodeFactor](https://www.codefactor.io/repository/github/jandie1505/connectionmanager/badge)](https://www.codefactor.io/repository/github/jandie1505/connectionmanager)
  
A java socket management library

## What is ConnectionManager

ConnectionManager is a library for managing Java TCP Socket Connections.  

### Features:  
- Socket and ServerSocket management
- Client management (of the clients connected to the server on server-side)
- Multiple InputStreams and OutputStreams to send/receive bytes
- Accept or refuse connections on server (via EventListener)
- EventListener with many Events (Created, Closed, Byte received, Connection attempt/refuse/accept, DataIO (String received, etc...), Error, Byte limit reached, ...)
- Sending Strings (or primitive types) via DataIO

## Import

### From Jitpack
```xml
<repositories>
  <repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
  </repository>
</repositories>
```
```xml
<dependency>
    <groupId>com.github.jandie1505</groupId>
    <artifactId>ConnectionManager</artifactId>
    <version>1.0</version>
</dependency>
```

### From GitHub Packages
```
<repository>
    <id>github</id>
    <url>https://maven.pkg.github.com/jandie1505/ConnectionManager</url>
    <snapshots>
        <enabled>true</enabled>
    </snapshots>
</repository>
```
```
<dependency>
    <groupId>net.jandie1505</groupId>
    <artifactId>connectionmanager</artifactId>
    <version>1.0</version>
</dependency>
```
Using Github Packages requires authentication.

## Getting started

ConnectionManager contains 3 parts:
- Server (CMS Prefix)
- Client (CMC Prefix)
- DataIO (DataIO Prefix)

### Example

This is an all-in-one example for using ConnectionManager.  
For more-detailed examples, read the wiki (if it is finished...).

#### Server-side:

```java
public class ExampleServer {
    CMSServer server;
    DataIOManager dataIOManager;

    public static void main(String[] args) {

        // CREATE SERVER
        server = new CMSServer(12345); // Create the server on port 12345
        server.addEventListener(new EventListener()); // Add the Server EventListener
        server.addGlobalListener(new EventListener()); // Add the global client EventListener (EventListener that will be added to every client from the server

        // CREATE DATAIOMANAGER
        dataIOManager = new DataIOManager(server, DataIOType.UTF, DataIOStreamType.MULTI_STREAM_HANDLER_CONSUMING); // Create a DataIOManager for the server
        dataIOManager.addEventListener(new EventListener()); // Add the DataIO EventListener
    }

    public void sendByteToClient(UUID clientId, int b) {
        CMSClient client = server.getClientById(clientId); // This will get a client with a specific UUID
        client.sendByte(b); // This will send one single byte
        System.out.println("Sent byte " + b + " to client " + client.getUniqueId());
    }

    public void sendStringToClient(UUID clientId, String string) {
        CMSClient client = server.getClientById(clientId); // This will get a client with a specific UUID
        DataIOStreamHandler handler = dataIOManager.getHandlerByClient(client); // This will get the DataIOStreamHandler of a client from the DataIOManager
        handler.writeUTF(string); // This will send a string (UTF-8)
        System.out.println("Sent string " + string + " to client " + client.getUniqueId());
    }
}

public class EventListener implements CMSServerEventListener, CMClientEventListener, DataIOEventListener {

    // FOR RECEIVING SERVER EVENTS (CMSServerEventListener)
    @Override
    public void onEvent(CMSServerEvent event) {
        if (event instanceof CMSServerConnectionAttemptEvent) {
            // The CMSServerConnectionAttemptEvent will be fired if a client connects to the server
            CMSServerConnectionAttemptEvent event1 = (CMSServerConnectionAttemptEvent) event;
            event1.getClient().setState(PendingClientState.ACCEPTED); // Accept the connection
        } else if (event instanceof CMSServerConnectionAcceptedEvent) {
            System.out.println("Connection " + ((CMSServerConnectionAcceptedEvent) event).getClient().getUniqueId() + " accepted");
        } else if (event instanceof CMSServerConnectionRefusedEvent) {
            System.out.println("Connection " + ((CMSServerConnectionRefusedEvent) event).getUuid() + " refused");
        }
    }

    // FOR RECEIVING CLIENT EVENTS (CMClientEventListener)
    @Override
    public void onEvent(CMClientEvent event) {
        if (event instanceof CMClientCreatedEvent) {
            System.out.println("Client " + ((CMSClient) event.getClient()).getUniqueId() + " created");
        } else if (event instanceof CMClientClosedEvent) {
            System.out.println("Client " + ((CMSClient) event.getClient()).getUniqueId() + " closed");
        } else if (event instanceof CMClientErrorEvent) {
            System.out.println("Error in client " + ((CMSClient) event.getClient()).getUniqueId() + " occurred: " + ((CMClientErrorEvent) event).getException());
        } else if (event instanceof CMClientInputStreamByteLimitReachedEvent) {
            System.out.println("Input stream limit reached in client " + ((CMSClient) ((CMClientInputStreamByteLimitReachedEvent))).getUniqueId());
        } else if (event instanceof CMClientByteReceivedEvent) {
            // Do something when the client receives a byte
        }
    }

    // FOR RECEIVING DATAIO EVENTS (DataIOEventListener)
    @Override
    public void onEvent(DataIOEvent event) {
        if (event instanceof DataIOUTFReceivedEvent) {
            System.out.println("String received from " + ((CMSClient) event.getClient().getEventClient()).getUniqueId() + ": " + ((DataIOUTFReceivedEvent) event).getData());
        }
    }
}
```

This will create a server with an ServerEventListener which accepts all connections.  
If a string will be received, it will be printed to console.  
If a client connects/disconnects or an error occurs, it will be also printed to console.

#### Client-side:

```java
public class ExampleClient {
    CMCClient client;
    DataIOStreamHandler handler;

    public static void main(String[] args) {

        // CLIENT
        client = new CMCClient("127.0.0.1", 12345, List.of(new EventListener())); // This will create a new client

        // DATAIO
        handler = new DataIOStreamHandler(client, DataIOType.UTF, DataIOStreamType.MULTI_STREAM_HANDLER_CONSUMING); // This will create a DataIOStreamHandler for the client
        handler.addEventListener(new EventListener());
    }
    
    public static void sendByte(int b) {
        client.sendByte(b); // Send a byte
        System.out.println("Byte " + b + " sent to server");
    }
    
    public static void sendString(String string) {
        handler.writeUTF(string); // Send a string
        System.out.println("String " + string + " was sent to server");
    }
}

public class EventListener implements CMClientEventListener, DataIOEventListener {
    // FOR RECEIVING CLIENT EVENTS (CMClientEventListener)
    @Override
    public void onEvent(CMClientEvent event) {
        if (event instanceof CMClientCreatedEvent) {
            System.out.println("Client connected");
        } else if (event instanceof CMClientClosedEvent) {
            System.out.println("Client closed");
        } else if (event instanceof CMClientErrorEvent) {
            System.out.println("Error in client occurred: " + ((CMClientErrorEvent) event).getException());
        } else if (event instanceof CMClientInputStreamByteLimitReachedEvent) {
            System.out.println("Input stream limit reached");
        } else if (event instanceof CMClientByteReceivedEvent) {
            // Do something when the client receives a byte
        }
    }

    // FOR RECEIVING DATAIO EVENTS (DataIOEventListener)
    @Override
    public void onEvent(DataIOEvent event) {
        if (event instanceof DataIOUTFReceivedEvent) {
            System.out.println("String received server: " + ((DataIOUTFReceivedEvent) event).getData());
        }
    }
}
```

This will create a client which connects to the server.  
If it receives a string from the server, it will be printed to console.  
If it connects/disconnects or an error occurs, it will be also printed to console.
