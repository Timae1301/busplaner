package de.hsw.busplaner.services;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.hsw.busplaner.beans.Haltestelle;
import de.hsw.busplaner.dtos.HaltestelleDTO;
import de.hsw.busplaner.repositories.HaltestelleRepository;
import lombok.extern.java.Log;

@Log
@Service
public class HaltestelleService extends BasicService<Haltestelle, Long> {

    @Autowired
    private HaltestelleRepository repository;

    @Override
    protected HaltestelleRepository getRepository() {
        return repository;
    }

    public ArrayList<HaltestelleDTO> getAllHaltestellen() {
        ArrayList<HaltestelleDTO> haltestellen = new ArrayList<>();
        for (Haltestelle haltestelle : findAll()) {
            log.info(String.format("Haltestelle: %s gefunden", haltestelle.getId()));
            haltestellen.add(new HaltestelleDTO(haltestelle));
        }
        return haltestellen;
    }

    public boolean postHaltestelle(String haltestelleName) {
        save(new Haltestelle(haltestelleName));
        log.info(String.format("Neue Haltestelle: %s angelegt", haltestelleName));
        return true;
    }
}
