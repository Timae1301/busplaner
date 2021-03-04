package de.hsw.busplaner.services;

import java.util.ArrayList;
import java.util.Optional;

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
        Optional<Fahrtstrecke> fahrtstreckeOpt = fahrtstreckeService
                .findById(haltestellenzuordnungInputDTO.getFahrtstreckeId());
        if (fahrtstreckeOpt.isEmpty()) {
            throw new IllegalArgumentException(String.format("Keine Fahrtstrecke zu ID %s gefunden",
                    haltestellenzuordnungInputDTO.getFahrtstreckeId()));
        }
        Optional<Haltestelle> haltestelleOpt = haltestelleService
                .findById(haltestellenzuordnungInputDTO.getHaltestelleId());
        if (haltestelleOpt.isEmpty()) {
            throw new IllegalArgumentException(String.format("Keine Haltestelle zu ID %s gefunden",
                    haltestellenzuordnungInputDTO.getHaltestelleId()));
        }
        Haltestellenzuordnung haltestellenzuordnung = new Haltestellenzuordnung(haltestellenzuordnungInputDTO,
                fahrtstreckeOpt.get(), haltestelleOpt.get());
        save(haltestellenzuordnung);
        return haltestellenzuordnung.getId();
    }

    public ArrayList<HaltestellenzuordnungOutputDTO> getAlleZuordnungen() {
        ArrayList<HaltestellenzuordnungOutputDTO> zuordnungen = new ArrayList<>();
        for (Haltestellenzuordnung zuordnung : findAll()) {
            log.info(String.format("Zuordnung: %s gefunden", zuordnung.getId()));

            Optional<Haltestelle> neachsteHaltestelleOpt = haltestelleService
                    .findById(zuordnung.getNaechsteHaltestelle());
            if (neachsteHaltestelleOpt.isEmpty()) {
                throw new IllegalArgumentException(
                        String.format("Keine Haltestelle zu ID %s gefunden", zuordnung.getNaechsteHaltestelle()));
            }
            zuordnungen.add(new HaltestellenzuordnungOutputDTO(zuordnung, neachsteHaltestelleOpt.get()));
        }
        return zuordnungen;
    }
}
