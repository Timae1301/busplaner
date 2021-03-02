package de.hsw.busplaner.repositories;

import de.hsw.busplaner.beans.Buslinie;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BuslinieRepository extends CrudRepository<Buslinie, Long> {
}
