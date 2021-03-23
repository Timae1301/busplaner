package de.hsw.busplaner.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.management.InstanceNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.hsw.busplaner.beans.Buslinie;
import de.hsw.busplaner.beans.Fahrtstrecke;
import de.hsw.busplaner.dtos.buslinie.BuslinieOutputDTO;
import de.hsw.busplaner.dtos.fahrtstrecke.FahrtstreckeOutputDTO;
import de.hsw.busplaner.repositories.BuslinieRepository;
import lombok.extern.java.Log;

@Service
@Log
public class BuslinieService extends BasicService<Buslinie, Long> {

    private final BuslinieRepository repository;

    @Autowired
    private FahrtstreckeService fahrtstreckeService;

    @Autowired
    private HaltestelleService haltestelleService;

    @Autowired
    public BuslinieService(final BuslinieRepository repository) {
        this.repository = repository;
    }

    @Override
    protected BuslinieRepository getRepository() {
        return repository;
    }

    public Buslinie findByBusnr(Long busnr) {
        Optional<Buslinie> buslinieOpt = repository.findByBusnr(busnr);
        if (buslinieOpt.isPresent()) {
            return buslinieOpt.get();
        }
        log.warning("Kein Eintrag für BusNr: " + busnr);
        throw new IllegalArgumentException(String.format("Zu der BusNr %s wurde kein Eintrag gefunden", busnr));
    }

    public List<BuslinieOutputDTO> getAllBuslinien() {
        List<BuslinieOutputDTO> buslinien = new ArrayList<>();
        for (Buslinie buslinie : findAll()) {
            log.info(String.format("Buslinie: %s gefunden", buslinie.getBusnr()));
            BuslinieOutputDTO buslinieDto = new BuslinieOutputDTO(buslinie);
            if (isBuslinieLoeschbar(buslinie.getId())) {
                buslinieDto.setLoeschbar(true);
            }
            buslinien.add(buslinieDto);
        }
        return buslinien;
    }

    public Long postBuslinie(Long busNr) {
        log.info(String.format("Neue Buslinie: %s angelegt", busNr));
        return save(new Buslinie(busNr)).getId();
    }

    public BuslinieOutputDTO getBuslinie(Long buslinieId) throws IllegalArgumentException {
        return new BuslinieOutputDTO(getBuslinieById(buslinieId));
    }

    public Buslinie getBuslinieById(Long buslinieId) throws IllegalArgumentException {
        Optional<Buslinie> buslinieOpt = findById(buslinieId);
        if (buslinieOpt.isPresent()) {
            return buslinieOpt.get();
        }
        throw new IllegalArgumentException(
                String.format("Zu der BuslinienId %s wurde kein Eintrag gefunden", buslinieId));
    }

    public void patchBuslinie(BuslinieOutputDTO buslinieDTO) {
        Buslinie buslinie = new Buslinie(buslinieDTO);
        save(buslinie);
    }

    private boolean isBuslinieLoeschbar(Long buslinieId) {
        List<Fahrtstrecke> fahrtstreckenMitBuslinie = new ArrayList<>();
        Buslinie buslinie = getBuslinieById(buslinieId);
        fahrtstreckeService.findAllByBuslinieId(buslinie).forEach(fahrtstreckenMitBuslinie::add);
        return fahrtstreckenMitBuslinie.isEmpty();
    }

    public boolean deleteBuslinie(Long buslinieId) throws IllegalArgumentException {
        boolean isBuslinieLoeschbar = isBuslinieLoeschbar(buslinieId);
        if (isBuslinieLoeschbar) {
            deleteById(buslinieId);
        }
        return isBuslinieLoeschbar;
    }

    public List<BuslinieOutputDTO> getAlleBuslinienFuerHaltestelle(Long haltestelleId)
            throws InstanceNotFoundException {
        List<BuslinieOutputDTO> buslinien = new ArrayList<>();
        for (FahrtstreckeOutputDTO fahrtstreckeOutputDTO : fahrtstreckeService.getAlleFahrtstrecken()) {
            if (haltestelleService.isHaltestelleInFahrtstrecke(haltestelleId, fahrtstreckeOutputDTO.getId())) {
                buslinien.add(new BuslinieOutputDTO(findByBusnr(fahrtstreckeOutputDTO.getBuslinie())));
            }
        }
        return buslinien;
    }

}
