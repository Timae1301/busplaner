package de.hsw.busplaner.controller;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.hsw.busplaner.dtos.fahrplanzuordnung.FahrplanzuordnungInputDTO;
import de.hsw.busplaner.dtos.fahrplanzuordnung.FahrplanzuordnungOutputDTO;
import de.hsw.busplaner.services.FahrplanzuordnungService;
import lombok.extern.java.Log;

@Log
@RestController
@RequestMapping(path = "/api/fahrplanzuordnung")
public class FahrplanzuordnungController {

    @Autowired
    FahrplanzuordnungService service;

    @PostMapping(path = "")
    public ResponseEntity<Long> postFahrplanzuordnung(@RequestBody FahrplanzuordnungInputDTO zuordung) {
        try {
            return ResponseEntity.ok(service.postFahrplanzuordnung(zuordung));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(path = "")
    public ResponseEntity<ArrayList<FahrplanzuordnungOutputDTO>> getAlleFahrplanzuordnungen() {
        ArrayList<FahrplanzuordnungOutputDTO> zuordnungen = new ArrayList<>();
        zuordnungen = service.getAlleFahrplanzuordnungen();
        if (zuordnungen.isEmpty()) {
            log.warning("Es sind keine Fahrplanzuordnungen vorhanden");
        }
        return ResponseEntity.ok(zuordnungen);
    }
}