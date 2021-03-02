package de.hsw.busplaner.services;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import lombok.extern.java.Log;

@Service
@Log
public abstract class BasicService<T, ID> {

    public <S extends T> S save(S entity) {
        log.info(String.format("Neue Entitaet: %s erstellt", entity.getClass().getSimpleName()));
        return getRepository().save(entity);
    }

    public <S extends T> Iterable<S> saveAll(Iterable<S> entities) {
        return getRepository().saveAll(entities);
    }

    public Optional<T> findById(ID identifier) {
        return getRepository().findById(identifier);
    }

    public boolean existsById(ID identifier) {
        return getRepository().existsById(identifier);
    }

    public Iterable<T> findAll() {
        return getRepository().findAll();
    }

    public Iterable<T> findAllById(Iterable<ID> identifiers) {
        return getRepository().findAllById(identifiers);
    }

    public long count() {
        return getRepository().count();
    }

    public void deleteById(ID identifier) {
        getRepository().deleteById(identifier);
    }

    public void delete(T entity) {
        getRepository().delete(entity);
    }

    public void deleteAll(Iterable<? extends T> entities) {
        getRepository().deleteAll(entities);
    }

    public void deleteAll() {
        getRepository().deleteAll();
    }

    protected abstract CrudRepository<T, ID> getRepository();
}
