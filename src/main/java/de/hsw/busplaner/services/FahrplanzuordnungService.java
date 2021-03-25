package de.hsw.busplaner.services;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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

    private final FahrplanzuordnungRepository repository;

    @Autowired
    private FahrtstreckeService fahrtstreckeService;

    @Autowired
    private FahrplanService fahrplanService;

    @Autowired
    public FahrplanzuordnungService(final FahrplanzuordnungRepository repository) {
        this.repository = repository;
    }

    @Override
    protected FahrplanzuordnungRepository getRepository() {
        return repository;
    }

    public Long postFahrplanzuordnung(FahrplanzuordnungInputDTO fahrplanzuordnungInputDTO) {
        Fahrtstrecke fahrtstrecke = fahrtstreckeService
                .getFahrtstreckeZuId(fahrplanzuordnungInputDTO.getFahrtstreckeId());
        pruefeGueltigkeitDerFahrtstreckeFuerFahrplan(fahrplanzuordnungInputDTO, fahrtstrecke.getBuslinie().getId());
        Fahrplan fahrplan = fahrplanService.getFahrplanZuId(fahrplanzuordnungInputDTO.getFahrplanId());
        Fahrplanzuordnung fahrplanzuordnung = new Fahrplanzuordnung(fahrplanzuordnungInputDTO, fahrtstrecke, fahrplan);
        save(fahrplanzuordnung);
        return fahrplanzuordnung.getId();
    }

    private void pruefeGueltigkeitDerFahrtstreckeFuerFahrplan(FahrplanzuordnungInputDTO fahrplanzuordnungInputDTO,
            Long buslinieId) {
        List<Fahrplanzuordnung> fahrplanzuordnungen = getAlleFahrplanzuordnungenZuFahrplan(
                fahrplanService.getFahrplanZuId(fahrplanzuordnungInputDTO.getFahrplanId()));
        if (fahrplanzuordnungen.isEmpty()) {
            return;
        }
        Long buslinieIdFahrplan = fahrplanzuordnungen.get(0).getFahrtstrecke().getBuslinie().getId();
        if (buslinieIdFahrplan.equals(buslinieId)) {
            return;
        }
        throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                String.format("Die Fahrtstrecke muss von der Buslinie %s sein", buslinieIdFahrplan));
    }

    public List<FahrplanzuordnungOutputDTO> getAlleFahrplanzuordnungen() {
        List<FahrplanzuordnungOutputDTO> zuordnungen = new ArrayList<>();
        for (Fahrplanzuordnung fahrplanzuordnung : findAll()) {
            log.info(String.format("Zuordnung: %s gefunden", fahrplanzuordnung.getId()));
            zuordnungen.add(new FahrplanzuordnungOutputDTO(fahrplanzuordnung));
        }
        return zuordnungen;
    }

    public List<Fahrplanzuordnung> getAlleFahrplanzuordnungenZuFahrplan(Fahrplan fahrplan) {
        List<Fahrplanzuordnung> zuordnungen = new ArrayList<>();
        repository.findAllByFahrplan(fahrplan).forEach(zuordnungen::add);
        return zuordnungen;
    }

    public List<Fahrplanzuordnung> getAlleFahrplanzuordnungenZuFahrtstrecke(Fahrtstrecke fahrtstrecke) {
        List<Fahrplanzuordnung> zuordnungen = new ArrayList<>();
        repository.findAllByFahrtstrecke(fahrtstrecke).forEach(zuordnungen::add);
        return zuordnungen;
    }

    public List<Fahrplanzuordnung> getAlleFahrplanzuordnungenZuFahrplanInTime(Fahrplan fahrplan,
            LocalTime zeitpunktVorher, LocalTime zeitpunktNachher) {
        List<Fahrplanzuordnung> zuordnungen = new ArrayList<>();
        repository.findAllByFahrplanAndStartzeitpunktBetween(fahrplan, zeitpunktVorher, zeitpunktNachher)
                .forEach(zuordnungen::add);
        return zuordnungen;
    }
}
