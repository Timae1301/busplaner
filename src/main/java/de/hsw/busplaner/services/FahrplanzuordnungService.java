package de.hsw.busplaner.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.hsw.busplaner.beans.Fahrplanzuordnung;
import de.hsw.busplaner.repositories.FahrplanzuordnungRepository;
import lombok.extern.java.Log;

@Log
@Service
public class FahrplanzuordnungService extends BasicService<Fahrplanzuordnung, Long> {

    @Autowired
    FahrplanzuordnungRepository repository;

    @Override
    protected FahrplanzuordnungRepository getRepository() {
        return repository;
    }
}
