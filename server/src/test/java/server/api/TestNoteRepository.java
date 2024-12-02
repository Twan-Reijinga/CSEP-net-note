package server.api;

import commons.Collection;
import commons.Note;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import server.database.NoteRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class TestNoteRepository implements NoteRepository {

    public final List<Note> notes = new ArrayList<>();
    public final List<String> called = new ArrayList<>();

    private void call(String name) {
        called.add(name);
    }

    public Optional<Note> find(long id) {
        return notes.stream().filter(x -> x.id == id).findFirst();
    }

    @Override
    public List<Note> findByCollectionId(long collectionId) {
        Collection collection = new Collection("first collection", "title");

        Map<Long, Note> fakeDB =
                Map.of(
                        1L, new Note("Title 1", "Content 1", collection),
                        2L, new Note("Title 2", "Content 2", collection));
        List<Note> notes = new ArrayList<>();
        for (Note note : fakeDB.values()) {
            notes.add(note);
        }
        return notes;
    }

    @Override
    public Optional<Note> findById(Long aLong) {
        call("findById");
        return find(aLong);
    }

    @Override
    public boolean existsById(Long aLong) {
        call("existsById");
        return find(aLong).isPresent();
    }

    @Override
    public List<Note> findAll() {
        call("findAll");
        return notes;
    }

    @Override
    public long count() {
        call("count");
        return notes.stream().count();
    }

    @Override
    public <S extends Note> S save(S entity) {
        call("save");
        entity.id = (long) notes.size();
        notes.add(entity);
        return entity;
    }


    @Override
    public void deleteById(Long aLong) {

    }

    @Override
    public void delete(Note entity) {

    }

    @Override
    public void flush() {

    }

    @Override
    public <S extends Note> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public <S extends Note> List<S> saveAllAndFlush(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public void deleteAllInBatch(Iterable<Note> entities) {

    }

    @Override
    public void deleteAllByIdInBatch(Iterable<Long> longs) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public Note getOne(Long aLong) {
        return null;
    }

    @Override
    public Note getById(Long aLong) {
        return null;
    }

    @Override
    public Note getReferenceById(Long aLong) {
        return null;
    }

    @Override
    public <S extends Note> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends Note> List<S> findAll(Example<S> example) {
        return List.of();
    }

    @Override
    public <S extends Note> List<S> findAll(Example<S> example, Sort sort) {
        return List.of();
    }

    @Override
    public <S extends Note> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends Note> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends Note> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends Note, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }

    @Override
    public <S extends Note> List<S> saveAll(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public List<Note> findAllById(Iterable<Long> longs) {
        return List.of();
    }

    @Override
    public void deleteAllById(Iterable<? extends Long> longs) {

    }

    @Override
    public void deleteAll(Iterable<? extends Note> entities) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public List<Note> findAll(Sort sort) {
        return List.of();
    }

    @Override
    public Page<Note> findAll(Pageable pageable) {
        return null;
    }
}
