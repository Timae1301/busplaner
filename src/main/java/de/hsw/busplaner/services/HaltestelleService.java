package de.hsw.busplaner.services;

import java.util.ArrayList;
import java.util.Optional;

import javax.management.InstanceNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.hsw.busplaner.beans.Haltestelle;
import de.hsw.busplaner.beans.Haltestellenzuordnung;
import de.hsw.busplaner.dtos.haltestelle.HaltestelleOutputDTO;
import de.hsw.busplaner.dtos.haltestellenzuordnung.HaltestellenzuordnungSortierDTO;
import de.hsw.busplaner.repositories.HaltestelleRepository;
import de.hsw.busplaner.util.HaltestellenSortierer;
import lombok.extern.java.Log;

@Log
@Service
public class HaltestelleService extends BasicService<Haltestelle, Long> {

    @Autowired
    private HaltestelleRepository repository;

    @Autowired
    private HaltestellenzuordnungService haltestellenzuordnungService;

    @Autowired
    private HaltestellenSortierer sortierer;

    @Override
    protected HaltestelleRepository getRepository() {
        return repository;
    }

    public ArrayList<HaltestelleOutputDTO> getAllHaltestellen() {
        ArrayList<HaltestelleOutputDTO> haltestellen = new ArrayList<>();
        for (Haltestelle haltestelle : findAll()) {
            log.info(String.format("Haltestelle: %s gefunden", haltestelle.getId()));
            HaltestelleOutputDTO haltestelleDto = new HaltestelleOutputDTO(haltestelle);
            if (isHaltestelleLoeschbar(haltestelle.getId())) {
                haltestelleDto.setLoeschbar(true);
            }
            haltestellen.add(haltestelleDto);
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

    public Boolean deleteHaltestelle(Long haltestelleId) throws IllegalArgumentException {
        if (isHaltestelleLoeschbar(haltestelleId)) {
            deleteById(haltestelleId);
            return true;
        }
        return false;
    }

    private Boolean isHaltestelleLoeschbar(Long haltestelleId) {
        Haltestelle haltestelle = getHaltestelleById(haltestelleId);
        if (haltestelle.getHaltestellenzuordnungen().isEmpty()) {
            for (Haltestellenzuordnung haltestellenzuordnung : haltestellenzuordnungService.findAll()) {
                if (haltestellenzuordnung.getNaechsteHaltestelle().equals(haltestelleId)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public Boolean isHaltestelleInFahrtstrecke(Long haltestelleId, Long fahrtstreckeId)
            throws InstanceNotFoundException {
        ArrayList<Haltestellenzuordnung> zuordnungen = haltestellenzuordnungService
                .getAlleZuordnungenZuFahrtstrecke(fahrtstreckeId);
        for (HaltestellenzuordnungSortierDTO haltestellenzuordnung : sortierer.sortiereHaltestellen(zuordnungen)) {
            if (haltestellenzuordnung.getHaltestelleId().equals(haltestelleId)) {
                return true;
            }
        }
        return false;
    }

    public Haltestelle getHaltestelleById(Long id) throws IllegalArgumentException {
        Optional<Haltestelle> haltestelleOpt = findById(id);
        if (haltestelleOpt.isEmpty()) {
            throw new IllegalArgumentException(String.format("Keine Haltestelle zu ID %s gefunden", id));
        }
        return haltestelleOpt.get();
    }
}
