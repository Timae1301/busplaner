package de.hsw.busplaner.repositories;

import de.hsw.busplaner.beans.Haltestelle;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Das ist das Haltestelle Repository welches von dem CRUD Repository erbt
 */
@Repository
public interface HaltestelleRepository extends CrudRepository<Haltestelle, Long> {

    /**
     * Gibt ein Optional einer Haltestelle zurück anhand eines übergebenen
     * Haltestllennamens
     * 
     * @param haltestelleName
     * @return Optional einer Haltestelle
     */
    Optional<Haltestelle> findByName(String haltestelleName);
}
