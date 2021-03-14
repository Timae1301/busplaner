package de.hsw.busplaner.services;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.hsw.busplaner.beans.Fahrplan;
import de.hsw.busplaner.beans.Fahrplanzuordnung;
import de.hsw.busplaner.beans.Fahrtstrecke;
import de.hsw.busplaner.dtos.fahrplanzuordnung.FahrplanzuordnungInputDTO;
import de.hsw.busplaner.dtos.fahrplanzuordnung.FahrplanzuordnungOutputDTO;
import de.hsw.busplaner.repositories.FahrplanzuordnungRepository;
import lombok.extern.java.Log;

@Log
@Service
public class FahrplanzuordnungService extends BasicService<Fahrplanzuordnung, Long> {

    @Autowired
    FahrplanzuordnungRepository repository;

    @Autowired
    FahrtstreckeService fahrtstreckeService;

    @Autowired
    FahrplanService fahrplanService;

    @Override
    protected FahrplanzuordnungRepository getRepository() {
        return repository;
    }

    public Long postFahrplanzuordnung(FahrplanzuordnungInputDTO fahrplanzuordnungInputDTO) {
        Fahrtstrecke fahrtstrecke = fahrtstreckeService
                .getFahrtstreckeZuId(fahrplanzuordnungInputDTO.getFahrtstreckeId());
        Fahrplan fahrplan = fahrplanService.getFahrplanZuId(fahrplanzuordnungInputDTO.getFahrplanId());
        Fahrplanzuordnung fahrplanzuordnung = new Fahrplanzuordnung(fahrplanzuordnungInputDTO, fahrtstrecke, fahrplan);
        save(fahrplanzuordnung);
        return fahrplanzuordnung.getId();
    }

    public ArrayList<FahrplanzuordnungOutputDTO> getAlleFahrplanzuordnungen() {
        ArrayList<FahrplanzuordnungOutputDTO> zuordnungen = new ArrayList<>();
        for (Fahrplanzuordnung fahrplanzuordnung : findAll()) {
            log.info(String.format("Zuordnung: %s gefunden", fahrplanzuordnung.getId()));
            zuordnungen.add(new FahrplanzuordnungOutputDTO(fahrplanzuordnung));
        }
        return zuordnungen;
    }

}
