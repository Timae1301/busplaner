package de.hsw.busplaner.controller;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.hsw.busplaner.dtos.fahrplan.FahrplanOutputDTO;
import de.hsw.busplaner.services.FahrplanService;
import lombok.extern.java.Log;

@Log
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping(path = "/api/fahrplan")
public class FahrplanController {

    @Autowired
    FahrplanService service;

    @PostMapping(path = "")
    public ResponseEntity<Long> postFahrplan(@RequestParam String name) {
        return ResponseEntity.ok(service.postFahrplan(name));
    }

    @GetMapping(path = "")
    public ResponseEntity<ArrayList<FahrplanOutputDTO>> getAlleFahrplaene() {
        ArrayList<FahrplanOutputDTO> fahrplaene = service.getAlleFahrplaene();
        if (fahrplaene.isEmpty()) {
            log.warning("Es sind keine Fahrplaene vorhanden");
        }
        return ResponseEntity.ok(fahrplaene);
    }

    @DeleteMapping(path = "/{fahrplanId}")
    public ResponseEntity<Boolean> deleteFahrplan(@PathVariable Long fahrplanId) {
        service.deleteFahrplan(fahrplanId);
        return ResponseEntity.ok(true);
    }
}
