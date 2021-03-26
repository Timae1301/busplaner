package de.hsw.busplaner.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.management.InstanceNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import de.hsw.busplaner.beans.Buslinie;
import de.hsw.busplaner.beans.Fahrtstrecke;
import de.hsw.busplaner.dtos.buslinie.BuslinieOutputDTO;
import de.hsw.busplaner.dtos.fahrtstrecke.FahrtstreckeOutputDTO;
import de.hsw.busplaner.repositories.BuslinieRepository;
import lombok.extern.java.Log;

/**
 * Der Service der Buslinie erbt von dem abstrakten BasicService für die CRUD
 * Operationen
 */
@Service
@Log
public class BuslinieService extends BasicService<Buslinie, Long> {

    private final BuslinieRepository repository;

    @Autowired
    private FahrtstreckeService fahrtstreckeService;

    @Autowired
    private HaltestelleService haltestelleService;

    @Autowired
    public BuslinieService(final BuslinieRepository repository) {
        this.repository = repository;
    }

    @Override
    protected BuslinieRepository getRepository() {
        return repository;
    }

    /**
     * Gibt eine Buslinie zu einer BusNr zurück
     * 
     * @param busnr
     * @return Buslinie
     * @throws IllegalArgumentException wenn zu der BusNr kein Eintrag gefunden
     *                                  wurde
     */
    public Buslinie findByBusnr(Long busnr) throws IllegalArgumentException {
        Optional<Buslinie> buslinieOpt = repository.findByBusnr(busnr);
        if (buslinieOpt.isPresent()) {
            return buslinieOpt.get();
        }
        log.warning("Kein Eintrag für BusNr: " + busnr);
        throw new IllegalArgumentException(String.format("Zu der BusNr %s wurde kein Eintrag gefunden", busnr));
    }

    /**
     * Liest alle Buslinien aus dem Repository aus und erstellt daraus eine Liste an
     * BuslinieOutputDTOs
     * 
     * @return Liste aus BuslinieOutputDTOs
     */
    public List<BuslinieOutputDTO> getAllBuslinien() {
        List<BuslinieOutputDTO> buslinien = new ArrayList<>();
        for (Buslinie buslinie : findAll()) {
            log.info(String.format("Buslinie: %s gefunden", buslinie.getBusnr()));
            BuslinieOutputDTO buslinieDto = new BuslinieOutputDTO(buslinie);
            if (isBuslinieLoeschbar(buslinie.getId())) {
                buslinieDto.setLoeschbar(true);
            }
            buslinien.add(buslinieDto);
        }
        return buslinien;
    }

    /**
     * Speichert eine neue Buslinie anhand ihrer BusNr und gibt die ID zurück
     * 
     * @param busNr
     * @return ID der neuen Buslinie
     * @throws ResponseStatusException Wenn die BusNr schon vergeben ist
     */
    public Long postBuslinie(Long busNr) throws ResponseStatusException {
        try {
            findByBusnr(busNr);
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    String.format("Die BusNr %s ist bereits vergeben", busNr));
        } catch (IllegalArgumentException e) {
            log.info(String.format("Neue Buslinie: %s angelegt", busNr));
            return save(new Buslinie(busNr)).getId();
        }
    }

    /**
     * Erstellt aus einer BuslinieID ein BuslinieOutputDTO
     * 
     * @param buslinieId
     * @return BuslinieOutputDTO
     * @throws IllegalArgumentException wenn zu der ID keine Buslinie gefunden wurde
     */
    public BuslinieOutputDTO getBuslinieDtoById(Long buslinieId) throws IllegalArgumentException {
        return new BuslinieOutputDTO(getBuslinieById(buslinieId));
    }

    /**
     * Findet eine Buslinie anhand einer ID und gibt sie zurück
     * 
     * @param buslinieId
     * @return Buslinie
     * @throws IllegalArgumentException wenn zu der ID keine Buslinie gefunden wurde
     */
    public Buslinie getBuslinieById(Long buslinieId) throws IllegalArgumentException {
        Optional<Buslinie> buslinieOpt = findById(buslinieId);
        if (buslinieOpt.isPresent()) {
            return buslinieOpt.get();
        }
        throw new IllegalArgumentException(
                String.format("Zu der BuslinienId %s wurde kein Eintrag gefunden", buslinieId));
    }

    /**
     * Speichert eine berarbeitete Buslinie ab
     * 
     * @param buslinieDTO
     * @throws ResponseStatusException wenn die Buslinie bereits vergeben ist
     */
    public void patchBuslinie(BuslinieOutputDTO buslinieDTO) throws ResponseStatusException {
        try {
            findByBusnr(buslinieDTO.getBusnr());
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    String.format("Die BusNr %s ist bereits vergeben", buslinieDTO.getBusnr()));
        } catch (IllegalArgumentException e) {
            save(new Buslinie(buslinieDTO));
        }

    }

    /**
     * Prüft die Löschbarkeit einer Buslinie anhand der Präsenz von Fahrtstrecken zu
     * dieser Buslinie
     * 
     * @param buslinieId
     * @return boolean, ob Fahrtstrecken zu der Buslinie existieren
     * @throws IllegalArgumentException wenn zu der ID keine Buslinie gefunden wurde
     */
    private boolean isBuslinieLoeschbar(Long buslinieId) throws IllegalArgumentException {
        List<Fahrtstrecke> fahrtstreckenMitBuslinie = new ArrayList<>();
        Buslinie buslinie = getBuslinieById(buslinieId);
        fahrtstreckeService.findAllByBuslinieId(buslinie).forEach(fahrtstreckenMitBuslinie::add);
        return fahrtstreckenMitBuslinie.isEmpty();
    }

    /**
     * Prüft, ob eine Buslinie gelöscht werden kann und löscht sie anhand ihrer ID
     * 
     * @param buslinieId
     * @return boolean, ob die Buslinie gelöscht wurde
     * @throws IllegalArgumentException wenn zu der ID keine Buslinie gefunden wurde
     */
    public boolean deleteBuslinie(Long buslinieId) throws IllegalArgumentException {
        boolean isBuslinieLoeschbar = isBuslinieLoeschbar(buslinieId);
        if (isBuslinieLoeschbar) {
            deleteById(buslinieId);
        }
        return isBuslinieLoeschbar;
    }

    /**
     * Gibt alle Buslinien aus, die die übergebene Haltestelle beinhalten
     * 
     * @param haltestelleId
     * @return Liste mit BuslinienDTOs in denen die übergebene Haltestelle ist
     * @throws InstanceNotFoundException wenn beim Sortieren der Haltestellen die
     *                                   Informationen zu der Haltestelle nicht
     *                                   ermittelt werden konnten
     */
    public List<BuslinieOutputDTO> getAlleBuslinienFuerHaltestelle(Long haltestelleId)
            throws InstanceNotFoundException {
        List<BuslinieOutputDTO> buslinien = new ArrayList<>();
        for (FahrtstreckeOutputDTO fahrtstreckeOutputDTO : fahrtstreckeService.getAlleFahrtstrecken()) {
            if (haltestelleService.isHaltestelleInFahrtstrecke(haltestelleId, fahrtstreckeOutputDTO.getId())) {
                buslinien.add(new BuslinieOutputDTO(findByBusnr(fahrtstreckeOutputDTO.getBuslinie())));
            }
        }
        return buslinien;
    }

}
