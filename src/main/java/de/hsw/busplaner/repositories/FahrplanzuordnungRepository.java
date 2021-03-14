package de.hsw.busplaner.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import de.hsw.busplaner.beans.Fahrplan;
import de.hsw.busplaner.beans.Fahrplanzuordnung;
import de.hsw.busplaner.beans.Fahrtstrecke;

@Repository
public interface FahrplanzuordnungRepository extends CrudRepository<Fahrplanzuordnung, Long> {
    Iterable<Fahrplanzuordnung> findAllByFahrplanid(Fahrplan fahrplanId);

    Iterable<Fahrplanzuordnung> findAllByFahrtstreckeid(Fahrtstrecke fahrtstreckeId);
}
