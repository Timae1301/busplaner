package de.hsw.busplaner.services;

import java.util.ArrayList;
import java.util.List;

import javax.management.InstanceNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.hsw.busplaner.beans.Fahrtstrecke;
import de.hsw.busplaner.beans.Haltestelle;
import de.hsw.busplaner.beans.Haltestellenzuordnung;
import de.hsw.busplaner.dtos.haltestellenzuordnung.HaltestellenzuordnungInputDTO;
import de.hsw.busplaner.dtos.haltestellenzuordnung.HaltestellenzuordnungOutputDTO;
import de.hsw.busplaner.dtos.haltestellenzuordnung.HaltestellenzuordnungSortierDTO;
import de.hsw.busplaner.repositories.HaltestellenzuordnungRepository;
import de.hsw.busplaner.util.HaltestellenSortierer;
import lombok.extern.java.Log;

/**
 * Der Service der Haltestellenzuordnung erbt von dem abstrakten BasicService
 * für die CRUD Operationen
 */
@Log
@Service
public class HaltestellenzuordnungService extends BasicService<Haltestellenzuordnung, Long> {

    private final HaltestellenzuordnungRepository repository;

    @Autowired
    private HaltestellenSortierer sortierer;

    @Autowired
    private FahrtstreckeService fahrtstreckeService;

    @Autowired
    private HaltestelleService haltestelleService;

    @Autowired
    public HaltestellenzuordnungService(final HaltestellenzuordnungRepository repository) {
        this.repository = repository;
    }

    @Override
    protected HaltestellenzuordnungRepository getRepository() {
        return repository;
    }

    /**
     * Erstellt und speichert eine neue Haltestellenzuordnung anhand des
     * HaltestellenzuordnungInputDTOs
     * 
     * @param haltestellenzuordnungInputDTO
     * @return ID der neuen Haltestellenzuordnung
     */
    public Long postHaltestellenzuordnug(HaltestellenzuordnungInputDTO haltestellenzuordnungInputDTO) {
        Fahrtstrecke fahrtstrecke = fahrtstreckeService
                .getFahrtstreckeZuId(haltestellenzuordnungInputDTO.getFahrtstreckeId());
        Haltestelle haltestelle = haltestelleService
                .getHaltestelleById(haltestellenzuordnungInputDTO.getHaltestelleId());
        haltestelleService.getHaltestelleById(haltestellenzuordnungInputDTO.getNaechsteHaltestelle());

        Haltestellenzuordnung haltestellenzuordnung = new Haltestellenzuordnung(haltestellenzuordnungInputDTO,
                fahrtstrecke, haltestelle);
        save(haltestellenzuordnung);
        return haltestellenzuordnung.getId();
    }

    /**
     * Findet alle vorhanden Haltestellenzuordnungen und erstellt damit eine Liste
     * mit HaltestellenzuordnungOutputDTOs
     * 
     * @return Liste mit HaltestellenzuordnungOutputDTOs
     */
    public List<HaltestellenzuordnungOutputDTO> getAlleZuordnungen() {
        List<HaltestellenzuordnungOutputDTO> zuordnungen = new ArrayList<>();
        for (Haltestellenzuordnung zuordnung : findAll()) {
            log.info(String.format("Zuordnung: %s gefunden", zuordnung.getId()));
            Haltestelle neachsteHaltestelle = haltestelleService.getHaltestelleById(zuordnung.getNaechsteHaltestelle());

            zuordnungen.add(new HaltestellenzuordnungOutputDTO(zuordnung, neachsteHaltestelle));
        }
        return zuordnungen;
    }

    /**
     * Findet alle Haltestellenzuordnungen zu einer übergebenen FahrtstreckeID und
     * gibt die Haltestellen soritert aus
     * 
     * @param fahrtstreckeId
     * @return Liste mit HaltestellenzuordnungSortierDTOs
     * @throws InstanceNotFoundException wenn zu der ID keine Fahrtstrecke gefunden
     *                                   wurde
     */
    public List<HaltestellenzuordnungSortierDTO> getAlleZuordnungenSorted(Long fahrtstreckeId)
            throws InstanceNotFoundException {
        ArrayList<Haltestellenzuordnung> zuordnungen = new ArrayList<>();
        Fahrtstrecke fahrtstrecke = fahrtstreckeService.getFahrtstreckeZuId(fahrtstreckeId);
        repository.findAllByFahrtstrecke(fahrtstrecke).forEach(zuordnungen::add);
        return sortierer.sortiereHaltestellen(zuordnungen);
    }

    /**
     * Findet alle Haltestellenzuordnungen zu übergebener FahrtstreckeID
     * 
     * @param fahrtstreckeId
     * @return Liste mit Haltestellenzuordnungen
     */
    public List<Haltestellenzuordnung> getAlleZuordnungenZuFahrtstrecke(Long fahrtstreckeId) {
        Fahrtstrecke fahrtstrecke = fahrtstreckeService.getFahrtstreckeZuId(fahrtstreckeId);
        List<Haltestellenzuordnung> zuordnungen = new ArrayList<>();
        for (Haltestellenzuordnung zuordnung : repository.findAllByFahrtstrecke(fahrtstrecke)) {
            zuordnungen.add(zuordnung);
        }
        return zuordnungen;
    }

}
