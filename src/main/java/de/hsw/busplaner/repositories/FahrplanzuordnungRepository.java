package de.hsw.busplaner.repositories;

import java.time.LocalTime;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import de.hsw.busplaner.beans.Fahrplan;
import de.hsw.busplaner.beans.Fahrplanzuordnung;
import de.hsw.busplaner.beans.Fahrtstrecke;

/**
 * Das ist das Fahrplanzuordnung Repository welches von dem CRUD Repository erbt
 */
@Repository
public interface FahrplanzuordnungRepository extends CrudRepository<Fahrplanzuordnung, Long> {

    /**
     * Gibt Fahrplanzuordnungen zurück anhand eines übergebenen Fahrplans
     * 
     * @param fahrplan
     * @return Iterable mit Fahrplanzuordnungen
     */
    Iterable<Fahrplanzuordnung> findAllByFahrplan(Fahrplan fahrplan);

    /**
     * Gibt Fahrplanzuordnungen zurück anhand einer übergebenen Fahrtstrecke
     * 
     * @param fahrtstrecke
     * @return Iterable mit Fahrplanzuordnungen
     */
    Iterable<Fahrplanzuordnung> findAllByFahrtstrecke(Fahrtstrecke fahrtstrecke);

    /**
     * Gibt Fahrplanzuordnungen zurück anhand eines übergebenen Fahrplans und
     * innerhalb eines übergebenen Zeitintervalls
     * 
     * @param fahrplan
     * @param zeitpunktVorher
     * @param zeitpunktNachher
     * @return Iterable mit Fahrplanzuordnungen
     */
    Iterable<Fahrplanzuordnung> findAllByFahrplanAndStartzeitpunktBetween(Fahrplan fahrplan, LocalTime zeitpunktVorher,
            LocalTime zeitpunktNachher);
}
