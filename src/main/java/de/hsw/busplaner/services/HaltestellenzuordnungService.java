package de.hsw.busplaner.services;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.hsw.busplaner.beans.Fahrtstrecke;
import de.hsw.busplaner.beans.Haltestelle;
import de.hsw.busplaner.beans.Haltestellenzuordnung;
import de.hsw.busplaner.dtos.haltestellenzuordnung.HaltestellenzuordnungInputDTO;
import de.hsw.busplaner.dtos.haltestellenzuordnung.HaltestellenzuordnungOutputDTO;
import de.hsw.busplaner.repositories.HaltestellenzuordnungRepository;
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

    @Override
    protected HaltestellenzuordnungRepository getRepository() {
        return repository;
    }

    public Long postHaltestellenzuordnug(HaltestellenzuordnungInputDTO haltestellenzuordnungInputDTO) {
        Fahrtstrecke fahrtstrecke = fahrtstreckeService
                .getFahrtstreckeZuId(haltestellenzuordnungInputDTO.getFahrtstreckeId());
        Haltestelle haltestelle = haltestelleService
                .getHaltestelleZuId(haltestellenzuordnungInputDTO.getHaltestelleId());
        haltestelleService.getHaltestelleZuId(haltestellenzuordnungInputDTO.getNaechsteHaltestelle());

        Haltestellenzuordnung haltestellenzuordnung = new Haltestellenzuordnung(haltestellenzuordnungInputDTO,
                fahrtstrecke, haltestelle);
        save(haltestellenzuordnung);
        return haltestellenzuordnung.getId();
    }

    public ArrayList<HaltestellenzuordnungOutputDTO> getAlleZuordnungen() {
        ArrayList<HaltestellenzuordnungOutputDTO> zuordnungen = new ArrayList<>();
        for (Haltestellenzuordnung zuordnung : findAll()) {
            log.info(String.format("Zuordnung: %s gefunden", zuordnung.getId()));
            Haltestelle neachsteHaltestelle = haltestelleService.getHaltestelleZuId(zuordnung.getNaechsteHaltestelle());

            zuordnungen.add(new HaltestellenzuordnungOutputDTO(zuordnung, neachsteHaltestelle));
        }
        return zuordnungen;
    }

    public ArrayList<HaltestellenzuordnungOutputDTO> getAlleZuordnungenZuFahrtstrecke(Long fahrtstreckeId) {
        Fahrtstrecke fahrtstrecke = fahrtstreckeService.getFahrtstreckeZuId(fahrtstreckeId);
        ArrayList<HaltestellenzuordnungOutputDTO> zuordnungen = new ArrayList<>();
        for (Haltestellenzuordnung zuordnung : repository.findAllByFahrtstreckeid(fahrtstrecke)) {
            Haltestelle neachsteHaltestelle = haltestelleService.getHaltestelleZuId(zuordnung.getNaechsteHaltestelle());
            zuordnungen.add(new HaltestellenzuordnungOutputDTO(zuordnung, neachsteHaltestelle));
        }
        return zuordnungen;
    }
}
