package de.hsw.busplaner.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import javax.management.InstanceNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.hsw.busplaner.beans.Haltestelle;
import de.hsw.busplaner.beans.Haltestellenzuordnung;
import de.hsw.busplaner.dtos.fahrtstrecke.FahrtstreckeMitHaltestellenDTO;
import de.hsw.busplaner.dtos.haltestelle.HaltestelleOutputDTO;
import de.hsw.busplaner.dtos.haltestellenzuordnung.HaltestellenzuordnungSortierDTO;
import de.hsw.busplaner.repositories.HaltestelleRepository;
import de.hsw.busplaner.util.HaltestellenSortierer;
import lombok.extern.java.Log;

/**
 * Der Service der Haltestelle erbt von dem abstrakten BasicService für die CRUD
 * Operationen
 */
@Log
@Service
public class HaltestelleService extends BasicService<Haltestelle, Long> {

    private final HaltestelleRepository repository;

    @Autowired
    private HaltestellenSortierer sortierer;

    @Autowired
    private HaltestellenzuordnungService haltestellenzuordnungService;

    @Autowired
    private FahrtstreckeService fahrtstreckeService;

    @Autowired
    public HaltestelleService(final HaltestelleRepository repository) {
        this.repository = repository;
    }

    @Override
    protected HaltestelleRepository getRepository() {
        return repository;
    }

    public List<HaltestelleOutputDTO> getAllHaltestellen() {
        List<HaltestelleOutputDTO> haltestellen = new ArrayList<>();
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

    public void patchHaltestelle(HaltestelleOutputDTO haltestelleOutputDTO) {
        Haltestelle haltestelle = new Haltestelle(haltestelleOutputDTO);
        save(haltestelle);
    }

    public boolean deleteHaltestelle(Long haltestelleId) throws IllegalArgumentException {
        if (isHaltestelleLoeschbar(haltestelleId)) {
            deleteById(haltestelleId);
            return true;
        }
        return false;
    }

    private boolean isHaltestelleLoeschbar(Long haltestelleId) {
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

    public boolean isHaltestelleInFahrtstrecke(Long haltestelleId, Long fahrtstreckeId)
            throws InstanceNotFoundException {
        List<Haltestellenzuordnung> zuordnungen = haltestellenzuordnungService
                .getAlleZuordnungenZuFahrtstrecke(fahrtstreckeId);
        for (HaltestellenzuordnungSortierDTO haltestellenzuordnung : sortierer
                .sortiereHaltestellen(new ArrayList<>(zuordnungen))) {
            if (haltestellenzuordnung.getHaltestelleId().equals(haltestelleId)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Findet einen Haltestelle anhand einer ID und gibt ihn zurück
     * 
     * @param id
     * @return Haltestelle
     * @throws IllegalArgumentException wenn zu der ID keine Haltestelle gefunden
     *                                  wurde
     */
    public Haltestelle getHaltestelleById(Long id) throws IllegalArgumentException {
        Optional<Haltestelle> haltestelleOpt = findById(id);
        if (haltestelleOpt.isEmpty()) {
            log.warning(String.format("Keine Haltestelle zu ID %s gefunden", id));
            throw new IllegalArgumentException(String.format("Keine Haltestelle zu ID %s gefunden", id));
        }
        return haltestelleOpt.get();
    }

    /**
     * Findet aus der ID ein HaltestelleOutputDTO und gibt es zurück
     * 
     * @param haltestelleId
     * @return HaltestelleOutputDTO
     * @throws IllegalArgumentException wenn zu der ID keine Haltestelle gefunden
     *                                  wurde
     */
    public HaltestelleOutputDTO getHaltestelleDtoById(Long haltestelleId) throws IllegalArgumentException {
        return new HaltestelleOutputDTO(getHaltestelleById(haltestelleId));
    }

    public List<HaltestelleOutputDTO> getAlleHaltestellenZuBuslinieId(Long buslinieId)
            throws InstanceNotFoundException, IllegalArgumentException {
        HashSet<HaltestelleOutputDTO> haltestellenSet = new HashSet<>();
        for (FahrtstreckeMitHaltestellenDTO fahrtstrecke : fahrtstreckeService
                .getAlleFahrtstreckenZuBuslinieId(buslinieId)) {
            for (HaltestellenzuordnungSortierDTO haltestelle : fahrtstrecke.getHaltestellen()) {
                haltestellenSet.add(new HaltestelleOutputDTO(getHaltestelleById(haltestelle.getHaltestelleId())));
            }
        }
        return new ArrayList<>(haltestellenSet);
    }
}
