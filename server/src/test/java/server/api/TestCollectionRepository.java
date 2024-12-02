package server.api;

import commons.Collection;
import commons.Note;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import server.database.CollectionRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public class TestCollectionRepository implements CollectionRepository {

    public final List<Collection> collections = new ArrayList<>();
    public final List<String> called = new ArrayList<>();

    private void call(String name) {
        called.add(name);
    }

    public Optional<Collection> find(long id) {
        return collections.stream().filter(x -> x.id == id).findFirst();
    }

    @Override
    public Optional<Collection> findFirstByIsDefaultTrue() {
        call("findFirstByIsDefaultTrue");
        return collections.stream().filter(c -> c.isDefault).findFirst();    }

    @Override
    public void flush() {
        call("flush");
    }

    @Override
    public <S extends Collection> S saveAndFlush(S entity) {
        call("saveAndFlush");
        save(entity);
        return entity;
    }

    @Override
    public <S extends Collection> List<S> saveAllAndFlush(Iterable<S> entities) {
        call("saveAllAndFlush");
        List<S> saved = saveAll(entities);
        return new ArrayList<>(saved);
    }

    @Override
    public void deleteAllInBatch(Iterable<Collection> entities) {
        call("deleteAllInBatch");
        entities.forEach(this::delete);
    }

    @Override
    public void deleteAllByIdInBatch(Iterable<Long> longs) {
        call("deleteAllByIdInBatch");
        longs.forEach(this::deleteById);
    }

    @Override
    public void deleteAllInBatch() {
        call("deleteAllInBatch");
        collections.clear();
    }

    /**
     * @param aLong
     * @deprecated
     */
    @Override
    public Collection getOne(Long aLong) {
        return getReferenceById(aLong);
    }

    /**
     * @param aLong
     * @deprecated
     */
    @Override
    public Collection getById(Long aLong) {
        return getReferenceById(aLong);
    }

    @Override
    public Collection getReferenceById(Long aLong) {
        call("getReferenceById");
        return find(aLong).get();
    }

    @Override
    public <S extends Collection> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends Collection> List<S> findAll(Example<S> example) {
        return List.of();
    }

    @Override
    public <S extends Collection> List<S> findAll(Example<S> example, Sort sort) {
        return List.of();
    }

    @Override
    public <S extends Collection> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends Collection> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends Collection> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends Collection, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }

    @Override
    public <S extends Collection> S save(S entity) {
        return null;
    }

    @Override
    public <S extends Collection> List<S> saveAll(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public Optional<Collection> findById(Long aLong) {
        return Optional.empty();
    }

    @Override
    public boolean existsById(Long aLong) {
        return true;
    }

    @Override
    public List<Collection> findAll() {
        return List.of();
    }

    @Override
    public List<Collection> findAllById(Iterable<Long> longs) {
        return List.of();
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void deleteById(Long aLong) {

    }

    @Override
    public void delete(Collection entity) {

    }

    @Override
    public void deleteAllById(Iterable<? extends Long> longs) {

    }

    @Override
    public void deleteAll(Iterable<? extends Collection> entities) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public List<Collection> findAll(Sort sort) {
        return List.of();
    }

    @Override
    public Page<Collection> findAll(Pageable pageable) {
        return null;
    }
}
