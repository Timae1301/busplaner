package de.hsw.busplaner.repositories;

import de.hsw.busplaner.beans.Fahrplan;
import de.hsw.busplaner.beans.Fahrplanzuordnung;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FahrplanzuordnungRepository extends CrudRepository<Fahrplanzuordnung, Long> {
    Iterable<Fahrplanzuordnung> findAllByFahrplanid(Fahrplan fahrplan);
}
