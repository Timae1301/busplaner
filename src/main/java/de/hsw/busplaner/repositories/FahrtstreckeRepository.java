package de.hsw.busplaner.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import de.hsw.busplaner.beans.Buslinie;
import de.hsw.busplaner.beans.Fahrtstrecke;

@Repository
public interface FahrtstreckeRepository extends CrudRepository<Fahrtstrecke, Long> {
    Iterable<Fahrtstrecke> findAllByBuslinieId(Buslinie buslinie);
}
