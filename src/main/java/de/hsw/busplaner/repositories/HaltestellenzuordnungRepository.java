package de.hsw.busplaner.repositories;

import de.hsw.busplaner.beans.Fahrtstrecke;
import de.hsw.busplaner.beans.Haltestellenzuordnung;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Das ist das Haltestellezuordnung Repository welches von dem CRUD Repository
 * erbt
 */
@Repository
public interface HaltestellenzuordnungRepository extends CrudRepository<Haltestellenzuordnung, Long> {

    /**
     * Gibt die Haltestellenzuordnungen zurück mit der übergebenen Fahrtstrecke
     * 
     * @param fahrtstrecke
     * @return Iterable von Haltestellenzuordnungen
     */
    Iterable<Haltestellenzuordnung> findAllByFahrtstrecke(Fahrtstrecke fahrtstrecke);
}
