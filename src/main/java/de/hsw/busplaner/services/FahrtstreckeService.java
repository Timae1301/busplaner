package de.hsw.busplaner.services;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.hsw.busplaner.beans.Buslinie;
import de.hsw.busplaner.beans.Fahrplanzuordnung;
import de.hsw.busplaner.beans.Fahrtstrecke;
import de.hsw.busplaner.dtos.fahrtstrecke.FahrtstreckeInputDTO;
import de.hsw.busplaner.dtos.fahrtstrecke.FahrtstreckeOutputDTO;
import de.hsw.busplaner.dtos.haltestellenzuordnung.HaltestellenzuordnungOutputDTO;
import de.hsw.busplaner.repositories.FahrtstreckeRepository;

@Service
public class FahrtstreckeService extends BasicService<Fahrtstrecke, Long> {

    @Autowired
    FahrtstreckeRepository repository;

    @Autowired
    BuslinieService buslinieService;

    @Autowired
    FahrplanzuordnungService fahrplanzuordnungService;

    @Autowired
    HaltestellenzuordnungService haltestellenzuordnungService;

    @Override
    protected FahrtstreckeRepository getRepository() {
        return repository;
    }

    public Iterable<Fahrtstrecke> findAllByBuslinieId(Buslinie buslinieId) {
        return repository.findAllByBuslinieId(buslinieId);
    }

    public Long postFahrtstrecke(FahrtstreckeInputDTO fahrtstreckeInputDTO) throws IllegalArgumentException {
        Fahrtstrecke fahrtstrecke = new Fahrtstrecke(fahrtstreckeInputDTO,
                buslinieService.getBuslinieById(fahrtstreckeInputDTO.getBuslinieId()));
        save(fahrtstrecke);
        return fahrtstrecke.getId();
    }

    public ArrayList<FahrtstreckeOutputDTO> getAlleFahrtstrecken() {
        ArrayList<FahrtstreckeOutputDTO> fahrtstrecken = new ArrayList<>();
        for (Fahrtstrecke fahrtstrecke : findAll()) {
            FahrtstreckeOutputDTO fahrtstreckeDto = new FahrtstreckeOutputDTO(fahrtstrecke);
            if (isFahrtstreckeLoeschbar(fahrtstrecke)) {
                fahrtstreckeDto.setLoeschbar(true);
            }
            fahrtstrecken.add(fahrtstreckeDto);
        }
        return fahrtstrecken;
    }

    public Fahrtstrecke getFahrtstreckeZuId(Long id) {
        Optional<Fahrtstrecke> fahrtstreckeOpt = findById(id);
        if (fahrtstreckeOpt.isEmpty()) {
            throw new IllegalArgumentException(String.format("Keine Fahrtstrecke zu ID %s gefunden", id));
        }
        return fahrtstreckeOpt.get();
    }

    private Boolean isFahrtstreckeLoeschbar(Fahrtstrecke fahrtstreckeId) {
        ArrayList<Fahrplanzuordnung> fahrplanzuordnungen = fahrplanzuordnungService
                .getAlleFahrplanzuordnungenZuFahrtstreckeId(fahrtstreckeId);
        return fahrplanzuordnungen.isEmpty();
    }

    public Boolean deleteFahrtstrecke(Long fahrtstreckeId) {
        Fahrtstrecke fahrtstrecke = getFahrtstreckeZuId(fahrtstreckeId);
        if (isFahrtstreckeLoeschbar(fahrtstrecke)) {
            for (HaltestellenzuordnungOutputDTO zuordnung : haltestellenzuordnungService
                    .getAlleZuordnungenZuFahrtstrecke(fahrtstreckeId)) {
                haltestellenzuordnungService.deleteById(zuordnung.getId());
            }
            deleteById(fahrtstrecke.getId());
            return true;
        }
        return false;
    }

}
