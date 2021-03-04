package de.hsw.busplaner.services;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.hsw.busplaner.beans.Haltestelle;
import de.hsw.busplaner.dtos.haltestelle.HaltestelleOutputDTO;
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

    public ArrayList<HaltestelleOutputDTO> getAllHaltestellen() {
        ArrayList<HaltestelleOutputDTO> haltestellen = new ArrayList<>();
        for (Haltestelle haltestelle : findAll()) {
            log.info(String.format("Haltestelle: %s gefunden", haltestelle.getId()));
            haltestellen.add(new HaltestelleOutputDTO(haltestelle));
        }
        return haltestellen;
    }

    public Long postHaltestelle(String haltestelleName) {
        Haltestelle haltestelle = new Haltestelle(haltestelleName);
        log.info(String.format("Neue Haltestelle: %s angelegt", haltestelleName));
        return save(haltestelle).getId();
    }

    public HaltestelleOutputDTO getHaltestelle(Long haltestelleId) {
        Optional<Haltestelle> haltestelleOpt = findById(haltestelleId);
        if (haltestelleOpt.isPresent()) {
            return new HaltestelleOutputDTO(haltestelleOpt.get());
        }
        throw new IllegalArgumentException(String.format("Keine Haltestelle zu ID: %s gefunden", haltestelleId));
    }

    public void patchHaltestelle(HaltestelleOutputDTO haltestelleOutputDTO) {
        Haltestelle haltestelle = new Haltestelle(haltestelleOutputDTO);
        save(haltestelle);
    }

    public Boolean deleteHaltestelle(Long haltestelleId) {
        Optional<Haltestelle> haltestelleOpt = findById(haltestelleId);
        if (haltestelleOpt.isEmpty()) {
            throw new IllegalArgumentException(
                    String.format("Zu der ID: %s wurde keine Haltestelle gefunden", haltestelleId));
        }
        Haltestelle haltestelle = haltestelleOpt.get();
        if (haltestelle.getHaltestellenzuordnungen().isEmpty()) {
            delete(haltestelle);
            return true;
        }
        return false;
    }
}
