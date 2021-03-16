package de.hsw.busplaner.services;

import java.util.ArrayList;

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

    @Autowired
    HaltestellenzuordnungRepository repository;

    @Autowired
    FahrtstreckeService fahrtstreckeService;

    @Autowired
    HaltestelleService haltestelleService;

    @Autowired
    HaltestellenSortierer haltestellenSortierer;

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

    public ArrayList<HaltestellenzuordnungOutputDTO> getAlleZuordnungen() {
        ArrayList<HaltestellenzuordnungOutputDTO> zuordnungen = new ArrayList<>();
        for (Haltestellenzuordnung zuordnung : findAll()) {
            log.info(String.format("Zuordnung: %s gefunden", zuordnung.getId()));
            Haltestelle neachsteHaltestelle = haltestelleService.getHaltestelleById(zuordnung.getNaechsteHaltestelle());

            zuordnungen.add(new HaltestellenzuordnungOutputDTO(zuordnung, neachsteHaltestelle));
        }
        return zuordnungen;
    }

    public ArrayList<HaltestellenzuordnungSortierDTO> getAlleZuordnungenSorted(Long fahrtstreckeId)
            throws InstanceNotFoundException {
        ArrayList<Haltestellenzuordnung> zuordnungen = new ArrayList<>();
        Fahrtstrecke fahrtstrecke = fahrtstreckeService.getFahrtstreckeZuId(fahrtstreckeId);
        repository.findAllByFahrtstreckeid(fahrtstrecke).forEach(zuordnungen::add);
        return haltestellenSortierer.sortiereHaltestellen(zuordnungen);
    }

    public ArrayList<HaltestellenzuordnungOutputDTO> getAlleZuordnungenDTOsZuFahrtstrecke(Long fahrtstreckeId) {
        Fahrtstrecke fahrtstrecke = fahrtstreckeService.getFahrtstreckeZuId(fahrtstreckeId);
        ArrayList<HaltestellenzuordnungOutputDTO> zuordnungen = new ArrayList<>();
        for (Haltestellenzuordnung zuordnung : repository.findAllByFahrtstreckeid(fahrtstrecke)) {
            Haltestelle neachsteHaltestelle = haltestelleService.getHaltestelleById(zuordnung.getNaechsteHaltestelle());
            zuordnungen.add(new HaltestellenzuordnungOutputDTO(zuordnung, neachsteHaltestelle));
        }
        return zuordnungen;
    }

    public ArrayList<Haltestellenzuordnung> getAlleZuordnungenZuFahrtstrecke(Long fahrtstreckeId) {
        Fahrtstrecke fahrtstrecke = fahrtstreckeService.getFahrtstreckeZuId(fahrtstreckeId);
        ArrayList<Haltestellenzuordnung> zuordnungen = new ArrayList<>();
        for (Haltestellenzuordnung zuordnung : repository.findAllByFahrtstreckeid(fahrtstrecke)) {
            zuordnungen.add(zuordnung);
        }
        return zuordnungen;
    }

}
