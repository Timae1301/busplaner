package de.hsw.busplaner.services;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.management.InstanceNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import de.hsw.busplaner.beans.Buslinie;
import de.hsw.busplaner.beans.Fahrplanzuordnung;
import de.hsw.busplaner.beans.Fahrtstrecke;
import de.hsw.busplaner.beans.Haltestellenzuordnung;
import de.hsw.busplaner.dtos.fahrtstrecke.FahrtstreckeInputDTO;
import de.hsw.busplaner.dtos.fahrtstrecke.FahrtstreckeMitHaltestellenDTO;
import de.hsw.busplaner.dtos.fahrtstrecke.FahrtstreckeMitUhrzeitDTO;
import de.hsw.busplaner.dtos.fahrtstrecke.FahrtstreckeOutputDTO;
import de.hsw.busplaner.dtos.haltestellenzuordnung.HaltestellenzuordnungSortierDTO;
import de.hsw.busplaner.repositories.FahrtstreckeRepository;
import de.hsw.busplaner.util.FahrtzeitenErmitteln;
import de.hsw.busplaner.util.HaltestellenSortierer;
import lombok.extern.java.Log;

/**
 * Der Service der Fahrtstrecke erbt von dem abstrakten BasicService für die
 * CRUD Operationen
 */
@Log
@Service
public class FahrtstreckeService extends BasicService<Fahrtstrecke, Long> {

    private final FahrtstreckeRepository repository;

    @Autowired
    private BuslinieService buslinieService;

    @Autowired
    private FahrplanzuordnungService fahrplanzuordnungService;

    @Autowired
    private HaltestellenzuordnungService haltestellenzuordnungService;

    @Autowired
    private HaltestellenSortierer sortierer;

    @Autowired
    public FahrtstreckeService(final FahrtstreckeRepository repository) {
        this.repository = repository;
    }

    @Override
    protected FahrtstreckeRepository getRepository() {
        return repository;
    }

    /**
     * Gibt eine Menge an Fahrtstrecken zu einer übergebenen Buslinie zurück
     * 
     * @param buslinie
     * @return Iterable mit Fahrtstrecken
     */
    public Iterable<Fahrtstrecke> findAllByBuslinieId(Buslinie buslinie) {
        return repository.findAllByBuslinie(buslinie);
    }

    /**
     * Erstellt und speichert neue Fahrtstrecke anhand eines FahrtstreckeInputDTO
     * 
     * @param fahrtstreckeInputDTO
     * @return ID der neuen Fahrtstrecke
     * @throws IllegalArgumentException wenn zu der ID keine Buslinie gefunden wurde
     * @throws ResponseStatusException  wenn der Fahrtstreckenname bereits vergeben
     *                                  wurde
     */
    public Long postFahrtstrecke(FahrtstreckeInputDTO fahrtstreckeInputDTO)
            throws IllegalArgumentException, ResponseStatusException {
        Fahrtstrecke fahrtstrecke = new Fahrtstrecke(fahrtstreckeInputDTO,
                buslinieService.getBuslinieById(fahrtstreckeInputDTO.getBuslinieId()));
        try {
            findByName(fahrtstrecke.getName());
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    String.format("Der Fahrtstreckenname %s ist bereits vergeben", fahrtstrecke.getName()));
        } catch (IllegalArgumentException e) {
            save(fahrtstrecke);
            return fahrtstrecke.getId();
        }
    }

    /**
     * Ermittelt alle Fahrtstrecken und hängt an das FahrtstreckeOutputDTO, ob die
     * einzelnen Fahrten löschbar sind
     * 
     * @return Liste aus FahrtstreckeOutputDTOs
     */
    public List<FahrtstreckeOutputDTO> getAlleFahrtstrecken() {
        List<FahrtstreckeOutputDTO> fahrtstrecken = new ArrayList<>();
        for (Fahrtstrecke fahrtstrecke : findAll()) {
            FahrtstreckeOutputDTO fahrtstreckeDto = new FahrtstreckeOutputDTO(fahrtstrecke);
            if (isFahrtstreckeLoeschbar(fahrtstrecke)) {
                fahrtstreckeDto.setLoeschbar(true);
            }
            fahrtstrecken.add(fahrtstreckeDto);
        }
        return fahrtstrecken;
    }

    /**
     * Gibt eine Fahrtstrecke zu einem Fahrtstreckennamen zurück
     * 
     * @param fahrtstreckeName
     * @return Fahrtstrecke
     * @throws IllegalArgumentException wenn zu dem Fahrtstreckennamen bereits ein
     *                                  Eintrag vorhanden ist
     */
    public Fahrtstrecke findByName(String fahrtstreckeName) throws IllegalArgumentException {
        Optional<Fahrtstrecke> fahrtstreckeOpt = repository.findByName(fahrtstreckeName);
        if (fahrtstreckeOpt.isPresent()) {
            return fahrtstreckeOpt.get();
        }
        log.warning("Kein Eintrag für Fahrtstreckennamen: " + fahrtstreckeName);
        throw new IllegalArgumentException(
                String.format("Zu dem Fahrtstreckennamen %s wurde kein Eintrag gefunden", fahrtstreckeName));
    }

    /**
     * Findet einen Fahrtstrecke anhand einer ID und gibt ihn zurück
     * 
     * @param id
     * @return Fahrtstrecke
     * @throws IllegalArgumentException wenn zu der ID keine Fahrtstrecke gefunden
     *                                  wurde
     */
    public Fahrtstrecke getFahrtstreckeZuId(Long id) {
        Optional<Fahrtstrecke> fahrtstreckeOpt = findById(id);
        if (fahrtstreckeOpt.isEmpty()) {
            log.warning(String.format("Keine Fahrtstrecke zu ID %s gefunden", id));
            throw new IllegalArgumentException(String.format("Keine Fahrtstrecke zu ID %s gefunden", id));
        }
        return fahrtstreckeOpt.get();
    }

    /**
     * Prüft, ob die Fahrtstrecke löschbar ist. Eine Fahrt ist löschbar, wenn sie in
     * keinem Fahrplan enthalten ist
     * 
     * @param fahrtstrecke
     * @return boolean, ob die Fahrtstrecke löschbar ist
     */
    private boolean isFahrtstreckeLoeschbar(Fahrtstrecke fahrtstrecke) {
        List<Fahrplanzuordnung> fahrplanzuordnungen = fahrplanzuordnungService
                .getAlleFahrplanzuordnungenZuFahrtstrecke(fahrtstrecke);
        return fahrplanzuordnungen.isEmpty();
    }

    /**
     * Löscht Fahrtstrecke und die zugehörigen Haltestellenzuordnungen
     * 
     * @param fahrtstreckeId
     * @return boolean, ob die Fahrtstrecke gelöscht wurde
     * @throws IllegalArgumentException wenn zu der ID keine Fahrtstrecke gefunden
     *                                  wurde
     */
    public boolean deleteFahrtstrecke(Long fahrtstreckeId) throws IllegalArgumentException {
        Fahrtstrecke fahrtstrecke = getFahrtstreckeZuId(fahrtstreckeId);
        if (isFahrtstreckeLoeschbar(fahrtstrecke)) {
            for (Haltestellenzuordnung zuordnung : haltestellenzuordnungService
                    .getAlleZuordnungenZuFahrtstrecke(fahrtstreckeId)) {
                haltestellenzuordnungService.deleteById(zuordnung.getId());
            }
            deleteById(fahrtstrecke.getId());
            return true;
        }
        return false;
    }

    /**
     * Sucht alle Fahrtstrecken zu einer übergebenen BuslinienID und sortiert dessen
     * Haltestellen
     * 
     * @param buslinieId
     * @return Liste mit FahrtstreckeMitHaltestellenDTOs
     * @throws InstanceNotFoundException wenn beim Sortieren der Haltestellen die
     *                                   Informationen zu der Haltestelle nicht
     *                                   ermittelt werden konnten
     * @throws IllegalArgumentException  wenn zu der ID keine Buslinie gefunden
     *                                   wurde
     */
    public List<FahrtstreckeMitHaltestellenDTO> getAlleFahrtstreckenZuBuslinieId(Long buslinieId)
            throws InstanceNotFoundException, IllegalArgumentException {
        List<FahrtstreckeMitHaltestellenDTO> alleFahrtstrecken = new ArrayList<>();
        for (Fahrtstrecke fahrtstrecke : findAllByBuslinieId(buslinieService.getBuslinieById(buslinieId))) {
            alleFahrtstrecken.add(new FahrtstreckeMitHaltestellenDTO(fahrtstrecke,
                    sortierer.sortiereHaltestellen(new ArrayList<>(fahrtstrecke.getHaltestellenzuordnungen()))));
        }
        return alleFahrtstrecken;
    }

    /**
     * Sucht alle Fahrtstrecken zu einer übergebenen BuslinienID und sortiert dessen
     * Haltestellen
     * 
     * @param buslinieId
     * @return Liste mit FahrtstreckeMitHaltestellenDTOs
     * @throws InstanceNotFoundException wenn beim Sortieren der Haltestellen die
     *                                   Informationen zu der Haltestelle nicht
     *                                   ermittelt werden konnten
     * @throws IllegalArgumentException  wenn zu der ID keine Buslinie gefunden
     *                                   wurde
     */
    public List<FahrtstreckeMitHaltestellenDTO> getAlleFahrtstreckenMitHaltestellen()
            throws InstanceNotFoundException, IllegalArgumentException {
        List<FahrtstreckeMitHaltestellenDTO> alleFahrtstrecken = new ArrayList<>();
        for (Fahrtstrecke fahrtstrecke : findAll()) {
            if (fahrtstrecke.getHaltestellenzuordnungen().isEmpty()) {
                break;
            }
            alleFahrtstrecken.add(new FahrtstreckeMitHaltestellenDTO(fahrtstrecke,
                    sortierer.sortiereHaltestellen(new ArrayList<>(fahrtstrecke.getHaltestellenzuordnungen()))));
        }
        return alleFahrtstrecken;
    }

    /**
     * Findet Fahrtstrecken einer übergebenen Buslinie, die eine übergebene
     * Haltestelle enthält und gibt sie aus mit der Uhrzeit zu der die Haltestelle
     * erreicht wird
     * 
     * @param buslinieId
     * @param haltestelleId
     * @return Liste mit FahrtstreckeMitUhrzeitDTOs
     * @throws InstanceNotFoundException wenn beim Sortieren der Haltestellen die
     *                                   Informationen zu der Haltestelle nicht
     *                                   ermittelt werden konnten
     * @throws IllegalArgumentException  wenn zu der ID keine Fahrtstrecke gefunden
     *                                   wurde
     */
    public List<FahrtstreckeMitUhrzeitDTO> ermittleFahrtstreckenMitUhrzeit(Long buslinieId, Long haltestelleId)
            throws InstanceNotFoundException, IllegalArgumentException {
        List<FahrtstreckeMitUhrzeitDTO> fahrtstreckenMitUhrzeit = new ArrayList<>();
        for (FahrtstreckeMitHaltestellenDTO fahrtstreckeMitHaltestellenDTO : getFahrtstreckenMitBuslinieUndHaltestelle(
                buslinieId, haltestelleId)) {
            for (Fahrplanzuordnung fahrplanzuordnung : fahrplanzuordnungService
                    .getAlleFahrplanzuordnungenZuFahrtstrecke(
                            getFahrtstreckeZuId(fahrtstreckeMitHaltestellenDTO.getFahrtstreckeId()))) {
                fahrtstreckenMitUhrzeit.add(new FahrtstreckeMitUhrzeitDTO(fahrtstreckeMitHaltestellenDTO,
                        getUhrzeitAnHaltestelle(fahrplanzuordnung, haltestelleId, fahrtstreckeMitHaltestellenDTO)));
            }
        }
        return fahrtstreckenMitUhrzeit;
    }

    /**
     * Ermittelt ja nach Fahrtrichtung zu welcher Uhrzeit eine Haltestelle erreicht
     * wird
     * 
     * @param fahrplanzuordnung
     * @param haltestelleId
     * @param fahrtstreckeMitHaltestellenDTO
     * @return LocalTime wann die Haltestelle erreicht wird
     */
    private LocalTime getUhrzeitAnHaltestelle(Fahrplanzuordnung fahrplanzuordnung, Long haltestelleId,
            FahrtstreckeMitHaltestellenDTO fahrtstreckeMitHaltestellenDTO) {
        int fahrtzeit;
        if (fahrplanzuordnung.isRichtung()) {
            fahrtzeit = FahrtzeitenErmitteln
                    .ermittleFahrtzeitInMinuten(fahrtstreckeMitHaltestellenDTO.getHaltestellen(), haltestelleId);
        } else {
            fahrtzeit = FahrtzeitenErmitteln.ermittleFahrtzeitInMinutenInvertiert(
                    fahrtstreckeMitHaltestellenDTO.getHaltestellen(), haltestelleId);
        }
        return fahrplanzuordnung.getStartzeitpunkt().plusMinutes(fahrtzeit);
    }

    /**
     * Ermittelt alle Fahrt zu einer übergebenen BuslinieID und prüft ob die
     * übergebene HaltestelleID in der Fahrt enthalten ist
     * 
     * @param buslinieId
     * @param haltestelleId
     * @return Liste mit FahrtstreckeMitHaltestellenDTO
     * @throws InstanceNotFoundException wenn beim Sortieren der Haltestellen die
     *                                   Informationen zu der Haltestelle nicht
     *                                   ermittelt werden konnten
     * @throws IllegalArgumentException  wenn zu der ID keine Buslinie gefunden
     *                                   wurde
     */
    private List<FahrtstreckeMitHaltestellenDTO> getFahrtstreckenMitBuslinieUndHaltestelle(Long buslinieId,
            Long haltestelleId) throws InstanceNotFoundException, IllegalArgumentException {
        List<FahrtstreckeMitHaltestellenDTO> fahrtstreckenMithaltestellen = new ArrayList<>();
        for (FahrtstreckeMitHaltestellenDTO fahrtstrecke : getAlleFahrtstreckenZuBuslinieId(buslinieId)) {
            if (isHaltestelleInFahrt(fahrtstrecke, haltestelleId)) {
                fahrtstreckenMithaltestellen.add(fahrtstrecke);
            }
        }
        return fahrtstreckenMithaltestellen;
    }

    /**
     * Prüft, ob eine Haltestelle in einer Fahrt enthalten ist
     * 
     * @param fahrtstrecke
     * @param haltestelleId
     * @return boolean, ob die Haltestelle in der Fahrt enthalten ist
     */
    private boolean isHaltestelleInFahrt(FahrtstreckeMitHaltestellenDTO fahrtstrecke, Long haltestelleId) {
        for (HaltestellenzuordnungSortierDTO haltestelle : fahrtstrecke.getHaltestellen()) {
            if (haltestelle.getHaltestelleId().equals(haltestelleId)) {
                return true;
            }
        }
        return false;
    }
}
