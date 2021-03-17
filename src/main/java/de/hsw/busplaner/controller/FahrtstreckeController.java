package de.hsw.busplaner.controller;

import java.util.ArrayList;
import java.util.List;

import javax.management.InstanceNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.hsw.busplaner.dtos.fahrtstrecke.FahrtstreckeInputDTO;
import de.hsw.busplaner.dtos.fahrtstrecke.FahrtstreckeInputMitHaltestellenDTO;
import de.hsw.busplaner.dtos.fahrtstrecke.FahrtstreckeMitHaltestellenDTO;
import de.hsw.busplaner.dtos.fahrtstrecke.FahrtstreckeMitUhrzeitDTO;
import de.hsw.busplaner.dtos.fahrtstrecke.FahrtstreckeOutputDTO;
import de.hsw.busplaner.dtos.haltestellenzuordnung.HaltestellenzuordnungInputDTO;
import de.hsw.busplaner.dtos.haltestellenzuordnung.HaltestellenzuordnungOhneNaechsteHaltestelleInputDTO;
import de.hsw.busplaner.services.FahrtstreckeService;
import lombok.extern.java.Log;

@Log
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping(path = "/api/fahrtstrecke")
public class FahrtstreckeController {

    @Autowired
    FahrtstreckeService service;

    @Autowired
    HaltestellenzuordnungController haltestellenzuordnungController;

    @PostMapping(path = "")
    public ResponseEntity<Long> postFahrtstrecke(@RequestBody FahrtstreckeInputDTO fahrtstreckeInputDTO) {
        try {
            return ResponseEntity.ok(service.postFahrtstrecke(fahrtstreckeInputDTO));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping(path = "/haltestellen")
    public ResponseEntity<Long> postFahrtstreckeMitHaltestellen(
            @RequestBody FahrtstreckeInputMitHaltestellenDTO fahrtstreckeInputMitHaltestellenDTO) {
        try {
            Long fahrtstreckeId = service
                    .postFahrtstrecke(genNewFahrtstreckeInputDTO(fahrtstreckeInputMitHaltestellenDTO));
            List<HaltestellenzuordnungInputDTO> haltestellenzuordnungInputDTOs = genHaltestellenzuordnungInputDTOs(
                    fahrtstreckeInputMitHaltestellenDTO, fahrtstreckeId);
            haltestellenzuordnungController.postZuordnungen(haltestellenzuordnungInputDTOs);
            return ResponseEntity.ok(fahrtstreckeId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    private List<HaltestellenzuordnungInputDTO> genHaltestellenzuordnungInputDTOs(
            FahrtstreckeInputMitHaltestellenDTO fahrtstreckeInputMitHaltestellenDTO, Long fahrtstreckeId) {
        List<HaltestellenzuordnungInputDTO> zuordnungDtos = new ArrayList<>();

        for (int i = 0; i < fahrtstreckeInputMitHaltestellenDTO.getHaltestellen().size(); i++) {
            HaltestellenzuordnungOhneNaechsteHaltestelleInputDTO haltestelle = fahrtstreckeInputMitHaltestellenDTO
                    .getHaltestellen().get(i);
            if (i + 1 - fahrtstreckeInputMitHaltestellenDTO.getHaltestellen().size() == 0) {
                break;
            }
            HaltestellenzuordnungInputDTO zuordnung = new HaltestellenzuordnungInputDTO();
            zuordnung.setFahrtstreckeId(fahrtstreckeId);
            zuordnung.setHaltestelleId(haltestelle.getHaltestelleId());

            zuordnung.setFahrtzeit(haltestelle.getFahrtzeit());
            zuordnung.setNaechsteHaltestelle(
                    fahrtstreckeInputMitHaltestellenDTO.getHaltestellen().get(i + 1).getHaltestelleId());
            zuordnungDtos.add(zuordnung);
        }
        return zuordnungDtos;
    }

    private FahrtstreckeInputDTO genNewFahrtstreckeInputDTO(
            FahrtstreckeInputMitHaltestellenDTO fahrtstreckeInputMitHaltestellenDTO) {
        FahrtstreckeInputDTO fahrtstreckeInputDTO = new FahrtstreckeInputDTO();
        fahrtstreckeInputDTO.setBuslinieId(fahrtstreckeInputMitHaltestellenDTO.getBuslinieId());
        fahrtstreckeInputDTO.setName(fahrtstreckeInputMitHaltestellenDTO.getName());
        return fahrtstreckeInputDTO;
    }

    @GetMapping(path = "/buslinie/{buslinieId}")
    public ResponseEntity<ArrayList<FahrtstreckeMitHaltestellenDTO>> getAlleFahrtenZuBuslinie(
            @PathVariable Long buslinieId) {
        ArrayList<FahrtstreckeMitHaltestellenDTO> alleFahrten = new ArrayList<>();
        try {
            alleFahrten.addAll(service.getAlleFahrtstreckenZuBuslinieId(buslinieId));
        } catch (InstanceNotFoundException e) {
            log.warning("Fehler beim Sortieren der Haltestellen");
        }
        return ResponseEntity.ok(alleFahrten);
    }

    @GetMapping(path = "/uhrzeit")
    public ResponseEntity<ArrayList<FahrtstreckeMitUhrzeitDTO>> getAlleFahrtenZuBuslinieUndHaltestelleMitUhrzeit(
            @RequestParam Long buslinieId, @RequestParam Long haltestelleId) {
        ArrayList<FahrtstreckeMitUhrzeitDTO> fahrtstreckenMitUhrzeit = new ArrayList<>();
        try {
            fahrtstreckenMitUhrzeit.addAll(service.ermittleFahrtstreckenMitUhrzeit(buslinieId, haltestelleId));
        } catch (InstanceNotFoundException e) {
            log.warning("Eine Instanz wurde nicht gefunden");
        } catch (IllegalArgumentException e) {
            log.warning("Zu einer ID konnte nichts gefunden werden");
        }
        if (fahrtstreckenMitUhrzeit.isEmpty()) {
            log.warning("Es wurden keine Fahrtstrecken gefunden");
        }
        return ResponseEntity.ok(fahrtstreckenMitUhrzeit);
    }

    @GetMapping(path = "")
    public ResponseEntity<ArrayList<FahrtstreckeOutputDTO>> getAlleFahrtstrecken() {
        ArrayList<FahrtstreckeOutputDTO> fahrtstrecken = new ArrayList<>();
        fahrtstrecken = service.getAlleFahrtstrecken();
        if (fahrtstrecken.isEmpty()) {
            log.warning("Es sind keine Fahrtstrecken vorhanden");
        }
        return ResponseEntity.ok(fahrtstrecken);
    }

    @DeleteMapping(path = "/{fahrtstreckeId}")
    public ResponseEntity<Boolean> deleteFahrtstreckeById(@PathVariable Long fahrtstreckeId) {
        try {
            return ResponseEntity.ok(service.deleteFahrtstrecke(fahrtstreckeId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(true);
        }
    }
}
