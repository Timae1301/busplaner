package de.hsw.busplaner.repositories;

import de.hsw.busplaner.beans.Fahrtstrecke;
import de.hsw.busplaner.beans.Haltestellenzuordnung;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HaltestellenzuordnungRepository extends CrudRepository<Haltestellenzuordnung, Long> {
    Iterable<Haltestellenzuordnung> findAllByFahrtstreckeid(Fahrtstrecke fahrtstrecke);
}
