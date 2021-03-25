package de.hsw.busplaner.repositories;

import de.hsw.busplaner.beans.Buslinie;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Das ist das Buslinien Repository welches von dem CRUD Repository erbt
 */
@Repository
public interface BuslinieRepository extends CrudRepository<Buslinie, Long> {

    /**
     * Gibt ein Optional einer Buslinie zurück anhand einer übergebenen BusNr
     * 
     * @param busnr
     * @return Optional einer Buslinie
     */
    Optional<Buslinie> findByBusnr(Long busnr);
}
