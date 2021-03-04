package de.hsw.busplaner.services;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.hsw.busplaner.beans.Fahrplan;
import de.hsw.busplaner.dtos.fahrplan.FahrplanOutputDTO;
import de.hsw.busplaner.repositories.FahrplanRepository;
import lombok.extern.java.Log;

@Log
@Service
public class FahrplanService extends BasicService<Fahrplan, Long> {

    @Autowired
    FahrplanRepository repository;

    @Override
    protected FahrplanRepository getRepository() {
        return repository;
    }

    public Long postFahrplan(String name) {
        Fahrplan fahrplan = new Fahrplan(name);
        log.info(String.format("Neuen Fahrplan: %s angelegt", fahrplan));
        return save(fahrplan).getId();
    }

    public ArrayList<FahrplanOutputDTO> getAlleFahrplaene() {
        ArrayList<FahrplanOutputDTO> fahrplaene = new ArrayList<>();
        for (Fahrplan fahrplan : findAll()) {
            log.info(String.format("Fahrplan: %s gefunden", fahrplan.getName()));
            fahrplaene.add(new FahrplanOutputDTO(fahrplan));
        }
        return fahrplaene;
    }

}