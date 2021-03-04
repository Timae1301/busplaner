package de.hsw.busplaner.services;

import de.hsw.busplaner.beans.Buslinie;
import de.hsw.busplaner.beans.Fahrtstrecke;
import de.hsw.busplaner.dtos.buslinie.BuslinieOutputDTO;
import de.hsw.busplaner.repositories.BuslinieRepository;
import lombok.extern.java.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Log
public class BuslinieService extends BasicService<Buslinie, Long> {

    @Autowired
    private BuslinieRepository repository;

    @Autowired
    private FahrtstreckeService fahrtstreckeService;

    @Override
    protected BuslinieRepository getRepository() {
        return repository;
    }

    public Optional<Buslinie> findByBusnr(Long busnr) {
        return repository.findByBusnr(busnr);
    }

    public ArrayList<BuslinieOutputDTO> getAllBuslinien() {
        ArrayList<BuslinieOutputDTO> buslinien = new ArrayList<>();
        for (Buslinie buslinie : findAll()) {
            log.info(String.format("Buslinie: %s gefunden", buslinie.getBusnr()));
            buslinien.add(new BuslinieOutputDTO(buslinie));
        }
        return buslinien;
    }

    public Long postBuslinie(Long busNr) {
        log.info(String.format("Neue Buslinie: %s angelegt", busNr));
        return save(new Buslinie(busNr)).getId();
    }

    public BuslinieOutputDTO getBuslinie(Long buslinieId) {
        Optional<Buslinie> buslinieOpt = findById(buslinieId);
        if (buslinieOpt.isPresent()) {
            return new BuslinieOutputDTO(buslinieOpt.get());
        }
        throw new IllegalArgumentException(
                String.format("Zu der BuslinienId %s wurde kein Eintrag gefunden", buslinieId));
    }

    public void patchBuslinie(BuslinieOutputDTO buslinieDTO) {
        Buslinie buslinie = new Buslinie(buslinieDTO);
        save(buslinie);
    }

    public Boolean deleteBuslinie(Long buslinieId) {
        Optional<Buslinie> buslinieOpt = findById(buslinieId);
        if (buslinieOpt.isEmpty()) {
            throw new IllegalArgumentException(String.format("Die ID %s konnte nicht gefunden werden", buslinieId));
        }
        List<Fahrtstrecke> fahrtstreckenMitBuslinie = new ArrayList<>();
        fahrtstreckeService.findAllByBuslinieId(buslinieOpt.get()).forEach(fahrtstreckenMitBuslinie::add);
        if (fahrtstreckenMitBuslinie.isEmpty()) {
            deleteById(buslinieId);
            return true;
        } else {
            return false;
        }
    }

}
