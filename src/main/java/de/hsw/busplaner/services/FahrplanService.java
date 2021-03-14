package de.hsw.busplaner.services;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.hsw.busplaner.beans.Fahrplan;
import de.hsw.busplaner.beans.Fahrplanzuordnung;
import de.hsw.busplaner.dtos.fahrplan.FahrplanOutputDTO;
import de.hsw.busplaner.repositories.FahrplanRepository;
import lombok.extern.java.Log;

@Log
@Service
public class FahrplanService extends BasicService<Fahrplan, Long> {

    @Autowired
    FahrplanRepository repository;

    @Autowired
    FahrplanzuordnungService fahrplanzuordnungService;

    @Override
    protected FahrplanRepository getRepository() {
        return repository;
    }

    public Long postFahrplan(String name) {
        Fahrplan fahrplan = new Fahrplan(name);
        log.info(String.format("Neuen Fahrplan: %s angelegt", fahrplan));
        return save(fahrplan).getId();
    }

    public ArrayList<FahrplanOutputDTO> getAlleFahrplaene() {
        ArrayList<FahrplanOutputDTO> fahrplaene = new ArrayList<>();
        for (Fahrplan fahrplan : findAll()) {
            log.info(String.format("Fahrplan: %s gefunden", fahrplan.getName()));
            fahrplaene.add(new FahrplanOutputDTO(fahrplan));
        }
        return fahrplaene;
    }

    public Fahrplan getFahrplanZuId(Long id) {
        Optional<Fahrplan> fahrplanOpt = findById(id);
        if (fahrplanOpt.isEmpty()) {
            throw new IllegalArgumentException(String.format("Keine Fahrplan zu ID %s gefunden", id));
        }
        return fahrplanOpt.get();
    }

    public Boolean deleteFahrplan(Long fahrplanId) {
        ArrayList<Fahrplanzuordnung> fahrplanzuordnungen = fahrplanzuordnungService
                .getAlleFahrplanzuordnungenZuFahrplan(getFahrplanZuId(fahrplanId));
        for (Fahrplanzuordnung fahrplanzuordnung : fahrplanzuordnungen) {
            fahrplanzuordnungService.deleteById(fahrplanzuordnung.getId());
        }
        deleteById(fahrplanId);
        return true;
    }

}
