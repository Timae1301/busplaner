package de.hsw.busplaner.repositories;

import de.hsw.busplaner.beans.Haltestelle;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Das ist das Haltestelle Repository welches von dem CRUD Repository erbt
 */
@Repository
public interface HaltestelleRepository extends CrudRepository<Haltestelle, Long> {

}
