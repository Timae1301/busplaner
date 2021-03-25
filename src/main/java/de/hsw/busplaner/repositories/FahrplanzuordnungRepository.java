package de.hsw.busplaner.repositories;

import java.time.LocalTime;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import de.hsw.busplaner.beans.Fahrplan;
import de.hsw.busplaner.beans.Fahrplanzuordnung;
import de.hsw.busplaner.beans.Fahrtstrecke;

@Repository
public interface FahrplanzuordnungRepository extends CrudRepository<Fahrplanzuordnung, Long> {
    Iterable<Fahrplanzuordnung> findAllByFahrplan(Fahrplan fahrplan);

    Iterable<Fahrplanzuordnung> findAllByFahrtstrecke(Fahrtstrecke fahrtstrecke);

    Iterable<Fahrplanzuordnung> findAllByFahrplanAndStartzeitpunktBetween(Fahrplan fahrplan, LocalTime zeitpunktVorher,
            LocalTime zeitpunktNachher);
}
