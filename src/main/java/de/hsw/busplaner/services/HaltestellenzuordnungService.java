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

    public List<HaltestellenzuordnungOutputDTO> getAlleZuordnungen() {
        List<HaltestellenzuordnungOutputDTO> zuordnungen = new ArrayList<>();
        for (Haltestellenzuordnung zuordnung : findAll()) {
            log.info(String.format("Zuordnung: %s gefunden", zuordnung.getId()));
            Haltestelle neachsteHaltestelle = haltestelleService.getHaltestelleById(zuordnung.getNaechsteHaltestelle());

            zuordnungen.add(new HaltestellenzuordnungOutputDTO(zuordnung, neachsteHaltestelle));
        }
        return zuordnungen;
    }

    public List<HaltestellenzuordnungSortierDTO> getAlleZuordnungenSorted(Long fahrtstreckeId)
            throws InstanceNotFoundException {
        ArrayList<Haltestellenzuordnung> zuordnungen = new ArrayList<>();
        Fahrtstrecke fahrtstrecke = fahrtstreckeService.getFahrtstreckeZuId(fahrtstreckeId);
        repository.findAllByFahrtstreckeid(fahrtstrecke).forEach(zuordnungen::add);
        return sortierer.sortiereHaltestellen(zuordnungen);
    }

    public List<HaltestellenzuordnungOutputDTO> getAlleZuordnungenDTOsZuFahrtstrecke(Long fahrtstreckeId) {
        Fahrtstrecke fahrtstrecke = fahrtstreckeService.getFahrtstreckeZuId(fahrtstreckeId);
        List<HaltestellenzuordnungOutputDTO> zuordnungen = new ArrayList<>();
        for (Haltestellenzuordnung zuordnung : repository.findAllByFahrtstreckeid(fahrtstrecke)) {
            Haltestelle neachsteHaltestelle = haltestelleService.getHaltestelleById(zuordnung.getNaechsteHaltestelle());
            zuordnungen.add(new HaltestellenzuordnungOutputDTO(zuordnung, neachsteHaltestelle));
        }
        return zuordnungen;
    }

    public List<Haltestellenzuordnung> getAlleZuordnungenZuFahrtstrecke(Long fahrtstreckeId) {
        Fahrtstrecke fahrtstrecke = fahrtstreckeService.getFahrtstreckeZuId(fahrtstreckeId);
        List<Haltestellenzuordnung> zuordnungen = new ArrayList<>();
        for (Haltestellenzuordnung zuordnung : repository.findAllByFahrtstreckeid(fahrtstrecke)) {
            zuordnungen.add(zuordnung);
        }
        return zuordnungen;
    }

}
