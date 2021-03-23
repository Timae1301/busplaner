package de.hsw.busplaner.services;

import java.time.LocalTime;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
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

    private final FahrtstreckeService fahrtstreckeService;

    private final FahrplanService fahrplanService;

    @Autowired
    public FahrplanzuordnungService(final FahrplanzuordnungRepository repository,
            @Lazy final FahrtstreckeService fahrtstreckeService, @Lazy final FahrplanService fahrplanService) {
        this.repository = repository;
        this.fahrtstreckeService = fahrtstreckeService;
        this.fahrplanService = fahrplanService;
    }

    @Override
    protected FahrplanzuordnungRepository getRepository() {
        return repository;
    }

    public Long postFahrplanzuordnung(FahrplanzuordnungInputDTO fahrplanzuordnungInputDTO) {
        Fahrtstrecke fahrtstrecke = fahrtstreckeService
                .getFahrtstreckeZuId(fahrplanzuordnungInputDTO.getFahrtstreckeId());
        pruefeGueltigkeitDerFahrtstreckeFuerFahrplan(fahrplanzuordnungInputDTO, fahrtstrecke.getBuslinieId().getId());
        Fahrplan fahrplan = fahrplanService.getFahrplanZuId(fahrplanzuordnungInputDTO.getFahrplanId());
        Fahrplanzuordnung fahrplanzuordnung = new Fahrplanzuordnung(fahrplanzuordnungInputDTO, fahrtstrecke, fahrplan);
        save(fahrplanzuordnung);
        return fahrplanzuordnung.getId();
    }

    private void pruefeGueltigkeitDerFahrtstreckeFuerFahrplan(FahrplanzuordnungInputDTO fahrplanzuordnungInputDTO,
            Long buslinieId) {
        ArrayList<Fahrplanzuordnung> fahrplanzuordnungen = getAlleFahrplanzuordnungenZuFahrplanId(
                fahrplanService.getFahrplanZuId(fahrplanzuordnungInputDTO.getFahrplanId()));
        if (fahrplanzuordnungen.isEmpty()) {
            return;
        }
        Long buslinieIdFahrplan = fahrplanzuordnungen.get(0).getFahrtstreckeid().getBuslinieId().getId();
        if (buslinieIdFahrplan.equals(buslinieId)) {
            return;
        }
        throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                String.format("Die Fahrtstrecke muss von der Buslinie %s sein", buslinieIdFahrplan));
    }

    public ArrayList<FahrplanzuordnungOutputDTO> getAlleFahrplanzuordnungen() {
        ArrayList<FahrplanzuordnungOutputDTO> zuordnungen = new ArrayList<>();
        for (Fahrplanzuordnung fahrplanzuordnung : findAll()) {
            log.info(String.format("Zuordnung: %s gefunden", fahrplanzuordnung.getId()));
            zuordnungen.add(new FahrplanzuordnungOutputDTO(fahrplanzuordnung));
        }
        return zuordnungen;
    }

    public ArrayList<Fahrplanzuordnung> getAlleFahrplanzuordnungenZuFahrplanId(Fahrplan fahrplanId) {
        ArrayList<Fahrplanzuordnung> zuordnungen = new ArrayList<>();
        repository.findAllByFahrplanid(fahrplanId).forEach(zuordnungen::add);
        return zuordnungen;
    }

    public ArrayList<Fahrplanzuordnung> getAlleFahrplanzuordnungenZuFahrtstreckeId(Fahrtstrecke fahrtstreckeId) {
        ArrayList<Fahrplanzuordnung> zuordnungen = new ArrayList<>();
        repository.findAllByFahrtstreckeid(fahrtstreckeId).forEach(zuordnungen::add);
        return zuordnungen;
    }

    public ArrayList<Fahrplanzuordnung> getAlleFahrplanzuordnungenZuFahrplanIdInTime(Fahrplan fahrplanId,
            LocalTime zeitpunktVorher, LocalTime zeitpunktNachher) {
        ArrayList<Fahrplanzuordnung> zuordnungen = new ArrayList<>();
        repository.findAllByFahrplanidAndStartzeitpunktBetween(fahrplanId, zeitpunktVorher, zeitpunktNachher)
                .forEach(zuordnungen::add);
        return zuordnungen;
    }
}
