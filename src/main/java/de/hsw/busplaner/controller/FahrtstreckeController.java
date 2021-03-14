package de.hsw.busplaner.controller;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.hsw.busplaner.dtos.fahrtstrecke.FahrtstreckeInputDTO;
import de.hsw.busplaner.dtos.fahrtstrecke.FahrtstreckeOutputDTO;
import de.hsw.busplaner.services.FahrtstreckeService;
import lombok.extern.java.Log;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Log
@RestController
@RequestMapping(path = "/api/fahrtstrecke")
public class FahrtstreckeController {

    @Autowired
    FahrtstreckeService service;

    @PostMapping(path = "")
    public ResponseEntity<Long> postFahrtstrecke(@RequestBody FahrtstreckeInputDTO fahrtstreckeInputDTO) {
        try {
            log.info("wir sind da");
            return ResponseEntity.ok(service.postFahrtstrecke(fahrtstreckeInputDTO));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(path = "")
    public ResponseEntity<ArrayList<FahrtstreckeOutputDTO>> getAlleFahrtstrecken() {
        ArrayList<FahrtstreckeOutputDTO> fahrtstrecken = new ArrayList<>();
        fahrtstrecken = service.getAlleFahrtstrecken();
        if (fahrtstrecken.isEmpty()) {
            log.warning("Es sind keine Fahrtstrecken vorhanden");
        }
        return ResponseEntity.ok(fahrtstrecken);

        // an das FahrtstreckenOutputDTO ein boolean: löschbar hinzufügen
        // delete hinzufügen

    }
}
