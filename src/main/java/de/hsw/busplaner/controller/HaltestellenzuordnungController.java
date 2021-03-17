package de.hsw.busplaner.controller;

import java.util.ArrayList;
import java.util.List;

import javax.management.InstanceNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.hsw.busplaner.dtos.haltestellenzuordnung.HaltestellenzuordnungInputDTO;
import de.hsw.busplaner.dtos.haltestellenzuordnung.HaltestellenzuordnungOutputDTO;
import de.hsw.busplaner.dtos.haltestellenzuordnung.HaltestellenzuordnungSortierDTO;
import de.hsw.busplaner.services.HaltestellenzuordnungService;
import lombok.extern.java.Log;

@Log
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping(path = "/api/haltestellenzuordnung")
public class HaltestellenzuordnungController {

    @Autowired
    HaltestellenzuordnungService service;

    @PostMapping(path = "")
    public ResponseEntity<ArrayList<Long>> postZuordnungen(
            @RequestBody List<HaltestellenzuordnungInputDTO> haltestellenzuordnungInputDTOs) {
        ArrayList<Long> ids = new ArrayList<>();
        for (HaltestellenzuordnungInputDTO haltestellenzuordnungInputDTO : haltestellenzuordnungInputDTOs) {
            try {
                ids.add(service.postHaltestellenzuordnug(haltestellenzuordnungInputDTO));
            } catch (IllegalArgumentException e) {
                return ResponseEntity.notFound().build();
            }
        }
        if (ids.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ids);
    }

    @GetMapping(path = "")
    public ResponseEntity<ArrayList<HaltestellenzuordnungOutputDTO>> getAlleZuordnungen() {
        ArrayList<HaltestellenzuordnungOutputDTO> zuordnungen = new ArrayList<>();
        zuordnungen = service.getAlleZuordnungen();
        if (zuordnungen.isEmpty()) {
            log.warning("Es sind keine Haltestellenzuordnungen vorhanden");
        }
        return ResponseEntity.ok(zuordnungen);
    }

    @GetMapping(path = "/{fahrtstreckeId}/sort")
    public ResponseEntity<ArrayList<HaltestellenzuordnungSortierDTO>> getAlleZuordnungenSorted(
            @PathVariable Long fahrtstreckeId) {
        ArrayList<HaltestellenzuordnungSortierDTO> zuordnungen = new ArrayList<>();
        try {
            zuordnungen = service.getAlleZuordnungenSorted(fahrtstreckeId);
        } catch (InstanceNotFoundException e) {
            log.warning("Problem beim sortieren");
        }
        if (zuordnungen.isEmpty()) {
            log.warning("Es sind keine Haltestellenzuordnungen vorhanden");
        }
        return ResponseEntity.ok(zuordnungen);
    }

    @GetMapping(path = "/{fahrtstreckeId}")
    public ResponseEntity<ArrayList<HaltestellenzuordnungOutputDTO>> getAlleZuordnungenZuFahrtstrecke(
            @PathVariable Long fahrtstreckeId) {
        ArrayList<HaltestellenzuordnungOutputDTO> zuordnungen = new ArrayList<>();
        zuordnungen = service.getAlleZuordnungenDTOsZuFahrtstrecke(fahrtstreckeId);
        if (zuordnungen.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(zuordnungen);
    }
}
