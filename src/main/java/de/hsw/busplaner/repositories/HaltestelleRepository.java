package de.hsw.busplaner.repositories;

import de.hsw.busplaner.beans.Haltestelle;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HaltestelleRepository extends CrudRepository<Haltestelle, Long>{
    
}
