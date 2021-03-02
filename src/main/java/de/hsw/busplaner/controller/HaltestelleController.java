package de.hsw.busplaner.controller;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.hsw.busplaner.dtos.HaltestelleDTO;
import de.hsw.busplaner.services.HaltestelleService;
import lombok.extern.java.Log;

@RestController
@Log
@RequestMapping(path = "/api/haltestelle")
public class HaltestelleController {

    private final HaltestelleService service;

    @Autowired
    public HaltestelleController(final HaltestelleService service) {
        this.service = service;
    }

    @GetMapping(path = "")
    public ResponseEntity<ArrayList<HaltestelleDTO>> getAlleHaltestellen() {
        ArrayList<HaltestelleDTO> haltestellen = service.getAllHaltestellen();
        if (haltestellen.isEmpty()) {
            log.warning("Es sind keine Haltestellen vorhanden");
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(haltestellen);

    }

    @PostMapping(path = "/{haltestelleName}")
    public ResponseEntity<Boolean> postHaltestelle(@PathVariable String haltestelleName) {
        return ResponseEntity.ok(service.postHaltestelle(haltestelleName));
    }
}
