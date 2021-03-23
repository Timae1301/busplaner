package de.hsw.busplaner.services;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

import javax.management.InstanceNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.hsw.busplaner.beans.Fahrplan;
import de.hsw.busplaner.beans.Fahrplanzuordnung;
import de.hsw.busplaner.dtos.fahrplan.FahrplanOutputDTO;
import de.hsw.busplaner.dtos.fahrplan.FahrplanauskunftDTO;
import de.hsw.busplaner.dtos.fahrtstrecke.FahrtstreckeMitHaltestellenDTO;
import de.hsw.busplaner.dtos.haltestellenzuordnung.HaltestellenzuordnungSortierDTO;
import de.hsw.busplaner.repositories.FahrplanRepository;
import de.hsw.busplaner.util.FahrtzeitenErmitteln;
import de.hsw.busplaner.util.HaltestellenSortierer;
import lombok.extern.java.Log;

@Log
@Service
public class FahrplanService extends BasicService<Fahrplan, Long> {

    @Autowired
    FahrplanRepository repository;

    @Autowired
    FahrplanzuordnungService fahrplanzuordnungService;

    @Autowired
    HaltestellenSortierer sortierer;

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
        Fahrplan fahrplan = getFahrplanZuId(fahrplanId);
        ArrayList<Fahrplanzuordnung> fahrplanzuordnungen = fahrplanzuordnungService
                .getAlleFahrplanzuordnungenZuFahrplanId(fahrplan);
        for (Fahrplanzuordnung fahrplanzuordnung : fahrplanzuordnungen) {
            fahrplanzuordnungService.deleteById(fahrplanzuordnung.getId());
        }
        deleteById(fahrplan.getId());
        return true;
    }

    public FahrplanauskunftDTO getFahrplanaukunft(Long fahrplanId, LocalTime zeitpunktVorher,
            LocalTime zeitpunktNachher) throws InstanceNotFoundException {
        Fahrplan fahrplan = getFahrplanZuId(fahrplanId);
        ArrayList<FahrtstreckeMitHaltestellenDTO> fahrtstrecken = new ArrayList<>();
        ArrayList<Fahrplanzuordnung> fahrplanzuordnungen = fahrplanzuordnungService
                .getAlleFahrplanzuordnungenZuFahrplanIdInTime(fahrplan, zeitpunktVorher, zeitpunktNachher);
        for (Fahrplanzuordnung fahrplanzuordnung : fahrplanzuordnungen) {
            fahrtstrecken.add(getFahrtstreckeMitHaltestellen(fahrplanzuordnung));
        }
        Collections.sort(fahrtstrecken);
        return new FahrplanauskunftDTO(fahrplan.getName(), fahrtstrecken);
    }

    private FahrtstreckeMitHaltestellenDTO getFahrtstreckeMitHaltestellen(Fahrplanzuordnung fahrplanzuordnung)
            throws InstanceNotFoundException {
        ArrayList<HaltestellenzuordnungSortierDTO> sortiertehaltestellen = sortierer
                .sortiereHaltestellen(fahrplanzuordnung.getFahrtstreckeid().getHaltestellenzuordnungen());
        if (fahrplanzuordnung.isRichtung()) {
            sortiertehaltestellen = FahrtzeitenErmitteln.setzeUhrzeiten(sortiertehaltestellen,
                    fahrplanzuordnung.getStartzeitpunkt());
        } else {
            sortiertehaltestellen = FahrtzeitenErmitteln.setzeUhrzeitenInvertiert(sortiertehaltestellen,
                    fahrplanzuordnung.getStartzeitpunkt());
        }
        Collections.sort(sortiertehaltestellen);
        return new FahrtstreckeMitHaltestellenDTO(fahrplanzuordnung.getFahrtstreckeid(), sortiertehaltestellen);
    }

}
