package de.hsw.busplaner.controller;

import java.util.ArrayList;

import javax.management.InstanceNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.hsw.busplaner.dtos.fahrtstrecke.FahrtstreckeInputDTO;
import de.hsw.busplaner.dtos.fahrtstrecke.FahrtstreckeMitHaltestellenDTO;
import de.hsw.busplaner.dtos.fahrtstrecke.FahrtstreckeOutputDTO;
import de.hsw.busplaner.services.FahrtstreckeService;
import lombok.extern.java.Log;

@Log
@RestController
@RequestMapping(path = "/api/fahrtstrecke")
public class FahrtstreckeController {

    @Autowired
    FahrtstreckeService service;

    @PostMapping(path = "")
    public ResponseEntity<Long> postFahrtstrecke(@RequestBody FahrtstreckeInputDTO fahrtstreckeInputDTO) {
        try {
            return ResponseEntity.ok(service.postFahrtstrecke(fahrtstreckeInputDTO));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
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
