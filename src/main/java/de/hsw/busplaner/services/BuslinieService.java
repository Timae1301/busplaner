package de.hsw.busplaner.services;

import de.hsw.busplaner.beans.Buslinie;
import de.hsw.busplaner.dtos.BuslinieDTO;
import de.hsw.busplaner.repositories.BuslinieRepository;
import lombok.extern.java.Log;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Log
public class BuslinieService extends BasicService<Buslinie, Long> {

    @Autowired
    private BuslinieRepository repository;

    @Override
    protected BuslinieRepository getRepository() {
        return repository;
    }

    public ArrayList<BuslinieDTO> getAllBuslinien() {
        ArrayList<BuslinieDTO> buslinien = new ArrayList<>();
        for (Buslinie buslinie : findAll()) {
            log.info(String.format("Buslinie: %s gefunden", buslinie.getBusnr()));
            buslinien.add(new BuslinieDTO(buslinie));
        }
        return buslinien;
    }

    public boolean postBuslinie(Long busNr) {
        save(new Buslinie(busNr));
        log.info(String.format("Neue Buslinie: %s angelegt", busNr));
        return true;
    }

}
