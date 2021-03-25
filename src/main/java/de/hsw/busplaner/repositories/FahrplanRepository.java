package de.hsw.busplaner.repositories;

import de.hsw.busplaner.beans.Fahrplan;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Das ist das Fahrplan Repository welches von dem CRUD Repository erbt
 */
@Repository
public interface FahrplanRepository extends CrudRepository<Fahrplan, Long> {
}
