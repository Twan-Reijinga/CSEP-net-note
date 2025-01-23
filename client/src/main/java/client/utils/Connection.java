package client.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import commons.*;
import jakarta.websocket.*;

import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.function.Consumer;

@ClientEndpoint
public class Connection {
    private Session websocketSession;
    private final ObjectMapper objectMapper;
    private final Object lock = new Object();
    private final List<Consumer<WebsocketUpdate>> subscribers;

    public Connection() {
        objectMapper = new ObjectMapper();
        subscribers = new ArrayList<>();
    }

    public void connect(String host) {
        try {
            ContainerProvider.getWebSocketContainer().connectToServer(this, URI.create("ws://" + host + ":8025/ws"));
        } catch (DeploymentException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isOpen() {
        if(websocketSession == null) return false;
        return websocketSession.isOpen();
    }

    public void close() {
        try {
            websocketSession.close(
                    new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE,"Client is closing gracefully")
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("OPENED " + session.getId());
        websocketSession = session;
    }

    @OnClose
    public void onClose(Session session, CloseReason reason) {
        System.out.println("Disconnected with the following reason " + reason.toString());
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.out.println("There has been an error with session " + session.getId() + '\n' + throwable.toString());
    }

    @OnMessage
    public void processMessage(String message) {
        synchronized (lock) {
            WebsocketUpdate update;
            try {
                update = objectMapper.readValue(message, WebsocketUpdate.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

            if (update == null) {
                System.out.println("Failed to parse message!");
                return;
            }

            subscribers.forEach(s -> {
                s.accept(update);
            });
        }
    }

    public void subscribe(Consumer<WebsocketUpdate> action) { subscribers.add(action); }
    public void unsubscribe(Consumer<WebsocketUpdate> action) { subscribers.remove(action); }
}
