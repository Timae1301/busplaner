package de.hsw.busplaner.services;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.hsw.busplaner.beans.Buslinie;
import de.hsw.busplaner.beans.Fahrtstrecke;
import de.hsw.busplaner.dtos.fahrtstrecke.FahrtstreckeInputDTO;
import de.hsw.busplaner.dtos.fahrtstrecke.FahrtstreckeOutputDTO;
import de.hsw.busplaner.repositories.FahrtstreckeRepository;
import lombok.extern.java.Log;

@Log
@Service
public class FahrtstreckeService extends BasicService<Fahrtstrecke, Long> {

    @Autowired
    FahrtstreckeRepository repository;

    @Autowired
    BuslinieService buslinieService;

    @Override
    protected FahrtstreckeRepository getRepository() {
        return repository;
    }

    public Iterable<Fahrtstrecke> findAllByBuslinieId(Buslinie buslinie) {
        return repository.findAllByBuslinieId(buslinie);
    }

    public Long postFahrtstrecke(FahrtstreckeInputDTO fahrtstreckeInputDTO) {
        Optional<Buslinie> buslinieOpt = buslinieService.findById(fahrtstreckeInputDTO.getBuslinieId());
        if (buslinieOpt.isEmpty()) {
            throw new IllegalArgumentException(
                    String.format("Keine Buslinie zu ID: %s gefunden", fahrtstreckeInputDTO.getBuslinieId()));
        }
        Fahrtstrecke fahrtstrecke = new Fahrtstrecke(fahrtstreckeInputDTO, buslinieOpt.get());
        save(fahrtstrecke);
        return fahrtstrecke.getId();
    }

    public ArrayList<FahrtstreckeOutputDTO> getAlleFahrtstrecken() {
        ArrayList<FahrtstreckeOutputDTO> fahrtstrecken = new ArrayList<>();
        for (Fahrtstrecke fahrtstrecke : findAll()) {
            fahrtstrecken.add(new FahrtstreckeOutputDTO(fahrtstrecke));
        }
        return fahrtstrecken;
    }

}
