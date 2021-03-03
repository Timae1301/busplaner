package de.hsw.busplaner.controller;

import java.util.ArrayList;

import de.hsw.busplaner.dtos.BuslinieDTO;
import de.hsw.busplaner.services.BuslinieService;
import lombok.extern.java.Log;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Log
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping(path = "/api/buslinie")
public class BuslinieController {

    private final BuslinieService service;

    @Autowired
    public BuslinieController(final BuslinieService service) {
        this.service = service;
    }

    @GetMapping(path = "")
    public ResponseEntity<ArrayList<BuslinieDTO>> getAllBuslinie() {
        ArrayList<BuslinieDTO> buslinien = service.getAllBuslinien();
        if (buslinien.isEmpty()) {
            log.warning("Es sind keine Buslinien vorhanden");
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(buslinien);
    }

    @PostMapping(path = "/{busNr}")
    public ResponseEntity<Boolean> postBuslinie(@PathVariable Long busNr) {
        return ResponseEntity.ok(service.postBuslinie(busNr));
    }
}
