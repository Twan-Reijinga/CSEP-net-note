package server.api;

import commons.Collection;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import server.database.CollectionRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

public class TestCollectionRepository implements CollectionRepository {

    public final List<Collection> collections = new ArrayList<>();
    public final List<String> called = new ArrayList<>();

    private void call(String name) {
        called.add(name);
    }

    public Optional<Collection> find(UUID id) {
        return collections.stream().filter(x -> x.id.equals(id)).findFirst();
    }

    @Override
    public Boolean existsByName(String name) {
        return false;
    }

    @Override
    public Optional<Collection> getCollectionByName(String name) {
        return Optional.empty();
    }

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

    public Optional<Collection> findById(UUID id) {
        call("findById");
        return find(id);
    }

    @Override
    public boolean existsById(UUID id) {
        call("existsById");
        return find(id).isPresent();
    }

    @Override
    public List<Collection> findAll() {
        call("findAll");
        return collections;
    }

    @Override
    public long count() {
        call("count");
        return collections.stream().count();
    }

    @Override
    public <S extends Collection> S save(S entity) {
        call("save");
        entity.id = UUID.randomUUID();
        collections.add(entity);
        return entity;
    }

    @Override
    public <S extends Collection> List<S> saveAllAndFlush(Iterable<S> entities) {
        call("saveAllAndFlush");
        List<S> saved = saveAll(entities);
        return new ArrayList<>(saved);
    }

    @Override
    public void deleteById(UUID id) {
        call("deleteById");
        int i = 0;
        for(Collection currentCollection : collections) {
            if(currentCollection.id.equals(id)) {
                collections.remove(currentCollection);
            }
            if (collections.size() <= i)
                break;
            i++;
        }
    }

    @Override
    public void deleteAllInBatch(Iterable<Collection> entities) {
        call("deleteAllInBatch");
        entities.forEach(this::delete);
    }

    @Override
    public void deleteAllByIdInBatch(Iterable<UUID> ids) {
        call("deleteAllByIdInBatch");
        ids.forEach(this::deleteById);
    }

    @Override
    public void deleteAllInBatch() {
        call("deleteAllInBatch");
        collections.clear();
    }

    /**
     * @param id
     * @deprecated
     */
    @Override
    public Collection getOne(UUID id) {
        return getReferenceById(id);
    }

    /**
     * @param id
     * @deprecated
     */
    @Override
    public Collection getById(UUID id) {
        return getReferenceById(id);

    }

    @Override
    public Collection getReferenceById(UUID id) {
        call("getReferenceById");
        return find(id).get();
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
    public <S extends Collection> List<S> saveAll(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public List<Collection> findAllById(Iterable<UUID> ids) {
        return List.of();
    }

    @Override
    public void delete(Collection entity) {

    }

    @Override
    public void deleteAllById(Iterable<? extends UUID> ids) {

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
