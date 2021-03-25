package de.hsw.busplaner.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import de.hsw.busplaner.beans.Buslinie;
import de.hsw.busplaner.beans.Fahrtstrecke;

/**
 * Das ist das Fahrtstrecke Repository welches von dem CRUD Repository erbt
 */
@Repository
public interface FahrtstreckeRepository extends CrudRepository<Fahrtstrecke, Long> {

    /**
     * Gibt Fahrtstrecken zurück, die zu der übergebenen Buslinie gehören
     * 
     * @param buslinie
     * @return Iterable mit Fahrtstrecken
     */
    Iterable<Fahrtstrecke> findAllByBuslinie(Buslinie buslinie);
}
