package de.hsw.busplaner.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import javax.management.InstanceNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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

    /**
     * Findet alle Haltestellen und fügt an das HaltestelleOutputDTO an, ob sie
     * löschbar sind
     * 
     * @return Liste mit HaltestelleOutputDTOs
     */
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

    /**
     * Erstellt und speichert eine Haltestelle anhand des Namens
     * 
     * @param haltestelleName
     * @return ID der neuen Haltestelle
     * @throws ResponseStatusException wenn der Haltestellenname schon vergeben ist
     */
    public Long postHaltestelle(String haltestelleName) throws ResponseStatusException {
        Haltestelle haltestelle = new Haltestelle(haltestelleName);
        try {
            findByName(haltestelle.getName());
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    String.format("Der HaltestelleName %s ist bereits vergeben", haltestelle.getName()));
        } catch (IllegalArgumentException e) {
            log.info(String.format("Neue Haltestelle: %s angelegt", haltestelleName));
            return save(haltestelle).getId();
        }
    }

    /**
     * Gibt eine Haltestelle zu einem Haltestellennamen zurück
     * 
     * @param haltestellenName
     * @return Haltestelle
     * @throws IllegalArgumentException wenn zu dem Haltestellennamen bereits ein
     *                                  Eintrag vorhanden ist
     */
    public Haltestelle findByName(String haltestellenName) throws IllegalArgumentException {
        Optional<Haltestelle> haltestelleOpt = repository.findByName(haltestellenName);
        if (haltestelleOpt.isPresent()) {
            return haltestelleOpt.get();
        }
        log.warning("Kein Eintrag für Haltestellennamen: " + haltestellenName);
        throw new IllegalArgumentException(
                String.format("Zu dem Haltestellennamen %s wurde kein Eintrag gefunden", haltestellenName));
    }

    /**
     * Bearbeitet die Haltestelle anhand des HaltestelleOutputDTOs
     * 
     * @param haltestelleOutputDTO
     * @throws IllegalArgumentException wenn zu dem Haltestellennamen bereits ein
     *                                  Eintrag vorhanden ist
     */
    public void patchHaltestelle(HaltestelleOutputDTO haltestelleOutputDTO) throws ResponseStatusException {
        Haltestelle haltestelle = new Haltestelle(haltestelleOutputDTO);
        try {
            findByName(haltestelle.getName());
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    String.format("Der HaltestelleName %s ist bereits vergeben", haltestelle.getName()));
        } catch (IllegalArgumentException e) {
            log.info(String.format("Neue Haltestelle: %s angelegt", haltestelle.getName()));
            save(haltestelle);
        }
    }

    /**
     * Löscht eine Haltestelle anhand der übergebenen HaltestelleID
     * 
     * @param haltestelleId
     * @return boolean, ob die Löschung erfolgreich war
     */
    public boolean deleteHaltestelle(Long haltestelleId) {
        if (isHaltestelleLoeschbar(haltestelleId)) {
            deleteById(haltestelleId);
            return true;
        }
        return false;
    }

    /**
     * Prüft, ob eine Haltestelle löschbar ist Eine Haltestelle ist löschbar, wenn
     * sie in keiner Fahrtstrecke enthalten ist, also keine Haltestellenzuordnungen
     * zu ihrer ID existieren
     * 
     * @param haltestelleId
     * @return boolean, ob die Haltestelle löschbar ist
     */
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

    /**
     * Ermittelt alle Fahrten zu übergebner FahrtstreckeID und überprüft, ob die
     * Haltestelle enthalten ist
     * 
     * @param haltestelleId
     * @param fahrtstreckeId
     * @return boolean, ob die Haltestelle in der Fahrtstrecke enthalten ist
     * @throws InstanceNotFoundException wenn beim Sortieren der Haltestellen die
     *                                   Informationen zu der Haltestelle nicht
     *                                   ermittelt werden konnten
     */
    public boolean isHaltestelleInFahrtstrecke(Long haltestelleId, Long fahrtstreckeId)
            throws InstanceNotFoundException {
        List<Haltestellenzuordnung> zuordnungen = haltestellenzuordnungService
                .getAlleZuordnungenZuFahrtstrecke(fahrtstreckeId);
        // Haltestellen müssen sortiert werden, um auch die letzte Haltestelle einer
        // Fahrt
        // überprüfen zu können
        for (HaltestellenzuordnungSortierDTO haltestellenzuordnung : sortierer
                .sortiereHaltestellen(new ArrayList<>(zuordnungen))) {
            if (haltestellenzuordnung.getHaltestelleId().equals(haltestelleId)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Findet einen Haltestelle anhand einer ID und gibt sie zurück
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

    /**
     * Ermittelt alle Fahrten zu einer übergebenen BuslinieID und fügt die
     * Haltestellen als HaltestelleOutputDTO in ein HashSet, um Dopplungen zu
     * vermeiden
     * 
     * @param buslinieId
     * @return Liste mit HaltestelleOutputDTOs
     * @throws InstanceNotFoundException wenn beim Sortieren der Haltestellen die
     *                                   Informationen zu der Haltestelle nicht
     *                                   ermittelt werden konnten
     * @throws IllegalArgumentException  wenn zu der ID keine Buslinie gefunden
     *                                   wurde oder wenn zu der ID keine Haltestelle
     *                                   gefunden wurde
     */
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
