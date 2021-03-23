package de.hsw.busplaner.services;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Optional;

import javax.management.InstanceNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.hsw.busplaner.beans.Buslinie;
import de.hsw.busplaner.beans.Fahrplanzuordnung;
import de.hsw.busplaner.beans.Fahrtstrecke;
import de.hsw.busplaner.dtos.fahrtstrecke.FahrtstreckeInputDTO;
import de.hsw.busplaner.dtos.fahrtstrecke.FahrtstreckeMitHaltestellenDTO;
import de.hsw.busplaner.dtos.fahrtstrecke.FahrtstreckeMitUhrzeitDTO;
import de.hsw.busplaner.dtos.fahrtstrecke.FahrtstreckeOutputDTO;
import de.hsw.busplaner.dtos.haltestellenzuordnung.HaltestellenzuordnungOutputDTO;
import de.hsw.busplaner.dtos.haltestellenzuordnung.HaltestellenzuordnungSortierDTO;
import de.hsw.busplaner.repositories.FahrtstreckeRepository;
import de.hsw.busplaner.util.FahrtzeitenErmitteln;
import de.hsw.busplaner.util.HaltestellenSortierer;

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

    @Autowired
    HaltestellenSortierer sortierer;

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
                    .getAlleZuordnungenDTOsZuFahrtstrecke(fahrtstreckeId)) {
                haltestellenzuordnungService.deleteById(zuordnung.getId());
            }
            deleteById(fahrtstrecke.getId());
            return true;
        }
        return false;
    }

    public ArrayList<FahrtstreckeMitHaltestellenDTO> getAlleFahrtstreckenZuBuslinieId(Long buslinieId)
            throws InstanceNotFoundException, IllegalArgumentException {
        ArrayList<FahrtstreckeMitHaltestellenDTO> alleFahrtstrecken = new ArrayList<>();
        for (Fahrtstrecke fahrtstrecke : findAllByBuslinieId(buslinieService.getBuslinieById(buslinieId))) {
            alleFahrtstrecken.add(new FahrtstreckeMitHaltestellenDTO(fahrtstrecke,
                    sortierer.sortiereHaltestellen(fahrtstrecke.getHaltestellenzuordnungen())));
        }
        return alleFahrtstrecken;
    }

    public ArrayList<FahrtstreckeMitUhrzeitDTO> ermittleFahrtstreckenMitUhrzeit(Long buslinieId, Long haltestelleId)
            throws InstanceNotFoundException, IllegalArgumentException {
        ArrayList<FahrtstreckeMitUhrzeitDTO> fahrtstreckenMitUhrzeit = new ArrayList<>();

        for (FahrtstreckeMitHaltestellenDTO fahrtstreckeMitHaltestellenDTO : getFahrtstreckenMitBuslinieUndHaltestelle(
                buslinieId, haltestelleId)) {
            for (Fahrplanzuordnung fahrplanzuordnung : fahrplanzuordnungService
                    .getAlleFahrplanzuordnungenZuFahrtstreckeId(
                            getFahrtstreckeZuId(fahrtstreckeMitHaltestellenDTO.getFahrtstreckeId()))) {
                fahrtstreckenMitUhrzeit.add(
                        genFahrtstreckeMitUhrzeit(haltestelleId, fahrtstreckeMitHaltestellenDTO, fahrplanzuordnung));
            }
        }
        return fahrtstreckenMitUhrzeit;
    }

    private FahrtstreckeMitUhrzeitDTO genFahrtstreckeMitUhrzeit(Long haltestelleId,
            FahrtstreckeMitHaltestellenDTO fahrtstreckeMitHaltestellenDTO, Fahrplanzuordnung fahrplanzuordnung) {
        FahrtstreckeMitUhrzeitDTO fahrtstreckeMitUhrzeit = new FahrtstreckeMitUhrzeitDTO();
        fahrtstreckeMitUhrzeit.setFahrtId(fahrtstreckeMitHaltestellenDTO.getFahrtstreckeId());
        fahrtstreckeMitUhrzeit.setFahrtName(fahrtstreckeMitHaltestellenDTO.getFahrtstreckeName());
        fahrtstreckeMitUhrzeit
                .setUhrzeit(getUhrzeitAnHaltestelle(fahrplanzuordnung, haltestelleId, fahrtstreckeMitHaltestellenDTO));
        return fahrtstreckeMitUhrzeit;
    }

    private LocalTime getUhrzeitAnHaltestelle(Fahrplanzuordnung fahrplanzuordnung, Long haltestelleId,
            FahrtstreckeMitHaltestellenDTO fahrtstreckeMitHaltestellenDTO) {
        int fahrtzeit = 0;
        if (fahrplanzuordnung.isRichtung()) {
            fahrtzeit = FahrtzeitenErmitteln
                    .ermittleFahrtzeitInMinuten(fahrtstreckeMitHaltestellenDTO.getHaltestellen(), haltestelleId);
        } else {
            fahrtzeit = FahrtzeitenErmitteln.ermittleFahrtzeitInMinutenInvertiert(
                    fahrtstreckeMitHaltestellenDTO.getHaltestellen(), haltestelleId);
        }
        return fahrplanzuordnung.getStartzeitpunkt().plusMinutes(fahrtzeit);
    }

    private ArrayList<FahrtstreckeMitHaltestellenDTO> getFahrtstreckenMitBuslinieUndHaltestelle(Long buslinieId,
            Long haltestelleId) throws InstanceNotFoundException, IllegalArgumentException {
        ArrayList<FahrtstreckeMitHaltestellenDTO> fahrtstreckenMithaltestellen = new ArrayList<>();
        for (FahrtstreckeMitHaltestellenDTO fahrtstrecke : getAlleFahrtstreckenZuBuslinieId(buslinieId)) {
            if (isHaltestelleInFahrt(fahrtstrecke, haltestelleId)) {
                fahrtstreckenMithaltestellen.add(fahrtstrecke);
            }
        }
        return fahrtstreckenMithaltestellen;
    }

    private boolean isHaltestelleInFahrt(FahrtstreckeMitHaltestellenDTO fahrtstrecke, Long haltestelleId) {
        for (HaltestellenzuordnungSortierDTO haltestelle : fahrtstrecke.getHaltestellen()) {
            if (haltestelle.getHaltestelleId().equals(haltestelleId)) {
                return true;
            }
        }
        return false;
    }
}
