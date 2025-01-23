package server.services;

import commons.Collection;
import commons.Note;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Consumer;

@Service
public class WebsocketService {

    public final List<Consumer<Note>> onNoteCreated = new ArrayList<>();
    public final List<Consumer<Note>> onNoteUpdated = new ArrayList<>();
    public final List<Consumer<Note>> onNoteDeleted = new ArrayList<>();
    public final List<Consumer<Collection>> onCollectionCreated = new ArrayList<>();
    public final List<Consumer<Collection>> onCollectionUpdated = new ArrayList<>();
    public final List<Consumer<Collection>> onCollectionDeleted = new ArrayList<>();

    @Autowired
    public WebsocketService() { }

    public void notifyNoteSubscribers(List<Consumer<Note>> subscribers, Note note) {
        for (var subscriber : subscribers)
            subscriber.accept(note);
    }

    public void notifyCollectionSubscribers(List<Consumer<Collection>> subscribers, Collection collection) {
        for (var subscriber : subscribers)
            subscriber.accept(collection);
    }
}
