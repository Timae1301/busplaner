package de.hsw.busplaner.services;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.management.InstanceNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import de.hsw.busplaner.beans.Fahrplan;
import de.hsw.busplaner.beans.Fahrplanzuordnung;
import de.hsw.busplaner.dtos.fahrplan.FahrplanOutputDTO;
import de.hsw.busplaner.dtos.fahrplan.FahrplanauskunftDTO;
import de.hsw.busplaner.dtos.fahrtstrecke.FahrtstreckeMitHaltestellenDTO;
import de.hsw.busplaner.dtos.haltestellenzuordnung.HaltestellenzuordnungSortierDTO;
import de.hsw.busplaner.repositories.FahrplanRepository;
import de.hsw.busplaner.util.FahrtzeitenErmitteln;
import de.hsw.busplaner.util.HaltestellenSortierer;
import lombok.extern.java.Log;

/**
 * Der Service des Fahrplans erbt von dem abstrakten BasicService für die CRUD
 * Operationen
 */
@Log
@Service
public class FahrplanService extends BasicService<Fahrplan, Long> {

    private final FahrplanRepository repository;

    @Autowired
    private HaltestellenSortierer sortierer;

    @Autowired
    private FahrplanzuordnungService fahrplanzuordnungService;

    @Autowired
    public FahrplanService(final FahrplanRepository repository) {
        this.repository = repository;
    }

    @Override
    protected FahrplanRepository getRepository() {
        return repository;
    }

    /**
     * Speichert neuen Fahrplan anhand übergebenen Namens
     * 
     * @param name
     * @return ID des neuen Fahrplans
     * @throws ResponseStatusException wenn der Fahrplanname bereits vorhanden ist
     */
    public Long postFahrplan(String name) throws ResponseStatusException {
        Fahrplan fahrplan = new Fahrplan(name);
        try {
            findByName(fahrplan.getName());
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    String.format("Der FahrplanName %s ist bereits vergeben", fahrplan.getName()));
        } catch (IllegalArgumentException e) {
            log.info(String.format("Neuen Fahrplan: %s angelegt", fahrplan));
            return save(fahrplan).getId();
        }
    }

    /**
     * Gibt eine Fahrplan zu einem Fahrplannamen zurück
     * 
     * @param haltestellenName
     * @return Fahrplan
     * @throws IllegalArgumentException wenn zu dem Fahrplannamen bereits ein
     *                                  Eintrag vorhanden ist
     */
    public Fahrplan findByName(String fahrplanName) throws IllegalArgumentException {
        Optional<Fahrplan> fahrplanOpt = repository.findByName(fahrplanName);
        if (fahrplanOpt.isPresent()) {
            return fahrplanOpt.get();
        }
        log.warning("Kein Eintrag für Fahrplannamen: " + fahrplanName);
        throw new IllegalArgumentException(
                String.format("Zu dem Fahrplannamen %s wurde kein Eintrag gefunden", fahrplanName));
    }

    /**
     * Gibt alle Fahrpläne als FahrplanOutputDTOs zurücl
     * 
     * @return Liste von FahrplanOutputDTOs
     */
    public List<FahrplanOutputDTO> getAlleFahrplaene() {
        List<FahrplanOutputDTO> fahrplaene = new ArrayList<>();
        for (Fahrplan fahrplan : findAll()) {
            log.info(String.format("Fahrplan: %s gefunden", fahrplan.getName()));
            fahrplaene.add(new FahrplanOutputDTO(fahrplan));
        }
        return fahrplaene;
    }

    /**
     * Findet einen Fahrplan anhand einer ID und gibt ihn zurück
     * 
     * @param id
     * @return Fahrplan
     * @throws IllegalArgumentException wenn zu der ID kein Fahrplan gefunden wurde
     */
    public Fahrplan getFahrplanZuId(Long id) throws IllegalArgumentException {
        Optional<Fahrplan> fahrplanOpt = findById(id);
        if (fahrplanOpt.isEmpty()) {
            log.warning(String.format("Keine Fahrplan zu ID %s gefunden", id));
            throw new IllegalArgumentException(String.format("Keine Fahrplan zu ID %s gefunden", id));
        }
        return fahrplanOpt.get();
    }

    /**
     * Löscht einen Fahrplan zu der übergebenen ID und löscht auch alle bestehenden
     * Fahrplanzuordnungen
     * 
     * @param fahrplanId
     * @return boolean, ob das Löschen erfolgreich war
     * @throws IllegalArgumentException wenn zu der ID kein Fahrplan gefunden wurde
     */
    public boolean deleteFahrplan(Long fahrplanId) throws IllegalArgumentException {
        Fahrplan fahrplan = getFahrplanZuId(fahrplanId);
        List<Fahrplanzuordnung> fahrplanzuordnungen = fahrplanzuordnungService
                .getAlleFahrplanzuordnungenZuFahrplan(fahrplan);
        for (Fahrplanzuordnung fahrplanzuordnung : fahrplanzuordnungen) {
            fahrplanzuordnungService.deleteById(fahrplanzuordnung.getId());
        }
        deleteById(fahrplan.getId());
        return true;
    }

    /**
     * Liest zu einem Fahrplan alle Fahrten in einem ausgewählten Zeitraum aus und
     * ermittelt deren Haltestellen und gibt die sortierten Fahrtstrecken in ein
     * FahrplanauskunftDTO
     * 
     * @param fahrplanId
     * @param zeitpunktVorher
     * @param zeitpunktNachher
     * @return FahrplanauskunftDTO mit sortierten Fahrtstrecken inklusive Uhrzeit
     * @throws InstanceNotFoundException wenn beim Sortieren der Haltestellen die
     *                                   Informationen zu der Haltestelle nicht
     *                                   ermittelt werden konnten
     * @throws IllegalArgumentException  wenn zu der ID kein Fahrplan gefunden wurde
     */
    public FahrplanauskunftDTO getFahrplanauskunft(Long fahrplanId, LocalTime zeitpunktVorher,
            LocalTime zeitpunktNachher) throws InstanceNotFoundException, IllegalArgumentException {
        Fahrplan fahrplan = getFahrplanZuId(fahrplanId);
        List<FahrtstreckeMitHaltestellenDTO> fahrtstrecken = new ArrayList<>();
        List<Fahrplanzuordnung> fahrplanzuordnungen = fahrplanzuordnungService
                .getAlleFahrplanzuordnungenZuFahrplanInTime(fahrplan, zeitpunktVorher, zeitpunktNachher);
        for (Fahrplanzuordnung fahrplanzuordnung : fahrplanzuordnungen) {
            fahrtstrecken.add(getFahrtstreckeMitHaltestellen(fahrplanzuordnung));
        }
        // Die Liste wird anhand des Startzeitpunkts sortiert
        Collections.sort(fahrtstrecken);
        return new FahrplanauskunftDTO(fahrplan.getName(), fahrtstrecken);
    }

    /**
     * Bringt die Fahrten in die richtige Reihenfolge und ermittelt die Uhrzeiten
     * 
     * @param fahrplanzuordnung
     * @return FahrtstreckeMitHaltestellenDTO
     * @throws InstanceNotFoundException wenn beim Sortieren der Haltestellen die
     *                                   Informationen zu der Haltestelle nicht
     *                                   ermittelt werden konnten
     */
    private FahrtstreckeMitHaltestellenDTO getFahrtstreckeMitHaltestellen(Fahrplanzuordnung fahrplanzuordnung)
            throws InstanceNotFoundException {
        List<HaltestellenzuordnungSortierDTO> sortiertehaltestellen = sortierer.sortiereHaltestellen(
                new ArrayList<>(fahrplanzuordnung.getFahrtstrecke().getHaltestellenzuordnungen()));

        // Die Fahrtzeit für die einzelnen Haltestellen muss je nach Fahrtrichtung
        // ermittelt werden
        if (fahrplanzuordnung.isRichtung()) {
            sortiertehaltestellen = FahrtzeitenErmitteln.setzeUhrzeiten(sortiertehaltestellen,
                    fahrplanzuordnung.getStartzeitpunkt());
        } else {
            sortiertehaltestellen = FahrtzeitenErmitteln.setzeUhrzeitenInvertiert(sortiertehaltestellen,
                    fahrplanzuordnung.getStartzeitpunkt());
        }
        // Die Liste wird anhand der Uhrzeiten sortiert um alle Haltestellenabfolgen in
        // der richtigen Reihenfolge zu haben
        Collections.sort(sortiertehaltestellen);
        return new FahrtstreckeMitHaltestellenDTO(fahrplanzuordnung.getFahrtstrecke(), sortiertehaltestellen);
    }

}
