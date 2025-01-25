package server.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import commons.Collection;
import commons.Note;
import commons.WebsocketUpdate;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import org.glassfish.tyrus.server.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import server.services.WebsocketService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Configuration
@ServerEndpoint("/ws")
public class WebsocketController {
    /*
     * Short explanation on why some stuff is static and why this class has two constructors.
     * I find that both the connection/communication code should stay in the controller class.
     * Given the fact that jakarta/tyrus and spring use different instances of the class, I
     * decided to use static fields for keeping data (mono state). I would rather have this than
     * have one more class just to adhere to the separation of concerns principle.
     * <br/>
     * There is also the concern of the code being too templated. A solution would be to have
     * the service hold a reference of the controller and call the broadcast function, but that
     * goes against the current (and IMO fine) way of using services, id est, controllers call
     * the services' functions when needed.
     */

    // Boot
    private static Server server;
    private static List<Session> clients;
    private static ObjectMapper objectMapper;

    @Autowired
    public WebsocketController(WebsocketService service) {
        objectMapper = new ObjectMapper();
        service.onNoteCreated.add(this::noteCreate);
        service.onNoteUpdated.add(this::noteUpdate);
        service.onNoteDeleted.add(this::noteDelete);
        service.onCollectionCreated.add(this::collectionCreate);
        service.onCollectionUpdated.add(this::collectionUpdate);
        service.onCollectionDeleted.add(this::collectionDelete);
        service.onDefaultCollectionIdChanged.add(this::defaultIdChanged);

        clients = new ArrayList<>();
        server = new Server("localhost", 8025, "/", null, WebsocketController.class);
        try {
            start();
        } catch (DeploymentException e) {
            throw new RuntimeException(e);
        }
    }

    public static void start() throws DeploymentException {
        server.start();
    }

    public static void stop() {
        server.stop();
    }

    // For jakarta DO NOT USE
    public WebsocketController() { }

    @OnOpen
    public void onOpen(Session session) throws IOException, EncodeException {
        clients.add(session);
        System.out.println(session.getId() + " connected to server");
    }

    @OnClose
    public void onClose(Session session, CloseReason reason) {
        System.out.println(session.getId() + " disconnected from server with following the reason "
                + reason.toString());
        clients.remove(session);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.out.println("There has been an error with session " + session.getId() + "\n" + throwable.toString());
        clients.remove(session);
    }

    @OnMessage
    public void onMessage(Session session, String message) throws IOException {
        // Ignore all messages from clients
    }

    private synchronized void broadcast(String message) {
        for (Session session : clients) {
            try {
                session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void noteCreate(Note note) {
        try {
            broadcast(objectMapper.writeValueAsString(
                    new WebsocketUpdate(WebsocketUpdate.Opcode.NOTE_CREATE, note, null, null)));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private void noteUpdate(Note note) {
        try {
            broadcast(objectMapper.writeValueAsString(
                    new WebsocketUpdate(WebsocketUpdate.Opcode.NOTE_UPDATE, note, null, null)));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private void noteDelete(Note note) {
        try {
            broadcast(objectMapper.writeValueAsString(
                    new WebsocketUpdate(WebsocketUpdate.Opcode.NOTE_DELETE, note, null, null)));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private void collectionCreate(Collection collection) {
        try {
            var str = objectMapper.writeValueAsString(
                    new WebsocketUpdate(WebsocketUpdate.Opcode.COLLECTION_CREATE, null, collection, null));
            broadcast(str);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private void collectionUpdate(Collection collection) {
        try {
            broadcast(objectMapper.writeValueAsString(
                    new WebsocketUpdate(WebsocketUpdate.Opcode.COLLECTION_UPDATE, null, collection, null)));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private void collectionDelete(Collection collection) {
        try {
            broadcast(objectMapper.writeValueAsString(
                    new WebsocketUpdate(WebsocketUpdate.Opcode.COLLECTION_DELETE, null, collection, null)));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private void defaultIdChanged(UUID id) {
        try {
            broadcast(objectMapper.writeValueAsString(
                    new WebsocketUpdate(WebsocketUpdate.Opcode.DEFAULT_COLLECTION_CHANGED, null, null, id)));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
