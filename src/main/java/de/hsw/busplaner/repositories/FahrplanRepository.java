package de.hsw.busplaner.repositories;

import de.hsw.busplaner.beans.Fahrplan;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FahrplanRepository extends CrudRepository<Fahrplan, Long>{
    
}
